package ownCode;

import java.util.ArrayList;

public class Board {
    public static final int DIM = 3;/**deze krijgt hij mee vanuit de client of game */
    private Intersection[] intersections;
    
   // ---Constructors----

    //krijgt een string mee van DIM*DIM lengte, waarbij de characters kunnen zijn: 0,1,2
    //maakt elke beurt een nieuw board aan, dus geen behoefte aan een diepcopy
    public Board(String boardstring) {
    	String stringArray[] = boardstring.split("\\B");
    	int integerArray[] = new int[stringArray.length];
    	for (int i = 0; i < stringArray.length; i++) {
    		integerArray[i] = Integer.parseInt(stringArray[i]);
    	}
    
    	intersections = new Intersection[DIM*DIM];
    	for(int i = 0; 0 <= i && i < DIM*DIM; i++) {
    		if(integerArray[i] == 0) {
    			intersections[i]=Intersection.EMPTY;
    		}
    		if(integerArray[i] == 1) {
    			intersections[i]=Intersection.BLACK;
    		}
    		if(integerArray[i] == 2) {
    			intersections[i]=Intersection.WHITE;
    		}
    	}
    }
    

    //---booleans---
  //checkt of de intersectie er is
    public boolean isIntersection(int i) {
    	if(0 <= i && i < DIM*DIM) {
    		return true;
    	}
        return false;
    }
    
    //checkt of het een intersectie is en of deze leeg is
    public boolean isEmptyIntersection(int i) {
    	if(isIntersection(i) == true && intersections[i]==Intersection.EMPTY) {
    		return true;
    	}
        return false;
    }
    
    //kijkt of het board vol is
    public boolean isFull() {
    	int teller=0;
    	for(int i = 0; 0 <= i && i < DIM*DIM; i++)
    		if(this.intersections[i] == Intersection.EMPTY) {
    			teller++;
    		}
    	if(teller == 0) {
    		return true;
    	}
        return false;
    }
    /**
     * pas interessant als ik me bezig ga houden met score/captures berekenen
     
    public boolean isIntersectionOnEdge(int i) {
    	/** moet nog geimplementeerd worden 
    	return false;
    }
    
    public boolean isIntersectionInCorner(int i) {
    	/** moet nog geimplementeerd worden 
    	return false;
    }
    */

    //---queries---
    //geeft intersectie van de index, mits die op het board is
    public Intersection getIntersection(int i) {
    	if(isIntersection(i)) {
    		return intersections[i];
    	}
        return null;
    }
    
    /**
     * pas interessant als ik me bezig ga houden met score/captures berekenen
     
    //geeft buren van een intersectie
    public Intersection getNeighbors(int i) {
    	/**moet nog geimplementeerd worden
    	return null;
    }
	*/

    
    //---voids---
    //sets intersection met index i naar intersectie-enum-optie playerColour
    public void setIntersection(int i, Intersection playerColour) {
    	if(isIntersection(i)) {
    			intersections[i] = playerColour;
    	}
    }
    
    // maakt intersections[] naar een string
    public String toBoardstring() {
        String boardstring = null;
        int tempi = 0;
        String temps;
        for(int i = 0; 0 <= i && i < DIM*DIM; i++) {
        	if(intersections[i] == Intersection.EMPTY) {
    			tempi = 0;
    		}
    		if(intersections[i] == Intersection.BLACK) {
    			tempi = 1;
    		}
    		if(intersections[i] == Intersection.WHITE) {
    			tempi = 2;
    		}
        	 temps = Integer.toString(tempi);
        	 boardstring = boardstring + temps;
        }
        return boardstring;
    }

}