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
      Caffeine
        .newBuilder()
        .expireAfterWrite(Duration.ofMinutes(60))
        .maximumSize(1000)
        .build(),
      Caffeine
        .newBuilder()
        .expireAfterWrite(Duration.ofHours(24))
        .maximumSize(1000)
        .build()
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
}
