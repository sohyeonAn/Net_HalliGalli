package server;



import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

public class HalliGalli_Server {
	/// Field

	static HashMap<String, ArrayList<String>> gameUser = new HashMap<String, ArrayList<String>>();
	static HashMap<String, ArrayList<Integer>> gameReady = new HashMap<String, ArrayList<Integer>>();
	static HashMap<String, ArrayList<BufferedWriter>> gameWriter = new HashMap<String, ArrayList<BufferedWriter>>();

	static ServerSocket game_server;
	static Random rnd = new Random();
	static int Card[] = new int[56]; // ī�� �������� ����
	static int TurnCard[][] = new int[4][]; // �������� ī�带 �����ϴ� ����
	static int TurnCardCount[] = new int[4]; // �������� ī���� ����
	static int CardType[] = new int[4]; // ī���� ������ �˱����� ����
	static int CardNum[] = new int[4]; // ī��� ���� ���� �˱����� ����
	static int ClientCard[][] = new int[4][]; // Ŭ���̾�Ʈ ī�� ����
	static int ClientCardCount[] = new int[4]; // Ŭ���̾�Ʈ ī�� ���� ����
	static int NowPlayer; // ���� ���ʰ� �������� ����
	static int enterCount = 0;
	static boolean isSuccess = false; // ��ġ�⿡ �����ߴ��� Ȯ��
	static boolean dead[] = new boolean[4]; // �׾����� ��Ҵ��� Ȯ��
	boolean EndGame = false; // ������ ������ Ȯ��.
	static boolean isBell = false; // ������ ���� �ƴ��� Ȯ��
	static int ComboNum = 0; // �޺��� Ƚ��
	static String preBellUser;// ���� �������� �̸�
	static String Player[] = new String[4]; // �÷��̾��̸� ���Ӽ������ ����
	static BManager bMan = new BManager(); // Ŭ���̾�Ʈ���� ������ִ� ��ü

	/// Constructor
	public HalliGalli_Server() {
	}

