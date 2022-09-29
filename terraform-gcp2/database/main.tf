# Contains main description of bulk of terraform?
terraform {
  required_version = ">= 0.13.2"
}

provider "google" {}

# Create database
# OBS: Intentionally  commented out config, create database and replicas manually
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
    insights_config {
      query_insights_enabled = true
      query_string_length = 2048
      record_application_tags = false
      record_client_address = false
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
    insights_config {
      query_insights_enabled = true
      query_string_length = 2048
      record_application_tags = false
      record_client_address = false
    }
    ip_configuration {
      require_ssl = true
    }
  }

}