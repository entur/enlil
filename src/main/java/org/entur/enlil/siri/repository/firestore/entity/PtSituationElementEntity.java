package org.entur.enlil.siri.repository.firestore.entity;

import com.google.cloud.firestore.annotation.PropertyName;
import java.util.List;

public class PtSituationElementEntity {

  @PropertyName("CreationTime")
  private String creationTime;

  @PropertyName("ParticipantRef")
  private String participantRef;

  @PropertyName("SituationNumber")
  private String situationNumber;

  @PropertyName("Source")
  private Source source;

  @PropertyName("Progress")
  private String progress;

  @PropertyName("ValidityPeriod")
  private ValidityPeriod validityPeriod;

  @PropertyName("Severity")
  private String severity;

  @PropertyName("ReportType")
  private String reportType;

  @PropertyName("Summary")
  private Text summary;

  @PropertyName("Description")
  private Text description;

  @PropertyName("Advice")
  private Text advice;

  @PropertyName("Affects")
  private Affects affects;

  @PropertyName("InfoLinks")
  private InfoLinks infoLinks;

  public String getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(String creationTime) {
    this.creationTime = creationTime;
  }

  public String getParticipantRef() {
    return participantRef;
  }

  public void setParticipantRef(String participantRef) {
    this.participantRef = participantRef;
  }

  public String getSituationNumber() {
    return situationNumber;
  }

  public void setSituationNumber(String situationNumber) {
    this.situationNumber = situationNumber;
  }

  public Source getSource() {
    return source;
  }

  public void setSource(Source source) {
    this.source = source;
  }

  public String getProgress() {
    return progress;
  }

  public void setProgress(String progress) {
    this.progress = progress;
  }

  public ValidityPeriod getValidityPeriod() {
    return validityPeriod;
  }

  public void setValidityPeriod(ValidityPeriod validityPeriod) {
    this.validityPeriod = validityPeriod;
  }

  public String getSeverity() {
    return severity;
  }

  public void setSeverity(String severity) {
    this.severity = severity;
  }

  public String getReportType() {
    return reportType;
  }

  public void setReportType(String reportType) {
    this.reportType = reportType;
  }

  public Text getSummary() {
    return summary;
  }

  public void setSummary(Text summary) {
    this.summary = summary;
  }

  public Text getDescription() {
    return description;
  }

  public void setDescription(Text description) {
    this.description = description;
  }

  public Text getAdvice() {
    return advice;
  }

  public void setAdvice(Text advice) {
    this.advice = advice;
  }

  public Affects getAffects() {
    return affects;
  }

  public void setAffects(Affects affects) {
    this.affects = affects;
  }

  public InfoLinks getInfoLinks() {
    return infoLinks;
  }

  public void setInfoLinks(InfoLinks infoLinks) {
    this.infoLinks = infoLinks;
  }

  public static class Source {

    @PropertyName("SourceType")
    private String sourceType;

    public String getSourceType() {
      return sourceType;
    }

    public void setSourceType(String sourceType) {
      this.sourceType = sourceType;
    }
  }

  public static class ValidityPeriod {

    @PropertyName("StartTime")
    private String startTime;

    @PropertyName("EndTime")
    private String endTime;

    public String getStartTime() {
      return startTime;
    }

    public void setStartTime(String startTime) {
      this.startTime = startTime;
    }

    public String getEndTime() {
      return endTime;
    }

    public void setEndTime(String endTime) {
      this.endTime = endTime;
    }
  }

  public static class Text {

    @PropertyName("_attributes")
    public Attributes attributes;

    @PropertyName("_text")
    public String text;

    public Attributes getAttributes() {
      return attributes;
    }

    public void setAttributes(Attributes attributes) {
      this.attributes = attributes;
    }

    public String getText() {
      return text;
    }

    public void setText(String text) {
      this.text = text;
    }
  }

  public static class Attributes {

    @PropertyName("xml:lang")
    public String xmlLang;

    public String getXmlLang() {
      return xmlLang;
    }

    public void setXmlLang(String xmlLang) {
      this.xmlLang = xmlLang;
    }
  }

  public static class Affects {

    @PropertyName("Networks")
    private Networks networks;

    @PropertyName("StopPoints")
    private StopPoints stopPoints;

    @PropertyName("VehicleJourneys")
    private VehicleJourneys vehicleJourneys;

    public Networks getNetworks() {
      return networks;
    }

    public void setNetworks(Networks networks) {
      this.networks = networks;
    }

