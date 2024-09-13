package org.entur.enlil.security;

import java.util.List;
import org.entur.enlil.security.spi.EnlilAuthorizationService;
import org.entur.enlil.security.spi.UserContextService;
import org.rutebanken.helper.organisation.RoleAssignmentExtractor;

public class EnturUserContextService implements UserContextService {

  private final EnlilAuthorizationService<String> authorizationService;

  public EnturUserContextService(RoleAssignmentExtractor roleAssignmentExtractor) {
    authorizationService =
      new DefaultEnlilAuthorizationService<>(t -> t, roleAssignmentExtractor);
  }

  @Override
  public boolean hasAccessToCodespace(String codespace) {
    return authorizationService.canEditSX(codespace);
  }

  @Override
  public List<String> getAllowedCodespaces() {
    return authorizationService.getAllowedCodespacesForEditSX();
  }
}
