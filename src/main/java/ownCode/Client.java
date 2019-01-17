package ownCode;

public class Client {
	public int UserInputDIM;


	
	
	
	
	
	
	//krijgt van GameBrain:
		//newboardstring
		//playerpasses
	
	//houdt voor zichzelf bij
		//game ID
		// maakt player aan met goede kleur
		// bij nieuwe game, oldboardstring = DIM*DIM aantal 0 achter elkaar
		// zelf bij houden of er 2 mensen passen (voor zekerheid ook in game geimplementeerd als de boolean exit)
	
	
	//deze boolean houdt bij of beide players gepassed hebben. In dat geval is het spel afgelopen en kan de score bepaald worden
	/*public boolean twoTimesPassed() {
		if(playerPasses == true && otherPlayerPasses == true){
			exit = true;
			return true;
		}
		return false;
	}
	*/
	
	
	
	
	//opties om met server te praten:
		//handshake
		//set_config
			//denk aan default config (alleen human wilt kiezen)
		//MOVE
		//pass
		//exit
	
	//gui/tui zooi
	
}
