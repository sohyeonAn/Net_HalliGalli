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
	CardLayout card = new CardLayout(); // â ��ȯ�� ���� �ʿ��մϴ�!
	Login login = new Login(); // �α���â
	GameWindow gameR = new GameWindow();// ����â
	Join join = new Join(); // ȸ������â

	MakeRoom makeR = new MakeRoom(); // �游���â
	WaitRoom waitR = new WaitRoom();
	Profile newprofile;

	static Socket s;
	BufferedReader in;
	static BufferedWriter writer;
	// ������ ��û���� ������

	Timer request_time = new Timer();

	public ClientStart() {
		connection();
		setLayout(card); // BorderLayout

		add("LOGIN", login); // �α���â

		setSize(1000, 800); // ������â ũ�� ����
		setLocation(170, 50); // â ��ġ
		setVisible(true); // ��������
		setResizable(false); // ������ â ����(�ø� �� ����)

		add("WR", waitR); // ����â

		login.btJoin.addActionListener(this);// ȸ�����Թ�ư ������
		login.btLogin.addActionListener(this);// �α��� ��ư ������

		waitR.profile.addActionListener(this);// ���� ��ư
		waitR.help.addActionListener(this);// ���� ��ư
		waitR.make.addActionListener(this);// �游��� â �˾�
		waitR.RoomList.addMouseListener(this);// �濡 ����
		makeR.submit.addActionListener(this);// �游��� Ȯ��
		gameR.exit.addActionListener(this);// ���ӹ濡�� ������

		add("GW", gameR); // ���� ������â
	}

	@SuppressWarnings("deprecation")
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

		if (e.getSource() == login.btJoin) {
			join.setBounds(580, 350, 600, 450);
			join.setVisible(true);
		} else if (e.getSource() == login.btLogin) {

			// ���̵�� ��й�ȣ �Է��ߴ��� Ȯ��
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

				if (checkPwd == 1) {// ���� â���� ��ȯ
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
		/*
		 * else if (e.getSource() == waitR.chatF || e.getSource() == waitR.b1) {
		 * // ä��â�̰ų� ������ ������..? String data = waitR.chatF.getText();
		 * waitR.chat.append(data + "\n"); waitR.chatF.setText("");
		 * 
		 * }
		 */
		else if (e.getSource() == waitR.profile) {
			// newprofile = new Profile(id, waitR);
		} else if (e.getSource() == waitR.make) {
			// �游����ư�� ������
			makeR.setBounds(500, 300, 420, 300);
			makeR.setVisible(true);

		} else if (e.getSource() == makeR.submit) {
			// ����â���� ��ȯ
			try {
				writer.write(Protocol.MAKEROOM + "|" + id + "|" + makeR.nameF.getText() + '\n');// -------------------------->��������!!ok
				writer.flush();
			} catch (Exception ee) {
				ee.printStackTrace();
			}
			roomName = makeR.nameF.getText();
			makeR.dispose();
			card.show(getContentPane(), "GW");
			HalliGalli_Client client = new HalliGalli_Client(id, roomName, gameR);
		} else if (e.getSource() == gameR.exit) {// ���� ������ ��ư ������
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

				HalliGalli_Client client = new HalliGalli_Client(id, roomName, gameR);// ����
				// ���

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

			// ������ ���� �о���� //������ ���� ����
			/*
			 * out.write((Protocol.LOGIN+"|"+id+"|" +pass+"\n").getBytes());
			 */
		} catch (Exception ex) {
		}

		new Thread(this).start(); // run()���� �̵� // �����κ��� ���䰪�� �޾Ƽ� ó��
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

				case Protocol.IDCHECK: {// ������ ���� ���̵� �ߺ�üũ ���� �޾ƿ�
					int check = Integer.parseInt(st.nextToken());
					System.out.println(check);
					if (check == -1)
						join.pop_up("Please use another ID.");
					else
						join.pop_up("You can use your ID.");

				}
					break;

				case Protocol.IDEXIST: {// ������ ���� ���̵� �ߺ�üũ ���� �޾ƿ�
					int check = Integer.parseInt(st.nextToken());
					System.out.println(check);
					if (check == -1)
						idExist = 1;
					else
						idExist = -1;

				}
					break;

				case Protocol.CHECKPWD: {// ������ ���� ��й�ȣ�� �´��� �޾ƿ�
					int check = Integer.parseInt(st.nextToken());
					System.out.println(check);
					if (check == -1)
						checkPwd = 1;
					else
						checkPwd = -1;

				}
					break;

				case Protocol.ROOMLIST: {// �����κ��� ���� ���� ������ �޾ƿ�

					waitR.readMsg(protocol, st);

				}
					break;

				case Protocol.WAITCLIENTNUM: {// �����κ��� ���� ���ǿ� �ִ� Ŭ���̾�Ʈ�� ������ �޾ƿ�

					waitR.readMsg(protocol, st);

				}
					break;

				case Protocol.JOINROOM: {// ���� ���ӹ椷�� ����� �������� �� �礿�� ���̵� �޾ƿ�

					String id = st.nextToken();

					// gameR.chatA.append("[" + id + "]���� �����Ͽ����ϴ�.\n");

				}
					break;

				case Protocol.EXITROOM: {// ���� ���ӹ濡 ��� ������ ��

					String id = st.nextToken();

					// gameR.chatA.append("[" + id + "]���� �����ϼ̽��ϴ�.\n");

				}
					break;

				/*
				 * case Protocol.YOURTURN: // 0.�ڱ������� �� ī������� ��ưȰ��ȭ { //
				 * gameR.cardOpen.setBorderPainted(false); //
				 * gameR.cardOpen.setContentAreaFilled(false); //
				 * gameR.cardOpen.setEnabled(true); } break; case
				 * Protocol.DELROW: // 1.���������� client ���� ������ List ���� ���� { int
				 * rowIndex = (Integer.parseInt(st.nextToken())); //
				 * rowIndex=delIndex System.out.println("���� ��: " + rowIndex); //
				 * waitR.model2.removeRow(rowIndex); //�����ڸ���Ʈ���� ���� } break; case
				 * Protocol.CLIENTEXIT: // 2.waitRoom ä�ù濡 00���� �����̽��ϴ� ���� {
				 * waitR.chat.append(st.nextToken() + "\n"); //
				 * waitR.bar.setValue(waitR.bar.getMaximum()); } break; case
				 * Protocol.MYLOG: // 1.windowŸ��Ʋ�� ������̸� ������Ʈ { String id =
				 * st.nextToken(); setTitle(id); card.show(getContentPane(),
				 * "WR"); // waitingroom���� â ��ȯ
				 * 
				 * } break;
				 * 
				 * case Protocol.LOGIN: // 2.���������̺� ����� ������Ʈ { String[] data =
				 * { st.nextToken(), st.nextToken()
				 * 
				 * }; // waitR.model2.addRow(data); } break;
				 * 
				 * case Protocol.ROOMUSER: // 2.���ӷ� �������̺� ����������Ʈ {
				 * System.out.println("In-ROOMUSER"); String[] data = {
				 * st.nextToken() }; // gameR.model1.addRow(data); } break; case
				 * Protocol.OUTUSER: { int rowIndex =
				 * (Integer.parseInt(st.nextToken())); // rowIndex=delIndex
				 * System.out.println("���� ��: " + rowIndex); //
				 * gameR.model1.removeRow(rowIndex); }
				 * 
				 * case Protocol.WAITCHAT1: // 3.ä���� ��(waitroom) {
				 * waitR.chat.append(st.nextToken() + "\n"); //
				 * waitR.bar.setValue(waitR.bar.getMaximum()); } break;
				 * 
				 * case Protocol.ROOMCHAT: // 3.ä���� ��(gameWindow) {
				 * gameR.chat.append(st.nextToken() + "\n"); //
				 * gameR.setValue(gameR.bar.getMaximum()); validate(); } break;
				 * 
				 * case Protocol.NOTOVERLAP: // 4.ID�� �ߺ����� ���� �� {
				 * JOptionPane.showMessageDialog(this, "ID�� �ߺ����� �ʽ��ϴ�"); //
				 * join.ck=true; join.pwdF.requestFocus(); } break;
				 * 
				 * /* case Protocol.OVERLAP: //4.ID�� �ߺ��� �� {
				 * JOptionPane.showMessageDialog(this, "ID�� �ߺ��˴ϴ�. �ٽ� �Է��ϼ���.");
				 * //join.ck=false; join.nameF.requestFocus(); } break;
				 */

				/*
				 * case Protocol.MAKEROOM: // 5.client�� �游��� Ȯ�� ��ư�� ������ ��(����â
				 * ��ȯ) { String roomId = st.nextToken(); // ���ӷ� ���� ��� id String
				 * roomName = st.nextToken(); // ���� ���� ���ӷ��� �̸� String humanNum =
				 * st.nextToken(); // �����ο��� //���� �Ⱦ��� String capaNum =
				 * st.nextToken(); // �ִ��ο��� //���� �Ⱦ��� setTitle("����_" + roomId +
				 * "    " + "����_" + roomName); // gameR.b5.setEnabled(false);
				 * //���۹�ư ��Ȱ��ȭ gameR.chat.setText("");
				 * card.show(getContentPane(), "GW"); // ����â���� ��ȯ
				 * 
				 * } break;
				 * 
				 * case Protocol.ROOMINFORM: // 5.client�� �游��� Ȯ�� ��ư�� ������ //
				 * ��(waitRoom�� ����Ʈ�� �� �߰�) { String roomType = st.nextToken(); //
				 * ��������� String roomName = st.nextToken(); // ���ӷ��� �̸� String
				 * nnum = st.nextToken(); // �����ο� String num = st.nextToken();
				 * // �ִ��ο� String pos = st.nextToken(); // �����(���Ӵ����) String[]
				 * data = { roomType, roomName, nnum, num, pos }; //
				 * waitR.model1.addRow(data); //waitRoom�� ����Ʈ�� �� �߰�
				 * waitR.repaint(); } break;
				 * 
				 * 
				 * case Protocol.ROOMREADY: // 6.�غ��ư ������ �� ��ư ��Ȱ��ȭ {
				 * System.out.println("���������� �غ����޹���");
				 * gameR.b4.setEnabled(false); // �غ��ư��Ȱ��ȭ } break;
				 * 
				 * case Protocol.ROOMREADYBUTTON: // 7.����غ����� �� ���常 ���� Ȱ��ȭ {
				 * System.out.println("������ �������� ���۹�ư Ȱ��ȭ"); //
				 * gameR.b5.setEnabled(true); //�غ��ư��Ȱ��ȭ
				 * 
				 * } break; // case Protocol.GAMESTART: //7.����غ����� �� ���常 ���� Ȱ��ȭ
				 * // { // System.out.println("������ �������� ���۹�ư Ȱ��ȭ"); //
				 * gw.cardOpen.setBorderPainted(false); //
				 * gw.cardOpen.setContentAreaFilled(false); //
				 * gw.cardOpen.setEnabled(false); // // } // break; /* [���ο����� ]
				 * -> case Protocol.CHGROOMUSER: { // ���� �� List table �� Ư�� Row
				 * �� ���ο��� ����� int row = Integer.parseInt(st.nextToken()); String
				 * userNum = st.nextToken(); // waitR.model1.setValueAt(userNum,
				 * row, 2); waitR.repaint(); }
				 * 
				 * break;
				 * 
				 * /* [�������º���] -> case Protocol.CHGUSERPOS: { int row =
				 * Integer.parseInt(st.nextToken()); // ���ȣ
				 * System.out.println("\\\\\\--->" + row); String pos =
				 * st.nextToken(); // �����ο��� // waitR.model2.setValueAt(pos, row,
				 * 1); waitR.repaint(); } break;
				 * 
				 * /* [����º��� ] -> case Protocol.CHGROOMSTATE: { // ���� �� List
				 * table �� Ư�� Row �� ���ο��� ����� int row =
				 * Integer.parseInt(st.nextToken()); // ���ȣ String roomState =
				 * st.nextToken(); // ����� // waitR.model1.setValueAt(roomState,
				 * row, 4); waitR.repaint(); } break;
				 * 
				 * /* [�泪����] -> case Protocol.DELROOM: // �濡 ����ڰ� ���� ����� �޽��� ����
				 * { gameR.chatF.setText(""); int roomRow =
				 * Integer.parseInt(st.nextToken()); System.out.println(roomRow
				 * + "�� ����"); // waitR.model1.removeRow(roomRow);
				 * waitR.repaint(); } break; case Protocol.REPAINT: { String
				 * tmpName = st.nextToken(); int b =
				 * Integer.parseInt(st.nextToken());
				 * System.out.println("InREPAIT-ID:" + tmpName + "Number:" + b);
				 * // gameR.UpdateDraw(tmpName, b);
				 * 
				 * } break; case Protocol.CARDNUM: { String tmpName =
				 * st.nextToken(); // id int b =
				 * Integer.parseInt(st.nextToken()); // ī���
				 * System.out.println("InCARDNUM-ID:" + tmpName + "Number:" +
				 * b); // gameR.UpdateCardNum(tmpName, b); } break; case
				 * Protocol.DEAD: { gameR.chat.append("����� �׾����ϴ�.\n"); //
				 * gameR.bell.setEnabled(false); //
				 * gameR.cardOpen.setEnabled(false); } break; case
				 * Protocol.UPDATEDEAD: { String tmpName = st.nextToken();
				 * gameR.chat.append(tmpName + " ���� �׾����ϴ�.\n"); //
				 * gameR.UpdateDead(tmpName); validate(); } break; case
				 * Protocol.BELLSUCCESS: { String tmpName = st.nextToken();
				 * gameR.chat.append(tmpName + " ���� ��ġ�� �����߽��ϴ�.\n"); //
				 * gameR.bell.setEnabled(true); // gameR.CardInit(); } break;
				 * 
				 * case Protocol.BELLFAIL: { String tmpName = st.nextToken();
				 * gameR.chat.append(tmpName + "���� ��ġ�� �����Ͽ����ϴ�.\n"); //
				 * gameR.bell.setEnabled(true); validate(); } break;
				 * 
				 * case Protocol.BELL: { // gameR.bell.setEnabled(false); }
				 * break;
				 * 
				 * case Protocol.TURNINFO: { //
				 * gameR.userName[0]=st.nextToken(); //
				 * gameR.userName[1]=st.nextToken(); //
				 * gameR.userName[2]=st.nextToken(); //
				 * gameR.userName[3]=st.nextToken(); } break; case
				 * Protocol.EXITFALSE: // ���ӽ��۽� �������Ȱ��ȭ { //
				 * gameR.b6.setEnabled(false); } break;
				 * 
				 * case Protocol.IDLABEL: // ���ӽ��۽� id�� �Է� { String ID =
				 * st.nextToken(); // id for (int i = 0; i < 4; i++) { //
				 * if(ID.equals(gameR.userName[i])){ //
				 * gameR.laPlayer[i].setText("Player"+(i+1)+": "+ID); } } }
				 * break;/* -> ���� ���� case Protocol.GAMEEXIT: {
				 * System.out.println("zzzzz"); String tmpId=st.nextToken();
				 * String tmpMsg=st.nextToken();
				 * gameR.chat.append("��������=====>"+tmpId+tmpMsg);
				 * gameR.b4.setEnabled(true); gameR.b6.setEnabled(true);
				 * gameR.CardInit(); } break; }
				 * 
				 * 
				 */
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