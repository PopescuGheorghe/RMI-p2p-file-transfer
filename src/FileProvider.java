import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;

public class FileProvider implements ProviderInterface {

    static File fileDirectory = new File("Client_files");
    private static String filename;
    static String ServerName = "CentralIndex";
    private static int peerID ;

    public byte[] download(String filename){
        String pwd = fileDirectory.getAbsolutePath();
        pwd = pwd+"/"+filename;
        Path path = Paths.get(pwd);
        byte[] data =null;
        try {
            data = Files.readAllBytes(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    void directoryCheck(SharedInterface file) throws RemoteException {
        System.out.print(" start");
        String pwd = fileDirectory.getAbsolutePath();
        File[] subDirectory = new File(pwd).listFiles();
        if (subDirectory == null) {
            System.out.print(" Directory is empty ");
        }else {
            for (int c = 0; c < subDirectory.length; c++){
                File name = subDirectory[c];
                try{
                    file.insertToRegistry(peerID, name.getName());

                }catch (RemoteException e) {
                    System.err.println("Registry error");
                    e.printStackTrace();
                }
            }
            try {
                file.insertPeerIP(peerID);
            } catch (ServerNotActiveException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {

        String serverIp = null ;
        if(args.length != 2) {
            System.out.println("Usage: java FileProvider FileProviderID ServerIp ");
            System.exit(0);
        }

        serverIp = args[1];
        peerID = Integer.parseInt(args[0]);

        FileProvider provider = new FileProvider();
        System.setProperty("java.security.policy", "server.policy");
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            Registry reg = LocateRegistry.getRegistry(serverIp);
            SharedInterface handler = (SharedInterface) reg.lookup(ServerName); //reg.lookup
            provider.directoryCheck(handler);


            try{

                String IP = handler.peerIpSearch(peerID);
                System.setProperty("java.rmi.server.hostname",IP );

                String peername = String.valueOf(peerID);
                ProviderInterface peerHandler = new FileProvider();
                ProviderInterface stub = (ProviderInterface) UnicastRemoteObject.exportObject(peerHandler, 0);
                Registry peerReg = LocateRegistry.getRegistry();
                peerReg.rebind(peername, stub);
                System.out.println("Peer "+ IP +" service bound " + peerID );

            }catch (Exception e){
                System.err.println("CentralIndex exception: ");
                e.printStackTrace();
            }

        } catch (Exception e) {
            System.err.println("Handler exception");
            e.printStackTrace();
        }
    }
}
