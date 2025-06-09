package org.entur.enlil.faker;

import net.datafaker.providers.base.AbstractProvider;
import net.datafaker.providers.base.BaseProviders;
import org.entur.enlil.model.EstimatedVehicleJourneyEntity;

import static org.entur.enlil.faker.EnlilFaker.ENLIL_FAKER;
import static org.entur.enlil.faker.PolygonProvider.polygonProvider;

public class ExpectedFlexibleAreaProvider extends AbstractProvider<BaseProviders> {

  public ExpectedFlexibleAreaProvider(BaseProviders faker) {
    super(faker);
  }

  public static ExpectedFlexibleAreaProvider expectedFlexibleAreaProvider() {
    return ENLIL_FAKER.getProvider(ExpectedFlexibleAreaProvider.class, ExpectedFlexibleAreaProvider::new);
  }

  public EstimatedVehicleJourneyEntity.ExpectedFlexibleArea next() {
    var expoectedFlexibleArea = new EstimatedVehicleJourneyEntity.ExpectedFlexibleArea();

    expoectedFlexibleArea.setPolygon(polygonProvider().next());

    return expoectedFlexibleArea;
  }
}
