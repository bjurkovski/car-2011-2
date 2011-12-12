import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;


/**
* Cette classe est la classe principale constituant le programme client.
* Elle est composé d'une fonction main qui à pour rôle d'instancier 
* l'interface graphique associée au client (IrcGui) ainsi qu'un objet 
* gérant les communications avec le forum (IntervenantImpl).
* 
*/

public class Irc {
         
    public static void main(String args[]) {			
	try{
		int port = Integer.parseInt(args[2]);
		IntervenantImpl intervenant = new IntervenantImpl(args[0], args[1], args[2]);
		intervenant.setGUI(new IrcGui());
		Intervenant stub = (Intervenant) UnicastRemoteObject.exportObject(intervenant, port);
		Registry registry = LocateRegistry.getRegistry(port);
		//Registry registry = LocateRegistry.createRegistry(port);
		//Registry registry = LocateRegistry.getRegistry();
		registry.rebind(args[0]+"_"+args[1], stub);
		System.out.println("Client running");
	} catch (Exception e) {
          System.out.println("ERROR : " + e) ;
	  e.printStackTrace(System.out);
	  }
    }
}

