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
	public int moveTime;
	public boolean exit = false;

	public BufferedReader serverInput;
	public BufferedWriter userToServer;
	public BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
	public ServerInputHandler SIH;
	public Socket sock;
	public InetAddress addr;
	public Thread clientThread;
	
	public Client(){
	}
	
	public static void main(String[] args) {
		Client client = new Client();
		client.gameFlow();
	}
	
	//een keuze menu voor naam, AI, UI, tijd per move en server
	public void gameFlow(){
		try {
			SIH = new ServerInputHandler(userInput, this);
			chosingName();	
			chosingAI();
			chosingUI();
			chosingTime();
			chosingServer();
		} catch(IOException e){
			System.out.println(e);
		}
	}

	//krijg een goede playername
	public void chosingName() throws IOException {
		print("Welkom gamer, what is your name?");
		try {
			if(userInput != null) {
				String thisLine = userInput.readLine();
				if(thisLine != "") {
					if( !thisLine.contains("+")) {
						playerName = thisLine;
						SIH.playerName = thisLine;
					} else {
						print("Are you trying to kill the programm?");
						print("Try a name without a +");
						chosingName();
					} 
				} else {
					print("I am sure you do have a name");
					print("You van even come up with a fake name, but give me one");
					chosingName();
				}
			} else {
				print("I am sure you do have a name");
				print("You van even come up with a fake name, but give me one");
				chosingName();
			}
		}catch (IOException e) {	
			print("error" + e.getMessage());
		}
	}
	
	//kies tussen humanplayer en computerplayer
	public void chosingAI() throws IOException {
		print("Very well " + playerName + ", would you like to play yourself or would you like to use the AI?");
		print("type 1 for playing yourself, type 2 for letting the AI play.");
		try {
			if(userInput != null) {
				int number = Integer.parseInt(userInput.readLine());
				if(number == 1 || number == 2) {
					whichPlayerIndexChoice = number;
					print("You have chosen " + number);
				} else {
					print("No dummy, that is not a 1 or a 2, try again");
					chosingAI();
				}
			} else {
				print("No dummy, that is not a 1 or a 2, try again");
				chosingAI();
			}
		} catch (NumberFormatException e) {
				print("No no, that is not an integer, try a number");
	            chosingAI();
		}
	}
	
	//kies tussen TUI en GUI
	public void chosingUI() throws IOException {
		print("What would you like to as display?");
		print("type 1 for TUI, type 2 for GUI.");
		try {
			if(userInput != null) {
				int number = Integer.parseInt(userInput.readLine());
				if( number == 1 || number == 2) {
					if(number == 1) {
						SIH.setUseTUI(true);;
						print("you have chosen TUI");
					} else if(number == 2){
						SIH.setUseTUI(false);
						print("you have chosen GUI");
					}
				} else {
					print("No no, that is not a 1 or 2");
					chosingUI();
				} 
			} else {
				print("No, that is not a 1 or 2, try again");
				chosingUI();
			}
		} catch (IOException e) {
			print("error " + e.getMessage());
			print("try again");
			chosingUI();
		} catch (NumberFormatException e) {
			print("error " + e.getMessage());
			print("try again");
			chosingUI();
		}
	}
	
	//geef aan hoelang een move mag duren
	public void chosingTime() throws IOException {
		print("What is the maximum seconds a move may take?");
		int thisLine = Integer.parseInt(userInput.readLine());
		try {
			moveTime = thisLine;
		} catch(NumberFormatException e) {
			print("No silly, that is not an integer, try again");
            chosingTime();
		}
		
	}
	
	//geef een Inetadress en port, er wordt een socket gecreeert wanneer mogelijk
	public void chosingServer() throws IOException {
		print("To which server would you like to connect?");
		print("type the Inetadress");
			String thisLine1 = userInput.readLine();
			try {
				if(thisLine1!="") {
					addr = InetAddress.getByName(thisLine1);
				} else {
					print("You have to type something, try again");
					chosingServer();
				}
	        } catch (UnknownHostException e) {
	        	print("Computer says no, try anoter Inetadress");
	            chosingServer();
	        }
			print("type the port");
			String thisLine2 = userInput.readLine();
			try {
				if(thisLine1!="") {
					port = Integer.parseInt(thisLine2);
				} else {
					print("You have to type something");
					print("Now you have to start all over again...");
					chosingServer();
				}
			} catch (NumberFormatException e) {
				print("No crazy, that is not an integer, try another port");
	            chosingServer();
			}
			
			try {
				print("Trying to connect with this server");
				sock = new Socket(addr, port);
				print("Created Socket!");
				serverInput = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				userToServer = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
				this.start();
				this.sendMessage(SIH.handshake());

			} catch (IOException e) {
				print("ERROR: couldn't construct a client object!");
				System.exit(0);
			}
	}
	
	//luister naar de server. Wanneer er een string binnen is, stuur deze dan naar de ServerInputHandler
	public void run() {
		try {
			print("I am listening");
			while(true) {
				serverString = serverInput.readLine();
				String thisLine = userInput.readLine();
				if(thisLine.equals("EXIT")) {
					sendMessage(SIH.exit());
					exit = true;
					shutdown();
				} else {
					SIH.serverStringSplitter(serverString);
				}
			}
			}catch (IOException e) {
				print("Something went wrong while running the client");
				print("it is possible that the server has disconnected");
				print("Sorry, but we have to close the socket connection");
				shutdown();
		}
	}
	
	//stuur een berichtje over de socket naar de server
	public void sendMessage(String msg) {
		try {
			userToServer.write(msg);
			userToServer.newLine();
			userToServer.flush();
		} catch (IOException e) {
			shutdown();
		}

	}
	
	//close the socket
	public void shutdown() {
		print("Closing socket connection...");
		try {
			sock.close();
		} catch (IOException e) {
			print("Error when closing socket!");
			e.printStackTrace();
		}
	}
	
	//whichPlayerIndexChoice geeft aan of het een humanplayer of computerplayer is
	public int getWhichPlayerIndexChoice() {
		return whichPlayerIndexChoice;
	}
	
	private static void print(String message){
		System.out.println(message);
	}
}
