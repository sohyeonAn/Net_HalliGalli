package client;

import javax.swing.*;
import javax.swing.border.BevelBorder;

import java.awt.*;

public class Login extends JPanel{

   Image bg;         //�α���â background
   static JTextField idF   ;   //id �Է�â
   static JPasswordField pwdF; // �н����� �Է�â
   JButton btJoin, btLogin; // ȸ������, �α��� ��ư
   ImageIcon joinButton = new ImageIcon("img/joinB.png");
   ImageIcon loginButton = new ImageIcon("img/loginB.png");
   
   public Login()
   {
      //�̹��� �ػ� ���̱�.
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
      
      
      p.setOpaque(false);//JPanel ���� ȸ���� �����ϰ�...
      
      p.add(idF);
      p.add(pwdF);
      p.add(btJoin);
      p.add(btLogin);
      
      add(p);
   }
   
   
   protected void paintComponent(Graphics g)
   {
      //this->JPanel�� ��� �̹��� �Ѹ�.
      g.drawImage(bg,0,0,getWidth(),getHeight(),this);
   }
}