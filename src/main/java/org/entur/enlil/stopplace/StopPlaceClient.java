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
