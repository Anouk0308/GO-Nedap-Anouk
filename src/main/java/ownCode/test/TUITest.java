package ownCode.test;

import org.junit.Before;
import org.junit.Test;

import ownCode.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TUITest {
	private TUI t1;
	private TUI t2;
	private TUI t3;
	private TUI t4;
	private TUI t5;
	private TUI t6;
	
	 @Before
	    public void setUp() {
	        t1 = new TUI("0000", 2);
	        t2 = new TUI("1111", 2);
	        t3 = new TUI("2222", 2);
	        t4 = new TUI("000000000", 3);
	        t5 = new TUI("111111111", 3);
	        t6 = new TUI("222222222", 3);
	 }
	 
	 //print TUI uit en test visueel
	 @Test
	 public void testTUIVisueel() {
		 System.out.println("controleer visueel");
	 }


}
