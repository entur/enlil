package org.entur.enlil.security;

import java.util.List;
import org.entur.enlil.security.spi.UserContextService;

// TODO this needs to change
public class DefaltUserContextService implements UserContextService {

  @Override
  public boolean hasAccessToCodespace(String codespace) {
    return false;
  }

  @Override
  public List<String> getAllowedCodespaces() {
    return List.of();
  }
}
