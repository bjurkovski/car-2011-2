#!/bin/sh
SERVER_FILES='./src/Forum.java ./src/ForumImpl.java ./src/ForumServer.java'
CLIENT_FILES='./src/Intervenant.java ./src/IntervenantDescriptor.java ./src/IntervenantImpl.java ./src/Irc.java ./src/IrcGui.java'

# clean all classes in client and server directory
rm src/*.class

# compile server classes
javac -d ./src -classpath src $SERVER_FILES

# generate the sub for the server
rmic -d ./src/ -classpath src/ Server

# The remote interface is needed to compile the client
# cp src/ServerItf.class ./src
javac -d ./src -classpath src $CLIENT_FILES
