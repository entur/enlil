module "init" {
  source      = "github.com/entur/terraform-google-init//modules/init?ref=v0.3.0"
  app_id      = "enlil"
  environment = var.environment
}

// This controls access to the legacy firestore database, not the one defined below ("firestore_db")
resource "google_project_iam_member" "firestore_access" {
  project = var.firestore_project
  role = "roles/datastore.user"
  member = "serviceAccount:${module.init.service_accounts.default.email}"
}


resource "google_firestore_database" "firestore_db" {
  project                           = var.project
  name                              = "default"
  location_id                       = "europe-west1"
  type                              = "FIRESTORE_NATIVE"
  concurrency_mode                  = "OPTIMISTIC"
  app_engine_integration_mode       = "DISABLED"
  point_in_time_recovery_enablement = "POINT_IN_TIME_RECOVERY_DISABLED"
  delete_protection_state           = "DELETE_PROTECTION_ENABLED"
  deletion_policy                   = "DELETE"
}

resource "google_firestore_index" "firestore_db_index" {
  project    = var.project
  database   = google_firestore_database.firestore_db.name
  collection = "messages"
  query_scope = "COLLECTION_GROUP"

  fields {
    field_path = "Progress"
    order      = "ASCENDING"
  }

  fields {
    field_path = "ValidityPeriod.EndTime"
    order      = "ASCENDING"
  }
}

resource "google_firestore_field" "messages_overrides" {
  project    = var.project
  database   = google_firestore_database.firestore_db.name
  collection = "messages"
  field      = "Progress"

  index_config {
    indexes {
      order = "ASCENDING"
      query_scope = "COLLECTION"
    }
    indexes {
      order = "DESCENDING"
      query_scope = "COLLECTION"
    }
    indexes {
      array_config = "CONTAINS"
      query_scope = "COLLECTION"
    }
    indexes {
      order = "ASCENDING"
      query_scope = "COLLECTION_GROUP"
    }
  }
}

resource "google_firestore_field" "cancellations_overrides" {
  project    = var.project
  database   = google_firestore_database.firestore_db.name
  collection = "cancellations"
  field      = "EstimatedVehicleJourney.ExpiresAtEpochMs"

  index_config {
    indexes {
      order = "ASCENDING"
      query_scope = "COLLECTION"
    }
    indexes {
      order = "DESCENDING"
      query_scope = "COLLECTION"
    }
    indexes {
      array_config = "CONTAINS"
      query_scope = "COLLECTION"
    }
    indexes {
      order = "ASCENDING"
      query_scope = "COLLECTION_GROUP"
    }
  }
}

resource "google_firestore_field" "extrajourneys_overrides" {
  project    = var.project
  database   = google_firestore_database.firestore_db.name
  collection = "extrajourneys"
  field      = "EstimatedVehicleJourney.ExpiresAtEpochMs"

  index_config {
    indexes {
      order = "ASCENDING"
      query_scope = "COLLECTION"
    }
    indexes {
      order = "DESCENDING"
      query_scope = "COLLECTION"
    }
    indexes {
      array_config = "CONTAINS"
      query_scope = "COLLECTION"
    }
    indexes {
      order = "ASCENDING"
      query_scope = "COLLECTION_GROUP"
    }
  }
}
