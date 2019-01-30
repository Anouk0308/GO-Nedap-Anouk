package ownCode;

import java.util.HashMap;
import java.util.List;

public class Board {
    public int DIM;/**deze krijgt hij mee vanuit de client of game */
    public Intersection[] intersections;
    public String boardstring;
    
    //krijgt een string mee van DIM*DIM lengte, waarbij de characters kunnen zijn: 0,1,2,3
    public Board(String boardstring, int DIM) {
    	this.DIM = DIM;
    	this.boardstring = boardstring;
    	String stringArray[] = boardstring.split("\\B");
    	intersections = new Intersection[DIM*DIM];
    	for(int i = 0; 0 <= i && i < DIM*DIM; i++) {
    		if(stringArray[i].equals("0")) {
    			intersections[i]=Intersection.EMPTY;
    		} else if(stringArray[i].equals("1")) {
    			intersections[i]=Intersection.BLACK;
    		} else if(stringArray[i].equals("2")) {
    			intersections[i]=Intersection.WHITE;
    		} else if(stringArray[i].equals("3")) {
    			intersections[i]=Intersection.HINT;
    		} else {
    			print("Something is wrong with the boardstring, it is not a 0/1/2/3");
    		}
    	}
    }
 
    //checkt of de intersectie is op het board
    public boolean isIntersection(int i) {
    	if(0 <= i && i < DIM*DIM) {
    		return true;
    	}
        return false;
    }
    
    //checkt of het een intersectie is en of deze leeg is (een intersectie die 'bezet' is door een hint steen, is natuurlijk ook een lege steen)
    public boolean isEmptyIntersection(int i) {
    	if(isIntersection(i) == true && intersections[i]==Intersection.EMPTY||isIntersection(i) == true && intersections[i]==Intersection.HINT) {
    		return true;
    	}
        return false;
    }
    
    //kijkt of het board vol is
    public boolean isFull(String boardstring) {
    	return !boardstring.contains("0");
    }
   
    //geeft buren van een intersectie
    public HashMap<Integer, Intersection> getNeighbours(int i, int DIM, List<Intersection> intersections) {
    	HashMap<Integer, Intersection> neightboursListhsm = new HashMap<Integer, Intersection>();
    	/*een hoek op het board
	    	int linkerbovenhoek 	= 0
	    	int rechterbovenhoek 	= DIM-1
	    	int rechteronderhoek 	= DIM*DIM-DIM
	    	int linkerbovenhoek 	= DIM*DIM-1
    	intersectie op een rand
	    	bovenste rij 	= tussen 0 en DIM-1
	    	onderste rij 	= tussen DIM*DIM-DIM en DIM*DIM-1
	    	linker kolom 	= (i%DIM = 0)
	    	rechter kollom 	= (i%DIM = DIM-1)
    	Toevoegen van buren
	    	linkerbuur: 	neightboursListhsm.put(i-1, intersections.get(i-1));
			rechterbuur: 	neightboursListhsm.put(i+1, intersections.get(i+1));
			bovenbuur:		neightboursListhsm.put(i-DIM, intersections.get(i-DIM));
			onderbuur:		neightboursListhsm.put(i+DIM, intersections.get(i+DIM));
		*/
    	if(i == 0) {
    		neightboursListhsm.put(i+1, intersections.get(i+1));
    		neightboursListhsm.put(i+DIM, intersections.get(i+DIM));
    	} else if(i == DIM-1){
    		neightboursListhsm.put(i-1, intersections.get(i-1));
    		neightboursListhsm.put(i+DIM, intersections.get(i+DIM));
    	} else if(i == DIM*DIM-DIM){
    		neightboursListhsm.put(i+1, intersections.get(i+1));
    		neightboursListhsm.put(i-DIM, intersections.get(i-DIM));
    	} else if(i == DIM*DIM-1){
    		neightboursListhsm.put(i-1, intersections.get(i-1));
    		neightboursListhsm.put(i-DIM, intersections.get(i-DIM));
    	} else if(0 < i && i < DIM-1){
    		neightboursListhsm.put(i+1, intersections.get(i+1));
    		neightboursListhsm.put(i-1, intersections.get(i-1));
    		neightboursListhsm.put(i+DIM, intersections.get(i+DIM));
    	} else if(DIM*DIM-DIM < i && i < DIM*DIM-1){
    		neightboursListhsm.put(i+1, intersections.get(i+1));
    		neightboursListhsm.put(i-1, intersections.get(i-1));
    		neightboursListhsm.put(i-DIM, intersections.get(i-DIM));
    	} else if(i%DIM == 0){
    		neightboursListhsm.put(i+1, intersections.get(i+1));
    		neightboursListhsm.put(i-1, intersections.get(i-1));
    		neightboursListhsm.put(i+DIM, intersections.get(i+DIM));
    	} else if(i%DIM == DIM-1){
    		neightboursListhsm.put(i-1, intersections.get(i-1));
    		neightboursListhsm.put(i-DIM, intersections.get(i-DIM));
    		neightboursListhsm.put(i+DIM, intersections.get(i+DIM));
    	} else {
    		neightboursListhsm.put(i-1, intersections.get(i-1));
    		neightboursListhsm.put(i+1, intersections.get(i+1));
    		neightboursListhsm.put(i-DIM, intersections.get(i-DIM));
    		neightboursListhsm.put(i+DIM, intersections.get(i+DIM));
    	}
    	return neightboursListhsm;
    }
    
    //geeft intersectie van de index, mits die op het board is
    public Intersection getIntersection(int i) {
    	if(isIntersection(i)) {
    		return intersections[i];
    	}
        return null;
    }

    //zet intersection met index i naar intersectie-enum-optie playerColour
    public void setIntersection(int i, Intersection playerColour) {
    	if(playerColour == Intersection.BLACK || playerColour == Intersection.WHITE) {
	    	if(isIntersection(i)) {
	    			intersections[i] = playerColour;
	    	}
    	} else {
    		print("setIntersection kan alleen met de kleuren wit en zwart");
    	}
    }
    
    //zet een hintsteen op meegegeven index
    public void setHint(int i) {
    	if(isIntersection(i)&&isEmptyIntersection(i)) {
    			intersections[i] = Intersection.HINT;
    	}
    }
    
    // maakt intersections[] naar een string
    public String toBoardstring() {
        String newboardstring = "";
        int tempi = 0;
        String temps;
        for(int i = 0; 0 <= i && i < DIM*DIM; i++) {
        	if(intersections[i] == Intersection.EMPTY) {
    			tempi = 0;
    		} else if(intersections[i] == Intersection.BLACK) {
    			tempi = 1;
    		} else if(intersections[i] == Intersection.WHITE) {
    			tempi = 2;
    		}else if(intersections[i] == Intersection.HINT) {
    			tempi = 3;
    		}
        	 temps = Integer.toString(tempi);
        	 newboardstring = newboardstring + temps;
        }
        return newboardstring;
    }
    
	private static void print(String message){
		System.out.println(message);
	}

}
