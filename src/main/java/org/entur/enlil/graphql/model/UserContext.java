package org.entur.enlil.graphql.model;

import java.util.List;

public record UserContext(List<String> allowedCodespaces) {}
