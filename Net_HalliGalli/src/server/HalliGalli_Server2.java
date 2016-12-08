package server;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.InetAddress;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.Vector;

import client.GameTimer;

import java.util.TimerTask;


public class HalliGalli_Server2{
   /// Field
   ServerSocket server;
   Random rnd = new Random();
   
   long fastTime=0;
   String fastUser = null;
   int Card[] = new int[64]; // ī�� �������� ���� + gold apple + joker
   int TurnCard[][] = new int[4][]; // �������� ī�带 �����ϴ� ����
   int TurnCardCount[] = new int[4]; // �������� ī���� ����
   int CardType[] = new int[4]; // ī���� ������ �˱����� ����
   int CardNum[] = new int[4]; // ī��� ���� ���� �˱����� ����
   int ClientCard[][] = new int[4][]; // Ŭ���̾�Ʈ ī�� ����
   int ClientCardCount[] = new int[4]; // Ŭ���̾�Ʈ ī�� ���� ����
   int NowPlayer; // ���� ���ʰ� �������� ����
   int enterCount=0;
   boolean isSuccess = false; // ��ġ�⿡ �����ߴ��� Ȯ��
   boolean dead[] = new boolean[4]; // �׾����� ��Ҵ��� Ȯ��
   boolean EndGame = false; // ������ ������ Ȯ��.
   boolean isBell = false; // ������ ���� �ƴ��� Ȯ��
   int ComboNum =0; // �޺��� Ƚ��
   String preBellUser;// ���� �������� �̸�
   String Player[] = new String[4]; // �÷��̾��̸� ���Ӽ������ ����
   BManager bMan = new BManager(); // Ŭ���̾�Ʈ���� ������ִ� ��ü
   boolean useBell[] = new boolean[4];
   boolean isAuto = false;
   //item
   
   
   /// Constructor
   public HalliGalli_Server2() {
   }

   /// Method
   public void startServer() {
      try {
         // server = new ServerSocket(7777, 4);
         InetAddress addr = InetAddress.getByName("localhost");
         server = new ServerSocket(1112, 4, addr);
         System.out.println("���������� �����Ǿ����ϴ�.");
         while (true) {
            // Ŭ���̾�Ʈ�� ����� �����带 ��´�.
            Socket socket = server.accept();

            // �����带 ����� �����Ų��.
            HalliGalli_Thread host = new HalliGalli_Thread(socket);
            host.start();

            // bMan�� �����带 �߰��Ѵ�.
            bMan.add(host);

            System.out.println("������ ��: " + bMan.size());
         }
      } catch (Exception e) {
         System.out.println(e);
      }
   }

