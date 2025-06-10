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

  public EstimatedVehicleJourneyEntity.LinearRing fixedHoyenhall() {
    var linearRing = new EstimatedVehicleJourneyEntity.LinearRing();

    linearRing.setPosList(
      """
      							10.813701152801514 59.90490269568961
      							10.813824534416199 59.905002222436366
      							10.818298459053041 59.90387782183805
      							10.818470120429994 59.90393162177695
      							10.81859886646271 59.903902031821346
      							10.818480849266054 59.903821331808345
      							10.818260908126833 59.903805191782205
      							10.813701152801514 59.90490269568961\
      """
    );

    return linearRing;
  }

  public EstimatedVehicleJourneyEntity.LinearRing fixedDrammenRoklubb() {
    var linearRing = new EstimatedVehicleJourneyEntity.LinearRing();

    linearRing.setPosList(
      """
      							10.198048353195192 59.744999037838035
      							10.197742581367494 59.744653070138405
      						  10.197254419326784 59.74476388830704
      						  10.197045207023622 59.7446233383721
      						  10.197629928588867 59.74445846173351
      						  10.197780132293703 59.744542251602304
      						  10.197908878326418 59.74463955570244
      						  10.198810100555422 59.744396294920904
      						  10.198960304260256 59.744482787846074
      						  10.198627710342409 59.74456928054744
      						  10.19883155822754 59.74479902569844
      						  10.198048353195192 59.744999037838035\
      """
    );

    return linearRing;
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
