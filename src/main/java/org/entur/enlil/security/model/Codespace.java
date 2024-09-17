package org.entur.enlil.security.model;

import java.util.List;

public record Codespace(String id, List<Permission> permissions) {}
