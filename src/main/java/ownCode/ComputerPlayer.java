package ownCode;


public class ComputerPlayer extends Player{
	 private Intersection intersection;
	 private Strategy g;
	
	 /** voor als ik meerdere strategieen heb
	public ComputerPlayer(Intersection in, Strategy g) {
		super("Computer", in);
		if(g != null) {
		   this.g = g;
		   this.intersection = in;
	   }
	}
	*/
	
	public ComputerPlayer(Intersection in, Strategy g) {
		super("Computer", in);
		this.intersection = in;
		if(g!= null) {
			this.g = g;
		}
		else {
			NaiveStrategy ng = new NaiveStrategy();
		    this.g = ng;
		}
	}
	
	public String getName() {
		String playerColour = "";
			if(intersection == Intersection.BLACK) {
				playerColour = "Black";
			}
			if(intersection == Intersection.WHITE) {
				playerColour = "White";
			}
		String strategy = "";
			if(g != null) {
				strategy = "Naive";
			}
		return strategy + "-" + playerColour ;
   }

	public int determineMove(Board board) {
		return g.determineMove(board);
	}
	
}

