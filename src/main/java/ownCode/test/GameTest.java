package ownCode.test;

import ownCode.*;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GameTest {
	private String notChecked;
	private Game g;
	private String player1Name;
	private int player1ColorIndex;
	private String player2Name;
	private int DIM;
	private int gameID;
	
	
	@Before
	public void setUp() {
		notChecked = "120220000";
		player1Name = "Jan";
		player1ColorIndex = 1;
		player2Name = "Piet";
		DIM = 3;
		gameID = 99;
		g = new Game(player1Name, player1ColorIndex, player2Name, DIM, gameID);

	}
	
	//conctoleer of checkForCaptures() werkt
	@Test
	public void testCFC() {
		g.boardstring = notChecked;
		assertEquals(g.checkForCaptures(notChecked, DIM), "020220000");
	}
	

}
