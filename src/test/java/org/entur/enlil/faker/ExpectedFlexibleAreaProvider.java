package org.entur.enlil.faker;

import static org.entur.enlil.faker.EnlilFaker.ENLIL_FAKER;
import static org.entur.enlil.faker.PolygonProvider.polygonProvider;

import net.datafaker.providers.base.AbstractProvider;
import net.datafaker.providers.base.BaseProviders;
import org.entur.enlil.model.EstimatedVehicleJourneyEntity;

public class ExpectedFlexibleAreaProvider extends AbstractProvider<BaseProviders> {

  public ExpectedFlexibleAreaProvider(BaseProviders faker) {
    super(faker);
  }

  public static ExpectedFlexibleAreaProvider expectedFlexibleAreaProvider() {
    return ENLIL_FAKER.getProvider(
      ExpectedFlexibleAreaProvider.class,
      ExpectedFlexibleAreaProvider::new
    );
  }

  public EstimatedVehicleJourneyEntity.ExpectedFlexibleArea fixedDepartureArea() {
    var expoectedFlexibleArea = new EstimatedVehicleJourneyEntity.ExpectedFlexibleArea();

    expoectedFlexibleArea.setPolygon(polygonProvider().fixedDeparturePolygon());

    return expoectedFlexibleArea;
  }

  public EstimatedVehicleJourneyEntity.ExpectedFlexibleArea fixedArrivalArea() {
    var expoectedFlexibleArea = new EstimatedVehicleJourneyEntity.ExpectedFlexibleArea();

    expoectedFlexibleArea.setPolygon(polygonProvider().fixedArrivalPolygon());

    return expoectedFlexibleArea;
  }

  public EstimatedVehicleJourneyEntity.ExpectedFlexibleArea next() {
    var expoectedFlexibleArea = new EstimatedVehicleJourneyEntity.ExpectedFlexibleArea();

    expoectedFlexibleArea.setPolygon(polygonProvider().next());

    return expoectedFlexibleArea;
  }
}
