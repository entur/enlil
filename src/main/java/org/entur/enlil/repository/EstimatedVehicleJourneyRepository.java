package org.entur.enlil.repository;

import java.util.stream.Stream;
import org.entur.enlil.model.EstimatedVehicleJourneyEntity;

public interface EstimatedVehicleJourneyRepository {
  Stream<EstimatedVehicleJourneyEntity> getAllEstimatedVehicleJourneys();
}
