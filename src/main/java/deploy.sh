#!/bin/bash

PROJ_DIR=P2T3G07
USR="sd307"
n=10 # for 2 assault parties, need to change if more are deployed

rm deploy.tar
find . -name "*.tar" -type f -delete
tar czf deploy.tar $(find . -name '*.class' -o -name '*.policy')

for machine in $(seq -w $n); do
  # skip faulty machines
#  if [ $machine -eq "04" ] || [ $machine -eq "10" ]; then
  if [ $machine -eq "04" ]; then
    continue
  fi

  {
    echo "Deploying to $machine"
    sshpass -f password scp deploy.tar sd307@l040101-ws$machine.ua.pt:/home/$USR/deploy.tar
    sshpass -f password ssh sd307@l040101-ws$machine.ua.pt "tar -C $PROJ_DIR -xzf deploy.tar"
#    sshpass -f password ssh sd307@l040101-ws$machine.ua.pt "rm -r $PROJ_DIR/*"
  } &
done

#rm deploy.tar
