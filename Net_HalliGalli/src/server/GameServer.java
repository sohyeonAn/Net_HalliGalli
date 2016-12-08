package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.StringTokenizer;

public class GameServer implements Runnable {

	Socket s;
	ServerSocket server;

	static ArrayList<String> waitClients = new ArrayList<String>();//대기실 클라이언트 아이디 저장
	static HashMap<String, ArrayList<String>> roomInf = new HashMap<String, ArrayList<String>>();//방이름에 따른 그 방에 있는 클라이언트 저장
	static HashMap<String, ArrayList<BufferedWriter>> roomWriter = new HashMap<String, ArrayList<BufferedWriter>>();//
	static ArrayList<BufferedWriter> waitWriters = new ArrayList<BufferedWriter>();
	
	

	public static void main(String[] args) throws IOException, SQLException {
		// TODO Auto-generated method stub

		GameServer server = new GameServer(); // 서버 가동

		new Thread(server).start();
	}

	public GameServer() throws IOException, SQLException {
		try {
			InetAddress addr = InetAddress.getByName("localhost");

			server = new ServerSocket(1111, 100, addr); 
			System.out.println("NEW Server Start...");
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}

	}

	public void run() // 1. 접속을 처리
	{
		while (true) {
			try {
				s = server.accept();
				System.out.println("소켓" + s + "에 연결됨");
				ClientThread ct = new ClientThread(s);
				ct.start(); // 통신 시작
			} catch (Exception ex) {
			}
		}
	}

}

class ClientThread extends Thread {

	String id, posUser; // id,이름,성별,상태
	int myIndex; // 0부터 시작 첫번째 유저는 0번...
	Socket s;
	BufferedReader in; // client요청값을 읽어온다
	BufferedWriter out; // client로 결과값을 응답할때
	int myRoomIndex = -1; // client가 있는 방 번호, 0부터 시작

	public ClientThread(Socket s) {
		try {
			this.s = s; // 각 클라이언트의 소켓 장착
			in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			System.out.println("connect by user");
		} catch (Exception ex) {
		}
	}

	public void run() // 2.client와 server간의 통신을 처리 //Client의 요청을 받음
	{
		while (true) {

			try {
				String msg = in.readLine();
				StringTokenizer st = new StringTokenizer(msg, "|");
				int protocol = Integer.parseInt(st.nextToken());
				switch (protocol) {
				case Protocol.SUCCESSJOIN: {

					String id = st.nextToken();
					String pwd = st.nextToken();

					DB insertDB = new DB(id, pwd);
					insertDB.insert();

				}
					break;

				case Protocol.IDCHECK: {
					String id = st.nextToken();

					DB checkDB = new DB(id, null);
					int check = checkDB.checkID(id);
					out.write(Protocol.IDCHECK + "|" + check + "\n");
					out.flush();
				}
					break;

				case Protocol.IDEXIST: {
					String id = st.nextToken();

					DB checkDB = new DB(id, null);
					int check = checkDB.checkID(id);
					out.write(Protocol.IDEXIST + "|" + check + "\n");
					out.flush();
				}
					break;

				case Protocol.CHECKPWD: {
					String pwd = st.nextToken();

					DB checkDB = new DB(null, pwd);
					int check = checkDB.checkPWD(pwd);
					out.write(Protocol.CHECKPWD + "|" + check + "\n");
					out.flush();
				}
					break;

				// WAITROOMENTER,MAKEROOM

				case Protocol.WAITROOMENTER: {// 클라이언트가 대기실에 입장했을 때

					System.out.println("waitroomenter");
					String id = st.nextToken();


					synchronized (GameServer.waitClients) {// 대기실 클라이언트 어레이에 추가
						if (!GameServer.waitClients.contains(id)) {
							GameServer.waitClients.add(id);
						}
					}

					synchronized (GameServer.waitWriters) {// 클라이언트 소켓 어레이에 저장
						if (!GameServer.waitWriters.contains(out)) {
							GameServer.waitWriters.add(out);
						}
					}

				}
					break;

				case Protocol.WAITCLIENTNUM: {

					System.out.println("QWE");
					// 대기실사람들에게 대기사람 보내기
					StringBuffer buffer2 = new StringBuffer(Protocol.WAITCLIENTNUM + "|");

					for (String t : GameServer.waitClients) {
						buffer2.append(t + "|");
					}

					for (BufferedWriter write : GameServer.waitWriters) {
						write.write(buffer2.toString() + '\n');
						write.flush();
					}

				}
					break;

				case Protocol.ROOMLIST: {
					// 방정보 보내기

					System.out.println("rooommlist");
					Set<String> roomlist = GameServer.roomInf.keySet();
					StringBuffer buffer = new StringBuffer(Protocol.ROOMLIST + "|");

					for (String t : roomlist) {
						buffer.append(t + "|");
					}

					for (BufferedWriter write : GameServer.waitWriters) {
						write.write(buffer.toString() + '\n');
						write.flush();
					}

				}
					break;

				case Protocol.MAKEROOM: {// 클라이언트가 방을 만들때

					String id = st.nextToken();
					String roomName = st.nextToken();

					ArrayList<String> templist = new ArrayList<String>();
					templist.add(id);

					ArrayList<BufferedWriter> tempWriter = new ArrayList<BufferedWriter>();
					tempWriter.add(out);

					GameServer.roomInf.put(roomName, templist);// 방정보 업데이트
					GameServer.roomWriter.put(roomName, tempWriter);// 방정보 업데이트
																	// ->소켓

					System.out.println("개설된방 :" + roomName);
					System.out.println("사용자수 :" + templist.size());//

					for (int i = 0; i < GameServer.waitClients.size(); i++) {//대기실 클라이언트 삭제
						if (GameServer.waitClients.get(i).equals(id)) {
							GameServer.waitClients.remove(i);
							GameServer.waitWriters.remove(out);
							break;
						}
					}

				}
					break;

				case Protocol.JOINROOM: {// 방에 입장

					// 방정보 업데이트

					String id = st.nextToken();
					String roomName = st.nextToken();

					ArrayList<String> list = GameServer.roomInf.get(roomName);
					list.add(id);
					System.out.print("방제" + roomName + " ,");
					System.out.println("사람수 :" + list.size());

					for (int i = 0; i < GameServer.waitClients.size(); i++) {//대기실 클라이언트 삭제
						if (GameServer.waitClients.get(i).equals(id)) {
							GameServer.waitClients.remove(i);
							GameServer.waitWriters.remove(i);
							break;
						}
					}

				}
					break;

				case Protocol.EXITROOM: {// 게임방에서 나감

					// 방정보 업데이트

					String id = st.nextToken();
					String roomName = st.nextToken();

					GameServer.roomInf.get(roomName).remove(id);


					// 방에 사람이 없다면 지우기
					if (GameServer.roomInf.get(roomName).size() == 0) {
						GameServer.roomInf.remove(roomName);
					}
					
					synchronized (GameServer.waitClients) {// 대기실 클라이언트 어레이에 추가
						if (!GameServer.waitClients.contains(id)) {
							GameServer.waitClients.add(id);
						}
					}

					synchronized (GameServer.waitWriters) {// 클라이언트 소켓 어레이에 저장
						if (!GameServer.waitWriters.contains(out)) {
							GameServer.waitWriters.add(out);
						}
					}

				}
					break;

				}

			} catch (Exception ex) {
				/* 접속되어있던 Client 접속 종료시 */
				interrupt();
			}
		}
	}
}
