# Stop Place Summary Projection Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace the stop picker's two chained fan-out request waves against the stop place API with a single call to a projected, cached `stopPlaces` query in enlil.

**Architecture:** enlil gains a `stopPlaces(ids)` GraphQL query returning only `{id, transportMode, topographicPlaceName}`. A service layer deduplicates ids, resolves from two Caffeine caches, fetches only cache misses from the upstream REST API in chunks, and discards polygon geometry at the deserialization boundary. nirgali's `StopPicker` calls that one query instead of chaining `/stop-places?ids=` into `/topographic-places?ids=`.

**Tech Stack:** enlil — Java 21, Spring Boot, Spring GraphQL, WebClient, Caffeine, JUnit 5, AssertJ, Mockito, `spring-graphql-test`, Maven. nirgali — TypeScript, React, Apollo Client, Vitest, Testing Library.

**Spec:** `docs/superpowers/specs/2026-08-28-stop-place-summary-projection-design.md`

---

## File Structure

**enlil** — all new code in a new `org.entur.enlil.stopplace` package:

| File | Responsibility |
|---|---|
| `StopPlaceSummary.java` | Public GraphQL-facing record: `id`, `transportMode`, `topographicPlaceName` |
| `StopPlaceRecord.java` | Internal projection of an upstream stop place: `id`, `transportMode`, `topographicPlaceRef` |
| `TopographicPlaceRecord.java` | Internal projection of an upstream topographic place: `id`, `name` |
| `StopPlaceClient.java` | Interface: the two upstream fetches. Lets the service be unit-tested against a stub |
| `MummuStopPlaceClient.java` | WebClient implementation: chunking, DTOs, geometry discarded via `ignoreUnknown` |
| `StopPlaceService.java` | Dedupe, cap, cache resolution, join, partial degradation |
| `StopPlaceCacheConfiguration.java` | The two Caffeine cache beans |

Modified: `graphql/QueryController.java`, `resources/graphql/schema.graphqls`, `resources/application.properties`, `helm/enlil/templates/configmap.yaml`, the three `helm/enlil/env/values-*.yaml`, `pom.xml`.

**nirgali:**

| File | Change |
|---|---|
| `src/api/api.ts` | Replace `getStopPlaces` + `getTopographicPlaces` with `getStopPlaceSummaries` |
| `src/components/common/StopPicker.tsx` | Single fetch; remove the chunk → fetch → map → chunk → fetch chain |
| `src/components/messages/Messages.tsx` | Memoize the api object |
| `src/components/cancellations/Cancellations.tsx` | Memoize the api object |
| `src/components/common/StopPicker.test.tsx` | New — the no-refetch regression test |

**Formatting note (enlil):** `prettier-maven-plugin` runs with goal `write` during the build, so `mvn compile` reformats sources automatically; CI runs it with `check`. Do not hand-format — build, then commit whatever prettier produced.

---

### Task 1: Add the Caffeine dependency

**Files:**
- Modify: `pom.xml`

- [ ] **Step 1: Add the dependency**

In the `<dependencies>` block, alongside the other third-party entries:

```xml
<dependency>
    <groupId>com.github.ben-manes.caffeine</groupId>
    <artifactId>caffeine</artifactId>
</dependency>
```

Spring Boot's dependency management supplies the version, so no `<version>` tag.

We use Caffeine directly rather than `spring-boot-starter-cache`/`@Cacheable`, because the access pattern is bulk get-many-then-fetch-the-misses, which the annotation-driven cache abstraction does not express.

- [ ] **Step 2: Verify it resolves**

Run: `./mvnw -q dependency:resolve -DincludeArtifactIds=caffeine`
Expected: completes with no error.

- [ ] **Step 3: Commit**

```bash
git add pom.xml
git commit -m "build: add caffeine for stop place caching"
```

---

### Task 2: Domain records and the client interface

These are pure data declarations with no behaviour, so there is nothing to test in isolation. They are exercised by every later task.

**Files:**
- Create: `src/main/java/org/entur/enlil/stopplace/StopPlaceSummary.java`
- Create: `src/main/java/org/entur/enlil/stopplace/StopPlaceRecord.java`
- Create: `src/main/java/org/entur/enlil/stopplace/TopographicPlaceRecord.java`
- Create: `src/main/java/org/entur/enlil/stopplace/StopPlaceClient.java`

- [ ] **Step 1: Create the records**

`StopPlaceSummary.java`:

```java
package org.entur.enlil.stopplace;

/**
 * The projection returned to GraphQL consumers. These three fields are the whole
 * of what the stop picker renders; everything else in the upstream payload is
 * deliberately discarded.
 */
public record StopPlaceSummary(
  String id,
  String transportMode,
  String topographicPlaceName
) {}
```

`StopPlaceRecord.java`:

```java
package org.entur.enlil.stopplace;

/** Internal projection of an upstream stop place. */
public record StopPlaceRecord(
  String id,
  String transportMode,
  String topographicPlaceRef
) {}
```

`TopographicPlaceRecord.java`:

```java
package org.entur.enlil.stopplace;

/** Internal projection of an upstream topographic place. */
public record TopographicPlaceRecord(String id, String name) {}
```

- [ ] **Step 2: Create the client interface**

`StopPlaceClient.java`:

```java
package org.entur.enlil.stopplace;

import java.util.Collection;
import java.util.List;

/**
 * Upstream stop place API. An interface so {@link StopPlaceService} can be unit
 * tested against a stub without HTTP.
 */
public interface StopPlaceClient {
  List<StopPlaceRecord> fetchStopPlaces(Collection<String> ids);

  List<TopographicPlaceRecord> fetchTopographicPlaces(Collection<String> ids);
}
```

