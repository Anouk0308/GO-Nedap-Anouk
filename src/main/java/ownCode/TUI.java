package ownCode;

public class TUI {
	public String newboardstring;
	public int DIM;
	private String[] intersections;
	private static final String LINE = "-";
	private static final String COLUMN = "  |";
	private static final String BLACK = "BL";
	private static final String WHITE = "WH";
	private String DC;
	
	public TUI(String newboardstring, int DIM) {
		this.newboardstring = newboardstring;
		this.DIM = DIM;
		System.out.println("test: newboardstring in TUI" + newboardstring);
		
		String stringArray[] = newboardstring.split("\\B");
    
		//bepaal of het een cijfer is, BL of WH
    	intersections = new String[DIM*DIM];
    	for(int i = 0; 0 <= i && i < DIM*DIM; i++) {
    		if(stringArray[i].equals("0")) {
    			intersections[i]=String.format("%02d", i);
    		}
    		if(stringArray[i].equals("1")) {
    			intersections[i]=BLACK;
    		}
    		if(stringArray[i].equals("2")) {
    			intersections[i]=WHITE;
    		}
    	}
    	
    	//print het board uit
    	for(int i = 0; i<DIM*DIM; i+=DIM) {
    	
	    	//rij aan verticalen strepen
	    	DC = "";
	    	for(int a = 0; a<DIM; a++) {
	    		DC = DC + COLUMN;
	    	}
	    	System.out.print(DC);
	    	System.out.println("");
	    	
	    	//rij aan hozisontale strepen + getallen/letters
	    	System.out.print(LINE);
	    	for(int b = i; b< (i+DIM); b++) {
	    		System.out.print(intersections[b] + LINE);
	    	}
	    	System.out.println("");
    	}
	
		//rij aan verticalen strepen
		DC = "";
		for(int i = 0; i<DIM; i++) {
			DC = DC + COLUMN;
		}
		System.out.print(DC);
    	System.out.println("");
	}
}
/**
 
 * public String toString() {
        String s = "";
        for (int i = 0; i < DIM; i++) {
            String row = "";
            for (int j = 0; j < DIM; j++) {
                row = row + " " + getField(i, j).toString() + " ";
                if (j < DIM - 1) {
                    row = row + "|";
                }
            }
            s = s + row + DELIM + NUMBERING[i * 2];
            if (i < DIM - 1) {
                s = s + "\n" + LINE + DELIM + NUMBERING[i * 2 + 1] + "\n";
            }
        }
        return s;
    }
    */
