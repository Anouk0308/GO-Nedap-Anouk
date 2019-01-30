package ownCode.test;

import ownCode.*;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class BoardTest {
    private Board board1;
    private Board board2;
    private Board board3;
    private Board board4;

    @Before
    public void setUp() {
        board1 = new Board("0000", 2);
        board2 = new Board("0101", 2);
        board3 = new Board("1212", 2);
        board4 = new Board("000000021",3);
    }

    //cotroleer of er een intersection array wordt gemaakt met de boardstring && controleer of toBoardstring de intersections[] goed omzet naar een boardstring
    @Test
    public void testBoardstringToIntersectionArrayToBoardstring() {
    	assertEquals(board1.toBoardstring(), "0000");
    	assertEquals(board2.toBoardstring(), "0101");
    	assertEquals(board3.toBoardstring(), "1212");
    }
  
    //controleer of isIntersection opmerkt als getal niet tussen -1 en DIM*DIM-1 ligt
    @Test
    public void testIsIntersection() {
        assertFalse(board1.isIntersection(-1));
        assertTrue(board1.isIntersection(0));
        assertFalse(board1.isIntersection(8));
        assertFalse(board1.isIntersection(9));
    }
    
    //controleer of isEmptyIntersection opmerkt of het een intersectie is en of deze empty is
    @Test
    public void testIsEmptyIntersection() {
        board1.setIntersection(0, Intersection.BLACK);
        assertFalse(board1.isEmptyIntersection(0));
        assertTrue(board1.isEmptyIntersection(1));
    }
    //controleer of isFull opmerkt als het boord vol is
    @Test
    public void testIsFull() {
        assertFalse(board1.isFull(board1.boardstring));
        assertFalse(board2.isFull(board2.boardstring));
        assertTrue(board3.isFull(board3.boardstring));
    }
    
    //controleer of setIntersection de intersectie omzet naar een van de andere opties & of getIntersection het kan uitlezen
    @Test
    public void testSetandGetIntersection() {
        board2.setIntersection(0, Intersection.BLACK);
        assertEquals(Intersection.BLACK, board2.getIntersection(0));
        assertEquals(Intersection.BLACK, board2.getIntersection(1));
    }
    
    //controleer getNeighbours
    @Test
    public void testGetNeighbours() {
    	int i = 7;
    	int DIM = 3;
    	Intersection[] Array = board4.intersections;
    	List<Intersection> intersections = new ArrayList<Intersection>(Arrays.asList(Array));;
    	HashMap<Integer, Intersection> hsm = board4.getNeighbours(i, DIM, intersections);
    	System.out.println(hsm);
    }
 
}
