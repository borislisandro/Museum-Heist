#!/bin/bash

PROJ_DIR="P2T3G07"

logger_ip="localhost"
logger_port=50000
concentration_ip="localhost"
concentration_port=50001
collection_ip="localhost"
collection_port=50002
museum_ip="localhost"
museum_port=50007

ap1_ip="localhost"
ap1_port=50004
ap2_ip="localhost"
ap2_port=50005

master_ip="localhost"
thief_ip="localhost"

registry_ip="localhost"
registry_port=50009
register_port=50006

# master museum thief

#java logger.Main $logger_port &
#sleep 1

alias java='/usr/lib/jvm/java-8-openjdk/jre/bin/java -Djava.rmi.server.codebase="http://localhost/$USER/classes/" -Djava.rmi.server.useCodebaseOnly=true -Djava.security.policy=/home/me/uni/42/SD/projetos/2/SD_P2/src/main/java/java.policy'
alias rmiregistry=/usr/lib/jvm/java-8-openjdk/jre/bin/rmiregistry

#type java
#exit 0

{
  if ! lsof -i :$registry_port >/dev/null
  then
    rmiregistry -J-Djava.rmi.server.codebase="http://localhost/$USER/classes/" \
      -J-Djava.rmi.server.useCodebaseOnly=true \
      $registry_port &
    sleep 1
  fi
  #                   server                      rmi
  java Register.Main $register_port $registry_ip $registry_port &
  sleep 1

  java logger.Main $logger_port $registry_ip $registry_port &
  sleep 1

  java assaultParty.Main $ap1_port 0 $registry_ip $registry_port &
  java assaultParty.Main $ap2_port 1 $registry_ip $registry_port &

  java concentrationSite.Main $concentration_port $registry_ip $registry_port &
  java collectionSite.Main $collection_port $registry_ip $registry_port &
  sleep 1

  java museum.Main $museum_port $registry_ip $registry_port &
  sleep 1

  java thief.Main $registry_ip $registry_port &
  sleep 3

  java masterThief.Main $registry_ip $registry_port &
}