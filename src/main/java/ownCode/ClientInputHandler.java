package ownCode;

import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

public class ClientInputHandler {
	public String clientString;
	public String playerName;
	
	private int gameID;
	
	private Game g;
	private ReentrantLock lock = new ReentrantLock();
	private Server server;
	private Socket sock;

	
	public ClientInputHandler(Server s) {
		this.server = s;
	}
	
	public void print(String s) {
		System.out.println(s);
	}
	
    //split de serverstring in een array en stuurt analyser aan
  	public void clientStringSplitter(String clientString, ClientHandler ch) {
  		this.clientString = clientString;
  		print("Commando received from client: " + clientString);
  		String[] stringArray = clientString.split("\\+");
  		stringArrayAnalyser(stringArray, ch);
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
  			default:						print("The client sent an invalid command");
  											break;
  		}
  	}
  	
  	//wanneer de server een handshake binnen krijgt, gaat deze door naar matchingPlayers
  	public void handshake(String[] sa, ClientHandler ch) {
  		playerName = sa[1];

  		ch.sendMessage(acknowledgeHandshake());//deze ch alleen
  		
  		if(server.playerNames.contains(playerName)){
  			playerName = playerName+"1";
  		}
  		server.playerNames.add(playerName);
  		matchingPlayers(playerName, ch);
  	}
	
  	//kijk of er iemand aan het wachten is op een opponent. Nee? dit is de nieuwe wachtende persoon. Ja? voeg ze samen in een game
    public void matchingPlayers(String playerName, ClientHandler ch) {
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
  	
    //wachtende speler geeft aan welke kleur en DIM hij wilt in het volgende spel
  	public void setConfig(String[] sa, ClientHandler ch) {
	  	server.requestPlayerColorIndex = Integer.parseInt(sa[2]);
	  	server.requestDIM = Integer.parseInt(sa[3]);
  	}
  	
  	//als een speler een move gezet heeft:
  	public void move(String[] sa, ClientHandler ch) {
  		Game g = server.gameList.get(Integer.parseInt(sa[1]));
  		String playerName = sa[2];
  		int tileIndex = Integer.parseInt(sa[3]);
  		if(tileIndex == -1) {//speler passt
  			//is deze speler de eerste passer is
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
	  			
	  			g.player1CH.sendMessage(acknowledgeMove(gameID, move, gameState));
	  			g.player2CH.sendMessage(acknowledgeMove(gameID, move, gameState));
  			}
  			//als deze speler de tweede passer is, is het spel afgelopen
  			else if(g.onePass == true){
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
				
				String s1 = acknowledgeMove(gameID, move, gameState);
				String s2 = gameFinishedPasses(gameID, winner, score, message);
				String s3 = requestRematch();
			
				lock.lock();//er gaat wat fout, alleen speler krijgt deze berichten. Wanneer ik tijd over heb, zal ik hier nog een keer naar kijken
				g.player1CH.sendMessage(s1);
  				g.player1CH.sendMessage(s2);
  				g.player1CH.sendMessage(s3);
				g.player2CH.sendMessage(s1);
  				g.player2CH.sendMessage(s2);
  				g.player2CH.sendMessage(s3);
  				lock.unlock();
  				
  			} else {
  				print("Something went wrong when both players passed");
  			}
  		}
  		//als de move op het board ligt
  		else if (0 <= tileIndex && tileIndex < g.DIM*g.DIM ) {
  			g.onePass = false;
  			Board b = new Board(g.boardstring, g.DIM);
  			if(b.isEmptyIntersection(tileIndex)) {//is de intersectie leeg?
  				String newboardstring = g.updateBoard(playerName, tileIndex, g.boardstring, g.DIM);
  				
  				if(g.boardHistory == null || !g.boardHistory.contains(newboardstring)) {//zorgt de move ervoor dat er een board komt, die al een keer eerder is gespeelt?
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
  					
  					g.player1CH.sendMessage(acknowledgeMove(gameID, move, gameState));
  					g.player2CH.sendMessage(acknowledgeMove(gameID, move, gameState));
  				}
  				else {
  				ch.sendMessage(invalidMove());//alleen naar de speler die de move heeft gezet
  				}
  			}
  			
  		}
  		else {
  			ch.sendMessage(invalidMove());
  		}				
  	}
  	
  	//wanneer de speler EXIT typt, moet de ander weten dat het spel over is
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
  	
  	//bekijk het antwoord van de spelers op de vraag of ze nog een potje willen spelen
  	public void setRematch(String[] sa, ClientHandler ch) {
  		int answerfromThisCH = Integer.parseInt(sa[1]);
  		int getGame = -1;
  		
  		//vind de game waar deze ch in zit
  		for(int i = 0; i < server.gameList.size(); i++) {
  			g = server.gameList.get(i);
  			if(g.player1CH == ch || g.player2CH == ch) {
  				getGame = i;
  			}
  		}
  		
  		if(getGame != -1) {//dus er is een spel gevonden
  			g = server.gameList.get(getGame);
  		} else {
  			print("Cannot find the game with this Client");
  		}
  		
  		g.rematchOrNot(answerfromThisCH);//moet onthouden wat ze allebei willen
  		
  		if(g.twoAnswers = true) {//dit gebeurt pas als tweede player ook antwoord heeft gegeven
  				rematchAnswer(g);
  		}
  		
  	}
  	
  	public void rematchAnswer(Game game) {
  		Game g = game;
  		int answerGame;
  		Game ng;
  		
  		if(g.rematch = true) { // allebei hebben gezegd dat ze een rematch willen
	  		answerGame = 1;
  	  		g.player1CH.sendMessage(acknowledgeRematch(answerGame));
  			g.player2CH.sendMessage(acknowledgeRematch(answerGame));
  			
  			ng = new Game(g.player1Name, g.player1ColorIndex, g.player2Name, g.DIM, g.gameID);
  			int gameID = server.gameList.size();
  			ng.gameID = gameID;
  			server.gameList.add(ng);
  		
  			String player1Name = ng.player1Name;
  			int player1ColorIndex = ng.player1ColorIndex; 
  			String player2Name = ng.player2Name;
  			int player2ColorIndex = ng.player2ColorIndex; 
  			int currentPlayer = ng.currentPlayer;
  			ng.player1CH.sendMessage(acknowledgeConfig(ng, player1Name, player1ColorIndex, player2Name, currentPlayer, gameID));
  			ng.player2CH.sendMessage(acknowledgeConfig(ng, player2Name, player2ColorIndex, player1Name, currentPlayer, gameID));
  			
  			g = null;
  		} else { //een of allebei willen niet een rematch spelen
	  		answerGame = 0;
  	  		g.player1CH.sendMessage(acknowledgeRematch(answerGame));
  			g.player2CH.sendMessage(acknowledgeRematch(answerGame));
	  	}
  	}

  	//stuur deze string als de handshake van de client goed is aangekomen
  	public String acknowledgeHandshake() {
  		if(server.gameList == null) {
  			gameID = 0;
  		} else {
  			gameID = server.gameList.size();
  		}
  		int isLeaderBoolean;
  		if(server.namePlayerWaiting == null) {
  			isLeaderBoolean = 1;
  		} else {
  			isLeaderBoolean = 0;
  		}
  		String s = "ACKNOWLEDGE_HANDSHAKE+" + gameID + "+" + isLeaderBoolean;
  		print("Commando send to client: " + s);
  		return s;
  	}
  
  	//stuur deze string als de speler mag kiezen welke kleur hij/zij speelt en hoe groot het board moet zijn
  	public String requestConfig() {
  		String s = "REQUEST_CONFIG+" + "Please chose your prefered color and prefered board dimension";
  		print("Commando send to client: " + s);
  		return s;
  	}
  	
  	//stuur deze string met het spelConfig
  	public String acknowledgeConfig(Game g, String ownPlayerName, int ownPlayerColorIndex, String otherPlayerName, int currentPlayer, int gameID) {
  		String boardstring = g.boardstring;
  		int DIM = g.DIM;
  		String s = "ACKNOWLEDGE_CONFIG+" + ownPlayerName + "+" + ownPlayerColorIndex + "+" + DIM + "+" + "PLAYING;" + currentPlayer + ";" + boardstring + "+" + otherPlayerName;
  		print("Commando send to client: " + s);
  		return s;
  	}
  	
  	//stuur deze string met de acknowledged move
  	public String acknowledgeMove(int gameID, Move move, GameState gameState) {
  		String sgameID = Integer.toString(gameID);
  		String smove = move.toString();
  		String sgameState = gameState.toString();
  		String s = "ACKNOWLEDGE_MOVE+" + sgameID + "+" + smove + "+" + sgameState;
  		print("Commando send to client: " + s);
  		return s;
  	}
  	
  	//stuur deze string als het een invalid move is
  	public String invalidMove() {
  		String s = "this move is invalid";
  		print("Commando send to client: " + s);
  		return s;
  	}
  	
  	//stuur deze string als de commando van de client unknown is
  	public String unknownCommand(String s) {
  		print("Commando send to client: " + s);
  		return s;
  	}
  	
  	//stuur deze string in het geval een van de players EXIT heeft getypt
  	public String gameFinishedExit(int gameID, String winner, Score score, String message) {
  		String sscore = score.toString();
  		String s = "GAME_FINISHED+" + gameID + "+" + winner + "+" + sscore + "+"+ message;
  		print("Commando send to client: " + s);
  		return s;
  	}
  	
  	//stuur deze string als beide spelers gepassed hebben
  	public String gameFinishedPasses(int gameID, String winner, Score score, String message) {
  		String sscore = score.toString();
  		String s = "GAME_FINISHED+" + gameID + "+" + winner + "+" + sscore + "+"+ message;
  		print("Commando send to client: " + s);
  		return s;
  	}
  	
  	//stuur deze string als je het hen mogelijk wilt maken of een rematch te spelen (alleen als de game gefinished is met 2 passes)
  	public String requestRematch() {
  		String s = "REQUEST_REMATCH";
  		print("Commando send to client: " + s);
  		return s;
  	}
  	
  	//stuur deze string als beide antwoorden binnen zijn op de vraag of er een rematch komt (int answer = 0 betekend dat minimaal 1 speler geen rematch wilt)
  	public String acknowledgeRematch(int answer) {
  		String s = "ACKNOWLEDGE_REMATCH+"+ answer;
  		print("Commando send to client: " + s);
  		return s;
  	}   

}
