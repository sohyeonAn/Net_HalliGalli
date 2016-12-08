package client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;


public class MakeRoom extends JFrame implements ActionListener{

   JTextField nameF;//���̸� �Է� â
   JButton submit,back;//Ȯ��, ���
   MyPanel2 panel;
   
   public MakeRoom()
   {
      setUndecorated(true);//Ÿ��Ʋ�ٰ� �����.
      setBounds(580,350,420,300);
      setBackground(new Color(0,0,0,0));
      panel = new MyPanel2("img/makeRoom.png");
      setContentPane(panel);
      
      nameF = new JTextField();
      submit = new JButton();
      back = new JButton();
   
        nameF.setFont(new Font("�ü�ü", Font.PLAIN, 18));
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
      {   //�游��� ���
         dispose();
      }
   }   
    
}


class MyPanel2 extends JPanel // ȸ������ JPanelâ
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

