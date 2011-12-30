import java.util.*;
import java.io.*;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;
/**
 * classe représentant l'objet servant de la fabrique
 */
public class FabriqueImpl implements Fabrique {
	private static FabriqueGui gui;
	private Type serverType;
	/**
    la structure de mémoristion des forums
	 */
	private static ArrayList<Forum> forums = new ArrayList<Forum>();
	/**
	 * l'identifiant unique d'intervenant
	 */	
	protected Integer portCounter;

	public FabriqueImpl(Type t) throws RemoteException {
		serverType = t; 
		portCounter = t == Type.PRI ? 1240 : 2000;
	}

	public void setGUI(FabriqueGui fGui) {
		gui = fGui;
	}

	public synchronized ArrayList<Forum> create(String forumName, boolean fromGUI) throws RemoteException{
		boolean alreadyExists = false;

		for(Iterator<Forum> i=forums.iterator(); i.hasNext(); ) {
			Forum forumAux = i.next();
			if(forumAux.getForumName().equals(forumName))
				alreadyExists = true;
		}

		if(!alreadyExists) {
			Forum forum = new ForumImpl(forumName, portCounter);

			forums.add(forum);	
			portCounter++;
			forum.start();
			if(fromGUI)
			{
				Registry registry;
				Fabrique fabrique;
				try {
					if (serverType == Type.PRI){
						registry = LocateRegistry.getRegistry(Fabrique.PORT_SEC);
						fabrique = (Fabrique) registry.lookup(Fabrique.NAME_SEC);
					}else{
						registry = LocateRegistry.getRegistry(Fabrique.PORT_PRI);
						fabrique = (Fabrique) registry.lookup(Fabrique.NAME_PRI);
					}
					fabrique.create(forumName, false);
				} catch (Exception e) {
					System.out.println("The other server is offline");
				}
			}
		}

		return forums;
	}

	public synchronized ArrayList<Forum> destroy(String forumName, boolean fromGUI) throws RemoteException{
		boolean forumFound = false;
		int i= 0;
		do {
			if(forumName.equals(forums.get(i).getForumName()))
				forumFound = true;
			else
				i++;
		} while(!forumFound && (i < forums.size()));

		if(forumFound) {
			forums.get(i).stop();
			forums.remove(forums.get(i));
			if(fromGUI)
			{
				Registry registry;
				Fabrique fabrique;
				try {
					if (serverType == Type.PRI){
						registry = LocateRegistry.getRegistry(Fabrique.PORT_SEC);
						fabrique = (Fabrique) registry.lookup(Fabrique.NAME_SEC);
					}else{
						registry = LocateRegistry.getRegistry(Fabrique.PORT_PRI);
						fabrique = (Fabrique) registry.lookup(Fabrique.NAME_PRI);
					}
					fabrique.destroy(forumName, false);
				} catch (Exception e) {
					System.out.println("The other server is offline");
				}
			}
		}

		return forums;
	}

	public synchronized Forum getForum(String forumName) throws RemoteException {
		boolean forumFound = false;
		int i= 0;
		if(!forums.isEmpty()){
			do{
				if(forumName.equals(forums.get(i).getForumName()))
					forumFound = true;
				else
					i++;
			}while(!forumFound && (i < forums.size()));

			if(forumFound)
				return forums.get(i);
		}
		return null;
	}

