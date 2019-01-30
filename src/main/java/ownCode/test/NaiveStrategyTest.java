package ownCode.test;

import ownCode.*;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NaiveStrategyTest {
	private NaiveStrategy ns;
	private String boardstring;


    @Before
    public void setUp() {
    	ns = new NaiveStrategy();
        boardstring = "1212";
    }

	//controleer of getName "Naive" terug geeft
    @Test
    public void testGetName() {
    	assertEquals(ns.getName(), "Naive");
    }
    
	//controleer of determineMove -1 geeft als het full is. de rest kan niet getest worden omdat het een random getal kan zijn
    @Test
    public void testDetermineMove() {
    	assertEquals(ns.determineMove(boardstring), -1);
    }
}



