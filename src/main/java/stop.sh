#!/bin/bash

registry_ip="l040101-ws09.ua.pt"

sshpass -f password ssh sd307@$registry_ip "bash -s" < kill_remote.sh

if [ "$1" = "-a" ]
then
  for machine in $(seq -w 10); do
    # skip faulty machines and registry
    if [ "$machine" = "04" ] || [ "$machine" = "$registry_ip" ]; then
      continue
    fi
      sshpass -f password ssh sd307@l040101-ws$machine.ua.pt "bash -s" < kill_remote.sh
  done
fi