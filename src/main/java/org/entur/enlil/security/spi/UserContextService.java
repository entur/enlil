package org.entur.enlil.security.spi;

import java.util.List;
import org.entur.enlil.security.model.Codespace;
import org.entur.enlil.security.model.Permission;

public interface UserContextService {
  boolean hasAccessToCodespaceForMessages(String codespace);
  boolean hasAccessToCodespaceForCancellations(String codespace);
  boolean hasAccessToCodespaceForExtrajourneys(String codespace);
  List<Codespace> getAllowedCodespaces();
}
