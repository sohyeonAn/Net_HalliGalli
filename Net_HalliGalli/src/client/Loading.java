package client;
import javax.swing.*;
import java.awt.*;

public class Loading extends JPanel{
	Image bg;			//�α��� â ����̹���(background)
	
	public Loading()
	{
		//��� �̹���
		bg = Toolkit.getDefaultToolkit().getImage("img/loading.jpg");  
	}
	
	protected void paintComponent(Graphics g)
	{
		g.drawImage(bg, 0, 0, getWidth(),getHeight(),this);
	}
}
