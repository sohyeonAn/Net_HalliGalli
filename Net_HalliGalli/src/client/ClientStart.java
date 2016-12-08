package client;

import java.awt.*;

import javax.swing.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;
import client.Login;
import java.util.Timer;
import java.util.TimerTask;

public class ClientStart extends JFrame implements ActionListener, Runnable, MouseListener {

	String id;
	String pwd;
	String roomName;
	int checkPwd, idExist = 1;

	
	String serverIp = "localhost"; // server Ip addr
	CardLayout card = new CardLayout(); // 창 전환을 위해 필요합니다!
	Login login = new Login(); // 로그인창
	GameWindow gameR = new GameWindow();// 게임창
	Join join = new Join(); // 회원가입창

	MakeRoom makeR = new MakeRoom(); // 방만들기창
	WaitRoom waitR = new WaitRoom();
	Profile newprofile;

	static Socket s;
	BufferedReader in;
	static BufferedWriter writer;
	// 서버로 요청값을 보낸다

	Timer request_time = new Timer();

	public ClientStart() {
		connection();
		setLayout(card); // BorderLayout

		add("LOGIN", login); // 로그인창

		setSize(1000, 800); // 윈도우창 크기 설정
		setLocation(170, 50); // 창 위치
		setVisible(true); // 보여지게
		setResizable(false); // 윈도우 창 고정(늘릴 수 없음)

		add("WR", waitR); // 대기실창

		login.btJoin.addActionListener(this);// 회원가입버튼 누르면
		login.btLogin.addActionListener(this);// 로그인 버튼 누르면

		waitR.profile.addActionListener(this);// 도움말 버튼
		waitR.help.addActionListener(this);// 도움말 버튼
		waitR.make.addActionListener(this);// 방만들기 창 팝업
		waitR.RoomList.addMouseListener(this);// 방에 들어가기
		makeR.submit.addActionListener(this);// 방만들기 확인
		gameR.exit.addActionListener(this);// 게임방에서 나가기

		add("GW", gameR); // 게임 윈도우창
	}

	@SuppressWarnings("deprecation")
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

		if (e.getSource() == login.btJoin) {
			join.setBounds(580, 350, 600, 450);
			join.setVisible(true);
		} else if (e.getSource() == login.btLogin) {

			// 아이디와 비밀번호 입력했는지 확인
			id = Login.idF.getText();
			pwd = Login.pwdF.getText();
			if (id.length() == 0)
				JOptionPane.showMessageDialog(this, "Please enter your ID.");
			else if (pwd.length() == 0)
				JOptionPane.showMessageDialog(this, "Please enter your PASSWORD.");
			else {
				try {
					writer.write(Protocol.IDEXIST + "|" + id + "\n");

				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}

				if (idExist == 1) {
					try {
						writer.write(Protocol.CHECKPWD + "|" + pwd + "\n");

					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
				} else
					JOptionPane.showMessageDialog(this, "Your ID is wrong.");

				if (checkPwd == 1) {// 대기실 창으로 전환
					try {
						waitR.idF.setText(id);
						waitR.setProfile(id);
						writer.write(Protocol.WAITROOMENTER + "|" + id + '\n');
						writer.flush();
						card.show(getContentPane(), "WR");
						TimerTask request_task = new TimerTask() {
							public void run() {
								try {
									writer.write(Protocol.ROOMLIST + "|" + id + '\n');
									writer.flush();
									writer.write(Protocol.WAITCLIENTNUM + "|" + id + '\n');
									writer.flush();
								} catch (IOException e) {
									System.out.println("request Timer error!");
								}

							}
						};

						request_time.schedule(request_task, 0, 5000);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				} else
					JOptionPane.showMessageDialog(this, "Your PASSWORD is wrong.");

			}

		}
	
		else if (e.getSource() == waitR.profile) {
			// newprofile = new Profile(id, waitR);->미구현
		} else if (e.getSource() == waitR.make) {
			// 방만들기버튼을 누르면
			makeR.setBounds(500, 300, 420, 300);
			makeR.setVisible(true);

		} else if (e.getSource() == makeR.submit) {
			// 게임창으로 전환
			try {
				writer.write(Protocol.MAKEROOM + "|" + id + "|" + makeR.nameF.getText() + '\n');// -------------------------->서버구현!!ok
				writer.flush();
			} catch (Exception ee) {
				ee.printStackTrace();
			}
			roomName = makeR.nameF.getText();
			makeR.dispose();
			card.show(getContentPane(), "GW");
			HalliGalli_Client client = new HalliGalli_Client(id, roomName, gameR);
		} else if (e.getSource() == gameR.exit) {// 게임 나가기 버튼 누르면
			card.show(getContentPane(), "WR");
			try {
				writer.write(Protocol.EXITROOM + "|" + id + "|" + gameR + '\n');
				writer.flush();
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}

	}

	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
			try {

				String roomname = waitR.RoomList.getSelectedItem();
				StringTokenizer st = new StringTokenizer(roomname, "|");
				makeR.dispose();
				card.show(getContentPane(), "GW");
				roomName = st.nextToken();

				writer.write(Protocol.JOINROOM + "|" + id + "|" + roomName + "|" + '\n');

				HalliGalli_Client client = new HalliGalli_Client(id, roomName, gameR);// 소켓
				// 통신

			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ClientStart start = new ClientStart();
	}

	public void connection() {
		try {
			s = new Socket(serverIp, 1111);
			writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));// s=>server
			in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		} catch (Exception ex) {
		}

		new Thread(this).start(); // run()으로 이동 // 서버로부터 응답값을 받아서 처리
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		String msg;
		try {
			while (true) {
				msg = in.readLine();
				System.out.println("Server=>" + msg);
				StringTokenizer st = new StringTokenizer(msg, "|");// shift + \
				int protocol = Integer.parseInt(st.nextToken());

				System.out.println("msg : " + msg);
				switch (protocol) {

				case Protocol.IDCHECK: {// 서버로 부터 아이디 중복체크 값을 받아옴
					int check = Integer.parseInt(st.nextToken());
					System.out.println(check);
					if (check == -1)
						join.pop_up("Please use another ID.");
					else
						join.pop_up("You can use your ID.");

				}
					break;

				case Protocol.IDEXIST: {// 서버로 부터 아이디 중복체크 값을 받아옴
					int check = Integer.parseInt(st.nextToken());
					System.out.println(check);
					if (check == -1)
						idExist = 1;
					else
						idExist = -1;

				}
					break;

				case Protocol.CHECKPWD: {// 서버로 부터 비밀번호가 맞는지 받아옴
					int check = Integer.parseInt(st.nextToken());
					System.out.println(check);
					if (check == -1)
						checkPwd = 1;
					else
						checkPwd = -1;

				}
					break;

				case Protocol.ROOMLIST: {// 서버로부터 현재 방의 정보를 받아옴

					waitR.readMsg(protocol, st);

				}
					break;

				case Protocol.WAITCLIENTNUM: {// 서버로부터 현재 대기실에 있는 클라이언트의 정보를 받아옴

					waitR.readMsg(protocol, st);

				}
					break;

				case Protocol.JOINROOM: {// 현재 게임방ㅇ에 사람이 입장했을 때 사ㅏ람 아이디 받아옴

					String id = st.nextToken();

					// gameR.chatA.append("[" + id + "]님이 입장하였습니다.\n");

				}
					break;

				case Protocol.EXITROOM: {// 현재 게임방에 사람 나갔을 때

					String id = st.nextToken();

					// gameR.chatA.append("[" + id + "]님이 퇴장하셨습니다.\n");

				}
					break;

				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			validate();
		}

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}