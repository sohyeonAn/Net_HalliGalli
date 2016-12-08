package client;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.PrintWriter;


public class GameWindow2 extends JPanel{

   PrintWriter writer;
   Image bg;         //로그인창 background
   JButton ready, start, exit,bell;
   JLabel laPlayer[];//user id
   JLabel score[];//user score
   JLabel combo[];
   JTextArea chatA;
   JTextField chatF;
   ImageIcon p1,p2,p3,p4;//user profile
   ImageIcon pp1,pp2,pp3,pp4;//이미지 사이즈 조절용
   ImageIcon tmpSeeF, seeF;//see through아이템용 이미지 필드 tmp는 이미지 크기 조절 때문에 필요
   JScrollPane scroll;
   
   JLabel info;
   ImageIcon iiCard[];        // 총카드56개 
   ImageIcon iiCardBack;     // 카드뒷면   
   ImageIcon iiPlayerCard[];  // 각플레이어의 카드 내기 직전모양
   ImageIcon iiBell;
   
   JLabel laCardNum[];
   String userid[];        // 사용자 id   
   String roomName;
   
   JButton changeI, seethI, autoI, secretI;
   ImageIcon change = new ImageIcon("img/change.png");
   ImageIcon seeth= new ImageIcon("img/seethrough.png");
   ImageIcon auto= new ImageIcon("img/auto.png");
   ImageIcon secret= new ImageIcon("img/secret.png");
   
