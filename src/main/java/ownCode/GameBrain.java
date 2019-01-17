package ownCode;

import java.util.*;

public class GameBrain {
	public ArrayList<String> boardHistory;
	public String oldboardstring;//krijgt Game van de Client
	public String newboardstring;//gaat naar de client
	public Board board;
	public Player p;
	public static final int DIM = 0;//deze krijgt hij mee vanuit de client
	public boolean playerPasses = false;
	public boolean otherPlayerPasses = false;//deze krijgt game an client
	public boolean exit = false;
	
	//game krijgt van client een oldboarstring om het huidige board te bepalen en een player om te bepalen welke strategy te gebruiken voor het bepalen van de move (kan niet alleen strategie meegeven, omdat we ook een human player moeten maken)
	// game geeft client een newboardstring met een move toegepast, waarbij game zelf checkt of de move valid is
	public GameBrain(String oldboardstring, Player p){
		p = this.p;
		oldboardstring = this.oldboardstring;
		this.board = new Board(oldboardstring);
	}
	
	//deze boolean houdt bij of beide players gepassed hebben. In dat geval is het spel afgelopen en kan de score bepaald worden
	public boolean twoTimesPassed() {
		if(playerPasses == true && otherPlayerPasses == true){
			exit = true;
			return true;
		}
		return false;
	}
	
	//houdt bij of de boardstring niet al eerder is voorgekomen in het spel
	public void updateBoardHistory() {
		if(otherPlayerPasses == false) {
			if( this.boardHistory.contains(this.oldboardstring) == false) {
				boardHistory.add(oldboardstring);
			}
			else {
				System.out.println("er is niet gepasst door de andere speler, maar heb wel dezelfde boardstring gekregen vanuit de server");
			}
		}
	}

	//foundGoodMove geeft aan of er een valid move gevonden is. Valid betekend als de intersectie leeg is en als het niet een board creeert die al eerder in het spel is voorgekomen
	public void play() {
		while (exit = false) {
			boolean foundGoodMove = false;
			int move = p.determineMove(board);
			while(!foundGoodMove) {
				if(move == -1) {
					playerPasses = true;
					newboardstring = oldboardstring;
					foundGoodMove = true;
				}
				if(move > -1 && board.isEmptyIntersection(move)) {
					board.setIntersection(move, p.getPlayerColour());
					newboardstring = board.toBoardstring();
					if(this.boardHistory.contains(this.newboardstring)) {
						this.play();
					}
					else {
						foundGoodMove = true;
					}					
				}
				else {
					this.play();
				}
			}
		}
	}

}
