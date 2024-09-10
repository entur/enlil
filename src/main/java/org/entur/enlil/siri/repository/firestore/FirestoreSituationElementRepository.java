package org.entur.enlil.siri.repository.firestore;

import com.google.cloud.firestore.Filter;
import com.google.cloud.firestore.Firestore;
import java.time.Clock;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;
import org.entur.enlil.siri.helpers.DateMapper;
import org.entur.enlil.siri.repository.SituationElementRepository;
import org.entur.enlil.siri.repository.firestore.entity.PtSituationElementEntity;
import org.entur.enlil.siri.repository.firestore.mapper.SituationElementEntityToSiriMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import uk.org.siri.siri21.PtSituationElement;

@Repository
public class FirestoreSituationElementRepository implements SituationElementRepository {

  private final Logger logger = LoggerFactory.getLogger(
    FirestoreSituationElementRepository.class
  );
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
        .where(
          Filter.greaterThan(
            "ValidityPeriod.EndTime",
            DateMapper.mapZonedDateTimeToString(ZonedDateTime.now(clock))
          )
        )
        .get()
        .get(5, TimeUnit.SECONDS)
        .toObjects(PtSituationElementEntity.class)
        .stream();
    } catch (InterruptedException | TimeoutException | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void closeOpenExpiredMessages() {
    firestore.runTransaction(transaction -> {
      transaction
        .get(
          firestore.collectionGroup("messages").where(Filter.equalTo("Progress", "open"))
        )
        .get(5, TimeUnit.SECONDS)
        .forEach(docRef -> {
          var doc = docRef.toObject(PtSituationElementEntity.class);
          if (
            doc.getValidityPeriod().getEndTime() != null &&
            ZonedDateTime
              .now(clock)
              .isAfter(
                DateMapper.mapISOStringToZonedDateTime(
                  doc.getValidityPeriod().getEndTime()
                )
              )
          ) {
            // extend end time
            String newEndTime = DateMapper.mapZonedDateTimeToString(
              ZonedDateTime.now(clock).plusHours(5)
            );

            logger.info(
              "Clossing message id={} situationNumber={} newEndTime={}",
              docRef.getId(),
              doc.getSituationNumber(),
              newEndTime
            );

            // update document
            transaction.update(
              docRef.getReference(),
              Map.of(
                "Progress",
                "closed",
                "ValidityPeriod",
                Map.of(
                  "StartTime",
                  doc.getValidityPeriod().getStartTime(),
                  "EndTime",
                  newEndTime
                )
              )
            );
          }
        });
      return transaction;
    });
  }
}
