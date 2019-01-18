package ownCode.test;

import ownCode.*;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.*;

public class GameBrainTest {
	private String obs1;
	private String obs2;
	private String obs3;
	private String obs4;
	private int DIM;
	private Intersection i;
	private NaiveStrategy ng;
	private ComputerPlayer cp;
	private boolean otherPlayerPasses;
	private GameBrain gb1;
	private GameBrain gb2;
	
	 @Before
	 public void setUp() {
		 obs1 = new String("1210");
		 obs2 = new String("1212");
		 obs3 = new String("1111");
		 obs4 = new String("2222");
		 DIM = 2;
		 ng = new NaiveStrategy();
		 i = Intersection.BLACK;
		 cp = new ComputerPlayer(i, ng);
		 otherPlayerPasses = false;
		 
		 gb1 = new GameBrain(obs1, DIM, cp, otherPlayerPasses);
		 gb2 = new GameBrain(obs2, DIM, cp, otherPlayerPasses);
	 }

	//test of updateBoardHistory() iets add & test of updateBoardHistory aangeeft als er 2 stringen hetzelfde zijn in geval dat other neit gepast heeft
	@Test
	 public void testUBH() {
		gb1.updateBoardHistory(obs3);
		assertEquals(gb1.boardHistory.get(0), obs3);
		gb1.updateBoardHistory(obs4);
		assertEquals(gb1.boardHistory.get(1), obs4);
		gb1.updateBoardHistory(obs3);
		assertTrue(gb1.boardHistory.contains(obs3));
	 }
	
	//test of setMove speelt als boolean exit = true
	@Test
	public void testExit() {
		gb1.exit = true;
		assertEquals(gb1.setMove(),"");
	}
	
	
	//test of play een valid move kan vinden op board met leeg vlak
	@Test
	public void testValidMove() {
		String temp = gb1.setMove();
		System.out.println(temp);
		assertEquals(gb1.setMove(),"1211");
	}
	
	//test of play passes als board vol is
	/**@Test
	public void testPassesWhenFull() {
		assertEquals(gb2.setMove(),"1212");
	}*/
	
}
	 
	
	

