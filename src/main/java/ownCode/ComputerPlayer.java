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
	
	public ComputerPlayer(Intersection in) {
		super("Computer", in);
	    NaiveStrategy ng = new NaiveStrategy();
	    this.g = ng;
	}
	
	public String getName() {
       return g.toString() + "-" + intersection.toString() ;
   }

	public int determineMove(Board board) {
		return g.determineMove(board, intersection);
	}
	
}

