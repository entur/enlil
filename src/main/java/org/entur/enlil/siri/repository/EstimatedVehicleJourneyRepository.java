package org.entur.enlil.siri.repository;

import java.util.stream.Stream;
import uk.org.siri.siri21.EstimatedVehicleJourney;

public interface EstimatedVehicleJourneyRepository {
  Stream<EstimatedVehicleJourney> getAllEstimatedVehicleJourneys();
}
