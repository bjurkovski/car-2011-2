import java.rmi.*;

public interface Intervenant extends Remote {
	
	/**
	 * @return Formatted address of an intervenant
	 * @throws RemoteException
	 */
	public String getAddress () throws RemoteException;
	
	public void listen (String msg) throws RemoteException;

	public void addNewClient(Intervenant i) throws RemoteException;

	public void delNewClient(Intervenant i) throws RemoteException;
}
