package org.entur.enlil.graphql;

import java.util.Collection;
import org.entur.enlil.graphql.model.UserContext;
import org.entur.enlil.model.PtSituationElementEntity;
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
  private final UserContextService userContextService;

  public QueryController(
    SituationElementRepository situationElementRepository,
    UserContextService userContextService
  ) {
    this.situationElementRepository = situationElementRepository;
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
}