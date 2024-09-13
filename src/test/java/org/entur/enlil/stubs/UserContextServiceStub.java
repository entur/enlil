package org.entur.enlil.stubs;

import java.util.List;
import org.entur.enlil.security.spi.UserContextService;

public class UserContextServiceStub implements UserContextService {

  @Override
  public boolean hasAccessToCodespace(String codespace) {
    return false;
  }

  @Override
  public List<String> getAllowedCodespaces() {
    return List.of();
  }
}
