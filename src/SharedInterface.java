import java.rmi.*;

public interface SharedInterface extends Remote {
    int peerSearch (String filename) throws RemoteException;
    boolean insertToRegistry(int peerID, String filename) throws RemoteException;
}