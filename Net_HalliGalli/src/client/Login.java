package client;

import javax.swing.*;
import javax.swing.border.BevelBorder;

import java.awt.*;

public class Login extends JPanel{

   Image bg;         //로그인창 background
   static JTextField idF   ;   //id 입력창
   static JPasswordField pwdF; // 패스워드 입력창
   JButton btJoin, btLogin; // 회원가입, 로그인 버튼
   ImageIcon joinButton = new ImageIcon("img/joinB.png");
   ImageIcon loginButton = new ImageIcon("img/loginB.png");
   
   public Login()
   {
      //이미지 해상도 높이기.
      ImageIcon back = new ImageIcon("img/login2.png");
      bg  = back.getImage().getScaledInstance(1000,800, Image.SCALE_FAST);
      
      idF = new JTextField();
      pwdF = new JPasswordField();
      btJoin = new JButton(joinButton);
      btLogin = new JButton(loginButton);
      
      setLayout(null);
      
      JPanel p = new JPanel();
      p.setBounds(0, 0, 1000, 800);
      p.setLayout(null);
      
      idF.setBounds(390, 487, 290, 45);
      pwdF.setBounds(390,562, 290,45);

      idF.setOpaque(false);
      pwdF.setOpaque(false);
      btJoin.setBounds(350,623,95,60);
      btLogin.setBounds(580,623,95,60);
      
      btJoin.setBorderPainted(false);
      btJoin.setContentAreaFilled(false);
      btLogin.setBorderPainted(false);
      btLogin.setContentAreaFilled(false);
      
      
      p.setOpaque(false);//JPanel 묶은 회색을 투명하게...
      
      p.add(idF);
      p.add(pwdF);
      p.add(btJoin);
      p.add(btLogin);
      
      add(p);
   }
   
   
   protected void paintComponent(Graphics g)
   {
      //this->JPanel에 배경 이미지 뿌림.
      g.drawImage(bg,0,0,getWidth(),getHeight(),this);
   }
}