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
	static int Card[] = new int[56]; // 카드 섞기위한 변수
	static int TurnCard[][] = new int[4][]; // 뒤집어진 카드를 저장하는 변수
	static int TurnCardCount[] = new int[4]; // 뒤집어진 카드의 개수
	static int CardType[] = new int[4]; // 카드의 종류를 알기위한 변수
	static int CardNum[] = new int[4]; // 카드속 과일 개수 알기위한 변수
	static int ClientCard[][] = new int[4][]; // 클라이언트 카드 변수
	static int ClientCardCount[] = new int[4]; // 클라이언트 카드 개수 변수
	static int NowPlayer; // 현재 차례가 누구인지 저장
	static int enterCount = 0;
	static boolean isSuccess = false; // 종치기에 성공했는지 확인
	static boolean dead[] = new boolean[4]; // 죽었는지 살았는지 확인
	boolean EndGame = false; // 게임이 끝인지 확인.
	static boolean isBell = false; // 상대방이 종을 쳤는지 확인
	static int ComboNum = 0; // 콤보의 횟수
	static String preBellUser;// 지난 정답자의 이름
	static String Player[] = new String[4]; // 플레이어이름 접속순서대로 저장
	static BManager bMan = new BManager(); // 클라이언트에게 방송해주는 객체

	/// Constructor
	public HalliGalli_Server() {
	}

	/// Method
	public void startServer() {
		try {
			InetAddress addr = InetAddress.getByName("localhost");
			game_server = new ServerSocket(333);
			System.out.println("게임서버소켓이 생성되었습니다.");
			while (true) {
				// 클라이언트와 연결된 스레드를 얻는다.
				Socket socket = game_server.accept();
				System.out.println("소켓" + socket + "에 연결됨");
				// 스레드를 만들고 실행시킨다.
				HalliGalli_Thread host = new HalliGalli_Thread(socket);
				host.start();
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public static void GameInit() // 게임 초기화
	{

		for (int i = 0; i < 4; i++) {
			dead[i] = false;
			TurnCard[i] = new int[56];
			TurnCardCount[i] = 0;
			ClientCard[i] = new int[56];
			ClientCardCount[i] = 0;
		}

		for (int i = 0; i < 56; i++) // 카드번호 삽입
		{
			Card[i] = i;
		}

		for (int i = 55; i >= 0; i--) // 카드 섞기
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

	public static void DivideCard() // 카드를 클라이언트에게 나눠줌
	{
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 14; j++) {
				ClientCard[i][j] = Card[i * 14 + j];
				ClientCardCount[i]++;
			}
		}
	}

	public static void UpdateCardNum(String roomName) // 클라이언트들에게 카드정보가 업데이트됨을
														// 알림.
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

	public static int isEndGame() // 게임이 끝인지를 검사
	{
		int count = 0;

		for (int i = 0; i < 4; i++) {
			if (dead[i]) {
				count++;
			}
		}
		if (count == 3) {// 이긴사람이 누구인지.
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
		Socket socket; // 서버소켓
		boolean ready = false; // 준비여부
		BufferedReader reader; // 받기
		static BufferedWriter writer; // 보내기

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
							System.out.println("이미 방이 가득 찼습니다.");
							System.out.println("접속자 수: " + gameUser.get(roomName).size());
							bMan.sendToAll(roomName, "[DISCONNECT]|" + userName);
						} else { // 제대로 접속이 된경우
							bMan.sendToOthers(userName, roomName, "[ENTER]|" + userName);
							//bMan.sendToAll(roomName, "[MSG]|" + userName + "|님이 입장하셨습니다.");
						}
					}

					else if (protocol.equals("[READY]")) // 클라이언트에서 레디한 경우
					{
						String userName = st.nextToken();
						String roomName = st.nextToken();

						bMan.doReady(userName, roomName);
						bMan.sendToAll(roomName, userName + "님 준비완료!");

						if (bMan.isReady(roomName)) {// if all ready , start the
														// game

							// Thread.sleep(3000);

							GameInit(); // 게임초기화
							for (int i = 0; i < gameUser.get(roomName).size(); i++) // 플레이어
																					// 이름을
																					// 저장
							{
								Player[i] = gameUser.get(roomName).get(i);
							}
							NowPlayer = 0; // 게임시작은 0번부터
							bMan.sendToAll(roomName, Player[NowPlayer] + "님 차례입니다.");
							bMan.sendToAll(roomName, "[SEQUENCE]|" + Player[0] + "|0|" + Player[1] + "|1|" + Player[2]
									+ "|2|" + Player[3] + "|3");// player 들에게
																// 자신의
																// index를 보내준다.
							DivideCard();
							UpdateCardNum(roomName);
							bMan.sendToAll(roomName, "게임을 시작합니다.");
						}
					}

					else if (protocol.equals("[NOREADY]")) // 클라이언트에서 레디를 다시
															// 해제했을
					// 경우
					{
						String userName = st.nextToken();
						String roomName = st.nextToken();

						bMan.unReady(userName, roomName);
						bMan.sendToAll(roomName, "[READY]|" + userName + "님이 레디를 해제했습니다.");
					} else if (protocol.equals("[TURN]")) // 클라이언트에서 카드뒤집기를 했을
															// 경우
					{
						String userName = st.nextToken();					
						String roomName = st.nextToken();
						
						System.out.println(userName+roomName+"in turn");

						if (Player[NowPlayer].equals(userName)) // 자기차례여부검사
						{
							TurnCard[NowPlayer][TurnCardCount[NowPlayer]++] = ClientCard[NowPlayer][--ClientCardCount[NowPlayer]];
							if (ClientCardCount[NowPlayer] == 0) { // 클라이언트가 카드가
								// 한장도 남지 않은
								// 경우 죽음으로
								// 처리
								dead[NowPlayer] = true;
								bMan.sendToAll(roomName, "[UPDATEDEAD]|" + Player[NowPlayer]);
								writer.write("[DEAD]|\n");
								writer.flush();

								if (isEndGame() != -1) {
									bMan.sendToAll(roomName, Player[isEndGame()] + "님이 이겼습니다.");
									bMan.sendToAll(roomName, "[GAMEINIT]|");
									bMan.sendTo(isEndGame(), roomName, "[WIN]|");
									bMan.unReady(userName, roomName);
								}

								NextPlayer();
								bMan.sendToAll(roomName, Player[NowPlayer] + "님 차례입니다.");
							} else { // 그외는 클라이언트에게 카드 다시그림 요청
								bMan.sendToAll(roomName, "[REPAINT]|" + Player[NowPlayer] + "|"
										+ TurnCard[NowPlayer][TurnCardCount[NowPlayer] - 1]);
								UpdateCardNum(roomName);
								NextPlayer();
								bMan.sendToAll(roomName, Player[NowPlayer] + "님 차례입니다.");
							}
						} else { // 자기 차례가 아닐 경우 보내는 메시지.
							writer.write("당신차례가 아닙니다.|\n");
							writer.flush();
						}
					}

					else if (protocol.equals("[MSG]")) // 클라이언트에서 메세지를 받았을 때
					{
						String userName = st.nextToken();
						String roomName = st.nextToken();
						String m = st.nextToken();
						bMan.sendToAll(roomName, "[MSG]|" + userName + "|" + m);
					}

					else if (protocol.equals("[BELL]")) // 클라이언트에서 벨을 울렸을 때
					{
						String userName = st.nextToken();
						String roomName = st.nextToken();
						if (isBell == true) {
							writer.write("당신이 늦었습니다.|\n");
							writer.flush();
						} else {
							isBell = true;
							bMan.sendToAll(roomName, userName + "님이 종을 쳤습니다!!!");
							bMan.sendToAll(roomName, "[BELL]|");// 다른 플레이어들이 종을
																// 못 치게 한다.
							Thread.sleep(1000);
							isSuccess = false;
							int CardSum = 0;
							for (int i = 0; i < 4; i++) {
								if (TurnCardCount[i] != 0) { // 맨위에 깔린 카드의 과일종류와
									// 수를 구한다.
									int temp = TurnCard[i][TurnCardCount[i] - 1];
									CardType[i] = temp / 14;
									CardNum[i] = temp % 14;
								} else { // 4장이 다 깔리지 않은경우를 대비해 0으로 초기화.
									CardType[i] = -1;
									CardNum[i] = -1;
								}
							}
							for (int i = 0; i < 4; i++) {
								CardSum = 0;
								for (int j = 0; j < 4; j++) {
									if (CardType[i] == CardType[j]) { // 과일종류가
										// 같은 것만
										// 더한다.
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
								if (CardSum == 5) { // 종치기성공시 깔린 카드를 다 가져간다.
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
									bMan.sendToAll(roomName, userName + "님이 종치기에 성공했습니다.");
									int a = bMan.getNum(userName, roomName);
									for (i = 0; i < 4; i++) {
										for (int j = 0; j < TurnCardCount[i]; j++) {
											ClientCard[a][ClientCardCount[a]++] = TurnCard[i][j];
										}
										TurnCardCount[i] = 0;
									}

									Thread.sleep(1000);
									bMan.sendToAll(roomName, Player[NowPlayer] + "님 차례입니다.");
									for (int m = ClientCardCount[a]; m > 0; m--) // 카드
									// 섞기
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
							if (!isSuccess) { // 종치기 실패시 다른플레이어에게 카드를 한장씩 돌린다.
								FailBell(roomName);
								if (preBellUser.equals(userName)) {
									preBellUser = null;
									ComboNum = 0;
								}
								bMan.sendToAll(roomName, "[COMBO]|" + preBellUser + " " + ComboNum);

								isBell = false;
								bMan.sendToAll(roomName, userName + "님이 종치기에 실패했습니다.");
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
												bMan.sendToAll(roomName, Player[isEndGame()] + "님이 이겼습니다.");
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
								bMan.sendToAll(roomName, Player[NowPlayer] + "님 차례입니다.");
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
					System.out.println(userName + "님이 접속을 끊었습니다.");
					System.out.println("접속자 수: " + gameUser.get(roomName).size());
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

				HalliGalli_Server.gameUser.put(roomName, templist);// 방정보 업데이트
				HalliGalli_Server.gameWriter.put(roomName, tempWriter);// 방정보
																		// 업데이트
			}

			System.out.println("개설된방 :" + roomName);
			System.out.println("사용자수 :" + HalliGalli_Server.gameUser.get(roomName).size());//

		}

		public void removeUser(String id, String roomName) {
			HalliGalli_Server.gameUser.get(roomName).remove(id);
			HalliGalli_Server.gameWriter.get(roomName).remove(HalliGalli_Thread.writer);
			// 방에 사람이 없다면 지우기
			if (HalliGalli_Server.gameUser.get(roomName).size() == 0) {
				HalliGalli_Server.gameUser.remove(roomName);
			}
		}

		synchronized boolean isFull(String roomName) // 서버가 다 찼는지 확인
		{

			if (HalliGalli_Server.gameUser.get(roomName).size() >= 5) {
				return true;
			}
			return false;
		}

		void sendTo(int i, String roomName, String msg) // i번스레드에 메시지를 전달.
		{
			try {
				HalliGalli_Server.gameWriter.get(roomName).get(i).write(msg + '\n');
				HalliGalli_Server.gameWriter.get(roomName).get(i).flush();

			} catch (Exception e) {
				System.out.println("SendTo error");
			}
		}

		void sendToAll(String roomName, String msg) // 모든 스레드에게 보내는 메시지
		{

			// 해당 게임룸의 사람들에게 입장 알려주기
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

		void sendToOthers(String id, String roomName, String msg) // 자기를 제외한
																	// 유저에게 보내는
		// 메시지
		{
			for (int i = 0; i < HalliGalli_Server.gameUser.get(roomName).size(); i++) {
				if (!(HalliGalli_Server.gameUser.get(roomName).get(i).equals(id))) {
					sendTo(i, roomName, msg);
				}
			}
		}

		synchronized void InitReady(String roomName) // 준비하기
		{
			
			Set<String> roomlist = HalliGalli_Server.gameReady.keySet();

			if (roomlist.contains(roomName)) {
				HalliGalli_Server.gameReady.get(roomName).add(0);

			}else {
				ArrayList<Integer> templist = new ArrayList<Integer>();
				templist.add(0);
	
				HalliGalli_Server.gameReady.put(roomName, templist);// 방정보 업데이트
			}

		}
		
		
		synchronized void doReady(String id, String roomName) // 준비하기
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

		synchronized void unReady(String id, String roomName) // 준비해제
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

		synchronized boolean isReady(String roomName) // 전부준비여부확인
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

		String getNames(String roomName) // 현재 접속된 스레드의 이름을 가져옴.
		{
			ArrayList<String> Userlist = HalliGalli_Server.gameUser.get(roomName);

			StringBuffer sb = new StringBuffer("[PLAYERS]");
			for (String t : Userlist) {
				sb.append(t + "|");
			}
			return sb.toString();
		}

		int getNum(String id, String roomName) // 현재 접속된 스레드의 번호를 통해서 이름을 가져옴.
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