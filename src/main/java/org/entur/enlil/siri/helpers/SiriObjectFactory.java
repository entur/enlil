/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.entur.enlil.siri.helpers;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.entur.enlil.configuration.EnlilConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.org.siri.siri21.AffectedLineStructure;
import uk.org.siri.siri21.AffectedRouteStructure;
import uk.org.siri.siri21.AffectedStopPointStructure;
import uk.org.siri.siri21.AffectedVehicleJourneyStructure;
import uk.org.siri.siri21.AffectsScopeStructure;
import uk.org.siri.siri21.DataFrameRefStructure;
import uk.org.siri.siri21.DefaultedTextStructure;
import uk.org.siri.siri21.DirectionRefStructure;
import uk.org.siri.siri21.EstimatedCall;
import uk.org.siri.siri21.EstimatedTimetableDeliveryStructure;
import uk.org.siri.siri21.EstimatedVehicleJourney;
import uk.org.siri.siri21.EstimatedVersionFrameStructure;
import uk.org.siri.siri21.FramedVehicleJourneyRefStructure;
import uk.org.siri.siri21.GroupOfLinesRefStructure;
import uk.org.siri.siri21.HalfOpenTimestampOutputRangeStructure;
import uk.org.siri.siri21.InfoLinkStructure;
import uk.org.siri.siri21.LineRef;
import uk.org.siri.siri21.NaturalLanguageStringStructure;
import uk.org.siri.siri21.OperatorRefStructure;
import uk.org.siri.siri21.PtSituationElement;
import uk.org.siri.siri21.RequestorRef;
import uk.org.siri.siri21.RouteRefStructure;
import uk.org.siri.siri21.ServiceDelivery;
import uk.org.siri.siri21.Siri;
import uk.org.siri.siri21.SituationExchangeDeliveryStructure;
import uk.org.siri.siri21.SituationNumber;
import uk.org.siri.siri21.SituationSourceStructure;
import uk.org.siri.siri21.SituationSourceTypeEnumeration;
import uk.org.siri.siri21.StopPointRefStructure;

@Service
public class SiriObjectFactory {

  public static final String FALLBACK_SIRI_VERSION = "2.1";

  private final EnlilConfiguration configuration;
  private final Clock clock;

  public SiriObjectFactory(EnlilConfiguration enlilConfiguration, Clock clock) {
    this.configuration = enlilConfiguration;
    this.clock = clock;
  }

  public Siri createSXServiceDelivery(Collection<PtSituationElement> elements) {
    Siri siri = createSiriObject(FALLBACK_SIRI_VERSION);
    ServiceDelivery delivery = createServiceDelivery();
    SituationExchangeDeliveryStructure deliveryStructure =
      new SituationExchangeDeliveryStructure();
    SituationExchangeDeliveryStructure.Situations situations =
      new SituationExchangeDeliveryStructure.Situations();
    situations.getPtSituationElements().addAll(elements);
    deliveryStructure.setSituations(situations);
    deliveryStructure.setResponseTimestamp(ZonedDateTime.now(clock));
    delivery.getSituationExchangeDeliveries().add(deliveryStructure);
    siri.setServiceDelivery(delivery);
    return siri;
  }

  public Siri createETServiceDelivery(Collection<EstimatedVehicleJourney> elements) {
    Siri siri = createSiriObject(FALLBACK_SIRI_VERSION);
    ServiceDelivery delivery = createServiceDelivery();
    EstimatedTimetableDeliveryStructure deliveryStructure =
      new EstimatedTimetableDeliveryStructure();
    deliveryStructure.setVersion(FALLBACK_SIRI_VERSION);
    EstimatedVersionFrameStructure estimatedVersionFrameStructure =
      new EstimatedVersionFrameStructure();
    estimatedVersionFrameStructure.setRecordedAtTime(ZonedDateTime.now(clock));
    estimatedVersionFrameStructure.getEstimatedVehicleJourneies().addAll(elements);
    deliveryStructure
      .getEstimatedJourneyVersionFrames()
      .add(estimatedVersionFrameStructure);
    deliveryStructure.setResponseTimestamp(ZonedDateTime.now(clock));

    delivery.getEstimatedTimetableDeliveries().add(deliveryStructure);
    siri.setServiceDelivery(delivery);
    return siri;
  }

  private ServiceDelivery createServiceDelivery() {
    ServiceDelivery delivery = new ServiceDelivery();
    delivery.setResponseTimestamp(ZonedDateTime.now(clock));
    if (configuration != null && configuration.getProducerRef() != null) {
      delivery.setProducerRef(createRequestorRef(configuration.getProducerRef()));
    }
    return delivery;
  }

