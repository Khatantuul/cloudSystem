#!/bin/bash


#Installing Java JDK
#curl https://download.java.net/java/GA/jdk21.0.2/f2283984656d49d69e91c558476027ac/13/GPL/openjdk-21.0.2_linux-x64_bin.tar.gz -o openjdk-21.0.2_linux-x64_bin.tar.gz
curl -o jdk-17_linux-x64_bin.tar.gz https://download.oracle.com/java/17/latest/jdk-17_linux-x64_bin.tar.gz
tar xvf jdk-17_linux-x64_bin.tar.gz
sudo mv jdk-17.0.10 /usr/local/
echo "export JAVA_HOME=/usr/local/jdk-17.0.10" >> ~/.bashrc
echo "export PATH=\$JAVA_HOME/bin:\$PATH" >> ~/.bashrc
source ~/.bashrc

echo "Reached here 1"
echo "DNF version:"
dnf --version
sudo dnf module enable  -y maven:3.8
echo "Reached here 2"
sudo dnf install -y maven


sudo yum install -y unzip

sudo unzip -j /tmp/webapp.zip -d /opt

curl -sSO https://dl.google.com/cloudagents/add-google-cloud-ops-agent-repo.sh
sudo bash add-google-cloud-ops-agent-repo.sh --also-install