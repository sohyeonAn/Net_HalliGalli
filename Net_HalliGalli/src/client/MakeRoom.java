package client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;


public class MakeRoom extends JFrame implements ActionListener{

   JTextField nameF;//방이름 입력 창
   JButton submit,back;//확인, 취소
   MyPanel2 panel;
   
   public MakeRoom()
   {
      setUndecorated(true);//타이틀바가 사라짐.
      setBounds(580,350,420,300);
      setBackground(new Color(0,0,0,0));
      panel = new MyPanel2("img/makeRoom.png");
      setContentPane(panel);
      
      nameF = new JTextField();
      submit = new JButton();
      back = new JButton();
   
        nameF.setFont(new Font("궁서체", Font.PLAIN, 18));
      nameF.setOpaque(false);
      nameF.setForeground(Color.white);
      
      submit.setBorderPainted(true);
      submit.setContentAreaFilled(false);
      back.setBorderPainted(true);
      back.setContentAreaFilled(false);
   
      nameF.setBounds(128, 100, 235, 70);
      back.setBounds(88,210,78,44);
      submit.setBounds(247,210,78,44);
   
      add(back);
      add(submit);   
      add(nameF);

      setLayout(null);
      
      back.addActionListener(this);
   }
   
   public static void main(String[] args)
   {
      new MakeRoom();
   }
   
   @Override
   public void actionPerformed(ActionEvent e) {
      // TODO Auto-generated method stub
      
      if(e.getSource()==back)
      {   //방만들기 취소
         dispose();
      }
   }   
    
}


class MyPanel2 extends JPanel // 회원가입 JPanel창
{
   Image image;

   MyPanel2(String img) {
      image = Toolkit.getDefaultToolkit().createImage(img);
      setOpaque(false);
   }

   public void paintComponent(Graphics g) {
      super.paintComponent(g);
      if (image != null) {
         g.drawImage(image, 0, 0, 420, 300, this);
      }

      Graphics2D g2d = (Graphics2D) g.create();

      g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.0f));

      g2d.setColor(getBackground());
      g2d.fill(getBounds());
      g2d.dispose();
   }
}

