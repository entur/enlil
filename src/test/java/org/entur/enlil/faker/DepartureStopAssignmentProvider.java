package org.entur.enlil.faker;

import net.datafaker.providers.base.AbstractProvider;
import net.datafaker.providers.base.BaseProviders;
import org.entur.enlil.model.EstimatedVehicleJourneyEntity;

import static org.entur.enlil.faker.EnlilFaker.ENLIL_FAKER;
import static org.entur.enlil.faker.ExpectedFlexibleAreaProvider.expectedFlexibleAreaProvider;

public class DepartureStopAssignmentProvider extends AbstractProvider<BaseProviders> {

  public DepartureStopAssignmentProvider(BaseProviders faker) {
    super(faker);
  }

  public static DepartureStopAssignmentProvider departureStopAssignmentProvider() {
    return ENLIL_FAKER.getProvider(DepartureStopAssignmentProvider.class, DepartureStopAssignmentProvider::new);
  }

  public EstimatedVehicleJourneyEntity.DepartureStopAssignment next() {
    var departureStopAssignment = new EstimatedVehicleJourneyEntity.DepartureStopAssignment();

    departureStopAssignment.setExpectedFlexibleArea(expectedFlexibleAreaProvider().next());

    return departureStopAssignment;
  }
}
