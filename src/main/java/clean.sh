#!/bin/bash

if [ "$1" = "--remote" ]; then
  PROJ_DIR="P2T3G07"
  for machine in $(seq -w 1 10); do
      # skip faulty machines
#      if [ $machine -eq "04" ] || [ $machine -eq "10" ]; then
#        continue
#      fi

      echo "Cleaning $machine"
      sshpass -f password ssh sd307@l040101-ws$machine.ua.pt "rm -r $PROJ_DIR/*" &
      sshpass -f password ssh sd307@l040101-ws$machine.ua.pt "rm deploy.tar" &
    done
else
  find . -name "*.class" -type f -delete
  find . -name "*.tar" -type f -delete
fi