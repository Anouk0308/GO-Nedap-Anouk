package ownCode;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class ClientInputHandler {

	public String clientString;
	public String namePlayerWaiting = null;
	public String playerName;
	
	
	private List<Game> gameList = new ArrayList<Game>();
	private Game g;
	
	//gamelist moet in Server
	
	
	private int requestDIM;
	private int requestPlayerColorIndex;
	private int gameID;
	private ReentrantLock lock = new ReentrantLock();
	private ClientHandler ch;
	private Socket sock;

	
	public ClientInputHandler(ClientHandler ch, Socket sock) {
		this.ch = ch;
		this.sock = sock;
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
  		ch.sendMessage(acknowledgeHandshake());
  		playerName = sa[1];
  		matchingPlayers(playerName);
  	}
	
    public void matchingPlayers(String playerName) {//maakt spelletjes mogelijk voor 2 mensen
    	if(namePlayerWaiting == null) {
    		namePlayerWaiting = playerName;
    		ch.sendMessage(requestConfig());//naar 1
    	}
    	else {
    		lock.lock();
    		try {
	    		if(playerName.equals(namePlayerWaiting)) {
	    			playerName = playerName+1;//niet dezelfde naam in 1 game
	    		}
	    		g = createNewGame(namePlayerWaiting, playerName);
	    		gameList.add(g);
	    		int gameID = gameList.size()-1;
	    		
	    		//stuur volgende naar beide, alleen dus een andere regel naar ander persoon
	    		lllllll;
	    		String ownPlayerName = "";//moet ik nog naar kijken
	    		int ownPlayerColorIndex = 0; 
	    		String otherPlayerName = "";
	    		int currentPlayer = gameList.get(gameID).currentPlayer;
	    		ch.sendMessage(acknowledgeConfig(ownPlayerName, ownPlayerColorIndex, otherPlayerName, currentPlayer, gameID));
	    		
	    		namePlayerWaiting = null;
	    		g = null;
    		} catch (IOException e) {
    			System.out.println(e.getMessage());
    		} finally {
    			lock.unlock();
    		}
  
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
  			ch.sendMessage(unknownCommand("SET_CONFIG invalid gameID"));
  		}
  		
  		
  	//stringArray[1] = gameID( dus voor die game is het)
		//playerName1, die krijgt preggered_colour (int is colorIndex) stringArray[2]
		// DIM = stringArray[3]
  	}
  	
  	public void move(String[] sa) {
  		Game g = gameList.get(Integer.parseInt(sa[1]));
  		String playerName = sa[2];
  		int tileIndex = Integer.parseInt(sa[3]);
  		if(tileIndex == -1) {
  			String boardstring = g.boardstring;
  			g.setCurrentPlayerOther();
  			int currentPlayer = g.currentPlayer;
  			ch.sendMessage(acknowledgeMove());//naar beide
  			
  			
  		// if -1, hou dan boolean bij da ter 1 heeft gepast.
			//als volgende ook past. end game
			//als volgende niet past, boolean weer op 0
  		}
  		else if (0 <= tileIndex && tileIndex < g.DIM*g.DIM ) {
  			Board b = new Board(g.boardstring, g.DIM);
  			if(b.isEmptyIntersection(tileIndex)) {//validatie
  				String newboardstring = g.updateBoard(playerName, tileIndex, g.boardstring, g.DIM);
  				if(!g.boardHistory.contains(newboardstring)) {//validatie of nieuwboardstring al een keer gemaakt is
  					g.updateBoardHistory(newboardstring);;
  					g.setCurrentPlayerOther();
  					ch.sendMessage(acknowledgeMove());//naar beide
  				}
  				else {
  				ch.sendMessage(invalidMove());//naar 1
  				}
  			}
  			
  		}
  		else {
  			ch.sendMessage(invalidMove());//naar 1
  		}
  		
  		
 
						
  	}
  	
  	public void exit(String[] sa) {
  		llll;
  		
  	//stringArray[1] = game_id
		//stringArray[2] = playername (verliezer van het spel)
		//update_status
		//gameFinished()
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
  		//geen 1 en 2 sturen in scoren
  		//Game finished: exit, disconnect, twee passes
  		//Na GAME_FINISH stuurt server REQUEST_REMATCH om te vragen of je nog een keer wilt spelen
  		
  		
  		
  		//gameID is stop
    	//bereken score
    	//als exit boolean van game is aan, 
    		//dan losername=exit persoon, winner is de andere
    	//als beide gepasst
    		//winner = meeste punten, ander is loser
    	//stuur string
  		
  		
  		return null;
  	}
    	

    

}
