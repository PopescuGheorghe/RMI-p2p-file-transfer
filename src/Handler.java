import java.rmi.Remote;
import java.rmi.RemoteException;


public interface Handler extends Remote {
    byte[] obtain(String filename) throws RemoteException;
}