- [ ] **Step 3: Verify it compiles**

Run: `./mvnw -q compile`
Expected: BUILD SUCCESS.

- [ ] **Step 4: Commit**

```bash
git add src/main/java/org/entur/enlil/stopplace/
git commit -m "feat: add stop place projection records and client interface"
```

---

### Task 3: StopPlaceService — deduplication and the input cap

This is the task that encodes the incident's root cause as a test. Write it first.

**Files:**
- Create: `src/test/java/org/entur/enlil/stopplace/StopPlaceClientStub.java`
- Create: `src/test/java/org/entur/enlil/stopplace/StopPlaceServiceTest.java`
- Create: `src/main/java/org/entur/enlil/stopplace/StopPlaceService.java`

- [ ] **Step 1: Write the stub client**

`StopPlaceClientStub.java` — follows the existing `src/test/java/org/entur/enlil/stubs/UserContextServiceStub.java` convention of a hand-written stub:

```java
package org.entur.enlil.stopplace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/** Records the calls made to it so tests can assert on request shape. */
public class StopPlaceClientStub implements StopPlaceClient {

  private final Map<String, StopPlaceRecord> stopPlaces;
  private final Map<String, TopographicPlaceRecord> topographicPlaces;

  final List<List<String>> stopPlaceCalls = new ArrayList<>();
  final List<List<String>> topographicCalls = new ArrayList<>();

  RuntimeException stopPlaceFailure;
  RuntimeException topographicFailure;

  public StopPlaceClientStub(
    Map<String, StopPlaceRecord> stopPlaces,
    Map<String, TopographicPlaceRecord> topographicPlaces
  ) {
    this.stopPlaces = stopPlaces;
    this.topographicPlaces = topographicPlaces;
  }

  @Override
  public List<StopPlaceRecord> fetchStopPlaces(Collection<String> ids) {
    stopPlaceCalls.add(List.copyOf(ids));
    if (stopPlaceFailure != null) {
      throw stopPlaceFailure;
    }
    return ids.stream().map(stopPlaces::get).filter(java.util.Objects::nonNull).toList();
  }

  @Override
  public List<TopographicPlaceRecord> fetchTopographicPlaces(Collection<String> ids) {
    topographicCalls.add(List.copyOf(ids));
    if (topographicFailure != null) {
      throw topographicFailure;
    }
    return ids
      .stream()
      .map(topographicPlaces::get)
      .filter(java.util.Objects::nonNull)
      .toList();
  }
}
```

- [ ] **Step 2: Write the failing test**

`StopPlaceServiceTest.java`:

```java
package org.entur.enlil.stopplace;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

class StopPlaceServiceTest {

  private static final String STOP_A = "NSR:StopPlace:1";
  private static final String TOPO_A = "KVE:TopographicPlace:3412";

  private StopPlaceService serviceFor(StopPlaceClientStub client) {
    return new StopPlaceService(
      client,
      Caffeine.newBuilder().expireAfterWrite(Duration.ofMinutes(60)).maximumSize(1000).build(),
      Caffeine.newBuilder().expireAfterWrite(Duration.ofHours(24)).maximumSize(1000).build()
    );
  }

  @Test
  void deduplicatesIdsBeforeCallingUpstream() {
    // The incident shape: 200 ids that resolve to a handful of distinct
    // topographic places. Both upstream calls must carry only distinct values.
    Map<String, StopPlaceRecord> stopPlaces = new HashMap<>();
    List<String> requested = new ArrayList<>();
    for (int i = 0; i < 40; i++) {
      for (int t = 0; t < 5; t++) {
        String stopId = "NSR:StopPlace:" + i + "-" + t;
        stopPlaces.put(
          stopId,
          new StopPlaceRecord(stopId, "bus", "KVE:TopographicPlace:" + t)
        );
        requested.add(stopId);
      }
    }
    Map<String, TopographicPlaceRecord> topo = new HashMap<>();
    IntStream
      .range(0, 5)
      .forEach(t ->
        topo.put(
          "KVE:TopographicPlace:" + t,
          new TopographicPlaceRecord("KVE:TopographicPlace:" + t, "Sted " + t)
        )
      );

    StopPlaceClientStub client = new StopPlaceClientStub(stopPlaces, topo);
    serviceFor(client).getStopPlaceSummaries(requested);

    assertThat(client.stopPlaceCalls).hasSize(1);
    assertThat(client.stopPlaceCalls.get(0)).hasSize(200);
    assertThat(client.topographicCalls).hasSize(1);
    assertThat(client.topographicCalls.get(0)).hasSize(5);
  }

  @Test
  void deduplicatesRepeatedIdsInTheRequest() {
    Map<String, StopPlaceRecord> stopPlaces = Map.of(
      STOP_A,
      new StopPlaceRecord(STOP_A, "rail", TOPO_A)
    );
    Map<String, TopographicPlaceRecord> topo = Map.of(
      TOPO_A,
      new TopographicPlaceRecord(TOPO_A, "Oslo")
    );
    StopPlaceClientStub client = new StopPlaceClientStub(stopPlaces, topo);

    List<StopPlaceSummary> result = serviceFor(client)
      .getStopPlaceSummaries(List.of(STOP_A, STOP_A, STOP_A));

    assertThat(client.stopPlaceCalls.get(0)).containsExactly(STOP_A);
    assertThat(result).containsExactly(new StopPlaceSummary(STOP_A, "rail", "Oslo"));
  }

  @Test
  void rejectsMoreThanOneThousandDistinctIds() {
    StopPlaceClientStub client = new StopPlaceClientStub(Map.of(), Map.of());
    List<String> tooMany = IntStream
      .range(0, 1001)
      .mapToObj(i -> "NSR:StopPlace:" + i)
      .toList();

    assertThatThrownBy(() -> serviceFor(client).getStopPlaceSummaries(tooMany))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("1001");
  }

  @Test
  void acceptsManyIdsThatDeduplicateBelowTheCap() {
    Map<String, StopPlaceRecord> stopPlaces = Map.of(
      STOP_A,
      new StopPlaceRecord(STOP_A, "bus", null)
    );
    StopPlaceClientStub client = new StopPlaceClientStub(stopPlaces, Map.of());
    List<String> redundant = IntStream.range(0, 2000).mapToObj(i -> STOP_A).toList();

    assertThat(serviceFor(client).getStopPlaceSummaries(redundant)).hasSize(1);
  }
}
```

