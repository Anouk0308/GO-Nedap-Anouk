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
	
	//test capturedEmptyFields
	@Test
	public void testCEF() {
		String s = g.capturedEmptyfields("1000", 2);
		assertEquals("1111", s);
		String ss = g.capturedEmptyfields("0120", 2);
		assertEquals("0120", ss);
	}
	
	//test score
	/**	
		public void testScore() {
			String boardstring = "1111";
			int DIM = 2;
			Score s = g.score(boardstring, DIM);
			Score test = new Score("4;0.5");
			assertEquals(test,s);
			//eerst capturedEmptyFields testen
		}*/
	
	@Test
	public void proberen(){
		String boardstring = "1111";
		int DIM = 2;
		String checkedBoardstring = g.capturedEmptyfields(boardstring, DIM);//elk vak aan lege intersecties dat gecaptured is door 1 kleur wordt omgezet in stenen in die kleur
		assertEquals("1111", checkedBoardstring);
		
		Board board = new Board(checkedBoardstring, DIM);
		Intersection[] intersectionsArray = board.intersections;
		int pointsBlack = 0;
		double pointsWhite = 0.0;
		
		for(int i = 0; i < intersectionsArray.length; i++) {
			if(intersectionsArray[i] == Intersection.BLACK) {
				pointsBlack = pointsBlack + 1;
			}
			else if(intersectionsArray[i] == Intersection.WHITE) {
				pointsWhite = pointsWhite + 1.0;
			}
		}
		pointsWhite = pointsWhite + 0.5;
		String scoreString = pointsBlack + ";" + pointsWhite;
		assertEquals(scoreString, "4;0.5");
		Score score = new Score(scoreString);
	}

}
