# Contains main description of bulk of terraform?
terraform {
  required_version = ">= 0.12"
}

provider "google" {
  version = "~> 2.19"
}
provider "kubernetes" {
  load_config_file = var.load_config_file
}

# Create bucket
resource "google_storage_bucket" "storage_bucket" {
  name               = "${var.bucket_instance_prefix}${var.labels.app}-${var.bucket_instance_suffix}"
  force_destroy      = var.force_destroy
  location           = var.location
  project            = var.storage_project
  storage_class      = var.storage_class
  bucket_policy_only = var.bucket_policy_only
  labels             = var.labels

  versioning {
    enabled = var.versioning
  }
  logging {
    log_bucket        = var.log_bucket
    log_object_prefix = "${var.labels.app}-${var.bucket_instance_suffix}"
  }
}
# Create folder in a bucket
resource "google_storage_bucket_object" "content_folder" {
  name          = "export/"
  content       = "Not really a directory, but it's empty."
  bucket        = google_storage_bucket.storage_bucket.name
}
# Create pubsub topic
resource "google_pubsub_topic" "changelog" {
  name   = "${var.labels.team}.${var.labels.app}.changelog"
  project = var.pubsub_project
  labels = var.labels
}

# Create pubsub subscription
resource "google_pubsub_subscription" "changelog-subscription" {
  name  = "${var.labels.team}.${var.labels.app}.changelog"
  topic = google_pubsub_topic.changelog.name
  project = var.pubsub_project
  labels = var.labels
  }


# create service account
resource "google_service_account" "tiamat_service_account" {
  account_id   = "${var.labels.team}-${var.labels.app}-sa"
  display_name = "${var.labels.team}-${var.labels.app} service account"
  project = var.gcp_project
}

# add service account as member to the bucket
resource "google_storage_bucket_iam_member" "storage_bucket_iam_member" {
  bucket = google_storage_bucket.storage_bucket.name
  role   = var.service_account_bucket_role
  member = "serviceAccount:${google_service_account.tiamat_service_account.email}"
}

# add service account as member to the cloudsql client
resource "google_project_iam_member" "project" {
  project = var.cloudsql_project
  role    = var.service_account_cloudsql_role
  member = "serviceAccount:${google_service_account.tiamat_service_account.email}"
}

# add service account as member to the pubsub
resource "google_project_iam_member" "pubsub_member" {
  project = var.pubsub_project
  role    = var.service_account_pubsub_role
  member = "serviceAccount:${google_service_account.tiamat_service_account.email}"
}

# create key for service account
resource "google_service_account_key" "tiamat_service_account_key" {
  service_account_id = google_service_account.tiamat_service_account.name
}

  # Add SA key to to k8s
resource "kubernetes_secret" "tiamat_service_account_credentials" {
  metadata {
    name      = "${var.labels.team}-${var.labels.app}-sa-key"
    namespace = var.kube_namespace
  }
  data = {
    "credentials.json" = "${base64decode(google_service_account_key.tiamat_service_account_key.private_key)}"
  }
}
#
resource "kubernetes_secret" "ror-tiamat-db-password" {
  metadata {
  name      = "${var.labels.team}-${var.labels.app}-db-password"
  namespace = var.kube_namespace
  }

  data = {
  "password"     = var.ror-tiamat-db-password
  }
}