- [ ] **Step 3: Run the tests to verify they fail**

Run: `./mvnw -q test -Dtest=StopPlaceServiceTest`
Expected: compilation failure — `StopPlaceService` does not exist.

- [ ] **Step 4: Write the implementation**

`StopPlaceService.java`:

```java
package org.entur.enlil.stopplace;

import com.github.benmanes.caffeine.cache.Cache;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class StopPlaceService {

  private static final Logger log = LoggerFactory.getLogger(StopPlaceService.class);

  /**
   * Counted after deduplication, so a redundant list is corrected rather than
   * refused. Matches the upstream service's own default count ceiling.
   */
  static final int MAX_DISTINCT_IDS = 1000;

  private final StopPlaceClient client;
  private final Cache<String, StopPlaceRecord> stopPlaceCache;
  private final Cache<String, String> topographicNameCache;

  public StopPlaceService(
    StopPlaceClient client,
    @Qualifier("stopPlaceCache") Cache<String, StopPlaceRecord> stopPlaceCache,
    @Qualifier("topographicNameCache") Cache<String, String> topographicNameCache
  ) {
    this.client = client;
    this.stopPlaceCache = stopPlaceCache;
    this.topographicNameCache = topographicNameCache;
  }

  public List<StopPlaceSummary> getStopPlaceSummaries(Collection<String> ids) {
    Set<String> distinctIds = new LinkedHashSet<>(ids);
    if (distinctIds.size() > MAX_DISTINCT_IDS) {
      throw new IllegalArgumentException(
        "ids contains " +
        distinctIds.size() +
        " distinct values, maximum is " +
        MAX_DISTINCT_IDS
      );
    }

    Map<String, StopPlaceRecord> stopPlaces = resolveStopPlaces(distinctIds);

    Set<String> refs = stopPlaces
      .values()
      .stream()
      .map(StopPlaceRecord::topographicPlaceRef)
      .filter(Objects::nonNull)
      .collect(Collectors.toCollection(LinkedHashSet::new));

    Map<String, String> names = resolveTopographicNames(refs);

    return distinctIds
      .stream()
      .map(stopPlaces::get)
      .filter(Objects::nonNull)
      .map(record ->
        new StopPlaceSummary(
          record.id(),
          record.transportMode(),
          record.topographicPlaceRef() == null
            ? null
            : names.get(record.topographicPlaceRef())
        )
      )
      .toList();
  }

  private Map<String, StopPlaceRecord> resolveStopPlaces(Set<String> ids) {
    Map<String, StopPlaceRecord> resolved = new LinkedHashMap<>();
    List<String> misses = new ArrayList<>();
    for (String id : ids) {
      StopPlaceRecord cached = stopPlaceCache.getIfPresent(id);
      if (cached != null) {
        resolved.put(id, cached);
      } else {
        misses.add(id);
      }
    }
    if (!misses.isEmpty()) {
      try {
        for (StopPlaceRecord record : client.fetchStopPlaces(misses)) {
          stopPlaceCache.put(record.id(), record);
          resolved.put(record.id(), record);
        }
      } catch (RuntimeException e) {
        // Partial results beat a hard failure: this picker is used while
        // responding to incidents, including incidents affecting the upstream.
        log.warn(
          "Failed to fetch {} stop places, returning {} resolved from cache",
          misses.size(),
          resolved.size(),
          e
        );
      }
    }
    return resolved;
  }

  private Map<String, String> resolveTopographicNames(Set<String> refs) {
    Map<String, String> resolved = new LinkedHashMap<>();
    List<String> misses = new ArrayList<>();
    for (String ref : refs) {
      String cached = topographicNameCache.getIfPresent(ref);
      if (cached != null) {
        resolved.put(ref, cached);
      } else {
        misses.add(ref);
      }
    }
    if (!misses.isEmpty()) {
      try {
        for (TopographicPlaceRecord record : client.fetchTopographicPlaces(misses)) {
          if (record.name() != null) {
            topographicNameCache.put(record.id(), record.name());
            resolved.put(record.id(), record.name());
          }
        }
      } catch (RuntimeException e) {
        log.warn(
          "Failed to fetch {} topographic places, names will be omitted",
          misses.size(),
          e
        );
      }
    }
    return resolved;
  }
}
```

- [ ] **Step 5: Run the tests to verify they pass**

Run: `./mvnw -q test -Dtest=StopPlaceServiceTest`
Expected: 4 tests pass.

- [ ] **Step 6: Commit**

```bash
git add src/main/java/org/entur/enlil/stopplace/StopPlaceService.java src/test/java/org/entur/enlil/stopplace/
git commit -m "feat: add StopPlaceService with id deduplication and input cap"
```

---

