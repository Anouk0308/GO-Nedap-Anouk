package ownCode;

public class Game {
	public int DIM;
	public String boardstring;
	
	public Game() {
		String s = "";
		for(int i = 0; i<DIM*DIM; i++) {
			s = s+Integer.toString(0);
		}
		boardstring = s;
	}
	
	
	
	
	
	
	//twee players
		//playernaam
		//player intersection
		//player intersection index (kleur)
	//board
		//boardstring
		//dim
	//exit boolean = false
	
	//updateboard met:
		//if move = -1
			//acknowledgeMove, andere currentplayer, zelfde boardgame
		//if move tussen 0 en DIM(DIM
			//if( board.isEmptyIntersection(move)) {
			//board.setIntersection(move, p.getPlayerColour());
			//notYetCheckedBoardstring = board.toBoardstring
			//controleer of stenen weg via captured
		//else
			//invalidMove()
	
	
	//public String captured(notYetCheckedBoardstring)
		//lalalalala
		//return checkedBoardstring
	
	
	//public void score (boardstring)
		//lalalalala
		//pointsblack=
		//pointswhite=
		//als exit = true
			//loser = exit persoon
			//winner = other
		//anders
			//loser is kleinste getal
			//winner is andere
}
