package org.entur.enlil.siri.repository.firestore.mapper;

import java.math.BigInteger;
import java.util.Objects;
import java.util.Optional;
import org.entur.enlil.siri.helpers.DateMapper;
import org.entur.enlil.siri.helpers.SiriObjectFactory;
import org.entur.enlil.siri.repository.firestore.entity.EstimatedVehicleJourneyEntity;
import uk.org.siri.siri21.ArrivalBoardingActivityEnumeration;
import uk.org.siri.siri21.CallStatusEnumeration;
import uk.org.siri.siri21.DepartureBoardingActivityEnumeration;
import uk.org.siri.siri21.EstimatedCall;
import uk.org.siri.siri21.EstimatedVehicleJourney;
import uk.org.siri.siri21.VehicleModesEnumeration;

public class EstimatedVehicleJourneyEntityToSiriMapper {

  private EstimatedVehicleJourneyEntityToSiriMapper() {}

  public static EstimatedVehicleJourney mapToEstimatedVehicleJourney(
    EstimatedVehicleJourneyEntity dto
  ) {
    EstimatedVehicleJourney estimatedVehicleJourney = new EstimatedVehicleJourney();
    estimatedVehicleJourney.setRecordedAtTime(
      DateMapper.mapISOStringToZonedDateTime(
        Objects.requireNonNull(dto.EstimatedVehicleJourney.getRecordedAtTime())
      )
    );
    estimatedVehicleJourney.setLineRef(
      SiriObjectFactory.createLineRef(dto.EstimatedVehicleJourney.getLineRef())
    );
    estimatedVehicleJourney.setDirectionRef(
      SiriObjectFactory.createDirectionRefStructure(
        dto.EstimatedVehicleJourney.getDirectionRef()
      )
    );
    Optional
      .ofNullable(dto.EstimatedVehicleJourney.getFramedVehicleJourneyRef())
      .ifPresent(framedVehicleJourneyRef ->
        estimatedVehicleJourney.setFramedVehicleJourneyRef(
          SiriObjectFactory.createFramedVehicleJourneyRefStructure(
            framedVehicleJourneyRef.getDatedVehicleJourneyRef(),
            framedVehicleJourneyRef.getDataFrameRef()
          )
        )
      );

    Optional
      .ofNullable(dto.EstimatedVehicleJourney.getCancellation())
      .ifPresent(estimatedVehicleJourney::setCancellation);

    Optional
      .ofNullable(dto.EstimatedVehicleJourney.getEstimatedVehicleJourneyCode())
      .ifPresent(estimatedVehicleJourney::setEstimatedVehicleJourneyCode);

    Optional
      .ofNullable(dto.EstimatedVehicleJourney.getExtraJourney())
      .ifPresent(estimatedVehicleJourney::setExtraJourney);

    Optional
      .ofNullable(dto.EstimatedVehicleJourney.getVehicleMode())
      .map(VehicleModesEnumeration::fromValue)
      .ifPresent(vehicleMode -> estimatedVehicleJourney.getVehicleModes().add(vehicleMode)
      );

    Optional
      .ofNullable(dto.EstimatedVehicleJourney.getRouteRef())
      .map(SiriObjectFactory::createRouteRefStructure)
      .ifPresent(estimatedVehicleJourney::setRouteRef);

    Optional
      .ofNullable(dto.EstimatedVehicleJourney.getPublishedLineName())
      .map(SiriObjectFactory::createNaturalLanguageStringStructure)
      .ifPresent(publishedLineName ->
        estimatedVehicleJourney.getPublishedLineNames().add(publishedLineName)
      );

    Optional
      .ofNullable(dto.EstimatedVehicleJourney.getGroupOfLinesRef())
      .map(SiriObjectFactory::createGroupOfLinesRefStructure)
      .ifPresent(estimatedVehicleJourney::setGroupOfLinesRef);

    Optional
      .ofNullable(dto.EstimatedVehicleJourney.getExternalLineRef())
      .map(SiriObjectFactory::createLineRef)
      .ifPresent(estimatedVehicleJourney::setExternalLineRef);

    Optional
      .ofNullable(dto.EstimatedVehicleJourney.getOperatorRef())
      .map(SiriObjectFactory::createOperatorRefStructure)
      .ifPresent(estimatedVehicleJourney::setOperatorRef);

    Optional
      .ofNullable(dto.EstimatedVehicleJourney.getMonitored())
      .ifPresent(estimatedVehicleJourney::setMonitored);

    estimatedVehicleJourney.setDataSource(dto.EstimatedVehicleJourney.getDataSource());

    estimatedVehicleJourney.setEstimatedCalls(
      SiriObjectFactory.createEstimatedCalls(
        dto.EstimatedVehicleJourney
          .getEstimatedCalls()
          .getEstimatedCall()
          .stream()
          .map(EstimatedVehicleJourneyEntityToSiriMapper::mapEstimatedCall)
          .toList()
      )
    );

    estimatedVehicleJourney.setIsCompleteStopSequence(
      dto.EstimatedVehicleJourney.getIsCompleteStopSequence()
    );

    return estimatedVehicleJourney;
  }

