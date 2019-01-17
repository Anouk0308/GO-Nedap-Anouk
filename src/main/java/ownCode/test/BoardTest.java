package ownCode.test;

import ownCode.*;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BoardTest {
    private Board board1;
    private Board board2;
    private Board board3;
    private Intersection[] intersections;
    private static final int DIM = 2;

    @Before
    public void setUp() {
        board1 = new Board("0000");
        board2 = new Board("0101");
        board3 = new Board("1212");
        intersections = new Intersection[DIM*DIM];
        intersections[0]=Intersection.BLACK;
        intersections[1]=Intersection.BLACK;
        intersections[2]=Intersection.BLACK;
        intersections[3]=Intersection.BLACK;
    }

    //cotroleer of er een intersection array wordt gemaakt met de boardstring
    
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
    //controleer of isFulle opmerkt als het boord vol is
    
    //controleer of setIntersection de intersectie omzet naar een van de andere opties & of getIntersection het kan uitlezen
    @Test
    public void testSetandGetIntersection() {
        board2.setIntersection(0, Intersection.BLACK);
        assertEquals(Intersection.BLACK, board2.getIntersection(0));
        assertEquals(Intersection.BLACK, board2.getIntersection(1));
    }
    
    //controleer of toBoardstring de intersections[] goed omzet naar een boardstring
    
    


    @Test
    public void testIsEmptyFieldRowCol() {
        board.setField(0, 0, Mark.XX);
        assertFalse(board.isEmptyField(0, 0));
        assertTrue(board.isEmptyField(0, 1));
        assertTrue(board.isEmptyField(1, 0));
    }

    @Test
    public void testIsFull() {
        for (int i = 0; i < 8; i++) {
            board.setField(i, Mark.XX);
        }
        assertFalse(board.isFull());

        board.setField(8, Mark.XX);
        assertTrue(board.isFull());
    }

    @Test
    public void testGameOverFullBoard() {
        /**
         * xxo
         * oox
         * xxo
         */
        board.setField(0, 0, Mark.XX);
        board.setField(0, 1, Mark.XX);
        board.setField(0, 2, Mark.OO);
        board.setField(1, 0, Mark.OO);
        board.setField(1, 1, Mark.OO);
        board.setField(1, 2, Mark.XX);
        board.setField(2, 0, Mark.XX);
        board.setField(2, 1, Mark.OO);

        assertFalse(board.gameOver());
        board.setField(2, 2, Mark.XX);
        assertTrue(board.gameOver());
    }

    @Test
    public void testHasRow() {
        board.setField(0, Mark.XX);
        board.setField(1, Mark.XX);
        assertFalse(board.hasRow(Mark.XX));
        assertFalse(board.hasRow(Mark.OO));

        board.setField(2, Mark.XX);
        assertTrue(board.hasRow(Mark.XX));
        assertFalse(board.hasRow(Mark.OO));
    }

    @Test
    public void testHasColumn() {
        board.setField(0, Mark.XX);
        board.setField(3, Mark.XX);
        assertFalse(board.hasColumn(Mark.XX));
        assertFalse(board.hasColumn(Mark.OO));

        board.setField(6, Mark.XX);
        assertTrue(board.hasColumn(Mark.XX));
        assertFalse(board.hasColumn(Mark.OO));
    }

    @Test
    public void testHasDiagonalDown() {
        board.setField(0, 0, Mark.XX);
        board.setField(1, 1, Mark.XX);
        assertFalse(board.hasDiagonal(Mark.XX));
        assertFalse(board.hasDiagonal(Mark.OO));

        board.setField(2, 2, Mark.XX);
        assertTrue(board.hasDiagonal(Mark.XX));
        assertFalse(board.hasDiagonal(Mark.OO));
    }

    @Test
    public void testHasDiagonalUp() {
        board.setField(0, 2, Mark.XX);
        board.setField(1, 1, Mark.XX);
        assertFalse(board.hasDiagonal(Mark.XX));
        assertFalse(board.hasDiagonal(Mark.OO));

        board.setField(2, 0, Mark.XX);
        assertTrue(board.hasDiagonal(Mark.XX));
        assertFalse(board.hasDiagonal(Mark.OO));
    }

    @Test
    public void testIsWinner() {
        board.setField(0, Mark.XX);
        board.setField(1, Mark.XX);
        assertFalse(board.isWinner(Mark.XX));
        assertFalse(board.isWinner(Mark.OO));

        board.setField(2, Mark.XX);
        assertTrue(board.isWinner(Mark.XX));
        assertFalse(board.isWinner(Mark.OO));

        board.setField(0, 0, Mark.OO);
        board.setField(1, 1, Mark.OO);
        assertFalse(board.isWinner(Mark.XX));
        assertFalse(board.isWinner(Mark.OO));

        board.setField(2, 2, Mark.OO);
        assertFalse(board.isWinner(Mark.XX));
        assertTrue(board.isWinner(Mark.OO));
    }

    @Test
    public void testHasNoWinnerFullBoard() {
        /**
         * xxo
         * oox
         * xxo
         */
        board.setField(0, 0, Mark.XX);
        board.setField(0, 1, Mark.XX);
        board.setField(0, 2, Mark.OO);
        board.setField(1, 0, Mark.OO);
        board.setField(1, 1, Mark.OO);
        board.setField(1, 2, Mark.XX);
        board.setField(2, 0, Mark.XX);
        board.setField(2, 1, Mark.OO);
        board.setField(2, 2, Mark.XX);
        assertFalse(board.hasWinner());
    }

    @Test
    public void testHasWinnerRow() {
        board.setField(0, Mark.XX);
        board.setField(1, Mark.XX);
        assertFalse(board.hasWinner());

        board.setField(2, Mark.XX);
        assertTrue(board.hasWinner());
    }

    @Test
    public void testHasWinnerColumn() {
        board.setField(0, Mark.XX);
        board.setField(3, Mark.XX);
        assertFalse(board.hasWinner());

        board.setField(6, Mark.XX);
        assertTrue(board.hasWinner());
    }

    @Test
    public void testHasWinnerDiagonal() {
        board.setField(0, Mark.XX);
        board.setField(1, Mark.XX);
        assertFalse(board.hasWinner());

        board.setField(2, Mark.XX);
        assertTrue(board.hasWinner());
    }
}
