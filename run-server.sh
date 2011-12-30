#!/bin/sh
# usage : run-server.sh <PRI|SEC>

java -Djava.security.policy=server/policy.all -classpath src ForumServer $1 $2

