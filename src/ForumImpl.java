import java.util.*;
import java.io.*;
import java.rmi.*;
import java.rmi.server.*;
/**
 * classe représentant l'objet servant du forum 
 */
public class ForumImpl implements Forum {
	// TO DO
	/**
    la structure de mémoristion des intervenants
	 */
	protected ArrayList<Intervenant> intervenants = new ArrayList<Intervenant>();
	/**
	 * l'identifiant unique d'intervenant
	 */	
	protected Integer idCounter = new Integer(0);
	
	public ForumImpl() throws RemoteException {
		super();
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
	public synchronized ArrayList<Intervenant> enter(Intervenant intervenant, String prenom, String nom) throws RemoteException{
		boolean alreadyExists = false;
		
		for(Iterator<Intervenant> i=intervenants.iterator(); i.hasNext(); ) {
			Intervenant interv = i.next();
			if(interv.equals(intervenant))
				alreadyExists = true;
		}
		
		if(!alreadyExists)
		{
			intervenants.add(intervenant);
			
			for(Iterator<Intervenant> i=intervenants.iterator(); i.hasNext(); ) {
				Intervenant interv = i.next();
				interv.addNewClient(intervenant);
			}
		}
		intervenant.setId(idCounter);
		idCounter++;
		
		return intervenants;
	}

	/**
	 * De-enregistre un intervanant dans la structure de mémoristion des intervenants. Cette méthode est
	 * appelée par le traitant de communication du programme client (IntervenantImpl) 
	 * @param id identification de l'intervenant retourne lors de l'appel à la methode enter.
	 */
	public synchronized void leave(int id) throws RemoteException{
		Intervenant intervenant = intervenants.get(id);
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
			sBuilder.append(interv.getName());
			if (i.hasNext())
		        sBuilder.append("\n");
		}
		return sBuilder.toString();
	}
}

