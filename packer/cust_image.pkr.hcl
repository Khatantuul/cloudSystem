packer {
  required_plugins {
    googlecompute = {
      source  = "github.com/hashicorp/googlecompute"
      version = "~> 1"
    }
  }
}

variable "project_id" {}
variable "source_image_family" {}
variable "ssh_username" {}



source "googlecompute" "my-image" {
  credentials_json    = ""
  project_id          = var.project_id
  source_image_family = var.source_image_family
  zone                = "us-east1-b"
  network             = "default"
  image_name          = "first-image-{{timestamp}}"
  ssh_username        = var.ssh_username

}

build {
  sources = ["source.googlecompute.my-image"]
  provisioner "file" {
    source      = "webapp.zip"
    destination = "/tmp/webapp.zip"
  }


  provisioner "shell" {
    script = "packer/installations.sh"
  }

  provisioner "file" {
    source      = "packer/service.service"
    destination = "/tmp/service.service"
  }

  provisioner "file" {
    source      = "packer/config.yaml"
    destination = "/tmp/config.yaml"
  }

  provisioner "shell" {
    script = "./packer/user.sh"
  }

  provisioner "shell" {
    script = "./packer/service.sh"
  }

  post-processor "manifest" {
    output     = "manifest.json"
    strip_path = true
    custom_data = {
      my_custom_data = "example"
    }
  }



}
