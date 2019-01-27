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
	public int gameID;
	public int currentPlayer;
	public Board board;
	public String boardstring;
	public ArrayList<String> boardHistory;
	private List<Intersection> sameColorNeightbours = new ArrayList<Intersection>();
	private List<Intersection> notSameColorNeightbours = new ArrayList<Intersection>();//empty zit hier ook in
	private int neightboursListSize;
	public boolean onePass = false;
	
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
	
	public String getPlayerNameOther(String playerName) {
		if(playerName == player1Name) {
			return player2Name;
		}
		else {
			return player1Name;
		}
	}

	public String updateBoard(String playerName, int tileIndex, String boardstring, int DIM) {
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
    			//hashmap
    			sameColorOrNot(sameColorNeightbours, i, intersections);
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
     	lll;
    	//je moet niet alleen de kleur van de Intersection weten, maar ook de int op het board.
    	//de list moet dus een hasmap worden
     	//dat is mooi, want daar kan je ook een set van maken ipv een list
     		for(int i = 0; i < neightboursList.size(); i++) {
    			Intersection checkingThisNeightbour = neightboursList.get(i);
    			if(checkingThisNeightbour == intersections.get(a)) {//zelfde als de steen waar we de buren van hebben gevraagd? dan horen ze bij dezelfde groep
    				sameColorNeightbours.add(checkingThisNeightbour);
    			} else {
    				notSameColorNeightbours.add(checkingThisNeightbour);//dit is de rand van de groep (kan ook een lege intersectie zijn)
    			}
     		}
     		if(neightboursList == sameColorNeightbours) {
     			neightboursListSize = neightboursList.size();
     		}
    }

	public Score score(String boardstring) {
		llll;
		//lalalalala
		//bereken points black en points white
		
		int pointsBlack = 0;
		int pointsWhite = 0;
		String scoreString = pointsBlack + ";" + pointsWhite;
		Score score = new Score(scoreString);
		return score;
	}
	
	public String winner(Score score) {
		int pointsBlack = score.pointsBlack;
		int pointsWhite = score.pointsWhite;
		int winnerColor = 0;
		String winner = "";
		if (pointsBlack > pointsWhite) {
			winnerColor = 1;
		}
		else {
			winnerColor = 2;
		}
		
		if(player1ColorIndex == winnerColor) {
			winner = player1Name;
		}
		else {
			winner = player2Name;
		}
		return winner;
	}
	
	public int getPlayerColor(String playerName) {
		if(playerName == player1Name) {
			return player1ColorIndex;
		} else {
			if(player1ColorIndex == 1) {
				return 2;
			} else {
				return 1;
			}
		}
	}

}
