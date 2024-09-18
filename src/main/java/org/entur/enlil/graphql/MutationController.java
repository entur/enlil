package org.entur.enlil.graphql;

import org.entur.enlil.model.EstimatedVehicleJourneyEntity;
import org.entur.enlil.model.PtSituationElementEntity;
import org.entur.enlil.repository.EstimatedVehicleJourneyRepository;
import org.entur.enlil.repository.SituationElementRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

@Controller
@SchemaMapping(typeName = "Mutation")
public class MutationController {

  private final SituationElementRepository situationElementRepository;
  private final EstimatedVehicleJourneyRepository estimatedVehicleJourneyRepository;

  public MutationController(
    SituationElementRepository situationElementRepository,
    EstimatedVehicleJourneyRepository estimatedVehicleJourneyRepository
  ) {
    this.situationElementRepository = situationElementRepository;
    this.estimatedVehicleJourneyRepository = estimatedVehicleJourneyRepository;
  }

  @MutationMapping
  @PreAuthorize("@userContextService.hasAccessToCodespaceForMessages(#codespace)")
  public String createOrUpdateSituationElement(
    @Argument String codespace,
    @Argument String authority,
    @Argument PtSituationElementEntity input
  ) {
    if (input.getId() == null) {
      return situationElementRepository.createSituationElement(
        codespace,
        authority,
        input
      );
    } else {
      return situationElementRepository.updateSituationElement(
        codespace,
        authority,
        input
      );
    }
  }

  @MutationMapping
  @PreAuthorize("@userContextService.hasAccessToCodespaceForCancellations(#codespace)")
  public String createOrUpdateCancellation(
    @Argument String codespace,
    @Argument String authority,
    @Argument EstimatedVehicleJourneyEntity input
  ) {
    if (input.getId() == null) {
      return estimatedVehicleJourneyRepository.createCancellation(
        codespace,
        authority,
        input
      );
    } else {
      return estimatedVehicleJourneyRepository.updateCancellation(
        codespace,
        authority,
        input
      );
    }
  }

  @MutationMapping
  @PreAuthorize("@userContextService.hasAccessToCodespaceForExtrajourneys(#codespace)")
  public String createOrUpdateExtrajourney(
    @Argument String codespace,
    @Argument String authority,
    @Argument EstimatedVehicleJourneyEntity input
  ) {
    if (input.getId() == null) {
      return estimatedVehicleJourneyRepository.createExtraJourney(
        codespace,
        authority,
        input
      );
    } else {
      return estimatedVehicleJourneyRepository.updateExtraJourney(
        codespace,
        authority,
        input
      );
    }
  }
}