  private static Siri createSiriObject(@Nonnull String version) {
    Siri siri = new Siri();
    siri.setVersion(version);
    return siri;
  }

  public static RequestorRef createRequestorRef(String value) {
    if (value == null) {
      value = UUID.randomUUID().toString();
    }
    RequestorRef requestorRef = new RequestorRef();
    requestorRef.setValue(value);
    return requestorRef;
  }

  public static SituationNumber createSituationNumber(String value) {
    SituationNumber situationNumber = new SituationNumber();
    situationNumber.setValue(value);
    return situationNumber;
  }

  public static SituationSourceStructure createSituationSourceStructure(
    String situationSourceType
  ) {
    var type = new SituationSourceStructure();
    type.setSourceType(SituationSourceTypeEnumeration.fromValue(situationSourceType));
    return type;
  }

  public static HalfOpenTimestampOutputRangeStructure createValidityPeriod(
    @Nonnull String startTime,
    String endTime
  ) {
    var validityPeriod = new HalfOpenTimestampOutputRangeStructure();
    validityPeriod.setStartTime(DateMapper.mapISOStringToZonedDateTime(startTime));

    if (endTime != null) {
      validityPeriod.setEndTime(DateMapper.mapISOStringToZonedDateTime(endTime));
    }

    return validityPeriod;
  }

  public static DefaultedTextStructure createDefaultedTextStructure(
    String lang,
    String value
  ) {
    DefaultedTextStructure defaultedTextStructure = new DefaultedTextStructure();
    defaultedTextStructure.setLang(lang);
    defaultedTextStructure.setValue(value);
    return defaultedTextStructure;
  }

  public static PtSituationElement.InfoLinks createInfoLinks(
    InfoLinkStructure infoLinkStructure
  ) {
    PtSituationElement.InfoLinks infoLinks = new PtSituationElement.InfoLinks();
    infoLinks.getInfoLinks().add(infoLinkStructure);
    return infoLinks;
  }

  public static InfoLinkStructure createInfoLinkStructure(
    @Nonnull String uri,
    String label
  ) {
    InfoLinkStructure infoLinkStructure = new InfoLinkStructure();
    infoLinkStructure.setUri(uri);
    if (label != null) {
      NaturalLanguageStringStructure naturalLanguageStringStructure =
        createNaturalLanguageStringStructure(label);
      infoLinkStructure.getLabels().add(naturalLanguageStringStructure);
    }
    return infoLinkStructure;
  }

  public static NaturalLanguageStringStructure createNaturalLanguageStringStructure(
    String text
  ) {
    NaturalLanguageStringStructure naturalLanguageStringStructure =
      new NaturalLanguageStringStructure();
    naturalLanguageStringStructure.setValue(text);
    return naturalLanguageStringStructure;
  }

  public static AffectsScopeStructure createAffectsScopeStructure(
    AffectsScopeStructure.Networks.AffectedNetwork affectedNetwork,
    List<AffectedStopPointStructure> affectedStopPointStructures,
    AffectedVehicleJourneyStructure affectedVehicleJourneyStructure
  ) {
    AffectsScopeStructure affectsScopeStructure = new AffectsScopeStructure();

    if (affectedNetwork != null) {
      AffectsScopeStructure.Networks networks = new AffectsScopeStructure.Networks();
      networks.getAffectedNetworks().add(affectedNetwork);
      affectsScopeStructure.setNetworks(networks);
    }

    if (affectedStopPointStructures != null) {
      AffectsScopeStructure.StopPoints stopPoints =
        new AffectsScopeStructure.StopPoints();
      stopPoints.getAffectedStopPoints().addAll(affectedStopPointStructures);
      affectsScopeStructure.setStopPoints(stopPoints);
    }

    if (affectedVehicleJourneyStructure != null) {
      AffectsScopeStructure.VehicleJourneys vehicleJourneys =
        new AffectsScopeStructure.VehicleJourneys();
      vehicleJourneys.getAffectedVehicleJourneies().add(affectedVehicleJourneyStructure);
      affectsScopeStructure.setVehicleJourneys(vehicleJourneys);
    }

    return affectsScopeStructure;
  }

