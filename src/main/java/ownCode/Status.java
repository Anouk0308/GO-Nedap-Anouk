package ownCode;

public enum Status {
	WAITING, PLAYING, FINISHED;
	
	public String statusString(Status status) {
		if(status == Status.WAITING) {
			return "Wait for another player";
		}
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


