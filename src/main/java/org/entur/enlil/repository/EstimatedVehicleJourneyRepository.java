package org.entur.enlil.repository;

import java.util.stream.Stream;
import org.entur.enlil.model.EstimatedVehicleJourneyEntity;

public interface EstimatedVehicleJourneyRepository {
  Stream<EstimatedVehicleJourneyEntity> getAllEstimatedVehicleJourneys();
  Stream<EstimatedVehicleJourneyEntity> getCancellationsByCodespace(
    String codespace,
    String authority
  );
  Stream<EstimatedVehicleJourneyEntity> getExtrajourneysByCodespace(
    String codespace,
    String authority,
    Boolean showCompletedTrips
  );
  String createCancellation(
    String codespace,
    String authority,
    EstimatedVehicleJourneyEntity input
  );
  String updateCancellation(
    String codespace,
    String authority,
    EstimatedVehicleJourneyEntity input
  );
  String createExtraJourney(
    String codespace,
    String authority,
    EstimatedVehicleJourneyEntity input
  );
  String updateExtraJourney(
    String codespace,
    String authority,
    EstimatedVehicleJourneyEntity input
  );
}
