package ownCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

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
	private HashMap<Integer, Intersection> sameColorNeightbours = new HashMap<Integer, Intersection>();
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
    			sameColorNeightbours.put(i, intersections.get(i));//stop de eerste steen in sameColorNeightbours
    			
    		
    			HashMap<Integer, Intersection> neightboursListhsm = board.getNeighbors(i, DIM, intersections);
    			
    			sameColorOrNot(neightboursListhsm, i, intersections);//bekijk of de buren dezelfde kleur hebben als de steen op tileIndex i
    			
    			while(!notSameColorNeightbours.contains(Intersection.EMPTY)) {//als de groep omringt wordt door de andere kleur
    				for(int a = 0; a < intersections.size(); a++) {//ga hashmap sameColorNeig.. af op alle mogelijke tileIndexes die opgeslagen zijn
    					if(sameColorNeightbours.get(a)!= null) {//als er op deze tileIndex dus WEL een intersectie in de hashmap opgeslagen is
    						board.setIntersection(a, Intersection.EMPTY);
    					}
    	
    				}
    				String checkedBoardstring = board.toBoardstring();
    				return checkedBoardstring;
    			}
    			return notYetCheckedBoardstring;//niks is veranderd
    		}
    	}
    	return notYetCheckedBoardstring;
	}

    public void sameColorOrNot(HashMap<Integer, Intersection> neightboursList, int a, List<Intersection> intersections) {//a tileIndex eerste steen & intersections weet of het black/white is
     		for(int i = 0; i < intersections.size(); i++) {
    			Intersection checkingThisNeightbour = neightboursList.get(i);//get in hsm is met gegeven keyvalue (niet de plaats in de hsm maar megegeven value)
    			if(checkingThisNeightbour == intersections.get(a)) {//zelfde als de steen waar we de buren van hebben gevraagd? dan horen ze bij dezelfde groep
    				if(!sameColorNeightbours.containsKey(i)) {//Dus een nieuwe steen die ook in dezelfde groep hoort
    					sameColorNeightbours.put(i, checkingThisNeightbour);//stop nieuwe bij de groep
    					sameColorOrNot(sameColorNeightbours, a, intersections);//ook van de nieuw toegevoegde steen wil je weten of er buren zijn met dezelfde kleur
    				}
    			} else {
    				notSameColorNeightbours.add(checkingThisNeightbour);//dit is de rand van de groep (kan ook een lege intersectie zijn)
    			}
     		}
     		/**
     		if(neightboursList == sameColorNeightbours) {
     			neightboursListSize = neightboursList.size();
     		}*/
    }

	public Score score(String boardstring, int DIM) {
		String checkedBoardstring = capturedEmptyfields(boardstring, DIM);//elk vak aan lege intersecties dat gecaptured is door 1 kleur wordt omgezet in stenen in die kleur
		
		Board board = new Board(checkedBoardstring, DIM);
		Intersection[] intersectionsArray = board.intersections;
		int pointsBlack = 0;
		int pointsWhite = 0;
		for(int i = 0; i < intersectionsArray.length; i++) {
			if(intersectionsArray[i] == Intersection.BLACK) {
				pointsBlack = pointsBlack + 1;
			}
			else if(intersectionsArray[i] == Intersection.WHITE) {
				pointsBlack = pointsWhite + 1;
			}
		}
		String scoreString = pointsBlack + ";" + pointsWhite;
		Score score = new Score(scoreString);
		return score;
	}
	
	public String capturedEmptyfields(String boardstring, int DIM) {
		Board board = new Board(boardstring, DIM);
		Intersection[] intersectionsArray = board.intersections;
		List<Intersection> intersections = new ArrayList<Intersection>(Arrays.asList(intersectionsArray));
		
    	
    	for(int i = 0; i < intersections.size(); i++) {
    		if(intersections.get(i) != Intersection.EMPTY) {
    			//zijn nu bezig met lege vlakken te zoeken
    		}
    		else {
    			sameColorNeightbours.put(i, intersections.get(i));//stop de lege intersectie in sameColorNeightbours
    			
    			HashMap<Integer, Intersection> neightboursListhsm = board.getNeighbors(i, DIM, intersections);
    			sameColorOrNot(neightboursListhsm, i, intersections);//bekijk of de buren dezelfde kleur hebben als de steen op tileIndex i
    			
    			if(notSameColorNeightbours.contains(Intersection.WHITE) && !notSameColorNeightbours.contains(Intersection.BLACK)) {//als de groep alleen omringt door wit 
    				for(int a = 0; a < intersections.size(); a++) {//ga hashmap sameColorNeig.. af op alle mogelijke tileIndexes die opgeslagen zijn
    					if(sameColorNeightbours.get(a)!= null) {//als er op deze tileIndex dus WEL een intersectie in de hashmap opgeslagen is
    						board.setIntersection(a, Intersection.WHITE);
    					}
    				}
    				String checkedBoardstring = board.toBoardstring();
    				return checkedBoardstring;
    			} else if(!notSameColorNeightbours.contains(Intersection.WHITE) && notSameColorNeightbours.contains(Intersection.BLACK)) {//als de groep alleen omringt door zwart 
    				for(int a = 0; a < intersections.size(); a++) {//ga hashmap sameColorNeig.. af op alle mogelijke tileIndexes die opgeslagen zijn
    					if(sameColorNeightbours.get(a)!= null) {//als er op deze tileIndex dus WEL een intersectie in de hashmap opgeslagen is
    						board.setIntersection(a, Intersection.BLACK);
    					}
    				}
    				String checkedBoardstring = board.toBoardstring();
    				return checkedBoardstring;
    			} else {
    				return boardstring;
    			}
    		}
    		return boardstring;
    	}
    	return boardstring;
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
