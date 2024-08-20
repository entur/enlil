package org.entur.enlil.siri.repository;

import java.util.stream.Stream;
import uk.org.siri.siri21.PtSituationElement;

public interface SituationElementRepository {
  Stream<PtSituationElement> getAllSituationElements();
}