### Task 4: StopPlaceService — caching and graceful degradation

**Files:**
- Modify: `src/test/java/org/entur/enlil/stopplace/StopPlaceServiceTest.java`

No implementation change is needed — Task 3's implementation already covers this. These tests lock the behaviour in. If any fails, fix `StopPlaceService`, not the test.

- [ ] **Step 1: Add the tests**

Append inside `StopPlaceServiceTest`:

```java
  @Test
  void secondIdenticalCallIssuesNoUpstreamRequests() {
    Map<String, StopPlaceRecord> stopPlaces = Map.of(
      STOP_A,
      new StopPlaceRecord(STOP_A, "bus", TOPO_A)
    );
    Map<String, TopographicPlaceRecord> topo = Map.of(
      TOPO_A,
      new TopographicPlaceRecord(TOPO_A, "Bergen")
    );
    StopPlaceClientStub client = new StopPlaceClientStub(stopPlaces, topo);
    StopPlaceService service = serviceFor(client);

    service.getStopPlaceSummaries(List.of(STOP_A));
    service.getStopPlaceSummaries(List.of(STOP_A));

    assertThat(client.stopPlaceCalls).hasSize(1);
    assertThat(client.topographicCalls).hasSize(1);
  }

  @Test
  void returnsNullNameWhenTopographicFetchFails() {
    Map<String, StopPlaceRecord> stopPlaces = Map.of(
      STOP_A,
      new StopPlaceRecord(STOP_A, "tram", TOPO_A)
    );
    StopPlaceClientStub client = new StopPlaceClientStub(stopPlaces, Map.of());
    client.topographicFailure = new IllegalStateException("upstream down");

    List<StopPlaceSummary> result = serviceFor(client)
      .getStopPlaceSummaries(List.of(STOP_A));

    assertThat(result).containsExactly(new StopPlaceSummary(STOP_A, "tram", null));
  }

  @Test
  void omitsStopPlacesWhenStopPlaceFetchFails() {
    StopPlaceClientStub client = new StopPlaceClientStub(Map.of(), Map.of());
    client.stopPlaceFailure = new IllegalStateException("upstream down");

    assertThat(serviceFor(client).getStopPlaceSummaries(List.of(STOP_A))).isEmpty();
  }

  @Test
  void omitsUnknownIdsWithoutFailing() {
    StopPlaceClientStub client = new StopPlaceClientStub(Map.of(), Map.of());

    assertThat(serviceFor(client).getStopPlaceSummaries(List.of("NSR:StopPlace:missing")))
      .isEmpty();
  }
```

- [ ] **Step 2: Run the tests**

Run: `./mvnw -q test -Dtest=StopPlaceServiceTest`
Expected: 8 tests pass.

- [ ] **Step 3: Commit**

```bash
git add src/test/java/org/entur/enlil/stopplace/StopPlaceServiceTest.java
git commit -m "test: cover stop place caching and upstream failure degradation"
```

---

### Task 5: The Caffeine cache beans

**Files:**
- Create: `src/main/java/org/entur/enlil/stopplace/StopPlaceCacheConfiguration.java`

- [ ] **Step 1: Write the configuration**

```java
package org.entur.enlil.stopplace;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StopPlaceCacheConfiguration {

  /**
   * Stop place attributes change rarely. Entries are roughly 200 bytes, so a
   * full cache is about 10 MB against a 2 GB heap.
   */
  @Bean("stopPlaceCache")
  Cache<String, StopPlaceRecord> stopPlaceCache(
    @Value("${enlil.stop-places.cache.stop-place-ttl-minutes:60}") long ttlMinutes,
    @Value("${enlil.stop-places.cache.stop-place-max-size:50000}") long maxSize
  ) {
    return Caffeine
      .newBuilder()
      .expireAfterWrite(Duration.ofMinutes(ttlMinutes))
      .maximumSize(maxSize)
      .build();
  }

  /**
   * Municipality and county names effectively never change, and there are only a
   * few hundred of them, so this never evicts in practice.
   */
  @Bean("topographicNameCache")
  Cache<String, String> topographicNameCache(
    @Value("${enlil.stop-places.cache.topographic-ttl-hours:24}") long ttlHours,
    @Value("${enlil.stop-places.cache.topographic-max-size:5000}") long maxSize
  ) {
    return Caffeine
      .newBuilder()
      .expireAfterWrite(Duration.ofHours(ttlHours))
      .maximumSize(maxSize)
      .build();
  }
}
```

- [ ] **Step 2: Verify it compiles**

Run: `./mvnw -q compile`
Expected: BUILD SUCCESS.

- [ ] **Step 3: Commit**

```bash
git add src/main/java/org/entur/enlil/stopplace/StopPlaceCacheConfiguration.java
git commit -m "feat: add caffeine caches for stop place projections"
```

---

### Task 6: The upstream WebClient client

**Files:**
- Create: `src/main/java/org/entur/enlil/stopplace/MummuStopPlaceClient.java`

- [ ] **Step 1: Write the implementation**

