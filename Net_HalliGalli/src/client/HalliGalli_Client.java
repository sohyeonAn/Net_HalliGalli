package client;



import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;

public class HalliGalli_Client extends JFrame implements Runnable, ActionListener {
   CardLayout card = new CardLayout(); // â ��ȯ�� ���� �ʿ��մϴ�!

   // test test = new test();
   BufferedReader reader; // �Է½�Ʈ��
   PrintWriter writer; // ��½�Ʈ��
   Socket socket; // ����
   String userName = null; // ����� �̸�
   Timer timer;// Ÿ�̸�
   int count = 0;
   int myNumber = 0;
   GameTimer time;
   String comboName = null;
   int comboNum = 0;
   String roomName;
   GameWindow board;
   
   public HalliGalli_Client(String id,String roomname, GameWindow b) {
      userName = id;
      this.roomName=roomname;
      connect();
//      add("GW",board);
//      board.setVisible(true);
//      board.setBounds(200, 200, 400, 300);
//      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//      setSize(1000, 800); // ������â ũ�� ����
//      setLocation(300, 80); // Ȱ��ȭ�Ǵ� â ��ġ
//      setVisible(true); // ��������
//      setResizable(false); // ������ â ����(�ø� �� ����)
//      setLayout(card);
      
      this.board = b;
      board.chatF.addActionListener(this);
      board.ready.addActionListener(this);
      // test.connectButton.addActionListener(this);
      // board.turn.addActionListener(this);
      board.bell.addActionListener(this);
      board.chatA.append("ȯ���մϴ� >��<\n");


      // add("test", test);
   }
   
      public void actionPerformed(ActionEvent ae) {
            try {
               if (ae.getSource() == board.chatF) // �������ؽ�Ʈ�ʵ忡 ���� �Է����� ���
               {
                  String msg = board.chatF.getText();
                  if (msg.length() == 0) {
                     return;
                  }
                  if (msg.length() >= 30) {
                     msg = msg.substring(0, 30);
                  }
                  writer.println("[MSG]|"+userName+"|"+roomName+"|"+ msg);
                  board.chatF.setText("");
               } 
             else if (ae.getSource() == board.ready) // ����������ư�� ������ ���
               {

                  if (!board.Ready) {
                     board.Ready = true;
                     writer.println("[READY]|"+userName+"|"+roomName);
                    board.ready.setText("�غ�����");
                    
                  } else {
                     board.Ready = false;
                     writer.println("[NOREADY]|"+userName+"|"+roomName);
                     board.ready.setText("�غ�");
                  }
               }
             //  else if (ae.getSource() == board.turn) // ī��������ư�� ������ ���
            //   {
             //     board.info.setText("����� ī�带 ���������ϴ�.");
              //    writer.println("[TURN]" + userName);
             //  } 
               else if (ae.getSource() == board.bell) // ��ġ���ư�� ������ ���
               {
                  writer.println("[BELL]|"+userName+"|"+roomName);
               }
            } catch (Exception e) {
            }
         }




