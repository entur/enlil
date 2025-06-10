package org.entur.enlil.faker;

import static org.entur.enlil.faker.EnlilFaker.ENLIL_FAKER;

import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.datafaker.providers.base.AbstractProvider;
import net.datafaker.providers.base.BaseProviders;
import org.apache.commons.lang3.StringUtils;
import org.entur.enlil.model.EstimatedVehicleJourneyEntity;

public class LinearRingProvider extends AbstractProvider<BaseProviders> {

  public LinearRingProvider(BaseProviders faker) {
    super(faker);
  }

  public static LinearRingProvider linearRingProvider() {
    return ENLIL_FAKER.getProvider(LinearRingProvider.class, LinearRingProvider::new);
  }

  /**
   * Doesn't generate a sensible nor valid polygon, but good enough for silly test data
   *
   * @return a linear ring that can represent a polygon
   */
  public EstimatedVehicleJourneyEntity.LinearRing next() {
    var linearRing = new EstimatedVehicleJourneyEntity.LinearRing();

    var startStop = new String[] { faker.address().latLon(" ") };

    int size = faker.random().nextInt(1, 3);
    var middle = IntStream
      .range(0, size)
      .boxed()
      .map(i -> faker.address().latLon(" "))
      .toArray(String[]::new);

    var posList = Stream
      .concat(
        Stream.concat(Arrays.stream(startStop), Arrays.stream(middle)),
        Arrays.stream(startStop)
      )
      .toArray();

    linearRing.setPosList(StringUtils.join(posList, " "));

    return linearRing;
  }
}
