package org.entur.enlil.siri.repository.firestore;

import com.google.cloud.firestore.Filter;
import com.google.cloud.firestore.Firestore;
import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;
import org.entur.enlil.siri.repository.SituationElementRepository;
import org.entur.enlil.siri.repository.firestore.entity.PtSituationElementEntity;
import org.entur.enlil.siri.repository.firestore.mapper.EntityToSiriMapper;
import org.springframework.stereotype.Repository;
import uk.org.siri.siri21.PtSituationElement;

@Repository
public class FirestoreSituationElementRepository implements SituationElementRepository {

  private final Firestore firestore;

  public FirestoreSituationElementRepository(Firestore firestore) {
    this.firestore = firestore;
  }

  public Stream<PtSituationElement> getAllMessages() {
    return Stream
      .concat(getOpenMessages(), getClosedValidMessages())
      .map(EntityToSiriMapper::mapToPtSituationElement);
  }

  private Stream<PtSituationElementEntity> getOpenMessages() {
    try {
      return firestore
        .collectionGroup("messages")
        .where(Filter.equalTo("Progress", "open"))
        .get()
        .get(5, TimeUnit.SECONDS)
        .toObjects(PtSituationElementEntity.class)
        .stream();
    } catch (InterruptedException | TimeoutException | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

  private Stream<PtSituationElementEntity> getClosedValidMessages() {
    try {
      return firestore
        .collectionGroup("messages")
        .where(Filter.equalTo("Progress", "closed"))
        .where(Filter.greaterThan("ValidityPeriod.EndTime", Instant.now()))
        .get()
        .get(5, TimeUnit.SECONDS)
        .toObjects(PtSituationElementEntity.class)
        .stream();
    } catch (InterruptedException | TimeoutException | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }
}
