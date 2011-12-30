import java.rmi.*;
import java.util.ArrayList;
import java.util.HashMap;

public interface Fabrique extends Remote {
	/**
	 * Enregistre un intervanant dans la structure de mémoristion des intervenants. Cette méthode est
	 * appelée par le traitant de communication du programme client (IntervenantImpl) 
	 * @param intervenant une reference distante vers l'intervenant
	 * @param nom nom de l'intervenant
	 * @param prenom prenom de l'intervenant
	 * @return un identifiant interne representant l'intervenant 
	 * dans la structure de mémoristion des intervenants
	 */

	public enum Type { PRI, SEC };

	final static int PORT_PRI = 1234;
	final static int PORT_SEC = 1235;

	final static String NAME_PRI = "Fabrique";
	final static String NAME_SEC = "Fabrique2";

	public ArrayList<Forum> create(String forumName, boolean fromGUI) throws RemoteException;

	public ArrayList<Forum> destroy(String forumName, boolean fromGUI) throws RemoteException;

	public Forum getForum(String forumName) throws RemoteException;

	public void listForums() throws RemoteException;

	public void listClients(String forumName) throws RemoteException;

	public void banClient(String forumName, String clientName, String clientLastName, boolean fromGUI) throws RemoteException;

	public void authClient(String forumName, String clientName, String clientLastName, boolean fromGUI) throws RemoteException;

	public void pingForum(String forumName) throws RemoteException;
}





