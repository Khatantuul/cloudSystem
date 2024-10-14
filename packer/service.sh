#!/bin/bash

#sudo setenforce 0

sudo systemctl daemon-reload
sudo systemctl enable service
#sudo systemctl start service
sudo systemctl status service --no-pager
echo "Journal Logs for service.service:"
journalctl -xe | grep service.service