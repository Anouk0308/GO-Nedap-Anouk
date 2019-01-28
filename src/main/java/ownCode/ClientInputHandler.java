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

	
	public ClientInputHandler(Server s) {
		this.server = server;
	}
	
    //split de serverstring in een array
  	public String[] clientStringSplitter(String clientString) {
  		this.clientString = clientString;
  		String[] stringArray = clientString.split("+");
  		return stringArray;
  	}
  	
  
  //analyseerd welk commando het is, en stuurt door naar de goede methode
  	public void stringArrayAnalyser(String[] sa, ClientHandler ch) {
  		String s = sa[0];
  		switch(s) {
  			case "HANDSHAKE":				handshake(sa, ch);
  											break;
  			case "SET_CONFIG":				setConfig(sa, ch);
  											break;
  			case "MOVE":					move(sa, ch);
  											break;
  			case "EXIT":					exit(sa, ch);
  											break;
  			case "SET_REMATCH":				setRematch(sa, ch);
											break;
  			default:						System.out.println("The client sent an invalid command");
  											break;
  		}
  	}
  	
  	public void handshake(String[] sa, ClientHandler ch) {
  		playerName = sa[1];

  		ch.sendMessage(acknowledgeHandshake());//deze ch alleen
  		
  		if(server.playerNames.contains(playerName)){
  			playerName = playerName+"1";
  		}
  		server.playerNames.add(playerName);
  		matchingPlayers(playerName, ch);
  	}
	
    public void matchingPlayers(String playerName, ClientHandler ch) {//maakt spelletjes mogelijk voor 2 mensen
    	if(server.namePlayerWaiting == null) {
    		server.namePlayerWaiting = playerName;
    		server.chPlayerWaiting = ch;
    		ch.sendMessage(requestConfig());//naar 1
    	}
    	else {
    		lock.lock();
    		try {
	    		g = createNewGame(server.namePlayerWaiting, playerName);
	    		g.player1CH = server.chPlayerWaiting;
	    		g.player2CH = ch;
	    		int gameID = server.gameList.size();
	    		server.gameList.add(g);
	    		
	    		String player1Name = server.namePlayerWaiting;//moet ik nog naar kijken
	    		int player1ColorIndex = g.player1ColorIndex; 
	    		String player2Name = playerName;
	    		int player2ColorIndex = g.player2ColorIndex; 
	    		int currentPlayer = server.gameList.get(gameID).currentPlayer;
	    		g.player1CH.sendMessage(acknowledgeConfig(player1Name, player1ColorIndex, player2Name, currentPlayer, gameID));
	    		g.player2CH.sendMessage(acknowledgeConfig(player2Name, player2ColorIndex, player1Name, currentPlayer, gameID));
	    		server.namePlayerWaiting = null;
	    		g = null;
    		} finally {
    			lock.unlock();
    		}
    	}
    }
  	
    
    public Game createNewGame(String playerName1, String playerName2) {
    	int gameID = server.gameList.size();
    	Game ng = new Game(playerName1, requestPlayerColorIndex, playerName2, requestDIM, gameID);
    	return ng;
    }
    
 
  	public void setConfig(String[] sa, ClientHandler ch) {
  		//Game g = server.gameList.get(Integer.parseInt(sa[1]));
	  	this.requestPlayerColorIndex = Integer.parseInt(sa[2]);
	  	this.requestDIM = Integer.parseInt(sa[3]);

  	}
  	
  	public void move(String[] sa, ClientHandler ch) {
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
	  			
	  			g.player1CH.sendMessage(acknowledgeMove(gameID, move, gameState));//naar beide
	  			g.player2CH.sendMessage(acknowledgeMove(gameID, move, gameState));
  			}
  			else {
  				int gameID = g.gameID;
  				Score score = g.score(g.boardstring, g.DIM);
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
				
				g.player1CH.sendMessage(acknowledgeMove(gameID, move, gameState));//naar beide
				g.player2CH.sendMessage(acknowledgeMove(gameID, move, gameState));
  				g.player1CH.sendMessage(gameFinishedPasses(gameID, winner, score, message));//naar beide
  				g.player2CH.sendMessage(gameFinishedPasses(gameID, winner, score, message));
  				g.player1CH.sendMessage(requestRematch());//naar beide //rematch is alleen als spel op goede manier is afgelopen
  				g.player2CH.sendMessage(requestRematch());//naar beide //rematch is alleen als spel op goede manier is afgelopen
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
  					
  					g.player1CH.sendMessage(acknowledgeMove(gameID, move, gameState));//naar beide
  					g.player2CH.sendMessage(acknowledgeMove(gameID, move, gameState));//naar beide
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
  	
  	public void exit(String[] sa, ClientHandler ch) {
  		int gameID = Integer.parseInt(sa[1]);
  		Game g = server.gameList.get(gameID);
  		String loser = sa[2];
  		String winner = g.getPlayerNameOther(loser);
  		String message = "The other has exited";
  		Score score = g.score(g.boardstring, g.DIM);
  		ClientHandler ch2 = g.getPlayerCHOther(ch);
  		ch2.sendMessage(gameFinishedExit(gameID, winner, score, message));
  	}
  	
  	public void setRematch(String[] sa, ClientHandler ch) {
  		int answerThisCH = Integer.parseInt(sa[1]);
  		int getGame = -1;
  		for(int i = 0; i < server.gameList.size(); i++) {
  			g = server.gameList.get(i);
  			if(g.player1CH == ch || g.player2CH == ch) {
  				getGame = i;
  			}
  		}
  		if(getGame != -1) {
  			g = server.gameList.get(getGame);
  		}
  		
  		g.rematchOrNot(answerThisCH);
  		
  		if(g.twoAnswers = true) {//dit gebeurt pas als tweede player ook antwoord heeft gegeven
  				rematchAnswer(g);
  		}
  		
  	}
  	
  	public void rematchAnswer(Game gg) {
  		Game g = gg;
  		int answerGame;
  		Game ng;
  		
  		if(g.rematch = true) {
	  		answerGame = 1;
  	  		g.player1CH.sendMessage(acknowledgeRematch(answerGame));//allebei
  			g.player2CH.sendMessage(acknowledgeRematch(answerGame));//allebei
  			
  			ng = new Game(g.player1Name, g.player1ColorIndex, g.player2Name, g.DIM, g.gameID);
  			int gameID = server.gameList.size();
  			server.gameList.add(ng);
  		
  			String player1Name = ng.player1Name;
  			int player1ColorIndex = ng.player1ColorIndex; 
  			String player2Name = ng.player2Name;
  			int player2ColorIndex = ng.player2ColorIndex; 
  			int currentPlayer = ng.currentPlayer;
  			ng.player1CH.sendMessage(acknowledgeConfig(player1Name, player1ColorIndex, player2Name, currentPlayer, gameID));
  			ng.player2CH.sendMessage(acknowledgeConfig(player2Name, player2ColorIndex, player1Name, currentPlayer, gameID));
  			
  			g = null;
	  		}
	  		else {
	  			answerGame = 0;
  	  		g.player1CH.sendMessage(acknowledgeRematch(answerGame));//allebei
  			g.player2CH.sendMessage(acknowledgeRematch(answerGame));//allebei
	  		}
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
  	public String acknowledgeRematch(int answer) {
  		String s = "REQUEST_REMATCH+"+ answer;
  		return s;
  	}   

}
