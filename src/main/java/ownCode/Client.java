package ownCode;

import com.nedap.go.*;
import com.nedap.go.gui.*;
import java.io.IOException;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
	//Client bestaat uit:
		//code voor het Socket verkeer:
		//code om de String van de server om te zetten naar variabelen
		//code om variabelen om te zetten naar een String om naar de server door te sturen
	
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
	public String gameStateString;
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
	public Player p;
	public boolean useTUI = true;
	public GameBrain gb;
	
	
	public static void main(String[] args) {
	
		//maakt een socket aan, om met de server te connecten
		if (args.length != 3) {
            System.out.println(INITIAL_INPUT);
            System.exit(0);
        }
		
		String name = args[0];
        InetAddress addr = null;
        int port = 0;
        Socket sock = null;
        
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
            sock = new Socket(addr, port);
        } catch (IOException e) {
            System.out.println("ERROR: could not create a socket on " + addr
                    + " and port " + port);
        }
        
     // create SocketInteraction object and start the two-way communication
        try {
            SocketInteraction clientSocket = new SocketInteraction(name, sock);
            Thread clientThread = new Thread(clientSocket);
            clientThread.start();
            clientSocket.handleTerminalInput();
            clientSocket.shutDown();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	//kijkt of het een serverstring een herkenbare command bevat. zo ja, reageert de client daarop volgens protocol
	public void serverStringSplitter(String serverString) {
		this.serverString = serverString;
		String stringArray[] = gameStateString.split("+");
		if(stringArray[0].equals("ACKNOWLEDGE_HANDSHAKE")) {
			this.gameID = Integer.parseInt(stringArray[1]);
			if(Integer.parseInt(stringArray[2]) == 0) {
				this.isLeader = false;
			}
			if(Integer.parseInt(stringArray[2]) == 1) {
				this.isLeader = true;
			}
		}
		else if(stringArray[0].equals("REQUEST_CONFIG")) {
			this.serverMessage = stringArray[1];
			String s = setConfig();
			//stuur s naar server
			
		}
		else if(stringArray[0].equals("ACKNOWLEDGE_CONFIG")) {
			this.playerName = stringArray[1];
			if(Integer.parseInt(stringArray[2])==1){
				this.playerColorIndex = 1;
				this.playerColor = playerColor.BLACK;
			}
			if(Integer.parseInt(stringArray[2])==2){
				this.playerColorIndex = 2;
				this.playerColor = playerColor.WHITE;
			}
			this.DIM = Integer.parseInt(stringArray[3]);
			this.gameState = new GameState(stringArray[4]);
				this.status = this.gameState.status;
				this.currentPlayer = this.gameState.currentPlayer;
				this.boardstring = this.gameState.boardstring;
			this.opponentName = stringArray[5];
			
			if(this.currentPlayer == this.playerColorIndex) {
				gb = new GameBrain(boardstring, DIM, p);
				gb.updateBoardHistory(boardstring);
				String s = move(gb,boardstring);
				//Stuur s naar server
			}
			else{
				gb = new GameBrain(boardstring, DIM, p);
				UI(boardstring, DIM);
			}
		}
		else if(stringArray[0].equals("ACKNOWLEDGE_MOVE")) {
			if(this.gameID == Integer.parseInt(stringArray[1])){
				this.move = new Move(stringArray[2]);
					this.tileIndex = this.move.tileIndex;
					this.playerColorIndex = this.move.playerColorIndex;
				this.gameState = new GameState(stringArray[3]);
					this.status = this.gameState.status;
					this.currentPlayer = this.gameState.currentPlayer;
					this.boardstring = this.gameState.boardstring;
			}	
			
			//als ik current player ben en ander heeft niet gepast
			if(this.currentPlayer == this.playerColorIndex && tileIndex != -1) {
				gb.updateBoardHistory(boardstring); //tegenstander heeft een nieuw board gemaakt
				String s = move(gb,boardstring); //ik ben aan zet
				//Stuur s naar server
				
				
				
				//stuur s naar server zie hierboven niet vergeten en beneden
				
				
			}
			//als ik current player en andere heeft wel gepast
			else if(this.currentPlayer == this.playerColorIndex && tileIndex == -1) {
				//de tegenstander heeft geen nieuw board aan gemaakt
				String s = move(gb,boardstring); //ik ben aan zet
				//Stuur s naar server
			}

			//als ik niet current player ben en ik heb niet gepast
			else if(this.currentPlayer != this.playerColorIndex && tileIndex != -1) {
				gb.updateBoardHistory(boardstring); //mijn zet is geaccepteerd en maakt een nieuw board
				//wacht tot ik aan de beurt ben
			}
			
			//als ik niet current player ben en ik heb wel gepast
			else if(this.currentPlayer != this.playerColorIndex && tileIndex != -1) {
				//mijn zet is geaccepteerd maar ik maak geen nieuw board
				//wacht tot ik aan de beurt ben
			}	
		}
		else if(stringArray[0].equals("INVALID_MOVE")) {
			this.serverMessage = stringArray[1];
			System.out.println(serverMessage);
			//vraag om nieuwe move
			String s = move(gb,boardstring); //ik ben aan zet
			//Stuur s naar server
			
		}
		else if(stringArray[0].equals("UNKOWN_COMMAND")) {
			this.serverMessage = stringArray[1];
			System.out.println(serverMessage);
			//vraag om nieuwe command
			
			
			//niet vergeten
			
			
			
		}
		else if(stringArray[0].equals("UPDATE_STATUS")) {
			this.gameState = new GameState(stringArray[1]);
				this.status = this.gameState.status;
				this.currentPlayer = this.gameState.currentPlayer;
				this.boardstring = this.gameState.boardstring;
			System.out.println("The status:" + status.statusString(this.status) + " the current layer:" + currentPlayer);
			UI(boardstring, DIM);
			if(status == Status.PLAYING && this.currentPlayer == this.playerColorIndex) {
				String s = move(gb,boardstring);
				//Stuur s naar server
			}
		}
		else if(stringArray[0].equals("GAME_FINISHED")) {
			if(this.gameID == Integer.parseInt(stringArray[1])) {
				this.winner = stringArray[2];
				this.score = new Score(stringArray[3]);
					this.pointsBlack = this.score.pointsBlack;
					this.pointsWhite = this.score.pointsWhite;
				this.serverMessage = stringArray[4];
			}
			System.out.println(winner);
			System.out.println("points for black:" + Integer.toString(pointsBlack)+" - points for white:" + Integer.toString(pointsWhite));
			System.out.println(serverMessage);
			
			// vraag user om nog een potje
			
			
		}
		else {
			System.out.println("de server stuurt een foutief bericht");
		}
		
	}
	
	//Strings die client naar de server stuurt
		public String handshake() {
			return "HANDSHAKE"+playerName;
		}
		public String setConfig() {
			
			//dit is default. maar user kan ook kiezen om computerplayer te doen, maar dan geen default
			
			if(p instanceof ComputerPlayer) {
				this.playerColorIndex = 2;
				this.DIM = 7;
				return "SET_CONFIG+"+Integer.toString(gameID)+"+"+Integer.toString(playerColorIndex)+"+"+Integer.toString(DIM);
			}
			else{
			return null;
			/** moet ik nog maken*/
			//je vraagt aan de user om kleur en DIM te kiezen
			
			}
		}
		public String move(GameBrain gb, String boardstring) {
			this.tileIndex = gb.setMove(boardstring);
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
