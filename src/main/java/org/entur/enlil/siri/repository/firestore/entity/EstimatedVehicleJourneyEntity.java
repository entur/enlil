package org.entur.enlil.siri.repository.firestore.entity;

import com.google.cloud.firestore.annotation.PropertyName;
import java.util.List;

public class EstimatedVehicleJourneyEntity {

  @PropertyName("EstimatedVehicleJourney")
  public EstimatedVehicleJourney EstimatedVehicleJourney;

  public static class EstimatedVehicleJourney {

    @PropertyName("RecordedAtTime")
    private String recordedAtTime;

    @PropertyName("LineRef")
    private String lineRef;

    @PropertyName("DirectionRef")
    private String directionRef;

    @PropertyName("FramedVehicleJourneyRef")
    private FramedVehicleJourneyRef framedVehicleJourneyRef;

    @PropertyName("Cancellation")
    private Boolean cancellation;

    @PropertyName("EstimatedVehicleJourneyCode")
    private String estimatedVehicleJourneyCode;

    @PropertyName("ExtraJourney")
    private Boolean extraJourney;

    @PropertyName("VehicleMode")
    private String vehicleMode;

    @PropertyName("RouteRef")
    private String routeRef;

    @PropertyName("PublishedLineName")
    private String publishedLineName;

    @PropertyName("GroupOfLinesRef")
    private String groupOfLinesRef;

    @PropertyName("ExternalLineRef")
    private String externalLineRef;

    @PropertyName("OperatorRef")
    private String operatorRef;

    @PropertyName("Monitored")
    private Boolean monitored;

    @PropertyName("DataSource")
    private String dataSource;

    @PropertyName("EstimatedCalls")
    private EstimatedCalls estimatedCalls;

    @PropertyName("IsCompleteStopSequence")
    private Boolean isCompleteStopSequence;

    @PropertyName("ExpiresAtEpochMs")
    public Long expiresAtEpochMs;

    public String getRecordedAtTime() {
      return recordedAtTime;
    }

    public void setRecordedAtTime(String recordedAtTime) {
      this.recordedAtTime = recordedAtTime;
    }

    public String getLineRef() {
      return lineRef;
    }

    public void setLineRef(String lineRef) {
      this.lineRef = lineRef;
    }

    public String getDirectionRef() {
      return directionRef;
    }

    public void setDirectionRef(String directionRef) {
      this.directionRef = directionRef;
    }

    public FramedVehicleJourneyRef getFramedVehicleJourneyRef() {
      return framedVehicleJourneyRef;
    }

    public void setFramedVehicleJourneyRef(
      FramedVehicleJourneyRef framedVehicleJourneyRef
    ) {
      this.framedVehicleJourneyRef = framedVehicleJourneyRef;
    }

    public Boolean getCancellation() {
      return cancellation;
    }

    public void setCancellation(Boolean cancellation) {
      this.cancellation = cancellation;
    }

    public String getEstimatedVehicleJourneyCode() {
      return estimatedVehicleJourneyCode;
    }

    public void setEstimatedVehicleJourneyCode(String estimatedVehicleJourneyCode) {
      this.estimatedVehicleJourneyCode = estimatedVehicleJourneyCode;
    }

    public Boolean getExtraJourney() {
      return extraJourney;
    }

    public void setExtraJourney(Boolean extraJourney) {
      this.extraJourney = extraJourney;
    }

    public String getVehicleMode() {
      return vehicleMode;
    }

    public void setVehicleMode(String vehicleMode) {
      this.vehicleMode = vehicleMode;
    }

    public String getRouteRef() {
      return routeRef;
    }

    public void setRouteRef(String routeRef) {
      this.routeRef = routeRef;
    }

    public String getPublishedLineName() {
      return publishedLineName;
    }

    public void setPublishedLineName(String publishedLineName) {
      this.publishedLineName = publishedLineName;
    }

    public String getGroupOfLinesRef() {
      return groupOfLinesRef;
    }

    public void setGroupOfLinesRef(String groupOfLinesRef) {
      this.groupOfLinesRef = groupOfLinesRef;
    }

    public String getExternalLineRef() {
      return externalLineRef;
    }

    public void setExternalLineRef(String externalLineRef) {
      this.externalLineRef = externalLineRef;
    }

    public String getOperatorRef() {
      return operatorRef;
    }

    public void setOperatorRef(String operatorRef) {
      this.operatorRef = operatorRef;
    }

    public Boolean getMonitored() {
      return monitored;
    }

    public void setMonitored(Boolean monitored) {
      this.monitored = monitored;
    }

    public String getDataSource() {
      return dataSource;
    }

