// HelloServer.java
// Copyright and License 

import java.util.*;
import java.io.*;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;


/**
 * Cette classe est la classe principale constituant le programme serveur de forum. 
 * Cette classe représente le serveur du forum. Elle initialise l'orb 
 * et l'objet servant du forum (ForumImpl).
 */
 

public class ForumServer {

    
 public static void main(String args[]){

   int status = 0;


   try {	
	   int port = Integer.parseInt(args[1]);
	   Forum forum = new ForumImpl();
       Forum stub = (Forum) UnicastRemoteObject.exportObject(forum, port);
       Registry registry = LocateRegistry.createRegistry(port);
       //Registry registry = LocateRegistry.getRegistry();
       registry.rebind(args[0], stub);
       System.out.println("Forum '" + args[0] + "' running on port " + port + "!");
   } catch(Exception ex) {
 		ex.printStackTrace();
 		status = 1;
   }

 }
  
}
