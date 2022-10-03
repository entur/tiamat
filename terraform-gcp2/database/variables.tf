variable "cloudsql_project" {
  description = "GCP project of sql database"
}
variable "db_region" {
  description = "GCP  region"
  default = "europe-west1"
}
variable "db_zone" {
  description = "GCP zone"
  default = "europe-west1-b"
}

variable "db_tier" {
  description = "Database instance tier"
  default = "db-custom-1-3840"
}
variable "db_availability" {
  description = "Database availability"
  default = "ZONAL"
}
variable "db_replica_availability" {
  description = "Database replica availability"
  default = "ZONAL"
}

variable "labels" {
  description = "Labels used in all resources"
  type        = map(string)
     default = {
       manager = "terraform"
       team    = "ror"
       slack   = "talk-ror"
       app     = "tiamat"
     }
}


variable "db_instance_name" {
  description = "Database instance name"
  default = "tiamat-db-1"
}

variable "db_instance_replica_name" {
  description = "Database instance name"
  default = "tiamat-db-1-replica"
}

variable "ror-tiamat-db-username" {
  description = "Tiamat database username"
}
variable ror-tiamat-db-password {
  description = "Tiamat database password"
}
variable "db_disk_size" {
  description = "Database disk size"
  default = "20"
}
variable "db_version" {
  description = "postgres version"
  default = "POSTGRES_13"
}

variable "transaction_log_retention_days" {
  default = "7"
}
variable "retained_backups" {
  default = "7"
}