package ownCode;

import com.nedap.go.gui.*;

import java.io.BufferedReader;
import java.io.IOException;

public class ServerInputHandler {
	public String playerName;
	public String opponentName;
	public String serverMessage;
	public String winner;
	public String boardstring;
	
	public int DIM;
	public int tileIndex;
	public int pointsBlack;
	public int currentPlayer;
	public int gameID;
	public int playerColorIndex;
	public double pointsWhite;
	
	public boolean isLeader;
	public boolean useTUI;

	public Intersection playerColor;
	public GameState gameState;
	public Status status;
	public Move move;
	public Score score;
	public Player p;
	public Strategy g;
	public GameBrain gb;
	public BufferedReader userInput;
	public Client c;
	public GoGuiIntegrator gogui;
	
	public ServerInputHandler(BufferedReader userInput, Client c) {
		this.c = c;
		this.userInput = userInput;
	}
	
	//split de serverstring in een array en stuurt analyser aan
	public void serverStringSplitter(String serverString) {
		String[] stringArray = serverString.split("\\+");
		stringArrayAnalyser(stringArray);
	}
	
	//analyseerd welk commando het is, en stuurt door naar de goede methode
	public void stringArrayAnalyser(String[] sa) {
		String s = sa[0];
		switch(s) {
			case "ACKNOWLEDGE_HANDSHAKE":	acknowledgeHandshake(sa);
											break;
			case "REQUEST_CONFIG":			requestConfig(sa);
											break;
			case "ACKNOWLEDGE_CONFIG":		acknowledgeConfig(sa);
											break;
			case "ACKNOWLEDGE_MOVE":		acknowledgeMove(sa);
											break;
			case "INVALID_MOVE":			invalidMove(sa);
											break;
			case "UNKNOWN_COMMAND":			unknownCommand(sa);
											break;
			case "GAME_FINISHED":			gameFinished(sa);
											break;
			case "REQUEST_REMATCH":			requestRematch(sa);
											break;
			default:						System.out.println("The server sent an unknown command");
											break;
		}
	}
	
	//laat user weten dat de handshake is acknowledged door de server
	public void acknowledgeHandshake(String[] sa) {
		this.gameID = Integer.parseInt(sa[1]);
		if(Integer.parseInt(sa[2]) == 0) {
			this.isLeader = false;
		} else if(Integer.parseInt(sa[2]) == 1) {
			this.isLeader = true;
		} else {
			print("Something went wrong with the acknowledgeHandshake command. No isLeader is given");
		}
		print("Connected with the server");
		print("Wait till more information");
	}
	
	//laat de user weten dat er nog een 2e player moet connecten om een spel te spelen
	public void requestConfig(String[] sa) {
		this.serverMessage = sa[1];
		print(serverMessage);
		try {
			String s = setConfig();
		c.sendMessage(s);
		}catch (IOException e) {
            e.printStackTrace();
        }
		print("Wait till another player");
	}
	
	//vertelt de user zijn playername, zijn kleur, hoe groot het board is en wie de opponent is
	public void acknowledgeConfig(String[] sa) {
		p = null;
		Strategy ns;
		
		this.playerName = sa[1];
		
		if(Integer.parseInt(sa[2])==1){
			this.playerColorIndex = 1;
			this.playerColor = playerColor.BLACK;
			print("Your color is black");
		} else if(Integer.parseInt(sa[2])==2){
			this.playerColorIndex = 2;
			this.playerColor = playerColor.WHITE;
			print("Your color is white");
		} else {
			print("Something went wrong with the acknowledgeConfig command. I don't know what your colour is");
		}
		
		this.DIM = Integer.parseInt(sa[3]);
			print("The board is " + DIM + " by " + DIM + " size.");
		
		if(c.getWhichPlayerIndexChoice() == 1) {
			p = new HumanPlayer(playerName, playerColor);
		}
		else if(c.getWhichPlayerIndexChoice() == 2) {
			ns = new NaiveStrategy();
			p = new ComputerPlayer(playerColor, ns);
		} else {
			print("Something went wrong. I don't know if your human or a computer");
		}
		
		this.gameState = new GameState(sa[4]);
			//this.status = this.gameState.status; gaat fout
			this.currentPlayer = this.gameState.currentPlayer;
			this.boardstring = this.gameState.boardstring;
			UI(boardstring, DIM);
			
		this.opponentName = sa[5];
			print("You will play to " + opponentName);
		
			
			
		if(this.currentPlayer == this.playerColorIndex) {
			gb = new GameBrain(boardstring, DIM, p);
			gb.updateBoardHistory(boardstring);
			c.sendMessage(move(gb,boardstring));
			print("Wait till the other places a stone");//wacht tot eigen zet is acknowledged, de ander een zet heeft gezet en die zet is acknowledged
		} else {
			gb = new GameBrain(boardstring, DIM, p);
			print("It is the other player's turn");
			print("Wait till the other places a stone");//wacht tot een acknowledgement van de opponents move
		}
	}
	
