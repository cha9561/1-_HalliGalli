package com.sist.client;
import javax.swing.*;
import java.awt.*;

public class Login extends JPanel{
	Image back;				//LOGINâ ����̹���
	JLabel lab1,lab2;		//��
	JTextField tf;			//ID
	JPasswordField pf;		//��й�ȣ
	JButton bt1,bt2;		//ȸ������,login��ư
	ImageIcon joinButton=new ImageIcon("img/JoinButton2.png");
	ImageIcon loginButton=new ImageIcon("img/LoginButton.png");
	
	public Login(){
		
		back=Toolkit.getDefaultToolkit().getImage("img/monkey_back3.jpg");		//LOGINâ ����̹���
		lab1=new JLabel("ID");						//�����
		lab2=new JLabel("PW");
		tf=new JTextField();
		pf=new JPasswordField();
		bt1=new JButton(joinButton);
		bt2=new JButton(loginButton);
		
		setLayout(null);							//����� ���� ��ġ!!
		JPanel p=new JPanel();
		p.setBounds(200, 250, 220, 150); 			//p�� ��ġ����
		p.setLayout(null);
		
		lab1.setBounds(10, 15, 30, 30);			//0,0�������� 10,15�� ����, 30,30�� lab1�� ���μ���ũ��
		tf.setBounds(45, 15, 150, 30);			//0,0�������� 10+30=40,15�� ����
		lab2.setBounds(10, 50, 30, 30);			//����:15+30+5(����)=50
		pf.setBounds(45, 50, 150, 30);
		bt1.setBounds(15, 95, 90, 40);			
		bt2.setBounds(120, 95, 90, 40);
		bt1.setBorderPainted(false);
		bt1.setContentAreaFilled(false); 
		bt2.setBorderPainted(false);
		bt2.setContentAreaFilled(false); 
		
		p.setOpaque(false); 					//JPanel������ ȸ���� �����ϰ�!
		
		p.add(lab1);p.add(tf);p.add(lab2);
		p.add(pf);p.add(bt1);p.add(bt2);

		add(p);									//JPanel�� ������ ȭ�鿡 �߰�		
	}
	
	protected void paintComponent(Graphics g) {				
		g.drawImage(back, 0, 0, getWidth(), getHeight(), this);	//this->JPanel�� ����̹��� �Ѹ�
	}
}
