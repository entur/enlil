package org.entur.enlil.siri.repository;

import uk.org.siri.siri21.PtSituationElement;

import java.util.stream.Stream;

public interface SituationElementRepository {
    Stream<PtSituationElement> getAllMessages();
}
