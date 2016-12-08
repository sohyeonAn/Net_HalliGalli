package client;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Label;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;

public class test extends JPanel {
	JTextArea ta_MsgView; // �޽����� �����ִ� �ؽ�Ʈ����
	   JScrollPane scPane; // ��ũ��
	   JTextField tf_Send = new JTextField(""); // ���� �޽����� �����ʵ�
	   JTextField tf_Name = new JTextField(); // ����� �̸� ����
	   JTextField tf_ipaddress = new JTextField(); // ������ ������ ip�ּ��Է��ʵ�
	   DefaultListModel model = new DefaultListModel(); // ����Ʈ��
	   JList li_Player = new JList(model); // ����� ����Ʈ
	   JButton connectButton = new JButton("����"); // ���� ��ư
	   JButton readyButton = new JButton("�غ�"); // �����ư
	   JButton turnButton = new JButton("ī�������"); // ī�� ������ ��ư
	   JButton bellButton = new JButton("��ġ��"); // ��ġ�� ��ư
	   JLabel la_GameInfo = new JLabel("<����â>"); // ����â
	   JLabel la_PlayerInfo = new JLabel("<�ο�����>"); // �ο�����
	   boolean ready = false;
	   
	   public test()
	   {
		   setLayout(null);
		   ta_MsgView = new JTextArea(1, 1);
		      scPane = new JScrollPane(ta_MsgView);
		     
		      // ���� ������Ʈ�� �����ϰ� ��ġ�Ѵ�.
		      EtchedBorder eborder = new EtchedBorder(EtchedBorder.RAISED);
		      LineBorder border = new LineBorder(Color.RED);//�׵θ�������.��ġ����������.
		      
		      ta_MsgView.setEditable(false);
		      la_GameInfo.setBounds(10, 30, 480, 30);
		      add(la_GameInfo);

		      JPanel p1 = new JPanel();
		      p1.setLayout(new GridLayout(4, 4));
		      p1.add(new Label("�����ּ� : ", 2));
		      p1.add(tf_ipaddress);
		      p1.add(new Label("�̸� : ", 2));
		      p1.add(tf_Name);
		      p1.add(connectButton);
		      p1.add(readyButton);
		      readyButton.setEnabled(false);
		      p1.setBounds(500, 30, 250, 70);

		      JPanel p2 = new JPanel();
		      p2.setLayout(new BorderLayout());
		      JPanel p2_1 = new JPanel();
		      p2_1.add(turnButton);
		      p2_1.add(bellButton);
		      p2.add(la_PlayerInfo, "North");
		      p2.add(li_Player, "Center");
		      p2.add(p2_1, "South");
		      turnButton.setEnabled(false);
		      bellButton.setEnabled(false);
		      p2.setBounds(500, 110, 250, 180);
		      p2.setBorder(border);

		      
		      JPanel p3 = new JPanel();
		      p3.setLayout(new BorderLayout());
		      p3.add(scPane, "Center");
		      p3.add(tf_Send, "South");
		      p3.setBounds(500, 300, 250, 250);
		      p3.setBorder(eborder);

		      add(p1);
		      add(p2);
		      add(p3);
		   
	   }

}