   public void GameInit() // ���� �ʱ�ȭ
   {
      
      for (int i = 0; i < 4; i++) {
         dead[i] = false;
         TurnCard[i] = new int[64];
         TurnCardCount[i] = 0;
         ClientCard[i] = new int[64];
         ClientCardCount[i] = 0;
         useBell[i] = false;
      }
      	
      
     
      for (int i = 0; i < 64; i++) // ī���ȣ ����
      {
         Card[i] = i;
      }
      
      
      for (int i = 60; i >= 0; i--) // ī�� ����
      {
         int temp;
         int j = rnd.nextInt(64);
         temp = Card[i];
         Card[i] = Card[j];
         Card[j] = temp;
      }
     // if(preBellUser != null)
      //   bMan.sendToAll("[COMBO]|"+ preBellUser + " "+ ComboNum);
      System.out.println("[COMBO]|"+ preBellUser + " "+ ComboNum);
      
      fastTime=0;
      fastUser = null;
      try {
      Thread.sleep(2000);
   } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
   }
      System.out.println("Success gameInit");
   }

   public void DivideCard() // ī�带 Ŭ���̾�Ʈ���� ������
   {
      for (int i = 0; i < 4; i++) {
         for (int j = 0; j < 16; j++) {
            ClientCard[i][j] = Card[i * 16 + j];
            ClientCardCount[i]++;
         }
      }
   }
   
   public void UpdateCardNum() // Ŭ���̾�Ʈ�鿡�� ī�������� ������Ʈ���� �˸�.
   {
      for (int i = 0; i < 4; i++) {
         if (!dead[i]) {
            bMan.sendToAll("[CARDNUM]" + Player[i] + "|" + ClientCardCount[i]);//broadcast
         }
      }
   }

   public void NextPlayer() {
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

   public void SuccessBell() {
      
      for (int i = 0; i < 4; i++) {
         if (!dead[i]) {
            bMan.sendTo(i, "[SUCCESS]" + Player[i]);
         }
      }
   }

   public void FailBell() {
      for (int i = 0; i < 4; i++) {
         if (!dead[i]) {
            bMan.sendTo(i, "[FAIL]" + Player[i]);
         }
      }
   }

   public int isEndGame() // ������ �������� �˻�
   {
      int count = 0;
      
      for (int i = 0; i < 4; i++) {
         if (dead[i]) {
            count++;
         }
      }
      if (count == 3) {//�̱����� ��������.
         for (int i = 0; i < 4; i++) {
            if (!dead[i]) {
               return i;
            }
         }
      }
      return -1;
   }

   public static void main(String[] args) {
      HalliGalli_Server2 server = new HalliGalli_Server2();
      server.startServer();
   }

   class HalliGalli_Thread extends Thread {
      /// Field
      String userName = null; // �̸�
      Socket socket; // ��������
      boolean ready = false; // �غ񿩺�
      BufferedReader reader; // �ޱ�
      PrintWriter writer; // ������

      /// Constructor
      HalliGalli_Thread(Socket socket) {
         this.socket = socket;
      }

      Socket getSocket() {
         return socket;
      }

      String getUserName() {
         return userName;
      }

      boolean isReady() {
         return ready;
      }

      public void run() {
         try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            
            String msg;

            while ((msg = reader.readLine()) != null) {
               if (msg.startsWith("[CONNECT]")) {
                  if (bMan.isFull()) {// already room is full
                     bMan.remove(this);
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
                     System.out.println("������ ��: " + bMan.size());
                     bMan.sendToAll("[DISCONNECT]" + userName);
                  } else { // ����� ������ �Ȱ��
                     userName = msg.substring(9);
                     bMan.sendToOthers(this, "[ENTER]" + userName);
                     bMan.sendToAll(bMan.getNames());
                  }
               }

               else if (msg.startsWith("[READY]")) // Ŭ���̾�Ʈ���� ������ ���
               {
                  ready = true;
                  bMan.sendToAll(userName + "�� �غ�Ϸ�!");
                  
                  if (bMan.isReady()) {// if all ready , start the game
                	  
                     //Thread.sleep(3000);
                     
                     GameInit(); // �����ʱ�ȭ
                     for (int i = 0; i < bMan.size(); i++) // �÷��̾� �̸��� ����
                     {
                        Player[i] = bMan.getHT(i).getUserName();
                     }
                     NowPlayer = 0; // ���ӽ����� 0������
                     bMan.sendToAll(Player[NowPlayer] + "�� �����Դϴ�.");
                     bMan.sendToAll("[SEQUENCE]|"+ Player[0] + " 0|"+ Player[1]+ " 1|"+ Player[2]+" 2|"+ Player[3] + " 3|");// player �鿡�� �ڽ��� index�� �����ش�.
                     DivideCard();
                     UpdateCardNum();
                     bMan.sendToAll("������ �����մϴ�.");
                  }
               }

               else if (msg.startsWith("[NOREADY]")) // Ŭ���̾�Ʈ���� ���� �ٽ� ��������
                                             // ���
               {
                  ready = false;
                  bMan.sendToAll("[READY]"+userName + "���� ���� �����߽��ϴ�.");
               } else if (msg.startsWith("[TURN]")) // Ŭ���̾�Ʈ���� ī������⸦ ���� ���
               {
                  int a = msg.indexOf("|");
                  if (Player[NowPlayer].equals(msg.substring(6))) // �ڱ����ʿ��ΰ˻�
                  {
                     TurnCard[NowPlayer][TurnCardCount[NowPlayer]++] = ClientCard[NowPlayer][--ClientCardCount[NowPlayer]];
                     if (ClientCardCount[NowPlayer] == 0) { // Ŭ���̾�Ʈ�� ī�尡
                                                   // ���嵵 ���� ����
                                                   // ��� ��������
                                                   // ó��
                        dead[NowPlayer] = true;
                        bMan.sendToAll("[UPDATEDEAD]" + Player[NowPlayer]);
                        writer.println("[DEAD]");
                        
                        if (isEndGame() != -1) {
                           bMan.sendToAll(Player[isEndGame()] + "���� �̰���ϴ�.");
                           bMan.sendToAll("[GAMEINIT]");
                           bMan.sendTo(isEndGame(), "[WIN]");
                           bMan.unReady();
                        }
                        
                        NextPlayer();
                        bMan.sendToAll(Player[NowPlayer] + "�� �����Դϴ�.");
                     } else { // �׿ܴ� Ŭ���̾�Ʈ���� ī�� �ٽñ׸� ��û
                        bMan.sendToAll("[REPAINT]" + Player[NowPlayer] + "|"
                              + TurnCard[NowPlayer][TurnCardCount[NowPlayer] - 1]);
                        UpdateCardNum();
                        NextPlayer();
                        bMan.sendToAll(Player[NowPlayer] + "�� �����Դϴ�.");
                     }
                  } else { // �ڱ� ���ʰ� �ƴ� ��� ������ �޽���.
                     writer.println("������ʰ� �ƴմϴ�.");
                  }
               }

               else if (msg.startsWith("[MSG]")) // Ŭ���̾�Ʈ���� �޼����� �޾��� ��
               {
                  bMan.sendToAll("[MSG][" + userName + "]: " + msg.substring(5));
               }

               else if (msg.startsWith("[BELL]")) // Ŭ���̾�Ʈ���� ���� ����� ��
               {
                  if (isBell == true) {
                     writer.println("����� �ʾ����ϴ�.");
                  } else {
                     isBell = true;
                     bMan.sendToAll(userName + "���� ���� �ƽ��ϴ�!!!");
                     bMan.sendToAll("[BELL]");// �ٸ� �÷��̾���� ���� �� ġ�� �Ѵ�.
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
                        	   if(CardType[i] != 4){// banana, lemon,peach,straw

                                   if (CardNum[j] >= 0 && CardNum[j] <= 3) {//4 = minus
                                      CardSum += 1;
                                   } else if (CardNum[j] ==4) {
                                       CardSum -= 1;
                                   } else if (CardNum[j] >= 5 && CardNum[j] <= 7) {
                                       CardSum += 2;
                                    } else if (CardNum[j] ==7) {
                                        CardSum -= 2;
                                    } else if (CardNum[j] >= 8 && CardNum[j] <= 10) {
                                        CardSum += 3;
                                    } else if (CardNum[j]==10) {
                                        CardSum -= 3;
                                    } else if (CardNum[j] >= 11 && CardNum[j] <= 12) {
                                      CardSum += 4;
                                   } else if (CardNum[j] == 13) {
                                      CardSum += 5;
                                   }
                        	   }
                        	   else{// gold apple & jokerCard
                        		   if (CardNum[j] >= 0 && CardNum[j] <= 1) {//0 1 gold 1
                                       CardSum += 1;
                        		   } else if (CardNum[j] >= 2 && CardNum[j] <= 3) {
                                       CardSum += 2;
                        		   } else if (CardNum[j] == 4) {
                                       CardSum += 3;
                                   } else if (CardNum[j] == 5) {
                                       CardSum += 4;
                                   } else if (CardNum[j] == 6) {
                                       CardSum += 5;
                                   }else if (CardNum[j] ==7) {
                                	   CardSum = 5;
                                   }
                                       
                                    
                        	   }
                           }
                        }
                        
                        if (CardSum == 5) { // ��ġ�⼺���� �� ī�带 �� ��������.
                           SuccessBell();
                           String tmpName = userName;
                           if(isAuto == true){
                        	   for(int tmp=0;tmp<4; tmp++){
                        		   if(useBell[tmp] == true){// �̻���� ģ�ŷ� �ٲ�
                        			   System.out.println("ģ��� = "+ userName + " auto �� ��� = "+ Player[tmp]);
                        			   tmpName = userName;
                        			   userName =Player[tmp];
                        		   }
                        	   }
                           }
                           if(preBellUser== null){
                              ComboNum =1;
                              preBellUser = userName;
                           }
                           else if(preBellUser.equals(userName)){
                              ComboNum++;
                           }
                           else{
                              ComboNum=1;
                              preBellUser = userName;
                           }
                           
                           System.out.println("[COMBO]|"+ preBellUser + " "+ ComboNum);
                           bMan.sendToAll("[COMBO]|"+ preBellUser + " "+ ComboNum);
                           isBell = false;
                           bMan.sendToAll(userName + "���� ��ġ�⿡ �����߽��ϴ�.");
                           int a = bMan.getNum(userName);
                           for (i = 0; i < 4; i++) {
                              for (int j = 0; j < TurnCardCount[i]; j++) {
                                 ClientCard[a][ClientCardCount[a]++] = TurnCard[i][j];
                              }
                              TurnCardCount[i] = 0;
                           }
                           
                           Thread.sleep(1000);
                           bMan.sendToAll(Player[NowPlayer] + "�� �����Դϴ�.");
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
                           UpdateCardNum();
                           userName = tmpName;
                           break;
                        }
                     }
                     if (!isSuccess) { // ��ġ�� ���н� �ٸ��÷��̾�� ī�带 ���徿 ������.
                        FailBell();
                        if(preBellUser!= null){
                            if(preBellUser.equals(userName)){
                               preBellUser = null;
                               ComboNum =0;
                            }
                        }
                        bMan.sendToAll("[COMBO]|"+ preBellUser + " "+ ComboNum);

                        isBell = false;
                        bMan.sendToAll(userName + "���� ��ġ�⿡ �����߽��ϴ�.");
                        for (int i = 0; i < 4; i++) {
                           if (!userName.equals(Player[i]) && !dead[i]) {
                              int a = bMan.getNum(userName);
                              ClientCard[i][ClientCardCount[i]++] = ClientCard[a][--ClientCardCount[a]];
                              if (ClientCardCount[a] == 0) {
                                 dead[a] = true;
                                 bMan.sendToAll("[UPDATEDEAD]" + userName);
                                 writer.println("[DEAD]" + userName);
                                 if (userName.equals(Player[NowPlayer])) {
                                    NextPlayer();
                                 }
                                 if (isEndGame() != -1) {
                                    bMan.sendToAll(Player[isEndGame()] + "���� �̰���ϴ�.");
                                    bMan.sendToAll("[GAMEINIT]");
                                    bMan.sendTo(isEndGame(), "[WIN]");
                                    bMan.unReady();
                                 }
                                 break;
                              }
                           }
                        }
                        UpdateCardNum();
                        Thread.sleep(1000);
                        bMan.sendToAll(Player[NowPlayer] + "�� �����Դϴ�.");
                     }
                  }
               }
               else if(msg.startsWith("[ITEM]")){// [item]|
                  msg = msg.substring(7);
                  StringTokenizer st = new StringTokenizer(msg, "|");

                  msg = st.nextToken();
                  if(msg.startsWith("[AUTO]")){
                         msg = st.nextToken();
                         // msg = userName
                         int i=0;
                         for(i=0; i<4; i++){
                        	 if(Player[i] != null){
                              	  if(Player[i].equals(userName)){
                               		  useBell[i] = true;
                               		  isAuto = true;
                               		  break;
                               		          
                               	  }
                        		 
                        	 }
                         }
                         Timer time = new Timer();
                         long delay = 5000;
                         System.out.println("before timer2 and i="+i);
                         time.schedule(new GameTimer2(this,i), delay);
                       
                   }
                   else if(msg.startsWith("[SEETHI]")){
                      msg = st.nextToken();
                      
                   }
                   else if(msg.startsWith("[SECRET]")){
                      msg = st.nextToken();
                      for(int i=0; i<4; i++){
                    	  if(Player[i].equals(userName)){
                    		  bMan.sendToOthers(this,"[REPAINT]" + Player[i] + "|" + 64 );// ���⿡ �̹��� �߰��ؼ� ���ϴ� ī�� ��ȣ�� �����ָ� ��
                    		  //bMan.sendToAll("[REPAINT]" + Player[i] + "|" + GameWindow �� iicard�� ������Ʈ�� �̹�����ȣ);
                    	  }
                      }
                   }
                   else if(msg.startsWith("[CHANGE]")){
                      msg = st.nextToken();
                      
                      for(int i=0; i<4; i++){
                    	  if(Player[i].equals(userName)){
                             
                    		  if(TurnCardCount[i] !=0){

                                  TurnCard[i][TurnCardCount[i]-1] = rnd.nextInt(64);// ����� �ڽ��� ī�带 �ٲپ��ش�.
                                  
                                  bMan.sendToAll("[REPAINT]" + Player[i] + "|"
                                          + TurnCard[i][TurnCardCount[i]-1]);
                                  String tmp = userName + "���� see change �������� ����߽��ϴ�.";
                                  bMan.sendToAll("[MSG]" + tmp);

                    		  }
                    		  break;
                    	  }
                      }
                   }
               }
               
            }
         } catch (Exception e) {
         } finally {
            try {
               bMan.remove(this);
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
               System.out.println("������ ��: " + bMan.size());
               bMan.sendToAll("[DISCONNECT]" + userName);
            } catch (Exception e) {
            }
         }
      }
      public void setBellauto(int i){
    	  int count=0;
    	  useBell[i] = false;
    	  for(int j=0; j<4; j++){
    		  if(useBell[j] == true){
    			  isAuto = true;
    		  }
    		  else{
    			  count++;
    		  }
    	  }
    	  if(count ==0){
    		  isAuto = false;
    	  }
    	  System.out.println("useBell[i] = "+ useBell[i]+ " isAuto = " + isAuto);
      }
   }

   // Ŭ������ �����ϴ� Vector�� ��ӹ޾Ƽ� ��������� �����ϴ� Ŭ����
   class BManager extends Vector {
      public BManager() {
      }

      HalliGalli_Thread getHT(int i) // ���� ��ȣ�� �����带 ����.
      {
         return (HalliGalli_Thread) elementAt(i);
      }

      Socket getSocket(int i) // ������ �����´�.
      {
         return getHT(i).getSocket();
      }

      void sendTo(int i, String msg) // i�������忡 �޽����� ����.
      {
         try {
            PrintWriter pw = new PrintWriter(getSocket(i).getOutputStream(), true);
            pw.println(msg);
         } catch (Exception e) {
         }
      }

      synchronized boolean isFull() // ������ �� á���� Ȯ��
      {
         if (size() >= 5) {
            return true;
         }
         return false;
      }

      void sendToAll(String msg) // ��� �����忡�� ������ �޽���
      {
         for (int i = 0; i < size(); i++) {
              sendTo(i, msg);
         }
      }

      void sendToOthers(HalliGalli_Thread ht, String msg) // �ڱ⸦ ������ �������� ������
                                             // �޽���
      {
         for (int i = 0; i < size(); i++) {
            if (getHT(i) != ht) {
               sendTo(i, msg);
            }
         }
      }

      synchronized void unReady() // �غ�����
      {
         for (int i = 0; i < size(); i++) {
            getHT(i).ready = false;
         }
      }

      synchronized boolean isReady() // �����غ񿩺�Ȯ��
      {
         int count = 0;
         for (int i = 0; i < size(); i++) {
            if (getHT(i).isReady()) {
               count++;
            }
         }
         if (count == 4) {
            return true;
         }
         return false;
      }

      String getNames() // ���� ���ӵ� �������� �̸��� ������.
      {
         StringBuffer sb = new StringBuffer("[PLAYERS]");
         for (int i = 0; i < size(); i++) {
            sb.append(getHT(i).getUserName() + "\t");
         }
         return sb.toString();
      }

      int getNum(String name) // ���� ���ӵ� �������� ��ȣ�� ���ؼ� �̸��� ������.
      {
         for (int i = 0; i < size(); i++) {
            String getName = getHT(i).getUserName();
            if (getName.equals(name)) {
               return i;
            }
         }
         return 0;
      }
   }
}
