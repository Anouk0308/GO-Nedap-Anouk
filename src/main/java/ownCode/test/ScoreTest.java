package ownCode.test;

import ownCode.*;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class ScoreTest {
	private Score s1;
	private Score s2;
	private Score s3;
	private Score s4;

    @Before
    public void setUp() {
        s1 = new Score("34;36.0");
        s2 = new Score("34;36.0");
        s3 = new Score("34");
        s4 = new Score("34;36.0;45");
    }

    //cotroleer of string goed wordt gesplit
    @Test
    public void splitGameState() {
    	assertEquals(s1.pointsBlack, 34);
    	assertEquals(s1.pointsWhite,36.0);

    }
    
    //cotroleer of variabelen niet worden gezet bij een verkeerde string
    @Test
    public void testFoutieveString() {
    	assertThat(s2.pointsBlack, not(34));
    	assertThat(s2.pointsWhite, not(36));
    	assertThat(s3.pointsBlack, not(34));
    	assertThat(s3.pointsWhite, not(36));
    	assertThat(s4.pointsBlack, not(34));
    	assertThat(s4.pointsWhite, not(36));
    }
}