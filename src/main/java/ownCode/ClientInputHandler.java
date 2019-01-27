package ownCode;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class ClientInputHandler {
	public String clientString;
	public String playerName;
	private Game g;
	private int requestDIM;
	private int requestPlayerColorIndex;
	private int gameID;
	private ReentrantLock lock = new ReentrantLock();
	private Server server;
	private Socket sock;
	private List<ClientHandler> threads;

	
	public ClientInputHandler(Server s) {
		this.server = server;
		this.threads = server.threads;
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
  		playerName = sa[1];

  		ch.sendMessage(acknowledgeHandshake());
  		
  		if(server.playerNames.contains(playerName)){
  			playerName = playerName+"1";
  		}
  		server.playerNames.add(playerName);
  		matchingPlayers(playerName);
  	}
	
    public void matchingPlayers(String playerName) {//maakt spelletjes mogelijk voor 2 mensen
    	if(server.namePlayerWaiting == null) {
    		server.namePlayerWaiting = playerName;
    		ch.sendMessage(requestConfig());//naar 1
    	}
    	else {
    		lock.lock();
    		try {
	    		g = createNewGame(server.namePlayerWaiting, playerName);
	    		int gameID = server.gameList.size();
	    		server.gameList.add(g);
	    		
	    		//stuur volgende naar beide, alleen dus een andere regel naar ander persoon
	    		lllllll;
	    		String ownPlayerName = "";//moet ik nog naar kijken
	    		int ownPlayerColorIndex = 0; 
	    		String otherPlayerName = g.getPlayerNameOther(ownPlayerName);
	    		int currentPlayer = server.gameList.get(gameID).currentPlayer;
	    		ch.sendMessage(acknowledgeConfig(ownPlayerName, ownPlayerColorIndex, otherPlayerName, currentPlayer, gameID));
	    		
	    		server.namePlayerWaiting = null;
	    		g = null;
    		} catch (IOException e) {
    			System.out.println(e.getMessage());
    		} finally {
    			lock.unlock();
    		}
  
    	}
    }
  	
    
    public Game createNewGame(String playerName1, String playerName2) {
    	int gameID = server.gameList.size();
    	Game ng = new Game(playerName1, requestPlayerColorIndex, playerName2, requestDIM, gameID);
    	server.gameList.add(ng);
    	return ng;
    }
    
 
  	public void setConfig(String[] sa) {
  		if(this.gameID == Integer.parseInt(sa[1])) {
	  		this.requestPlayerColorIndex = Integer.parseInt(sa[2]);
	  		this.requestDIM = Integer.parseInt(sa[3]);
  		}
  		else {
  			ch.sendMessage(unknownCommand("SET_CONFIG invalid gameID"));
  		}
  	}
  	
  	public void move(String[] sa) {
  		Game g = server.gameList.get(Integer.parseInt(sa[1]));
  		String playerName = sa[2];
  		int tileIndex = Integer.parseInt(sa[3]);
  		if(tileIndex == -1) {
  			if(g.onePass == false) {
	  			String boardstring = g.boardstring;
	  			g.onePass = true;
	  			g.setCurrentPlayerOther();
	  			int currentPlayer = g.currentPlayer;
	  			
	  			int gameID = g.gameID;
				int playerColorIndex = g.getPlayerColor(playerName);
				String moveString = tileIndex + ";" + playerColorIndex;
				Move move = new Move(moveString);
				Status status = Status.PLAYING;
				String gameStateString = status + ";" + currentPlayer + ";" + boardstring;
				GameState gameState = new GameState(gameStateString);
	  			
	  			ch.sendMessage(acknowledgeMove(gameID, move, gameState));//naar beide
  			}
  			else {
  				int gameID = g.gameID;
  				Score score = g.score(g.boardstring);
  				String winner = g.winner(score);
  				String message = "The game is finished";
  				
				int playerColorIndex = g.getPlayerColor(playerName);
				String moveString = tileIndex + ";" + playerColorIndex;
				Move move = new Move(moveString);
				Status status = Status.FINISHED;
				int currentPlayer = g.currentPlayer;
				String boardstring = g.boardstring;
				String gameStateString = status + ";" + currentPlayer + ";" + boardstring;
				GameState gameState = new GameState(gameStateString);
				
  				ch.sendMessage(acknowledgeMove(gameID, move, gameState));//naar beide
  				ch.sendMessage(gameFinishedPasses(gameID, winner, score, message));//naar beide
  				ch.sendMessage(requestRematch());//naar beide //rematch is alleen als spel op goede manier is afgelopen
  			}
  		}
  		else if (0 <= tileIndex && tileIndex < g.DIM*g.DIM ) {
  			g.onePass = false;
  			Board b = new Board(g.boardstring, g.DIM);
  			if(b.isEmptyIntersection(tileIndex)) {//validatie
  				String newboardstring = g.updateBoard(playerName, tileIndex, g.boardstring, g.DIM);
  				if(!g.boardHistory.contains(newboardstring)) {//validatie of nieuwboardstring al een keer gemaakt is
  					g.updateBoardHistory(newboardstring);;
  					g.setCurrentPlayerOther();
  					
  					int gameID = g.gameID;
  					int playerColorIndex = g.getPlayerColor(playerName);
  					String moveString = tileIndex + ";" + playerColorIndex;
  					Move move = new Move(moveString);
  					Status status = Status.PLAYING;
  					int currentPlayer = g.currentPlayer;
  					String boardstring = g.boardstring;
  					String gameStateString = status + ";" + currentPlayer + ";" + boardstring;
  					GameState gameState = new GameState(gameStateString);
  					
  					ch.sendMessage(acknowledgeMove(gameID, move, gameState));//naar beide
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
  		int gameID = Integer.parseInt(sa[1]);
  		Game g = server.gameList.get(gameID);
  		String loser = sa[2];
  		String winner = g.getPlayerNameOther(loser);
  		String message = "The other has exited";
  		Score score = g.score(g.boardstring);
  		gameFinishedExit(gameID, winner, score, message);
  	}

  	public String acknowledgeHandshake() {
  		int gameID = server.gameList.size();
  		int isLeaderBoolean;
  		if(server.namePlayerWaiting == null) {
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
  		Game g = server.gameList.get(gameID);
  		String boardstring = g.boardstring;	
  		String s = "ACKNOWLEDGE_CONFIG+" + ownPlayerName + "+" + ownPlayerColorIndex + "+" + "PLAYING;" + currentPlayer + ";" + boardstring + "+" + otherPlayerName;
  		return s;
  	}
  	
  	public String acknowledgeMove(int gameID, Move move, GameState gameState) {
  		String s = "ACKNOWLEDGE_MOVE+" + gameID + "+" + move + "+" + gameState;
  		return s;
  	}
  	
  	public String invalidMove() {
  		return "this move is invalid";
  	}
  	
  	public String unknownCommand(String s) {
  		return s;
  	}
  	
  	public String gameFinishedExit(int gameID, String winner, Score score, String message) {//in geval dat iemand exit drukt
  		String s = "GAME_FINISHED+" + gameID + "+" + winner + "+" + score + "+"+ message;
  		return s;
  	}
  	
  	public String gameFinishedPasses(int gameID, String winner, Score score, String message) {
  		String s = "GAME_FINISHED+" + gameID + "+" + winner + "+" + score + "+"+ message;
  		return s;
  	}
  	
  	public String requestRematch() {
  		String s = "REQUEST_REMATCH";
  		return s;
  	}
    	

    

}
