variable "pubsub_project" {
  description = "GCP project of pubsub topic"
}

variable "storage_project" {
  description = "GCP project of pubsub topic"
}
variable "location" {
  description = "GCP bucket location"
}
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
variable "kube_namespace" {
  description = "The Kubernetes namespace"
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

variable "bucket_instance_suffix" {
  description = "A suffix for the bucket instance, may be changed if environment is destroyed and then needed again (name collision workaround) - also bucket names must be globally unique"
  default     = ""
}

variable "bucket_instance_prefix" {
  description = "A prefix for the bucket instance, may be changed if environment is destroyed and then needed again (name collision workaround) - also bucket names must be globally unique"
}

variable "force_destroy" {
  description = "(Optional, Default: false) When deleting a bucket, this boolean option will delete all contained objects. If you try to delete a bucket that contains objects, Terraform will fail that run"
  default     = false
}

variable "storage_class" {
  description = "GCP storage class"
  default     = "REGIONAL"
}

variable "versioning" {
  description = "The bucket's Versioning configuration."
  default     = "true"
}

variable "log_bucket" {
  description = "The bucket's Access & Storage Logs configuration"
  default     = "false"
}

variable "bucket_policy_only" {
  description = "Enables Bucket Policy Only access to a bucket"
  default     = "false"
}

variable "prevent_destroy" {
  description = "Prevent destruction of bucket"
  type        = bool
  default     = false
}

variable "db_instance_name" {
  description = "Database instance name"
  default = "tiamat-db-pg13"
}

variable "db_instance_replica_name" {
  description = "Database instance name"
  default = "tiamat-db-pg13-replica"
}

variable "ror-tiamat-db-username" {
  default = "Tiamat database username"
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