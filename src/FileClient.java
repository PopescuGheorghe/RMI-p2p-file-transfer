import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class FileClient {

    File theDir = new File("download");

    private static String filename = null;
    private static String ServerName = "CentralIndex";

    boolean createFile(byte[] file, String filename){
        BufferedOutputStream out;
        if (!(Files.isDirectory(Paths.get("download")))) {
            try {
                theDir.mkdir();

            } catch (SecurityException se) {
                System.err.println("Directory not created");
            }
        }
        String toDownload = null;
        if(!(new File("download/"+filename).isFile())) {
            toDownload = theDir + File.separator + filename;
        }
        else {
            toDownload = theDir + File.separator + filename + "_copy";
            System.out.println("File already exists, downloading a copy");
        }

            try {
                out = new BufferedOutputStream
                        (new FileOutputStream(toDownload));
                out.write(file, 0, file.length);
                out.flush();
                out.close();
            } catch (FileNotFoundException e) {
                System.err.println("This file path does not exist.");
                e.printStackTrace();
                return false;
            } catch (IOException e) {
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
            if (peerProviderID == 1000){
                System.out.println("No such file.");
            }else {
                System.out.println("File "+filename+" found on peer: "+peerProviderID +" at the adress: " + peerProviderIP);
            }

           try {
               Registry regProvider = LocateRegistry.getRegistry(peerProviderIP);
               ProviderInterface provider = (ProviderInterface) regProvider.lookup(String.valueOf(peerProviderID));
               byte[] file =null;
               file = provider.download(filename);
               if (client.createFile(file,filename))
                   System.out.println("Download succesful");
               else
                    System.out.println("Download unsuccesful");
           }catch(Exception e) {
               System.err.println("FileClient exception");
           }

        }catch (Exception e){
            System.err.println("Handler exception");
            e.printStackTrace();
        }

    }
}