	//dealt met de acknowledgeMove commando
	public void acknowledgeMove(String[] sa) {
			Move move = new Move(sa[2]);
				int tileIndex = move.tileIndex;
				int playerColorIndex = move.playerColorIndex;
			GameState gameState = new GameState(sa[3]);
				int currentPlayer = gameState.currentPlayer;
				String boardstring = gameState.getBoardstring();
				
			//als de user de current player is en ander heeft niet gepast
			if(currentPlayer == this.playerColorIndex && tileIndex != -1) {
				UI(boardstring, this.DIM);
				gb.updateBoardHistory(boardstring); //tegenstander heeft een nieuw board gemaakt
				c.sendMessage(move(gb,boardstring)); //ik ben aan zet en stuur het door naar de server
				print("Wait till the other places a stone"); //wacht tot eigen zet is acknowledged, de ander een zet heeft gezet en die zet is acknowledged
			}
			//als de user de current player is en andere heeft wel gepast
			else if(currentPlayer == this.playerColorIndex && tileIndex == -1) {
				UI(boardstring, this.DIM);//de tegenstander heeft geen nieuw board aan gemaakt
				print("The other has passed");
				c.sendMessage(move(gb,boardstring)); //ik ben aan zet en stuur het door naar de server
				print("Wait till the other places a stone");//wacht tot eigen zet is acknowledged, de ander een zet heeft gezet en die zet is acknowledged
			}
			//als de user niet de current player is ben en ik heb niet gepast
			else if(currentPlayer != this.playerColorIndex && tileIndex != -1) {
				UI(boardstring, DIM);
				gb.updateBoardHistory(boardstring); //mijn zet is geaccepteerd en maakt een nieuw board
				print("Wait till the other places a stone");//wacht tot een acknowledgement van de opponents move
			}
			//als de user niet de current player is ben en ik heb wel gepast
			else if(currentPlayer != this.playerColorIndex && tileIndex != -1) {
				UI(boardstring, DIM);
				//mijn zet is geaccepteerd maar ik maak geen nieuw board
				print("Wait till the other places a stone");//wacht tot een acknowledgement van de opponents move
			}	
	}
	
	//wanneer de server vind dat het een foute move is, probeer dan een nieuwe move te maken
	public void invalidMove(String[] sa) {
		this.serverMessage = sa[1];
		print(serverMessage);
		print("The server finds it an invalid move");
		c.sendMessage(move(gb,boardstring));
	}
	
	//wanneer de server iets stuurt wat niet klopt
	public void unknownCommand(String[] sa) {
		this.serverMessage = sa[1];
		print("The server does not recognise a command");
		print("please go cry to Anouk, because I also don't know what to do :(");
	}
	
	//wanneer de server aangeeft dat het spel is afgelopen
	public void gameFinished(String[] sa) {
		if(this.gameID == Integer.parseInt(sa[1])) {
			this.winner = sa[2];
			this.score = new Score(sa[3]);
				this.pointsBlack = this.score.pointsBlack;
				this.pointsWhite = this.score.pointsWhite;
			this.serverMessage = sa[4];
		} else {
			print("The server doesn't no which game your playing! Go kick the server till he knows!");
		}
		print("The winner is:" + winner);
		print("Points for black:" + Integer.toString(pointsBlack)+" - points for white:" + Double.toString(pointsWhite));
		print(serverMessage); 
	}
	
	//geef de mogelijkheid om nog een potje te spelen
	public void requestRematch(String[] sa) {
		print("would you like to play a new game?");
		print("1 for yes, 0 for no");
		try {
			String answer = userInput.readLine();
			if(answer == "0" || answer == "1") {
					setRematch(Integer.parseInt(answer));
			} else {
				print("that is not a 0 or 1, try again");
				requestRematch(sa);
			}
		} catch(IOException e) {
			print(e.getMessage());
		}
	}
	
	//probeer te connecten met de server
	public String handshake() {
		String s = "HANDSHAKE+"+playerName;
		return s;
	}
	
