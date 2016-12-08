package client;

import java.awt.BorderLayout;

import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.List;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Random;
import java.util.StringTokenizer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;

import client.ClientStart;

class WaitRoom extends JPanel {

   ImageIcon bg;
   List RoomList;
   List ClientList;// 1:방리스트 2: 사람리스트
   JTextArea chatA; // 채팅 창
   JTextField chatF; // 채팅 입력창
   JButton help, make;// 도움말, 방만들기
   JButton profile, rank;
   JScrollPane scroll;

   String id;
   JLabel idF;
   ImageIcon tmpPro, pro;

   public WaitRoom() {

      bg = new ImageIcon("img/wait.jpg");// background.
      setLayout(null);
      LineBorder border = new LineBorder(Color.RED);//테두리색설정.위치선정때문에.
      
      help = new JButton();
      make = new JButton();
      profile = new JButton();
      rank = new JButton();

      chatA = new JTextArea();
      chatF = new JTextField();
      scroll = new JScrollPane(chatA);
      
      chatA.setBounds(70, 520, 345, 148);
      chatA.setOpaque(false);
      chatA.setBorder(null);

      chatF.setBounds(70, 675, 408, 32);
      chatF.setOpaque(false);
      chatF.setBorder(null);

      scroll.setBorder(null);
      scroll.setBounds(70, 528, 401, 132);
      scroll.setViewportView(chatA);

      make.setBounds(497, 585, 116, 44);
      make.setBorderPainted(true);
      make.setContentAreaFilled(false);

      help.setBounds(497, 638, 116, 44);
      help.setBorderPainted(true);
      help.setContentAreaFilled(false);

      profile.setBounds(713, 642, 80, 44);
      profile.setBorderPainted(true);
      profile.setContentAreaFilled(false);

      rank.setBounds(820, 642, 80, 44);
      rank.setBorderPainted(true);
      rank.setContentAreaFilled(false);

      RoomList = new List();
      ClientList = new List();
      RoomList.setBounds(83, 70, 530, 420);
      ClientList.setBounds(681, 65, 249, 180);

      idF = new JLabel();//개인정보창
      idF.setBounds(733, 592,150, 30);
      idF.setHorizontalAlignment(SwingConstants.CENTER);//가운데정렬
       idF.setBorder(border);//위치확인용
       idF.setFont(new Font("맑은고딕", Font.BOLD, 20));
       idF.setForeground(Color.white);
       
      setOpaque(false);
      add(RoomList);
      add(ClientList);
      add(chatF);add(idF);
      add(scroll);
      add(help);
      add(make);
      add(rank);
      add(profile);
      

   }

   public void readMsg(int msg, StringTokenizer st) throws IOException {

      switch (msg) {

      case Protocol.WAITCLIENTNUM: {
         ClientList.removeAll();

         while (st.hasMoreElements())
            ClientList.add(st.nextToken());

      }
         break;

      case Protocol.ROOMLIST: {
         RoomList.removeAll();

         while (st.hasMoreElements())
            RoomList.add(st.nextToken());
      }
         break;

      }
   }

   protected void paintComponent(Graphics g) {
      g.drawImage(bg.getImage(), 0, 0, getWidth(), getHeight(), this);
      pro.paintIcon(this, g, 705, 330);// 사용자 프로필)

   }

   public void setProfile(String name) {
      // TODO Auto-generated method stub
       tmpPro = new ImageIcon("c:/profile/" + name + ".jpg");
      
       Image tmp = tmpPro.getImage();
       Image tmp2 = tmp.getScaledInstance(200, 235, java.awt.Image.SCALE_DEFAULT);
       
       pro = new ImageIcon(tmp2);
       repaint();
   }

   

}