package org.entur.enlil.security.model;

import java.util.List;

public record UserContext(List<Codespace> allowedCodespaces, boolean isAdmin) {}
