package ownCode.test;

import ownCode.*;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GameTest {
	private String notChecked1;
	private String notChecked2;
	private String empty;
	private Game g;
	private String player1Name;
	private int player1ColorIndex;
	private String player2Name;
	private int DIM;
	private int gameID;
	private int tileIndexMove;
	
	
	@Before
	public void setUp() {
		notChecked1 = "120220000";
		notChecked2 = "000000021";
		empty = "000000000";
		player1Name = "Jan";
		player1ColorIndex = 1;
		player2Name = "Piet";
		DIM = 3;
		gameID = 99;
		g = new Game(player1Name, player1ColorIndex, player2Name, DIM, gameID);
		tileIndexMove = 8;

	}
	
	//conctoleer of checkForCaptures() werkt
	@Test
	public void testCFC() {
		g.boardstring = notChecked1;
		assertEquals(g.checkForCaptures(notChecked1, DIM), "020220000");
		g.boardstring = notChecked2;
		String test = g.checkForCaptures(notChecked2, DIM);
		assertEquals(test, "000000021");
	}
	
	//test updateBoard, specifiek op of er een goede newboardstring wordt gemaakt
	@Test
	public void testUB() {
		assertEquals(g.updateBoard(player1Name, tileIndexMove, empty, DIM), "000000001");
	}
	
	//test score
	@Test
	public void testScore() {
		//eerst capturedEmptyFields testen
	}
	
	//test capturedEmptyFields
	@Test
	public void testCEF() {
		String s = g.capturedEmptyfields("1000", 2);
		assertEquals("1111", s);
		//String s = g.capturedEmptyfields("0000", 2);
		//assertEquals("0000", s);
	}

}
