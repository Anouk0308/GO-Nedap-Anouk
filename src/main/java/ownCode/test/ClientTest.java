package ownCode.test;

import ownCode.*;

import org.junit.Before;
import org.junit.Test;

import com.nedap.go.gui.GoGuiIntegrator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ClientTest {

private String ah1;
private String rc1;
private String ac1;
private String am1;
private String im1;
private String uc1;
private String us1;
private String gf1;


	

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
}




//connect with other server
		//testen door te connecten
	


//test analyser, acknowledgeHandshake
	//check gameID die meegegeven wordt
	//check feedback System.out

//test analyser, requestConfig
	//check feedback System.out

//test analyser, acknowledgeConfig
	//check playerName die meegegeven wordt
	//check of kleur goed is
	//check of er een player gemaakt kan worden
	//check of DIM goed is
	//check of gamestate + onderdelen goed zijn
	//check of UI aangemaakt wordt
	//check opponentName
	//check of currentplayer/niet currentplayer effect heeft

//test analyser, acknowledgeMove
	//check reactie of goede gameID en foute gameID
	//check move en onderdelen
	//check gameState en onderdelen
	//check als wel of niet current player, en of de ander gepasst heeft of niet

//test analyser, invalidMove
	//check feedback System.out
	
//test analyseer, unknownCommand
	//check feedback System.out
	
//test analyseer, updateStatus
	//check gamestate en onderdelen
	//check feedback System.out
	//check of verder spelen wanneer current player
	
//test analyseer, gameFinished
	//check gameID
	//check winner
	//check score
	//check feedback System.out
	
//test analyseer default

//test anotherGame
	
//test handshake
	
//test setConfig

//test Move
	
//test Exit

//test UI
}