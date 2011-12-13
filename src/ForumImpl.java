import java.util.*;
import java.io.*;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;
/**
 * classe représentant l'objet servant du forum 
 */
public class ForumImpl implements Forum {

	/**
    la structure de mémoristion des intervenants
	 */
	protected ArrayList<Intervenant> intervenants = new ArrayList<Intervenant>();
	protected ArrayList<String> intervenantsBanned = new ArrayList<String>();
	/**
	 * l'identifiant unique d'intervenant
	 */	
	protected Integer idCounter = new Integer(0);
	protected String forumName;
	protected int forumPort;
	
	public ForumImpl(String forumName, int forumPort) throws RemoteException {
		super();
		this.forumName = forumName;
		this.forumPort = forumPort;
	}

	/**
	 * Enregistre un intervanant dans la structure de mémoristion des intervenants. Cette méthode est
	 * appelée par le traitant de communication du programme client (IntervenantImpl) 
	 * @param intervenant une reference distante vers l'intervenant
	 * @param nom nom de l'intervenant
	 * @param prenom prenom de l'intervenant
	 * @return un identifiant interne representant l'intervenant 
	 * dans la structure de mémoristion des intervenants
	 */
	public synchronized boolean enter(Intervenant intervenant, String prenom, String nom) throws RemoteException{
		
		boolean isBanned = false;
		
		for(Iterator<String> i=intervenantsBanned.iterator(); i.hasNext(); ) {
			String interv = i.next();
			if(interv.equals(prenom + "_" + nom))
				isBanned = true;
		}
		
		if(!isBanned) {
			intervenants.add(intervenant);
			
			for(Iterator<Intervenant> i=intervenants.iterator(); i.hasNext(); ) {
				Intervenant interv = i.next();
				interv.addNewClient(intervenant);
			}
			intervenant.setId(idCounter);
			idCounter++;
			return true;
		}
		
		return false;
	}

	/**
	 * De-enregistre un intervanant dans la structure de mémoristion des intervenants. Cette méthode est
	 * appelée par le traitant de communication du programme client (IntervenantImpl) 
	 * @param id identification de l'intervenant retourne lors de l'appel à la methode enter.
	 */
	public synchronized void leave(int id) throws RemoteException {
		boolean found = false;
		int j=0;
		do {
			if(intervenants.get(j).getId() == id)
				found = true;
			else
				j++;				
		} while(!found);
		
		Intervenant intervenant = intervenants.get(j);
		
		
		intervenants.remove(intervenant);
		
		for(Iterator<Intervenant> i=intervenants.iterator(); i.hasNext(); ) {
			Intervenant interv = i.next();
			interv.delNewClient(intervenant);
		}

	}
	
	public synchronized String who() throws RemoteException{
		StringBuilder sBuilder = new StringBuilder();
		
		for(Iterator<Intervenant> i=intervenants.iterator(); i.hasNext(); ) {
			Intervenant interv = i.next();
			sBuilder.append(interv.getName()+" "+interv.getLastName());
			if (i.hasNext())
		        sBuilder.append("\n");
		}
		return sBuilder.toString();
	}
	
	public synchronized String getForumName() throws RemoteException{
		return forumName;
	}
	
	public synchronized int getForumPort() throws RemoteException{
		return forumPort;
	}
	
	public synchronized ArrayList<Intervenant> getIntervenants() throws RemoteException {
		return intervenants;
	}

	public synchronized void start() throws RemoteException {
		try {	
		       Forum stub = (Forum) UnicastRemoteObject.exportObject(this, forumPort);
		       Registry registry = LocateRegistry.createRegistry(forumPort);
		       //Registry registry = LocateRegistry.getRegistry();
		       registry.rebind(forumName, stub);
		       System.out.println("Forum '" + forumName + "' running on port " + forumPort + "!");
		   } catch(Exception ex) {
		 		ex.printStackTrace();
		   }
		
	}
	
	public synchronized void stop() throws RemoteException {
		try {
			for(Iterator<Intervenant> i = intervenants.iterator(); i.hasNext(); ) {
				Intervenant interv = i.next();
				interv.clearForumInformation();
			}
			intervenants.clear();
			
	       Registry registry = LocateRegistry.getRegistry(forumPort);
	       registry.unbind(forumName);
	       System.out.println("Forum '" + forumName + "' deleted!");
	   } catch(Exception ex) {
	 		ex.printStackTrace();
	   }
	}
	
	public synchronized boolean banClient(String name, String lastName) throws RemoteException {
		boolean alreadyBanned = false;
		String fullName = name + "_" + lastName;
		
		for(Iterator<String> i=intervenantsBanned.iterator(); i.hasNext(); ) {
			String intervFullName = i.next();
			if(intervFullName.equals(fullName))
				alreadyBanned = true;
		}
		
		if(!alreadyBanned) {
			intervenantsBanned.add(fullName);
			
			int i=0;
			boolean found = false;
			while((i < intervenants.size()) && !found) {
				String fullNameAux = intervenants.get(i).getName() + "_" + intervenants.get(i).getLastName();
				if(fullNameAux.equals(fullName)) 
					found = true;
				else
					i++;
			}
			
			if(found) {
				try {
					Intervenant interv = intervenants.get(i); 
					leave(interv.getId());
					interv.clearForumInformation();
				} catch (Exception e) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	public synchronized boolean authClient(String name, String lastName) throws RemoteException {
		boolean isBanned = false;
		String fullName = name + "_" + lastName;
		
		for(Iterator<String> i=intervenantsBanned.iterator(); i.hasNext(); ) {
			String intervFullName = i.next();
			if(intervFullName.equals(fullName))
				isBanned = true;
		}
		
		if(isBanned) {
			intervenantsBanned.remove(fullName);			
			return true;
		}
		
		return false;
	}

	public String ping() throws RemoteException {
		return "Pong!";	
	}
}

