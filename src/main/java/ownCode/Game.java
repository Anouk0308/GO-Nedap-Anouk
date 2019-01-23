package ownCode;

public class Game {
	private int DIM;
	private String playerName1;
	private String playerName2;
	private String boardstring;
	private int gameID;
	private int player1ColorIndex;
	
	public Game(String playerName1, int player1ColorIndex, String playerName2, int DIM, int gameID) {
		this.player1ColorIndex = player1ColorIndex;
		this.playerName1 = playerName1;
		this.playerName2 = playerName2;
		this.DIM = DIM;
		this.boardstring = createEmptyBoard(DIM);
		this.gameID = gameID;
	}
	
	public String createEmptyBoard(int DIM) {
		String boardstring = "";
		for(int i = 0; i<DIM*DIM; i++) {
			boardstring = boardstring+Integer.toString(0);
		}
		return boardstring;
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
