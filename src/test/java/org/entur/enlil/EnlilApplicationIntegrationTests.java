package org.entur.enlil;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.cloud.firestore.Firestore;
import java.net.URI;
import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.entur.enlil.siri.repository.firestore.entity.EstimatedVehicleJourneyEntity;
import org.entur.enlil.siri.repository.firestore.entity.FramedVehicleJourneyRef;
import org.entur.enlil.siri.repository.firestore.entity.PtSituationElementEntity;
import org.entur.enlil.stubs.MockedClockConfiguration;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.FirestoreEmulatorContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import uk.org.siri.siri21.Siri;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
  classes = { EnlilApplication.class, MockedClockConfiguration.class }
)
@Testcontainers
@TestPropertySource("classpath:application-test.properties")
@ExtendWith({ SnapshotExtension.class, MockitoExtension.class })
@ActiveProfiles({ "test" })
class EnlilApplicationIntegrationTests {

  @LocalServerPort
  int randomPort;

  @Autowired
  Firestore firestore;

  @Autowired
  Clock clock;

  private Expect expect;

  @Container
  private static final FirestoreEmulatorContainer firestoreEmulator =
    new FirestoreEmulatorContainer(
      DockerImageName.parse("gcr.io/google.com/cloudsdktool/cloud-sdk:317.0.0-emulators")
    );

  @DynamicPropertySource
  static void emulatorProperties(DynamicPropertyRegistry registry) {
    registry.add(
      "spring.cloud.gcp.firestore.host-port",
      firestoreEmulator::getEmulatorEndpoint
    );
  }

  @TestConfiguration
  static class EmulatorConfiguration {

    // By default, autoconfiguration will initialize application default credentials.
    // For testing purposes, don't use any credentials. Bootstrap w/ NoCredentialsProvider.
    @Bean
    CredentialsProvider googleCredentials() {
      return NoCredentialsProvider.create();
    }
  }

  @Test
  void testSituationExchangeRequest() throws ExecutionException, InterruptedException {
    firestore
      .collection("codespaces/TST/authorities/TST:Authority:TST/messages")
      .add(createTestMessage())
      .get();

    TestRestTemplate restTemplate = new TestRestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(List.of(MediaType.APPLICATION_XML));
    headers.setContentType(MediaType.APPLICATION_XML);
    RequestEntity<String> requestEntity = new RequestEntity<>(
      """
                      <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                      <Siri version="2.0" xmlns="http://www.siri.org.uk/siri" xmlns:ns2="http://www.ifopt.org.uk/acsb" xmlns:ns3="http://www.ifopt.org.uk/ifopt" xmlns:ns4="http://datex2.eu/schema/2_0RC1/2_0">
                        <ServiceRequest>
                          <RequestTimestamp>2019-11-06T14:45:00+01:00</RequestTimestamp>
                          <RequestorRef>ENTUR_DEV-1</RequestorRef>
                          <SituationExchangeRequest version="2.0">
                            <RequestTimestamp>2019-11-06T14:45:00+01:00</RequestTimestamp>
                            <MessageIdentifier>e11d9efb-ee7b-4a67-847a-a254e813f0da</MessageIdentifier>
                          </SituationExchangeRequest>
                        </ServiceRequest>
                      </Siri>
                      """,
      headers,
      HttpMethod.POST,
      URI.create("http://localhost:" + randomPort + "/siri"),
      Siri.class
    );

    ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);

