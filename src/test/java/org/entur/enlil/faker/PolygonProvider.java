package org.entur.enlil.faker;

import static org.entur.enlil.faker.EnlilFaker.ENLIL_FAKER;
import static org.entur.enlil.faker.LinearRingProvider.linearRingProvider;

import net.datafaker.providers.base.AbstractProvider;
import net.datafaker.providers.base.BaseProviders;
import org.entur.enlil.model.EstimatedVehicleJourneyEntity;

public class PolygonProvider extends AbstractProvider<BaseProviders> {

  public PolygonProvider(BaseProviders faker) {
    super(faker);
  }

  public static PolygonProvider polygonProvider() {
    return ENLIL_FAKER.getProvider(PolygonProvider.class, PolygonProvider::new);
  }

  public EstimatedVehicleJourneyEntity.Polygon next() {
    var polygon = new EstimatedVehicleJourneyEntity.Polygon();

    polygon.setExterior(linearRingProvider().next());

    return polygon;
  }
}
