package ownCode.test;

import ownCode.*;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ComputerPlayerTest {
	private ComputerPlayer cp;
	private Board board;
	private Intersection playercolour = Intersection.BLACK;
	private NaiveStrategy ng;
	
	@Before
	public void setUp() {
		cp = new ComputerPlayer(playercolour, ng);
	    board = new Board("1212", 2);
	}
	
	//test of getName de goede string terug geeft
	   @Test
	   public void testGetName() {
		   assertEquals("Naive-Black", cp.getName());
	   }	    
	//test of determineMove een getal geeft als het vol is (rest moeilijk te testen door random getal)
	   @Test
	   public void testDetermineMove() {
		   assertEquals(cp.determineMove(board), -1);
	   }
}