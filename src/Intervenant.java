import java.rmi.*;

public interface Intervenant extends Remote {
	public void listen (String msg) throws RemoteException;

	public void addNewClient(Intervenant i) throws RemoteException;

	public void delNewClient(Intervenant i) throws RemoteException;
	
	public String getName() throws RemoteException;

	public String getLastName() throws RemoteException;
	
	public void setId(int newId) throws RemoteException;
	
	public int getId() throws RemoteException;
	
	public void clearForumInformation() throws RemoteException;

}
