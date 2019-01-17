package ownCode;

public class NaiveStrategy implements Strategy{
	private Intersection[] intersections;
	
	public NaiveStrategy() {
    	intersections = new Intersection[Board.DIM*Board.DIM];
    	for(int i = 0; 0 <= i && i < Board.DIM*Board.DIM; i++) {
    		this.intersections[i] = Intersection.EMPTY;
    	}
    	
	}
	
	public String getName() {
		return "Naive";
	}
	
	public int determineMove(Board b, Intersection in) {
		if(b.isFull() == true) {
			return -1;
		}
		else {
			int max = b.DIM*b.DIM;
			int min = 0;
			int range = max-min +1;
			int rand = 0;
			
			for(int i=0; i<max; i++) {
				 rand = (int)(Math.random() * range) + min;
				
					}
			return rand;
		}
	}

}

