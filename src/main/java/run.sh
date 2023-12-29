#!/bin/bash

PROJ_DIR="P2T3G07"

logger_ip="l040101-ws01.ua.pt"
ap1_ip="l040101-ws02.ua.pt"
ap2_ip="l040101-ws03.ua.pt"
concentration_ip="l040101-ws10.ua.pt"
collection_ip="l040101-ws05.ua.pt"
museum_ip="l040101-ws06.ua.pt"

thief_ip="l040101-ws07.ua.pt"
master_ip="l040101-ws08.ua.pt"

logger_port="22360"
ap1_port="22360"
ap2_port="22360"
concentration_port="22360"
collection_port="22360"
museum_port="22360"


registry_ip="l040101-ws09.ua.pt"
registry_port="22361"
register_port="22360"

#java="java -Djava.rmi.server.codebase=http://$registry_ip:$registry_port"/sd307/classes/" -Djava.rmi.server.useCodebaseOnly=true -Djava.security.manager -Djava.security.policy=$HOME/$PROJ_DIR/java.policy"
# java='java -Djava.rmi.server.codebase="http://'"$registry_ip:$registry_port"'/sd307/classes/" -Djava.rmi.server.useCodebaseOnly=true -Djava.security.manager -Djava.security.policy='$HOME/$PROJ_DIR'/java.policy'
#java='java -Djava.rmi.server.codebase=http://'"$registry_ip"'/sd307/classes/ -Djava.rmi.server.useCodebaseOnly=true -Djava.security.manager -Djava.security.policy=/home/sd307/'$PROJ_DIR'/java.policy'
java='java -Djava.rmi.server.codebase=http://'"$registry_ip"'/sd307/classes/ -Djava.rmi.server.useCodebaseOnly=true -Djava.security.policy=/home/sd307/'$PROJ_DIR'/java.policy'

echo $java

#sshpass -f password ssh sd307@$registry_ip 'kill -9 $(ps aux | grep $USER | grep rmiregistry | awk "{print \$2}")' &
#sshpass -f password ssh sd307@$registry_ip 'kill -9 $(ps aux | grep $USER | grep java | '"awk '{print $2})'" &
#sshpass -f password ssh sd307@$logger_ip 'kill -9 $(ps aux | grep $USER | grep java | awk "{print $2}")' &
#sshpass -f password ssh sd307@$museum_ip 'kill -9 $(ps aux | grep $USER | grep java | awk "{print $2}")' &
#sshpass -f password ssh sd307@$concentration_ip 'kill -9 $(ps aux | grep $USER | grep java | awk "{print $2}")' &
#sshpass -f password ssh sd307@$collection_ip 'kill -9 $(ps aux | grep $USER | grep java | awk "{print $2}")' &
#sshpass -f password ssh sd307@$ap1_ip 'kill -9 $(ps aux | grep $USER | grep java | awk "{print $2}")' &
#sshpass -f password ssh sd307@$ap2_ip 'kill -9 $(ps aux | grep $USER | grep java | awk "{print $2}")' &
#sshpass -f password ssh sd307@$master_ip "fuser -k 22360/tcp" &
#sshpass -f password ssh sd307@$thief_ip "fuser -k 22360/tcp" &
#exit 1
#sleep 5

sshpass -f password ssh sd307@$registry_ip "cd $PROJ_DIR; rmiregistry -J-Djava.rmi.server.codebase=http://$registry_ip/sd307/classes/ -J-Djava.rmi.server.useCodebaseOnly=true $registry_port" &
sleep 1
sshpass -f password ssh sd307@$registry_ip "cd $PROJ_DIR; $java Register.Main $register_port $registry_ip $registry_port" &
sleep 1
sshpass -f password ssh sd307@$logger_ip "cd $PROJ_DIR; $java logger.Main $logger_port $registry_ip $registry_port" &
sleep 1
sshpass -f password ssh sd307@$concentration_ip "cd $PROJ_DIR; $java concentrationSite.Main $concentration_port $registry_ip $registry_port" &
sshpass -f password ssh sd307@$collection_ip "cd $PROJ_DIR; $java collectionSite.Main $collection_port $registry_ip $registry_port" &
sleep 1
sshpass -f password ssh sd307@$ap1_ip "cd $PROJ_DIR; $java assaultParty.Main $ap1_port 0 $registry_ip $registry_port" &
sshpass -f password ssh sd307@$ap2_ip "cd $PROJ_DIR; $java assaultParty.Main $ap2_port 1 $registry_ip $registry_port" &
sleep 4
sshpass -f password ssh sd307@$museum_ip "cd $PROJ_DIR; $java museum.Main $museum_port $registry_ip $registry_port" &
sleep 1
sshpass -f password ssh sd307@$thief_ip "cd $PROJ_DIR; $java thief.Main $registry_ip $registry_port" &
sleep 3
sshpass -f password ssh sd307@$master_ip "cd $PROJ_DIR; $java masterThief.Main $registry_ip $registry_port" &
