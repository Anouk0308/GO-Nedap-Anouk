package ownCode;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Server {
    private static final String INITIAL_INPUT
        = "input should be: <name> <port>";

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
             
             server.handleTerminalInput();
             server.shutDown();
         } catch (IOException e) {
             e.printStackTrace();
         }
    	 
    }

}

		//acknowledge_handshake
			
		//request_config
		//acknowledge_config
		
		//maakt nieuwe game aan met game ID, player names+kleur + board.string
	
		//invalid_move
			// controleerd of binnenkomende string mag (nooit zelfde string per game)
		//update_status
	
	
		
		
		// telt score wanneer 2 x pass
		// haald captured weg
