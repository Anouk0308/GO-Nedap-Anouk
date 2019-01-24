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
	private List<Intersection> sameColorNeightbours = new ArrayList<Intersection>();
	private List<Intersection> notSameColorNeightbours = new ArrayList<Intersection>();//empty zit hier ook in
	
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
		
    	
    	for(int i = 0; i < intersections.size(); i++) {
    		if(intersections.get(i) == Intersection.EMPTY) {
    			//empty tellen niet mee met stenen capturen, dus a++
    		}
    		else {
    			sameColorNeightbours.add(intersections.get(i));
    			List<Intersection> neightboursList = board.getNeighbors(i, DIM, intersections);//neem buren van de intersectie met steen
    			sameColorOrNot(neightboursList, i, intersections);
    			sameColorOrNot(sameColorNeightbours, i, intersections);
    			lll;// hierdoor krijg je dat hij de black die je er al in hebt gezet, nog een keer afgaat
    				//daarbij komt hij hier niet uit, dus je moet aangeven: wanneer niks toegevoegd, eruit
    																	//wanneer wel toegevoegd, nog een keer
    				//plus nu sla je alleen de intersections op in sameColorNeightbors en notSmae... maar niet de int waar ze op het bord staan
    			while(!notSameColorNeightbours.contains(Intersection.EMPTY)) {
    				for(int a = 0; a < sameColorNeightbours.size(); a++) {
    					Intersection in = sameColorNeightbours.get(a);
    					in = Intersection.EMPTY;
    				}
    			}
    			
    		}
    	}
    	
    	String newboardstring = "";
        int tempi = 0;
        String temps;

    	for(int i = 0; 0 <= i && i < DIM*DIM; i++) {
        	if(intersections.get(i) == Intersection.EMPTY) {
    			tempi = 0;
    		}
    		if(intersections.get(i) == Intersection.BLACK) {
    			tempi = 1;
    		}
    		if(intersections.get(i) == Intersection.WHITE) {
    			tempi = 2;
    		}
        	 temps = Integer.toString(tempi);
        	 newboardstring = newboardstring + temps;
        }
		String checkedBoardstring = newboardstring;
		return checkedBoardstring;
	}

    public void sameColorOrNot(List<Intersection> neightboursList, int a, List<Intersection> intersections) {
     	for(int i = 0; i < neightboursList.size(); i++) {
			Intersection checkingThisNeightbour = neightboursList.get(i);
			if(checkingThisNeightbour == intersections.get(a)) {//zelfde als de steen waar we de buren van hebben gevraagd? dan horen ze bij dezelfde groep
				sameColorNeightbours.add(checkingThisNeightbour);
			} else {
				notSameColorNeightbours.add(checkingThisNeightbour);//dit is de rand van de groep (kan ook een lege intersectie zijn)
			}
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
}
