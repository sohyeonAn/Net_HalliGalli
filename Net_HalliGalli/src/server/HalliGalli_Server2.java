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
   int Card[] = new int[64]; // 카드 섞기위한 변수 + gold apple + joker
   int TurnCard[][] = new int[4][]; // 뒤집어진 카드를 저장하는 변수
   int TurnCardCount[] = new int[4]; // 뒤집어진 카드의 개수
   int CardType[] = new int[4]; // 카드의 종류를 알기위한 변수
   int CardNum[] = new int[4]; // 카드속 과일 개수 알기위한 변수
   int ClientCard[][] = new int[4][]; // 클라이언트 카드 변수
   int ClientCardCount[] = new int[4]; // 클라이언트 카드 개수 변수
   int NowPlayer; // 현재 차례가 누구인지 저장
   int enterCount=0;
   boolean isSuccess = false; // 종치기에 성공했는지 확인
   boolean dead[] = new boolean[4]; // 죽었는지 살았는지 확인
   boolean EndGame = false; // 게임이 끝인지 확인.
   boolean isBell = false; // 상대방이 종을 쳤는지 확인
   int ComboNum =0; // 콤보의 횟수
   String preBellUser;// 지난 정답자의 이름
   String Player[] = new String[4]; // 플레이어이름 접속순서대로 저장
   BManager bMan = new BManager(); // 클라이언트에게 방송해주는 객체
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
         System.out.println("서버소켓이 생성되었습니다.");
         while (true) {
            // 클라이언트와 연결된 스레드를 얻는다.
            Socket socket = server.accept();

            // 스레드를 만들고 실행시킨다.
            HalliGalli_Thread host = new HalliGalli_Thread(socket);
            host.start();

            // bMan에 스레드를 추가한다.
            bMan.add(host);

            System.out.println("접속자 수: " + bMan.size());
         }
      } catch (Exception e) {
         System.out.println(e);
      }
   }

   public void GameInit() // 게임 초기화
   {
      
      for (int i = 0; i < 4; i++) {
         dead[i] = false;
         TurnCard[i] = new int[64];
         TurnCardCount[i] = 0;
         ClientCard[i] = new int[64];
         ClientCardCount[i] = 0;
         useBell[i] = false;
      }
      	
      
     
      for (int i = 0; i < 64; i++) // 카드번호 삽입
      {
         Card[i] = i;
      }
      
      
      for (int i = 60; i >= 0; i--) // 카드 섞기
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

   public void DivideCard() // 카드를 클라이언트에게 나눠줌
   {
      for (int i = 0; i < 4; i++) {
         for (int j = 0; j < 16; j++) {
            ClientCard[i][j] = Card[i * 16 + j];
            ClientCardCount[i]++;
         }
      }
   }
   
   public void UpdateCardNum() // 클라이언트들에게 카드정보가 업데이트됨을 알림.
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

   public int isEndGame() // 게임이 끝인지를 검사
   {
      int count = 0;
      
      for (int i = 0; i < 4; i++) {
         if (dead[i]) {
            count++;
         }
      }
      if (count == 3) {//이긴사람이 누구인지.
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
      String userName = null; // 이름
      Socket socket; // 서버소켓
      boolean ready = false; // 준비여부
      BufferedReader reader; // 받기
      PrintWriter writer; // 보내기

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
                     System.out.println("이미 방이 가득 찼습니다.");
                     System.out.println("접속자 수: " + bMan.size());
                     bMan.sendToAll("[DISCONNECT]" + userName);
                  } else { // 제대로 접속이 된경우
                     userName = msg.substring(9);
                     bMan.sendToOthers(this, "[ENTER]" + userName);
                     bMan.sendToAll(bMan.getNames());
                  }
               }

               else if (msg.startsWith("[READY]")) // 클라이언트에서 레디한 경우
               {
                  ready = true;
                  bMan.sendToAll(userName + "님 준비완료!");
                  
                  if (bMan.isReady()) {// if all ready , start the game
                	  
                     //Thread.sleep(3000);
                     
                     GameInit(); // 게임초기화
                     for (int i = 0; i < bMan.size(); i++) // 플레이어 이름을 저장
                     {
                        Player[i] = bMan.getHT(i).getUserName();
                     }
                     NowPlayer = 0; // 게임시작은 0번부터
                     bMan.sendToAll(Player[NowPlayer] + "님 차례입니다.");
                     bMan.sendToAll("[SEQUENCE]|"+ Player[0] + " 0|"+ Player[1]+ " 1|"+ Player[2]+" 2|"+ Player[3] + " 3|");// player 들에게 자신의 index를 보내준다.
                     DivideCard();
                     UpdateCardNum();
                     bMan.sendToAll("게임을 시작합니다.");
                  }
               }

               else if (msg.startsWith("[NOREADY]")) // 클라이언트에서 레디를 다시 해제했을
                                             // 경우
               {
                  ready = false;
                  bMan.sendToAll("[READY]"+userName + "님이 레디를 해제했습니다.");
               } else if (msg.startsWith("[TURN]")) // 클라이언트에서 카드뒤집기를 했을 경우
               {
                  int a = msg.indexOf("|");
                  if (Player[NowPlayer].equals(msg.substring(6))) // 자기차례여부검사
                  {
                     TurnCard[NowPlayer][TurnCardCount[NowPlayer]++] = ClientCard[NowPlayer][--ClientCardCount[NowPlayer]];
                     if (ClientCardCount[NowPlayer] == 0) { // 클라이언트가 카드가
                                                   // 한장도 남지 않은
                                                   // 경우 죽음으로
                                                   // 처리
                        dead[NowPlayer] = true;
                        bMan.sendToAll("[UPDATEDEAD]" + Player[NowPlayer]);
                        writer.println("[DEAD]");
                        
                        if (isEndGame() != -1) {
                           bMan.sendToAll(Player[isEndGame()] + "님이 이겼습니다.");
                           bMan.sendToAll("[GAMEINIT]");
                           bMan.sendTo(isEndGame(), "[WIN]");
                           bMan.unReady();
                        }
                        
                        NextPlayer();
                        bMan.sendToAll(Player[NowPlayer] + "님 차례입니다.");
                     } else { // 그외는 클라이언트에게 카드 다시그림 요청
                        bMan.sendToAll("[REPAINT]" + Player[NowPlayer] + "|"
                              + TurnCard[NowPlayer][TurnCardCount[NowPlayer] - 1]);
                        UpdateCardNum();
                        NextPlayer();
                        bMan.sendToAll(Player[NowPlayer] + "님 차례입니다.");
                     }
                  } else { // 자기 차례가 아닐 경우 보내는 메시지.
                     writer.println("당신차례가 아닙니다.");
                  }
               }

               else if (msg.startsWith("[MSG]")) // 클라이언트에서 메세지를 받았을 때
               {
                  bMan.sendToAll("[MSG][" + userName + "]: " + msg.substring(5));
               }

               else if (msg.startsWith("[BELL]")) // 클라이언트에서 벨을 울렸을 때
               {
                  if (isBell == true) {
                     writer.println("당신이 늦었습니다.");
                  } else {
                     isBell = true;
                     bMan.sendToAll(userName + "님이 종을 쳤습니다!!!");
                     bMan.sendToAll("[BELL]");// 다른 플레이어들이 종을 못 치게 한다.
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
                        
                        if (CardSum == 5) { // 종치기성공시 깔린 카드를 다 가져간다.
                           SuccessBell();
                           String tmpName = userName;
                           if(isAuto == true){
                        	   for(int tmp=0;tmp<4; tmp++){
                        		   if(useBell[tmp] == true){// 이사람이 친거로 바꿈
                        			   System.out.println("친사람 = "+ userName + " auto 쓴 사람 = "+ Player[tmp]);
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
                           bMan.sendToAll(userName + "님이 종치기에 성공했습니다.");
                           int a = bMan.getNum(userName);
                           for (i = 0; i < 4; i++) {
                              for (int j = 0; j < TurnCardCount[i]; j++) {
                                 ClientCard[a][ClientCardCount[a]++] = TurnCard[i][j];
                              }
                              TurnCardCount[i] = 0;
                           }
                           
                           Thread.sleep(1000);
                           bMan.sendToAll(Player[NowPlayer] + "님 차례입니다.");
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
                           UpdateCardNum();
                           userName = tmpName;
                           break;
                        }
                     }
                     if (!isSuccess) { // 종치기 실패시 다른플레이어에게 카드를 한장씩 돌린다.
                        FailBell();
                        if(preBellUser!= null){
                            if(preBellUser.equals(userName)){
                               preBellUser = null;
                               ComboNum =0;
                            }
                        }
                        bMan.sendToAll("[COMBO]|"+ preBellUser + " "+ ComboNum);

                        isBell = false;
                        bMan.sendToAll(userName + "님이 종치기에 실패했습니다.");
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
                                    bMan.sendToAll(Player[isEndGame()] + "님이 이겼습니다.");
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
                        bMan.sendToAll(Player[NowPlayer] + "님 차례입니다.");
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
                    		  bMan.sendToOthers(this,"[REPAINT]" + Player[i] + "|" + 64 );// 여기에 이미지 추가해서 원하는 카드 번호로 보내주면 됨
                    		  //bMan.sendToAll("[REPAINT]" + Player[i] + "|" + GameWindow 에 iicard에 없데이트된 이미지번호);
                    	  }
                      }
                   }
                   else if(msg.startsWith("[CHANGE]")){
                      msg = st.nextToken();
                      
                      for(int i=0; i<4; i++){
                    	  if(Player[i].equals(userName)){
                             
                    		  if(TurnCardCount[i] !=0){

                                  TurnCard[i][TurnCardCount[i]-1] = rnd.nextInt(64);// 사용한 자신의 카드를 바꾸어준다.
                                  
                                  bMan.sendToAll("[REPAINT]" + Player[i] + "|"
                                          + TurnCard[i][TurnCardCount[i]-1]);
                                  String tmp = userName + "님이 see change 아이템을 사용했습니다.";
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
               System.out.println(userName + "님이 접속을 끊었습니다.");
               System.out.println("접속자 수: " + bMan.size());
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

   // 클래스를 저장하는 Vector를 상속받아서 스레드들을 저장하는 클래스
   class BManager extends Vector {
      public BManager() {
      }

      HalliGalli_Thread getHT(int i) // 현재 번호의 스레드를 저장.
      {
         return (HalliGalli_Thread) elementAt(i);
      }

      Socket getSocket(int i) // 소켓을 가져온다.
      {
         return getHT(i).getSocket();
      }

      void sendTo(int i, String msg) // i번스레드에 메시지를 전달.
      {
         try {
            PrintWriter pw = new PrintWriter(getSocket(i).getOutputStream(), true);
            pw.println(msg);
         } catch (Exception e) {
         }
      }

      synchronized boolean isFull() // 서버가 다 찼는지 확인
      {
         if (size() >= 5) {
            return true;
         }
         return false;
      }

      void sendToAll(String msg) // 모든 스레드에게 보내는 메시지
      {
         for (int i = 0; i < size(); i++) {
              sendTo(i, msg);
         }
      }

      void sendToOthers(HalliGalli_Thread ht, String msg) // 자기를 제외한 유저에게 보내는
                                             // 메시지
      {
         for (int i = 0; i < size(); i++) {
            if (getHT(i) != ht) {
               sendTo(i, msg);
            }
         }
      }

      synchronized void unReady() // 준비해제
      {
         for (int i = 0; i < size(); i++) {
            getHT(i).ready = false;
         }
      }

      synchronized boolean isReady() // 전부준비여부확인
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

      String getNames() // 현재 접속된 스레드의 이름을 가져옴.
      {
         StringBuffer sb = new StringBuffer("[PLAYERS]");
         for (int i = 0; i < size(); i++) {
            sb.append(getHT(i).getUserName() + "\t");
         }
         return sb.toString();
      }

      int getNum(String name) // 현재 접속된 스레드의 번호를 통해서 이름을 가져옴.
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
