package ownCode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

public class Server {
	public BufferedReader clientInput;
	public SocketInteraction clientServerSocket;
	public Socket sock;
	public String clientString;
	public String namePlayerWaiting = null;
	public String playerName;
	private List<Game> gameList = new ArrayList<Game>();
	private Game g;
	private int requestDIM;
	private int requestPlayerColorIndex;
	private int gameID;

	
    public static void main(String[] args) {
    	Server s = new Server();
    	s.gameFlow();
    }
    
    public void gameFlow() {
    	while(true) {
	    	try {
	    		connect();
	    		clientInput = new BufferedReader(new InputStreamReader(sock.getInputStream()));
	    		clientString = clientInput.readLine();
	    		clientStringSplitter(clientString);
	    	}catch(IOException e) {
	    		System.out.println(e);
	    	}
    	}
    }
    	
  public void connect() {
	   /**	
  	
  	
 	 if (args.length != 3) {
          System.out.println(INITIAL_INPUT);
          System.exit(0);
      }
 	 
 	 String name = args[0];
      InetAddress addr = null;
      int port = 0;
      Socket sock = null;
      ServerSocket servsock = null;
      
      
   // check args[1] - the IP-adress
      try {
          addr = InetAddress.getByName(args[1]);
      } catch (UnknownHostException e) {
          System.out.println(INITIAL_INPUT);
          System.out.println("ERROR: host " + args[1] + " unknown");
          System.exit(0);
      }

      // parse args[2] - the port
      try {
          port = Integer.parseInt(args[2]);
      } catch (NumberFormatException e) {
          System.out.println(INITIAL_INPUT);
          System.out.println("ERROR: port " + args[2]
          		           + " is not an integer");
          System.exit(0);
      }
      
      // try to open a Socket to the server
      try {
          servsock = new ServerSocket(port);
      } catch (IOException e) {
          System.out.println("ERROR: could not create a socket on port " + port);
      }
      
      //Server waits until a Client wants to connect with the Server over the port
     
      try {
     	 System.out.println("Listening....");
          sock = servsock.accept();
          System.out.println("Client found!");
      } catch (IOException e) {
          e.printStackTrace();
          System.exit(0);
      }

      
      // create Peer object and start the two-way communication
      try {
          SocketInteraction server = new SocketInteraction(name, sock);
          Thread serverThread = new Thread(server);
          serverThread.start();
          
          server.sendString("lala");
          server.shutDown();
      } catch (IOException e) {
          e.printStackTrace();
      }
 	 
 }
 */
  }
    
    //split de serverstring in een array
  	public String[] clientStringSplitter(String clientString) {
  		this.clientString = clientString;
  		String[] stringArray = clientString.split("+");
  		return stringArray;
  	}
  	
  //analyseerd welk commando het is, en stuurt door naar de goede methode
  	public void stringArrayAnalyser(String[] sa) {
  		String s = sa[0];
  		switch(s) {
  			case "HANDSHAKE":				handshake(sa);
  											break;
  			case "SET_CONFIG":				setConfig(sa);
  											break;
  			case "MOVE":					move(sa);
  											break;
  			case "EXIT":					exit(sa);
  											break;
  			default:						System.out.println("The client sent an invalid command");
  											break;
  		}
  	}
  	
  	public void handshake(String[] sa) {
  		clientServerSocket.sendString(acknowledgeHandshake());
  		matchingPlayers(playerName);
  	}
  	
    public void matchingPlayers(String playerName) {//maakt spelletjes mogelijk voor 2 mensen
    	if(namePlayerWaiting == null) {
    		namePlayerWaiting = playerName;
    		clientServerSocket.sendString(requestConfig());//naar 1
    	}
    	else {
    		//hier moet een lock
    		lllll;
    		if(playerName.equals(namePlayerWaiting)) {
    			playerName = playerName+1;//niet dezelfde naam in 1 game
    		}
    		g = createNewGame(namePlayerWaiting, playerName);
    		gameList.add(g);
    		namePlayerWaiting = null;
    		g = null;
    		int gameID = gameList.size()-1;
    		
    		//stuur volgende naar beide, alleen dus een andere regel naar ander persoon
    		lllllll;
    		String ownPlayerName = "";//moet ik nog naar kijken
    		int ownPlayerColorIndex = 0; 
    		String otherPlayerName = "";
    		int currentPlayer = gameList.get(gameID).currentPlayer;
    		clientServerSocket.sendString(acknowledgeConfig(ownPlayerName, ownPlayerColorIndex, otherPlayerName, currentPlayer, gameID));
    		
    		//einde lock
    	}
    }
    
    public Game createNewGame(String playerName1, String playerName2) {
    	int gameID = gameList.size();
    	Game ng = new Game(playerName1, requestPlayerColorIndex, playerName2, requestDIM, gameID);
    	return null;
    }
    
 
  	public void setConfig(String[] sa) {
  		if(this.gameID == Integer.parseInt(sa[1])) {
	  		this.requestPlayerColorIndex = Integer.parseInt(sa[2]);
	  		this.requestDIM = Integer.parseInt(sa[3]);
  		}
  		else {
  			clientServerSocket.sendString(unknownCommand("SET_CONFIG invalid gameID"));
  		}
  	}
  	