  private static EstimatedCall mapEstimatedCall(
    EstimatedVehicleJourneyEntity.EstimatedCall estimatedCall
  ) {
    EstimatedCall mapped = new EstimatedCall();
    mapped.setStopPointRef(
      SiriObjectFactory.createStopPointRefStructure(estimatedCall.getStopPointRef())
    );
    mapped.setOrder(BigInteger.valueOf(estimatedCall.getOrder()));

    Optional
      .ofNullable(estimatedCall.getStopPointName())
      .ifPresent(stopPointName ->
        mapped
          .getStopPointNames()
          .add(SiriObjectFactory.createNaturalLanguageStringStructure(stopPointName))
      );

    Optional
      .ofNullable(estimatedCall.getCancellation())
      .ifPresent(mapped::setCancellation);

    Optional.ofNullable(estimatedCall.getRequestStop()).ifPresent(mapped::setRequestStop);

    Optional
      .ofNullable(estimatedCall.getDestinationDisplay())
      .map(SiriObjectFactory::createNaturalLanguageStringStructure)
      .ifPresent(destinationDisplay ->
        mapped.getDestinationDisplaies().add(destinationDisplay)
      );

    Optional
      .ofNullable(estimatedCall.getAimedArrivalTime())
      .ifPresent(aimedArrivalTime ->
        mapped.setAimedArrivalTime(
          DateMapper.mapISOStringToZonedDateTime(aimedArrivalTime)
        )
      );

    Optional
      .ofNullable(estimatedCall.getExpectedArrivalTime())
      .ifPresent(expectedArrivalTime ->
        mapped.setExpectedArrivalTime(
          DateMapper.mapISOStringToZonedDateTime(expectedArrivalTime)
        )
      );

    Optional
      .ofNullable(estimatedCall.getArrivalStatus())
      .ifPresent(arrivalStatus ->
        mapped.setArrivalStatus(CallStatusEnumeration.fromValue(arrivalStatus))
      );

    Optional
      .ofNullable(estimatedCall.getArrivalBoardingActivity())
      .ifPresent(arrivalBoardingActivity ->
        mapped.setArrivalBoardingActivity(
          ArrivalBoardingActivityEnumeration.fromValue(arrivalBoardingActivity)
        )
      );

    Optional
      .ofNullable(estimatedCall.getAimedDepartureTime())
      .ifPresent(aimedDepartureTime ->
        mapped.setAimedDepartureTime(
          DateMapper.mapISOStringToZonedDateTime(aimedDepartureTime)
        )
      );

    Optional
      .ofNullable(estimatedCall.getExpectedDepartureTime())
      .ifPresent(expectedDepartureTime ->
        mapped.setExpectedDepartureTime(
          DateMapper.mapISOStringToZonedDateTime(expectedDepartureTime)
        )
      );

    Optional
      .ofNullable(estimatedCall.getDepartureStatus())
      .ifPresent(departureStatus ->
        mapped.setDepartureStatus(CallStatusEnumeration.fromValue(departureStatus))
      );

    Optional
      .ofNullable(estimatedCall.getDepartureBoardingActivity())
      .ifPresent(departureBoardingActivity ->
        mapped.setDepartureBoardingActivity(
          DepartureBoardingActivityEnumeration.fromValue(departureBoardingActivity)
        )
      );

    return mapped;
  }
}
