#!/bin/sh
# usage : run-server.sh <registry port>

java -Djava.security.policy=server/policy.all -classpath src ForumServer $1

