
import java.rmi.*;

public interface ProviderInterface extends Remote {

    byte[] download(String filename) throws RemoteException;
}
