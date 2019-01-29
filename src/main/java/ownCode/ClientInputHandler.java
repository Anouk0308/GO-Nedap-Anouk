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
	private int gameID;
	private ReentrantLock lock = new ReentrantLock();
	private Server server;
	private Socket sock;

	
	public ClientInputHandler(Server s) {
		this.server = s;
	}
	
    //split de serverstring in een array
  	public String[] clientStringSplitter(String clientString) {
  		this.clientString = clientString;
  		System.out.println("commando received from client: " + clientString);
  		String[] stringArray = clientString.split("\\+");
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
    			int gameID = server.gameList.size();
    			Game game = new Game(server.namePlayerWaiting, server.requestPlayerColorIndex, playerName, server.requestDIM, gameID);
	    		System.out.println("test: is deze game in matchingPlayers leeg?" + game);//niet leeg
    			System.out.println("test: en zn boardstring?" + game.boardstring);//wel leeg?????
    			System.out.println("test: en player1color?" + game.player1ColorIndex);//wel leeg????
    			System.out.println("test: en this.requestplayercolor?" + server.requestPlayerColorIndex);//wel leeg?????
    			System.out.println("test: en this.requestDIM?" + server.requestDIM);//wel leeg????
    			
    			game.player1CH = server.chPlayerWaiting;
	    		game.player2CH = ch;
	    		
	    		server.gameList.add(game);
	    		
	    		String player1Name = server.namePlayerWaiting;//moet ik nog naar kijken
	    		int player1ColorIndex = game.player1ColorIndex; 
	    		String player2Name = playerName;
	    		int player2ColorIndex = game.player2ColorIndex; 
	    		int currentPlayer = game.currentPlayer;
	    		game.player1CH.sendMessage(acknowledgeConfig(game, player1Name, player1ColorIndex, player2Name, currentPlayer, gameID));
	    		game.player2CH.sendMessage(acknowledgeConfig(game, player2Name, player2ColorIndex, player1Name, currentPlayer, gameID));
	    		server.namePlayerWaiting = null;
	    		game = null;
    		} finally {
    			lock.unlock();
    		}
    	}
    }
  	
  	public void setConfig(String[] sa, ClientHandler ch) {
  		//setConfig komt neit goed aan
  		//SET_CONFIG+0+1+4 is wat client stuurt
  		int rpci = Integer.parseInt(sa[2]);
	  	server.requestPlayerColorIndex = Integer.parseInt(sa[2]);
	  	int rdim = Integer.parseInt(sa[3]); 
	  	server.requestDIM = Integer.parseInt(sa[3]);
	  	System.out.println("test: kijk of requestplycolorinx en requestDIM aankomen" + rpci + rdim);

  	}
  	
  	public void move(String[] sa, ClientHandler ch) {
  		System.out.println("test: wat is sa[1]" + sa[1]);//klopt
  		System.out.println("test: wat is sa[2]" + sa[2]);//klopt
  		System.out.println("test: wat is sa[3]" + sa[3]);//klopt
  		
  		
  		Game g = server.gameList.get(Integer.parseInt(sa[1]));
  		System.out.println("test: is toevallig deze game leeg?" + g);//niet leeg
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
  				System.out.println("test: er is twee keer gepasst");
  				int gameID = g.gameID;
  				System.out.println("test: gameID"+gameID);
  				Score score = g.score(g.boardstring, g.DIM);
  				System.out.println("test: score" + score);
  				String winner = g.winner(score);
  				System.out.println("test: winner"+winner);
  				String message = "The game is finished";
  				System.out.println("test: we kunnen nu acknowledgeMove voor de laatste keer sturen");
  				
  				
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
  			System.out.println("test: wat is g.DIM?" + g.DIM);
  			g.onePass = false;
  			Board b = new Board(g.boardstring, g.DIM);
  			if(b.isEmptyIntersection(tileIndex)) {//validatie
  				String newboardstring = g.updateBoard(playerName, tileIndex, g.boardstring, g.DIM);
  				
  				System.out.println("test: wat is playername?"+ playerName);
  				System.out.println("test: wat is player1Index?"+ g.player1ColorIndex);
  				System.out.println("test: wat is player2Index?"+ g.player2ColorIndex);
  				System.out.println("test: wat is tileIndex?"+ tileIndex);
  				System.out.println("test: wat is g.boardstring?"+ g.boardstring);
  				System.out.println("test: wat is g.DIM?"+ g.DIM);
  				System.out.println("test: wat is newboardstring?"+ newboardstring);
  				
  				if(g.boardHistory == null || !g.boardHistory.contains(newboardstring)) {//validatie of nieuwboardstring al een keer gemaakt is
  					g.updateBoardHistory(newboardstring);
  					g.boardstring = newboardstring;
  					g.setCurrentPlayerOther();
  					
  					int gameID = g.gameID;
  					int playerColorIndex = g.getPlayerColor(playerName);
  					String moveString = tileIndex + ";" + playerColorIndex;
  					Move move = new Move(moveString);
  					Status status = Status.PLAYING;
  					int currentPlayer = g.currentPlayer;
  					String boardstring = newboardstring;
  					String gameStateString = status + ";" + currentPlayer + ";" + boardstring;
  					GameState gameState = new GameState(gameStateString);
  					
  					System.out.println("test: wat is gameID?"+ gameID);
  					System.out.println("test: wat is move?"+move);
  					System.out.println("test: wat is gamestate?"+gameState);
  					
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
  			ng.player1CH.sendMessage(acknowledgeConfig(ng, player1Name, player1ColorIndex, player2Name, currentPlayer, gameID));
  			ng.player2CH.sendMessage(acknowledgeConfig(ng, player2Name, player2ColorIndex, player1Name, currentPlayer, gameID));
  			
  			g = null;
	  		}
	  		else {
	  			answerGame = 0;
  	  		g.player1CH.sendMessage(acknowledgeRematch(answerGame));//allebei
  			g.player2CH.sendMessage(acknowledgeRematch(answerGame));//allebei
	  		}
  	}

  	public String acknowledgeHandshake() {
  		System.out.println("test: server =" + server);
  		if(server.gameList == null) {
  			gameID = 0;
  		} else {
  			gameID = server.gameList.size();
  		}
  		System.out.println("test: gameID in AH():" + gameID + ", 0 is okee");
  		int isLeaderBoolean;
  		if(server.namePlayerWaiting == null) {
  			isLeaderBoolean = 1;
  		} else {
  			isLeaderBoolean = 0;
  		}
  		String s = "ACKNOWLEDGE_HANDSHAKE+" + gameID + "+" + isLeaderBoolean;
  		System.out.println("Commando send to client: " + s);
  		return s;
  	}
  
  	public String requestConfig() {
  		String s = "REQUEST_CONFIG+" + "Please chose your prefered color and prefered board dimension";
  		System.out.println("Commando send to client: " + s);
  		return s;
  	}
  	
  	public String acknowledgeConfig(Game g, String ownPlayerName, int ownPlayerColorIndex, String otherPlayerName, int currentPlayer, int gameID) {
  		//ACKNOWLEDGE_CONFIG+Anouk+0+PLAYING;0;+Luuk krijgen we binnen, kleur 0 is fout en gamestate is fout
  		System.out.println("test: vind hij wel een game? " + g);
  		String boardstring = g.boardstring;
  		int DIM = g.DIM;
  		System.out.println("test: vind hij wel boardstring van g? " + boardstring);
  		//anders naar matching partners
  		String s = "ACKNOWLEDGE_CONFIG+" + ownPlayerName + "+" + ownPlayerColorIndex + "+" + DIM + "+" + "PLAYING;" + currentPlayer + ";" + boardstring + "+" + otherPlayerName;
  		System.out.println("Commando send to client: " + s);
  		return s;
  	}
  	
  	public String acknowledgeMove(int gameID, Move move, GameState gameState) {
  		String sgameID = Integer.toString(gameID);
  		String smove = move.toString();
  		String sgameState = gameState.toString();
  		String s = "ACKNOWLEDGE_MOVE+" + sgameID + "+" + smove + "+" + sgameState;
  		System.out.println("Commando send to client: " + s);
  		return s;
  	}
  	
  	public String invalidMove() {
  		String s = "this move is invalid";
  		System.out.println("Commando send to client: " + s);
  		return s;
  	}
  	
  	public String unknownCommand(String s) {
  		System.out.println("Commando send to client: " + s);
  		return s;
  	}
  	
  	public String gameFinishedExit(int gameID, String winner, Score score, String message) {//in geval dat iemand exit drukt
  		String s = "GAME_FINISHED+" + gameID + "+" + winner + "+" + score + "+"+ message;
  		System.out.println("Commando send to client: " + s);
  		return s;
  	}
  	
  	public String gameFinishedPasses(int gameID, String winner, Score score, String message) {
  		String s = "GAME_FINISHED+" + gameID + "+" + winner + "+" + score + "+"+ message;
  		System.out.println("Commando send to client: " + s);
  		return s;
  	}
  	
  	public String requestRematch() {
  		String s = "REQUEST_REMATCH";
  		System.out.println("Commando send to client: " + s);
  		return s;
  	}
  	public String acknowledgeRematch(int answer) {
  		String s = "REQUEST_REMATCH+"+ answer;
  		System.out.println("Commando send to client: " + s);
  		return s;
  	}   

}
