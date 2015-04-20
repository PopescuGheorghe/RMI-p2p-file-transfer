import java.rmi.*;
import java.rmi.server.ServerNotActiveException;

public interface SharedInterface extends Remote {
    int peerSearch (String filename) throws RemoteException;
    String peerIpSearch(int peerID) throws  RemoteException;
    boolean insertToRegistry(int peerID, String filename) throws RemoteException;
    boolean insertPeerIP(int peerID) throws RemoteException, ServerNotActiveException;
    int login() throws RemoteException;
}