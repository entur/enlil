package org.entur.enlil.stubs;

import java.util.List;
import org.entur.enlil.security.model.Codespace;
import org.entur.enlil.security.spi.UserContextService;

public class UserContextServiceStub implements UserContextService {

  @Override
  public boolean hasAccessToCodespaceForMessages(String codespace) {
    return false;
  }

  @Override
  public boolean hasAccessToCodespaceForCancellations(String codespace) {
    return false;
  }

  @Override
  public boolean hasAccessToCodespaceForExtrajourneys(String codespace) {
    return false;
  }

  @Override
  public List<Codespace> getAllowedCodespaces() {
    return List.of();
  }
}
