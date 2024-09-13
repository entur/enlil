package org.entur.enlil.siri.graphql.model;

import java.util.List;

public record UserContext(List<String> allowedCodespaces) {}
