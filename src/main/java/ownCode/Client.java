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

public class Client {
	
	
	public String serverString;
	public String playerName;
	public String opponentName;
	public String serverMessage;
	public String winner;
	public String boardstring;
	
	public int whichPlayerIndexChoice;
	public int port;
	public int DIM;
	public int tileIndex;
	public int pointsBlack;
	public int pointsWhite;
	public int currentPlayer;
	public int gameID;
	public int playerColorIndex;
	
	public boolean isLeader;
	public boolean useTUI = true;
	public boolean exit = false;
	
	public Socket sock;
	public InetAddress addr;
	public SocketInteraction clientSocket;
	public Thread clientThread;
	public Intersection playerColor;
	public GameState gameState;
	public Status status;
	public Move move;
	public Score score;
	public Player p;
	public Strategy g;
	public GameBrain gb;
	public BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));//wat krijgt client binnen van de user
	public BufferedReader serverInput;//wat krijgt de client binnen van de server
	
	
	public static void main(String[] args) {
		Client c = new Client();
		c.gameFlow();
	}

	public void gameFlow(){
		try{
			welcomingUser();//till connecting with the server
			serverInput = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			serverString = serverInput.readLine();
			String thisLine = userInput.readLine();
			while(true) {
				if(thisLine == "EXIT") {
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
		System.out.println("Welkom gamer, what is your name?");
			if(userInput != null) {
			playerName = userInput.readLine();
			}
			
		chosingAI();
		chosingUI();
		chosingServer();
	}
	
	public void chosingAI() throws IOException {
		System.out.println("Very well " + playerName +", would you like to play yourself or would you like to use the AI?");
		System.out.println("type 1 for playing yourself, type 2 for letting the AI play.");
			if(userInput != null) {
				int number = Integer.parseInt(userInput.readLine());
				try {
					if(number == 1 || number == 2) {
						whichPlayerIndexChoice = number;
						System.out.println("You have chosen " + number);
					}
					else {
						System.out.println("No dummy, that is not a 1 or a 2, try again");
						chosingAI();
					}
				} catch (NumberFormatException e) {
					System.out.println("No simpelton, that is not an integer, try another port");
		            chosingServer();
				}
			}
	}
	
	public void chosingUI() throws IOException {
		System.out.println("What would you like to as display?");
		System.out.println("type 1 for TUI, type 2 for GUI.");
		try {
			if(userInput != null) {
				int number = Integer.parseInt(userInput.readLine());
				if( number == 1 || number == 2) {
					if(number == 1) {
						useTUI = true;
						System.out.println("you have chosen TUI");
					}
					else {
						useTUI = false;
						System.out.println("you have chosen GUI");s
					}
				}
				else {
					System.out.println("No clown, that is not a 1 or 2");
					chosingUI();
				} 
			}
		}catch (IOException e) {
			
		}

	}
	
	public void chosingServer() throws IOException {
		System.out.println("To which server would you like to connect?");
		System.out.println("type the Inetadress");
			String thisLine1 = userInput.readLine();
			try {
			addr = InetAddress.getByName(thisLine1);    
	        } catch (UnknownHostException e) {
	        	System.out.println("Computer says no, try anoter Inetadress");
	            chosingServer();
	        }
		System.out.println("type the port");
			String thisLine2 = userInput.readLine();
			try {
				port = Integer.parseInt(thisLine2);
			} catch (NumberFormatException e) {
				System.out.println("No simpelton, that is not an integer, try another port");
	            chosingServer();
			}
			try {
	            sock = new Socket(addr, port);
	        } catch (IOException e) {
	        	System.out.println("ERROR: could not create a socket on " + addr
	                    + " and port " + port);
	        	System.out.println("try another server");
	        	chosingServer();
	        }
			try {
				clientSocket = new SocketInteraction(playerName, sock);
				clientThread = new Thread(clientSocket);
				clientThread.start();
	            clientSocket.sendString(handshake());//serverOutput
	            System.out.println("Connecting with the server");
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
		System.out.println("Connected with the server");
		System.out.println("Wait till more information");
	}
	
	public void requestConfig(String[] sa) {
		this.serverMessage = sa[1];
		System.out.println(serverMessage);
		try {
		clientSocket.sendString(setConfig());//serverOutput
		}catch (IOException e) {
            e.printStackTrace();
        }
		System.out.println("Wait till another player");
		//wacht tot een acknowledgeConfig
	}
	
	public void acknowledgeConfig(String[] sa) {
		this.playerName = sa[1];
		if(Integer.parseInt(sa[2])==1){
			this.playerColorIndex = 1;
			this.playerColor = playerColor.BLACK;
			System.out.println("Your color is black");
		}
		if(Integer.parseInt(sa[2])==2){
			this.playerColorIndex = 2;
			this.playerColor = playerColor.WHITE;
			System.out.println("Your color is white");
		}
			if(whichPlayerIndexChoice == 1) {
				this.p = new HumanPlayer(playerName, playerColor);
			}
			if(whichPlayerIndexChoice == 1) {
				this.g = new NaiveStrategy();
				this.p = new ComputerPlayer(playerColor, g);
			}
		this.DIM = Integer.parseInt(sa[3]);
			System.out.println("The board is " + DIM + " by " + DIM + " size.");
		this.gameState = new GameState(sa[4]);
			this.status = this.gameState.status;
			this.currentPlayer = this.gameState.currentPlayer;
			this.boardstring = this.gameState.boardstring;
			UI(boardstring, DIM);
		this.opponentName = sa[5];
			System.out.println("You will play to " + opponentName);
		
			if(this.currentPlayer == this.playerColorIndex) {
				gb = new GameBrain(boardstring, DIM, p);
				gb.updateBoardHistory(boardstring);
				clientSocket.sendString(move(gb,boardstring));//serverOutput
				//wacht tot acknowledgeMove van eigen move
			}
			else{;
				gb = new GameBrain(boardstring, DIM, p);
				System.out.println("It is the other player's turn");
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
		System.out.println("The server finds it an invalid move");
		clientSocket.sendString(move(gb,boardstring));
	}
	
	public void unknownCommand(String[] sa) {
		this.serverMessage = sa[1];
		System.out.println("The server does not recognise a command");
		System.out.println("please go cry to Anouk, because I also don't know what to do :(");
	}
	
	public void updateStatus(String[] sa) {
		this.gameState = new GameState(sa[1]);
			this.status = this.gameState.status;
			this.currentPlayer = this.gameState.currentPlayer;
			this.boardstring = this.gameState.boardstring;
		System.out.println("The status:" + status.statusString(this.status) + " the current layer:" + currentPlayer);
		UI(boardstring, DIM);
		if(status == Status.PLAYING && this.currentPlayer == this.playerColorIndex) {
			clientSocket.sendString(move(gb,boardstring));//stuur nieuwe move naar server
		}
		else {
			System.out.println("Wait till the opponent makes a move");
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
		System.out.println("The winner is:" + winner);
		System.out.println("Points for black:" + Integer.toString(pointsBlack)+" - points for white:" + Integer.toString(pointsWhite));
		System.out.println(serverMessage);
		
		anotherGame();
	}
	
	public void anotherGame() {
		System.out.println("Would you like to play another game of GO? Y/N");
		
		try {
			String thisLine = userInput.readLine();
			if(userInput != null) {
				if(thisLine == "Y") {
					clientSocket.sendString(exit());//serverOutput
					exit = true;//kill this socket
					exit = false;//make it possible to create a socket again
					gameFlow();
				}
				else if(thisLine == "N") {
					clientSocket.sendString(exit());//serverOutput
					exit = true;
				}
				else {
					System.out.println("This is not a Y or N silly, try again");
				}
			}
		}catch (IOException e) {
	            e.printStackTrace();
	    }
	}
	
//Strings die client naar de server stuurt
	public String handshake() {
		return "HANDSHAKE"+playerName;
	}
	
	public String setConfig() throws IOException {
		System.out.println("Okay, which color would you like to use? 1 for black and 2 for white");
		int thisInt = Integer.parseInt(userInput.readLine());
			if(userInput != null) {
				if(thisInt == 1 || thisInt == 2) {
					this.playerColorIndex = thisInt;
				}
				else {
					System.out.println("No sillybilly, that is not a 1 or 2, try again");
					setConfig();
				}
			}
			else {//default
				this.playerColorIndex = 2;
			}
		System.out.println("Great, what is your perfered board dimension?");
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
			System.out.println("Where would you like to place a new stone?");
			try {
				if(userInput != null) {
					int thisInt = Integer.parseInt(userInput.readLine());
					this.tileIndex = thisInt;
					if(gb.validMove(tileIndex)) {
						System.out.println("this is a valid move");
					}
					else {
						System.out.println("this is not a valid move, please enter a new number");
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
