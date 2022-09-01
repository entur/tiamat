# Contains main description of bulk of terraform?
terraform {
  required_version = ">= 0.12"
}

provider "google" {
  version = "~> 2.19"
}
provider "kubernetes" {
  version = "~> 1.13.3"
  load_config_file = var.load_config_file
}

# Create bucket
resource "google_storage_bucket" "storage_bucket" {
  name               = "${var.bucket_instance_prefix}-${var.bucket_instance_suffix}"
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
    log_object_prefix = "${var.bucket_instance_prefix}-${var.bucket_instance_suffix}"
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