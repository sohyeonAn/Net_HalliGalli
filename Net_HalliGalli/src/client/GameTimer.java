package client;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Calendar;
import java.util.TimerTask;

public class GameTimer extends TimerTask{
	Writer wrt;
	String userName1 = null;
	String roomName;
	

	public GameTimer(PrintWriter writer, String userName2) {
		// TODO Auto-generated constructor stub
		  wrt = writer;
		  userName1 = userName2;
	}


	public GameTimer(PrintWriter writer, String userName, String roomName) {
		// TODO Auto-generated constructor stub
		this.userName1=userName;
		this.roomName=roomName;
	}


	public void run() {
		  String str;
		  	System.out.println("time");
		  	((PrintWriter) wrt).println("[TURN]" + userName1);
		  	
		  }
	
	public void timerIni(PrintWriter writer, String userName2){
		  wrt = writer;
		  userName1 = userName2;
	}


}