```java
package org.entur.enlil.stopplace;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Fetches stop place data from the upstream read API and projects it to small
 * records immediately. The DTOs below declare only the handful of fields we use;
 * {@code ignoreUnknown} means the large geometry members are skipped during
 * parsing rather than materialised, so they never accumulate in heap.
 */
@Component
public class MummuStopPlaceClient implements StopPlaceClient {

  /** The batch size the frontend already used against this API. */
  private static final int CHUNK_SIZE = 200;

  /**
   * Responses run to tens of MB uncompressed. WebClient's default in-memory
   * limit is 256 KB, which would fail these requests outright.
   */
  private static final int MAX_IN_MEMORY_BYTES = 32 * 1024 * 1024;

  private static final ParameterizedTypeReference<
    List<UpstreamStopPlace>
  > STOP_PLACE_LIST = new ParameterizedTypeReference<>() {};

  private static final ParameterizedTypeReference<
    List<UpstreamTopographicPlace>
  > TOPOGRAPHIC_LIST = new ParameterizedTypeReference<>() {};

  private final WebClient webClient;

  public MummuStopPlaceClient(
    WebClient.Builder builder,
    @Value("${enlil.stop-places.url}") String baseUrl
  ) {
    this.webClient =
      builder
        .baseUrl(baseUrl)
        .defaultHeader("ET-Client-Name", "entur-enlil")
        .codecs(configurer ->
          configurer.defaultCodecs().maxInMemorySize(MAX_IN_MEMORY_BYTES)
        )
        .build();
  }

  @Override
  public List<StopPlaceRecord> fetchStopPlaces(Collection<String> ids) {
    return fetchChunked(ids, "/stop-places", STOP_PLACE_LIST)
      .stream()
      .map(sp ->
        new StopPlaceRecord(
          sp.id(),
          sp.transportMode(),
          sp.topographicPlaceRef() == null ? null : sp.topographicPlaceRef().ref()
        )
      )
      .toList();
  }

  @Override
  public List<TopographicPlaceRecord> fetchTopographicPlaces(Collection<String> ids) {
    return fetchChunked(ids, "/topographic-places", TOPOGRAPHIC_LIST)
      .stream()
      .map(tp ->
        new TopographicPlaceRecord(
          tp.id(),
          tp.descriptor() == null || tp.descriptor().name() == null
            ? null
            : tp.descriptor().name().value()
        )
      )
      .toList();
  }

  private <T> List<T> fetchChunked(
    Collection<String> ids,
    String path,
    ParameterizedTypeReference<List<T>> type
  ) {
    List<String> all = List.copyOf(ids);
    List<T> results = new ArrayList<>();
    for (int start = 0; start < all.size(); start += CHUNK_SIZE) {
      List<String> chunk = all.subList(start, Math.min(start + CHUNK_SIZE, all.size()));
      List<T> page = webClient
        .get()
        .uri(uriBuilder ->
          uriBuilder.path(path).queryParam("ids", String.join(",", chunk)).build()
        )
        .retrieve()
        .bodyToMono(type)
        .block();
      if (page != null) {
        results.addAll(page);
      }
    }
    return results;
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  record UpstreamStopPlace(
    String id,
    String transportMode,
    TopographicPlaceRef topographicPlaceRef
  ) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    record TopographicPlaceRef(String ref) {}
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  record UpstreamTopographicPlace(String id, Descriptor descriptor) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    record Descriptor(Name name) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record Name(String value) {}
  }
}
```

- [ ] **Step 2: Verify it compiles**

Run: `./mvnw -q compile`
Expected: BUILD SUCCESS.

- [ ] **Step 3: Commit**

```bash
git add src/main/java/org/entur/enlil/stopplace/MummuStopPlaceClient.java
git commit -m "feat: add WebClient stop place client with chunking and projection"
```

---

### Task 7: Expose the GraphQL query

**Files:**
- Modify: `src/main/resources/graphql/schema.graphqls`
- Modify: `src/main/java/org/entur/enlil/graphql/QueryController.java`

- [ ] **Step 1: Extend the schema**

In `schema.graphqls`, add the field to `type Query` (which currently ends with the `extrajourneys` line):

```graphql
type Query {
    userContext: UserContext
    situationElements(codespace: String!, authority: String!): [SituationElement]
    cancellations(codespace: String!, authority: String!): [Cancellation]
    extrajourneys(codespace: String!, authority: String!, showCompletedTrips: Boolean!): [Extrajourney]
    stopPlaces(ids: [ID!]!): [StopPlaceSummary!]!
}
```

Then add the type. Put it immediately after the `Codespace`/`Permission` block so the top-of-file types stay together:

```graphql
type StopPlaceSummary {
    id: ID!
    transportMode: String
    topographicPlaceName: String
}
```

- [ ] **Step 2: Add the controller method**

In `QueryController.java`, add the import and constructor dependency, then the mapping.

Add to the imports:

```java
import java.util.List;
import org.entur.enlil.stopplace.StopPlaceService;
import org.entur.enlil.stopplace.StopPlaceSummary;
```

Add the field, constructor parameter and assignment alongside the existing three dependencies:

```java
  private final StopPlaceService stopPlaceService;
```

```java
    StopPlaceService stopPlaceService
```

```java
    this.stopPlaceService = stopPlaceService;
```

Add the query method at the end of the class:

```java
  /**
   * Not codespace-scoped, so no {@code @PreAuthorize} — this matches
   * {@code userContext}. Authentication is enforced at the HTTP layer.
   */
  @QueryMapping
  public List<StopPlaceSummary> stopPlaces(@Argument List<String> ids) {
    return stopPlaceService.getStopPlaceSummaries(ids);
  }
```

- [ ] **Step 3: Verify it compiles**

Run: `./mvnw -q compile`
Expected: BUILD SUCCESS.

- [ ] **Step 4: Commit**

```bash
git add src/main/resources/graphql/schema.graphqls src/main/java/org/entur/enlil/graphql/QueryController.java
git commit -m "feat: expose stopPlaces query returning projected summaries"
```

---

### Task 8: Configuration

