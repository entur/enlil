package org.entur.enlil.repository.firestore;

import com.google.cloud.firestore.FieldPath;
import com.google.cloud.firestore.Filter;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;
import org.entur.enlil.model.EstimatedVehicleJourneyEntity;
import org.entur.enlil.model.PtSituationElementEntity;
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

  @Override
  public Stream<EstimatedVehicleJourneyEntity> getCancellationsByCodespace(
    String codespace,
    String authority
  ) {
    try {
      return firestore
        .collection(
          "codespaces/" + codespace + "/authorities/" + authority + "/cancellations"
        )
        .get()
        .get(5, TimeUnit.SECONDS)
        .getDocuments()
        .stream()
        .map(queryDocumentSnapshot -> {
          var id = queryDocumentSnapshot.getId();
          var object = queryDocumentSnapshot.toObject(
            EstimatedVehicleJourneyEntity.class
          );
          object.setId(id);
          return object;
        });
    } catch (InterruptedException | TimeoutException | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Stream<EstimatedVehicleJourneyEntity> getExtrajourneysByCodespace(
    String codespace,
    String authority,
    Boolean showCompletedTrips
  ) {
    try {
      var collectionRef = firestore.collection(
        "codespaces/" + codespace + "/authorities/" + authority + "/extrajourneys"
      );

      Query query = null;

      if (Boolean.FALSE.equals(showCompletedTrips)) {
        query =
          collectionRef.where(
            Filter.greaterThan(
              "EstimatedVehicleJourney.ExpiresAtEpochMs",
              Instant.now(clock).toEpochMilli()
            )
          );
      }

      return (query != null ? query : collectionRef).get()
        .get(5, TimeUnit.SECONDS)
        .getDocuments()
        .stream()
        .map(queryDocumentSnapshot -> {
          var id = queryDocumentSnapshot.getId();
          var object = queryDocumentSnapshot.toObject(
            EstimatedVehicleJourneyEntity.class
          );
          object.setId(id);
          return object;
        });
    } catch (InterruptedException | TimeoutException | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String createCancellation(
    String codespace,
    String authority,
    EstimatedVehicleJourneyEntity input
  ) {
    try {
      return firestore
        .collection(
          "codespaces/" + codespace + "/authorities/" + authority + "/cancellations"
        )
        .add(input)
        .get()
        .getId();
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String updateCancellation(
    String codespace,
    String authority,
    EstimatedVehicleJourneyEntity input
  ) {
    try {
      firestore
        .document(
          "codespaces/" +
          codespace +
          "/authorities/" +
          authority +
          "/cancellations/" +
          input.getId()
        )
        .set(input)
        .get();
      return input.getId();
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String createExtraJourney(
    String codespace,
    String authority,
    EstimatedVehicleJourneyEntity input
  ) {
    try {
      return firestore
        .collection(
          "codespaces/" + codespace + "/authorities/" + authority + "/extrajourneys"
        )
        .add(input)
        .get()
        .getId();
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String updateExtraJourney(
    String codespace,
    String authority,
    EstimatedVehicleJourneyEntity input
  ) {
    try {
      firestore
        .document(
          "codespaces/" +
          codespace +
          "/authorities/" +
          authority +
          "/extrajourneys/" +
          input.getId()
        )
        .set(input)
        .get();
      return input.getId();
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException(e);
    }
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
