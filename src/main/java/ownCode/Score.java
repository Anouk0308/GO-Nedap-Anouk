package ownCode;

public class Score {
	public String scoreString;
	public int pointsBlack;
	public double pointsWhite;
	
	public Score(String scoreString){
		this.scoreString = scoreString;
		String stringArray[] = scoreString.split("\\;");
		this.pointsBlack = Integer.parseInt(stringArray[0]);
		this.pointsWhite = Double.parseDouble(stringArray[1]);
	}
	
	//geef de string zodat het gelijk verstuurd kan worden via het protocol
	@Override
	public String toString() {
		String s = pointsBlack + ";" + pointsWhite;
		return s;
	}
}
