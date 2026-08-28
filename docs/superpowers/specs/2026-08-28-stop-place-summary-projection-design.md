# Stop place summary projection and cache

**Date:** 2026-08-28
**Repos:** enlil (new query + cache), nirgali (migrate StopPicker)
**Status:** approved, pending implementation plan

## Background

On 2026-08-28, mummu (the read-only stop place API) suffered a latency incident: average
latency rose from ~15 ms to over 600 ms between 11:06 and 11:13:30 UTC. Both mummu
replicas went from ~0.03 CPU cores to a combined ~9.8 cores, and egress rose roughly 20×.

The load came from this application's stop picker. Three defects combined:

1. **No deduplication of ids.** `StopPicker.tsx` maps one entry per *stop place* to its
   `topographicPlaceRef.ref`. Many stops share a municipality, so the list is heavily
   redundant. Observed in production: 200 ids per request, **5 distinct values**.

2. **Refetch on every parent render.** `api(config, auth)` returns a fresh object literal
   and is constructed inline in JSX in `Messages.tsx` and `Cancellations.tsx`. The picker's
   `useEffect(..., [stops, api])` therefore re-runs whenever the parent re-renders, which
   `useAuth()` token renewals and the redux-backed `useLines`/`useMessages` hooks cause
   routinely.

3. **Oversized payloads.** Each `/topographic-places?ids=` response is ~0.9–1.9 MB gzipped
   of polygon geometry. The picker uses exactly one field from it:
   `topographicPlace.descriptor.name.value`, rendered as a parenthetical in a dropdown label.

The same effect also calls `/stop-places?ids=`, which is mummu's single heaviest endpoint
(8874 s cumulative request time versus 2181 s for topographic-places, measured on a
6-hour-old pod).

This is a recurring pattern, not a one-off — bursts of 6252, 2376 and 4529 requests per
hour occurred on 27–28 August against a background of 6–16 per hour.

### Relationship to mummu hardening

mummu PR #369 ("Per-endpoint max count with optional 400 enforcement", merged 2026-07-01)
added a per-endpoint ceiling on the `count` parameter. It **deliberately skips requests
that supply `ids`**, because there the effective limit is the number of ids rather than
`count`. That leaves the ids path unbounded, which is the path this application uses.

Capping ids in mummu is worthwhile but is **out of scope here**: it needs its own rollout
against all consumers, mirroring #369's dark-launch approach. This spec reduces the load at
source instead. Note also that mummu production currently runs a pre-#369 image, so its
`no.entur.mummu.list.*` properties are not deployed — do not assume the interceptor's
behaviour is live.

## Goals

- Collapse the picker's two chained fan-out request waves into a single call.
- Return only the three fields the UI actually renders.
- Make the duplicate-ids defect structurally impossible rather than fixed by convention.
- Keep the picker usable when mummu is unhealthy — it is used during incidents.

## Non-goals

- Capping `ids` in mummu (separate hardening work).
- Caching anything beyond what this picker needs.
- Changing what the picker displays.

## Design

### enlil: schema

```graphql
type Query {
    stopPlaces(ids: [ID!]!): [StopPlaceSummary!]!
}

type StopPlaceSummary {
    id: ID!
    transportMode: String
    topographicPlaceName: String
}
```

These three fields are exactly what the label in `StopPicker.tsx` is built from. Everything
else in both mummu payloads is discarded.

### enlil: components

**`StopPlaceClient`** — a `WebClient` wrapper over mummu's REST API, with two methods:
`fetchStopPlaces(Collection<String> ids)` and `fetchTopographicPlaces(Collection<String> ids)`.
Each chunks its input into batches of **200 ids** — the size the frontend uses today, so it
is known to work against mummu — and **projects to small records immediately on receipt**,
so polygon geometry is discarded at the edge and never accumulates in heap.

**`StopPlaceService`** — orchestration, and the only component that knows the two-step shape:

1. Deduplicate the incoming ids.
2. Resolve what it can from the stop place cache; fetch the misses.
3. Collect the distinct `topographicPlaceRef`s from the results.
4. Resolve what it can from the topographic cache; fetch the misses.
5. Join and return.

Step 3 is where today's 200 → 5 collapse happens — server-side and unconditionally.

**Caches** — two Caffeine caches, both size-bounded so a large working set cannot threaten
enlil's 2 GB heap:

