import java.awt.*;
import java.awt.event.*;
import java.rmi.RemoteException;

/**
 * Cette classe défini l'interface graphique du programme client. L'interface
 * est mise en place par le constructeur IrcGui.
 */

public class IrcGui {

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
	 * ref directe vers le handler de communication (IntervenantImpl)
	 */
	static IntervenantImpl intervenant;

	/**
	 * Indique que le client n'est pas connecté à un forum. Cette constante est
	 * utilisé pour gérer le comportement des boutons de l'interface graphique.
	 */
	private static final int NOTCONNECTED = 0;

	/**
	 * Indique que le client est connecté à un forum. Cette constante est
	 * utilisé pour gérer le comportement des boutons de l'interface graphique.
	 */
	private static final int CONNECTED = 1;

	/**
	 * Variable représentant l'état de connexion d'un client.
	 */
	private int statut = NOTCONNECTED;

	Button write_button;
	Button connect_button;
	Button who_button;
	Button leave_button;

	/**
	 * Constructeur de l'interface graphique Ircgui. Ce constructeur instancie
	 * les divers objets graphiques et associe au divers bouttons les classes de
	 * traitement correspondantes. L'interface comporte 4 boutons : - connect :
	 * connexion à un forum (traitant associé : connectListener) - write :
	 * Emission d'un message à un forum (traitant associé : connectListener) -
	 * who : liste des intervnant connecté au forum (traitant associé :
	 * WhoListener) - leave : quitte un forum de discussion ((traitant associé :
	 * LeaveListener))
	 */
	public IrcGui() {
		// initGui
		frame = new Frame();
		frame.setLayout(new FlowLayout());

		text = new TextArea(10, 60);
		text.setEditable(false);
		text.setForeground(Color.red);
		frame.add(text);

		data = new TextField(60);
		frame.add(data);

		write_button = new Button("write");
		write_button.addActionListener(new writeListener(this));
		frame.add(write_button);

		connect_button = new Button("connect");
		connect_button.addActionListener(new connectListener(this));
		frame.add(connect_button);

		who_button = new Button("who");
		who_button.addActionListener(new whoListener(this));
		frame.add(who_button);

		leave_button = new Button("leave");
		leave_button.addActionListener(new leaveListener(this));
		frame.add(leave_button);

		write_button.setEnabled(false);
		connect_button.setEnabled(true);
		who_button.setEnabled(false);
		leave_button.setEnabled(false);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				System.exit(0);
			}
		});
		
		frame.setSize(530, 300);
		text.setBackground(Color.black);
		frame.show();

	}

	/**
	 * Fixe le traitant de communication (IntervenantImpl) utiliser par le GUI
	 * pour communiquer avec le forum
	 * 
	 * @param intervenant
	 *            une référence directe sur le traitant de communication
	 */
	public void setHandler(IntervenantImpl interv) {
		intervenant = interv;
		try {
			frame.setTitle(intervenant.getName() + " " + intervenant.getLastName());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	 * Classe traitant les action sur le bouton connect du GUI.
	 * 
	 */
	class connectListener implements ActionListener {
		IrcGui irc;

		/**
		 * Constructeur de ConnectListener
		 * 
		 * @param IrcGui
		 *            une référence directe vers l'objet gérant le GUI (IrcGui).
		 */
		public connectListener(IrcGui i) {
			irc = i;
		}

		/**
		 * Traite les clicks sur bouton connect. Execute la methode
		 * enter(java.lang.String forum_name) sur le traitant de communication
		 * (IntervenantImpl)
		 * 
		 * @param e
		 *            l'evenement associé
		 */
		public void actionPerformed(ActionEvent e) {
			String forumName = irc.data.getText();
			if (!forumName.isEmpty()) {
				try {
					boolean hasEntered = intervenant.enter(forumName);

					if (hasEntered) {
						Print("You are connected to '" + forumName);
						statut = CONNECTED;
					}
					else {
						Print("You can't connect to this forum because you have been banned.");
					}
					irc.data.setText("");

				} catch (Exception e1) {
					e1.printStackTrace();
					Print("There occurred a problem in the connection.");
				}
			}
			if (statut == CONNECTED) {
				write_button.setEnabled(true);
				connect_button.setEnabled(false);
				who_button.setEnabled(true);
				leave_button.setEnabled(true);
			} else {
				write_button.setEnabled(false);
				connect_button.setEnabled(true);
				who_button.setEnabled(false);
				leave_button.setEnabled(false);
			}

		}
	}

	/**
	 * Classe traitant les action sur le bouton write du GUI.
	 * 
	 */
	class writeListener implements ActionListener {
		IrcGui irc;

		/**
		 * Constructeur de WriteListener
		 * 
		 * @param IrcGui
		 *            une référence directe vers l'objet gérant le GUI (IrcGui).
		 */
		public writeListener(IrcGui i) {
			irc = i;
		}

		/**
		 * Traite les clicks sur bouton connect. Execute la methode
		 * say(java.lang.String msg) ) sur le traitant de communication
		 * (IntervenantImpl)
		 * 
		 * @param e
		 *            l'evenement associé
		 */
		public void actionPerformed(ActionEvent e) {
			String msg = irc.data.getText();
			try {
				intervenant.say(msg);
				irc.data.setText("");
			} catch (Exception e1) {
				e1.printStackTrace();
				Print("There occurred a problem in receiving a message.");
			}
			if (statut == CONNECTED) {
				write_button.setEnabled(true);
				connect_button.setEnabled(false);
				who_button.setEnabled(true);
				leave_button.setEnabled(true);
			} else {
				write_button.setEnabled(false);
				connect_button.setEnabled(true);
				who_button.setEnabled(false);
				leave_button.setEnabled(false);
			}
		}
	}

	/**
	 * Classe traitant les action sur le bouton who du GUI.
	 * 
	 */
	class whoListener implements ActionListener {
		IrcGui irc;

		/**
		 * Constructeur de whoListener
		 * 
		 * @param IrcGui
		 *            une référence directe vers l'objet gérant le GUI (IrcGui).
		 */
		public whoListener(IrcGui i) {
			irc = i;
		}

		/**
		 * Traite les clicks sur bouton who. Execute la methode String who() sur
		 * le traitant de communication (IntervenantImpl)
		 * 
		 * @param e
		 *            l'evenement associé
		 */
		public void actionPerformed(ActionEvent e) {
			try {
				Print("Users Connected to the Forum:");
				Print(intervenant.who());
			} catch (Exception e1) {
				e1.printStackTrace();
				Print("There occurred a problem in receiving the list of clients.");
			}
			if (statut == CONNECTED) {
				write_button.setEnabled(true);
				connect_button.setEnabled(false);
				who_button.setEnabled(true);
				leave_button.setEnabled(true);
			} else {
				write_button.setEnabled(false);
				connect_button.setEnabled(true);
				who_button.setEnabled(false);
				leave_button.setEnabled(false);
			}
		}
	}

	/**
	 * Classe traitant les action sur le bouton leave du GUI.
	 * 
	 */
	class leaveListener implements ActionListener {
		IrcGui irc;

		/**
		 * Constructeur de leaveListener
		 * 
		 * @param IrcGui
		 *            une référence directe vers l'objet gérant le GUI (IrcGui).
		 */
		public leaveListener(IrcGui i) {
			irc = i;
		}

		/**
		 * Traite les clicks sur bouton leave. Execute la methode void leave()
		 * sur le traitant de communication (IntervenantImpl)
		 * 
		 * @param e
		 *            l'evenement associé
		 */
		public void actionPerformed(ActionEvent e) {
			try {
				intervenant.leave();
				Print("You've left the forum");
				statut = NOTCONNECTED;
			} catch (Exception e1) {
				e1.printStackTrace();
				Print("There occurred a problem in leaving a forum.");
			}
			if (statut == CONNECTED) {
				write_button.setEnabled(true);
				connect_button.setEnabled(false);
				who_button.setEnabled(true);
				leave_button.setEnabled(true);
			} else {
				write_button.setEnabled(false);
				connect_button.setEnabled(true);
				who_button.setEnabled(false);
				leave_button.setEnabled(false);
			}
		}
	}

	/**
	 * Efface tout l'information sur le forum de l'intervenant
	 * et change l'interface
	 */
	public void showDisconnectedInterface() {
		write_button.setEnabled(false);
		connect_button.setEnabled(true);
		who_button.setEnabled(false);
		leave_button.setEnabled(false);
		statut = NOTCONNECTED;
		Print("You have been kicked out of the forum");		
	}

}
