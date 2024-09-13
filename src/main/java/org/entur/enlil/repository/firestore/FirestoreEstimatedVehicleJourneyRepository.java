package org.entur.enlil.repository.firestore;

import com.google.cloud.firestore.FieldPath;
import com.google.cloud.firestore.Filter;
import com.google.cloud.firestore.Firestore;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;
import org.entur.enlil.model.EstimatedVehicleJourneyEntity;
import org.entur.enlil.repository.EstimatedVehicleJourneyRepository;
import org.springframework.stereotype.Repository;

@Repository
public class FirestoreEstimatedVehicleJourneyRepository
  implements EstimatedVehicleJourneyRepository {

  private final Firestore firestore;
  private final Clock clock;

  public FirestoreEstimatedVehicleJourneyRepository(Firestore firestore, Clock clock) {
    this.firestore = firestore;
    this.clock = clock;
  }

  @Override
  public Stream<EstimatedVehicleJourneyEntity> getAllEstimatedVehicleJourneys() {
    return Stream.concat(getCancellations(), getExtraJourneys());
  }

  private Stream<EstimatedVehicleJourneyEntity> getCancellations() {
    try {
      return firestore
        .collectionGroup("cancellations")
        .where(
          Filter.greaterThan(
            FieldPath.of("EstimatedVehicleJourney", "ExpiresAtEpochMs"),
            Instant.now(clock).minus(10, ChronoUnit.MINUTES).toEpochMilli()
          )
        )
        .get()
        .get(5, TimeUnit.SECONDS)
        .toObjects(EstimatedVehicleJourneyEntity.class)
        .stream();
    } catch (InterruptedException | TimeoutException | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

  private Stream<EstimatedVehicleJourneyEntity> getExtraJourneys() {
    try {
      return firestore
        .collectionGroup("extrajourneys")
        .where(
          Filter.greaterThan(
            FieldPath.of("EstimatedVehicleJourney", "ExpiresAtEpochMs"),
            Instant.now(clock).minus(10, ChronoUnit.MINUTES).toEpochMilli()
          )
        )
        .get()
        .get(5, TimeUnit.SECONDS)
        .toObjects(EstimatedVehicleJourneyEntity.class)
        .stream();
    } catch (InterruptedException | TimeoutException | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }
}
