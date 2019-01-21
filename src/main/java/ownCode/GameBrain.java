package ownCode;

import ownCode.test.*;

import java.util.*;

public class GameBrain {
	public ArrayList<String> boardHistory;//komt telkens een nieuwe bij
	public String oldboardstring;//krijgt Game van de Client
	public String newboardstring;//gaat naar de client
	public Player p; //krijgt mee van client
	public int DIM;//deze krijgt hij mee vanuit de client
	public boolean playerPasses = false; // wordt bepaald in play via determineMove
	public boolean otherPlayerPasses = false;//deze krijgt game van client
	public boolean exit = false; //geeft mee aan client
	public Board board;
	
	//game krijgt van client een oldboarstring om het huidige board te bepalen en een player om te bepalen welke strategy te gebruiken voor het bepalen van de move (kan niet alleen strategie meegeven, omdat we ook een human player moeten maken)
	// game geeft client een newboardstring met een move toegepast, waarbij game zelf checkt of de move valid is
	public GameBrain(String obs, int DIM, Player p, boolean opp){
		this.oldboardstring = obs;
		this.p = p;
		this.DIM = DIM;
		this.otherPlayerPasses = opp;
		this.boardHistory = new ArrayList<String>();
		this.board = new Board(oldboardstring,DIM);
	}

	
	//houdt bij of de boardstring niet al eerder is voorgekomen in het spel
	public void updateBoardHistory(String oldboardstring) {
		if(otherPlayerPasses == false) {
			if( this.boardHistory.contains(oldboardstring)) {
				System.out.println("er is niet gepasst door de andere speler, maar heb wel dezelfde boardstring gekregen vanuit de server");
			}
			else {
				boardHistory.add(oldboardstring);
			}
		}
	}

	//foundGoodMove geeft aan of er een valid move gevonden is. Valid betekend als de intersectie leeg is en als het niet een board creeert die al eerder in het spel is voorgekomen
	public int setMove(String oldboardstring) {
		this.updateBoardHistory(oldboardstring);
		int temp = -2;
		int move = p.determineMove(board);
				
		if(move == -1) {
			playerPasses = true;
			temp = move;
		}
		else if( board.isEmptyIntersection(move)) {
			board.setIntersection(move, p.getPlayerColour());
			newboardstring = board.toBoardstring();
			board = new Board(oldboardstring, DIM);
			if(!this.boardHistory.contains(this.newboardstring)) {
				temp = move;
			}
		}
		else {
			temp = this.setMove(oldboardstring);
		}
		return temp;
	}
	
	public String getNewboardstring() {
		return newboardstring;
	}

}

