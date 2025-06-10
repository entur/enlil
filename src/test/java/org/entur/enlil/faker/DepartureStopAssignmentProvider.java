package org.entur.enlil.faker;

import static org.entur.enlil.faker.EnlilFaker.ENLIL_FAKER;
import static org.entur.enlil.faker.ExpectedFlexibleAreaProvider.expectedFlexibleAreaProvider;

import net.datafaker.providers.base.AbstractProvider;
import net.datafaker.providers.base.BaseProviders;
import org.entur.enlil.model.EstimatedVehicleJourneyEntity;

public class DepartureStopAssignmentProvider extends AbstractProvider<BaseProviders> {

  public DepartureStopAssignmentProvider(BaseProviders faker) {
    super(faker);
  }

  public static DepartureStopAssignmentProvider departureStopAssignmentProvider() {
    return ENLIL_FAKER.getProvider(
      DepartureStopAssignmentProvider.class,
      DepartureStopAssignmentProvider::new
    );
  }

  public EstimatedVehicleJourneyEntity.DepartureStopAssignment fixedDepartureStop() {
    var departureStopAssignment =
      new EstimatedVehicleJourneyEntity.DepartureStopAssignment();

    departureStopAssignment.setExpectedFlexibleArea(
      expectedFlexibleAreaProvider().fixedDepartureArea()
    );

    return departureStopAssignment;
  }

  public EstimatedVehicleJourneyEntity.DepartureStopAssignment fixedArrivalStop() {
    var departureStopAssignment =
      new EstimatedVehicleJourneyEntity.DepartureStopAssignment();

    departureStopAssignment.setExpectedFlexibleArea(
      expectedFlexibleAreaProvider().fixedArrivalArea()
    );

    return departureStopAssignment;
  }

  public EstimatedVehicleJourneyEntity.DepartureStopAssignment next() {
    var departureStopAssignment =
      new EstimatedVehicleJourneyEntity.DepartureStopAssignment();

    departureStopAssignment.setExpectedFlexibleArea(
      expectedFlexibleAreaProvider().next()
    );

    return departureStopAssignment;
  }
}
