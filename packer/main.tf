provider "google" {
  project = "compact-haiku-414222"
  region = "us-east1"
}

resource "google_compute_instance" "vm_instance" {
  name = "vm_instance"

  network_interface {
    network = "default"
  }

}
