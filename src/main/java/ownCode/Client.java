package ownCode;

public class Client {
	public String serverString;
	public String playerName;
	public int gameID;
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
	
	public Client() {
		
	}
	
	
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
				this.playerColor = playerColor.BLACK;
			}
			if(Integer.parseInt(stringArray[2])==2){
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
		public void handshake() {
			/** moet ik nog maken*/
		}
		public void setConfig() {
			/** moet ik nog maken*/
		}
		public void move() {
			/** moet ik nog maken*/
		}
		public void exit() {
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
