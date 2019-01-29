package ownCode;

public class Move {
	public String moveString;
	public int tileIndex;
	public int playerColorIndex;
	
	public Move(String moveString){
		this.moveString = moveString;
		String stringArray[] = moveString.split("\\;");
		if (stringArray.length == 2) {
			this.tileIndex = Integer.parseInt(stringArray[0]);
			this.playerColorIndex = Integer.parseInt(stringArray[1]);
		}
		
	}
	
	@Override
	public String toString() {
		return moveString;
	}
}
