import javax.swing.*;
import java.awt.*;

public class Loading extends JPanel{
	Image back;				//LOGINâ ����̹���
	
	public Loading(){
		back=Toolkit.getDefaultToolkit().getImage("C:\\image\\loading.jpg");
	}
	
	protected void paintComponent(Graphics g) {				
		g.drawImage(back, 0, 0, getWidth(), getHeight(), this);	//this->JPanel�� ����̹��� �Ѹ�
	}
}
