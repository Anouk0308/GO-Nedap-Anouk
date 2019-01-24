package ownCode;

import java.util.ArrayList;
import java.util.List;

public class Board {
    public int DIM;/**deze krijgt hij mee vanuit de client of game */
    public Intersection[] intersections;
    
   // ---Constructors----

    //krijgt een string mee van DIM*DIM lengte, waarbij de characters kunnen zijn: 0,1,2
    //maakt elke beurt een nieuw board aan, dus geen behoefte aan een diepcopy
    public Board(String boardstring, int DIM) {
    	this.DIM = DIM;
    	String stringArray[] = boardstring.split("\\B");
    	intersections = new Intersection[DIM*DIM];
    	for(int i = 0; 0 <= i && i < DIM*DIM; i++) {
    		if(stringArray[i].equals("0")) {
    			intersections[i]=Intersection.EMPTY;
    		}
    		if(stringArray[i].equals("1")) {
    			intersections[i]=Intersection.BLACK;
    		}
    		if(stringArray[i].equals("2")) {
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
   
    //---queries---
    //geeft buren van een intersectie
    public List<Intersection> getNeighbors(int i, int DIM, List<Intersection> intersections) {
    	List<Intersection> neightboursList = new ArrayList<Intersection>();
    	//een hoek op het board
	    	//int linkerbovenhoek = 0
	    	//int rechterbovenhoek = DIM-1
	    	//int rechteronderhoek = DIM*DIM-DIM
	    	//int linkerbovenhoek = DIM*DIM-1
    	//intersectie op een rand
	    	//bovenste rij = tussen 0 en DIM-1
	    	//onderste rij = tussen DIM*DIM-DIM en DIM*DIM-1
	    	//linker kolom = i%DIM = 0
	    	//rechter kollom = i%DIM = DIM-1
    	//Toevoegen van buren
	    	//linkerbuur: 	neightboursList.add(intersections.get(i-1))
			//rechterbuur: 	neightboursList.add(intersections.get(i+1))
			//bovenbuur:	neightboursList.add(intersections.get(i-DIM))
			//onderbuur:	neightboursList.add(intersections.get(i+DIM))

    	if(i == 0) {
    		neightboursList.add(intersections.get(i+1));
    		neightboursList.add(intersections.get(i+DIM));
    	} else if(i == DIM-1){
    		neightboursList.add(intersections.get(i-1));
    		neightboursList.add(intersections.get(i+DIM));
    	} else if(i == DIM*DIM-DIM){
    		neightboursList.add(intersections.get(i+1));
    		neightboursList.add(intersections.get(i-DIM));
    	} else if(i == DIM*DIM-1){
    		neightboursList.add(intersections.get(i-1));
    		neightboursList.add(intersections.get(i-DIM));
    	} else if(0 < i << DIM-1){
    		neightboursList.add(intersections.get(i+1));
    		neightboursList.add(intersections.get(i-1));
    		neightboursList.add(intersections.get(i+DIM));
    	} else if(DIM*DIM-DIM < i << DIM*DIM-1){
    		neightboursList.add(intersections.get(i+1));
    		neightboursList.add(intersections.get(i-1));
    		neightboursList.add(intersections.get(i-DIM));
    	} else if(i%DIM == 0){
    		neightboursList.add(intersections.get(i+1));
    		neightboursList.add(intersections.get(i-DIM));
    		neightboursList.add(intersections.get(i+DIM));
    	} else if(i%DIM == DIM-1){
    		neightboursList.add(intersections.get(i-1));
    		neightboursList.add(intersections.get(i-DIM));
    		neightboursList.add(intersections.get(i+DIM));
    	}else {
    		neightboursList.add(intersections.get(i-1));
    		neightboursList.add(intersections.get(i+1));
    		neightboursList.add(intersections.get(i-DIM));
    		neightboursList.add(intersections.get(i+DIM));
    	}
    	return neightboursList;
    }
    
    //geeft intersectie van de index, mits die op het board is
    public Intersection getIntersection(int i) {
    	if(isIntersection(i)) {
    		return intersections[i];
    	}
        return null;
    }

    //---voids---
    //sets intersection met index i naar intersectie-enum-optie playerColour
    public void setIntersection(int i, Intersection playerColour) {
    	if(isIntersection(i)) {
    			intersections[i] = playerColour;
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
    		}
    		if(intersections[i] == Intersection.BLACK) {
    			tempi = 1;
    		}
    		if(intersections[i] == Intersection.WHITE) {
    			tempi = 2;
    		}
        	 temps = Integer.toString(tempi);
        	 newboardstring = newboardstring + temps;
        }
        return newboardstring;
    }

}
