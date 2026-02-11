#!/bin/bash
sudo ln -s $PWD/byransha.service /etc/systemd/system 
sudo systemctl enable --no-block byransha.service
sudo systemctl start byransha.service
sudo systemctl status byransha.service
sudo journalctl -u byransha.service