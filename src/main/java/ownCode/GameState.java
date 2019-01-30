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
	
	public String getBoardstring() {
		return this.boardstring;
	}
	
	@Override
	public String toString() {
		return gameStateString;
	}
}
