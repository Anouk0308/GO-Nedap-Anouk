package ownCode;

import com.nedap.go.gui.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

//Client bestaat uit:
		//code voor het Socket verkeer:
		//code om de String van de server om te zetten naar variabelen
		//code om variabelen om te zetten naar een String om naar de server door te sturen

public class ServerInputHandler {
	public String playerName;
	public String opponentName;
	public String serverMessage;
	public String winner;
	public String boardstring;
	
	public int whichPlayerIndexChoice;
	public int DIM;
	public int tileIndex;
	public int pointsBlack;
	public int pointsWhite;
	public int currentPlayer;
	public int gameID;
	public int playerColorIndex;
	
	public boolean isLeader;
	public boolean useTUI = true;

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
	
	public ServerInputHandler(BufferedReader userInput, Client c) {
		this.c = c;
		System.out.println("test: is de client null wanneer SIH aangemaakt wordt?" + c);
		this.userInput = userInput;
	}
	
	//split de serverstring in een array
	public String[] serverStringSplitter(String serverString) {
		String[] stringArray = serverString.split("\\+");
		System.out.println(stringArray[0]+ "zijn bij splitter");
		return stringArray;
	}
	
	//analyseerd welk commando het is, en stuurt door naar de goede methode
	public void stringArrayAnalyser(String[] sa) {
		String s = sa[0];
		System.out.println(s+ "zijn bij analyser");
		switch(s) {
			case "ACKNOWLEDGE_HANDSHAKE":	System.out.println("ah1 is binnen");
											acknowledgeHandshake(sa);
											break;
			case "REQUEST_CONFIG":			System.out.println("rc1 is binnen");
											requestConfig(sa);
											break;
			case "ACKNOWLEDGE_CONFIG":		acknowledgeConfig(sa);
											break;
			case "ACKNOWLEDGE_MOVE":		acknowledgeMove(sa);
											break;
			case "INVALID_MOVE":			invalidMove(sa);
											break;
			case "UNKNOWN_COMMAND":			unknownCommand(sa);
											break;
			case "UPDATE_STATUS":			updateStatus(sa);
											break;
			case "GAME_FINISHED":			gameFinished(sa);
											break;
			case "REQUEST_REMATCH":			requestRematch(sa);
											break;
			default:						System.out.println("The server sent an invalid command");
											break;
		}
	}
	
	public void acknowledgeHandshake(String[] sa) {
		this.gameID = Integer.parseInt(sa[1]);
		if(Integer.parseInt(sa[2]) == 0) {
			this.isLeader = false;
		}
		if(Integer.parseInt(sa[2]) == 1) {
			this.isLeader = true;
		}
		print("Connected with the server");
		print("Wait till more information");
	}
	
	public void requestConfig(String[] sa) {
		this.serverMessage = sa[1];
		print(serverMessage);
		try {
			String s = setConfig();
			System.out.println("test: is hier ook setConfig 0+1+4?" + s);
		c.sendMessage(s);//serverOutput
			System.out.println("test: is deze client leeg?"+c);//nee niet leeg
		}catch (IOException e) {
            e.printStackTrace();
        }
		print("Wait till another player");
		//wacht tot een acknowledgeConfig
	}
	
