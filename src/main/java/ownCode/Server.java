package ownCode;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.BindException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {
	public String clientString;
	public String namePlayerWaiting = null;
	public String playerName;
	public List<String> playerNames= new ArrayList<String>();
	
	public int requestDIM;
	public int requestPlayerColorIndex;
	public int gameID;
	public int serverPort;
	
	public BufferedReader clientInput;
	public ClientHandler clientServerSocket; 
	public Socket sock;
	public ClientHandler ch;
	public ClientHandler chPlayerWaiting;
	public List<ClientHandler> threads;
	public List<Game> gameList = new ArrayList<Game>();
	public BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
	public ClientInputHandler CIH;
	public ServerSocket serverSocket;

	 public Server() {
	 }
	 
    public static void main(String[] args) {
    	Server server = new Server();
    	server.gameFlow();
    }
    
  //geef een getal om een port te maken
    public void gameFlow() {
  	  	try {
  	  		print("Which port would you like to open?");
  	  		print("Type the port");
	  	  	if(userInput != null) {
				String thisLine = userInput.readLine();
				serverPort = Integer.parseInt(thisLine);
				print("You have chosen port " + serverPort);
				print("Wait till clients connect with you");
	  	  	}
	  	  	threads = new ArrayList<ClientHandler>();
  	  		this.run();
  	  	} catch (IOException e) {
  	  		print("Try again");
  	  		gameFlow();
  	  	}
    }

    /**
     * Listens to a port of this Server if there are any Clients that 
     * would like to connect. For every new socket connection a new
     * ClientHandler thread is started that takes care of the further
     * communication with the Client.
     */
    //probeer een ServerSocket aan te maken met zelf gegeven port
    public void run() {
    	try {
			serverSocket = new ServerSocket(serverPort);
    	} catch (BindException e) {
			print("port is already used, enter a new port");
			gameFlow();
		} 	catch (IOException e) {
			e.printStackTrace();
		} 
			while (true) {
				try {
					Socket s = serverSocket.accept();
					CIH = new ClientInputHandler(this);
					ch = new ClientHandler(this, s);
					ch.start();
					addHandler(ch);
				}catch (IOException e) {
					e.printStackTrace();
				} 
			}
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

    private static void print(String message){
		System.out.println(message);
	}
}

