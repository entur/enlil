package org.entur.enlil.graphql;

import java.util.Collection;
import org.entur.enlil.graphql.model.UserContext;
import org.entur.enlil.model.EstimatedVehicleJourneyEntity;
import org.entur.enlil.model.PtSituationElementEntity;
import org.entur.enlil.repository.EstimatedVehicleJourneyRepository;
import org.entur.enlil.repository.SituationElementRepository;
import org.entur.enlil.security.spi.UserContextService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

@Controller
@SchemaMapping(typeName = "Query")
public class QueryController {

  private final SituationElementRepository situationElementRepository;
  private final EstimatedVehicleJourneyRepository estimatedVehicleJourneyRepository;
  private final UserContextService userContextService;

  public QueryController(
    SituationElementRepository situationElementRepository,
    EstimatedVehicleJourneyRepository estimatedVehicleJourneyRepository,
    UserContextService userContextService
  ) {
    this.situationElementRepository = situationElementRepository;
    this.estimatedVehicleJourneyRepository = estimatedVehicleJourneyRepository;
    this.userContextService = userContextService;
  }

  @QueryMapping
  public UserContext userContext() {
    return new UserContext(userContextService.getAllowedCodespaces());
  }

  @QueryMapping
  @PreAuthorize("@userContextService.hasAccessToCodespace(#codespace)")
  public Collection<PtSituationElementEntity> situationElements(
    @Argument String codespace,
    @Argument String authority
  ) {
    return situationElementRepository
      .getAllSituationElementsByCodespace(codespace, authority)
      .toList();
  }

  @QueryMapping
  @PreAuthorize("@userContextService.hasAccessToCodespace(#codespace)")
  public Collection<EstimatedVehicleJourneyEntity> cancellations(
    @Argument String codespace,
    @Argument String authority
  ) {
    return estimatedVehicleJourneyRepository
      .getCancellationsByCodespace(codespace, authority)
      .toList();
  }

  @QueryMapping
  @PreAuthorize("@userContextService.hasAccessToCodespace(#codespace)")
  public Collection<EstimatedVehicleJourneyEntity> extrajourneys(
    @Argument String codespace,
    @Argument String authority,
    @Argument Boolean showCompletedTrips
  ) {
    return estimatedVehicleJourneyRepository
      .getExtrajourneysByCodespace(codespace, authority, showCompletedTrips)
      .toList();
  }
}
