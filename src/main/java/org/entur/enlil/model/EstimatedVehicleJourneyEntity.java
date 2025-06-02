package org.entur.enlil.model;

import com.google.cloud.firestore.annotation.PropertyName;
import java.util.List;

public class EstimatedVehicleJourneyEntity {

  private String id;

  private EstimatedVehicleJourney estimatedVehicleJourney;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @PropertyName("EstimatedVehicleJourney")
  public EstimatedVehicleJourney getEstimatedVehicleJourney() {
    return estimatedVehicleJourney;
  }

  @PropertyName("EstimatedVehicleJourney")
  public void setEstimatedVehicleJourney(
    EstimatedVehicleJourney estimatedVehicleJourney
  ) {
    this.estimatedVehicleJourney = estimatedVehicleJourney;
  }

  public static class EstimatedVehicleJourney {

    private String recordedAtTime;
    private String lineRef;
    private String directionRef;
    private FramedVehicleJourneyRef framedVehicleJourneyRef;
    private Boolean cancellation;
    private String estimatedVehicleJourneyCode;
    private Boolean extraJourney;
    private String vehicleMode;
    private String routeRef;
    private String publishedLineName;
    private String groupOfLinesRef;
    private String externalLineRef;
    private String operatorRef;
    private Boolean monitored;
    private String dataSource;
    private EstimatedCalls estimatedCalls;
    private Boolean isCompleteStopSequence;
    private Long expiresAtEpochMs;

    @PropertyName("RecordedAtTime")
    public String getRecordedAtTime() {
      return recordedAtTime;
    }

    @PropertyName("RecordedAtTime")
    public void setRecordedAtTime(String recordedAtTime) {
      this.recordedAtTime = recordedAtTime;
    }

    @PropertyName("LineRef")
    public String getLineRef() {
      return lineRef;
    }

    @PropertyName("LineRef")
    public void setLineRef(String lineRef) {
      this.lineRef = lineRef;
    }

    @PropertyName("DirectionRef")
    public String getDirectionRef() {
      return directionRef;
    }

    @PropertyName("DirectionRef")
    public void setDirectionRef(Object directionRef) {
      try {
        this.directionRef = (String) directionRef;
      } catch (ClassCastException e) {
        this.directionRef = ((Long) directionRef).toString();
      }
    }

    @PropertyName("FramedVehicleJourneyRef")
    public FramedVehicleJourneyRef getFramedVehicleJourneyRef() {
      return framedVehicleJourneyRef;
    }

    @PropertyName("FramedVehicleJourneyRef")
    public void setFramedVehicleJourneyRef(
      FramedVehicleJourneyRef framedVehicleJourneyRef
    ) {
      this.framedVehicleJourneyRef = framedVehicleJourneyRef;
    }

    @PropertyName("Cancellation")
    public Boolean getCancellation() {
      return cancellation;
    }

    @PropertyName("Cancellation")
    public void setCancellation(Boolean cancellation) {
      this.cancellation = cancellation;
    }

    @PropertyName("EstimatedVehicleJourneyCode")
    public String getEstimatedVehicleJourneyCode() {
      return estimatedVehicleJourneyCode;
    }

    @PropertyName("EstimatedVehicleJourneyCode")
    public void setEstimatedVehicleJourneyCode(String estimatedVehicleJourneyCode) {
      this.estimatedVehicleJourneyCode = estimatedVehicleJourneyCode;
    }

    @PropertyName("ExtraJourney")
    public Boolean getExtraJourney() {
      return extraJourney;
    }

    @PropertyName("ExtraJourney")
    public void setExtraJourney(Boolean extraJourney) {
      this.extraJourney = extraJourney;
    }

    @PropertyName("VehicleMode")
    public String getVehicleMode() {
      return vehicleMode;
    }

    @PropertyName("VehicleMode")
    public void setVehicleMode(String vehicleMode) {
      this.vehicleMode = vehicleMode;
    }

    @PropertyName("RouteRef")
    public String getRouteRef() {
      return routeRef;
    }

    @PropertyName("RouteRef")
    public void setRouteRef(String routeRef) {
      this.routeRef = routeRef;
    }

    @PropertyName("PublishedLineName")
    public String getPublishedLineName() {
      return publishedLineName;
    }

    @PropertyName("PublishedLineName")
    public void setPublishedLineName(String publishedLineName) {
      this.publishedLineName = publishedLineName;
    }

