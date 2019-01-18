package ownCode;

public class Move {
	public String moveString;
	public int tileIndex;
	public int playerColor;
	
	public Move(String moveString){
		String stringArray[] = moveString.split(";");
		if (stringArray.length == 2) {
			this.tileIndex = Integer.parseInt(stringArray[0]);
			this.playerColor = Integer.parseInt(stringArray[1]);
		}
		
	}
}
