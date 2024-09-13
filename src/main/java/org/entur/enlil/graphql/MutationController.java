package org.entur.enlil.graphql;

import org.entur.enlil.model.PtSituationElementEntity;
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

  public MutationController(SituationElementRepository situationElementRepository) {
    this.situationElementRepository = situationElementRepository;
  }

  @MutationMapping
  @PreAuthorize("@userContextService.hasAccessToCodespace(#codespace)")
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
}
