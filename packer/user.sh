#!/bin/bash

sudo mv /tmp/service.service /etc/systemd/system/
sudo mv /tmp/config.yaml /etc/google-cloud-ops-agent/
sudo systemctl restart google-cloud-ops-agent
echo pwd
sudo groupadd csye6225
sudo useradd -s /usr/sbin/nologin -g csye6225 csye6225
echo pwd
sudo chown -R csye6225:csye6225 "/opt/csye6225-0.0.1-SNAPSHOT.jar"
sudo chmod 755 -R "/opt/csye6225-0.0.1-SNAPSHOT.jar"
echo "THE HOME DIR:"
ls -l "/opt/csye6225-0.0.1-SNAPSHOT.jar"

sudo mkdir -p /var/log/csye/
sudo chown -R csye6225:csye6225 /var/log/csye/

sudo chown -R csye6225:csye6225 "/etc/systemd/system/service.service"
sudo chmod 755 -R "/etc/systemd/system/service.service"


#sudo chmod -R 755 /home/khatnabb/
#sudo usermod -s /bin/bash csye6225
#sudo chmod +rx /home/khatnabb/target
#sudo chmod +rx /home/khatnabb/target/csye6225-0.0.1-SNAPSHOT.jar

