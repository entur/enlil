package org.entur.enlil.security;

import java.util.List;
import org.entur.enlil.security.model.Codespace;
import org.entur.enlil.security.model.Permission;
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
  public boolean hasAccessToCodespaceForMessages(String codespace) {
    return authorizationService.hasAccessToCodespaceForPermission(
      codespace,
      Permission.MESSAGES
    );
  }

  @Override
  public boolean hasAccessToCodespaceForCancellations(String codespace) {
    return authorizationService.hasAccessToCodespaceForPermission(
      codespace,
      Permission.CANCELLATIONS
    );
  }

  @Override
  public boolean hasAccessToCodespaceForExtrajourneys(String codespace) {
    return authorizationService.hasAccessToCodespaceForPermission(
      codespace,
      Permission.EXTRAJOURNEYS
    );
  }

  @Override
  public List<Codespace> getAllowedCodespaces() {
    return authorizationService.getAllowedCodespaces();
  }
}
