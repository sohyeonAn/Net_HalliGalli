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
   CardLayout card = new CardLayout();   // â ��ȯ�� ���� �ʿ��մϴ�!

   long startTime, endTime;
   test test = new test();
   GameWindow2 board = new GameWindow2(null);
   BufferedReader reader; // �Է½�Ʈ��
   PrintWriter writer; // ��½�Ʈ��
   Socket socket; // ����
   String userName = null; // ����� �̸�
   Timer timer;// Ÿ�̸�
   int count=0;
   int myNumber=0;
   GameTimer time;
   String comboName=null;
   int comboNum=0;
   public HalliGalli_Client2() {
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setSize(1000,800);            //������â ũ�� ����
      setLocation(300,80);         //Ȱ��ȭ�Ǵ� â ��ġ
      setVisible(true);            //��������
      setResizable(false);         //������ â ����(�ø� �� ����)
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
      board.chatA.append("ȯ���մϴ� >��<\n");
      
      add("Game", board);
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
            writer.println("[MSG]" + msg);
            board.chatF.setText("");
         } 
         else if (ae.getSource() == test.connectButton) // ���ӹ�ư�� ������ ���
         {
            String name = test.tf_Name.getText().trim();
            if (name.length() <= 2 || name.length() > 10) {
               test.la_GameInfo.setText("�̸��� �߸��Ǿ����ϴ�.");
               test.tf_Name.requestFocus();
               return;
            }
            connect();            
            
            userName = name;
            writer.println("[CONNECT]" + userName);            
            test.connectButton.setEnabled(false);
          
         } else if (ae.getSource() == board.ready) // ����������ư�� ������ ���
         {

            if (!test.ready) {
               test.ready = true;
               writer.println("[READY]");
              board.ready.setText("�غ�����");
              
            } else {
               test.ready = false;
               writer.println("[NOREADY]");
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
            long tmp;
            endTime = System.currentTimeMillis(); 
            tmp  = startTime-endTime;
            writer.println("[BELL]" + userName+"|"+tmp);
         }
         else if(ae.getSource() == board.autoI){// click the auto button
            writer.println("[ITEM]|[AUTO]|" + userName);
            String tmp =null;
            tmp = userName + "���� auto �������� ����߽��ϴ�.";
            System.out.println(tmp);
            writer.println("[MSG]" + tmp);
         }
         else if(ae.getSource() == board.seethI){// click the item
            writer.println("[ITEM]|[SEETHI]|" + userName);
            String tmp =null;
            tmp = userName + "���� see through �������� ����߽��ϴ�.";
            System.out.println(tmp);
            writer.println("[MSG]" + tmp);
         }
         else if(ae.getSource() == board.secretI){
            writer.println("[ITEM]|[SECRET]|" + userName);
            String tmp =null;
            tmp = userName + "���� see secret �������� ����߽��ϴ�.";
            System.out.println(tmp);
            writer.println("[MSG]" + tmp);
         }
         else if(ae.getSource() == board.changeI){
            writer.println("[ITEM]|[CHANGE]|" + userName);
            String tmp =null;
           // tmp = userName + "���� see change �������� ����߽��ϴ�.";
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
        
            if (msg.startsWith("[FULL]")) // ������ �����ο��� ��á�� ���
            {
               test.la_GameInfo.setText("�濡 �ο��� ��á���ϴ�.");
            }

            else if(msg.startsWith("[MSG]"))
            {
               board.chatA.append(msg.substring(5)+"\n");
            }
            else if (msg.startsWith("[PLAYERS]")) // �÷��̾��Ʈ�� �޴´�.
            {
               nameList(msg.substring(9));
               card.show(getContentPane(), "Game");
            }

            else if (msg.startsWith("[ENTER]")) // ���� ������ ���
            {
               test.model.addElement(msg.substring(7));
               playersInfo();
               board.chatA.append("[" + msg.substring(7) + "]���� �����Ͽ����ϴ�.\n");
               test.scPane.getVerticalScrollBar().setValue(test.scPane.getVerticalScrollBar().getMaximum());
               validate();
            }

            else if (msg.startsWith("[DISCONNECT]")) // ������ ���������
            {
               test.model.removeElement(msg.substring(6));
               playersInfo();
               board.chatA.append("[" + msg.substring(12) + "]���� �������ϴ�.\n");
               test.scPane.getVerticalScrollBar().setValue(test.scPane.getVerticalScrollBar().getMaximum());
               validate();
            }
            else if (msg.startsWith("[READY]"))
            {
               board.chatA.append("["+msg.substring(12)+"]���� �غ� �����Ͽ����ϴ�.\n");
            }
            else if (msg.startsWith("������ �����մϴ�.")) // �������� ������ ���۵� ���
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

            else if (msg.startsWith("[REPAINT]")) // �������� ī������� �� �ٽ� �׸��� ��û
            {
               int a = msg.indexOf("|");
               int b = Integer.parseInt(msg.substring(a + 1));
               board.UpdateDraw(msg.substring(9, a), b);
            }

            else if (msg.startsWith("[CARDNUM]")) // ���� ī����� �޴´�.
            {
               int a = msg.indexOf("|");
               int b = Integer.parseInt(msg.substring(a + 1));
               board.UpdateCardNum(msg.substring(9, a), b);
            } else if (msg.startsWith("[DEAD]")) // ī�尡 ��� �׾��� ��� �޴� �޼���
            {
               board.info.setText("����� �׾����ϴ�.");
               board.info.setEnabled(false);
             //  board.turn.setEnabled(false);
               board.bell.setEnabled(false);
               time.cancel();
            }

            else if (msg.startsWith("[UPDATEDEAD]")) // ���� ������ �󺧰� ī�带 ����
            {
               board.info.setText(msg.substring(12) + "���� �׾����ϴ�.\n");
               test.scPane.getVerticalScrollBar().setValue(test.scPane.getVerticalScrollBar().getMaximum());
               validate();
               board.UpdateDead(msg.substring(12));
            }

            else if (msg.startsWith("[SUCCESS]")) // ��ġ�� �������� ���
            {
               board.info.setText(msg.substring(9) + "���� ��ġ�� �����߽��ϴ�.");
               board.bell.setEnabled(true);
               board.CardInit();
            } else if (msg.startsWith("[FAIL]")) // ��ġ�⿡ �������� ���
            {
               board.info.setText(msg.substring(6) + "���� ��ġ�� �����߽��ϴ�.");
               board.bell.setEnabled(true);
               validate();
            } else if (msg.startsWith("[GAMEINIT]")) // ������ ������ �ʱ�ȭ�� ��û�� ���
            {
               board.CardInit();
               board.ready.setEnabled(true);
               board.ready.setText("�غ�");
               
               timer = new Timer();
               time = new GameTimer(writer, userName);
               
               
               timer.schedule(time, myNumber * 3000,1000*12);
               
               test.ready = false;
            } else if (msg.startsWith("[WIN]")) {
               board.info.setText("����� �̰���ϴ�.");
          //     board.turn.setEnabled(false);
               board.bell.setEnabled(false);
            } else if (msg.startsWith("[BELL]")) {
               board.bell.setEnabled(false);// ���� ĥ �� ���� �����.
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
                     
                  
                }// �� ���� �Ϸ�
                
                for(int i=0; i<4; i++){
                   if(board.laPlayer[i].getText().equals(comboName)){// combo�� �޼��� ����� �̸���
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
            else // �׳� �޼����� ������� �׳� ���
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
      board.chatA.append("������ ������ϴ�.\n");
      test.scPane.getVerticalScrollBar().setValue(test.scPane.getVerticalScrollBar().getMaximum());
      validate();
   }

   private void playersInfo() // �÷��̾��� ���� �ο������� ���
   {
      int count = test.model.getSize();
      test.la_PlayerInfo.setText("���� " + count + "������");
   }

   private void nameList(String msg) // �������� ���� �÷��̾� ����Ʈ�� �з��ؼ� ����Ʈ�� ����.
   {
      test.model.removeAllElements();
      StringTokenizer st = new StringTokenizer(msg, "\t");
      while (st.hasMoreElements()) {
        test. model.addElement(st.nextToken());
      }
      playersInfo();
   }

   private void connect() // ������ ����
   {
      try {
         String ip = "localhost";
         test.ta_MsgView.append("������ ������ ��û�մϴ�.\n");
         socket = new Socket(ip, 1112);
         test.ta_MsgView.append("--���� ����--\n");
         reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
         writer = new PrintWriter(socket.getOutputStream(), true);
         new Thread(this).start();
         board.setWriter(writer);
      } catch (Exception e) {
         test.ta_MsgView.append(e + "\n\n���� ����..\n");
      }
   }

   public static void main(String[] args) {
      HalliGalli_Client2 client = new HalliGalli_Client2();

      
   }
}