import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.io.*;
import java.rmi.server.ServerNotActiveException;
import java.util.Arrays;
import java.util.Scanner;

import static java.rmi.server.RemoteServer.getClientHost;

public class RMIClient {
    static String ServerName = "CentralIndex";
    static int peerID = 1;
    static File fileDirectory = new File("Client_files");
    static String downloadDirectory = "temp";

    boolean download(byte[] file, String filename){
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

    void directoryCheck(SharedInterface file){
        String pwd = fileDirectory.getAbsolutePath();
        File[] subDirectory = new File(pwd).listFiles();
        if (subDirectory == null) {

        }else {
            for (int c = 0; c < subDirectory.length; c++){
                File name = subDirectory[c];
                try{
                    file.insertToRegistry(peerID, name.getName());
                }catch (RemoteException e) {
                    System.err.println("Registry error");
                    e.printStackTrace();
                }
                /*try{
                    try {
                        file.getPeerIP(peerID, getClientHost());
                        System.out.print(getClientHost());
                    } catch (ServerNotActiveException e) {
                        e.printStackTrace();
                    }
                }catch (RemoteException e) {
                    System.err.println("Registry error");
                    e.printStackTrace();
                }*/
            }
        }
    }

    public static void main(String[] args){
        RMIClient client = new RMIClient();
        Scanner in = new Scanner(System.in);
        int peerAsServerID = 0;
        String peerAsServerIP = null;
        System.out.println("To search a file just\n"
                + " type the name below: \n -->");
        String filename = in.nextLine();
        System.setProperty("java.security.policy", "server.policy");
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            Registry reg = LocateRegistry.getRegistry();
            SharedInterface handler = (SharedInterface) reg.lookup(ServerName); //reg.lookup
            client.directoryCheck(handler);
            peerAsServerID = handler.peerSearch(filename);
            peerAsServerIP = handler.peerIpSearch(peerAsServerID);
            System.out.println("peer ip:" + peerAsServerIP);
            if (peerAsServerID == 1000){
                System.out.println("No such file.");
            }else {
                System.out.println("File "+filename+" found on peer: "+peerAsServerID);
            }
        }catch (Exception e){
            System.err.println("Handler exception");
            e.printStackTrace();
        }


        try{
            if (peerAsServerID == 1000 || peerAsServerID == 0){
                System.out.println("File:"+filename+" not found.");
            } else{

                String clientAsServerName = String.valueOf(peerAsServerID);

                Registry reg = LocateRegistry.getRegistry();

                Handler stub = (Handler)reg.lookup(clientAsServerName);

                byte[] file = stub.obtain(filename);
                
                if (client.download(file,filename)){
                    System.out.println("Download successful.\n "
                            + "The requested file is at your temp folder.");
                }else{
                    System.out.println("Download unsuccessful.\n"
                            + " There was a problem. Please try again.");
                }
            }
        }catch (Exception e){
            System.err.println("Download exception");
            e.printStackTrace();
        }
    }
}