	public void listForums() throws RemoteException {
		try {
			gui.Print("List of Forums:");
			StringBuilder sBuilder = new StringBuilder();

			for(Iterator<Forum> i=forums.iterator(); i.hasNext(); ) {
				Forum forumAux = i.next();
				try {
					sBuilder.append(forumAux.getForumName());
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				if (i.hasNext())
					sBuilder.append("\n");
			}

			String lForums = sBuilder.toString();

			if (!lForums.equals(""))
				gui.Print(lForums);
		} catch (Exception e1) {
			e1.printStackTrace();
			gui.Print("There occurred a problem in the listing of forums.");
		}
	}

	public void listClients(String forumName) throws RemoteException {
		try {
			gui.Print("Clients of Forum " + forumName + " :");
			Forum forum = getForum(forumName);
			if (forum != null) {
				String listForums = forum.who();
				if (!listForums.equals(""))
					gui.Print(listForums);
			}
			else {
				gui.Print("This forum doesn't exist.");
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			gui.Print("There occurred a problem in the listing of clients.");
		}
	}

	public void banClient(String forumName, String clientName, String clientLastName, boolean fromGUI) throws RemoteException {
		try {
			Forum forum = getForum(forumName);
			if ((forum != null) && (!clientName.isEmpty()) && (!clientLastName.isEmpty())) {
				boolean banSucceedeed;
				if(fromGUI)
					banSucceedeed = forum.banClient(clientName, clientLastName, true);
				else
					banSucceedeed = forum.banClient(clientName, clientLastName, false);

				if (banSucceedeed){
					gui.Print(clientName + " " + clientLastName + " banned successfully");
					if(fromGUI)
					{
						Registry registry;
						Fabrique fabrique;
						try {
							if (serverType == Type.PRI){
								registry = LocateRegistry.getRegistry(Fabrique.PORT_SEC);
								fabrique = (Fabrique) registry.lookup(Fabrique.NAME_SEC);
							}else{
								registry = LocateRegistry.getRegistry(Fabrique.PORT_PRI);
								fabrique = (Fabrique) registry.lookup(Fabrique.NAME_PRI);
							}
							fabrique.banClient(forumName, clientName, clientLastName, false);
						} catch (Exception e) {
							System.out.println("The other server is offline");
						}
					}
				}else
					gui.Print("Client already banned!");
			}
			else {
				gui.Print("This forum doesn't exist or client full name is empty.");
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			gui.Print("There occurred a problem in the banning.");
		}
	}

	public void authClient(String forumName, String clientName, String clientLastName, boolean fromGUI) throws RemoteException {
		try {
			Forum forum = getForum(forumName);
			if ((forum != null) && (!clientName.isEmpty()) && (!clientLastName.isEmpty())) {
				boolean authSucceedeed = forum.authClient(clientName, clientLastName);
				if (authSucceedeed){
					gui.Print(clientName + " " + clientLastName + " authorized successfully");
					if(fromGUI)
					{
						Registry registry;
						Fabrique fabrique;
						try {
							if (serverType == Type.PRI){
								registry = LocateRegistry.getRegistry(Fabrique.PORT_SEC);
								fabrique = (Fabrique) registry.lookup(Fabrique.NAME_SEC);
							}else{
								registry = LocateRegistry.getRegistry(Fabrique.PORT_PRI);
								fabrique = (Fabrique) registry.lookup(Fabrique.NAME_PRI);
							}
							fabrique.authClient(forumName, clientName, clientLastName, false);
						} catch (Exception e) {
							System.out.println("The other server is offline");
						}
					}
				} else
					gui.Print("Client already authorized!");
			}
			else {
				gui.Print("Forum doesn't exist or client full name is empty.");
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			gui.Print("There occurred a problem in the authorization.");
		}
	}

	public void pingForum(String forumName) throws RemoteException {
		try {
			Forum forum = getForum(forumName);
			if (forum != null) {
				gui.Print("-- Pinging forum "+forumName+ "--");
				gui.Print("Ping?");
				// Get current time
				long start = System.currentTimeMillis();
				String response = forum.ping();
				// Get elapsed time in milliseconds
				long elapsedTimeMillis = System.currentTimeMillis() - start;
				gui.Print(response + " - " + elapsedTimeMillis + " ms");
			}
			else {
				gui.Print("This forum doesn't exist.");
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			gui.Print("There occurred a problem in the ping.");
		}
	}
}

