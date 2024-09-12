package org.entur.enlil.siri.repository.firestore.entity;

import com.google.cloud.firestore.annotation.PropertyName;
import java.util.List;

public class PtSituationElementEntity {

  private String id;
  private String creationTime;
  private String participantRef;
  private String situationNumber;
  private Source source;
  private String progress;
  private ValidityPeriod validityPeriod;
  private String severity;
  private String reportType;
  private Text summary;
  private Text description;
  private Text advice;
  private Affects affects;
  private InfoLinks infoLinks;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @PropertyName("CreationTime")
  public String getCreationTime() {
    return creationTime;
  }

  @PropertyName("CreationTime")
  public void setCreationTime(String creationTime) {
    this.creationTime = creationTime;
  }

  @PropertyName("ParticipantRef")
  public String getParticipantRef() {
    return participantRef;
  }

  @PropertyName("ParticipantRef")
  public void setParticipantRef(String participantRef) {
    this.participantRef = participantRef;
  }

  @PropertyName("SituationNumber")
  public String getSituationNumber() {
    return situationNumber;
  }

  @PropertyName("SituationNumber")
  public void setSituationNumber(String situationNumber) {
    this.situationNumber = situationNumber;
  }

  @PropertyName("Source")
  public Source getSource() {
    return source;
  }

  @PropertyName("Source")
  public void setSource(Source source) {
    this.source = source;
  }

  @PropertyName("Progress")
  public String getProgress() {
    return progress;
  }

  @PropertyName("Progress")
  public void setProgress(String progress) {
    this.progress = progress;
  }

  @PropertyName("ValidityPeriod")
  public ValidityPeriod getValidityPeriod() {
    return validityPeriod;
  }

  @PropertyName("ValidityPeriod")
  public void setValidityPeriod(ValidityPeriod validityPeriod) {
    this.validityPeriod = validityPeriod;
  }

  @PropertyName("Severity")
  public String getSeverity() {
    return severity;
  }

  @PropertyName("Severity")
  public void setSeverity(String severity) {
    this.severity = severity;
  }

  @PropertyName("ReportType")
  public String getReportType() {
    return reportType;
  }

  @PropertyName("ReportType")
  public void setReportType(String reportType) {
    this.reportType = reportType;
  }

  @PropertyName("Summary")
  public Text getSummary() {
    return summary;
  }

  @PropertyName("Summary")
  public void setSummary(Text summary) {
    this.summary = summary;
  }

  @PropertyName("Description")
  public Text getDescription() {
    return description;
  }

  @PropertyName("Description")
  public void setDescription(Text description) {
    this.description = description;
  }

  @PropertyName("Advice")
  public Text getAdvice() {
    return advice;
  }

  @PropertyName("Advice")
  public void setAdvice(Text advice) {
    this.advice = advice;
  }

  @PropertyName("Affects")
  public Affects getAffects() {
    return affects;
  }

  @PropertyName("Affects")
  public void setAffects(Affects affects) {
    this.affects = affects;
  }

  @PropertyName("InfoLinks")
  public InfoLinks getInfoLinks() {
    return infoLinks;
  }

  @PropertyName("InfoLinks")
  public void setInfoLinks(InfoLinks infoLinks) {
    this.infoLinks = infoLinks;
  }

  public static class Source {

    private String sourceType;

    @PropertyName("SourceType")
    public String getSourceType() {
      return sourceType;
    }

    @PropertyName("SourceType")
    public void setSourceType(String sourceType) {
      this.sourceType = sourceType;
    }
  }

  public static class ValidityPeriod {

    private String startTime;
    private String endTime;

    @PropertyName("StartTime")
    public String getStartTime() {
      return startTime;
    }

    @PropertyName("StartTime")
    public void setStartTime(String startTime) {
      this.startTime = startTime;
    }

    @PropertyName("EndTime")
    public String getEndTime() {
      return endTime;
    }

    @PropertyName("EndTime")
    public void setEndTime(String endTime) {
      this.endTime = endTime;
    }
  }

  public static class Text {

    private Attributes attributes;
    private String text;

    @PropertyName("_attributes")
    public Attributes getAttributes() {
      return attributes;
    }

    @PropertyName("_attributes")
    public void setAttributes(Attributes attributes) {
      this.attributes = attributes;
    }

    @PropertyName("_text")
    public String getText() {
      return text;
    }

    @PropertyName("_text")
    public void setText(String text) {
      this.text = text;
    }
  }

  public static class Attributes {

    private String xmlLang;

    @PropertyName("xml:lang")
    public String getXmlLang() {
      return xmlLang;
    }

    @PropertyName("xml:lang")
    public void setXmlLang(String xmlLang) {
      this.xmlLang = xmlLang;
    }
  }

  public static class Affects {

    private Networks networks;
    private StopPoints stopPoints;
    private VehicleJourneys vehicleJourneys;

    @PropertyName("Networks")
    public Networks getNetworks() {
      return networks;
    }

