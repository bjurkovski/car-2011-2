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

	static Fabrique fabrique;


	public static void main(String args[]) {

		int status = 0;

		try {
			fabrique = new FabriqueImpl();
			Fabrique stub = (Fabrique) UnicastRemoteObject.exportObject(
					fabrique, Fabrique.PORT);
			Registry registry = LocateRegistry.createRegistry(Fabrique.PORT);
			// Registry registry = LocateRegistry.getRegistry();
			registry.rebind(Fabrique.NAME, stub);
			System.out.println("Fabrique '" + Fabrique.NAME
					+ "' running on port " + Fabrique.PORT + "!");
			FabriqueGui fabriqueGui = new FabriqueGui(fabrique);
		} catch (Exception ex) {
			ex.printStackTrace();
			status = 1;
		}
		

	}

}
