package ownCode;

public class TUI {
	public String newboardstring;
	public int DIM;
	private String[] intersections;
	private static final String LINE = "-";
	private static final String COLUMN = "  |";
	private static final String BLACK = "BL";
	private static final String WHITE = "WI";
	private static final String HINT = "HH";
	private String row;
	
	//print het board uit met getallen voor lege intersecties, letters voor de andere intersecties en | en - om ruimte te creeeren tussen de intersecties
	public TUI(String newboardstring, int DIM) {
		this.newboardstring = newboardstring;
		this.DIM = DIM;
		
		String stringArray[] = newboardstring.split("\\B");
    
		//bepaal of het een cijfer is, BL of WH
    	intersections = new String[DIM*DIM];
    	for(int i = 0; 0 <= i && i < DIM*DIM; i++) {
    		if(stringArray[i].equals("0")) {
    			intersections[i]=String.format("%02d", i);
    		} else if(stringArray[i].equals("1")) {
    			intersections[i]=BLACK;
    		} else if(stringArray[i].equals("2")) {
    			intersections[i]=WHITE;
    		} else if(stringArray[i].equals("3")) {
    			intersections[i]=HINT;
    		} else {
    			System.out.println("Boardstring contains something else then 0/1/2/3");
    		}
    	}
    	
    	//print het board uit
    	for(int i = 0; i<DIM*DIM; i+=DIM) {
    	
	    	//rij aan verticalen strepen
	    	row = "";
	    	for(int a = 0; a<DIM; a++) {
	    		row = row + COLUMN;
	    	}
	    	System.out.print(row);
	    	System.out.println("");
	    	
	    	//rij aan hozisontale strepen + getallen/letters
	    	System.out.print(LINE);
	    	for(int b = i; b< (i+DIM); b++) {
	    		System.out.print(intersections[b] + LINE);
	    	}
	    	System.out.println("");
    	}
	
		//rij aan verticalen strepen
    	row = "";
		for(int i = 0; i<DIM; i++) {
			row = row + COLUMN;
		}
		System.out.print(row);
    	System.out.println("");
	}

}
