package ownCode;

public class NaiveStrategy implements Strategy{
	

	public String getName() {
		return "Naive";
	}
	
	//deze strategie is letterlijk math.random
	public int determineMove(String boardstring) {
		double DDIM = Math.sqrt((double)boardstring.length());
		int DIM = (int)DDIM;
		
		if(!boardstring.contains("0")) {
			return -1;
		}
		else {
			int max = DIM*DIM;
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

