package org.entur.enlil.faker;

import static java.time.OffsetDateTime.now;
import static java.time.format.DateTimeFormatter.ISO_INSTANT;
import static org.entur.enlil.faker.DepartureStopAssignmentProvider.departureStopAssignmentProvider;
import static org.entur.enlil.faker.EnlilFaker.ENLIL_FAKER;

import net.datafaker.providers.base.AbstractProvider;
import net.datafaker.providers.base.BaseProviders;
import org.entur.enlil.model.EstimatedVehicleJourneyEntity;

import java.time.Clock;

public class EstimatedCallProvider extends AbstractProvider<BaseProviders> {

  public EstimatedCallProvider(BaseProviders faker) {
    super(faker);
  }

  public static EstimatedCallProvider estimatedCallProvider() {
    return ENLIL_FAKER.getProvider(
      EstimatedCallProvider.class,
      EstimatedCallProvider::new
    );
  }

  @SuppressWarnings({ "deprecation" })
  public EstimatedVehicleJourneyEntity.EstimatedCall fixedDepartureForCarPooling(Clock clock) {
    var estmatedCall = new EstimatedVehicleJourneyEntity.EstimatedCall();

    estmatedCall.setStopPointName("Oslo");
    estmatedCall.setOrder(1);
    estmatedCall.setDestinationDisplay("Drammen Roklubb");
    var departureTime = ISO_INSTANT.format(now(clock).plusDays(2));
    estmatedCall.setAimedDepartureTime(departureTime);
    estmatedCall.setExpectedDepartureTime(departureTime);
    estmatedCall.setDepartureBoardingActivity("boarding");
    estmatedCall.setDepartureStopAssignment(departureStopAssignmentProvider().fixedDepartureStop());

    return estmatedCall;
  }

  @SuppressWarnings({ "deprecation" })
  public EstimatedVehicleJourneyEntity.EstimatedCall fixedArrivalForCarPooling(Clock clock) {
    var estmatedCall = new EstimatedVehicleJourneyEntity.EstimatedCall();

    estmatedCall.setStopPointName("Drammen");
    estmatedCall.setOrder(2);
    estmatedCall.setDestinationDisplay("Høyenhall");
    var arrivalTime = ISO_INSTANT.format(now(clock).plusDays(2).plusMinutes(50));
    estmatedCall.setAimedArrivalTime(arrivalTime);
    estmatedCall.setExpectedArrivalTime(arrivalTime);
    estmatedCall.setArrivalBoardingActivity("alighting");
    estmatedCall.setDepartureStopAssignment(departureStopAssignmentProvider().fixedArrivalStop());

    return estmatedCall;
  }

  @SuppressWarnings({ "deprecation" })
  public EstimatedVehicleJourneyEntity.EstimatedCall nextDepartureForCarPooling() {
    var estmatedCall = new EstimatedVehicleJourneyEntity.EstimatedCall();

    estmatedCall.setStopPointName(faker.location().publicSpace());
    estmatedCall.setOrder(1);
    estmatedCall.setDestinationDisplay(faker.address().city());
    var departureTime = faker.timeAndDate().future().toString();
    estmatedCall.setAimedDepartureTime(departureTime);
    estmatedCall.setExpectedDepartureTime(departureTime);
    estmatedCall.setDepartureBoardingActivity("boarding");
    estmatedCall.setDepartureStopAssignment(departureStopAssignmentProvider().next());

    return estmatedCall;
  }

  @SuppressWarnings({ "deprecation" })
  public EstimatedVehicleJourneyEntity.EstimatedCall nextArrivalForCarPooling() {
    var estmatedCall = new EstimatedVehicleJourneyEntity.EstimatedCall();

    estmatedCall.setStopPointName(faker.location().publicSpace());
    estmatedCall.setOrder(2);
    estmatedCall.setDestinationDisplay(faker.address().city());
    var arrivalTime = faker.timeAndDate().future().toString();
    estmatedCall.setAimedArrivalTime(arrivalTime);
    estmatedCall.setExpectedArrivalTime(arrivalTime);
    estmatedCall.setArrivalBoardingActivity("alighting");
    estmatedCall.setDepartureStopAssignment(departureStopAssignmentProvider().next());

    return estmatedCall;
  }
}
