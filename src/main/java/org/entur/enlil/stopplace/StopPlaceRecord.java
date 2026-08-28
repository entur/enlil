package org.entur.enlil.stopplace;

/** Internal projection of an upstream stop place. */
public record StopPlaceRecord(
  String id,
  String transportMode,
  String topographicPlaceRef
) {}
