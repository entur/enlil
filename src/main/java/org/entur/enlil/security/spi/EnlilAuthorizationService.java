package org.entur.enlil.security.spi;

import java.util.List;
import org.rutebanken.helper.organisation.authorization.AuthorizationService;

public interface EnlilAuthorizationService<T> extends AuthorizationService<T> {
  List<String> getAllowedCodespacesForEditSX();
  boolean canEditSX(T providerId);
}
