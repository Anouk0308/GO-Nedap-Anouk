package ownCode;

public class Client {
	public int UserInputDIM;

	//krijgt van server:
		//gameID
		//kleur van de player
	
	//SetUpGame:
		//eerste in server?
			//computerplayer naive?
				//default name = String AIAnouk
				//default colour = int 1
				//default board sixe = int 9
				
	
	//flow:
		//in nieuw geval en wij beginnen:
			//maak nieuw gb en bord 000000
		//in nieuw geval en wij beginnen niet:
			//maak nieuw gb met gegeven bord
		//in geval niet nieuw spel:
			// in geval dat mijn player passes, maar de andere player niet passed
				//zet gb.playerpasses = false
				//oldToNewboardstring()
			//in geval dat andere player passes, maar mijne kan nog een move zetten
				//zet gb.otherplayerpasses = true
				//oldToNewboardstring()
			//in geval dat mijn player passes, en de andere player passes
				//exit, scoren tellen
			//in geval geen player passes
				//oldToNewboardstring()
	
	
	
		//oldToNewboardstring(gb, boardstring){
			//gb.updateboardhistory(boardstring)
			//gb.setmove(boardstring) = nieuwboardstring voor de server
	

	
	
	
//opties om met server te praten:
	//c->s begint met een handshake 
		//handshake
			//HANDSHAKE+$PLAYER_NAME
				//playername string
	//s-c krijgt een acknowledge handshake terug wanneer verbonden
		//game_id,int
		//is_leader, int 0 = false, 1 = true (jij bent leader)
	//s-c request_config of acknowledge config
		//set_config
			//default_color
			//default_board_size
	//s-c acknowledge config
		//krijgt te horen wat je naam is, string
		//wat je kleur is, int
		//wat de size is, int
		//wat de game state is
			//waiting/playing/finished
			//current_player, int (colour)
			// boardstring
		//naam van de tegenstander,string
	//c-s move
		//gameID
		//playerName
		//tile_indez`?????????
	//s-c acknowledge move of invalid move
		//acknowledge move
			//gameID, int
			//move
				//index
				//colour
			//state
				//waiting/playing/finished
				//current_player, int (colour)
				// boardstring
		//invalid move
			//errormessage, string
	//s-c game finished
		//gameID, int
		//username winner, string
		//score, string (1;points_black;2;points_white)
		//message, string, leeg
	
	
	//c-s exit
		//game-id
		//playername
	
	
	
		//MOVE
		//pass
		//exit
	
	
	
	
	
	
	//gui/tui zooi
	
}
