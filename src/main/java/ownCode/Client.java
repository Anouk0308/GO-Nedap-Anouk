package ownCode;

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
	public String gameStateString;
	public String opponentName;
	public Move move;
	public String moveString;
	public String serverMessage;
	public String winner;
	public Score score;
	public Status status;
	public int currentPlayer;
	public String boardstring;
	private static final String INITIAL_INPUT
    = "input should be: <name> <address> <port>";
	
	//maakt een socket aan, om met de server te connecten
	public static void main(String[] args) {
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
            Thread streamInputHandler = new Thread(clientSocket);
            streamInputHandler.start();
            clientSocket.handleTerminalInput();
            clientSocket.shutDown();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	//kijkt of het een servercommand is volgens het protocol
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
			setConfig();
			
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
			this.opponentName = stringArray[5];
		}
		else if(stringArray[0].equals("ACKNOWLEDGE_MOVE")) {
			if(this.gameID == Integer.parseInt(stringArray[1])){
				if(this.move == new Move(stringArray[2])) {
					this.gameState = new GameState(stringArray[3]);
				}
			}	
		}
		else if(stringArray[0].equals("INVALID_MOVE")) {
			this.serverMessage = stringArray[1];
			
		}
		else if(stringArray[0].equals("UNKOWN_COMMAND")) {
			this.serverMessage = stringArray[1];
			
		}
		else if(stringArray[0].equals("UPDATE_STATUS")) {
			this.gameState = new GameState(stringArray[1]);
			
		}
		else if(stringArray[0].equals("GAME_FINISHED")) {
			if(this.gameID == Integer.parseInt(stringArray[1])) {
				this.winner = stringArray[2];
				this.score = new Score(stringArray[3]);
				this.serverMessage = stringArray[4];
			}
		}
		else {
			System.out.println("de server stuurt een foutief bericht");
		}
		
	}
		public String handshake() {
			return "HANDSHAKE"+playerName;
			/** moet ik nog maken*/
		}
		public String setConfig() {
			return "SET_CONFIG+"+Integer.toString(gameID)+"+"+Integer.toString(playerColorIndex)+"+"+Integer.toString(DIM);
			/** moet ik nog maken*/
		}
		public String move() {
			return "MOVE"+"+"+Integer.toString(gameID)+"+"+playerName+"+"+Integer.toString(tileIndex);
			/** moet ik nog maken*/
		}
		public String pass() {
			return "PASS"+"+"+Integer.toString(gameID)+"+"+playerName;
			/** moet ik nog maken*/
		}
		public String exit() {
			return "EXIT"+"+"+Integer.toString(gameID)+"+"+playerName;
			/** moet ik nog maken*/
		}
	
	//SetUpGame:
		//eerste in server?
			//computerplayer naive?
				//default name = String AIAnouk
				//default colour = int 1
				//default board sixe = int 9
				
	
	//flow:
		//in nieuw geval en wij beginnen:
			//maak nieuw gb en bord 000000
		//in nieuw geval en wij beginnen niet:
			//maak nieuw gb met gegeven bord
		//in geval niet nieuw spel:
			// in geval dat mijn player passes, maar de andere player niet passed
				//zet gb.playerpasses = false
				//oldToNewboardstring()
			//in geval dat andere player passes, maar mijne kan nog een move zetten
				//zet gb.otherplayerpasses = true
				//oldToNewboardstring()
			//in geval dat mijn player passes, en de andere player passes
				//exit, scoren tellen
			//in geval geen player passes
				//oldToNewboardstring()
	
	
	
		//oldToNewboardstring(gb, boardstring){
			//gb.updateboardhistory(boardstring)
			//gb.setmove(boardstring) = nieuwboardstring voor de server
	

	
	
	
//opties om met server te praten:
	//c->s begint met een handshake 
		//handshake
			//HANDSHAKE+$PLAYER_NAME
				//playername string
	//s-c krijgt een acknowledge handshake terug wanneer verbonden
		//game_id,int
		//is_leader, int 0 = false, 1 = true (jij bent leader)
	//s-c request_config of acknowledge config
		//set_config
			//default_color
			//default_board_size
	//s-c acknowledge config
		//krijgt te horen wat je naam is, string
		//wat je kleur is, int
		//wat de size is, int
		//wat de game state is
			//waiting/playing/finished
			//current_player, int (colour)
			// boardstring
		//naam van de tegenstander,string
	//c-s move
		//gameID
		//playerName
		//tile_indez`?????????
	//s-c acknowledge move of invalid move
		//acknowledge move
			//gameID, int
			//move
				//index
				//colour
			//state
				//waiting/playing/finished
				//current_player, int (colour)
				// boardstring
		//invalid move
			//errormessage, string
	//s-c game finished
		//gameID, int
		//username winner, string
		//score, string (1;points_black;2;points_white)
		//message, string, leeg
	
	
	//c-s exit
		//game-id
		//playername

	
	
	
	
	
	//gui/tui zooi
	
}