    @PropertyName("Networks")
    public void setNetworks(Networks networks) {
      this.networks = networks;
    }

    @PropertyName("StopPoints")
    public StopPoints getStopPoints() {
      return stopPoints;
    }

    @PropertyName("StopPoints")
    public void setStopPoints(StopPoints stopPoints) {
      this.stopPoints = stopPoints;
    }

    @PropertyName("VehicleJourneys")
    public VehicleJourneys getVehicleJourneys() {
      return vehicleJourneys;
    }

    @PropertyName("VehicleJourneys")
    public void setVehicleJourneys(VehicleJourneys vehicleJourneys) {
      this.vehicleJourneys = vehicleJourneys;
    }
  }

  public static class VehicleJourneys {

    private AffectedVehicleJourney affectedVehicleJourney;

    @PropertyName("AffectedVehicleJourney")
    public AffectedVehicleJourney getAffectedVehicleJourney() {
      return affectedVehicleJourney;
    }

    @PropertyName("AffectedVehicleJourney")
    public void setAffectedVehicleJourney(AffectedVehicleJourney affectedVehicleJourney) {
      this.affectedVehicleJourney = affectedVehicleJourney;
    }
  }

  public static class AffectedVehicleJourney {

    private FramedVehicleJourneyRef framedVehicleJourneyRef;
    private Route route;

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

    @PropertyName("Route")
    public Route getRoute() {
      return route;
    }

    @PropertyName("Route")
    public void setRoute(Route route) {
      this.route = route;
    }
  }

  public static class Networks {

    private AffectedNetwork affectedNetwork;

    @PropertyName("AffectedNetwork")
    public AffectedNetwork getAffectedNetwork() {
      return affectedNetwork;
    }

    @PropertyName("AffectedNetwork")
    public void setAffectedNetwork(AffectedNetwork affectedNetwork) {
      this.affectedNetwork = affectedNetwork;
    }
  }

  public static class AffectedNetwork {

    private AffectedLine affectedLine;

    @PropertyName("AffectedLine")
    public AffectedLine getAffectedLine() {
      return affectedLine;
    }

    @PropertyName("AffectedLine")
    public void setAffectedLine(AffectedLine affectedLine) {
      this.affectedLine = affectedLine;
    }
  }

  public static class AffectedLine {

    private String lineRef;
    private Routes routes;

    @PropertyName("LineRef")
    public String getLineRef() {
      return lineRef;
    }

    @PropertyName("LineRef")
    public void setLineRef(String lineRef) {
      this.lineRef = lineRef;
    }

    @PropertyName("Routes")
    public Routes getRoutes() {
      return routes;
    }

    @PropertyName("Routes")
    public void setRoutes(Routes routes) {
      this.routes = routes;
    }
  }

  public static class Routes {

    private Route affectedRoute;

    @PropertyName("AffectedRoute")
    public Route getAffectedRoute() {
      return affectedRoute;
    }

    @PropertyName("AffectedRoute")
    public void setAffectedRoute(Route affectedRoute) {
      this.affectedRoute = affectedRoute;
    }
  }

  public static class Route {

    private StopPoints stopPoints;

    @PropertyName("StopPoints")
    public StopPoints getStopPoints() {
      return stopPoints;
    }

    @PropertyName("StopPoints")
    public void setStopPoints(StopPoints stopPoints) {
      this.stopPoints = stopPoints;
    }
  }

  public static class StopPoints {

    private List<AffectedStopPoint> affectedStopPoint;

    @PropertyName("AffectedStopPoint")
    public List<AffectedStopPoint> getAffectedStopPoint() {
      return affectedStopPoint;
    }

    @PropertyName("AffectedStopPoint")
    public void setAffectedStopPoint(List<AffectedStopPoint> affectedStopPoint) {
      this.affectedStopPoint = affectedStopPoint;
    }
  }

  public static class AffectedStopPoint {

    private String stopPointRef;

    @PropertyName("StopPointRef")
    public String getStopPointRef() {
      return stopPointRef;
    }

    @PropertyName("StopPointRef")
    public void setStopPointRef(String stopPointRef) {
      this.stopPointRef = stopPointRef;
    }
  }

  public static class InfoLinks {

    private InfoLink infoLink;

    @PropertyName("InfoLink")
    public InfoLink getInfoLink() {
      return infoLink;
    }

    @PropertyName("InfoLink")
    public void setInfoLink(InfoLink infoLink) {
      this.infoLink = infoLink;
    }
  }

  public static class InfoLink {

    private String uri;
    private String label;

    @PropertyName("Uri")
    public String getUri() {
      return uri;
    }

    @PropertyName("Uri")
    public void setUri(String uri) {
      this.uri = uri;
    }

    @PropertyName("Label")
    public String getLabel() {
      return label;
    }

    @PropertyName("Label")
    public void setLabel(String label) {
      this.label = label;
    }
  }
}