	public void acknowledgeConfig(String[] sa) {
		this.playerName = sa[1];
		if(Integer.parseInt(sa[2])==1){
			this.playerColorIndex = 1;
			this.playerColor = playerColor.BLACK;
			print("Your color is black");
		}
		if(Integer.parseInt(sa[2])==2){
			this.playerColorIndex = 2;
			this.playerColor = playerColor.WHITE;
			print("Your color is white");
			lll; //deze print die nog wel, daarna niet meer
		}
			if(whichPlayerIndexChoice == 1) {
				this.p = new HumanPlayer(playerName, playerColor);
			}
			if(whichPlayerIndexChoice == 2) {
				this.g = new NaiveStrategy();
				this.p = new ComputerPlayer(playerColor, g);
			}
		this.DIM = Integer.parseInt(sa[3]);
			print("The board is " + DIM + " by " + DIM + " size.");
		System.out.println("test: bekijk wat de gameState is" + sa[4]);
		this.gameState = new GameState(sa[4]);
			this.status = this.gameState.status;
			this.currentPlayer = this.gameState.currentPlayer;
			this.boardstring = this.gameState.boardstring;
			UI(boardstring, DIM);
		this.opponentName = sa[5];
			print("You will play to " + opponentName);
		
			if(this.currentPlayer == this.playerColorIndex) {
				gb = new GameBrain(boardstring, DIM, p);
				gb.updateBoardHistory(boardstring);
				c.sendMessage(move(gb,boardstring));//serverOutput
				//wacht tot acknowledgeMove van eigen move
			}
			else{;
				gb = new GameBrain(boardstring, DIM, p);
				print("It is the other player's turn");
				//wacht tot acknowledgeMove van de andere players move
			}
	}
	
	public void acknowledgeMove(String[] sa) {
		if(this.gameID == Integer.parseInt(sa[1])){
			this.move = new Move(sa[2]);
				this.tileIndex = this.move.tileIndex;
				this.playerColorIndex = this.move.playerColorIndex;
			this.gameState = new GameState(sa[3]);
				this.status = this.gameState.status;
				this.currentPlayer = this.gameState.currentPlayer;
				this.boardstring = this.gameState.boardstring;
		}	
		
		//als de user de current player is en ander heeft niet gepast
		if(this.currentPlayer == this.playerColorIndex && tileIndex != -1) {
			UI(boardstring, DIM);
			gb.updateBoardHistory(boardstring); //tegenstander heeft een nieuw board gemaakt
			c.sendMessage(move(gb,boardstring)); //ik ben aan zet en stuur het door naar de server
			//wacht tot een acknowledgement van mijn move
		}
		//als de user de current player is en andere heeft wel gepast
		else if(this.currentPlayer == this.playerColorIndex && tileIndex == -1) {
			UI(boardstring, DIM);//de tegenstander heeft geen nieuw board aan gemaakt
			c.sendMessage(move(gb,boardstring)); //ik ben aan zet en stuur het door naar de server
			//wacht tot een acknowledgement van mijn move
		}

		//als de user niet de current player is ben en ik heb niet gepast
		else if(this.currentPlayer != this.playerColorIndex && tileIndex != -1) {
			UI(boardstring, DIM);
			gb.updateBoardHistory(boardstring); //mijn zet is geaccepteerd en maakt een nieuw board
			//wacht tot een acknowledgement van de opponents move
		}
		
		//als de user niet de current player is ben en ik heb wel gepast
		else if(this.currentPlayer != this.playerColorIndex && tileIndex != -1) {
			UI(boardstring, DIM);
			//mijn zet is geaccepteerd maar ik maak geen nieuw board
			//wacht tot een acknowledgement van de opponents move
		}	
	}
	
	public void invalidMove(String[] sa) {
		this.serverMessage = sa[1];
		print(serverMessage);
		print("The server finds it an invalid move");
		c.sendMessage(move(gb,boardstring));
	}
	
	public void unknownCommand(String[] sa) {
		this.serverMessage = sa[1];
		print("The server does not recognise a command");
		print("please go cry to Anouk, because I also don't know what to do :(");
	}
	
	public void updateStatus(String[] sa) {
		this.gameState = new GameState(sa[1]);
			this.status = this.gameState.status;
			this.currentPlayer = this.gameState.currentPlayer;
			this.boardstring = this.gameState.boardstring;
		print("The status:" + status.statusString(this.status) + " the current layer:" + currentPlayer);
		UI(boardstring, DIM);
		if(status == Status.PLAYING && this.currentPlayer == this.playerColorIndex) {
			c.sendMessage(move(gb,boardstring));//stuur nieuwe move naar server
		}
		else {
			print("Wait till the opponent makes a move");
		}
	}
	
