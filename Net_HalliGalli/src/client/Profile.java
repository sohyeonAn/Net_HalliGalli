package client;

import java.awt.*;

import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.net.*;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import java.io.*;
import client.ClientStart;

public class Profile extends JFrame implements ActionListener {

	FileDialog fd;
	Button b1, b2;
	TextField tf;
	String directory = "";
	static String file = "";
	String id;

	String serverIp = "localhost";
	JLabel label = new JLabel();

	public Profile(String id) {
		this.id = id;

		b1 = new Button("프로필 사진 선택");
		b1.addActionListener(this);
		tf = new TextField(25);
		b2 = new Button("프로필로 결정");
		b2.addActionListener(this);
		add(b1, "North");
		add(tf, "Center");
		add(b2, "South");
		setBounds(200, 200, 500, 200);
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent click) {
		String imgFile = null;
		try {

			if (click.getActionCommand().equals("프로필 사진 선택")) {
				fd = new FileDialog(this, "", FileDialog.LOAD);
				fd.setVisible(true);

				directory = fd.getDirectory();
				file = fd.getFile();
				tf.setText(directory + file);
			} else {

				Socket s = new Socket(serverIp, 655);
				imgFile = directory + file;
				InputStream is = s.getInputStream();
				BufferedReader in = new BufferedReader(new InputStreamReader(is));
				OutputStream os = s.getOutputStream();
				FileInputStream fi;
		        PrintWriter out = new PrintWriter(os, true);

				out.println("ID" + "|" + id );

				while (true) {
					String check = in.readLine();

					if (check.equals("profile")) {

						fi = new FileInputStream(imgFile);

						byte[] dataBuff = new byte[10000];
						int length = fi.read(dataBuff);

						while (length != -1) {
							os.write(dataBuff, 0, length);
							os.flush();
							length = fi.read(dataBuff);
						}

						System.out.println("전송성공");
						setVisible(false);

						break;

					}
				}

				in.close();
				is.close();
				fi.close();
				os.close();
				out.close();
				s.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		

	}



}
