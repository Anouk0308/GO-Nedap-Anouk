package ownCode;

public class GameState {
	public String gameStateString;
	public Status status;
	public int currentPlayer;
	public String boardstring;
	
	public GameState(String gameStateString){
		String stringArray[] = gameStateString.split("\\;");

			//this.status = status.valueOf(stringArray[0]);
			this.currentPlayer = Integer.parseInt(stringArray[1]);
			this.boardstring = stringArray[2];
		
	}
	
	public String getBoardstring() {
		return this.boardstring;
	}
}
