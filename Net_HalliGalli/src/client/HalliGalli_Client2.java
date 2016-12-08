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

public class HalliGalli_Client2 extends JFrame implements Runnable, ActionListener {
   CardLayout card = new CardLayout();   // 창 전환을 위해 필요합니다!

   long startTime, endTime;
   test test = new test();
   GameWindow2 board = new GameWindow2(null);
   BufferedReader reader; // 입력스트림
   PrintWriter writer; // 출력스트림
   Socket socket; // 소켓
   String userName = null; // 사용자 이름
   Timer timer;// 타이머
   int count=0;
   int myNumber=0;
   GameTimer time;
   String comboName=null;
   int comboNum=0;
   public HalliGalli_Client2() {
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setSize(1000,800);            //윈도우창 크기 설정
      setLocation(300,80);         //활성화되는 창 위치
      setVisible(true);            //보여지게
      setResizable(false);         //윈도우 창 고정(늘릴 수 없음)
      setLayout(card);
      
      add("test",test);
      
      
      board.chatF.addActionListener(this);
      board.ready.addActionListener(this);
      test.connectButton.addActionListener(this);
      board.autoI.addActionListener(this);
      board.changeI.addActionListener(this);
      board.secretI.addActionListener(this);
      board.seethI.addActionListener(this);
    //  board.turn.addActionListener(this);
      board.bell.addActionListener(this);
      board.chatA.append("환영합니다 >ㅇ<\n");
      
      add("Game", board);
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
            writer.println("[MSG]" + msg);
            board.chatF.setText("");
         } 
         else if (ae.getSource() == test.connectButton) // 접속버튼을 눌렀을 경우
         {
            String name = test.tf_Name.getText().trim();
            if (name.length() <= 2 || name.length() > 10) {
               test.la_GameInfo.setText("이름이 잘못되었습니다.");
               test.tf_Name.requestFocus();
               return;
            }
            connect();            
            
            userName = name;
            writer.println("[CONNECT]" + userName);            
            test.connectButton.setEnabled(false);
          
         } else if (ae.getSource() == board.ready) // 레디해제버튼을 눌렀을 경우
         {

            if (!test.ready) {
               test.ready = true;
               writer.println("[READY]");
              board.ready.setText("준비해제");
              
            } else {
               test.ready = false;
               writer.println("[NOREADY]");
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
            long tmp;
            endTime = System.currentTimeMillis(); 
            tmp  = startTime-endTime;
            writer.println("[BELL]" + userName+"|"+tmp);
         }
         else if(ae.getSource() == board.autoI){// click the auto button
            writer.println("[ITEM]|[AUTO]|" + userName);
            String tmp =null;
            tmp = userName + "님이 auto 아이템을 사용했습니다.";
            System.out.println(tmp);
            writer.println("[MSG]" + tmp);
         }
         else if(ae.getSource() == board.seethI){// click the item
            writer.println("[ITEM]|[SEETHI]|" + userName);
            String tmp =null;
            tmp = userName + "님이 see through 아이템을 사용했습니다.";
            System.out.println(tmp);
            writer.println("[MSG]" + tmp);
         }
         else if(ae.getSource() == board.secretI){
            writer.println("[ITEM]|[SECRET]|" + userName);
            String tmp =null;
            tmp = userName + "님이 see secret 아이템을 사용했습니다.";
            System.out.println(tmp);
            writer.println("[MSG]" + tmp);
         }
         else if(ae.getSource() == board.changeI){
            writer.println("[ITEM]|[CHANGE]|" + userName);
            String tmp =null;
           // tmp = userName + "님이 see change 아이템을 사용했습니다.";
          //  System.out.println(tmp);
            //writer.println("[MSG]" + tmp);
         }
      } catch (Exception e) {
      }
   }

   public void run() {
      String msg;
      try {
         while ((msg = reader.readLine()) != null) {
        
            if (msg.startsWith("[FULL]")) // 서버에 접속인원이 다찼을 경우
            {
               test.la_GameInfo.setText("방에 인원이 다찼습니다.");
            }

            else if(msg.startsWith("[MSG]"))
            {
               board.chatA.append(msg.substring(5)+"\n");
            }
            else if (msg.startsWith("[PLAYERS]")) // 플레이어리스트를 받는다.
            {
               nameList(msg.substring(9));
               card.show(getContentPane(), "Game");
            }

            else if (msg.startsWith("[ENTER]")) // 상대방 입장할 경우
            {
               test.model.addElement(msg.substring(7));
               playersInfo();
               board.chatA.append("[" + msg.substring(7) + "]님이 입장하였습니다.\n");
               test.scPane.getVerticalScrollBar().setValue(test.scPane.getVerticalScrollBar().getMaximum());
               validate();
            }

            else if (msg.startsWith("[DISCONNECT]")) // 접속이 끊어진경우
            {
               test.model.removeElement(msg.substring(6));
               playersInfo();
               board.chatA.append("[" + msg.substring(12) + "]님이 나갔습니다.\n");
               test.scPane.getVerticalScrollBar().setValue(test.scPane.getVerticalScrollBar().getMaximum());
               validate();
            }
            else if (msg.startsWith("[READY]"))
            {
               board.chatA.append("["+msg.substring(12)+"]님이 준비를 해제하였습니다.\n");
            }
            else if (msg.startsWith("게임을 시작합니다.")) // 서버에서 게임이 시작된 경우
            {
               board.setLabel(test.model);
              
               try {
            Thread.sleep(2000);
         } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
               
               timer = new Timer();
               
               timer.schedule(new GameTimer(writer, userName), myNumber * 3000,1000*12);
               startTime =System.currentTimeMillis(); 
               board.bell.setEnabled(true);
               board.ready.setEnabled(false);
            }

            else if (msg.startsWith("[REPAINT]")) // 서버에서 카드뒤집은 뒤 다시 그리기 요청
            {
               int a = msg.indexOf("|");
               int b = Integer.parseInt(msg.substring(a + 1));
               board.UpdateDraw(msg.substring(9, a), b);
            }

            else if (msg.startsWith("[CARDNUM]")) // 현재 카드수를 받는다.
            {
               int a = msg.indexOf("|");
               int b = Integer.parseInt(msg.substring(a + 1));
               board.UpdateCardNum(msg.substring(9, a), b);
            } else if (msg.startsWith("[DEAD]")) // 카드가 없어서 죽었을 경우 받는 메세지
            {
               board.info.setText("당신은 죽었습니다.");
               board.info.setEnabled(false);
             //  board.turn.setEnabled(false);
               board.bell.setEnabled(false);
               time.cancel();
            }

            else if (msg.startsWith("[UPDATEDEAD]")) // 죽은 유저의 라벨과 카드를 수정
            {
               board.info.setText(msg.substring(12) + "님이 죽었습니다.\n");
               test.scPane.getVerticalScrollBar().setValue(test.scPane.getVerticalScrollBar().getMaximum());
               validate();
               board.UpdateDead(msg.substring(12));
            }

            else if (msg.startsWith("[SUCCESS]")) // 종치기 성공했을 경우
            {
               board.info.setText(msg.substring(9) + "님이 종치기 성공했습니다.");
               board.bell.setEnabled(true);
               board.CardInit();
            } else if (msg.startsWith("[FAIL]")) // 종치기에 실패했을 경우
            {
               board.info.setText(msg.substring(6) + "님이 종치기 실패했습니다.");
               board.bell.setEnabled(true);
               validate();
            } else if (msg.startsWith("[GAMEINIT]")) // 게임이 끝나서 초기화를 요청한 경우
            {
               board.CardInit();
               board.ready.setEnabled(true);
               board.ready.setText("준비");
               
               timer = new Timer();
               time = new GameTimer(writer, userName);
               
               
               timer.schedule(time, myNumber * 3000,1000*12);
               
               test.ready = false;
            } else if (msg.startsWith("[WIN]")) {
               board.info.setText("당신이 이겼습니다.");
          //     board.turn.setEnabled(false);
               board.bell.setEnabled(false);
            } else if (msg.startsWith("[BELL]")) {
               board.bell.setEnabled(false);// 종을 칠 수 없게 만든다.
            } else if(msg.startsWith("[SEQUENCE]")){
               StringTokenizer st = new StringTokenizer(msg, "|");
               String tmp;
               System.out.println(msg);
                while (st.hasMoreElements()) {
                  tmp =st.nextToken();
                  if(tmp.startsWith(userName)){
                     System.out.println(tmp);
                     tmp = tmp.substring(userName.length()+1);
                     System.out.println(tmp);
                     myNumber= Integer.parseInt(tmp);
                     System.out.println("myNumber = "+ myNumber);
                     break;
                  }
                }
            }
            else if(msg.startsWith("[COMBO]")){
               StringTokenizer st = new StringTokenizer(msg, "|");
               String tmp;
                while (st.hasMoreElements()) {
                 tmp =st.nextToken(); 
                 tmp =st.nextToken();
                 comboName = tmp.substring(0,tmp.length()-2);
                   System.out.println(comboName);// userName
                 tmp = tmp.substring(tmp.length()-1);
                 comboNum= Integer.parseInt(tmp);
                 System.out.println("comboNum = "+ comboNum);
                     
                  
                }// 값 받이 완료
                
                for(int i=0; i<4; i++){
                   if(board.laPlayer[i].getText().equals(comboName)){// combo를 달성한 사람이 이름값
                      if(comboNum  ==0)
                         board.combo[i].setText(" ");
                      else
                         board.combo[i].setText("Combo "+ comboNum);
                   }
                   else{
                      board.combo[i].setText(" ");

                   }
                }
                
            }
            else if(msg.startsWith("[ITEM]")){// [item]|
               msg = msg.substring(7);
               StringTokenizer st = new StringTokenizer(msg, "|");

               msg = st.nextToken();
               if(msg.startsWith("[AUTO]")){
                      msg = st.nextToken();
                      String tmp = msg;// tmp = userName
                      if(tmp.startsWith(userName)){
                         
                      }
                }
                else if(msg.startsWith("[SEETHI]")){
                   msg = st.nextToken();
                }
                else if(msg.startsWith("[SECRET]")){
                   msg = st.nextToken();
                }
                else if(msg.startsWith("[CHANGE]")){
                   msg = st.nextToken();
                }
            }
            else if(msg.startsWith("[ITEM]")){// [item]|
               StringTokenizer st = new StringTokenizer(msg, "|");

               msg = st.nextToken();
               if(msg.startsWith("[AUTO]")){
                      msg = st.nextToken();// msg = username
                   
                      
                      
                    
                }
                else if(msg.startsWith("[SEETHI]")){
                   msg = st.nextToken();
                }
                else if(msg.startsWith("[SECRET]")){
                   msg = st.nextToken();
                }
                else if(msg.startsWith("[CHANGE]")){
                   msg = st.nextToken();
                }
            }
            else // 그냥 메세지만 왔을경우 그냥 출력
            {
               board.info.setText(msg + "\n");
               test.scPane.getVerticalScrollBar().setValue(test.scPane.getVerticalScrollBar().getMaximum());
               validate();
            }
         }
      } catch (IOException ie) {
         board.chatA.append(ie + "\n");
         test.scPane.getVerticalScrollBar().setValue(test.scPane.getVerticalScrollBar().getMaximum());
         validate();
      }
      board.chatA.append("접속이 끊겼습니다.\n");
      test.scPane.getVerticalScrollBar().setValue(test.scPane.getVerticalScrollBar().getMaximum());
      validate();
   }

   private void playersInfo() // 플레이어의 현재 인원수정보 출력
   {
      int count = test.model.getSize();
      test.la_PlayerInfo.setText("현재 " + count + "명접속");
   }

   private void nameList(String msg) // 서버에서 보낸 플레이어 리스트를 분류해서 리스트에 저장.
   {
      test.model.removeAllElements();
      StringTokenizer st = new StringTokenizer(msg, "\t");
      while (st.hasMoreElements()) {
        test. model.addElement(st.nextToken());
      }
      playersInfo();
   }

   private void connect() // 서버에 연결
   {
      try {
         String ip = "localhost";
         test.ta_MsgView.append("서버에 연결을 요청합니다.\n");
         socket = new Socket(ip, 1112);
         test.ta_MsgView.append("--연결 성공--\n");
         reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
         writer = new PrintWriter(socket.getOutputStream(), true);
         new Thread(this).start();
         board.setWriter(writer);
      } catch (Exception e) {
         test.ta_MsgView.append(e + "\n\n연결 실패..\n");
      }
   }

   public static void main(String[] args) {
      HalliGalli_Client2 client = new HalliGalli_Client2();

      
   }
}