package ownCode;


public class ComputerPlayer extends Player{
	 private Intersection intersection;
	 private Strategy g;
	
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
			} else if(intersection == Intersection.WHITE) {
				playerColour = "White";
			} else {
				System.out.println("Something went wrong, you don't have a colour");
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