	/// Method
	public void startServer() {
		try {
			InetAddress addr = InetAddress.getByName("localhost");
			game_server = new ServerSocket(333);
			System.out.println("���Ӽ��������� �����Ǿ����ϴ�.");
			while (true) {
				// Ŭ���̾�Ʈ�� ����� �����带 ��´�.
				Socket socket = game_server.accept();
				System.out.println("����" + socket + "�� �����");
				// �����带 ����� �����Ų��.
				HalliGalli_Thread host = new HalliGalli_Thread(socket);
				host.start();
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public static void GameInit() // ���� �ʱ�ȭ
	{

		for (int i = 0; i < 4; i++) {
			dead[i] = false;
			TurnCard[i] = new int[56];
			TurnCardCount[i] = 0;
			ClientCard[i] = new int[56];
			ClientCardCount[i] = 0;
		}

		for (int i = 0; i < 56; i++) // ī���ȣ ����
		{
			Card[i] = i;
		}

		for (int i = 55; i >= 0; i--) // ī�� ����
		{
			int temp;
			int j = rnd.nextInt(56);
			temp = Card[i];
			Card[i] = Card[j];
			Card[j] = temp;
		}
		// if(preBellUser != null)
		// bMan.sendToAll("[COMBO]|"+ preBellUser + " "+ ComboNum);
		System.out.println("[COMBO]|" + preBellUser + " " + ComboNum);

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void DivideCard() // ī�带 Ŭ���̾�Ʈ���� ������
	{
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 14; j++) {
				ClientCard[i][j] = Card[i * 14 + j];
				ClientCardCount[i]++;
			}
		}
	}

	public static void UpdateCardNum(String roomName) // Ŭ���̾�Ʈ�鿡�� ī�������� ������Ʈ����
														// �˸�.
	{
		for (int i = 0; i < 4; i++) {
			if (!dead[i]) {
				bMan.sendToAll(roomName, "[CARDNUM]|" + Player[i] + "|" + ClientCardCount[i]);// broadcast
			}
		}
	}

	public static void NextPlayer() {
		NowPlayer++;
		if (NowPlayer == 4) {
			NowPlayer = 0;
		}

		while (dead[NowPlayer]) {
			NowPlayer++;
			if (NowPlayer == 4) {
				NowPlayer = 0;
			}
		}
	}

	public static void SuccessBell(String roomName) {

		for (int i = 0; i < 4; i++) {
			if (!dead[i]) {
				bMan.sendTo(i, roomName, "[SUCCESS]|" + Player[i]);
			}
		}
	}

	public static void FailBell(String roomName) {
		for (int i = 0; i < 4; i++) {
			if (!dead[i]) {
				bMan.sendTo(i, roomName, "[FAIL]|" + Player[i]);
			}
		}
	}

	public static int isEndGame() // ������ �������� �˻�
	{
		int count = 0;

		for (int i = 0; i < 4; i++) {
			if (dead[i]) {
				count++;
			}
		}
		if (count == 3) {// �̱����� ��������.
			for (int i = 0; i < 4; i++) {
				if (!dead[i]) {
					return i;
				}
			}
		}
		return -1;
	}

	public static void main(String[] args) {
		HalliGalli_Server server = new HalliGalli_Server();
		server.startServer();
	}

	static class HalliGalli_Thread extends Thread {
		/// Field
		Socket socket; // ��������
		boolean ready = false; // �غ񿩺�
		BufferedReader reader; // �ޱ�
		static BufferedWriter writer; // ������

		String userName;
		static String roomName;

		/// Constructor
		HalliGalli_Thread(Socket socket) {
			this.socket = socket;
		}

		Socket getSocket() {
			return socket;
		}

		boolean isReady() {
			return ready;
		}

		public void run() {
			try {
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

				String msg;

				while ((msg = reader.readLine()) != null) {
					
					System.out.println(msg);

					StringTokenizer st = new StringTokenizer(msg, "|");
					String protocol = st.nextToken();

					if (protocol.equals("[CONNECT]")) {
						String userName = st.nextToken();
						String roomName = st.nextToken();
						this.userName = userName;
						this.roomName = roomName;
						bMan.addUser(userName, roomName);
						bMan.InitReady(roomName);

						if (bMan.isFull(roomName)) {

							bMan.removeUser(userName, roomName);
							if (reader != null) {
								reader.close();
							}
							if (writer != null) {
								writer.close();
							}
							if (socket != null) {
								socket.close();
							}
							reader = null;
							writer = null;
							socket = null;
							System.out.println("�̹� ���� ���� á���ϴ�.");
							System.out.println("������ ��: " + gameUser.get(roomName).size());
							bMan.sendToAll(roomName, "[DISCONNECT]|" + userName);
						} else { // ����� ������ �Ȱ��
							bMan.sendToOthers(userName, roomName, "[ENTER]|" + userName);
							//bMan.sendToAll(roomName, "[MSG]|" + userName + "|���� �����ϼ̽��ϴ�.");
						}
					}

					else if (protocol.equals("[READY]")) // Ŭ���̾�Ʈ���� ������ ���
					{
						String userName = st.nextToken();
						String roomName = st.nextToken();

						bMan.doReady(userName, roomName);
						bMan.sendToAll(roomName, userName + "�� �غ�Ϸ�!");

						if (bMan.isReady(roomName)) {// if all ready , start the
														// game

							// Thread.sleep(3000);

							GameInit(); // �����ʱ�ȭ
							for (int i = 0; i < gameUser.get(roomName).size(); i++) // �÷��̾�
																					// �̸���
																					// ����
							{
								Player[i] = gameUser.get(roomName).get(i);
							}
							NowPlayer = 0; // ���ӽ����� 0������
							bMan.sendToAll(roomName, Player[NowPlayer] + "�� �����Դϴ�.");
							bMan.sendToAll(roomName, "[SEQUENCE]|" + Player[0] + "|0|" + Player[1] + "|1|" + Player[2]
									+ "|2|" + Player[3] + "|3");// player �鿡��
																// �ڽ���
																// index�� �����ش�.
							DivideCard();
							UpdateCardNum(roomName);
							bMan.sendToAll(roomName, "������ �����մϴ�.");
						}
					}

					else if (protocol.equals("[NOREADY]")) // Ŭ���̾�Ʈ���� ���� �ٽ�
															// ��������
					// ���
					{
						String userName = st.nextToken();
						String roomName = st.nextToken();

						bMan.unReady(userName, roomName);
						bMan.sendToAll(roomName, "[READY]|" + userName + "���� ���� �����߽��ϴ�.");
					} else if (protocol.equals("[TURN]")) // Ŭ���̾�Ʈ���� ī������⸦ ����
															// ���
					{
						String userName = st.nextToken();					
						String roomName = st.nextToken();
						
						System.out.println(userName+roomName+"in turn");

						if (Player[NowPlayer].equals(userName)) // �ڱ����ʿ��ΰ˻�
						{
							TurnCard[NowPlayer][TurnCardCount[NowPlayer]++] = ClientCard[NowPlayer][--ClientCardCount[NowPlayer]];
							if (ClientCardCount[NowPlayer] == 0) { // Ŭ���̾�Ʈ�� ī�尡
								// ���嵵 ���� ����
								// ��� ��������
								// ó��
								dead[NowPlayer] = true;
								bMan.sendToAll(roomName, "[UPDATEDEAD]|" + Player[NowPlayer]);
								writer.write("[DEAD]|\n");
								writer.flush();

								if (isEndGame() != -1) {
									bMan.sendToAll(roomName, Player[isEndGame()] + "���� �̰���ϴ�.");
									bMan.sendToAll(roomName, "[GAMEINIT]|");
									bMan.sendTo(isEndGame(), roomName, "[WIN]|");
									bMan.unReady(userName, roomName);
								}

								NextPlayer();
								bMan.sendToAll(roomName, Player[NowPlayer] + "�� �����Դϴ�.");
							} else { // �׿ܴ� Ŭ���̾�Ʈ���� ī�� �ٽñ׸� ��û
								bMan.sendToAll(roomName, "[REPAINT]|" + Player[NowPlayer] + "|"
										+ TurnCard[NowPlayer][TurnCardCount[NowPlayer] - 1]);
								UpdateCardNum(roomName);
								NextPlayer();
								bMan.sendToAll(roomName, Player[NowPlayer] + "�� �����Դϴ�.");
							}
						} else { // �ڱ� ���ʰ� �ƴ� ��� ������ �޽���.
							writer.write("������ʰ� �ƴմϴ�.|\n");
							writer.flush();
						}
					}

					else if (protocol.equals("[MSG]")) // Ŭ���̾�Ʈ���� �޼����� �޾��� ��
					{
						String userName = st.nextToken();
						String roomName = st.nextToken();
						String m = st.nextToken();
						bMan.sendToAll(roomName, "[MSG]|" + userName + "|" + m);
					}

					else if (protocol.equals("[BELL]")) // Ŭ���̾�Ʈ���� ���� ����� ��
					{
						String userName = st.nextToken();
						String roomName = st.nextToken();
						if (isBell == true) {
							writer.write("����� �ʾ����ϴ�.|\n");
							writer.flush();
						} else {
							isBell = true;
							bMan.sendToAll(roomName, userName + "���� ���� �ƽ��ϴ�!!!");
							bMan.sendToAll(roomName, "[BELL]|");// �ٸ� �÷��̾���� ����
																// �� ġ�� �Ѵ�.
							Thread.sleep(1000);
							isSuccess = false;
							int CardSum = 0;
							for (int i = 0; i < 4; i++) {
								if (TurnCardCount[i] != 0) { // ������ �� ī���� ����������
									// ���� ���Ѵ�.
									int temp = TurnCard[i][TurnCardCount[i] - 1];
									CardType[i] = temp / 14;
									CardNum[i] = temp % 14;
								} else { // 4���� �� ���� ������츦 ����� 0���� �ʱ�ȭ.
									CardType[i] = -1;
									CardNum[i] = -1;
								}
							}
							for (int i = 0; i < 4; i++) {
								CardSum = 0;
								for (int j = 0; j < 4; j++) {
									if (CardType[i] == CardType[j]) { // ����������
										// ���� �͸�
										// ���Ѵ�.
										if (CardNum[j] >= 0 && CardNum[j] <= 4) {
											CardSum += 1;
										} else if (CardNum[j] >= 5 && CardNum[j] <= 7) {
											CardSum += 2;
										} else if (CardNum[j] >= 8 && CardNum[j] <= 10) {
											CardSum += 3;
										} else if (CardNum[j] >= 11 && CardNum[j] <= 12) {
											CardSum += 4;
										} else if (CardNum[j] == 13) {
											CardSum += 5;
										}
									}
								}
								if (CardSum == 5) { // ��ġ�⼺���� �� ī�带 �� ��������.
									SuccessBell(roomName);

									if (preBellUser == null) {
										ComboNum = 1;
										preBellUser = userName;
									} else if (preBellUser.equals(userName)) {
										ComboNum++;
									} else {
										ComboNum = 1;
										preBellUser = userName;
									}

									System.out.println("[COMBO]|" + preBellUser + "|" + ComboNum);
									bMan.sendToAll(roomName, "[COMBO]|" + preBellUser + "|" + ComboNum);
									isBell = false;
									bMan.sendToAll(roomName, userName + "���� ��ġ�⿡ �����߽��ϴ�.");
									int a = bMan.getNum(userName, roomName);
									for (i = 0; i < 4; i++) {
										for (int j = 0; j < TurnCardCount[i]; j++) {
											ClientCard[a][ClientCardCount[a]++] = TurnCard[i][j];
										}
										TurnCardCount[i] = 0;
									}

									Thread.sleep(1000);
									bMan.sendToAll(roomName, Player[NowPlayer] + "�� �����Դϴ�.");
									for (int m = ClientCardCount[a]; m > 0; m--) // ī��
									// ����
									{
										int temp;
										int n = rnd.nextInt(ClientCardCount[a]);
										temp = ClientCard[a][m];
										ClientCard[a][m] = ClientCard[a][n];
										ClientCard[a][n] = temp;
									}
									isSuccess = true;
									UpdateCardNum(roomName);
									break;
								}
							}
							if (!isSuccess) { // ��ġ�� ���н� �ٸ��÷��̾�� ī�带 ���徿 ������.
								FailBell(roomName);
								if (preBellUser.equals(userName)) {
									preBellUser = null;
									ComboNum = 0;
								}
								bMan.sendToAll(roomName, "[COMBO]|" + preBellUser + " " + ComboNum);

								isBell = false;
								bMan.sendToAll(roomName, userName + "���� ��ġ�⿡ �����߽��ϴ�.");
								for (int i = 0; i < 4; i++) {
									if (!userName.equals(Player[i]) && !dead[i]) {
										int a = bMan.getNum(userName, roomName);
										ClientCard[i][ClientCardCount[i]++] = ClientCard[a][--ClientCardCount[a]];
										if (ClientCardCount[a] == 0) {
											dead[a] = true;
											bMan.sendToAll(roomName, "[UPDATEDEAD]|" + userName);
											writer.write("[DEAD]|" + userName + '\n');
											writer.flush();
											if (userName.equals(Player[NowPlayer])) {
												NextPlayer();
											}
											if (isEndGame() != -1) {
												bMan.sendToAll(roomName, Player[isEndGame()] + "���� �̰���ϴ�.");
												bMan.sendToAll(roomName, "[GAMEINIT]|");
												bMan.sendTo(isEndGame(), roomName, "[WIN]|");
												bMan.unReady(userName, roomName);
												GameInit();
											}
											break;
										}
									}
								}
								UpdateCardNum(roomName);
								Thread.sleep(1000);
								bMan.sendToAll(roomName, Player[NowPlayer] + "�� �����Դϴ�.");
							}
						}
					}
				}
			} catch (Exception e) {
			} finally {
				try {
					bMan.removeUser(userName, roomName);
					if (reader != null) {
						reader.close();
					}
					if (writer != null) {
						writer.close();
					}
					if (socket != null) {
						socket.close();
					}
					reader = null;
					writer = null;
					socket = null;
					System.out.println(userName + "���� ������ �������ϴ�.");
					System.out.println("������ ��: " + gameUser.get(roomName).size());
					bMan.sendToAll(roomName, "[DISCONNECT]|" + userName);
				} catch (Exception e) {
				}
			}
		}

	}

	static class BManager {

		public void addUser(String id, String roomName) {

			Set<String> roomlist = HalliGalli_Server.gameUser.keySet();

			if (roomlist.contains(roomName)) {
				HalliGalli_Server.gameUser.get(roomName).add(id);
				HalliGalli_Server.gameWriter.get(roomName).add(HalliGalli_Thread.writer);

			} else {
				ArrayList<String> templist = new ArrayList<String>();
				templist.add(id);
				ArrayList<BufferedWriter> tempWriter = new ArrayList<BufferedWriter>();
				tempWriter.add(HalliGalli_Thread.writer);

				HalliGalli_Server.gameUser.put(roomName, templist);// ������ ������Ʈ
				HalliGalli_Server.gameWriter.put(roomName, tempWriter);// ������
																		// ������Ʈ
			}

			System.out.println("�����ȹ� :" + roomName);
			System.out.println("����ڼ� :" + HalliGalli_Server.gameUser.get(roomName).size());//

		}

		public void removeUser(String id, String roomName) {
			HalliGalli_Server.gameUser.get(roomName).remove(id);
			HalliGalli_Server.gameWriter.get(roomName).remove(HalliGalli_Thread.writer);
			// �濡 ����� ���ٸ� �����
			if (HalliGalli_Server.gameUser.get(roomName).size() == 0) {
				HalliGalli_Server.gameUser.remove(roomName);
			}
		}

		synchronized boolean isFull(String roomName) // ������ �� á���� Ȯ��
		{

			if (HalliGalli_Server.gameUser.get(roomName).size() >= 5) {
				return true;
			}
			return false;
		}

		void sendTo(int i, String roomName, String msg) // i�������忡 �޽����� ����.
		{
			try {
				HalliGalli_Server.gameWriter.get(roomName).get(i).write(msg + '\n');
				HalliGalli_Server.gameWriter.get(roomName).get(i).flush();

			} catch (Exception e) {
				System.out.println("SendTo error");
			}
		}

		void sendToAll(String roomName, String msg) // ��� �����忡�� ������ �޽���
		{

			// �ش� ���ӷ��� ����鿡�� ���� �˷��ֱ�
			ArrayList<BufferedWriter> Writerlist = HalliGalli_Server.gameWriter.get(roomName);

			for (BufferedWriter write : Writerlist) {
				try {
					write.write(msg + '\n');
					write.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println("SendToAll error");
				}
			}
		}

		void sendToOthers(String id, String roomName, String msg) // �ڱ⸦ ������
																	// �������� ������
		// �޽���
		{
			for (int i = 0; i < HalliGalli_Server.gameUser.get(roomName).size(); i++) {
				if (!(HalliGalli_Server.gameUser.get(roomName).get(i).equals(id))) {
					sendTo(i, roomName, msg);
				}
			}
		}

		synchronized void InitReady(String roomName) // �غ��ϱ�
		{
			
			Set<String> roomlist = HalliGalli_Server.gameReady.keySet();

			if (roomlist.contains(roomName)) {
				HalliGalli_Server.gameReady.get(roomName).add(0);

			}else {
				ArrayList<Integer> templist = new ArrayList<Integer>();
				templist.add(0);
	
				HalliGalli_Server.gameReady.put(roomName, templist);// ������ ������Ʈ
			}

		}
		
		
		synchronized void doReady(String id, String roomName) // �غ��ϱ�
		{

			int find = -1;

			for (int i = 0; i < HalliGalli_Server.gameUser.get(roomName).size(); i++) {
				if (HalliGalli_Server.gameUser.get(roomName).get(i).equals(id)) {
					find = i;
					break;
				}
			}


			HalliGalli_Server.gameReady.get(roomName).set(find,1);

		}

		synchronized void unReady(String id, String roomName) // �غ�����
		{

			int find = -1;

			for (int i = 0; i < HalliGalli_Server.gameUser.get(roomName).size(); i++) {
				if (HalliGalli_Server.gameUser.get(roomName).get(i).equals(id)) {
					find = i;
					break;
				}
			}

			ArrayList<Integer> readyList = HalliGalli_Server.gameReady.get(roomName);
			readyList.set(find, 0);

			HalliGalli_Server.gameReady.put(roomName, readyList);

		}

		synchronized boolean isReady(String roomName) // �����غ񿩺�Ȯ��
		{
			int count = 0;
			for (int i = 0; i < HalliGalli_Server.gameUser.get(roomName).size(); i++) {
				if (HalliGalli_Server.gameReady.get(roomName).get(i) == 1) {
					count++;
				}
			}
			if (count == 4) {
				return true;
			}
			return false;
		}

		String getNames(String roomName) // ���� ���ӵ� �������� �̸��� ������.
		{
			ArrayList<String> Userlist = HalliGalli_Server.gameUser.get(roomName);

			StringBuffer sb = new StringBuffer("[PLAYERS]");
			for (String t : Userlist) {
				sb.append(t + "|");
			}
			return sb.toString();
		}

		int getNum(String id, String roomName) // ���� ���ӵ� �������� ��ȣ�� ���ؼ� �̸��� ������.
		{
			for (int i = 0; i < HalliGalli_Server.gameUser.size(); i++) {
				if (HalliGalli_Server.gameUser.get(roomName).get(i).equals(id)) {
					return i;
				}
			}
			return -1;
		}

	}
}