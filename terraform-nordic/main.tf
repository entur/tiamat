locals {
  topic_name = "${module.init.app.owner}.${module.init.app.name}.changelog"
}

# Terraform configuration for tiamat
module "init" {
  source      = "github.com/entur/terraform-google-init//modules/init?ref=v0.2.1"
  app_id      = "tiamatno"
  environment = var.env
}

# https://github.com/entur/terraform-google-sql-db/tree/master/modules/postgresql#inputs
module "postgres" {
  source           = "github.com/entur/terraform-google-sql-db//modules/postgresql?ref=v0.1.2"
  init             = module.init
  generation       = 1
  database_version = "POSTGRES_14"
  databases        = ["tiamatno"]
}

# https://github.com/entur/terraform-google-cloud-storage/tree/master/modules/bucket#inputs
module "cloud-storage" {
  source = "github.com/entur/terraform-google-cloud-storage//modules/bucket?ref=v0.1.0"
  init   = module.init
}

# Create folder in a bucket
resource "google_storage_bucket_object" "content_folder" {
  name    = "export/"
  content = "Not really a directory, but it's empty."
  bucket  = module.cloud-storage.cloud_storage_bucket.name
}

# Create pubsub topic
resource "google_pubsub_topic" "changelog" {
  name    = local.topic_name
  project = module.init.app.project_id
  labels  = module.init.labels
}

# Create pubsub subscription
resource "google_pubsub_subscription" "changelog-subscription" {
  name    = local.topic_name
  topic   = google_pubsub_topic.changelog.name
  project = module.init.app.project_id
  labels  = module.init.labels
}


