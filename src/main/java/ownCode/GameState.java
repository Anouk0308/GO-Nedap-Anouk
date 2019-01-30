package ownCode;

public class GameState {
	public String gameStateString;
	public Status status;
	public int currentPlayer;
	public String boardstring;
	
	public GameState(String gameStateString){
		this.gameStateString = gameStateString;
		String stringArray[] = gameStateString.split("\\;");

			//this.status = status.valueOf(stringArray[0]); hier gaat wat fout :(
			this.currentPlayer = Integer.parseInt(stringArray[1]);
			this.boardstring = stringArray[2];
		
	}
	
	//getter voor deze boardstring
	public String getBoardstring() {
		return this.boardstring;
	}
	
	//geef de string zodat het gelijk verstuurd kan worden via het protocol
	@Override
	public String toString() {
		//String s = status+ ";" + currentPlayer + ";" + boardstring; 
		String s = gameStateString;
		return s;
	}
	
	
}
