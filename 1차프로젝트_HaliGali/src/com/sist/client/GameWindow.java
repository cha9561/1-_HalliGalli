package com.sist.client;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;


public class GameWindow extends JPanel{
	Image back;	
	JTextArea ta;
	JTextField tf;
	JButton b1,b2,b3;
	JTextArea profile1,profile2,profile3,profile4;
	
	public GameWindow(){
		back=Toolkit.getDefaultToolkit().getImage("C:\\image\\monkey_back.jpg");	//뒷배경		
		
		ta=new JTextArea();	
		JScrollPane js4=new JScrollPane(ta);	//스크롤되는 채팅창
		tf=new JTextField();					//채팅입력창
		b1=new JButton("전송");					//채팅전송버튼
		b2=new JButton("카드뒤집기");				//카드뒤집기
		b3=new JButton("Bell");					//bell누르기
		profile1=new JTextArea();				//사용자 정보창
		profile2=new JTextArea();
		profile3=new JTextArea();
		profile4=new JTextArea();
		
		setLayout(null);
		JPanel p=new JPanel();
		p.setBounds(10, 10, 775, 400); 			//p의 위치조정
		p.setLayout(null);
		p.setOpaque(true); 

		profile1.setBounds(0, 0, 120, 80);	
		profile2.setBounds(655, 0, 120, 80);
		profile3.setBounds(0, 320, 120, 80);
		profile4.setBounds(655, 320, 120, 80);
		b3.setBounds(370,200,50,50);
		
		p.add(profile1);p.add(profile2);p.add(profile3);p.add(profile4);
		p.add(b3);
		add(p);
		
		JPanel p1=new JPanel();				//채팅창
		p1.setBounds(10, 410, 350, 200);
		p1.setLayout(null); 
		p1.setOpaque(true); 
		
		js4.setBounds(0, 0, 350, 125);	//채팅
		tf.setBounds(0, 125, 300, 30);	//채팅입력창
		b1.setBounds(300, 125, 50, 30);	//채팅전송버튼
		p1.add(js4); p1.add(tf); p1.add(b1);
		p1.setOpaque(false);
		add(p1);
	}
	
	protected void paintComponent(Graphics g) {			
		g.drawImage(back, 0, 0, getWidth(), getHeight(), this);	//this->JPanel에 배경이미지 뿌림
	}

}
