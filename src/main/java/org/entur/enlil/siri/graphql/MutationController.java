package org.entur.enlil.siri.graphql;

import org.entur.enlil.siri.repository.SituationElementRepository;
import org.entur.enlil.siri.repository.firestore.entity.PtSituationElementEntity;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

@Controller
@SchemaMapping(typeName = "Mutation")
public class MutationController {

  private final SituationElementRepository situationElementRepository;

  public MutationController(SituationElementRepository situationElementRepository) {
    this.situationElementRepository = situationElementRepository;
  }

  @MutationMapping
  public PtSituationElementEntity createSituationElement(
    @Argument PtSituationElementEntity input
  ) {
    return input;
  }

  @MutationMapping
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
