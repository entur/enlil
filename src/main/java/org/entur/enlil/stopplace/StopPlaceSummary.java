package org.entur.enlil.stopplace;

/**
 * The projection returned to GraphQL consumers. These three fields are the whole
 * of what the stop picker renders; everything else in the upstream payload is
 * deliberately discarded.
 */
public record StopPlaceSummary(
  String id,
  String transportMode,
  String topographicPlaceName
) {}