    public void setDataSource(String dataSource) {
      this.dataSource = dataSource;
    }

    public EstimatedCalls getEstimatedCalls() {
      return estimatedCalls;
    }

    public void setEstimatedCalls(EstimatedCalls estimatedCalls) {
      this.estimatedCalls = estimatedCalls;
    }

    public Boolean getIsCompleteStopSequence() {
      return isCompleteStopSequence;
    }

    public void setIsCompleteStopSequence(Boolean isCompleteStopSequence) {
      isCompleteStopSequence = isCompleteStopSequence;
    }
  }

  public static class EstimatedCalls {

    @PropertyName("EstimatedCall")
    List<EstimatedCall> estimatedCall;

    public List<EstimatedCall> getEstimatedCall() {
      return estimatedCall;
    }

    public void setEstimatedCall(List<EstimatedCall> estimatedCall) {
      this.estimatedCall = estimatedCall;
    }
  }

  public static class EstimatedCall {

    @PropertyName("StopPointRef")
    private String stopPointRef;

    @PropertyName("Order")
    private Integer order;

    @PropertyName("StopPointName")
    private String stopPointName;

    @PropertyName("Cancellation")
    private Boolean cancellation;

    @PropertyName("RequestStop")
    private Boolean requestStop;

    @PropertyName("DestinationDisplay")
    private String destinationDisplay;

    @PropertyName("AimedArrivalTime")
    private String aimedArrivalTime;

    @PropertyName("ExpectedArrivalTime")
    private String expectedArrivalTime;

    @PropertyName("AimedDepartureTime")
    private String aimedDepartureTime;

    @PropertyName("ExpectedDepartureTime")
    private String expectedDepartureTime;

    @PropertyName("ArrivalStatus")
    private String arrivalStatus;

    @PropertyName("ArrivalBoardingActivity")
    private String arrivalBoardingActivity;

    @PropertyName("DepartureStatus")
    private String departureStatus;

    @PropertyName("DepartureBoardingActivity")
    private String departureBoardingActivity;

    public String getStopPointRef() {
      return stopPointRef;
    }

    public void setStopPointRef(String stopPointRef) {
      this.stopPointRef = stopPointRef;
    }

    public Integer getOrder() {
      return order;
    }

    public void setOrder(Integer order) {
      this.order = order;
    }

    public String getStopPointName() {
      return stopPointName;
    }

    public void setStopPointName(String stopPointName) {
      this.stopPointName = stopPointName;
    }

    public Boolean getCancellation() {
      return cancellation;
    }

    public void setCancellation(Boolean cancellation) {
      this.cancellation = cancellation;
    }

    public Boolean getRequestStop() {
      return requestStop;
    }

    public void setRequestStop(Boolean requestStop) {
      this.requestStop = requestStop;
    }

    public String getDestinationDisplay() {
      return destinationDisplay;
    }

    public void setDestinationDisplay(String destinationDisplay) {
      this.destinationDisplay = destinationDisplay;
    }

    public String getAimedArrivalTime() {
      return aimedArrivalTime;
    }

    public void setAimedArrivalTime(String aimedArrivalTime) {
      this.aimedArrivalTime = aimedArrivalTime;
    }

    public String getExpectedArrivalTime() {
      return expectedArrivalTime;
    }

    public void setExpectedArrivalTime(String expectedArrivalTime) {
      this.expectedArrivalTime = expectedArrivalTime;
    }

    public String getAimedDepartureTime() {
      return aimedDepartureTime;
    }

    public void setAimedDepartureTime(String aimedDepartureTime) {
      this.aimedDepartureTime = aimedDepartureTime;
    }

    public String getExpectedDepartureTime() {
      return expectedDepartureTime;
    }

    public void setExpectedDepartureTime(String expectedDepartureTime) {
      this.expectedDepartureTime = expectedDepartureTime;
    }

    public String getArrivalStatus() {
      return arrivalStatus;
    }

    public void setArrivalStatus(String arrivalStatus) {
      this.arrivalStatus = arrivalStatus;
    }

    public String getArrivalBoardingActivity() {
      return arrivalBoardingActivity;
    }

    public void setArrivalBoardingActivity(String arrivalBoardingActivity) {
      this.arrivalBoardingActivity = arrivalBoardingActivity;
    }

    public String getDepartureStatus() {
      return departureStatus;
    }

    public void setDepartureStatus(String departureStatus) {
      this.departureStatus = departureStatus;
    }

    public String getDepartureBoardingActivity() {
      return departureBoardingActivity;
    }

    public void setDepartureBoardingActivity(String departureBoardingActivity) {
      this.departureBoardingActivity = departureBoardingActivity;
    }
  }
}
