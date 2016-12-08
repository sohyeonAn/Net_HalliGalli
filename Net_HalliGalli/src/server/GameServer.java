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

	static ArrayList<String> waitClients = new ArrayList<String>();//���� Ŭ���̾�Ʈ ���̵� ����
	static HashMap<String, ArrayList<String>> roomInf = new HashMap<String, ArrayList<String>>();//���̸��� ���� �� �濡 �ִ� Ŭ���̾�Ʈ ����
	static HashMap<String, ArrayList<BufferedWriter>> roomWriter = new HashMap<String, ArrayList<BufferedWriter>>();//
	static ArrayList<BufferedWriter> waitWriters = new ArrayList<BufferedWriter>();
	
	

	public static void main(String[] args) throws IOException, SQLException {
		// TODO Auto-generated method stub

		GameServer server = new GameServer(); // ���� ����

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

	public void run() // 1. ������ ó��
	{
		while (true) {
			try {
				s = server.accept();
				System.out.println("����" + s + "�� �����");
				ClientThread ct = new ClientThread(s);
				ct.start(); // ��� ����
			} catch (Exception ex) {
			}
		}
	}

}

class ClientThread extends Thread {

	String id, posUser; // id,�̸�,����,����
	int myIndex; // 0���� ���� ù��° ������ 0��...
	Socket s;
	BufferedReader in; // client��û���� �о�´�
	BufferedWriter out; // client�� ������� �����Ҷ�
	int myRoomIndex = -1; // client�� �ִ� �� ��ȣ, 0���� ����

	public ClientThread(Socket s) {
		try {
			this.s = s; // �� Ŭ���̾�Ʈ�� ���� ����
			in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			System.out.println("connect by user");
		} catch (Exception ex) {
		}
	}

	public void run() // 2.client�� server���� ����� ó�� //Client�� ��û�� ����
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

				case Protocol.WAITROOMENTER: {// Ŭ���̾�Ʈ�� ���ǿ� �������� ��

					System.out.println("waitroomenter");
					String id = st.nextToken();


					synchronized (GameServer.waitClients) {// ���� Ŭ���̾�Ʈ ��̿� �߰�
						if (!GameServer.waitClients.contains(id)) {
							GameServer.waitClients.add(id);
						}
					}

					synchronized (GameServer.waitWriters) {// Ŭ���̾�Ʈ ���� ��̿� ����
						if (!GameServer.waitWriters.contains(out)) {
							GameServer.waitWriters.add(out);
						}
					}

				}
					break;

				case Protocol.WAITCLIENTNUM: {

					System.out.println("QWE");
					// ���ǻ���鿡�� ����� ������
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
					// ������ ������

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

				case Protocol.MAKEROOM: {// Ŭ���̾�Ʈ�� ���� ���鶧

					String id = st.nextToken();
					String roomName = st.nextToken();

					ArrayList<String> templist = new ArrayList<String>();
					templist.add(id);

					ArrayList<BufferedWriter> tempWriter = new ArrayList<BufferedWriter>();
					tempWriter.add(out);

					GameServer.roomInf.put(roomName, templist);// ������ ������Ʈ
					GameServer.roomWriter.put(roomName, tempWriter);// ������ ������Ʈ
																	// ->����

					System.out.println("�����ȹ� :" + roomName);
					System.out.println("����ڼ� :" + templist.size());//

					for (int i = 0; i < GameServer.waitClients.size(); i++) {//���� Ŭ���̾�Ʈ ����
						if (GameServer.waitClients.get(i).equals(id)) {
							GameServer.waitClients.remove(i);
							GameServer.waitWriters.remove(out);
							break;
						}
					}

				}
					break;

				case Protocol.JOINROOM: {// �濡 ����

					// ������ ������Ʈ

					String id = st.nextToken();
					String roomName = st.nextToken();

					ArrayList<String> list = GameServer.roomInf.get(roomName);
					list.add(id);
					System.out.print("����" + roomName + " ,");
					System.out.println("����� :" + list.size());

					for (int i = 0; i < GameServer.waitClients.size(); i++) {//���� Ŭ���̾�Ʈ ����
						if (GameServer.waitClients.get(i).equals(id)) {
							GameServer.waitClients.remove(i);
							GameServer.waitWriters.remove(i);
							break;
						}
					}

				}
					break;

				case Protocol.EXITROOM: {// ���ӹ濡�� ����

					// ������ ������Ʈ

					String id = st.nextToken();
					String roomName = st.nextToken();

					GameServer.roomInf.get(roomName).remove(id);


					// �濡 ����� ���ٸ� �����
					if (GameServer.roomInf.get(roomName).size() == 0) {
						GameServer.roomInf.remove(roomName);
					}
					
					synchronized (GameServer.waitClients) {// ���� Ŭ���̾�Ʈ ��̿� �߰�
						if (!GameServer.waitClients.contains(id)) {
							GameServer.waitClients.add(id);
						}
					}

					synchronized (GameServer.waitWriters) {// Ŭ���̾�Ʈ ���� ��̿� ����
						if (!GameServer.waitWriters.contains(out)) {
							GameServer.waitWriters.add(out);
						}
					}

				}
					break;

				}

			} catch (Exception ex) {
				/* ���ӵǾ��ִ� Client ���� ����� */
				interrupt();
			}
		}
	}
}
