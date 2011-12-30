// HelloServer.java
// Copyright and License 

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;


/**
 * Cette classe est la classe principale constituant le programme serveur de
 * forum. Cette classe représente le serveur du forum. Elle initialise l'orb et
 * l'objet servant du forum (ForumImpl).
 */

public class ForumServer {

	static FabriqueImpl fabrique;


	public static void main(String args[]) {

		int status = 0;
		Fabrique.Type t;
		if(args[0].equalsIgnoreCase("PRI")){
			t = Fabrique.Type.PRI;
			System.out.println("This server will be primary");
		} else {
			t = Fabrique.Type.SEC;
			System.out.println("This server will be secondary");
		}

		try {
			int port = Fabrique.Type.PRI == t ? Fabrique.PORT_PRI : Fabrique.PORT_SEC;
			String name = Fabrique.Type.PRI == t ? Fabrique.NAME_PRI : Fabrique.NAME_SEC;
			fabrique = new FabriqueImpl(t);
			Fabrique stub = (Fabrique) UnicastRemoteObject.exportObject(fabrique, port);
			Registry registry = LocateRegistry.createRegistry(port);
			// Registry registry = LocateRegistry.getRegistry();
			registry.rebind(name, stub);
			System.out.println("Fabrique '" + name + "' running on port " + port + "!");
			FabriqueGui fabriqueGui = new FabriqueGui(fabrique);
		} catch (Exception ex) {
			ex.printStackTrace();
			status = 1;
		}
	}
}