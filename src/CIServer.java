
import java.io.*;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;

import static java.rmi.server.RemoteServer.getClientHost;


public class CIServer implements SharedInterface{

    Hashtable<String,Integer> rmiregistry;
    Hashtable<Integer,String> peer;

    public CIServer(){
        super();
        this.rmiregistry = new Hashtable<String,Integer>();
        this.peer = new Hashtable<Integer,String>();
    }

    @Override
    public int peerSearch(String filename){

        if(rmiregistry.containsKey(filename)){
            try {
                peer.put(rmiregistry.get(filename),getClientHost());
            } catch (ServerNotActiveException e) {
                e.printStackTrace();
            }
            return rmiregistry.get(filename);

        }
        System.out.println("The file does not exist.");
        return 1000;
    }

    @Override
    public String peerIpSearch(int peerID) throws RemoteException {
        if(peer.containsKey(peerID)) {
            return peer.get(peerID);
        }
        return null;
    }

    @Override
    public boolean insertToRegistry(int peerID,String filename){
        rmiregistry.put(filename, peerID);
        return rmiregistry.isEmpty();
    }

    @Override
    public boolean getPeerIP(int peerID, String peerIP) throws RemoteException {
        peer.put(peerID, peerIP);
        return peer.isEmpty();
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