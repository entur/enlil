package org.entur.enlil.siri.graphql;

import java.util.Collection;
import org.entur.enlil.siri.repository.SituationElementRepository;
import org.entur.enlil.siri.repository.firestore.entity.PtSituationElementEntity;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

@Controller
@SchemaMapping(typeName = "Query")
public class QueryController {

  private final SituationElementRepository situationElementRepository;

  public QueryController(SituationElementRepository situationElementRepository) {
    this.situationElementRepository = situationElementRepository;
  }

  @QueryMapping
  public Collection<PtSituationElementEntity> situationElements(
    @Argument String codespace,
    @Argument String authority
  ) {
    return situationElementRepository
      .getAllSituationElementsByCodespace(codespace, authority)
      .toList();
  }
}