    expect.toMatchSnapshot(response.getBody());
  }

  PtSituationElementEntity createTestMessage() {
    var message = new PtSituationElementEntity();
    message.setCreationTime("2023-09-19T14:45:00+01:00");
    message.setParticipantRef("TST");
    message.setSituationNumber("TST:SituationNumber:1");
    PtSituationElementEntity.Source source = new PtSituationElementEntity.Source();
    source.setSourceType("directReport");
    message.setSource(source);
    message.setProgress("open");
    message.setSeverity("normal");
    message.setReportType("general");
    PtSituationElementEntity.Text summary = new PtSituationElementEntity.Text();
    summary.setText("we have a situation here");
    PtSituationElementEntity.Attributes attributes =
      new PtSituationElementEntity.Attributes();
    attributes.setXmlLang("no");
    summary.setAttributes(attributes);
    message.setSummary(summary);
    PtSituationElementEntity.Affects affects = createAffects();
    message.setAffects(affects);
    PtSituationElementEntity.ValidityPeriod validityPeriod =
      new PtSituationElementEntity.ValidityPeriod();
    validityPeriod.setStartTime("2023-09-19T14:45:00+01:00");
    message.setValidityPeriod(validityPeriod);
    return message;
  }

  private static PtSituationElementEntity.@NotNull Affects createAffects() {
    PtSituationElementEntity.Affects affects = new PtSituationElementEntity.Affects();
    PtSituationElementEntity.Networks networks = new PtSituationElementEntity.Networks();
    PtSituationElementEntity.AffectedNetwork affectedNetwork =
      new PtSituationElementEntity.AffectedNetwork();
    PtSituationElementEntity.AffectedLine affectedLine =
      new PtSituationElementEntity.AffectedLine();
    affectedLine.setLineRef("TST:Line:1");
    affectedNetwork.setAffectedLine(affectedLine);
    networks.setAffectedNetwork(affectedNetwork);
    affects.setNetworks(networks);
    return affects;
  }

  @Test
  void testEstimatedTimetableRequestWithCancellation()
    throws ExecutionException, InterruptedException {
    firestore
      .collection("codespaces/TST/authorities/TST:Authority:TST/cancellations")
      .add(createTestCancellation())
      .get();

    TestRestTemplate restTemplate = new TestRestTemplate();

    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(List.of(MediaType.APPLICATION_XML));
    headers.setContentType(MediaType.APPLICATION_XML);
    RequestEntity<String> requestEntity = new RequestEntity<>(
      """
                    <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                    <Siri version="2.0" xmlns="http://www.siri.org.uk/siri" xmlns:ns2="http://www.ifopt.org.uk/acsb" xmlns:ns3="http://www.ifopt.org.uk/ifopt" xmlns:ns4="http://datex2.eu/schema/2_0RC1/2_0">
                      <ServiceRequest>
                        <RequestTimestamp>2019-11-06T14:45:00+01:00</RequestTimestamp>
                        <RequestorRef>ENTUR_DEV-1</RequestorRef>
                        <EstimatedTimetableRequest version="2.0">
                          <RequestTimestamp>2019-11-06T14:45:00+01:00</RequestTimestamp>
                          <MessageIdentifier>e11d9efb-ee7b-4a67-847a-a254e813f0da</MessageIdentifier>
                        </EstimatedTimetableRequest>
                      </ServiceRequest>
                    </Siri>
                    """,
      headers,
      HttpMethod.POST,
      URI.create("http://localhost:" + randomPort + "/siri"),
      Siri.class
    );

    ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);

    expect.toMatchSnapshot(response.getBody());
  }

  private EstimatedVehicleJourneyEntity createTestCancellation() {
    var cancellation = new EstimatedVehicleJourneyEntity();
    var estimatedVehicleJourney =
      new EstimatedVehicleJourneyEntity.EstimatedVehicleJourney();
    estimatedVehicleJourney.setRecordedAtTime(
      ISO_DATE_TIME.format(OffsetDateTime.now(clock))
    );
    estimatedVehicleJourney.setLineRef("TST:Line:1");
    estimatedVehicleJourney.setDirectionRef("0");
    FramedVehicleJourneyRef framedVehicleJourneyRef = new FramedVehicleJourneyRef();
    framedVehicleJourneyRef.setDataFrameRef("TST:ServiceJourney:1");
    framedVehicleJourneyRef.setDatedVehicleJourneyRef("2024-01-01");
    estimatedVehicleJourney.setFramedVehicleJourneyRef(framedVehicleJourneyRef);
    estimatedVehicleJourney.setCancellation(true);
    estimatedVehicleJourney.setDataSource("TST");
    estimatedVehicleJourney.setEstimatedCalls(createTestEstimatedCalls());
    estimatedVehicleJourney.setIsCompleteStopSequence(true);
    estimatedVehicleJourney.setExpiresAtEpochMs(
      Instant.now(clock).plus(20, ChronoUnit.MINUTES).toEpochMilli()
    );

    cancellation.setEstimatedVehicleJourney(estimatedVehicleJourney);

    return cancellation;
  }

  private EstimatedVehicleJourneyEntity.@NotNull EstimatedCalls createTestEstimatedCalls() {
    EstimatedVehicleJourneyEntity.EstimatedCalls estimatedCalls =
      new EstimatedVehicleJourneyEntity.EstimatedCalls();
    EstimatedVehicleJourneyEntity.EstimatedCall call1 =
      new EstimatedVehicleJourneyEntity.EstimatedCall();
    call1.setStopPointRef("TST:Quay:1");
    call1.setOrder(1);
    call1.setStopPointName("Fjord");
    call1.setCancellation(true);
    call1.setRequestStop(false);
    call1.setArrivalBoardingActivity("noAlighting");
    call1.setDepartureStatus("cancelled");
    call1.setDepartureBoardingActivity("boarding");
    EstimatedVehicleJourneyEntity.EstimatedCall call2 =
      new EstimatedVehicleJourneyEntity.EstimatedCall();
    call2.setStopPointRef("TST:Quay:2");
    call2.setOrder(2);
    call2.setStopPointName("Fjell");
    call2.setCancellation(true);
    call2.setRequestStop(false);
    call2.setArrivalBoardingActivity("noAlighting");
    call2.setDepartureStatus("cancelled");
    call2.setDepartureBoardingActivity("boarding");
    estimatedCalls.setEstimatedCall(List.of(call1, call2));
    return estimatedCalls;
  }

  @Test
  void testEstimatedTimetableRequestWithExtraJourney()
    throws ExecutionException, InterruptedException {
    firestore
      .collection("codespaces/TST/authorities/TST:Authority:TST/extrajourneys")
      .add(createTestExtraJourney())
      .get();

    TestRestTemplate restTemplate = new TestRestTemplate();

    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(List.of(MediaType.APPLICATION_XML));
    headers.setContentType(MediaType.APPLICATION_XML);
    RequestEntity<String> requestEntity = new RequestEntity<>(
      """
                          <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                          <Siri version="2.0" xmlns="http://www.siri.org.uk/siri" xmlns:ns2="http://www.ifopt.org.uk/acsb" xmlns:ns3="http://www.ifopt.org.uk/ifopt" xmlns:ns4="http://datex2.eu/schema/2_0RC1/2_0">
                            <ServiceRequest>
                              <RequestTimestamp>2019-11-06T14:45:00+01:00</RequestTimestamp>
                              <RequestorRef>ENTUR_DEV-1</RequestorRef>
                              <EstimatedTimetableRequest version="2.0">
                                <RequestTimestamp>2019-11-06T14:45:00+01:00</RequestTimestamp>
                                <MessageIdentifier>e11d9efb-ee7b-4a67-847a-a254e813f0da</MessageIdentifier>
                              </EstimatedTimetableRequest>
                            </ServiceRequest>
                          </Siri>
                          """,
      headers,
      HttpMethod.POST,
      URI.create("http://localhost:" + randomPort + "/siri"),
      Siri.class
    );

    ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);

    expect.toMatchSnapshot(response.getBody());
  }

  private EstimatedVehicleJourneyEntity createTestExtraJourney() {
    var extraJourney = new EstimatedVehicleJourneyEntity();
    var estimatedVehicleJourney =
      new EstimatedVehicleJourneyEntity.EstimatedVehicleJourney();
    estimatedVehicleJourney.setRecordedAtTime(
      ISO_DATE_TIME.format(OffsetDateTime.now(clock))
    );
    estimatedVehicleJourney.setLineRef("TST:Line:1");
    estimatedVehicleJourney.setDirectionRef("0");
    estimatedVehicleJourney.setDataSource("TST");
    estimatedVehicleJourney.setEstimatedCalls(createExtraJourneyTestEstimatedCalls());
    estimatedVehicleJourney.setIsCompleteStopSequence(true);
    estimatedVehicleJourney.setExpiresAtEpochMs(
      Instant.now(clock).plus(20, ChronoUnit.MINUTES).toEpochMilli()
    );

    estimatedVehicleJourney.setExternalLineRef("TST:Line:1");
    estimatedVehicleJourney.setGroupOfLinesRef("TST:Network:1");
    estimatedVehicleJourney.setOperatorRef("TST:Operator:1");
    estimatedVehicleJourney.setPublishedLineName("Test");
    estimatedVehicleJourney.setRouteRef("TST:Route:1");
    estimatedVehicleJourney.setVehicleMode("bus");

    extraJourney.setEstimatedVehicleJourney(estimatedVehicleJourney);
    return extraJourney;
  }

  private EstimatedVehicleJourneyEntity.EstimatedCalls createExtraJourneyTestEstimatedCalls() {
    EstimatedVehicleJourneyEntity.EstimatedCalls estimatedCalls =
      new EstimatedVehicleJourneyEntity.EstimatedCalls();
    EstimatedVehicleJourneyEntity.EstimatedCall call1 =
      new EstimatedVehicleJourneyEntity.EstimatedCall();
    call1.setStopPointRef("TST:Quay:1");
    call1.setOrder(1);
    call1.setStopPointName("Fjord");
    call1.setDepartureBoardingActivity("boarding");
    EstimatedVehicleJourneyEntity.EstimatedCall call2 =
      new EstimatedVehicleJourneyEntity.EstimatedCall();
    call2.setStopPointRef("TST:Quay:2");
    call2.setOrder(2);
    call2.setStopPointName("Fjell");
    call2.setArrivalBoardingActivity("alighting");
    estimatedCalls.setEstimatedCall(List.of(call1, call2));
    return estimatedCalls;
  }
}