   public void run() {
      String msg;

      
      try {
         while ((msg = reader.readLine()) != null) {

            System.out.println(msg);

            StringTokenizer st = new StringTokenizer(msg, "|");
            String protocol =st.nextToken();
            /*
             * if (msg.startsWith("[FULL]")) // ������ �����ο��� ��á�� ��� {
             * test.la_GameInfo.setText("�濡 �ο��� ��á���ϴ�."); }
             */

            if (protocol.equals("[MSG]")) {
               String userName=st.nextToken();
               String m=st.nextToken();
               board.chatA.append(userName+"|"+m + "\n");
            } else if (msg.startsWith("[PLAYERS]")) { // �÷��̾��Ʈ�� �޴´�.
               //nameList(msg.substring(9));
              // card.show(getContentPane(), "Game");
            }

            else if (protocol.equals("[ENTER]")) // ���� ������ ���
            {
               String id=st.nextToken();
               board.chatA.append("[" + id + "]���� �����Ͽ����ϴ�.\n");
               // test.scPane.getVerticalScrollBar().setValue(test.scPane.getVerticalScrollBar().getMaximum());
               validate();
            }

            else if (protocol.equals("[DISCONNECT]")) // ������ ���������
            {
               String id=st.nextToken();

               // test.model.removeElement(msg.substring(6));
               // playersInfo();
               board.chatA.append("[" + id + "]���� �������ϴ�.\n");
               // test.scPane.getVerticalScrollBar().setValue(test.scPane.getVerticalScrollBar().getMaximum());
               validate();
            } else if (protocol.equals("[READY]")) {
               String id=st.nextToken();
               board.info.setText("[" +id + "]���� �غ� �����Ͽ����ϴ�.\n");
            } else if (msg.startsWith("������ �����մϴ�.")) // �������� ������ ���۵� ���
            { // board.setLabel(test.model);

               try {
                  Thread.sleep(2000);
               } catch (InterruptedException e) { // TODO Auto-generated
                                          // catch block
                  e.printStackTrace();
               }

               timer = new Timer();

               timer.schedule(new GameTimer(writer, userName,roomName), myNumber * 3000, 1000 * 12);

               board.bell.setEnabled(true);
               board.ready.setEnabled(false);
            }

            else if (protocol.equals("[REPAINT]")) // �������� ī������� �� �ٽ� �׸��� ��û
            {
               String userName=st.nextToken();
               int b = Integer.parseInt(st.nextToken());
               board.UpdateDraw(userName, b);
            }

            else if (protocol.equals("[CARDNUM]")) // ���� ī����� �޴´�.
            {
               String m=st.nextToken();
               int b = Integer.parseInt(st.nextToken());
               board.UpdateCardNum(m, b);
            } else if (protocol.equals("[DEAD]")) // ī�尡 ��� �׾��� ��� �޴� �޼���
            {
               board.info.setText("����� �׾����ϴ�.");
               board.info.setEnabled(false);
               // board.turn.setEnabled(false);
               board.bell.setEnabled(false);
               // time.cancel();
            }

            else if (protocol.equals("[UPDATEDEAD]")) // ���� ������ �󺧰� ī�带 ����
            {
               String id=st.nextToken();
               board.info.setText(id + "���� �׾����ϴ�.\n");
               // test.scPane.getVerticalScrollBar().setValue(test.scPane.getVerticalScrollBar().getMaximum());
               validate();
               board.UpdateDead(id);
            }

            else if (protocol.equals("[SUCCESS]")) // ��ġ�� �������� ���
            {
               String id=st.nextToken();

               board.info.setText(id + "���� ��ġ�� �����߽��ϴ�.");
               board.bell.setEnabled(true);
               board.CardInit();
            } else if (protocol.equals("[FAIL]")) // ��ġ�⿡ �������� ���
            {
               String id=st.nextToken();

               board.info.setText(id + "���� ��ġ�� �����߽��ϴ�.");
               board.bell.setEnabled(true);
               validate();
            } else if (protocol.equals("[GAMEINIT]")) // ������ ������ �ʱ�ȭ�� ��û�� ���
            {
               board.CardInit();
               board.ready.setEnabled(true);
               board.ready.setText("�غ�");

               timer = new Timer();
               time = new GameTimer(writer, userName,roomName);

               timer.schedule(time, myNumber * 3000, 1000 * 12);

               board.Ready = false;

            } else if (protocol.equals("[WIN]")) {
               board.info.setText("����� �̰���ϴ�.");
               // board.turn.setEnabled(false);
               board.bell.setEnabled(false);
            } else if (protocol.equals("[BELL]")) {
               board.bell.setEnabled(false);// ���� ĥ �� ���� �����.
            } else if (protocol.equals("[SEQUENCE]")) {
               String tmp=st.nextToken();
               while (st.hasMoreElements()) {
                  if (tmp.startsWith(userName)) {
                     System.out.println("MyNAme = "+tmp);
                     tmp=st.nextToken();
                     System.out.println(tmp);
                     myNumber = Integer.parseInt(tmp);
                     System.out.println("myNumber = " + myNumber);
                     break;
                  }
               }
            } else if (protocol.equals("[COMBO]")) {
               String tmp=st.nextToken();//user
               while (st.hasMoreElements()) {
                  comboName =tmp;
                  System.out.println(comboName);// userName
                  tmp = st.nextToken();
                  comboNum = Integer.parseInt(tmp);
                  System.out.println("comboNum = " + comboNum);

               } // �� ���� �Ϸ�

               for (int i = 0; i < 4; i++) {
                  if (board.laPlayer[i].getText().equals(comboName)) {// combo��
                                                         // �޼���
                                                         // �����
                                                         // �̸���
                     if (comboNum == 0)
                        board.combo[i].setText(" ");
                     else
                        board.combo[i].setText("Combo " + comboNum);
                  } else {
                     board.combo[i].setText(" ");

                  }
               }

            } else // �׳� �޼����� ������� �׳� ���
            {
            	board.info.setText(msg + "\n");
               // test.scPane.getVerticalScrollBar().setValue(test.scPane.getVerticalScrollBar().getMaximum());
               validate();
            }
         }
      } catch (

      IOException ie)

      {
         board.chatA.append(ie + "\n");
         // test.scPane.getVerticalScrollBar().setValue(test.scPane.getVerticalScrollBar().getMaximum());
         validate();
      }
      board.chatA.append("������ ������ϴ�.\n");

      // test.scPane.getVerticalScrollBar().setValue(test.scPane.getVerticalScrollBar().getMaximum());
      validate();
      
      
      
      
      

   }

   /*
    * private void playersInfo() // �÷��̾��� ���� �ο������� ��� { int count =
    * test.model.getSize(); test.la_PlayerInfo.setText("���� " + count + "������");
    * }
    */

   /*
    * private void nameList(String msg) // �������� ���� �÷��̾� ����Ʈ�� �з��ؼ� ����Ʈ�� ����. {
    * //test.model.removeAllElements(); StringTokenizer st = new
    * StringTokenizer(msg, "\t"); while (st.hasMoreElements()) {
    * test.model.addElement(st.nextToken()); } playersInfo(); }
    */

   private void connect() // ������ ����
   {
      try {
         String ip = "localhost";
         System.out.println("���ӹ� ������ ������ ��û�մϴ�.");
         socket = new Socket(ip, 333);
         System.out.println("--���� ����--");
         reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
         writer = new PrintWriter(socket.getOutputStream(), true);
         writer.println("[CONNECT]|" + userName+"|"+roomName);
         new Thread(this).start();
         board.setWriter(writer);
      } catch (Exception e) {
         System.out.println(e + "���� ����..");
      }
   }

}