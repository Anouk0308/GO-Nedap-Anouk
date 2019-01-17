package ownCode.test;

import ownCode.*;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NaiveStrategyTest {
	private NaiveStrategy ns;
	private Board board;


    @Before
    public void setUp() {
    	ns = new NaiveStrategy();
        board = new Board("1212", 2);
    }

	//controleer of getName "Naive" terug geeft
    @Test
    public void testGetName() {
    	assertEquals(ns.getName(), "Naive");
    }
    
	//controleer of determineMove -1 geeft als het full is. de rest kan niet getest worden omdat het een random getal kan zijn
    @Test
    public void testDetermineMove() {
    	assertEquals(ns.determineMove(board), -1);
    }
}



