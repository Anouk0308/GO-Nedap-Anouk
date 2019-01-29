package ownCode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock; 

public class Server {
	public BufferedReader clientInput;
	public ClientHandler clientServerSocket; 
	public Socket sock;
	public String clientString;
	public String namePlayerWaiting = null;
	public ClientHandler chPlayerWaiting;
	public String playerName;
	public List<Game> gameList = new ArrayList<Game>();
	public List<String> playerNames= new ArrayList<String>();
	public List<ClientHandler> threads;
	private Game g;
	public int requestDIM;
	public int requestPlayerColorIndex;
	public int gameID;
	private ReentrantLock lock = new ReentrantLock();
	public int serverPort;
	public BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
	public ClientInputHandler CIH;
	public ClientHandler ch;

	 public Server() {
		 //lege constructor om main te kunnen beginnen in gameFlow()
	 }
	 
    public static void main(String[] args) {
    	Server server = new Server();
    	server.gameFlow();
    }
    
    public void gameFlow() {
    	String defaultName = "default player name"; 
  	  	serverPort = 8000;
  	  
  	  	//probeer een ServerSocket aan te maken met zelf gegeven port
  	  	try {
  	  		print("Which port would you like to open?");
  	  		print("Type the port");
	  	  	if(userInput != null) {
				String thisLine = userInput.readLine();
				serverPort = Integer.parseInt(thisLine);
				System.out.println("You have chosen port " + serverPort);
	  	  	}
	  	  	threads = new ArrayList<ClientHandler>();
  	  		this.run();
  	  	} catch (IOException e) {
  	  		System.out.println("ERROR: could not create a socket on port " + serverPort);
  	  		System.out.println("Try again");
  	  		gameFlow();
  	  	}
    }

    /**
     * Listens to a port of this Server if there are any Clients that 
     * would like to connect. For every new socket connection a new
     * ClientHandler thread is started that takes care of the further
     * communication with the Client.
     */
    public void run() {
    	try {
			ServerSocket ssocket = new ServerSocket(serverPort);
			while (true) {
				Socket s = ssocket.accept();
				System.out.println("test: er is een clientsocket gemaakt");
				CIH = new ClientInputHandler(this);
				System.out.println("test: de clientinputhandler wordt gemaakt");
				ch = new ClientHandler(this, s);
				System.out.println("test: de clienthandler wordt gemaakt");
				ch.start();
				System.out.println("test: de clienthandler wordt gestart");
				addHandler(ch);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
   

   
    private static void print(String message){
		System.out.println(message);
	}
    
    /**
     * Sends a message using the collection of connected ClientHandlers
     * to all connected Clients.
     * @param msg message that is send
     */
    public void broadcast(String msg) {
    	print(msg);
    	for (ClientHandler c:threads) {
        	c.sendMessage(msg);
        }
    }
    
    /**
     * Add a ClientHandler to the collection of ClientHandlers.
     * @param handler ClientHandler that will be added
     */
    public void addHandler(ClientHandler handler) {
        threads.add(handler);
    }
    
    /**
     * Remove a ClientHandler from the collection of ClientHanlders. 
     * @param handler ClientHandler that will be removed
     */
    public void removeHandler(ClientHandler handler) {
        threads.remove(handler);
    }

}

