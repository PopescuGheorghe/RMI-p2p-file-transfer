/**
 * Created by gimmy on 4/17/15.
 */

import java.rmi.*;

public interface Handler extends Remote {
    byte[] obtain(String filename) throws RemoteException;
}
