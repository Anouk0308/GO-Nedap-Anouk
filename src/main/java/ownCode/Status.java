package ownCode;

public enum Status { PLAYING, FINISHED;
	
	public String statusString(Status status) {
		if(status == Status.PLAYING) {
			return "The game is still going";
		}
		if(status == Status.FINISHED) {
			return "The game is finished";
		}
		else {
			return "Something went wrong";
		}
	}
}