   public GameWindow2(String roomName)
   {
     this.roomName = roomName;
      userid = new String[4];  // 사용자이름
      laPlayer = new JLabel[4];
      info = new JLabel();
      bg = Toolkit.getDefaultToolkit().getImage("img/gameRoom.png");
      
      iiCardBack = new ImageIcon("cardimg/CardBack.jpg"); 
      
      iiPlayerCard = new ImageIcon[4]; //  사용자 내기직전모양? 카드뒷면
      iiCard = new ImageIcon[65];     // 총카드 이미지
      laCardNum = new JLabel[4];
          
      score = new JLabel[4];
      combo = new JLabel[4];
      ready = new JButton("ready");
      start = new JButton("start");
      exit = new JButton("exit");
      bell = new JButton("bell");
      
      JPanel p = new JPanel();
      p.setBounds(0, 0, 1000, 800);
      p.setLayout(null);
      
      
      //아이템 버튼
      seethI = new JButton(seeth);
      secretI = new JButton(secret);
      autoI = new JButton(auto);
      changeI = new JButton(change);
      
      autoI.setBounds(430,230,50,50);
      changeI.setBounds(530,230,50,50);
      seethI.setBounds(430,320,50,50);
      secretI.setBounds(530,320,50,50);
      
      seethI.setBorderPainted(false);
      seethI.setContentAreaFilled(false);
      changeI.setBorderPainted(false);
      changeI.setContentAreaFilled(false);
      secretI.setBorderPainted(false);
      secretI.setContentAreaFilled(false);
      autoI.setBorderPainted(false);
      autoI.setContentAreaFilled(false);
      
      p.setOpaque(false);
      add(p);
      
      p.add(changeI);p.add(seethI);
      p.add(secretI);p.add(autoI);
      
      p1 = new ImageIcon("img/profile.png");
      Image tmp = p1.getImage();
      Image tmp2 = tmp.getScaledInstance(157, 216, java.awt.Image.SCALE_DEFAULT);
      pp1 = new ImageIcon(tmp2);
      
      p2 = new ImageIcon("img/profile.png");
      tmp = p2.getImage();
      tmp2 = tmp.getScaledInstance(157, 216, java.awt.Image.SCALE_DEFAULT);
      pp2 = new ImageIcon(tmp2);
      
      p3 = new ImageIcon("img/profile.png");
      tmp = p3.getImage();
      tmp2 = tmp.getScaledInstance(157, 216, java.awt.Image.SCALE_DEFAULT);
      pp3 = new ImageIcon(tmp2);
      
      p4 = new ImageIcon("img/profile.png");
      tmp = p1.getImage();
      tmp2 = tmp.getScaledInstance(157, 216, java.awt.Image.SCALE_DEFAULT);
      pp4 = new ImageIcon(tmp2);
      
      LineBorder border = new LineBorder(Color.RED);//테두리색설정.위치선정때문에.
      
      chatA = new JTextArea();
      chatF = new JTextField();
      scroll = new JScrollPane(chatA);
      
      chatA.setBounds(280,543,350,148);
      chatA.setOpaque(false);
      chatA.setBorder(null);
      
      chatF.setBounds(280,700,350,32);
      chatF.setOpaque(false);
      chatF.setBorder(null);
    
      scroll.setBorder(null);
      scroll.setBounds(280,543,350,148);
      scroll.setViewportView(chatA);
      
      //게임의 상태 알려줌.
      info.setBounds(297,32,400,38);
      info.setHorizontalAlignment(SwingConstants.CENTER);
      info.setForeground(Color.white);
      
      
      //display player's id
      laPlayer[0] = new JLabel("Player1");
      laPlayer[0].setBounds(80,268,100,35);
      laPlayer[0].setHorizontalAlignment(SwingConstants.CENTER);
      
      laPlayer[2] = new JLabel("Player3");
      laPlayer[2].setBounds(80,628,100,35);
      laPlayer[2].setHorizontalAlignment(SwingConstants.CENTER);

      laPlayer[1] = new JLabel("Player2");
      laPlayer[1].setBounds(813,268,100,35);
      laPlayer[1].setHorizontalAlignment(SwingConstants.CENTER);

      laPlayer[3] = new JLabel("Player4");
      laPlayer[3].setBounds(813,628,100,35);
      laPlayer[3].setHorizontalAlignment(SwingConstants.CENTER);

      
      //display score
      score[0] = new JLabel("score 1");
      score[0].setBounds(80,315,100,35);
      score[0].setHorizontalAlignment(SwingConstants.CENTER);

      score[1] = new JLabel("score 2");
      score[1].setBounds(813,315,100,35);
      score[1].setHorizontalAlignment(SwingConstants.CENTER);

      score[2] = new JLabel("score 3");
      score[2].setBounds(80,674,100,35);
      score[2].setHorizontalAlignment(SwingConstants.CENTER);
            
      score[3] = new JLabel("score 4");
      score[3].setBounds(813,674,100,35);
      score[3].setHorizontalAlignment(SwingConstants.CENTER);
      
      //display combo
      for(int i=0;i<4;i++)
      {
         combo[i] = new JLabel();
         combo[i].setHorizontalAlignment(SwingConstants.CENTER);
          combo[i].setFont(new Font("Arial", Font.BOLD, 20));
          combo[i].setOpaque(false);
          combo[i].setForeground(Color.red);//글자색
      }
     
      combo[0].setBounds(315,252,100,50);
      combo[1].setBounds(590,252,100,50);
      combo[2].setBounds(315,310,100,50);
      combo[3].setBounds(590,310,100,50);

      
      //basic button(start, ready, exit)
      start.setBounds(640,545,76,42);
      start.setBorderPainted(false);
      start.setContentAreaFilled(false);
      
      ready.setBounds(640,620,76,42);
      ready.setBorderPainted(false);
      ready.setContentAreaFilled(false);
      
      exit.setBounds(640,695,76,42);
      exit.setBorderPainted(false);
      exit.setContentAreaFilled(false);
      
      bell.setBounds(476,271,60,60);
      bell.setBorderPainted(false);
      bell.setContentAreaFilled(false);
      
      setLayout(null);
      
    //see through 아이템 사용시 이미지.
    tmpSeeF = new ImageIcon("img/BLS.jpg");
     Image tmp3 = tmpSeeF.getImage();
     Image tmp4 = tmp.getScaledInstance(50, 70, java.awt.Image.SCALE_DEFAULT);
     seeF = new ImageIcon(tmp2);
      
      
      //어레이에 이미지 입히기
      for (int i = 0; i < 4; i++) // 1개짜리 카드 이미지설정
       {
            iiCard[14 * 0 + i] = new ImageIcon("cardimg/banana_1.jpg");
            iiCard[14 * 1 + i] = new ImageIcon("cardimg/lemon_1.jpg");
            iiCard[14 * 2 + i] = new ImageIcon("cardimg/peach_1.jpg");
            iiCard[14 * 3 + i] = new ImageIcon("cardimg/straw_1.jpg");
       }
      	iiCard[14 * 0+4] = new ImageIcon("cardimg/Banana_1_minus.jpg");
      	iiCard[14 * 1+4] = new ImageIcon("cardimg/Lemon_1_minus.jpg");
      	iiCard[14 * 2+4] = new ImageIcon("cardimg/Peach_1_minus.jpg");
      	iiCard[14 * 3+4] = new ImageIcon("cardimg/Peach_1_minus.jpg");
      	
       for (int i = 0; i < 2; i++) // 2,3개짜리 카드 이미지설정
       {
           iiCard[14 * 0 + i + 5] = new ImageIcon("cardimg/banana_2.jpg");//5 6 
            iiCard[14 * 1 + i + 5] = new ImageIcon("cardimg/lemon_2.jpg");
            iiCard[14 * 2 + i + 5] = new ImageIcon("cardimg/peach_2.jpg");
            iiCard[14 * 3 + i + 5] = new ImageIcon("cardimg/straw_2.jpg");
            
            iiCard[14 * 0 + i + 8] = new ImageIcon("cardimg/banana_3.jpg");//8 9 10
            iiCard[14 * 1 + i + 8] = new ImageIcon("cardimg/lemon_3.jpg");
            iiCard[14 * 2 + i + 8] = new ImageIcon("cardimg/peach_3.jpg");
            iiCard[14 * 3 + i + 8] = new ImageIcon("cardimg/straw_3.jpg");
       }
       
       iiCard[14 * 0+7] = new ImageIcon("cardimg/Banana_2_minus.jpg");
       iiCard[14 * 1+7] = new ImageIcon("cardimg/Lemon_2_minus.jpg");
       iiCard[14 * 2+7] = new ImageIcon("cardimg/Peach_2_minus.jpg");
       iiCard[14 * 3+7] = new ImageIcon("cardimg/Straw_2_minus.jpg");
       
       iiCard[14 * 0+10] = new ImageIcon("cardimg/Banana_3_minus.jpg");
       iiCard[14 * 1+10] = new ImageIcon("cardimg/Lemon_3_minus.jpg");
       iiCard[14 * 2+10] = new ImageIcon("cardimg/Peach_3_minus.jpg");
       iiCard[14 * 3+10] = new ImageIcon("cardimg/Straw_3_minus.jpg");
       
       for (int i = 0; i < 2; i++) // 4개짜리 카드 이미지설정
       {
            iiCard[14 * 0 + i + 11] = new ImageIcon("cardimg/banana_4.jpg");
            iiCard[14 * 1 + i + 11] = new ImageIcon("cardimg/lemon_4.jpg");
            iiCard[14 * 2 + i + 11] = new ImageIcon("cardimg/peach_4.jpg");
            iiCard[14 * 3 + i + 11] = new ImageIcon("cardimg/straw_4.jpg");
       }
         // 5개짜리 카드 이미지설정
       iiCard[14 * 0 + 13] = new ImageIcon("cardimg/banana_5.jpg");
       iiCard[14 * 1 + 13] = new ImageIcon("cardimg/lemon_5.jpg");
       iiCard[14 * 2 + 13] = new ImageIcon("cardimg/peach_5.jpg");
       iiCard[14 * 3 + 13] = new ImageIcon("cardimg/straw_5.jpg");
       
       
       //goldApple
       
       iiCard[56] = new ImageIcon("cardimg/goldapple1.jpg");
       iiCard[57] = new ImageIcon("cardimg/goldapple1.jpg");

       iiCard[58] = new ImageIcon("cardimg/goldapple2.jpg");
       iiCard[59] = new ImageIcon("cardimg/goldapple2.jpg");

       iiCard[60] = new ImageIcon("cardimg/goldapple3.jpg");
       iiCard[61] = new ImageIcon("cardimg/goldapple4.jpg");
       iiCard[62] = new ImageIcon("cardimg/goldapple5.jpg");

       //joker
       iiCard[63] = new ImageIcon("img/Joker.jpg");
       //secret
       iiCard[64] = new ImageIcon("img/secretCard.jpg");
       
       //패널 위에 올리기
      add(laPlayer[0]);add(laPlayer[1]);add(laPlayer[2]);add(laPlayer[3]);
      add(score[0]);add(score[1]);add(score[2]);add(score[3]);
      add(combo[0]);add(combo[1]);add(combo[2]);add(combo[3]);
      add(chatF);add(scroll);add(info);
      add(ready);add(start);add(exit);
      add(bell);
      
      setOpaque(false);
      
      for (int i = 0; i < 4; i++) {
            iiPlayerCard[i] = iiCardBack;      //사용자 각 카드->카드뒷면으로 지정
         }
      
      
      //사용자의 남은 카드 개수 보여주기
      laCardNum[0] = new JLabel("0장");
       laCardNum[0].setBounds(315, 135, 70, 15);
       add(laCardNum[0]);
       
       laCardNum[1] = new JLabel("0장");
       laCardNum[1].setBounds(663, 135, 70, 15);
       add(laCardNum[1]);
       
       laCardNum[2] = new JLabel("0장");
       laCardNum[2].setBounds(315, 457, 70, 15);
       add(laCardNum[2]);
       
       laCardNum[3] = new JLabel("0장");
       laCardNum[3].setBounds(663, 457, 70, 15);
       add(laCardNum[3]);
   }
   
