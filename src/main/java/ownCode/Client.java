package ownCode;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends Thread{
	public String playerName;
	public String serverString;
	public int whichPlayerIndexChoice;
	public int port;
	public boolean exit = false;

	public BufferedReader serverInput;
	public BufferedWriter userToServer;
	public BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
	public ServerInputHandler SIH;
	public Socket sock;
	public InetAddress addr;
	public Thread clientThread;
	public Client client;
	
	public Client(){
		//een lege constructor om in main de functie gameFlow() aan te vragen
	}
	
	public Client(String name, InetAddress addr, int port) throws IOException {
		this.playerName = name;
		sock = new Socket(addr, port);
		print("Created Socket!");
		serverInput = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		userToServer = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
		SIH = new ServerInputHandler(userInput, this); 
	}
	
	public static void main(String[] args) {
		Client c = new Client();
		c.gameFlow();
	}
	
	public void gameFlow(){
		try{
			SIH = new ServerInputHandler(userInput, this);
			System.out.println("test: SIH in gameflow is:" + this.SIH);
			chosingName();	
			chosingAI();
			chosingUI();
			chosingServer();
			//connect with server and starts client thread
		} catch(IOException e){
			System.out.println(e);
		}
	}

	
	public void chosingName() throws IOException {
		print("Welkom gamer, what is your name?");
		try {
			if(userInput != null) {
				String thisLine = userInput.readLine();
				if( !thisLine.contains("+")) {
					playerName = thisLine;
					SIH.playerName = thisLine;
				}
				else {
					print("Are you trying to kill the programm?");
					print("Try a name without a +");
					chosingName();
				} 
			}
		}catch (IOException e) {	
		}
	}
	
	public void chosingAI() throws IOException {
		print("Very well " + playerName +", would you like to play yourself or would you like to use the AI?");
		print("type 1 for playing yourself, type 2 for letting the AI play.");
			if(userInput != null) {
				int number = Integer.parseInt(userInput.readLine());
				try {
					if(number == 1 || number == 2) {
						this.whichPlayerIndexChoice = number;
						print("You have chosen " + number);
						System.out.println("test: what is whichPIC?" + this.whichPlayerIndexChoice);
					}
					else {
						print("No dummy, that is not a 1 or a 2, try again");
						chosingAI();
					}
				} catch (NumberFormatException e) {
					print("No no, that is not an integer, try a number");
		            chosingServer();
				}
			}
	}
	
	public void chosingUI() throws IOException {
		print("What would you like to as display?");
		print("type 1 for TUI, type 2 for GUI.");
		try {
			if(userInput != null) {
				int number = Integer.parseInt(userInput.readLine());
				if( number == 1 || number == 2) {
					if(number == 1) {
						SIH.useTUI = true;
						print("you have chosen TUI");
					}
					else if(number == 2){
						SIH.useTUI = false;
						System.out.println("test: geef mij useTUI"+SIH.useTUI);
						print("you have chosen GUI");
					}
				}
				else {
					print("No clown, that is not a 1 or 2");
					chosingUI();
				} 
			}
		}catch (IOException e) {
		}
	}
	
	public void chosingServer() throws IOException {
		print("To which server would you like to connect?");
		print("type the Inetadress");
			String thisLine1 = userInput.readLine();
			try {
				addr = InetAddress.getByName(thisLine1);    
	        } catch (UnknownHostException e) {
	        	print("Computer says no, try anoter Inetadress");
	            chosingServer();
	        }
			print("type the port");
			String thisLine2 = userInput.readLine();
			try {
				port = Integer.parseInt(thisLine2);
			} catch (NumberFormatException e) {
				print("No crazy, that is not an integer, try another port");
	            chosingServer();
			}
			
			try {
				print("Trying to connect with this server");
				client = new Client(playerName, addr, port);
				System.out.println("test: chosingsrv() SIH:" + SIH);
				client.whichPlayerIndexChoice = this.whichPlayerIndexChoice;
				client.start();
				client.sendMessage(this.SIH.handshake());

			} catch (IOException e) {
				print("ERROR: couldn't construct a client object!");
				System.exit(0);
			}
	}
	
	
	public void run() {
		try {
			System.out.println("I am listening");
			while(true) {
				System.out.println("test: we zitten in de while");
				serverString = serverInput.readLine();
				System.out.println("test: server says: " + serverString);
				/**String thisLine = userInput.readLine();
				if(thisLine.equals("EXIT")) {
					sendMessage(SIH.exit());//serverOutput
					exit = true;
					shutdown();
				}
				else {*/
					System.out.println("test: zijn in run, else");
					
					System.out.println("test: SIH in client,run()=" + this.SIH);
					String[] stringArray = this.SIH.serverStringSplitter(serverString);
					this.SIH.stringArrayAnalyser(stringArray);
				}
			/**}*/
			}catch (IOException e) {
				System.out.println("test: er gaat wat fout in run");;
		}
	}
	
	
	public void sendMessage(String msg) {
		try {
			userToServer.write(msg);
			userToServer.newLine();
			userToServer.flush();
		} catch (IOException e) {
			shutdown();
		}

	}
	
	public void shutdown() {
		print("Closing socket connection...");
		try {
			sock.close();
		} catch (IOException e) {
			print("Error when closing socket!");
			e.printStackTrace();
		}
	}
	
	private static void print(String message){
		System.out.println(message);
	}
	
	public void anotherGame() {
		print("Would you like to play another game of GO? Y/N");
		
		try {
			String thisLine = userInput.readLine();
			if(userInput != null) {
				if(thisLine == "Y") {
					sendMessage(SIH.exit());//serverOutput
					exit = true;//kill this socket
					exit = false;//make it possible to create a socket again
					gameFlow();
				}
				else if(thisLine == "N") {
					sendMessage(SIH.exit());//serverOutput
					exit = true;
				}
				else {
					print("This is not a Y or N silly, try again");
				}
			}
		}catch (IOException e) {
	            e.printStackTrace();
	    }
	}
	
	public int getWhichPlayerIndexChoice() {
		return whichPlayerIndexChoice;
	}
}
