module "init" {
  source      = "github.com/entur/terraform-google-init//modules/init?ref=v0.3.0"
  app_id      = "enlil"
  environment = var.environment
}

resource "google_project_iam_member" "firestore_access" {
  project = var.firestore_project
  role = "roles/datastore.user"
  member = "serviceAccount:${module.init.service_accounts.default.email}"
}