**Files:**
- Modify: `src/main/resources/application.properties`
- Modify: `helm/enlil/templates/configmap.yaml`
- Modify: `helm/enlil/env/values-kub-ent-dev.yaml`
- Modify: `helm/enlil/env/values-kub-ent-tst.yaml`
- Modify: `helm/enlil/env/values-kub-ent-prd.yaml`

- [ ] **Step 1: Add the local default**

Append to `src/main/resources/application.properties`:

```properties

# Upstream stop place read API
enlil.stop-places.url=https://api.dev.entur.io/stop-places/v1/read
```

- [ ] **Step 2: Add to the configmap template**

In `helm/enlil/templates/configmap.yaml`, append inside the `application.properties` block, after the Authorization section:

```
    # Upstream stop place read API
    enlil.stop-places.url={{ .Values.configMap.stopPlacesUrl }}
```

- [ ] **Step 3: Add the per-environment values**

In `helm/enlil/env/values-kub-ent-dev.yaml`, under the existing `configMap:` key:

```yaml
  stopPlacesUrl: https://api.dev.entur.io/stop-places/v1/read
```

In `helm/enlil/env/values-kub-ent-tst.yaml`:

```yaml
  stopPlacesUrl: https://api.staging.entur.io/stop-places/v1/read
```

In `helm/enlil/env/values-kub-ent-prd.yaml`:

```yaml
  stopPlacesUrl: https://api.entur.io/stop-places/v1/read
```

**Confirm before merging:** the tst → `api.staging` mapping is taken from nirgali's
`.github/environments/config-staging.json` and has not been verified against enlil's own
environment conventions. Check that enlil's tst environment really targets staging.

**Open question, not blocking:** enlil runs in the same cluster as the upstream service, so
an in-cluster URL would avoid the external load balancer entirely. That is an infrastructure
decision and the service DNS has not been confirmed — raise it separately rather than
guessing here.

- [ ] **Step 4: Verify the chart templates**

Run: `helm template helm/enlil -f helm/enlil/env/values-kub-ent-prd.yaml | grep stop-places`
Expected: `enlil.stop-places.url=https://api.entur.io/stop-places/v1/read`

- [ ] **Step 5: Commit**

```bash
git add src/main/resources/application.properties helm/
git commit -m "config: add upstream stop place API url per environment"
```

---

### Task 9: GraphQL integration test

**Files:**
- Create: `src/test/java/org/entur/enlil/stopplace/StopPlaceGraphQlTest.java`

- [ ] **Step 1: Write the test**

```java
package org.entur.enlil.stopplace;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.graphql.test.autoconfigure.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@AutoConfigureGraphQlTester
class StopPlaceGraphQlTest {

  @Autowired
  private GraphQlTester graphQlTester;

  @MockitoBean
  private StopPlaceService stopPlaceService;

  @Test
  void returnsProjectedSummaries() {
    when(stopPlaceService.getStopPlaceSummaries(anyCollection()))
      .thenReturn(List.of(new StopPlaceSummary("NSR:StopPlace:1", "bus", "Oslo")));

    List<StopPlaceSummary> result = graphQlTester
      .document(
        """
        query {
          stopPlaces(ids: ["NSR:StopPlace:1"]) {
            id
            transportMode
            topographicPlaceName
          }
        }
        """
      )
      .execute()
      .path("stopPlaces")
      .entityList(StopPlaceSummary.class)
      .get();

    assertThat(result)
      .containsExactly(new StopPlaceSummary("NSR:StopPlace:1", "bus", "Oslo"));
  }
}
```

- [ ] **Step 2: Run the test**

Run: `./mvnw -q test -Dtest=StopPlaceGraphQlTest`
Expected: PASS.

If the context fails to start because of Firestore or security wiring, mirror whatever
`EnlilApplicationIntegrationTests` does — it already boots the full context and configures a
Firestore emulator via testcontainers plus `MockedClockConfiguration`. Reuse its annotations
rather than inventing new ones.

- [ ] **Step 3: Run the whole suite**

Run: `./mvnw -q test`
Expected: all tests pass, prettier reformats sources during the build.

- [ ] **Step 4: Commit**

```bash
git add src/test/java/org/entur/enlil/stopplace/StopPlaceGraphQlTest.java
git commit -m "test: add graphql integration test for stopPlaces query"
```

---

### Task 10: nirgali — the API call

**Files:**
- Modify: `src/api/api.ts`

- [ ] **Step 1: Replace the two fetch helpers**

Delete `getStopPlaces` and `getTopographicPlaces` (currently at `src/api/api.ts:199-215`) and add in their place:

```ts
export interface StopPlaceSummary {
  id: string;
  transportMode?: string | null;
  topographicPlaceName?: string | null;
}

const getStopPlaceSummaries =
  (URI: string, auth: any) =>
  async (ids: string[]): Promise<StopPlaceSummary[]> => {
    if (ids.length === 0) {
      return [];
    }
    const client = createClient(URI, auth?.user?.access_token);
    const query = gql`
      query StopPlacesQuery($ids: [ID!]!) {
        stopPlaces(ids: $ids) {
          id
          transportMode
          topographicPlaceName
        }
      }
    `;
    const response = await client.query({ query, variables: { ids } });
    return response.data?.stopPlaces ?? [];
  };
```

- [ ] **Step 2: Rewire the api factory**

In the `api` factory object, replace these two lines:

```ts
  getStopPlaces: getStopPlaces(config['stop-places-api']),
  getTopographicPlaces: getTopographicPlaces(config['stop-places-api']),
```

with:

```ts
  getStopPlaceSummaries: getStopPlaceSummaries(
    config['deviation-messages-api'],
    auth,
  ),
```

The call now goes to enlil rather than the public stop place API, and is authenticated.

