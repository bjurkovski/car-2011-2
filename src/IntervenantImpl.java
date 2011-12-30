import java.awt.*;
import java.awt.event.*;
import java.lang.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Cette classe défini le traitant de communication du programme client. Elle
 * est utilisée par les classes
 * connectListener,writeListener,whoListener,leaveListener du GUI pour effectuer
 * les communications distante avec le forum.
 */
public class IntervenantImpl implements Intervenant {

	/**
	 * Indique que les possibles retours de la fonction enter
	 */
	public static final int RETURNOK = 0;
	public static final int ISBANNED = 1;
	public static final int FORUMABSENT = 2;

	private static IrcGui gui;

	/**
	 * Référence distante vers un forum.
	 */
	private static Fabrique fabrique = null; // ref vers fabrique
	private static String forumName = null;

	/**
	 * Identification du client dans le forum. Cet identifiant est retourné lors
	 * de l'appel à la méthode enter sur le forum distant.
	 */
	private int id;

	/**
	 * nom de l'intervenant
	 */
	private String nom;
	/**
	 * prenom de l'intervenant
	 */
	private String prenom;

	/**
	 * intervenants conectées au forum
	 */
	private ArrayList<Intervenant> intervenants = new ArrayList<Intervenant>();

	/**
	 * constructeur de la classe IntervenantImpl. Le nom et le prenom de
	 * l'intervenant sont passés en parametre du programme client (irc.java)
	 * 
	 * @param nom
	 *            nom de l'intervenant
	 * @param prenom
	 *            prenom de l'intervenant
	 * @param port
	 * 				port de l'intervenant
	 * @throws UnknownHostException
	 * 			l'intervenant n'a pas reussi a obtenir son IP
	 */
	public IntervenantImpl(String prenom, String nom, String port) throws RemoteException, UnknownHostException {
		this.nom = nom;
		this.prenom = prenom;
		//this.address = InetAddress.getLocalHost().getHostAddress() + port;
	}

	/**
	 * Fixe une reference directe vers le gui (IrcGui). Cette reference est
	 * utilisée par le traitant de communication pour imprimer des message de
	 * chat dans le gui via la methode print definie dans IrcGui.
	 * 
	 * @param gui
	 *            le GUI
	 */
	public void setGUI(IrcGui ircgui) {
		gui = ircgui;
		gui.setHandler(this);
	}

	/**
	 * Execute la methode enter sur le forum. Cette methode est appelé par le
	 * traitant writeListener défini dans IrcGui. Cette méthode doit utiliser un
	 * serveur de nom pour obtenir une référence distante vers le forum et
	 * exécuter la méthode enter dessus.
	 * 
	 * @param forum_name
	 *            nom du forum
	 */
	public int enter(String forum_name) throws Exception{
		int returnCode = FORUMABSENT;
		boolean addToIntervenants = false;
		forumName = forum_name;

		try {
			returnCode = enterServer(forum_name, Fabrique.PORT_SEC, Fabrique.NAME_SEC, true);
		} catch (Exception e) {
			System.out.println("The Secondary server is offline");
			addToIntervenants = true;
		}

		try {
			returnCode = enterServer(forum_name, Fabrique.PORT_PRI, Fabrique.NAME_PRI, addToIntervenants);
		} catch (Exception e) {
			System.out.println("The primary server is offline");
		}

		return returnCode;
	}

	private int enterServer(String forum_name, int fabriquePort, String fabriqueName, boolean addToIntervenants) throws Exception{
		Registry registry = LocateRegistry.getRegistry(fabriquePort);
		fabrique = (Fabrique) registry.lookup(fabriqueName);

		Forum forum = fabrique.getForum(forum_name);
		if(forum != null){
			if(forum.enter(this, this.prenom, this.nom, addToIntervenants)) {			
				this.intervenants = forum.getIntervenants();
				return RETURNOK;
			}
			else {
				return ISBANNED;
			}
		}
		else {
			return FORUMABSENT;
		}
	}

	/**
	 * Execute la methode say sur le forum. Cette methode est appelé par le
	 * traitant writeListener défini dans IrcGui. Cette méthode doit utilise une
	 * référence distante vers le forum et exécuter la méthode say dessus.
	 * 
	 * @param msg
	 *            message à envoyer aux intervenants enregistrer dans le forum.
	 */
	public void say(String msg) throws Exception {
		for(Iterator<Intervenant> i=intervenants.iterator(); i.hasNext(); ) {
			Intervenant interv = i.next();
			interv.listen(prenom + " " + nom + " says: " + msg);
		}
	}

	/**
	 * Cette methode est appelé par le forum pour imprimer un nouveau message de
	 * chat a l'intervenant. Cette impression est déléguée à la méthode print
	 * définie dans IrcGui.
	 * 
	 * @param msg
	 *            nouveau message à imprimer dans le gui.
	 */
	public void listen(String msg) throws RemoteException {
		gui.Print(msg);
	}

	public void addNewClient(Intervenant i) throws RemoteException {
		intervenants.add(i);
	}

	public void delNewClient(Intervenant i) throws RemoteException {
		intervenants.remove(i);
	}

	public String getName() throws RemoteException {
		return prenom;
	}

	public String getLastName() throws RemoteException {
		return nom;
	}

	public void setId(int newId) throws RemoteException {
		this.id = newId;
	}

	public int getId() throws RemoteException {
		return id;
	}

	/**
	 * Execute la methode leave sur le forum. Cette methode est appelé par le
	 * traitant leaveListener défini dans IrcGui. Cette méthode doit utilise une
	 * référence distante vers le forum et exécuter la méthode leave dessus.
	 */
	public void leave() throws Exception {
		try{
			leaveServer(Fabrique.PORT_PRI, Fabrique.NAME_PRI);
		} catch (Exception e){
			System.out.println("The Primary server is offline");
		}

		try{
			leaveServer(Fabrique.PORT_SEC, Fabrique.NAME_SEC);
		} catch (Exception e){
			System.out.println("The Secondary server is offline");
		}
		
		id = 0;
		intervenants.clear();
	}
	
	private void leaveServer(int fabriquePort, String fabriqueName) throws Exception {
		Registry registry = LocateRegistry.getRegistry(fabriquePort);
		fabrique = (Fabrique) registry.lookup(fabriqueName);
		
		Forum forum = fabrique.getForum(forumName);
		if(forum != null){
			forum.leave(this.id); 
			forum = null;
		}
	}

	/**
	 * Execute la methode who sur le forum. Cette methode est appelé par le
	 * traitant whoListener défini dans IrcGui. Cette méthode doit utilise une
	 * référence distante vers le forum et exécuter la methode who dessus.
	 */
	public String who() throws Exception {
		Forum forum = null;
		try{
			forum = whoServer(Fabrique.PORT_PRI, Fabrique.NAME_PRI);
		}catch (Exception e) {
			System.out.println("The Primary server is offline");
		}
		
		if (forum != null)
			return forum.who();
		
		try{
			forum = whoServer(Fabrique.PORT_SEC, Fabrique.NAME_SEC);
		}catch (Exception e) {
			System.out.println("The Secondary server is offline");
		}
		
		if(forum != null)
			return forum.who();
		else
			return "";
	}
	
	private Forum whoServer(int fabriquePort, String fabriqueName) throws Exception{
		Registry registry = LocateRegistry.getRegistry(fabriquePort);
		fabrique = (Fabrique) registry.lookup(fabriqueName);
		
		return fabrique.getForum(forumName);
	}

	public void clearForumInformation() throws Exception {
		intervenants.clear();
		gui.showDisconnectedInterface();
	}
}
