/**
 * Created by gimmy on 4/20/15.
 */
import java.rmi.*;

public interface ProviderInterface extends Remote {

    byte[] download(String filename) throws RemoteException;
}
