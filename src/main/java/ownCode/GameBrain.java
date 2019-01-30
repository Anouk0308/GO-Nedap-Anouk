package ownCode;

import java.util.*;

public class GameBrain {
	public ArrayList<String> boardHistory;
	public String oldboardstring;
	public String newboardstring;
	public Player p; 
	public int DIM;
	public Board board;

	public GameBrain(String obs, int DIM, Player p){
		this.oldboardstring = obs;
		this.p = p;
		this.DIM = DIM;
		this.boardHistory = new ArrayList<String>();
		this.board = new Board(oldboardstring,DIM);
	}

	
	//houdt bij of de boardstring niet al eerder is voorgekomen in het spel
	public void updateBoardHistory(String oldboardstring) {
		if( this.boardHistory.contains(oldboardstring)) {
			print("er is niet gepasst door de andere speler, maar heb wel dezelfde boardstring gekregen vanuit de server");
		}
		else {
			boardHistory.add(oldboardstring);
		}
	}
	
	//maakt een move
	public int setMove(String oldboardstring) {
		this.updateBoardHistory(oldboardstring);
		int temp = -2;
		int move = p.determineMove(board);
				
		if(move == -1) {
			temp = move;
		} else if( validMove(move)) {
			temp = move;
		} else {
			temp = this.setMove(oldboardstring); //probeer opnieuw
		}
		return temp;
	}
	
	// Valid betekend als de intersectie leeg is en als het niet een board creeert die al eerder in het spel is voorgekomen
	public boolean validMove(int move) {
		if(board.isEmptyIntersection(move)) {
			board.setIntersection(move, p.getPlayerColour());
			newboardstring = board.toBoardstring();
			board = new Board(oldboardstring, DIM);
			if(!this.boardHistory.contains(this.newboardstring)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
		
	}
	
	public String getNewboardstring() {
		return newboardstring;
	}
	
	public void print(String s) {
		System.out.println(s);
	}

}

