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
			System.out.println("problemen bij functie run()");
		}
    }
    
    //maak van de in van de ene de out van de ander
    synchronized public void sendString(String string) {
    	while(true) {
        	String thisLine = null;
          	try{
        		BufferedReader buf = new BufferedReader(new InputStreamReader(System.in));
        		thisLine = buf.readLine();
        		//System.out.println(thisLine);
            		out.write(thisLine);
            		out.newLine();
            		out.flush();

        	} 
        	catch(IOException e){
        		System.out.println("problemen bij functie sendString()");
        	}
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
