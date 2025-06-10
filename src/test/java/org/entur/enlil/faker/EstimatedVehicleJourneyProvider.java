package org.entur.enlil.faker;

import static java.lang.String.format;
import static java.time.Instant.now;
import static java.time.format.DateTimeFormatter.ISO_INSTANT;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.util.UUID.randomUUID;
import static org.entur.enlil.faker.EnlilFaker.ENLIL_FAKER;
import static org.entur.enlil.faker.EstimatedCallProvider.estimatedCallProvider;

import java.time.Clock;
import java.time.Duration;
import java.util.List;
import net.datafaker.providers.base.AbstractProvider;
import net.datafaker.providers.base.BaseProviders;
import org.entur.enlil.model.EstimatedVehicleJourneyEntity;

public class EstimatedVehicleJourneyProvider extends AbstractProvider<BaseProviders> {

  public EstimatedVehicleJourneyProvider(BaseProviders faker) {
    super(faker);
  }

  public static EstimatedVehicleJourneyProvider estimatedVehicleJourneyProvider() {
    return ENLIL_FAKER.getProvider(
      EstimatedVehicleJourneyProvider.class,
      EstimatedVehicleJourneyProvider::new
    );
  }

  public EstimatedVehicleJourneyEntity.EstimatedVehicleJourney nextForCarPooling(
    Clock clock
  ) {
    var estimatedVehicleJourney =
      new EstimatedVehicleJourneyEntity.EstimatedVehicleJourney();

    estimatedVehicleJourney.setRecordedAtTime(ISO_INSTANT.format(now()));
    estimatedVehicleJourney.setLineRef(format("ENT:Line:%s", randomUUID()));
    estimatedVehicleJourney.setDirectionRef("0");
    estimatedVehicleJourney.setEstimatedVehicleJourneyCode(
      format("ENT:ServiceJourney:%s", randomUUID())
    );
    estimatedVehicleJourney.setExtraJourney(true);
    estimatedVehicleJourney.setRouteRef(format("ENT:Route:%s", randomUUID()));
    estimatedVehicleJourney.setPublishedLineName("Samkjøring");
    estimatedVehicleJourney.setGroupOfLinesRef(format("ENT:Network:%s", randomUUID()));
    estimatedVehicleJourney.setExternalLineRef(format("ENT:Line:%s", randomUUID()));
    estimatedVehicleJourney.setOperatorRef("NLD:Operator:1");
    estimatedVehicleJourney.setMonitored(true);
    estimatedVehicleJourney.setDataSource("ENT");

    var estimatedCalls = new EstimatedVehicleJourneyEntity.EstimatedCalls();
    estimatedCalls.setEstimatedCall(
      List.of(
        estimatedCallProvider().nextDepartureForCarPooling(),
        estimatedCallProvider().nextArrivalForCarPooling()
      )
    );
    estimatedVehicleJourney.setEstimatedCalls(estimatedCalls);
    estimatedVehicleJourney.setIsCompleteStopSequence(true);
    var hours = faker.timeAndDate().duration(1, 3, HOURS);
    estimatedVehicleJourney.setExpiresAtEpochMs(now(clock).plus(hours).toEpochMilli());

    return estimatedVehicleJourney;
  }

  public EstimatedVehicleJourneyEntity.EstimatedVehicleJourney fixedForCarPooling(Clock clock) {
    var estimatedVehicleJourney =
      new EstimatedVehicleJourneyEntity.EstimatedVehicleJourney();

    estimatedVehicleJourney.setRecordedAtTime(ISO_INSTANT.format(now()));
    estimatedVehicleJourney.setLineRef("TST:Line:2");
    estimatedVehicleJourney.setDirectionRef("0");
    estimatedVehicleJourney.setEstimatedVehicleJourneyCode("TST:ServiceJourney:2");
    estimatedVehicleJourney.setExtraJourney(true);
    estimatedVehicleJourney.setRouteRef("TST:Route:2");
    estimatedVehicleJourney.setPublishedLineName("Samkjøring");
    estimatedVehicleJourney.setGroupOfLinesRef("TST:Network:2");
    estimatedVehicleJourney.setExternalLineRef("TST:Line:2");
    estimatedVehicleJourney.setOperatorRef("TST:Operator:2");
    estimatedVehicleJourney.setMonitored(true);
    estimatedVehicleJourney.setDataSource("TST");

    var estimatedCalls = new EstimatedVehicleJourneyEntity.EstimatedCalls();
    estimatedCalls.setEstimatedCall(
      List.of(
        estimatedCallProvider().fixedDepartureForCarPooling(clock),
        estimatedCallProvider().fixedDepartureForCarPooling(clock)
      )
    );
    estimatedVehicleJourney.setEstimatedCalls(estimatedCalls);

    estimatedVehicleJourney.setIsCompleteStopSequence(true);
    var duration = Duration.ofMinutes(20);
    estimatedVehicleJourney.setExpiresAtEpochMs(now(clock).plus(duration).toEpochMilli());

    return estimatedVehicleJourney;
  }
}