	public void gameFinished(String[] sa) {
		if(this.gameID == Integer.parseInt(sa[1])) {
			this.winner = sa[2];
			this.score = new Score(sa[3]);
				this.pointsBlack = this.score.pointsBlack;
				this.pointsWhite = this.score.pointsWhite;
			this.serverMessage = sa[4];
		}
		print("The winner is:" + winner);
		print("Points for black:" + Integer.toString(pointsBlack)+" - points for white:" + Integer.toString(pointsWhite));
		print(serverMessage);
		Client c = new Client();
		c.anotherGame();
	}
	
	public void requestRematch(String[] sa) {
		print("would you like to play a new game?");
		print("1 for yes, 0 for no");
		try {
			String answer = userInput.readLine();
			if(answer == "0" || answer == "1") {
					setRematch(Integer.parseInt(answer));
			}
			else {
				print("that is not a 0 or 1, try again");
				requestRematch(sa);
			}
		} catch(IOException e) {
			print(e.getMessage());
		}
	}
	
//Strings die client naar de server stuurt
	public String handshake() {
		return "HANDSHAKE+"+playerName;
	}
	
	public String setConfig() throws IOException {
		print("Okay, which color would you like to use? 1 for black and 2 for white");
		String userString = userInput.readLine();
		int thisInt = Integer.parseInt(userString);
			if(userInput != null) {
				if(thisInt == 1 || thisInt == 2) {
					this.playerColorIndex = thisInt;
				}
				else {
					print("No sillybilly, that is not a 1 or 2, try again");
					setConfig();
				}
			}
			else {//default
				this.playerColorIndex = 2;
			}
		print("Great, what is your perfered board dimension?");
			if(userInput != null) {
				this.DIM = Integer.parseInt(userInput.readLine());
			}
			else {//default
				this.DIM = 7;
			}
		String s = "SET_CONFIG+"+Integer.toString(gameID)+"+"+Integer.toString(playerColorIndex)+"+"+Integer.toString(DIM); 
		System.out.println("Test: setConfig geeft regel " +  s);//dit gaat goed
		System.out.println("test: is client null? " + c);
		return s; 
	}
	
	public String move(GameBrain gb, String boardstring) {
		if(p instanceof ComputerPlayer) {
			this.tileIndex = gb.setMove(boardstring);
		}
		else {
			print("Where would you like to place a new stone?");
			try {
				if(userInput != null) {
					int thisInt = Integer.parseInt(userInput.readLine());
					this.tileIndex = thisInt;
					if(gb.validMove(tileIndex)) {
						print("this is a valid move");
					}
					else {
						print("this is not a valid move, please enter a new number");
						move(gb,boardstring);
					}
				}
			} catch (IOException e) {
	            e.printStackTrace();
	        }
		}
		return "MOVE"+"+"+Integer.toString(gameID)+"+"+playerName+"+"+Integer.toString(tileIndex);
	}
	
	public String exit() {
		return "EXIT"+"+"+Integer.toString(gameID)+"+"+playerName;
	}
	
	public String setRematch(int answer) {
		return "SET_REMATCH+" + answer;
	}

//kiest goede UI en laat het board zien
	public void UI(String boardstring, int DIM) {
		if(useTUI = true) {
			TUI tui = new TUI(boardstring, DIM);
		}
		else {
			GoGuiIntegrator gogui = new GoGuiIntegrator(false,true,DIM);
			gogui.startGUI();
			gogui.setBoardSize(DIM);
			Board board = new Board(boardstring, DIM);
			
			for(int i = 0; 0 <= i && i < DIM*DIM; i++) {
				int x = i%DIM;
				int y = Math.floorDiv(i, DIM);
				if(board.intersections[i] == Intersection.WHITE) {
					gogui.addStone(x, y, true);
				}
				else if(board.intersections[i] == Intersection.BLACK){
					gogui.addStone(x, y, false);
				}
			}
		}

	}
	
	private static void print(String message){
		System.out.println(message);
	}
}
