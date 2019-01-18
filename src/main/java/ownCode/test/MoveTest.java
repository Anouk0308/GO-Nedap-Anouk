package ownCode.test;

	import ownCode.*;

	import org.junit.Before;
	import org.junit.Test;

	import static org.junit.Assert.*;
	import static org.hamcrest.CoreMatchers.*;

	public class MoveTest {
		private Move m1;
		private Move m2;
		private Move m3;

	    @Before
	    public void setUp() {
	        m1 = new Move("3;1");
	        m2 = new Move("3");
	        m3 = new Move("3;1;4");
	    }

	    //cotroleer of string goed wordt gesplit
	    @Test
	    public void splitMove() {
	    	assertEquals(m1.tileIndex,3);
	    	assertEquals(m1.playerColor,1);
	    }
	    
	  //cotroleer of variabelen niet worden gezet bij een verkeerde string
	    @Test
	    public void testFoutieveString() {
	    	assertThat(m2.tileIndex,not(3));
	    	assertThat(m2.playerColor,not(1));
	    	assertThat(m3.tileIndex,not(3));
	    	assertThat(m3.playerColor,not(1));
	    }
	}
