package ownCode;

public class NaiveStrategy implements Strategy{
	

	public String getName() {
		return "Naive";
	}
	
	//deze strategie is letterlijk math.random
	public int determineMove(Board b) {
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

