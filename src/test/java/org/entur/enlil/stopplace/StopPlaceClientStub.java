package org.entur.enlil.stopplace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    return ids.stream().map(stopPlaces::get).filter(Objects::nonNull).toList();
  }

  @Override
  public List<TopographicPlaceRecord> fetchTopographicPlaces(Collection<String> ids) {
    topographicCalls.add(List.copyOf(ids));
    if (topographicFailure != null) {
      throw topographicFailure;
    }
    return ids.stream().map(topographicPlaces::get).filter(Objects::nonNull).toList();
  }
}
