package org.entur.enlil.siri.repository.firestore;

import com.google.cloud.firestore.Filter;
import com.google.cloud.firestore.Firestore;
import java.time.Clock;
import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;
import org.entur.enlil.siri.repository.SituationElementRepository;
import org.entur.enlil.siri.repository.firestore.entity.PtSituationElementEntity;
import org.entur.enlil.siri.repository.firestore.mapper.SituationElementEntityToSiriMapper;
import org.springframework.stereotype.Repository;
import uk.org.siri.siri21.PtSituationElement;

@Repository
public class FirestoreSituationElementRepository implements SituationElementRepository {

  private final Firestore firestore;
  private final Clock clock;

  public FirestoreSituationElementRepository(Firestore firestore, Clock clock) {
    this.firestore = firestore;
    this.clock = clock;
  }

  public Stream<PtSituationElement> getAllSituationElements() {
    return Stream
      .concat(getOpenMessages(), getClosedValidMessages())
      .map(SituationElementEntityToSiriMapper::mapToPtSituationElement);
  }

  private Stream<PtSituationElementEntity> getOpenMessages() {
    try {
      Stream<PtSituationElementEntity> stream = firestore
        .collectionGroup("messages")
        .where(Filter.equalTo("Progress", "open"))
        .get()
        .get(5, TimeUnit.SECONDS)
        .toObjects(PtSituationElementEntity.class)
        .stream();

      return stream;
    } catch (InterruptedException | TimeoutException | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

  private Stream<PtSituationElementEntity> getClosedValidMessages() {
    try {
      return firestore
        .collectionGroup("messages")
        .where(Filter.equalTo("Progress", "closed"))
        .where(Filter.greaterThan("ValidityPeriod.EndTime", Instant.now(clock)))
        .get()
        .get(5, TimeUnit.SECONDS)
        .toObjects(PtSituationElementEntity.class)
        .stream();
    } catch (InterruptedException | TimeoutException | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }
}
