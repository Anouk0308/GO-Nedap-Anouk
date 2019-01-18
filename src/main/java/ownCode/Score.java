package ownCode;

public class Score {
	public String scoreString;
	public int pointsBlack;
	public int pointsWhite;
	
	public Score(String scoreString){
		String stringArray[] = scoreString.split(";");
		if (stringArray.length == 4 && Integer.parseInt(stringArray[0])==1 && Integer.parseInt(stringArray[2])==2) {
			this.pointsBlack = Integer.parseInt(stringArray[1]);
			this.pointsWhite = Integer.parseInt(stringArray[3]);
		}
		
	}
}
