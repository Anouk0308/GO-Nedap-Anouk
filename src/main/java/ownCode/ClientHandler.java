package ownCode;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ClientHandler extends Thread {
	public Server s;
    private BufferedReader clientInput;
    private BufferedWriter serverToClient;
    private String thisLine;
    private String clientString;
    private Socket sock;
    private ClientInputHandler CIH;

    public ClientHandler(Server server, Socket sock) { 
	   try {
		   	this.sock = sock;
	    	this.s = server;
	    	this.CIH = s.CIH;
	    	clientInput = new BufferedReader(new InputStreamReader(sock.getInputStream()));
	    	serverToClient = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
	    } catch(IOException e) {
	    	print(e.getMessage());
	    }
    }
    
    public void run() {
    	thisLine = null;
    	try {
    		while(true) {
		         clientString = clientInput.readLine();
		         String[] stringArray = CIH.clientStringSplitter(clientString);
		         CIH.stringArrayAnalyser(stringArray, this);
    		}

		} catch (IOException e1) {
			System.out.println("problemen bij functie run()");
		}
    	
    }
    
	public void sendMessage(String msg) {
		try {
			serverToClient.write(msg);
			serverToClient.newLine();
			serverToClient.flush();
		} catch (IOException e) {
			shutDown();
		}

	}
    
    //sluit alles
    public void shutDown() {
    	s.removeHandler(this);
    }
    
    private static void print(String message){
		System.out.println(message);
	}
    
}
