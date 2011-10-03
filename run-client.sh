#!/bin/sh
#usage : run-client.sh <registry host> <registry port>

java -Djava.security.policy=policy.all -classpath client Client $1 $2

