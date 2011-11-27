import java.rmi.*;

public interface Intervenant extends Remote {
	public void listen (String msg) throws RemoteException;

	public void addNewClient(Intervenant i) throws RemoteException;

	public void delNewClient(Intervenant i) throws RemoteException;
}
