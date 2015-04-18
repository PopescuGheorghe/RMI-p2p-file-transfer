
import java.io.*;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;


public class CIServer implements SharedInterface{

    Hashtable<String,Integer> rmiregistry;

    public CIServer(){
        super();
        this.rmiregistry = new Hashtable<String,Integer>();
    }

    @Override
    public int peerSearch(String filename){

        if(rmiregistry.containsKey(filename)){
            return rmiregistry.get(filename);
        }
        System.out.println("The file does not exist.");
        return 1000;
    }

    @Override
    public boolean insertToRegistry(int peerID,String filename){
        rmiregistry.put(filename, peerID);
        return rmiregistry.isEmpty();
    }

    public static void main(String[] args){
        System.setProperty("java.security.policy", "server.policy");
        //System.setProperty("java.rmi.server.codebase", "file:/build/classes");

        if (System.getSecurityManager() == null){
            System.setSecurityManager(new SecurityManager());
        }

        try{
            String servername = "CentralIndex";
            SharedInterface handler = new CIServer();
            SharedInterface stub = (SharedInterface) UnicastRemoteObject.exportObject(handler,0);
            Registry reg = LocateRegistry.getRegistry();
            reg.rebind(servername, stub);
            System.out.println("CentralIndex service bound");
        }catch (Exception e){
            System.err.println("CentralIndex exception: ");
            e.printStackTrace();
        }
    }

}