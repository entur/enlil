package org.entur.enlil.security.spi;

import java.util.List;

public interface UserContextService {
  boolean hasAccessToCodespace(String codespace);
  List<String> getAllowedCodespaces();
}
