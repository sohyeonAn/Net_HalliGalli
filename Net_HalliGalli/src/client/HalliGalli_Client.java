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
   CardLayout card = new CardLayout(); // 창 전환을 위해 필요합니다!

   // test test = new test();
   BufferedReader reader; // 입력스트림
   PrintWriter writer; // 출력스트림
   Socket socket; // 소켓
   String userName = null; // 사용자 이름
   Timer timer;// 타이머
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
//      setSize(1000, 800); // 윈도우창 크기 설정
//      setLocation(300, 80); // 활성화되는 창 위치
//      setVisible(true); // 보여지게
//      setResizable(false); // 윈도우 창 고정(늘릴 수 없음)
//      setLayout(card);
      
      this.board = b;
      board.chatF.addActionListener(this);
      board.ready.addActionListener(this);
      // test.connectButton.addActionListener(this);
      // board.turn.addActionListener(this);
      board.bell.addActionListener(this);
      board.chatA.append("환영합니다 >ㅇ<\n");


      // add("test", test);
   }
   
      public void actionPerformed(ActionEvent ae) {
            try {
               if (ae.getSource() == board.chatF) // 보내기텍스트필드에 글을 입력했을 경우
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
             else if (ae.getSource() == board.ready) // 레디해제버튼을 눌렀을 경우
               {

                  if (!board.Ready) {
                     board.Ready = true;
                     writer.println("[READY]|"+userName+"|"+roomName);
                    board.ready.setText("준비해제");
                    
                  } else {
                     board.Ready = false;
                     writer.println("[NOREADY]|"+userName+"|"+roomName);
                     board.ready.setText("준비");
                  }
               }
             //  else if (ae.getSource() == board.turn) // 카드뒤집기버튼을 눌렀을 경우
            //   {
             //     board.info.setText("당신이 카드를 뒤집었습니다.");
              //    writer.println("[TURN]" + userName);
             //  } 
               else if (ae.getSource() == board.bell) // 종치기버튼을 눌렀을 경우
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
             * if (msg.startsWith("[FULL]")) // 서버에 접속인원이 다찼을 경우 {
             * test.la_GameInfo.setText("방에 인원이 다찼습니다."); }
             */

            if (protocol.equals("[MSG]")) {
               String userName=st.nextToken();
               String m=st.nextToken();
               board.chatA.append(userName+"|"+m + "\n");
            } else if (msg.startsWith("[PLAYERS]")) { // 플레이어리스트를 받는다.
               //nameList(msg.substring(9));
              // card.show(getContentPane(), "Game");
            }

            else if (protocol.equals("[ENTER]")) // 상대방 입장할 경우
            {
               String id=st.nextToken();
               board.chatA.append("[" + id + "]님이 입장하였습니다.\n");
               // test.scPane.getVerticalScrollBar().setValue(test.scPane.getVerticalScrollBar().getMaximum());
               validate();
            }

            else if (protocol.equals("[DISCONNECT]")) // 접속이 끊어진경우
            {
               String id=st.nextToken();

               // test.model.removeElement(msg.substring(6));
               // playersInfo();
               board.chatA.append("[" + id + "]님이 나갔습니다.\n");
               // test.scPane.getVerticalScrollBar().setValue(test.scPane.getVerticalScrollBar().getMaximum());
               validate();
            } else if (protocol.equals("[READY]")) {
               String id=st.nextToken();
               board.info.setText("[" +id + "]님이 준비를 해제하였습니다.\n");
            } else if (msg.startsWith("게임을 시작합니다.")) // 서버에서 게임이 시작된 경우
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

            else if (protocol.equals("[REPAINT]")) // 서버에서 카드뒤집은 뒤 다시 그리기 요청
            {
               String userName=st.nextToken();
               int b = Integer.parseInt(st.nextToken());
               board.UpdateDraw(userName, b);
            }

            else if (protocol.equals("[CARDNUM]")) // 현재 카드수를 받는다.
            {
               String m=st.nextToken();
               int b = Integer.parseInt(st.nextToken());
               board.UpdateCardNum(m, b);
            } else if (protocol.equals("[DEAD]")) // 카드가 없어서 죽었을 경우 받는 메세지
            {
               board.info.setText("당신은 죽었습니다.");
               board.info.setEnabled(false);
               // board.turn.setEnabled(false);
               board.bell.setEnabled(false);
               // time.cancel();
            }

            else if (protocol.equals("[UPDATEDEAD]")) // 죽은 유저의 라벨과 카드를 수정
            {
               String id=st.nextToken();
               board.info.setText(id + "님이 죽었습니다.\n");
               // test.scPane.getVerticalScrollBar().setValue(test.scPane.getVerticalScrollBar().getMaximum());
               validate();
               board.UpdateDead(id);
            }

            else if (protocol.equals("[SUCCESS]")) // 종치기 성공했을 경우
            {
               String id=st.nextToken();

               board.info.setText(id + "님이 종치기 성공했습니다.");
               board.bell.setEnabled(true);
               board.CardInit();
            } else if (protocol.equals("[FAIL]")) // 종치기에 실패했을 경우
            {
               String id=st.nextToken();

               board.info.setText(id + "님이 종치기 실패했습니다.");
               board.bell.setEnabled(true);
               validate();
            } else if (protocol.equals("[GAMEINIT]")) // 게임이 끝나서 초기화를 요청한 경우
            {
               board.CardInit();
               board.ready.setEnabled(true);
               board.ready.setText("준비");

               timer = new Timer();
               time = new GameTimer(writer, userName,roomName);

               timer.schedule(time, myNumber * 3000, 1000 * 12);

               board.Ready = false;

            } else if (protocol.equals("[WIN]")) {
               board.info.setText("당신이 이겼습니다.");
               // board.turn.setEnabled(false);
               board.bell.setEnabled(false);
            } else if (protocol.equals("[BELL]")) {
               board.bell.setEnabled(false);// 종을 칠 수 없게 만든다.
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

               } // 값 받이 완료

               for (int i = 0; i < 4; i++) {
                  if (board.laPlayer[i].getText().equals(comboName)) {// combo를
                                                         // 달성한
                                                         // 사람이
                                                         // 이름값
                     if (comboNum == 0)
                        board.combo[i].setText(" ");
                     else
                        board.combo[i].setText("Combo " + comboNum);
                  } else {
                     board.combo[i].setText(" ");

                  }
               }

            } else // 그냥 메세지만 왔을경우 그냥 출력
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
      board.chatA.append("접속이 끊겼습니다.\n");

      // test.scPane.getVerticalScrollBar().setValue(test.scPane.getVerticalScrollBar().getMaximum());
      validate();
      
      
      
      
      

   }

   /*
    * private void playersInfo() // 플레이어의 현재 인원수정보 출력 { int count =
    * test.model.getSize(); test.la_PlayerInfo.setText("현재 " + count + "명접속");
    * }
    */

   /*
    * private void nameList(String msg) // 서버에서 보낸 플레이어 리스트를 분류해서 리스트에 저장. {
    * //test.model.removeAllElements(); StringTokenizer st = new
    * StringTokenizer(msg, "\t"); while (st.hasMoreElements()) {
    * test.model.addElement(st.nextToken()); } playersInfo(); }
    */

   private void connect() // 서버에 연결
   {
      try {
         String ip = "localhost";
         System.out.println("게임방 서버에 연결을 요청합니다.");
         socket = new Socket(ip, 333);
         System.out.println("--연결 성공--");
         reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
         writer = new PrintWriter(socket.getOutputStream(), true);
         writer.println("[CONNECT]|" + userName+"|"+roomName);
         new Thread(this).start();
         board.setWriter(writer);
      } catch (Exception e) {
         System.out.println(e + "연결 실패..");
      }
   }

}