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
	protected HashMap<Intervenant, IntervenantDescriptor> intervenants = new HashMap<Intervenant, IntervenantDescriptor>();
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
	public synchronized HashMap<Intervenant, IntervenantDescriptor> enter (Intervenant intervenant, String prenom, String nom) throws RemoteException{
		
		intervenants.put(intervenant, new IntervenantDescriptor(intervenant, prenom, nom));

		return intervenants;
	}

	/**
	 * De-enregistre un intervanant dans la structure de mémoristion des intervenants. Cette méthode est
	 * appelée par le traitant de communication du programme client (IntervenantImpl) 
	 * @param id identification de l'intervenant retourne lors de l'appel à la methode enter.
	 */
	public synchronized void leave(int id) throws RemoteException{
		// TO DO

	}
}