Leave the `'stop-places-api'` key in `src/config/ConfigContext.ts`, `src/util/test-utils.tsx`
and the `.github/environments/config-*.json` files in place for now — removing it is a
separate cleanup once this has been in production long enough to be sure of a rollback path.

- [ ] **Step 3: Verify it typechecks**

Run: `npx tsc --noEmit`
Expected: no errors relating to `api.ts`. Errors in `StopPicker.tsx` about the removed
methods are expected and are fixed in Task 11.

- [ ] **Step 4: Commit**

```bash
git add src/api/api.ts
git commit -m "feat: fetch stop place summaries from enlil in one call"
```

---

### Task 11: nirgali — rewrite StopPicker

**Files:**
- Create: `src/components/common/StopPicker.test.tsx`
- Modify: `src/components/common/StopPicker.tsx:19-127`

- [ ] **Step 1: Write the failing test**

`StopPicker.test.tsx` — the first test is the regression that caused the incident:

```tsx
import { describe, it, expect, vi } from 'vitest';
import { render, screen, waitFor } from '../../util/test-utils';
import StopPicker from './StopPicker';

const stops = [
  { id: 'q1', name: 'Jernbanetorget', stopPlace: { id: 'NSR:StopPlace:337' } },
  { id: 'q2', name: 'Nationaltheatret', stopPlace: { id: 'NSR:StopPlace:548' } },
];

const summaries = [
  {
    id: 'NSR:StopPlace:337',
    transportMode: 'tram',
    topographicPlaceName: 'Oslo',
  },
  {
    id: 'NSR:StopPlace:548',
    transportMode: 'rail',
    topographicPlaceName: 'Oslo',
  },
];

describe('StopPicker', () => {
  it('does not refetch when re-rendered with a stable api prop', async () => {
    const getStopPlaceSummaries = vi.fn().mockResolvedValue(summaries);
    const api = { getStopPlaceSummaries };

    const { rerender } = render(
      <StopPicker stops={stops} api={api} onChange={() => {}} />,
    );

    await waitFor(() => expect(getStopPlaceSummaries).toHaveBeenCalledTimes(1));

    rerender(<StopPicker stops={stops} api={api} onChange={() => {}} />);
    rerender(<StopPicker stops={stops} api={api} onChange={() => {}} />);

    expect(getStopPlaceSummaries).toHaveBeenCalledTimes(1);
  });

  it('does not refetch when stops is a new array with the same contents', async () => {
    const getStopPlaceSummaries = vi.fn().mockResolvedValue(summaries);
    const api = { getStopPlaceSummaries };

    const { rerender } = render(
      <StopPicker stops={stops} api={api} onChange={() => {}} />,
    );
    await waitFor(() => expect(getStopPlaceSummaries).toHaveBeenCalledTimes(1));

    rerender(
      <StopPicker stops={[...stops]} api={api} onChange={() => {}} />,
    );

    expect(getStopPlaceSummaries).toHaveBeenCalledTimes(1);
  });

  it('requests each stop place id only once', async () => {
    const getStopPlaceSummaries = vi.fn().mockResolvedValue(summaries);
    const duplicated = [...stops, ...stops, ...stops];

    render(
      <StopPicker
        stops={duplicated}
        api={{ getStopPlaceSummaries }}
        onChange={() => {}}
      />,
    );

    await waitFor(() => expect(getStopPlaceSummaries).toHaveBeenCalledTimes(1));
    expect(getStopPlaceSummaries).toHaveBeenCalledWith([
      'NSR:StopPlace:337',
      'NSR:StopPlace:548',
    ]);
  });

  it('renders a label without the parenthetical when the name is missing', async () => {
    const getStopPlaceSummaries = vi.fn().mockResolvedValue([
      { id: 'NSR:StopPlace:337', transportMode: 'tram', topographicPlaceName: null },
    ]);

    render(
      <StopPicker
        stops={[stops[0]]}
        api={{ getStopPlaceSummaries }}
        onChange={() => {}}
      />,
    );

    await waitFor(() => expect(getStopPlaceSummaries).toHaveBeenCalled());
    const input = screen.getByLabelText('Velg stopp');
    input.focus();
    await waitFor(() =>
      expect(
        screen.getByText('Jernbanetorget - NSR:StopPlace:337 - tram'),
      ).toBeInTheDocument(),
    );
  });
});
```

- [ ] **Step 2: Run the test to verify it fails**

Run: `npx vitest run src/components/common/StopPicker.test.tsx`
Expected: FAIL — the component still calls `api.getStopPlaces`, which the stub does not provide.

- [ ] **Step 3: Rewrite the hook and options**

In `StopPicker.tsx`, replace everything from `interface ApiClient` (line 19) through the end
of `useOptions` (line 127) with:

