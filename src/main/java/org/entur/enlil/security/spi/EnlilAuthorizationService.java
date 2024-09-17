package org.entur.enlil.security.spi;

import java.util.List;
import org.entur.enlil.security.model.Codespace;
import org.entur.enlil.security.model.Permission;
import org.rutebanken.helper.organisation.authorization.AuthorizationService;

public interface EnlilAuthorizationService<T> extends AuthorizationService<T> {
  List<Codespace> getAllowedCodespaces();
  boolean hasAccessToCodespaceForPermission(T providerId, Permission permission);
}
