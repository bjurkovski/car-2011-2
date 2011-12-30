import java.rmi.*;
import java.util.ArrayList;
import java.util.HashMap;

public interface Forum extends Remote {
	/**
 * Enregistre un intervanant dans la structure de mémoristion des intervenants. Cette méthode est
 * appelée par le traitant de communication du programme client (IntervenantImpl) 
 * @param intervenant une reference distante vers l'intervenant
 * @param nom nom de l'intervenant
 * @param prenom prenom de l'intervenant
 * @return un identifiant interne representant l'intervenant 
 * dans la structure de mémoristion des intervenants
 */
	
  public boolean enter (Intervenant intervenant, String prenom, String nom, boolean addToIntervenants)throws RemoteException;
  
   /**
 * De-enregistre un intervanant dans la structure de mémoristion des intervenants. Cette méthode est
 * appelée par le traitant de communication du programme client (IntervenantImpl) 
 * @param id identification de l'intervenant retourne lors de l'appel à la methode enter.
 */
  public  void leave(int id) throws RemoteException;
  
  public String who() throws RemoteException;
  
  public String getForumName() throws RemoteException;
  public int getForumPort() throws RemoteException;
  public ArrayList<Intervenant> getIntervenants() throws RemoteException;

  public void start() throws RemoteException;
  public void stop() throws RemoteException;
  
  public boolean banClient(String name, String lastName, boolean tellClient) throws RemoteException;
  public boolean authClient(String name, String lastName) throws RemoteException;

  public String ping() throws RemoteException;
 
}



    
  
