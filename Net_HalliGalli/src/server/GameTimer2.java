package server;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Calendar;
import java.util.TimerTask;

import server.HalliGalli_Server2.HalliGalli_Thread;

public class GameTimer2 extends TimerTask{
	Writer wrt;
	String userName1 = null;
	HalliGalli_Thread tmp;
	int tmp2;
	public GameTimer2(PrintWriter writer, String userName2) {
		// TODO Auto-generated constructor stub
		  wrt = writer;
		  userName1 = userName2;
	}
	
	public GameTimer2(HalliGalli_Thread dest, int i) {
		// TODO Auto-generated constructor stub
		tmp = dest;
		tmp2 = i;
	}
	

	public void run() {
		tmp.setBellauto(tmp2);
			
		  }
	
	public void timerIni(PrintWriter writer, String userName2){
		  wrt = writer;
		  userName1 = userName2;
	}


}