	//geef aan welke kleur je wilt spelen en hoe groot het board moet zijn
	public String setConfig() throws IOException {
		print("Okay, which color would you like to use? 1 for black and 2 for white");
		String userString = userInput.readLine();
		int thisInt = Integer.parseInt(userString);
			if(userInput != null) {
				if(thisInt == 1 || thisInt == 2) {
					this.playerColorIndex = thisInt;
				} else {
					print("No sillybilly, that is not a 1 or 2, try again");
					setConfig();
				}
			} else {//default
				this.playerColorIndex = 2;
			}
		print("Great, what is your perfered board dimension?");
			if(userInput != null) {
				this.DIM = Integer.parseInt(userInput.readLine());
			} else {//default
				this.DIM = 7;
			}
		String s = "SET_CONFIG+"+Integer.toString(gameID)+"+"+Integer.toString(playerColorIndex)+"+"+Integer.toString(DIM); 
		return s; 
	}
	
	//bepaalt een move
	public String move(GameBrain gb, String boardstring) {
		if(gb.p instanceof ComputerPlayer) { 
			
			//zorg ervoor dat een move niet te lang duurt
			long start = System.currentTimeMillis();
			long end = start + c.moveTime * 1000; // moveTime bepaald in selectiemenu op het begin * 1000 ms/sec
			while (System.currentTimeMillis() < end)
			{
				this.tileIndex = gb.setMove(boardstring);
			}	
		
		} else {
			print("Where would you like to place a new stone?");
			try {
				if(userInput != null) {
					int thisInt = Integer.parseInt(userInput.readLine());
					this.tileIndex = thisInt;
					if(gb.validMove(tileIndex)|| tileIndex == -1) {
						print("this is a valid move");
					} else {
						print("this is not a valid move, please enter a new number");
						move(gb,boardstring);
					}
				} else {
					print("this is not a valid move, please enter a new number");
					move(gb,boardstring);
				}
			} catch (IOException e) {
	            e.printStackTrace();
	        }
		}
		String s = "MOVE"+"+"+Integer.toString(gameID)+"+"+playerName+"+"+Integer.toString(tileIndex);
		return s;
	}
	
	//wanneer een user EXIT typt
	public String exit() {
		String s = "EXIT"+"+"+Integer.toString(gameID)+"+"+playerName;
		return s; 
	}
	
	//wanneer er gevraagd is of de user een rematch wilt, moet dit antwoord terug worden gestuurd naar de server
	public String setRematch(int answer) {
		String s = "SET_REMATCH+" + answer;
		return s;
	}

//kiest goede UI en laat het board zien
	public void UI(String boardstring, int DIM) {
		System.out.println("test: boardstring" + boardstring);
		System.out.println("test: DIM" + DIM);
		System.out.println("test: P"+ p);
		//creer ook hint op board, via computerplayer
		Board b = new Board(boardstring, DIM);
		Strategy g = new NaiveStrategy();
		ComputerPlayer cp = new ComputerPlayer(Intersection.HINT, g);
		int moveHint = cp.determineMove(b);
		System.out.println("test: moveHint" + moveHint);
		b.setHint(moveHint);
		String boardstringWithHint = b.toBoardstring();
		System.out.println("test: bwh" + boardstringWithHint);
		
		
		if(useTUI == true) {
			TUI tui = new TUI(boardstringWithHint, DIM);
		} else if (useTUI == false){
			if(gogui == null) {
				gogui = new GoGuiIntegrator(false,true,DIM);
				gogui.startGUI();
				gogui.setBoardSize(DIM);
				
				for(int i = 0; 0 <= i && i < DIM*DIM; i++) {
					int x = i%DIM;
					int y = Math.floorDiv(i, DIM);
					if(b.intersections[i] == Intersection.WHITE) {
						gogui.addStone(x, y, true);
					} else if(b.intersections[i] == Intersection.BLACK){
						gogui.addStone(x, y, false);
					} else if(b.intersections[i] == Intersection.HINT){
						gogui.addHintIndicator(x, y);
					} else {
						continue;//lege intersection
					}
				}
			} else {
				gogui.clearBoard();
			
				for(int i = 0; 0 <= i && i < DIM*DIM; i++) {
					int x = i%DIM;
					int y = Math.floorDiv(i, DIM);
					if(b.intersections[i] == Intersection.WHITE) {
						gogui.addStone(x, y, true);
					} else if(b.intersections[i] == Intersection.BLACK){
						gogui.addStone(x, y, false);
					} else if(b.intersections[i] == Intersection.HINT){
						gogui.addHintIndicator(x, y);
					} else {
						print("something is wrong with the boardstring in UI()");
					}
				}
			}
		} else {
			print("Something went wrong with creating a UI");
		}

	}
	
	//veranderd de boolean useTUI naar meegegeven boolean
	public void setUseTUI(boolean b) {
		useTUI = b;
	}
	
	private static void print(String message){
		System.out.println(message);
	}
}