    @PropertyName("GroupOfLinesRef")
    public String getGroupOfLinesRef() {
      return groupOfLinesRef;
    }

    @PropertyName("GroupOfLinesRef")
    public void setGroupOfLinesRef(String groupOfLinesRef) {
      this.groupOfLinesRef = groupOfLinesRef;
    }

    @PropertyName("ExternalLineRef")
    public String getExternalLineRef() {
      return externalLineRef;
    }

    @PropertyName("ExternalLineRef")
    public void setExternalLineRef(String externalLineRef) {
      this.externalLineRef = externalLineRef;
    }

    @PropertyName("OperatorRef")
    public String getOperatorRef() {
      return operatorRef;
    }

    @PropertyName("OperatorRef")
    public void setOperatorRef(String operatorRef) {
      this.operatorRef = operatorRef;
    }

    @PropertyName("Monitored")
    public Boolean getMonitored() {
      return monitored;
    }

    @PropertyName("Monitored")
    public void setMonitored(Boolean monitored) {
      this.monitored = monitored;
    }

    @PropertyName("DataSource")
    public String getDataSource() {
      return dataSource;
    }

    @PropertyName("DataSource")
    public void setDataSource(String dataSource) {
      this.dataSource = dataSource;
    }

    @PropertyName("EstimatedCalls")
    public EstimatedCalls getEstimatedCalls() {
      return estimatedCalls;
    }

    @PropertyName("EstimatedCalls")
    public void setEstimatedCalls(EstimatedCalls estimatedCalls) {
      this.estimatedCalls = estimatedCalls;
    }

    @PropertyName("IsCompleteStopSequence")
    public Boolean getIsCompleteStopSequence() {
      return isCompleteStopSequence;
    }

    @PropertyName("IsCompleteStopSequence")
    public void setIsCompleteStopSequence(Boolean isCompleteStopSequence) {
      this.isCompleteStopSequence = isCompleteStopSequence;
    }

    @PropertyName("ExpiresAtEpochMs")
    public Long getExpiresAtEpochMs() {
      return expiresAtEpochMs;
    }

    @PropertyName("ExpiresAtEpochMs")
    public void setExpiresAtEpochMs(Long expiresAtEpochMs) {
      this.expiresAtEpochMs = expiresAtEpochMs;
    }
  }

  public static class EstimatedCalls {

    private List<EstimatedCall> estimatedCall;

    @PropertyName("EstimatedCall")
    public List<EstimatedCall> getEstimatedCall() {
      return estimatedCall;
    }

    @PropertyName("EstimatedCall")
    public void setEstimatedCall(List<EstimatedCall> estimatedCall) {
      this.estimatedCall = estimatedCall;
    }
  }

  public static class EstimatedCall {

    private String stopPointRef;
    private Integer order;
    private String stopPointName;
    private Boolean cancellation;

    private Boolean requestStop;

    private String destinationDisplay;

    private String aimedArrivalTime;

    private String expectedArrivalTime;

    private String aimedDepartureTime;

    private String expectedDepartureTime;

    private String arrivalStatus;

    private String arrivalBoardingActivity;

    private String departureStatus;

    private String departureBoardingActivity;

    private DepartureStopAssignment departureStopAssignment;

    @PropertyName("StopPointRef")
    public String getStopPointRef() {
      return stopPointRef;
    }

    @PropertyName("StopPointRef")
    public void setStopPointRef(String stopPointRef) {
      this.stopPointRef = stopPointRef;
    }

    @PropertyName("Order")
    public Integer getOrder() {
      return order;
    }

    @PropertyName("Order")
    public void setOrder(Integer order) {
      this.order = order;
    }

    @PropertyName("StopPointName")
    public String getStopPointName() {
      return stopPointName;
    }

    @PropertyName("StopPointName")
    public void setStopPointName(String stopPointName) {
      this.stopPointName = stopPointName;
    }

    @PropertyName("Cancellation")
    public Boolean getCancellation() {
      return cancellation;
    }

    @PropertyName("Cancellation")
    public void setCancellation(Boolean cancellation) {
      this.cancellation = cancellation;
    }

    @PropertyName("RequestStop")
    public Boolean getRequestStop() {
      return requestStop;
    }

    @PropertyName("RequestStop")
    public void setRequestStop(Boolean requestStop) {
      this.requestStop = requestStop;
    }

    @PropertyName("DestinationDisplay")
    public String getDestinationDisplay() {
      return destinationDisplay;
    }

