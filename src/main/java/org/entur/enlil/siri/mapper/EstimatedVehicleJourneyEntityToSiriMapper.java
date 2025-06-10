package org.entur.enlil.siri.mapper;

import java.math.BigInteger;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import net.opengis.gml._3.*;
import net.opengis.gml._3.ObjectFactory;
import org.apache.commons.lang3.StringUtils;
import org.entur.enlil.model.EstimatedVehicleJourneyEntity;
import org.entur.enlil.siri.helpers.DateMapper;
import org.entur.enlil.siri.helpers.SiriObjectFactory;
import uk.org.siri.siri21.*;

public class EstimatedVehicleJourneyEntityToSiriMapper {

  private EstimatedVehicleJourneyEntityToSiriMapper() {}

  public static EstimatedVehicleJourney mapToEstimatedVehicleJourney(
    EstimatedVehicleJourneyEntity dto
  ) {
    EstimatedVehicleJourney estimatedVehicleJourney = new EstimatedVehicleJourney();
    estimatedVehicleJourney.setRecordedAtTime(
      DateMapper.mapISOStringToZonedDateTime(
        Objects.requireNonNull(dto.getEstimatedVehicleJourney().getRecordedAtTime())
      )
    );
    estimatedVehicleJourney.setLineRef(
      SiriObjectFactory.createLineRef(dto.getEstimatedVehicleJourney().getLineRef())
    );
    estimatedVehicleJourney.setDirectionRef(
      SiriObjectFactory.createDirectionRefStructure(
        dto.getEstimatedVehicleJourney().getDirectionRef()
      )
    );
    Optional
      .ofNullable(dto.getEstimatedVehicleJourney().getFramedVehicleJourneyRef())
      .ifPresent(framedVehicleJourneyRef ->
        estimatedVehicleJourney.setFramedVehicleJourneyRef(
          SiriObjectFactory.createFramedVehicleJourneyRefStructure(
            framedVehicleJourneyRef.getDatedVehicleJourneyRef(),
            framedVehicleJourneyRef.getDataFrameRef()
          )
        )
      );

    Optional
      .ofNullable(dto.getEstimatedVehicleJourney().getCancellation())
      .ifPresent(estimatedVehicleJourney::setCancellation);

    Optional
      .ofNullable(dto.getEstimatedVehicleJourney().getEstimatedVehicleJourneyCode())
      .ifPresent(estimatedVehicleJourney::setEstimatedVehicleJourneyCode);

    Optional
      .ofNullable(dto.getEstimatedVehicleJourney().getExtraJourney())
      .ifPresent(estimatedVehicleJourney::setExtraJourney);

    Optional
      .ofNullable(dto.getEstimatedVehicleJourney().getVehicleMode())
      .map(VehicleModesEnumeration::fromValue)
      .ifPresent(vehicleMode -> estimatedVehicleJourney.getVehicleModes().add(vehicleMode)
      );

    Optional
      .ofNullable(dto.getEstimatedVehicleJourney().getRouteRef())
      .map(SiriObjectFactory::createRouteRefStructure)
      .ifPresent(estimatedVehicleJourney::setRouteRef);

    Optional
      .ofNullable(dto.getEstimatedVehicleJourney().getPublishedLineName())
      .map(SiriObjectFactory::createNaturalLanguageStringStructure)
      .ifPresent(publishedLineName ->
        estimatedVehicleJourney.getPublishedLineNames().add(publishedLineName)
      );

    Optional
      .ofNullable(dto.getEstimatedVehicleJourney().getGroupOfLinesRef())
      .map(SiriObjectFactory::createGroupOfLinesRefStructure)
      .ifPresent(estimatedVehicleJourney::setGroupOfLinesRef);

    Optional
      .ofNullable(dto.getEstimatedVehicleJourney().getExternalLineRef())
      .map(SiriObjectFactory::createLineRef)
      .ifPresent(estimatedVehicleJourney::setExternalLineRef);

    Optional
      .ofNullable(dto.getEstimatedVehicleJourney().getOperatorRef())
      .map(SiriObjectFactory::createOperatorRefStructure)
      .ifPresent(estimatedVehicleJourney::setOperatorRef);

    Optional
      .ofNullable(dto.getEstimatedVehicleJourney().getMonitored())
      .ifPresent(estimatedVehicleJourney::setMonitored);

    estimatedVehicleJourney.setDataSource(
      dto.getEstimatedVehicleJourney().getDataSource()
    );

    estimatedVehicleJourney.setEstimatedCalls(
      SiriObjectFactory.createEstimatedCalls(
        dto
          .getEstimatedVehicleJourney()
          .getEstimatedCalls()
          .getEstimatedCall()
          .stream()
          .map(EstimatedVehicleJourneyEntityToSiriMapper::mapEstimatedCall)
          .toList()
      )
    );

    estimatedVehicleJourney.setIsCompleteStopSequence(
      dto.getEstimatedVehicleJourney().getIsCompleteStopSequence()
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

    Optional
      .ofNullable(estimatedCall.getDepartureStopAssignment())
      .ifPresent(departureStopAssignment ->
        mapped
          .getDepartureStopAssignments()
          .add(mapDepartureStopAssignment(departureStopAssignment))
      );

    return mapped;
  }

  private static StopAssignmentStructure mapDepartureStopAssignment(
    EstimatedVehicleJourneyEntity.DepartureStopAssignment departureStopAssignment
  ) {
    var departureStopAssignments = new StopAssignmentStructure();

    Optional
      .ofNullable(departureStopAssignment.getExpectedFlexibleArea())
      .ifPresent(expectedFlexibleArea ->
        departureStopAssignments.setExpectedFlexibleArea(
          mapExpectedFlexibleArea(expectedFlexibleArea)
        )
      );

    return departureStopAssignments;
  }

  private static AimedFlexibleArea mapExpectedFlexibleArea(
    EstimatedVehicleJourneyEntity.ExpectedFlexibleArea expectedFlexibleArea
  ) {
    var flexibleArea = new AimedFlexibleArea();

    Optional
      .ofNullable(expectedFlexibleArea.getPolygon())
      .ifPresent(polygon -> flexibleArea.setPolygon(mapPolygon(polygon)));

    return flexibleArea;
  }

  private static PolygonType mapPolygon(EstimatedVehicleJourneyEntity.Polygon polygon) {
    var gmlPolygon = new PolygonType();

    Optional
      .ofNullable(polygon.getExterior())
      .ifPresent(exterior -> gmlPolygon.setExterior(mapExterior(exterior)));

    return gmlPolygon;
  }

  private static AbstractRingPropertyType mapExterior(
    EstimatedVehicleJourneyEntity.LinearRing linearRing
  ) {
    var exterior = new AbstractRingPropertyType();

    var sanitized = linearRing.getPosList().replaceAll("[^ 0-9.\\-\\n]", "");
    var pos = StringUtils.split(sanitized, " \n");
    var coordinates = Stream.of(pos).map(Double::parseDouble).toList();
    var posList = new PosList();
    posList.getValues().addAll(coordinates);

    var ring = new LinearRingType();
    ring.setPosList(posList);

    var gmlFactory = new ObjectFactory();
    exterior.setAbstractRing(gmlFactory.createLinearRing(ring));

    return exterior;
  }
}
