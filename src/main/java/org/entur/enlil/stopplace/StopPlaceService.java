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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StopPlaceService {

  private static final Logger log = LoggerFactory.getLogger(StopPlaceService.class);

  private final StopPlaceClient client;
  private final Cache<String, StopPlaceRecord> stopPlaceCache;
  private final Cache<String, String> topographicNameCache;

  /**
   * Counted after deduplication, so a redundant list is corrected rather than
   * refused. This is a runaway-input backstop, not a cost control: a single
   * organisation legitimately has thousands of distinct stop places, and each one
   * costs only a small cached record plus 1/200th of an upstream request.
   */
  private final int maxDistinctIds;

  public StopPlaceService(
    StopPlaceClient client,
    @Qualifier("stopPlaceCache") Cache<String, StopPlaceRecord> stopPlaceCache,
    @Qualifier("topographicNameCache") Cache<String, String> topographicNameCache,
    @Value("${enlil.stop-places.max-ids:10000}") int maxDistinctIds
  ) {
    this.client = client;
    this.stopPlaceCache = stopPlaceCache;
    this.topographicNameCache = topographicNameCache;
    this.maxDistinctIds = maxDistinctIds;
  }

  public List<StopPlaceSummary> getStopPlaceSummaries(Collection<String> ids) {
    Set<String> distinctIds = new LinkedHashSet<>(ids);
    if (distinctIds.size() > maxDistinctIds) {
      throw new TooManyStopPlaceIdsException(distinctIds.size(), maxDistinctIds);
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
