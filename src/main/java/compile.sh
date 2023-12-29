#!/bin/bash

# Dirty hack to use this on arch linux
if [ "$(uname -a | grep -o 'arch')" == "arch" ]
then
  alias javac=/usr/lib/jvm/java-8-openjdk/bin/javac
fi

javac assaultParty/*.java collectionSite/*.java concentrationSite/*.java logger/*.java masterThief/*.java museum/*.java thief/*.java Register/*.java
