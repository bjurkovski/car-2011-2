#!/bin/sh

# clean all classes in client and server directory
rm src/server/*.class
rm src/client/*.class

# compile all server classes
javac -d ./src/server -classpath src/server src/server/*.java

# generate the sub for the server
rmic -d ./src/server -classpath src/server Server

# The remote interface is needed to compile the client
cp src/server/ServerItf.class ./src/client/
javac -d ./src/client/ -classpath src/client src/client/*.java

cp src/server/Server_Stub.class ./src/client/
