package org.entur.enlil.repository;

import java.util.stream.Stream;
import org.entur.enlil.model.PtSituationElementEntity;
import uk.org.siri.siri21.PtSituationElement;

public interface SituationElementRepository {
  Stream<PtSituationElementEntity> getAllSituationElements();
  void closeOpenExpiredMessages();
  Stream<PtSituationElementEntity> getAllSituationElementsByCodespace(
    String codespace,
    String authority
  );
  String createSituationElement(
    String codespace,
    String authority,
    PtSituationElementEntity situationElement
  );
  String updateSituationElement(
    String codespace,
    String authority,
    PtSituationElementEntity situationElement
  );
}
