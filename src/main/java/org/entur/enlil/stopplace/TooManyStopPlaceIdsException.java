package org.entur.enlil.stopplace;

/**
 * Thrown when a {@code stopPlaces} query asks for more distinct ids than the
 * configured ceiling. A dedicated type rather than {@code IllegalArgumentException}
 * so the GraphQL layer can map it to a client error without also swallowing
 * unrelated argument failures.
 */
public class TooManyStopPlaceIdsException extends RuntimeException {

  public TooManyStopPlaceIdsException(int requested, int maximum) {
    super("ids contains " + requested + " distinct values, maximum is " + maximum);
  }
}
