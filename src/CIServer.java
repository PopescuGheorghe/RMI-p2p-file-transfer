
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

    static int loginID = 0;

    public CIServer(){
        super();
        this.rmiregistry = new Hashtable<String,Integer>();
        this.peer = new Hashtable<Integer,String>();
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
    public boolean insertPeerIP(int peerID) throws RemoteException, ServerNotActiveException {
        peer.put(peerID, getClientHost());
        return peer.isEmpty();
    }

    @Override
    public int login() throws RemoteException {

        loginID++;
        System.out.println("Logged in succesfully with ID: " + loginID);
        return loginID;
    }

    public static void main(String[] args){

        String serverIp = null ;
        if(args.length != 1) {
            System.out.println("Usage: java FileProvider ServerIp ");
            System.exit(0);
        }

        serverIp = args[0];

        System.setProperty("java.security.policy", "server.policy");
        System.setProperty("java.rmi.server.hostname",serverIp );

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