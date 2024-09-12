package org.entur.enlil.siri.repository;

import java.util.stream.Stream;
import org.entur.enlil.siri.repository.firestore.entity.PtSituationElementEntity;
import uk.org.siri.siri21.PtSituationElement;

public interface SituationElementRepository {
  Stream<PtSituationElement> getAllSituationElements();
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
