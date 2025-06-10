package org.entur.enlil.faker;

import net.datafaker.providers.base.AbstractProvider;
import net.datafaker.providers.base.BaseProviders;
import org.entur.enlil.model.EstimatedVehicleJourneyEntity;

import static org.entur.enlil.faker.EnlilFaker.ENLIL_FAKER;
import static org.entur.enlil.faker.LinearRingProvider.linearRingProvider;

public class PolygonProvider extends AbstractProvider<BaseProviders> {

  public PolygonProvider(BaseProviders faker) {
    super(faker);
  }

  public static PolygonProvider polygonProvider() {
    return ENLIL_FAKER.getProvider(PolygonProvider.class, PolygonProvider::new);
  }

  public EstimatedVehicleJourneyEntity.Polygon fixedDeparturePolygon() {
    var polygon = new EstimatedVehicleJourneyEntity.Polygon();

    polygon.setExterior(linearRingProvider().fixedHoyenhall());

    return polygon;
  }

  public EstimatedVehicleJourneyEntity.Polygon fixedArrivalPolygon() {
    var polygon = new EstimatedVehicleJourneyEntity.Polygon();

    polygon.setExterior(linearRingProvider().fixedDrammenRoklubb());

    return polygon;
  }

  public EstimatedVehicleJourneyEntity.Polygon next() {
    var polygon = new EstimatedVehicleJourneyEntity.Polygon();

    polygon.setExterior(linearRingProvider().next());

    return polygon;
  }
}
