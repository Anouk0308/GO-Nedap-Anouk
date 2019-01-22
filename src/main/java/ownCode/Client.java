package ownCode;

import com.nedap.go.*;
import com.nedap.go.gui.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.PrintWriter;
//import java.io.Reader;
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

public class Client {
	public String serverString;
	public String playerName;
	public int gameID;
	public int playerColorIndex;
	public Intersection playerColor;
	public int DIM;
	public int tileIndex;
	public boolean isLeader;
	public GameState gameState;
	public Status status;
	public String opponentName;
	public Move move;
	public String moveString;
	public String serverMessage;
	public String winner;
	public Score score;
	public int pointsBlack;
	public int pointsWhite;
	public int currentPlayer;
	public String boardstring;
	private static final String INITIAL_INPUT
    = "input should be: <name> <address> <port>";
	public int whichPlayerIndexChoice;
	public Player p;
	public Strategy g;
	public boolean useTUI = true;
	public GameBrain gb;
	public InetAddress addr;
	public int port;
	public Socket sock;
	public SocketInteraction clientSocket;
	public Thread clientThread;
	
	public BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));//wat krijgt client binnen van de user
	public PrintWriter userOutput = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)));//wat krijgt de user van de client
	public BufferedReader serverInput;//wat krijgt de client binnen van de server
	public BufferedWriter serverOutput;//wat krijgt de server van de client
	public boolean exit = false;
	
	public static void main(String[] args) {
		Client c = new Client();
		c.gameFlow();
	}

	public void gameFlow(){
		try{
			welcomingUser();//till connecting with the server
			serverInput = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			serverString = serverInput.readLine();
			while(true) {
				if(userInput.readLine() == "EXIT") {
					clientSocket.sendString(exit());//serverOutput
					exit = true;
				}
				else {
					while(exit!= true) {
						String[] stringArray = serverStringSplitter(serverString);
						stringArrayAnalyser(stringArray);
					}
				}
			}
		} catch(IOException e){
			System.out.println(e);
		}
	}
	
	public void welcomingUser() throws IOException{
		userOutput.println("Welkom gamer, what is your name?");
			if(userInput != null) {
			playerName = userInput.readLine();
			}
			
		userOutput.println("Very well " + playerName +", would you like to play yourself or would you like to use the AI?");
		userOutput.println("type 1 for playing yourself, type 2 for letting the AI play.");
			if(userInput != null) {
				whichPlayerIndexChoice = Integer.parseInt(userInput.readLine());
			}
			
		userOutput.println("Good, to which server would you like to connect?");
		userOutput.println("type the Inetadress, click enter, type the port, click enter again");
			try {
			addr = InetAddress.getByName(userInput.readLine());    
	        } catch (UnknownHostException e) {
	        	userOutput.println(INITIAL_INPUT);
	        	userOutput.println("ERROR: host unknown");
	            System.exit(0);
	        }
			try {
				port = Integer.parseInt(userInput.readLine());
			} catch (NumberFormatException e) {
				userOutput.println(INITIAL_INPUT);
				userOutput.println("ERROR: that is not an integer");
	            System.exit(0);
			}
			try {
	            sock = new Socket(addr, port);
	        } catch (IOException e) {
	        	userOutput.println("ERROR: could not create a socket on " + addr
	                    + " and port " + port);
	        }
			try {
				clientSocket = new SocketInteraction(playerName, sock);
				clientThread = new Thread(clientSocket);
				clientThread.start();
	            clientSocket.sendString(handshake());//serverOutput
	            userOutput.println("Connecting with the server");
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	}
	
	//split de serverstring in een array
	public String[] serverStringSplitter(String serverString) {
		this.serverString = serverString;
		String[] stringArray = serverString.split("+");
		return stringArray;
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
			case "UPDATE_STATUS":			updateStatus(sa);
											break;
			case "GAME_FINISHED":			gameFinished(sa);
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
		userOutput.println("Connected with the server");
		userOutput.println("Wait till more information");
	}
	
	public void requestConfig(String[] sa) {
		this.serverMessage = sa[1];
		userOutput.println(serverMessage);
		try {
		clientSocket.sendString(setConfig());//serverOutput
		}catch (IOException e) {
            e.printStackTrace();
        }
		userOutput.println("Wait till another player");
		//wacht tot een acknowledgeConfig
	}
	
	public void acknowledgeConfig(String[] sa) {
		this.playerName = sa[1];
		if(Integer.parseInt(sa[2])==1){
			this.playerColorIndex = 1;
			this.playerColor = playerColor.BLACK;
			userOutput.println("Your color is black");
		}
		if(Integer.parseInt(sa[2])==2){
			this.playerColorIndex = 2;
			this.playerColor = playerColor.WHITE;
			userOutput.println("Your color is white");
		}
			if(whichPlayerIndexChoice == 1) {
				this.p = new HumanPlayer(playerName, playerColor);
			}
			if(whichPlayerIndexChoice == 1) {
				this.g = new NaiveStrategy();
				this.p = new ComputerPlayer(playerColor, g);
			}
		this.DIM = Integer.parseInt(sa[3]);
			userOutput.println("The board is " + DIM + " by " + DIM + " size.");
		this.gameState = new GameState(sa[4]);
			this.status = this.gameState.status;
			this.currentPlayer = this.gameState.currentPlayer;
			this.boardstring = this.gameState.boardstring;
			UI(boardstring, DIM);
		this.opponentName = sa[5];
			userOutput.println("You will play to " + opponentName);
		
			if(this.currentPlayer == this.playerColorIndex) {
				gb = new GameBrain(boardstring, DIM, p);
				gb.updateBoardHistory(boardstring);
				clientSocket.sendString(move(gb,boardstring));//serverOutput
				//wacht tot acknowledgeMove van eigen move
			}
			else{;
				gb = new GameBrain(boardstring, DIM, p);
				userOutput.println("It is the other player's turn");
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
			clientSocket.sendString(move(gb,boardstring)); //ik ben aan zet en stuur het door naar de server
			//wacht tot een acknowledgement van mijn move
		}
		//als de user de current player is en andere heeft wel gepast
		else if(this.currentPlayer == this.playerColorIndex && tileIndex == -1) {
			UI(boardstring, DIM);//de tegenstander heeft geen nieuw board aan gemaakt
			clientSocket.sendString(move(gb,boardstring)); //ik ben aan zet en stuur het door naar de server
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
		userOutput.println("The server finds it an invalid move");
		clientSocket.sendString(move(gb,boardstring));
	}
	
	public void unknownCommand(String[] sa) {
		this.serverMessage = sa[1];
		userOutput.println("The server does not recognise a command");
		userOutput.println("please go cry to Anouk, because I also don't know what to do :(");
	}
	
	public void updateStatus(String[] sa) {
		this.gameState = new GameState(sa[1]);
			this.status = this.gameState.status;
			this.currentPlayer = this.gameState.currentPlayer;
			this.boardstring = this.gameState.boardstring;
		/**System.out.println("The status:" + status.statusString(this.status) + " the current layer:" + currentPlayer);
		UI(boardstring, DIM);
		if(status == Status.PLAYING && this.currentPlayer == this.playerColorIndex) {
			String s = move(gb,boardstring);
			//Stuur s naar server
		}*/
	}
	
	public void gameFinished(String[] sa) {
		if(this.gameID == Integer.parseInt(sa[1])) {
			this.winner = sa[2];
			this.score = new Score(sa[3]);
				this.pointsBlack = this.score.pointsBlack;
				this.pointsWhite = this.score.pointsWhite;
			this.serverMessage = sa[4];
		}
		/**System.out.println(winner);
		System.out.println("points for black:" + Integer.toString(pointsBlack)+" - points for white:" + Integer.toString(pointsWhite));
		System.out.println(serverMessage);
		
		// vraag user om nog een potje*/
	}
	

	
//Strings die client naar de server stuurt
	public String handshake() {
		return "HANDSHAKE"+playerName;
	}
	
	public String setConfig() throws IOException {
		userOutput.println("Okay, which color would you like to use? 1 for black and 2 for white");
			if(userInput != null) {
				this.playerColorIndex = Integer.parseInt(userInput.readLine());
			}
			else {//default
				this.playerColorIndex = 2;
			}
		userOutput.println("Great, what is your perfered board dimension?");
			if(userInput != null) {
				this.DIM = Integer.parseInt(userInput.readLine());
			}
			else {//default
				this.DIM = 7;
			}
		return "SET_CONFIG+"+Integer.toString(gameID)+"+"+Integer.toString(playerColorIndex)+"+"+Integer.toString(DIM);
	}
	
	public String move(GameBrain gb, String boardstring) {
		if(p instanceof ComputerPlayer) {
			this.tileIndex = gb.setMove(boardstring);
		}
		else {
			userOutput.println("Where would you like to place a new stone?");
			try {
				if(userInput != null) {
					this.tileIndex = Integer.parseInt(userInput.readLine());
					if(gb.validMove(tileIndex)) {
						userOutput.println("this is a valid move");
					}
					else {
						userOutput.println("this is not a valid move, please enter a new number");
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
		
		//als user EXIT typt
		
		return "EXIT"+"+"+Integer.toString(gameID)+"+"+playerName;
		/** moet ik nog maken*/
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
}
