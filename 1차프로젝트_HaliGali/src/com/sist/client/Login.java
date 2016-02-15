package com.sist.client;
import javax.swing.*;
import java.awt.*;

public class Login extends JPanel{
	Image back;				//LOGIN창 배경이미지
	JLabel lab1,lab2;		//라벨
	JTextField tf;			//ID
	JPasswordField pf;		//비밀번호
	JButton bt1,bt2;		//회원가입,login버튼
	ImageIcon joinButton=new ImageIcon("img/JoinButton2.png");
	ImageIcon loginButton=new ImageIcon("img/LoginButton.png");
	
	public Login(){
		
		back=Toolkit.getDefaultToolkit().getImage("img/monkey_back3.jpg");		//LOGIN창 배경이미지
		lab1=new JLabel("ID");						//선언부
		lab2=new JLabel("PW");
		tf=new JTextField();
		pf=new JPasswordField();
		bt1=new JButton(joinButton);
		bt2=new JButton(loginButton);
		
		setLayout(null);							//사용자 직접 배치!!
		JPanel p=new JPanel();
		p.setBounds(200, 250, 220, 150); 			//p의 위치조정
		p.setLayout(null);
		
		lab1.setBounds(10, 15, 30, 30);			//0,0기준으로 10,15는 간격, 30,30은 lab1의 가로세로크기
		tf.setBounds(45, 15, 150, 30);			//0,0기준으로 10+30=40,15는 간격
		lab2.setBounds(10, 50, 30, 30);			//높이:15+30+5(간격)=50
		pf.setBounds(45, 50, 150, 30);
		bt1.setBounds(15, 95, 90, 40);			
		bt2.setBounds(120, 95, 90, 40);
		bt1.setBorderPainted(false);
		bt1.setContentAreaFilled(false); 
		bt2.setBorderPainted(false);
		bt2.setContentAreaFilled(false); 
		
		p.setOpaque(false); 					//JPanel묶은것 회색을 투명하게!
		
		p.add(lab1);p.add(tf);p.add(lab2);
		p.add(pf);p.add(bt1);p.add(bt2);

		add(p);									//JPanel로 묶음을 화면에 추가		
	}
	
	protected void paintComponent(Graphics g) {				
		g.drawImage(back, 0, 0, getWidth(), getHeight(), this);	//this->JPanel에 배경이미지 뿌림
	}
}
