package ownCode.test;

import ownCode.*;

import org.junit.Before;
import org.junit.Test;

import com.nedap.go.gui.GoGuiIntegrator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ServerInputHandlerTest {

private String ah1;
private String rc1;
private String ac1;
private String am1;
private String im1;
private String uc1;
private String us1;
private String gf1;
private ServerInputHandler SIH;
public BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
public Client c = new Client();
public String[] sa1;//ah1
public String[] sa2;//rc1
public String[] sa3;//ac1
public String[] sa4;//am1
public String[] sa5;//im1
public String[] sa6;//uc1
public String[] sa7;//us1
public String[] sa8;//gf1


	

@Before
public void setUp() {
    ah1 = "ACKNOWLEDGE_HANDSHAKE+1+1";
    rc1 = "REQUEST_CONFIG+Please provide a preferred configuration using the SET_CONFIG";
    ac1 = "ACKNOWLEDGE_CONFIG+Thiery Baudet+1+4+PLAYING;2;0000011120001200";
    am1 = "ACKNOWLEDGE_MOVE+1+30;1+PLAYING;2;0000011120001200";
    im1 = "INVALID_MOVE+Invalid move: Tile not empty.";
    uc1 = "UNKNOWN_COMMAND+this is weird";
    us1 = "UPDATE_STATUS+PLAYING;1;0000011120001200";
    gf1 = "GAME_FINISHED+1+JAAP+1;34;2;36+Jaap heeft gewonnen";
    SIH = new ServerInputHandler(userInput, c);
	}

	
//test serverStringSplitter of het goed split op +
@Test
public void testSSS() {
	sa1 = SIH.serverStringSplitter(ah1);
	assertEquals(sa1[0], "ACKNOWLEDGE_HANDSHAKE");
	assertEquals(sa1[1], "1");
}

//test stringArrayAnalyser
@Test
public void testSAA() {
	System.out.println("begin deze test");
	sa1 = SIH.serverStringSplitter(ah1);
	SIH.stringArrayAnalyser(sa1);
	System.out.println("SIH hoort te printen ah1");
	//test of system.out.println komt
		//klopt
	sa2 = SIH.serverStringSplitter(rc1);
	SIH.stringArrayAnalyser(sa2);
	System.out.println("SIH hoort te printen rc1");
	//test of system.out.println komt
		//klopt
	sa3 = SIH.serverStringSplitter(ac1);
	SIH.stringArrayAnalyser(sa3);
	System.out.println("SIH hoort te printen ac1");
	//test of system.out.println komt
		//klopt
}


}