  	public void move(String[] sa) {
  		Game g = gameList.get(Integer.parseInt(sa[1]));
  		String playerName = sa[2];
  		int tileIndex = Integer.parseInt(sa[3]);
  		if(tileIndex == -1) {
  			String boardstring = g.boardstring;
  			g.setCurrentPlayerOther();
  			int currentPlayer = g.currentPlayer;
  			clientServerSocket.sendString(acknowledgeMove());//naar beide
  		}
  		else if (0 <= tileIndex && tileIndex < g.DIM*g.DIM ) {
  			Board b = new Board(g.boardstring, g.DIM);
  			if(b.isEmptyIntersection(tileIndex)) {//validatie
  				String newboardstring = g.updateBoard(playerName, tileIndex, g.boardstring);
  				if(!g.boardHistory.contains(newboardstring)) {//validatie of nieuwboardstring al een keer gemaakt is
  					g.updateBoardHistory(newboardstring);;
  					g.setCurrentPlayerOther();
  					clientServerSocket.sendString(acknowledgeMove());//naar beide
  				}
  				else {
  				clientServerSocket.sendString(invalidMove());//naar 1
  				}
  			}
  			
  		}
  		else {
  			clientServerSocket.sendString(invalidMove());//naar 1
  		}
  	}
  	
  	public void exit(String[] sa) {
  		llll;
  	}
  	
  	public String acknowledgeHandshake() {
  		int gameID = gameList.size();
  		int isLeaderBoolean;
  		if(namePlayerWaiting == null) {
  			isLeaderBoolean = 1;
  		} else {
  			isLeaderBoolean = 0;
  		}
  		String s = "ACKNOWLEDGE_HANDSHAKE+" + gameID + "+" + isLeaderBoolean;
  		return s;
  	}
  	
  	public String requestConfig() {
  		String s = "REQUEST_CONFIG+" + "Please chose your prefered color and prefered board dimension";
  		return s;
  	}
  	
  	public String acknowledgeConfig(String ownPlayerName, int ownPlayerColorIndex, String otherPlayerName, int currentPlayer, int gameID) {
  		Game g = gameList.get(gameID);
  		String boardstring = g.boardstring;	
  		String s = "ACKNOWLEDGE_CONFIG+" + ownPlayerName + "+" + ownPlayerColorIndex + "+" + "PLAYING;" + currentPlayer + ";" + boardstring + "+" + otherPlayerName;
  		return s;
  	}
  	
  	public String acknowledgeMove() {
  		
  		//ACKNOWLEDGE_MOVE+$GAME_ID+$MOVE+$GAME_STATE
  		//ACKNOWLEDGE_MOVE+1+30;1+PLAYING;2;0000011120001200
  		
  		//als beide gepassed hebben, geef FINISHED als status mee 
  			//gameFinished();
  		llll;
  		
  		String s = "ACKNOWLEDGE_MOVE";
  		return s;
  	}
  	
  	public String invalidMove() {
  		return "this move is invalid";
  	}
  	
  	public String unknownCommand(String s) {
  		return s;
  	}
  	
  	public String gameFinished() {
  		llll;
  		return null;
  	}
    	
 
    
   /**
	
		
		}
		else if(stringArray[0].equals("SET_CONFIG")) {
				//stringArray[1] = gameID( dus voor die game is het)
				//playerName1, die krijgt preggered_colour (int is colorIndex) stringArray[2]
				// DIM = stringArray[3]
		}
		else if(stringArray[0].equals("MOVE")) {
			//stringArray[1] = game_id
			//stringArray[2] = de playername die een move zet (daarmee heb je kleur gekoppelt)
			//stringArray[3] =  tile index (cerander die tile naar kleur van de playername)
							// if -1, hou dan boolean bij da ter 1 heeft gepast.
							//als volgende ook past. end game
							//als volgende niet past, boolean weer op 0
		}
		else if(stringArray[0].equals("EXIT")) {
			//stringArray[1] = game_id
			//stringArray[2] = playername (verliezer van het spel)
			//update_status
			//gameFinished()
		}
	}
    
    public String acknowledgeHandshake() {//voor 1 persoon
    	return null;
    }
    public String requestConfig() {//voor 1 prsoon
    	return null;
    }
    public String acknowledgeConfig() {//voor allebei
    	return null;
    }
    public String acknowledgeMove() {//voor allebei
    	return null;
    }
    public String invalidMove() {//voor een persoon
    	return null;
    }
    public String unknownCommand() {//voor een persoon
    	return null;
    }
    public String updateStatus() {//voor allebei
    	return null;
    }
    public String gameFinished() {//voor allebei
    	//gameID is stop
    	//bereken score
    	//als exit boolean van game is aan, 
    		//dan losername=exit persoon, winner is de andere
    	//als beide gepasst
    		//winner = meeste punten, ander is loser
    	//stuur string
    	
    	return null;
    }
    */

}