| Cache | Key → value | TTL | Max entries | Rationale |
|---|---|---|---|---|
| `topographicPlaceNames` | id → name | 24 h | 5 000 | Municipality names effectively never change; Norway has a few hundred entries, so this never evicts in practice |
| `stopPlaceSummaries` | id → transportMode + ref | 1 h | 50 000 | Attributes change rarely; entries are ~200 bytes, so a full cache is ~10 MB against a 2 GB heap |

Population is lazy. Preloading was considered and rejected: mummu has no lightweight
projection, so warming the topographic cache would mean pulling roughly 150 MB at enlil
startup and coupling startup to mummu availability. Lazy population reaches the same steady
state after a brief warmup.

enlil runs a single replica, so an in-process cache has no cross-replica coherence problem
and no shared cache infrastructure is required.

**`QueryController`** — one additional `@QueryMapping`, following the existing pattern.

**Configuration** — mummu's base URL per environment in the helm configmap.

**Input cap** — `stopPlaces` rejects requests carrying more than **1000 ids**, counted
*after* deduplication so that a redundant list is corrected rather than refused. Since mummu
is not being capped in this scope, enlil must not become the new amplifier. 1000 matches
mummu's own default `max-count` from PR #369, keeping the two services' ceilings consistent.

### Data flow

```
nirgali ──stopPlaces(ids)──▶ enlil
                              │  dedupe ids
                              │  stopPlaceSummaries cache ──hit──▶ ┐
                              │        └─miss─▶ mummu /stop-places?ids=  (chunked, projected)
                              │  collect DISTINCT topographicPlaceRefs   ◀── the 200→5 collapse
                              │  topographicPlaceNames cache ──hit──▶ ┐
                              │        └─miss─▶ mummu /topographic-places?ids=  (chunked, projected)
                              ▼  join
                          [StopPlaceSummary]
```

After warmup the topographic cache should serve essentially every request, since the set of
Norwegian municipalities and counties is small and static.

### Error handling

Partial results are preferred over hard failure, because this tool is used while responding
to incidents — including incidents affecting mummu itself.

- **Topographic fetch fails:** return summaries with `topographicPlaceName: null`.
  `StopPicker.tsx:107` already null-checks this and omits the parenthetical.
- **Stop place fetch fails:** omit the affected ids. The label degrades to name + id, both
  of which the picker already holds in its own props.
- Both cases log a warning. Neither fails the whole query.

### nirgali changes

- **`src/api/api.ts`** — replace `getStopPlaces` and `getTopographicPlaces` with a single
  `getStopPlaceSummaries(ids)` issued through the existing authenticated GraphQL client.
  This also moves the call off the unauthenticated public API.
- **`src/components/common/StopPicker.tsx`** — `useTopographicPlaces` collapses to one
  fetch. The chunk → fetch → map → chunk → fetch chain and the
  `stopPlaceTopographicPlaceIndex` ref are removed.
- **`src/components/messages/Messages.tsx`**, **`src/components/cancellations/Cancellations.tsx`**
  — memoize the api object rather than constructing it inline in JSX.

**Caveat on memoization:** `useMemo(() => api(config, auth), [config, auth])` still produces
a new object whenever `useAuth()` changes identity, which happens on silent token renewals.
Keying on the access token instead of the auth object gives real stability. Verify this
empirically during implementation rather than assuming either behaviour.

## Testing

**enlil**

- `StopPlaceService` unit tests against a stubbed client:
  - 200 ids resolving to 5 distinct refs produce **one** topographic fetch carrying **5** ids.
    This is the regression that caused the incident.
  - A second identical call issues no mummu requests at all.
  - Topographic fetch failure yields summaries with null names, not an error.
  - Stop place fetch failure omits those ids and does not fail the query.
  - More than 1000 *distinct* ids is rejected; more than 1000 ids that deduplicate to fewer
    than 1000 is accepted.
- GraphQL integration test via `spring-graphql-test`, already a dependency.

**nirgali**

- Re-rendering `StopPicker` with a stable api prop does **not** refetch. This is the second
  half of the incident and is currently untested.
- The picker renders correct labels when `topographicPlaceName` is null.

## Rollout

enlil ships first and is inert until nirgali calls it. nirgali follows. Neither change is
user-visible; success is measured by the disappearance of `/topographic-places?ids=` and
`/stop-places?ids=` bursts in mummu's load balancer request log
(`logName="projects/ent-kub-prd/logs/requests"`), where this traffic is identifiable by its
`referer`.
