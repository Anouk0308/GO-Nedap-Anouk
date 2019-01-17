package ownCode;

public abstract class Player {
	private String name;
	private Intersection intersection;
	
	public Player(String name, Intersection intersection) {
        if(name != null) {
    	this.name = name;
        }
        if(intersection == intersection.BLACK || intersection == intersection.WHITE) {
        this.intersection = intersection;
        }
    }
	
	public String getName() {
        return name;
    }
	
	public Intersection getPlayerColour() {
        return intersection;
    }
	
	public abstract int determineMove(Board board);
	
	

}
