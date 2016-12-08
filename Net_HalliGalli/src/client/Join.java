
package client;

import java.awt.*;

import javax.swing.*;

import java.awt.event.*;
import java.awt.image.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import client.ClientStart;
import client.ClientStart;

public class Join extends JFrame implements ActionListener {

	JDialog check_ID;
	JLabel msg;
	JButton ok;

	String id, pwd;
	JTextField idF;
	JPasswordField pwdF;
	JButton b1, b2, b3;
	MyPanel panel;
	

	int idCheck = -1;// �ߺ�Ȯ�� ���� 0, ���̵� ��� ���� 1, �ߺ�Ȯ�� ������ ���� -1

	public Join() {

		setUndecorated(true);// Ÿ��Ʋ �ٰ� �����.

		setBounds(620, 350, 600, 450);
		setBackground(new Color(0, 0, 0, 0));
		panel = new MyPanel("img/join.png");// ȸ������ â ���
		setContentPane(panel);

		idF = new JTextField();
		pwdF = new JPasswordField();

		b1 = new JButton();
		b2 = new JButton();
		b3 = new JButton();

		// ��ġ Ȯ�ο� �׵θ� & �����ֱ�
		idF.setOpaque(false);
		pwdF.setOpaque(false);
		// idF.setBorder(null);
		// pwdF.setBorder(null);
		b1.setBorderPainted(true);
		b1.setContentAreaFilled(false);
		b2.setBorderPainted(true);
		b2.setContentAreaFilled(false);
		b3.setBorderPainted(true);
		b3.setContentAreaFilled(false);
		setLayout(null);

		// ���ڻ� �ٲٱ�
		idF.setForeground(Color.white);
		pwdF.setForeground(Color.white);

		// ��ġ����
		idF.setBounds(142, 136, 200, 30);
		pwdF.setBounds(142, 190, 200, 30);
		b1.setBounds(282, 272, 90, 42);// join
		b2.setBounds(135, 272, 90, 42);// back
		b3.setBounds(368, 136, 77, 34);// check

		add(b1);// join
		add(b2);// back
		add(b3);
		add(idF);
		add(pwdF);

		b1.addActionListener(this);
		b2.addActionListener(this);
		b3.addActionListener(this);

	}

	@SuppressWarnings("deprecation")
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (b1 == e.getSource()) {
			id = idF.getText();
			pwd = pwdF.getText();
			int error = 0;
			if (10 < id.length()) {
				JOptionPane.showMessageDialog(this, "ID length must be shorter than 10.");
				error = 1;
			} else if (id.length() <= 0) {
				JOptionPane.showMessageDialog(this, "Please enter your ID.");
				error = 1;
			}

			else if (15 < pwd.length()) {
				JOptionPane.showMessageDialog(this, "Password length must be shorter than 15.");
				error = 1;
			} else if (pwd.length() <= 0) {
				JOptionPane.showMessageDialog(this, "Please enter your password.");
				error = 1;
			}

			else if (idCheck == -1) {// �ߺ�Ȯ�� �ȴ���
				JOptionPane.showMessageDialog(this, "Please check if your ID is usable.");
				error = 1;
			} else if (idCheck == 0) {// �ߺ�Ȯ�� �������� id�� �ߺ���
				JOptionPane.showMessageDialog(this, "Please change your ID.");
				error = 1;
			}

			if (error == 0) {
				JOptionPane.showMessageDialog(this, "ȸ�����ԿϷ�");
				try {
					ClientStart.writer.write(Protocol.SUCCESSJOIN + "|" + id + "|" + pwd + "\n");
					ClientStart.writer.flush();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				dispose();// �޸� �״�� �� ä â�ݱ�
			}
		} else if (b2 == e.getSource()) {
			System.out.println("ȸ�������� ��ҵǾ����ϴ�.");
			dispose();
		} else if (b3 == e.getSource()) {
			System.out.println("ID�ߺ�üũ");
			id = idF.getText();
			try {
				ClientStart.writer.write(Protocol.IDCHECK + "|" + id + "\n");
				ClientStart.writer.flush();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

		}
	}

	public void pop_up(String m) {

		idCheck = 0;

		check_ID = new JDialog(this, "warning", true);
		msg = new JLabel(m, JLabel.CENTER);
		ok = new JButton("OK");

		if (m.equals("You can use your ID."))
			idCheck = 1;

		check_ID.setLayout(new FlowLayout());
		check_ID.setSize(140, 100);
		check_ID.setLocation(500, 500);

		check_ID.add(msg);
		check_ID.add(ok);

		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				check_ID.setVisible(false);
			}
		});

		check_ID.setVisible(true);

	}

}

class MyPanel extends JPanel // ȸ������ JPanelâ
{
	Image image;

	MyPanel(String img) {
		image = Toolkit.getDefaultToolkit().createImage(img);
		setOpaque(false);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (image != null) {
			g.drawImage(image, 0, 0, 500, 350, this);
		}

		Graphics2D g2d = (Graphics2D) g.create();

		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.0f));

		g2d.setColor(getBackground());
		g2d.fill(getBounds());
		g2d.dispose();
	}
}