    public void setWriter(PrintWriter writer) {
         this.writer = writer;
      }

      public void setLabel(DefaultListModel model) {
         for (int i = 0; i < 4; i++) {
            userid[i] = (String) model.get(i);
            laPlayer[i].setText(userid[i]);
         }
      }

      public void UpdateDraw(String name, int CardNum) // 그리기
      {
         for (int i = 0; i < 4; i++) {
            if (name.equals(userid[i])) {
               iiPlayerCard[i] = iiCard[CardNum];
               repaint();
            }
         }
      }

      public void UpdateCardNum(String name, int Count) // 카드를 냈을경우 업데이트
      {
         for (int i = 0; i < 4; i++) {
            if (name.equals(userid[i])) {
               laCardNum[i].setText(Count + "장");
            }
         }
      }

      public void UpdateDead(String name) // 죽은 플레이어정보 업데이트
      {
         for (int i = 0; i < 4; i++) {
            if (name.equals(userid[i])) {
               iiPlayerCard[i] = iiCardBack;
               laCardNum[i].setText("GameOver");
               repaint();
            }
         }
      }

      public void CardInit() // 카드를 초기화
      {
         for (int i = 0; i < 4; i++) {
            iiPlayerCard[i] = iiCardBack;
         }
         repaint();
      }

   protected void paintComponent(Graphics g)
   {
      //this->JPanel에 배경 이미지 뿌림.
      g.drawImage(bg,0,0,getWidth(),getHeight(),this);
      pp1.paintIcon(this, g, 52, 41);//뒤집어진 카드 이미지
      pp2.paintIcon(this, g, 789, 41);
      pp3.paintIcon(this, g, 53, 400);
      pp4.paintIcon(this, g, 789, 400);
      
       iiPlayerCard[0].paintIcon(this, g, 368, 98);   //왼쪽카드(이곳에, ,시작x좌표,시작y좌표)
       iiPlayerCard[1].paintIcon(this, g, 540, 98);
       iiPlayerCard[2].paintIcon(this, g, 368, 363);
       iiPlayerCard[3].paintIcon(this, g, 540, 363);
   }
}