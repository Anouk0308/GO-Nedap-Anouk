package ownCode;

public class Score {
	public String scoreString;
	public int pointsBlack;
	public double pointsWhite;
	
	public Score(String scoreString){
		String stringArray[] = scoreString.split(";");
		this.pointsBlack = Integer.parseInt(stringArray[1]);
		this.pointsWhite = Double.parseDouble(stringArray[3]);
	}
}
