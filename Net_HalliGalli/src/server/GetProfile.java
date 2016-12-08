package server;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class GetProfile implements Runnable {

	ServerSocket profile_server;
	String name = null;


	public static void main(String[] args) throws IOException, SQLException {
		// TODO Auto-generated method stub

		GetProfile profile_server = new GetProfile(); // ���� ����

		new Thread(profile_server).start();
	}

	public GetProfile() throws IOException {

		InetAddress addr = InetAddress.getByName("localhost");

		profile_server = new ServerSocket(655);
		System.out.println("Profile Server Start...");
	}

	public void run() { // 1. ������ ó��
		while (true) {
			try {
				Socket s = profile_server.accept();
				System.out.println("����" + s + "�� �����");
				ClientThread ct = new ClientThread(s);
				ct.start(); // ��� ����
			} catch (Exception ex) {
			}
		}
	}

	class ClientThread extends Thread {
		Socket s;

		public ClientThread(Socket s) {
			try {
				this.s = s; // �� Ŭ���̾�Ʈ�� ���� ����
				System.out.println("connect by user");
			} catch (Exception ex) {
			}
		}

		public void run() { // 2.client�� server���� ����� ó�� //Client�� ��û�� ����

			try {
				InputStream is = s.getInputStream();
				BufferedReader in = new BufferedReader(new InputStreamReader(is));
				OutputStream os = s.getOutputStream();
		        PrintWriter out = new PrintWriter(os, true);
				FileOutputStream fo;

				while (true) {
										
					String msg=in.readLine();
					StringTokenizer st=new StringTokenizer(msg, "|");
					String check=st.nextToken();
					
					if(check.equals("ID")){
					 name = st.nextToken();
					}

					if (!(name.isEmpty())) {
						
						out.println("profile\n");
						fo = new FileOutputStream("c:/profile/" + name + ".jpg");
						byte[] dataBuff = new byte[10000];
						int length = is.read(dataBuff);

						System.out.print("�ٿ��� ");
						while (length != -1) {
							System.out.print(".");
							fo.write(dataBuff, 0, length);
							length = is.read(dataBuff);
						}

						System.out.println();
						System.out.println("���� ���� ����");
						break;
					}
				}

				os.close();
				out.close();
				in.close();
				is.close();
				fo.close();
				s.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

}