  public static AffectedVehicleJourneyStructure createAffectedVehicleJourneyStructure(
    String dataFrameRef,
    String datedVehicleJourneyRef,
    List<AffectedStopPointStructure> affectedStopPointStructures
  ) {
    AffectedVehicleJourneyStructure affectedVehicleJourneyStructure =
      new AffectedVehicleJourneyStructure();
    FramedVehicleJourneyRefStructure framedVehicleJourneyRefStructure =
      createFramedVehicleJourneyRefStructure(datedVehicleJourneyRef, dataFrameRef);
    affectedVehicleJourneyStructure.setFramedVehicleJourneyRef(
      framedVehicleJourneyRefStructure
    );

    if (affectedStopPointStructures != null) {
      AffectedRouteStructure affectedRouteStructure = new AffectedRouteStructure();
      AffectedRouteStructure.StopPoints stopPoints =
        new AffectedRouteStructure.StopPoints();
      stopPoints
        .getAffectedStopPointsAndLinkProjectionToNextStopPoints()
        .addAll(affectedStopPointStructures);
      affectedRouteStructure.setStopPoints(stopPoints);
      affectedVehicleJourneyStructure.getRoutes().add(affectedRouteStructure);
    }
    return affectedVehicleJourneyStructure;
  }

  public static FramedVehicleJourneyRefStructure createFramedVehicleJourneyRefStructure(
    String datedVehicleJourneyRef,
    String dataFrameRef
  ) {
    FramedVehicleJourneyRefStructure framedVehicleJourneyRefStructure =
      new FramedVehicleJourneyRefStructure();
    DataFrameRefStructure dataFrameRefStructure = new DataFrameRefStructure();
    dataFrameRefStructure.setValue(dataFrameRef);
    framedVehicleJourneyRefStructure.setDataFrameRef(dataFrameRefStructure);
    framedVehicleJourneyRefStructure.setDatedVehicleJourneyRef(datedVehicleJourneyRef);
    return framedVehicleJourneyRefStructure;
  }

  public static AffectsScopeStructure.Networks.AffectedNetwork createAffectedNetwork(
    @Nonnull String lineRefString,
    List<AffectedStopPointStructure> stopPointRefs
  ) {
    AffectsScopeStructure.Networks.AffectedNetwork affectedNetwork =
      new AffectsScopeStructure.Networks.AffectedNetwork();
    AffectedLineStructure affectedLineStructure = new AffectedLineStructure();
    LineRef lineRef = new LineRef();
    lineRef.setValue(lineRefString);
    affectedLineStructure.setLineRef(lineRef);

    if (stopPointRefs != null) {
      AffectedLineStructure.StopPoints stopPoints =
        new AffectedLineStructure.StopPoints();
      stopPoints.getAffectedStopPoints().addAll(stopPointRefs);
      affectedLineStructure.setStopPoints(stopPoints);
    }

    affectedNetwork.getAffectedLines().add(affectedLineStructure);

    return affectedNetwork;
  }

  public static AffectedStopPointStructure createAffectedStopPointStructure(
    String stopPointRefString
  ) {
    StopPointRefStructure stopPointRefStructure = createStopPointRefStructure(
      stopPointRefString
    );
    AffectedStopPointStructure affectedStopPointStructure =
      new AffectedStopPointStructure();
    affectedStopPointStructure.setStopPointRef(stopPointRefStructure);
    return affectedStopPointStructure;
  }

  public static LineRef createLineRef(String lineRefString) {
    LineRef lineRef = new LineRef();
    lineRef.setValue(lineRefString);
    return lineRef;
  }

  public static DirectionRefStructure createDirectionRefStructure(String directionRef) {
    DirectionRefStructure directionRefStructure = new DirectionRefStructure();
    directionRefStructure.setValue(directionRef);
    return directionRefStructure;
  }

  public static EstimatedVehicleJourney.EstimatedCalls createEstimatedCalls(
    List<EstimatedCall> estimatedCallList
  ) {
    EstimatedVehicleJourney.EstimatedCalls estimatedCalls =
      new EstimatedVehicleJourney.EstimatedCalls();
    estimatedCalls.getEstimatedCalls().addAll(estimatedCallList);
    return estimatedCalls;
  }

  public static StopPointRefStructure createStopPointRefStructure(String stopPointRef) {
    StopPointRefStructure stopPointRefStructure = new StopPointRefStructure();
    stopPointRefStructure.setValue(stopPointRef);
    return stopPointRefStructure;
  }

  public static RouteRefStructure createRouteRefStructure(String routeRef) {
    RouteRefStructure routeRefStructure = new RouteRefStructure();
    routeRefStructure.setValue(routeRef);
    return routeRefStructure;
  }

  public static GroupOfLinesRefStructure createGroupOfLinesRefStructure(
    String groupOfLinesRef
  ) {
    GroupOfLinesRefStructure groupOfLinesRefStructure = new GroupOfLinesRefStructure();
    groupOfLinesRefStructure.setValue(groupOfLinesRef);
    return groupOfLinesRefStructure;
  }

  public static OperatorRefStructure createOperatorRefStructure(String operatorRef) {
    OperatorRefStructure operatorRefStructure = new OperatorRefStructure();
    operatorRefStructure.setValue(operatorRef);
    return operatorRefStructure;
  }
}