    public StopPoints getStopPoints() {
      return stopPoints;
    }

    public void setStopPoints(StopPoints stopPoints) {
      this.stopPoints = stopPoints;
    }

    public VehicleJourneys getVehicleJourneys() {
      return vehicleJourneys;
    }

    public void setVehicleJourneys(VehicleJourneys vehicleJourneys) {
      this.vehicleJourneys = vehicleJourneys;
    }
  }

  public static class VehicleJourneys {

    @PropertyName("AffectedVehicleJourney")
    private AffectedVehicleJourney affectedVehicleJourney;

    public AffectedVehicleJourney getAffectedVehicleJourney() {
      return affectedVehicleJourney;
    }

    public void setAffectedVehicleJourney(AffectedVehicleJourney affectedVehicleJourney) {
      this.affectedVehicleJourney = affectedVehicleJourney;
    }
  }

  public static class AffectedVehicleJourney {

    @PropertyName("FramedVehicleJourneyRef")
    private FramedVehicleJourneyRef framedVehicleJourneyRef;

    @PropertyName("Route")
    private Route route;

    public FramedVehicleJourneyRef getFramedVehicleJourneyRef() {
      return framedVehicleJourneyRef;
    }

    public void setFramedVehicleJourneyRef(
      FramedVehicleJourneyRef framedVehicleJourneyRef
    ) {
      this.framedVehicleJourneyRef = framedVehicleJourneyRef;
    }

    public Route getRoute() {
      return route;
    }

    public void setRoute(Route route) {
      this.route = route;
    }
  }

  public static class Networks {

    @PropertyName("AffectedNetwork")
    private AffectedNetwork affectedNetwork;

    public AffectedNetwork getAffectedNetwork() {
      return affectedNetwork;
    }

    public void setAffectedNetwork(AffectedNetwork affectedNetwork) {
      this.affectedNetwork = affectedNetwork;
    }
  }

  public static class AffectedNetwork {

    @PropertyName("AffectedLine")
    private AffectedLine affectedLine;

    public AffectedLine getAffectedLine() {
      return affectedLine;
    }

    public void setAffectedLine(AffectedLine affectedLine) {
      this.affectedLine = affectedLine;
    }
  }

  public static class AffectedLine {

    @PropertyName("LineRef")
    private String lineRef;

    @PropertyName("Routes")
    private Routes routes;

    public String getLineRef() {
      return lineRef;
    }

    public void setLineRef(String lineRef) {
      this.lineRef = lineRef;
    }

    public Routes getRoutes() {
      return routes;
    }

    public void setRoutes(Routes routes) {
      this.routes = routes;
    }
  }

  public static class Routes {

    @PropertyName("AffectedRoute")
    private Route affectedRoute;

    public Route getAffectedRoute() {
      return affectedRoute;
    }

    public void setAffectedRoute(Route affectedRoute) {
      this.affectedRoute = affectedRoute;
    }
  }

  public static class Route {

    @PropertyName("StopPoints")
    private StopPoints stopPoints;

    public StopPoints getStopPoints() {
      return stopPoints;
    }

    public void setStopPoints(StopPoints stopPoints) {
      this.stopPoints = stopPoints;
    }
  }

  public static class StopPoints {

    @PropertyName("AffectedStopPoint")
    private List<AffectedStopPoint> affectedStopPoint;

    public List<AffectedStopPoint> getAffectedStopPoint() {
      return affectedStopPoint;
    }

    public void setAffectedStopPoint(List<AffectedStopPoint> affectedStopPoint) {
      this.affectedStopPoint = affectedStopPoint;
    }
  }

  public static class AffectedStopPoint {

    @PropertyName("StopPointRef")
    private String stopPointRef;

    public String getStopPointRef() {
      return stopPointRef;
    }

    public void setStopPointRef(String stopPointRef) {
      this.stopPointRef = stopPointRef;
    }
  }

  public static class InfoLinks {

    @PropertyName("InfoLink")
    private InfoLink infoLink;

    public InfoLink getInfoLink() {
      return infoLink;
    }

    public void setInfoLink(InfoLink infoLink) {
      this.infoLink = infoLink;
    }
  }

  public static class InfoLink {

    @PropertyName("Uri")
    private String uri;

    @PropertyName("Label")
    private String label;

    public String getUri() {
      return uri;
    }

    public void setUri(String uri) {
      this.uri = uri;
    }

    public String getLabel() {
      return label;
    }

    public void setLabel(String label) {
      this.label = label;
    }
  }
}
