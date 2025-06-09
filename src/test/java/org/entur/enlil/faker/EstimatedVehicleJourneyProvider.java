package org.entur.enlil.faker;

import net.datafaker.providers.base.AbstractProvider;
import net.datafaker.providers.base.BaseProviders;
import org.entur.enlil.model.EstimatedVehicleJourneyEntity;

import java.time.Clock;
import java.util.List;

import static java.lang.String.format;
import static java.time.OffsetDateTime.now;
import static java.time.format.DateTimeFormatter.ISO_INSTANT;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.util.UUID.randomUUID;
import static org.entur.enlil.faker.EnlilFaker.ENLIL_FAKER;
import static org.entur.enlil.faker.EstimatedCallProvider.estimatedCallProvider;

public class EstimatedVehicleJourneyProvider extends AbstractProvider<BaseProviders> {

  public EstimatedVehicleJourneyProvider(BaseProviders faker) {
    super(faker);
  }

  public static EstimatedVehicleJourneyProvider estimatedVehicleJourneyProvider() {
    return ENLIL_FAKER.getProvider(EstimatedVehicleJourneyProvider.class, EstimatedVehicleJourneyProvider::new);
  }

  public EstimatedVehicleJourneyEntity.EstimatedVehicleJourney nextForCarPooling(Clock clock) {
      var estimatedVehicleJourney = new EstimatedVehicleJourneyEntity.EstimatedVehicleJourney();

      estimatedVehicleJourney.setRecordedAtTime(ISO_INSTANT.format(now()));
      estimatedVehicleJourney.setLineRef(format("ENT:Line:%s", randomUUID()));
      estimatedVehicleJourney.setEstimatedVehicleJourneyCode(format("ENT:ServiceJourney:%s", randomUUID()));
      estimatedVehicleJourney.setExtraJourney(true);
      estimatedVehicleJourney.setRouteRef(format("ENT:Route:%s", randomUUID()));
      estimatedVehicleJourney.setPublishedLineName("Samkjøring");
      estimatedVehicleJourney.setGroupOfLinesRef(format("ENT:Network:%s", randomUUID()));
      estimatedVehicleJourney.setExternalLineRef(format("ENT:Line:%s", randomUUID()));
      estimatedVehicleJourney.setOperatorRef("NLD:Operator:1");
      estimatedVehicleJourney.setMonitored(true);
      estimatedVehicleJourney.setDataSource("ENT");

      var estimatedCalls = new EstimatedVehicleJourneyEntity.EstimatedCalls();
      estimatedCalls.setEstimatedCall(List.of(
        estimatedCallProvider().nextDepartureForCarPooling(),
        estimatedCallProvider().nextArrivalForCarPooling()
      ));
      estimatedVehicleJourney.setEstimatedCalls(estimatedCalls);
      estimatedVehicleJourney.setIsCompleteStopSequence(true);
      var hours = faker.timeAndDate().duration(1, 3, HOURS);
      estimatedVehicleJourney.setExpiresAtEpochMs(now(clock).plus(hours).toEpochSecond());

      return estimatedVehicleJourney;
  }
}