```tsx
interface StopPlaceSummary {
  id: string;
  transportMode?: string | null;
  topographicPlaceName?: string | null;
}

interface ApiClient {
  getStopPlaceSummaries: (ids: string[]) => Promise<StopPlaceSummary[]>;
}

const useStopPlaceSummaries = (stops: Stop[], api: ApiClient) => {
  const [summaries, setSummaries] = useState<Record<string, StopPlaceSummary>>(
    {},
  );

  // Keyed on the joined id string rather than the array, so a new array with the
  // same contents does not retrigger the fetch. Stop place ids are deduplicated
  // here as well as in enlil.
  const idsKey = useMemo(
    () =>
      [
        ...new Set(
          stops.filter((stop) => stop.stopPlace).map((stop) => stop.stopPlace!.id),
        ),
      ].join(','),
    [stops],
  );

  useEffect(() => {
    if (!idsKey) {
      return;
    }
    let cancelled = false;

    api.getStopPlaceSummaries(idsKey.split(',')).then((data) => {
      if (cancelled) {
        return;
      }
      setSummaries((prev) =>
        data.reduce(
          (acc, summary) => {
            acc[summary.id] = summary;
            return acc;
          },
          { ...prev },
        ),
      );
    });

    return () => {
      cancelled = true;
    };
  }, [idsKey, api]);

  return summaries;
};

const useOptions = (stops: Stop[], api: ApiClient, sort = false) => {
  const summaries = useStopPlaceSummaries(stops, api);

  const options = useMemo(() => {
    const stopOptions = stops
      .filter(
        (item, i, list) =>
          i ===
          list.findIndex(
            (j) =>
              j.stopPlace &&
              item.stopPlace &&
              j.stopPlace.id === item.stopPlace.id,
          ),
      )
      .map((item) => {
        const summary = summaries[item.stopPlace!.id];
        return {
          label:
            item.name +
            ' - ' +
            item.stopPlace!.id +
            (summary?.topographicPlaceName
              ? ' (' + summary.topographicPlaceName + ')'
              : '') +
            (summary?.transportMode ? ' - ' + summary.transportMode : ''),
          value: item.stopPlace!.id,
        };
      });

    return sort
      ? stopOptions.sort((a, b) => a.label.localeCompare(b.label))
      : stopOptions;
  }, [stops, summaries, sort]);

  return options;
};
```

Then remove the now-unused `chunk` import at line 4 (`import { chunk } from '../../util/chunk';`). Leave `src/util/chunk.ts` and its test in place — verify with `grep -rn "from '.*util/chunk'" src` whether anything else imports it before deleting.

The label format is unchanged from the current implementation, so the picker looks identical.

- [ ] **Step 4: Run the tests to verify they pass**

Run: `npx vitest run src/components/common/StopPicker.test.tsx`
Expected: 4 tests pass.

- [ ] **Step 5: Commit**

```bash
git add src/components/common/StopPicker.tsx src/components/common/StopPicker.test.tsx
git commit -m "fix: fetch stop place summaries once per distinct id set

The picker previously mapped one id per stop place without deduplicating,
then chained two chunked fan-out request waves. Keying the effect on the
joined id string also stops it refetching when the stops array is
reallocated with identical contents."
```

---

### Task 12: nirgali — memoize the api object

**Files:**
- Modify: `src/components/messages/Messages.tsx:15-49`
- Modify: `src/components/cancellations/Cancellations.tsx:15-53`

- [ ] **Step 1: Memoize in Messages.tsx**

Add `useMemo` to the React import at the top of the file:

```tsx
import { useMemo } from 'react';
```

After the `useMessages(...)` call, add:

```tsx
  const apiClient = useMemo(() => api(config, auth), [config, auth]);
```

Then replace both `api={api(config, auth)}` occurrences (lines 35 and 45) with `api={apiClient}`.

- [ ] **Step 2: Memoize in Cancellations.tsx**

Add the same import, then after the `useCancellations(...)` call:

```tsx
  const apiClient = useMemo(() => api(config, auth), [config, auth]);
```

Replace both `api={api(config, auth)}` occurrences (lines 34 and 47) with `api={apiClient}`.

- [ ] **Step 3: Verify**

Run: `npx tsc --noEmit && npx vitest run`
Expected: no type errors, all tests pass.

- [ ] **Step 4: Check the renewal behaviour**

`useAuth()` from `react-oidc-context` returns a new object on silent token renewal, so
`[config, auth]` still reallocates then. Task 11's `idsKey` guard means that no longer causes
a refetch, so this is now a minor concern rather than a correctness one.

Before narrowing the dependency to `auth?.user?.access_token`, confirm empirically that
nothing else on the `auth` object is read by the api factory — `api(config, auth)` passes
`auth` into several other builders. Record what you find in the commit message rather than
changing it speculatively.

- [ ] **Step 5: Commit**

```bash
git add src/components/messages/Messages.tsx src/components/cancellations/Cancellations.tsx
git commit -m "fix: memoize api client so StopPicker does not refetch every render"
```

---

### Task 13: Format and full verification

- [ ] **Step 1: enlil — build, format and test**

Run: `cd ../enlil && ./mvnw -q clean verify`
Expected: BUILD SUCCESS. prettier runs with goal `write`, so sources may be reformatted.

- [ ] **Step 2: Commit any reformatting**

```bash
git status --short
git add -A && git commit -m "style: apply prettier formatting" || echo "nothing to format"
```

- [ ] **Step 3: nirgali — format check and full test run**

Run: `cd ../nirgali && npm run check && npx vitest run && npx tsc --noEmit`
Expected: prettier check passes, all tests pass, no type errors.

If `npm run check` fails, run `npm run format` and commit the result.

---

## Rollout

enlil ships first and is inert until nirgali calls it — nothing invokes `stopPlaces` before
Task 10 is deployed. Deploy and confirm the query responds before merging the nirgali change.

Verify the fix by watching the load balancer request log for the disappearance of the
`/topographic-places?ids=` and `/stop-places?ids=` bursts:

```
logName="projects/ent-kub-prd/logs/requests"
httpRequest.requestUrl:"topographic-places?ids"
```

Before the fix this showed bursts of thousands per hour against a background of 6–16.

## Out of scope

Capping `ids` in the upstream service. Note that a cap would not have prevented the incident
that motivated this work — those requests carried 200 ids, below any sane ceiling. The load
was rate multiplied by per-response size, which no per-request cap addresses. That work is
being tracked separately.
