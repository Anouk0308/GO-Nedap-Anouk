package ownCode.test;


import ownCode.*;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class GameStateTest {
	private GameState gs1;
	private GameState gs2;
	private GameState gs3;

    @Before
    public void setUp() {
        gs1 = new GameState("WAITING;1;1212");
        gs2 = new GameState("WAITING;1");
        gs3 = new GameState("WAITING;1;1212;45");
    }

    //cotroleer of string goed wordt gesplit
    @Test
    public void splitGameState() {
    	assertEquals(gs1.status, Status.WAITING);
    	assertEquals(gs1.currentPlayer,1);
    	assertEquals(gs1.boardstring, "1212");
    }
    
    //cotroleer of variabelen niet worden gezet bij een verkeerde string
    @Test
    public void testFoutieveString() {
    	assertThat(gs2.status, not(Status.WAITING));
    	assertThat(gs2.currentPlayer,not(1));
    	assertThat(gs2.boardstring, not("1212"));
    	assertThat(gs3.status, not(Status.WAITING));
    	assertThat(gs3.currentPlayer,not(1));
    	assertThat(gs3.boardstring, not("1212"));
    }
}
