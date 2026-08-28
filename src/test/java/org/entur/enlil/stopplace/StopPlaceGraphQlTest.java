package org.entur.enlil.stopplace;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.when;

import java.util.List;
import org.entur.enlil.graphql.QueryController;
import org.entur.enlil.repository.EstimatedVehicleJourneyRepository;
import org.entur.enlil.repository.SituationElementRepository;
import org.entur.enlil.security.spi.UserContextService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.graphql.test.autoconfigure.GraphQlTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * A GraphQL slice rather than a full {@code @SpringBootTest}: this only needs the
 * schema and the controller, and booting the whole application would drag in
 * Firestore and the security wiring for no benefit.
 */
@GraphQlTest(QueryController.class)
class StopPlaceGraphQlTest {

  @Autowired
  private GraphQlTester graphQlTester;

  @MockitoBean
  private StopPlaceService stopPlaceService;

  @MockitoBean
  private SituationElementRepository situationElementRepository;

  @MockitoBean
  private EstimatedVehicleJourneyRepository estimatedVehicleJourneyRepository;

  @MockitoBean
  private UserContextService userContextService;

  @Test
  void returnsProjectedSummaries() {
    when(stopPlaceService.getStopPlaceSummaries(anyCollection()))
      .thenReturn(List.of(new StopPlaceSummary("NSR:StopPlace:1", "bus", "Oslo")));

    List<StopPlaceSummary> result = graphQlTester
      .document(
        """
        query {
          stopPlaces(ids: ["NSR:StopPlace:1"]) {
            id
            transportMode
            topographicPlaceName
          }
        }
        """
      )
      .execute()
      .path("stopPlaces")
      .entityList(StopPlaceSummary.class)
      .get();

    assertThat(result)
      .containsExactly(new StopPlaceSummary("NSR:StopPlace:1", "bus", "Oslo"));
  }
}