    @PropertyName("DestinationDisplay")
    public void setDestinationDisplay(String destinationDisplay) {
      this.destinationDisplay = destinationDisplay;
    }

    @PropertyName("AimedArrivalTime")
    public String getAimedArrivalTime() {
      return aimedArrivalTime;
    }

    @PropertyName("AimedArrivalTime")
    public void setAimedArrivalTime(String aimedArrivalTime) {
      this.aimedArrivalTime = aimedArrivalTime;
    }

    @PropertyName("ExpectedArrivalTime")
    public String getExpectedArrivalTime() {
      return expectedArrivalTime;
    }

    @PropertyName("ExpectedArrivalTime")
    public void setExpectedArrivalTime(String expectedArrivalTime) {
      this.expectedArrivalTime = expectedArrivalTime;
    }

    @PropertyName("AimedDepartureTime")
    public String getAimedDepartureTime() {
      return aimedDepartureTime;
    }

    @PropertyName("AimedDepartureTime")
    public void setAimedDepartureTime(String aimedDepartureTime) {
      this.aimedDepartureTime = aimedDepartureTime;
    }

    @PropertyName("ExpectedDepartureTime")
    public String getExpectedDepartureTime() {
      return expectedDepartureTime;
    }

    @PropertyName("ExpectedDepartureTime")
    public void setExpectedDepartureTime(String expectedDepartureTime) {
      this.expectedDepartureTime = expectedDepartureTime;
    }

    @PropertyName("ArrivalStatus")
    public String getArrivalStatus() {
      return arrivalStatus;
    }

    @PropertyName("ArrivalStatus")
    public void setArrivalStatus(String arrivalStatus) {
      this.arrivalStatus = arrivalStatus;
    }

    @PropertyName("ArrivalBoardingActivity")
    public String getArrivalBoardingActivity() {
      return arrivalBoardingActivity;
    }

    @PropertyName("ArrivalBoardingActivity")
    public void setArrivalBoardingActivity(String arrivalBoardingActivity) {
      this.arrivalBoardingActivity = arrivalBoardingActivity;
    }

    @PropertyName("DepartureStatus")
    public String getDepartureStatus() {
      return departureStatus;
    }

    @PropertyName("DepartureStatus")
    public void setDepartureStatus(String departureStatus) {
      this.departureStatus = departureStatus;
    }

    @PropertyName("DepartureBoardingActivity")
    public String getDepartureBoardingActivity() {
      return departureBoardingActivity;
    }

    @PropertyName("DepartureBoardingActivity")
    public void setDepartureBoardingActivity(String departureBoardingActivity) {
      this.departureBoardingActivity = departureBoardingActivity;
    }

    @PropertyName("DepartureStopAssignment")
    public DepartureStopAssignment getDepartureStopAssignment() {
      return departureStopAssignment;
    }

    @PropertyName("DepartureStopAssignment")
    public void setDepartureStopAssignment(
      DepartureStopAssignment departureStopAssignment
    ) {
      this.departureStopAssignment = departureStopAssignment;
    }
  }

  public static class DepartureStopAssignment {

    private ExpectedFlexibleArea expectedFlexibleArea;

    @PropertyName("ExpectedFlexibleArea")
    public ExpectedFlexibleArea getExpectedFlexibleArea() {
      return expectedFlexibleArea;
    }

    @PropertyName("ExpectedFlexibleArea")
    public void setExpectedFlexibleArea(ExpectedFlexibleArea expectedFlexibleArea) {
      this.expectedFlexibleArea = expectedFlexibleArea;
    }
  }

  public static class ExpectedFlexibleArea {

    private Polygon polygon;

    @PropertyName("Polygon")
    public Polygon getPolygon() {
      return polygon;
    }

    @PropertyName("Polygon")
    public void setPolygon(Polygon polygon) {
      this.polygon = polygon;
    }
  }

  public static class Polygon {

    private LinearRing exterior;

    @PropertyName("Exterior")
    public LinearRing getExterior() {
      return exterior;
    }

    @PropertyName("Exterior")
    public void setExterior(LinearRing exterior) {
      this.exterior = exterior;
    }
  }

  public static class LinearRing {

    private String posList;

    @PropertyName("PosList")
    public String getPosList() {
      return posList;
    }

    @PropertyName("PosList")
    public void setPosList(String posList) {
      this.posList = posList;
    }
  }
}
