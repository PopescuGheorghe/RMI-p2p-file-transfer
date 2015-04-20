import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class FileClient {

    private static String downloadDirectory = "temp";
    static String filename = null;
    private static String ServerName = "CentralIndex";

    boolean createFile(byte[] file, String filename){
        BufferedOutputStream out;
        try{
            out = new BufferedOutputStream
                    (new FileOutputStream(downloadDirectory+filename));
            out.write(file, 0, file.length);
            out.flush();
            out.close();
        }catch (FileNotFoundException e){
            System.err.println("This file path does not exist.");
            e.printStackTrace();
            return false;
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void main(String[] args){

        String serverIp = null ;

        if(args.length != 1) {
            System.out.println("Usage: java FileCLient ServerIp");
            System.exit(0);
        }

        serverIp = args[0];

        FileClient client = new FileClient();
        Scanner in = new Scanner(System.in);
        int peerProviderID = 0;
        String peerProviderIP = null;
        System.setProperty("java.security.policy", "server.policy");
       /* System.setProperty("java.rmi.server.hostname", args[0]);*/
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            Registry reg = LocateRegistry.getRegistry(serverIp);
            SharedInterface handler = (SharedInterface) reg.lookup(ServerName); //reg.lookup
            System.out.println("To search a file just\n"
                    + " type the name below: \n -->");
            filename = in.nextLine();
            peerProviderID = handler.peerSearch(filename);
            peerProviderIP = handler.peerIpSearch(peerProviderID);


           try {
               Registry regProvider = LocateRegistry.getRegistry(peerProviderIP);
               ProviderInterface provider = (ProviderInterface) regProvider.lookup(String.valueOf(peerProviderID));
               byte[] file =null;
               file = provider.download(filename);
               if(file != null)
               System.out.format(String.valueOf(file));

               client.createFile(file,filename);

           }catch(Exception e) {
               System.err.println("FILEclient exception");
               e.printStackTrace();
           }
            System.out.println("peer ip:" + peerProviderIP);
            if (peerProviderID == 1000){
                System.out.println("No such file.");
            }else {
                System.out.println("File "+filename+" found on peer: "+peerProviderID);
            }



        }catch (Exception e){
            System.err.println("Handler exception");
            e.printStackTrace();
        }

    }
}
