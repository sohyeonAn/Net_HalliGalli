package client;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Label;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;

public class test extends JPanel {
	JTextArea ta_MsgView; // 메시지를 보여주는 텍스트영역
	   JScrollPane scPane; // 스크롤
	   JTextField tf_Send = new JTextField(""); // 보낼 메시지를 적는필드
	   JTextField tf_Name = new JTextField(); // 사용자 이름 상자
	   JTextField tf_ipaddress = new JTextField(); // 서버에 접속할 ip주소입력필드
	   DefaultListModel model = new DefaultListModel(); // 리스트모델
	   JList li_Player = new JList(model); // 사용자 리스트
	   JButton connectButton = new JButton("접속"); // 시작 버튼
	   JButton readyButton = new JButton("준비"); // 종료버튼
	   JButton turnButton = new JButton("카드뒤집기"); // 카드 뒤집기 버튼
	   JButton bellButton = new JButton("종치기"); // 종치기 버튼
	   JLabel la_GameInfo = new JLabel("<정보창>"); // 정보창
	   JLabel la_PlayerInfo = new JLabel("<인원정보>"); // 인원정보
	   boolean ready = false;
	   
	   public test()
	   {
		   setLayout(null);
		   ta_MsgView = new JTextArea(1, 1);
		      scPane = new JScrollPane(ta_MsgView);
		     
		      // 각종 컴포넌트를 생성하고 배치한다.
		      EtchedBorder eborder = new EtchedBorder(EtchedBorder.RAISED);
		      LineBorder border = new LineBorder(Color.RED);//테두리색설정.위치선정때문에.
		      
		      ta_MsgView.setEditable(false);
		      la_GameInfo.setBounds(10, 30, 480, 30);
		      add(la_GameInfo);

		      JPanel p1 = new JPanel();
		      p1.setLayout(new GridLayout(4, 4));
		      p1.add(new Label("서버주소 : ", 2));
		      p1.add(tf_ipaddress);
		      p1.add(new Label("이름 : ", 2));
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
