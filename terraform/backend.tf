
  # Describe where terraform will store the state of infrastructure
  terraform {
    backend "gcs" {
    bucket = "ent-gcs-tfa-tiamat"
    }
  }