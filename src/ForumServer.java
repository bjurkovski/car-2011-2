// HelloServer.java
// Copyright and License 

import java.util.*;
import java.awt.Button;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.rmi.*;
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
	/**
	 * utilisé pour imprimer les messages de chat
	 */
	public TextArea text;

	/**
	 * utilisé pour saisir les messages de chat
	 */
	public TextField data, dataconnect;

	/**
	 * Frame de la fenetre principale
	 */
	public Frame frame;

	/**
	 * Constructeur de l'interface graphique du ForumServer. Ce constructeur instancie
	 * les divers objets graphiques et associe au divers bouttons les classes de
	 * traitement correspondantes. L'interface comporte 6 boutons : - create :
	 * création d'un forum (traitant associé : connectListener) - destroy :
	 * destruction d'un forum (traitant associé : destroyListener) -
	 * list Forums : liste des forums connecté à la Fabrique (traitant associé :
	 * listForumsListener) - list Clients : liste des intervenants connecté à un Forum ((traitant associé :
	 * listClientsListener)) - ban client : ban un intervenant d'un forum ((traitant associé :
	 * banListener)) - authorize client : authorize un intervenant dans un forum ((traitant associé :
	 * authListener)) - ping forum : ping un forum ((traitant associé :
	 * pingListener))
	 */
	public ForumServer() {
		frame = new Frame();
		frame.setLayout(new FlowLayout());

		text = new TextArea(10, 60);
		text.setEditable(false);
		text.setForeground(Color.red);
		frame.add(text);

		data = new TextField(60);
		frame.add(data);

		Button create_button = new Button("create");
		create_button.addActionListener(new createListener(this));
		frame.add(create_button);

		Button destroy_button = new Button("destroy");
		destroy_button.addActionListener(new destroyListener(this));
		frame.add(destroy_button);

		Button listForums_button = new Button("list forums");
		listForums_button.addActionListener(new listForumsListener(this));
		frame.add(listForums_button);

		Button listClients_button = new Button("list clients");
		listClients_button.addActionListener(new listClientsListener(this));
		frame.add(listClients_button);

		Button banClient_button = new Button("ban client");
		banClient_button.addActionListener(new banClientListener(this));
		frame.add(banClient_button);

		Button authClient_button = new Button("authorize client");
		authClient_button.addActionListener(new authClientListener(this));
		frame.add(authClient_button);

		Button ping_button = new Button("ping forum");
		ping_button.addActionListener(new pingListener(this));
		frame.add(ping_button);

		frame.setSize(530, 300);
		text.setBackground(Color.black);
		frame.show();
	}

	/**
	 * Affiche un message de chat dans le GUI. Cette methode est utilisé par le
	 * traitant de communication lors de la reception d'un message de chat en
	 * provenance du forum.Un message de chat à le format suivant :
	 * "nom.prenom >> txt....".
	 * 
	 * @param msg
	 *            le message de chat à afficher.
	 */

	public void Print(String msg) {
		try {
			this.text.append(msg + "\n");
		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		}
	}

	/**
	 * Classe traitant les action sur le bouton create du GUI.
	 * 
	 */
	class createListener implements ActionListener {
		ForumServer forum;

		/**
		 * Constructeur de CreateListener
		 * 
		 * @param IrcGui
		 *            une référence directe vers l'objet gérant le GUI (ForumServer).
		 */
		public createListener(ForumServer forumServer) {
			forum = forumServer;
		}

		/**
		 * Traite les clicks sur bouton create. Execute la methode
		 * create(java.lang.String forumName) sur le traitant de communication
		 * (FabriqueImpl)
		 * 
		 * @param e
		 *            l'evenement associé
		 */
		public void actionPerformed(ActionEvent e) {
			String forumName = forum.data.getText();
			if (!forumName.equals("")) {
				try {
					fabrique.create(forumName);
					text.append(forumName + " was created successfully!");
					text.append("\n");
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			data.setText("");

		}
	}

	/**
	 * Classe traitant les action sur le destroy write du GUI.
	 * 
	 */
	class destroyListener implements ActionListener {
		ForumServer forum;

		/**
		 * Constructeur de DestroyListener
		 * 
		 * @param IrcGui
		 *            une référence directe vers l'objet gérant le GUI (ForumServer).
		 */
		public destroyListener(ForumServer forumServer) {
			forum = forumServer;
		}

		/**
		 * Traite les clicks sur bouton destroy. Execute la methode
		 * destroy(java.lang.String forumName) ) sur le traitant de communication
		 * (FabriqueImpl)
		 * 
		 * @param e
		 *            l'evenement associé
		 */
		public void actionPerformed(ActionEvent e) {
			String forumName = forum.data.getText();
			if (!forumName.equals("")) {
				try {
					fabrique.destroy(forumName);
					text.append(forumName + " was deleted successfully!");
					text.append("\n");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			data.setText("");
		}
	}

	/**
	 * Classe traitant les action sur le bouton list Forums du GUI.
	 * 
	 */
	class listForumsListener implements ActionListener {
		ForumServer forum;

		/**
		 * Constructeur de listForumsListener
		 * 
		 * @param IrcGui
		 *            une référence directe vers l'objet gérant le GUI (ForumServer).
		 */
		public listForumsListener(ForumServer forumServer) {
			forum = forumServer;
		}

		/**
		 * Traite les clicks sur bouton listForums. Execute la methode String listForums() sur
		 * le traitant de communication (FabriqueImpl)
		 * 
		 * @param e
		 *            l'evenement associé
		 */
		public void actionPerformed(ActionEvent e) {
			try {
				Print("Forums:");
				String listForums = fabrique.listForums();
				if (!listForums.equals(""))
					Print(listForums);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			data.setText("");
		}
	}

	/**
	 * Classe traitant les action sur le bouton list Clients du GUI.
	 * 
	 */
	class listClientsListener implements ActionListener {
		ForumServer forum;

		/**
		 * Constructeur de listClientsListener
		 * 
		 * @param IrcGui
		 *            une référence directe vers l'objet gérant le GUI (ForumServer).
		 */
		public listClientsListener(ForumServer forumServer) {
			forum = forumServer;
		}

		/**
		 * Traite les clicks sur bouton listClients. Execute la methode void getForum(java.lang.String forumName)
		 * sur le traitant de communication (FabriqueImpl) et après la methode who() de ForumImpl
		 * 
		 * @param e
		 *            l'evenement associé
		 */
		public void actionPerformed(ActionEvent e) {
			String forumName = forum.data.getText();
			try {
				Print("Clients:");
				Forum forum = fabrique.getForum(forumName);
				if (forum != null) {
					String listForums = forum.who();
					if (!listForums.equals(""))
						Print(listForums);
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			data.setText("");

		}
	}

	/**
	 * Classe traitant les action sur le bouton ban Client du GUI.
	 * 
	 */
	class banClientListener implements ActionListener {
		ForumServer forum;

		/**
		 * Constructeur de banListener
		 * 
		 * @param IrcGui
		 *            une référence directe vers l'objet gérant le GUI (ForumServer).
		 */
		public banClientListener(ForumServer forumServer) {
			forum = forumServer;
		}

		/**
		 * Traite les clicks sur bouton banClient. Execute la methode void getForum(java.lang.String forumName)
		 * sur le traitant de communication (FabriqueImpl) et après la methode 
		 * banClient(java.lang.String name, java.lang.String lastName) de ForumImpl
		 * 
		 * @param e
		 *            l'evenement associé
		 */
		public void actionPerformed(ActionEvent e) {
			String command = forum.data.getText();
			String[] strings = command.split(" ");
			try {

				Forum forum = fabrique.getForum(strings[0]);
				if ((forum != null) && (strings[1] != "") && (strings[2] != "")) {

					boolean banSucceedeed = forum.banClient(strings[1],
							strings[2]);
					if (banSucceedeed)
						Print(strings[1] + " " + strings[2]
								+ " banned successfully");
					else
						Print("Client already banned!");
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			data.setText("");
		}
	}

	/**
	 * Classe traitant les action sur le bouton authorize Client du GUI.
	 * 
	 */
	class authClientListener implements ActionListener {
		ForumServer forum;

		/**
		 * Constructeur de authClientListener
		 * 
		 * @param IrcGui
		 *            une référence directe vers l'objet gérant le GUI (ForumServer).
		 */
		public authClientListener(ForumServer forumServer) {
			forum = forumServer;
		}

		/**
		  * Traite les clicks sur bouton authClient. Execute la methode void getForum(java.lang.String forumName)
		 * sur le traitant de communication (FabriqueImpl) et après la methode 
		 * authClient(java.lang.String name, java.lang.String lastName) de ForumImpl
		 * 
		 * @param e
		 *            l'evenement associé
		 */
		public void actionPerformed(ActionEvent e) {
			String command = forum.data.getText();
			String[] strings = command.split(" ");
			try {

				Forum forum = fabrique.getForum(strings[0]);
				if ((forum != null) && (strings[1] != "") && (strings[2] != "")) {

					boolean authSucceedeed = forum.authClient(strings[1],
							strings[2]);
					if (authSucceedeed)
						Print(strings[1] + " " + strings[2]
								+ " authorized successfully");
					else
						Print("Client already authorized!");
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			data.setText("");
		}
	}

	/**
	 * Classe traitant les action sur le bouton ping du GUI.
	 * 
	 */
	class pingListener implements ActionListener {
		ForumServer forum;

		/**
		 * Constructeur de pingListener
		 * 
		 * @param IrcGui
		 *            une référence directe vers l'objet gérant le GUI (ForumServer).
		 */
		public pingListener(ForumServer forumServer) {
			forum = forumServer;
		}

		/**
		 * Traite les clicks sur bouton ping. Execute la methode void getForum(java.lang.String forumName)
		 * sur le traitant de communication (FabriqueImpl) et après la methode ping() de ForumImpl
		 * 
		 * @param e
		 *            l'evenement associé
		 */
		public void actionPerformed(ActionEvent e) {
			String forumName = forum.data.getText();
			try {
				Forum forum = fabrique.getForum(forumName);
				if (forum != null) {
					Print("Ping?");
					// Get current time
					long start = System.currentTimeMillis();
					String response = forum.ping();
					// Get elapsed time in milliseconds
					long elapsedTimeMillis = System.currentTimeMillis() - start;
					Print(response + " - " + elapsedTimeMillis + " ms");
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			data.setText("");
		}
	}

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
		} catch (Exception ex) {
			ex.printStackTrace();
			status = 1;
		}
		ForumServer forumServer = new ForumServer();

	}

}
