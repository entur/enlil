package org.entur.enlil;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.cloud.firestore.Firestore;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import org.entur.enlil.siri.repository.firestore.entity.PtSituationElementEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
  classes = EnlilApplication.class
)
@Testcontainers
@TestPropertySource("classpath:application-test.properties")
class EnlilApplicationIntegrationTests {

  @LocalServerPort
  int randomPort;

  @Autowired
  Firestore firestore;

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

  @BeforeEach
  void setup() {
    firestore
      .collection("codespaces/TST/authorities/TST:Authority:TST/messages")
      .add(createTestMessage());
  }

  @Test
  void testSituationExchangeRequest() {
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

    ResponseEntity<Siri> response = restTemplate.exchange(requestEntity, Siri.class);

    Assertions.assertEquals(
      "ENT",
      response.getBody().getServiceDelivery().getProducerRef().getValue()
    );
    Assertions.assertEquals(
      1,
      response.getBody().getServiceDelivery().getSituationExchangeDeliveries().size()
    );
  }

  PtSituationElementEntity createTestMessage() {
    var message = new PtSituationElementEntity();
    message.setCreationTime(ISO_OFFSET_DATE.format(OffsetDateTime.now()));
    message.setParticipantRef("TST");
    message.setSituationNumber("TST:SituationNumber:1");
    PtSituationElementEntity.Source source = new PtSituationElementEntity.Source();
    source.setSourceType("directReport");
    message.setSource(source);
    message.setProgress("open");
    PtSituationElementEntity.ValidityPeriod validityPeriod =
      new PtSituationElementEntity.ValidityPeriod();
    validityPeriod.setStartTime(ISO_OFFSET_DATE.format(OffsetDateTime.now()));
    message.setValidityPeriod(validityPeriod);
    return message;
  }
}
