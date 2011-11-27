#!/bin/sh
#usage : run-client.sh <registry host> <registry port>

java -Djava.security.policy=policy.all -classpath src Irc $1 $2 $3

