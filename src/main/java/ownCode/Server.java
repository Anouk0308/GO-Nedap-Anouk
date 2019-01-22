package ownCode;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Server {
    private static final String INITIAL_INPUT
        = "input should be: <name> <port>";
    public String clientString;
    //lijst met games erin, waarbij gameID de index van de game is

    /** Starts a Server-application. */
    public static void main(String[] args) {
    	 if (args.length != 3) {
             System.out.println(INITIAL_INPUT);
             System.exit(0);
         }
    	 
    	 String name = args[0];
         InetAddress addr = null;
         int port = 0;
         Socket sock = null;
         ServerSocket servsock = null;
         
         
      // check args[1] - the IP-adress
         try {
             addr = InetAddress.getByName(args[1]);
         } catch (UnknownHostException e) {
             System.out.println(INITIAL_INPUT);
             System.out.println("ERROR: host " + args[1] + " unknown");
             System.exit(0);
         }

         // parse args[2] - the port
         try {
             port = Integer.parseInt(args[2]);
         } catch (NumberFormatException e) {
             System.out.println(INITIAL_INPUT);
             System.out.println("ERROR: port " + args[2]
             		           + " is not an integer");
             System.exit(0);
         }
         
         // try to open a Socket to the server
         try {
             servsock = new ServerSocket(port);
         } catch (IOException e) {
             System.out.println("ERROR: could not create a socket on port " + port);
         }
         
         //Server waits until a Client wants to connect with the Server over the port
        
         try {
        	 System.out.println("Listening....");
             sock = servsock.accept();
             System.out.println("Client found!");
         } catch (IOException e) {
             e.printStackTrace();
             System.exit(0);
         }

         
         // create Peer object and start the two-way communication
         try {
             SocketInteraction server = new SocketInteraction(name, sock);
             Thread serverThread = new Thread(server);
             serverThread.start();
             
             server.sendString("lala");
             server.shutDown();
         } catch (IOException e) {
             e.printStackTrace();
         }
    	 
    }
    
    public void clientStringSplitter(String clientString) {
		this.clientString = clientString;
		String stringArray[] = clientString.split("+");
		if(stringArray[0].equals("HANDSHAKE")) {
			// stringArray[1] = newName
			//name in namelijst
				//wanneer namelijst is 1 groot, vraag die persoon om config (gameID die je dan meegeeft = game[].length.
					//sla dit als temp op
				//wanneer namelijst is 2:
				//----niet te onderbreken
					//maak nieuwe game aan met de twee players
					//sla deze op in gamelijst
					//maak namelijst leeg
				//----mag hierna weer onderbreken
			
			
	
			//if tweede, open game, die game ID, this.playerName2 = stringArray[1], color = de andere color dan player1;
		}
		else if(stringArray[0].equals("SET_CONFIG")) {
				//stringArray[1] = gameID( dus voor die game is het)
				//playerName1, die krijgt preggered_colour (int is colorIndex) stringArray[2]
				// DIM = stringArray[3]
		}
		else if(stringArray[0].equals("MOVE")) {
			//stringArray[1] = game_id
			//stringArray[2] = de playername die een move zet (daarmee heb je kleur gekoppelt)
			//stringArray[3] =  tile index (cerander die tile naar kleur van de playername)
							// if -1, hou dan boolean bij da ter 1 heeft gepast.
							//als volgende ook past. end game
							//als volgende niet past, boolean weer op 0
		}
		else if(stringArray[0].equals("EXIT")) {
			//stringArray[1] = game_id
			//stringArray[2] = playername (verliezer van het spel)
			//update_status
			//gameFinished()
		}
	}
    
    public String acknowledgeHandshake() {//voor 1 persoon
    	return null;
    }
    public String requestConfig() {//voor 1 prsoon
    	return null;
    }
    public String acknowledgeConfig() {//voor allebei
    	return null;
    }
    public String acknowledgeMove() {//voor allebei
    	return null;
    }
    public String invalidMove() {//voor een persoon
    	return null;
    }
    public String unknownCommand() {//voor een persoon
    	return null;
    }
    public String updateStatus() {//voor allebei
    	return null;
    }
    public String gameFinished() {//voor allebei
    	//gameID is stop
    	//bereken score
    	//als exit boolean van game is aan, 
    		//dan losername=exit persoon, winner is de andere
    	//als beide gepasst
    		//winner = meeste punten, ander is loser
    	//stuur string
    	
    	return null;
    }

}

	
		
		//maakt nieuwe game aan met game ID, player names+kleur + board.string
	
		//invalid_move
			// controleerd of binnenkomende string mag (nooit zelfde string per game)
		//update_status
	
	
		
		
		// telt score wanneer 2 x pass
		// haald captured weg
