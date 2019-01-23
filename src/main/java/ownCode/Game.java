package ownCode;

import java.util.ArrayList;

public class Game {
	public int DIM;
	private String player1Name;
	private int player1ColorIndex;
	private String player2Name;
	private int player2ColorIndex;
	private int gameID;
	public int currentPlayer;
	public Board board;
	public String boardstring;
	public ArrayList<String> boardHistory;
	
	public Game(String player1Name, int player1ColorIndex, String player2Name, int DIM, int gameID) {
		this.player1Name = player1Name;
		this.player2Name = player2Name;
		this.player1ColorIndex = player1ColorIndex;
		if(player1ColorIndex == 1) {
			player2ColorIndex = 2;
		}
		else if(player1ColorIndex == 2) {
			player2ColorIndex = 1;
		}
		this.DIM = DIM;
		this.boardstring = createEmptyBoard(DIM);
		this.gameID = gameID;
		this.currentPlayer = player1ColorIndex;
		this.board = new Board(boardstring, DIM);
		
	}
	
	public void updateBoardHistory(String oldboardstring) {
		if( this.boardHistory.contains(oldboardstring)) {
			System.out.println("er is niet gepasst door de andere speler, maar heb wel dezelfde boardstring gekregen vanuit de server");
		}
		else {
			boardHistory.add(oldboardstring);
		}
	}
	
	public String createEmptyBoard(int DIM) {
		String boardstring = "";
		for(int i = 0; i<DIM*DIM; i++) {
			boardstring = boardstring+Integer.toString(0);
		}
		return boardstring;
	}
	
	public void setCurrentPlayerOther() {
		if(currentPlayer == 1) {
			currentPlayer = 2;
		}
		else if(currentPlayer == 2) {
			currentPlayer = 1;
		}
	}

	public String updateBoard(String playerName, int tileIndex, String boardstring) {
		this.board = new Board(boardstring, DIM);//board updaten met huidige boardstring
		int tileColor = 0;
		Intersection i = Intersection.EMPTY;
		
		if(playerName == player1Name) {
			tileColor = player1ColorIndex;
		}
		else if (playerName == player2Name) {
			tileColor = player2ColorIndex;
		}
		
		if(tileColor == 1) {
			i= Intersection.BLACK;
		}
		else if (tileColor == 2) {
			i= Intersection.WHITE;
		}
		this.board.setIntersection(tileIndex, i);
		String notYetCheckedBoardstring = this.board.toBoardstring();
		String checkedBoardstring = checkForCaptures(notYetCheckedBoardstring);
		return checkedBoardstring;//de vernieuwe boardstring
	}
	
	public String checkForCaptures(String notYetCheckedBoardstring) {
		//als geen stenen vangen, stuur zelfde
		//als wel stenen vangen, haal ze weg en stuur nieuwe string
		String checkedBoardstring = null;
		return checkedBoardstring;
	}


	
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
