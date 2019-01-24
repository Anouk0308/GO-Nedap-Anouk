package ownCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

	public String updateBoard(String playerName, int tileIndex, String boardstring, int DIM, Board board) {
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
		String checkedBoardstring = checkForCaptures(notYetCheckedBoardstring, DIM);
		return checkedBoardstring;//de vernieuwe boardstring
	}
	
	public String checkForCaptures(String notYetCheckedBoardstring, int DIM) {
		//board wordt gemaakt met gegeven boardstring
		//intersections[] wordt gemaakt met gemaakte board
		Board board = new Board(notYetCheckedBoardstring, DIM);
		Intersection[] intersectionsArray = board.intersections;
		List<Intersection> intersections = new ArrayList<Intersection>(Arrays.asList(intersectionsArray));
		List<Intersection> sameColorNeightbours = new ArrayList<Intersection>();
		List<Intersection> notSameColorNeightbours = new ArrayList<Intersection>();//empty zit hier ook in
    	
    	for(int a = 0; a < intersections.size(); a++) {
    		if(intersections.get(a) == Intersection.EMPTY) {
    			//empty tellen niet mee met stenen capturen, dus a++
    		}
    		else {
    			List<Intersection> neightboursList = board.getNeighbors(a, DIM, intersections);//neem buren van de intersectie met steen
    			for(int b = 0; b < neightboursList.size(); b++) {
    				Intersection checkingThisNeightbour = neightboursList.get(b);
    				if(checkingThisNeightbour == intersections.get(a
    					
    						
    						)) {//zelfde als de steen waar we de buren van hebben gevraagd? dan horen ze bij dezelfde groep
    					sameColorNeightbours.add(checkingThisNeightbour);
    				} else {
    					notSameColorNeightbours.add(checkingThisNeightbour);//dit is de rand van de groep (kan ook een lege intersectie zijn)
    				}
    			}
	//nu wil je de eerste ELSe doen, maar ipv a, geef je de bovenste van de sameColorNeightbours. Deze lijst wil je nml aanvullen, tot je alle randen hebt
    				//in dat geval, kijk of alle Intersecties van notSameColorNeightbours gelijk zijn aan de tegenovergestelde kleur van a
    					//zo ja, zet dan alles van sameColorNeightbours op empty
    					//zo nee, a++
    			
    		}
    	}
		
		
		
		
		

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
