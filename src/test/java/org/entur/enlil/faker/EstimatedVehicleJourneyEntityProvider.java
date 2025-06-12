package org.entur.enlil.faker;

import static org.entur.enlil.faker.EnlilFaker.ENLIL_FAKER;
import static org.entur.enlil.faker.EstimatedVehicleJourneyProvider.estimatedVehicleJourneyProvider;

import java.time.Clock;
import net.datafaker.providers.base.AbstractProvider;
import net.datafaker.providers.base.BaseProviders;
import org.entur.enlil.model.EstimatedVehicleJourneyEntity;

public class EstimatedVehicleJourneyEntityProvider
  extends AbstractProvider<BaseProviders> {

  public EstimatedVehicleJourneyEntityProvider(BaseProviders faker) {
    super(faker);
  }

  public static EstimatedVehicleJourneyEntityProvider estimatedVehicleJourneyEntityProvider() {
    return ENLIL_FAKER.getProvider(
      EstimatedVehicleJourneyEntityProvider.class,
      EstimatedVehicleJourneyEntityProvider::new
    );
  }

  public EstimatedVehicleJourneyEntity fixedCarPoolingVehicleJourney(Clock clock) {
    var estimatedVehicleJourney = new EstimatedVehicleJourneyEntity();

    estimatedVehicleJourney.setEstimatedVehicleJourney(
      estimatedVehicleJourneyProvider().fixedForCarPooling(clock)
    );

    return estimatedVehicleJourney;
  }
}
