# Contains main description of bulk of terraform?
terraform {
  required_version = ">= 0.13.2"
}

provider "google" {
  version = ">= 4.26"
}
provider "kubernetes" {
  version = ">= 2.13.1"
}

# Create database
resource "google_sql_database_instance" "db_instance" {
  name = var.db_instance_name
  database_version = var.db_version
  project = var.cloudsql_project
  region = var.db_region

  settings {
    location_preference {
      zone = var.db_zone
    }
    tier = var.db_tier
    user_labels = var.labels
    availability_type = var.db_availability
    disk_size = var.db_disk_size
    backup_configuration {
      enabled = true
      // 01:00 UTC
      start_time = "01:00"
    }
    maintenance_window {
      // Sunday
      day = 7
      // 02:00 UTC
      hour = 2
    }
    ip_configuration {
      require_ssl = true
    }
  }
}

resource "google_sql_database" "db" {
  name = var.db_instance_name
  project = var.cloudsql_project
  instance = google_sql_database_instance.db_instance.name
}

resource "google_sql_user" "db-user" {
  name = var.ror-tiamat-db-username
  project = var.cloudsql_project
  instance = google_sql_database_instance.db_instance.name
  password = var.ror-tiamat-db-password
}

# database read replica used by kingu
resource "google_sql_database_instance" "db_instance_replica" {
  name = var.db_instance_replica_name
  master_instance_name = "${var.cloudsql_project}:${google_sql_database_instance.db_instance.name}"
  database_version = var.db_version
  project = var.cloudsql_project
  region = var.db_region

  replica_configuration {
    failover_target = false
  }

  settings {
    location_preference {
      zone =var.db_zone
    }
    tier = var.db_tier
    user_labels = var.labels
    availability_type = var.db_replica_availability
    disk_size = var.db_disk_size
    backup_configuration {
      enabled = false
    }
    ip_configuration {
      require_ssl = true
    }
  }

}
# Create bucket
resource "google_storage_bucket" "storage_bucket" {
  name               = "${var.bucket_instance_prefix}-${var.bucket_instance_suffix}"
  force_destroy      = var.force_destroy
  location           = var.location
  project            = var.storage_project
  storage_class      = var.storage_class
  labels             = var.labels
  uniform_bucket_level_access = true
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