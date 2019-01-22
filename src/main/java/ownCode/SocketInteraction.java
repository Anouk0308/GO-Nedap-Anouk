package ownCode;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class SocketInteraction implements Runnable {
	public static final String EXIT = "exit";

    protected String name;
    protected Socket sock;
    protected BufferedReader in;
    protected BufferedWriter out;
    protected String thisLine;

    public SocketInteraction(String nameArg, Socket sockArg) throws IOException
    {
    	this.name = nameArg;
    	this.sock = sockArg;
    	this.out = new BufferedWriter(new OutputStreamWriter(this.sock.getOutputStream()));
    }
    
    //probeer input te lezen. wanneer het EXIT is, shutdown
    public void run() {
    	thisLine = null;
    	try {
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
    	try{
    		while((thisLine = in.readLine()) != null ) {
    			if (thisLine == "EXIT") {
        			shutDown();
        		} else {
        			
        		System.out.println(thisLine);
        		}
    		}
    		
    	} 
    	catch(IOException e){
    		System.out.println("problemen bij functie run()");
	    }
    }
    
    //maak van de in van de ene de out van de ander
    synchronized public void sendString(String string) {
	      	try{
	        		out.write(string);
	        		out.newLine();
	        		out.flush();
	
	      	} 
	    	catch(IOException e){
	    		System.out.println("problemen bij functie handleTerminalInput()");
	    	}
    }
    
    //sluit alles
    public void shutDown() {
    	try{
    		in.close();
    		out.close();
    		sock.close();
    	} 
    	catch(IOException e){
    		System.out.println("problemen bij functie shutDown()");
    		}
    }
}
