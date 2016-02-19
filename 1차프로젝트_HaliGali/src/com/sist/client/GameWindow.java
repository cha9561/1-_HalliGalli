package com.sist.client;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.OutputStream;
import java.net.Socket;

import javax.swing.*;
import javax.swing.table.*;


public class GameWindow extends JPanel implements ActionListener,Runnable{
	Image back;	
	JTextArea ta;
	JTextField tf;
	JButton b1,b2,b3,b4,b5,b6;
	JTextArea profile1,profile2,profile3,profile4;
	JScrollBar bar;
	
	public GameWindow(){
		back=Toolkit.getDefaultToolkit().getImage("img/monkey_back3.jpg");	//뒷배경		
		
		ta=new JTextArea();	
		JScrollPane js4=new JScrollPane(ta);	//스크롤되는 채팅창
		bar=js4.getVerticalScrollBar();
		
		tf=new JTextField();					//채팅입력창
		b1=new JButton("전송");					//채팅전송버튼
		b2=new JButton("뒤집기");					//카드뒤집기
		b3=new JButton("Bell");					//bell누르기
		b4=new JButton("준비");					//bell누르기
		b5=new JButton("시작");					//bell누르기
		b6=new JButton("나가기");					//bell누르기
		profile1=new JTextArea();				//사용자 정보창
		profile2=new JTextArea();
		profile3=new JTextArea();
		profile4=new JTextArea();
		
		setLayout(null);
		
		JPanel p=new JPanel();					//카드게임 실행 판넬
		p.setBounds(10, 10, 775, 395); 			
		p.setLayout(null);
		p.setOpaque(true); 
		profile1.setBounds(0, 0, 120, 80);	
		profile2.setBounds(655, 0, 120, 80);
		profile3.setBounds(0, 320, 120, 80);
		profile4.setBounds(655, 320, 120, 80);	
		b2.setBounds(320,200,60,50);
		b3.setBounds(385,200,60,50);
		b4.setBounds(700,410,80,40);
		b5.setBounds(700,465,80,40);
		b6.setBounds(700,515,80,40);
		p.add(profile1);p.add(profile2);p.add(profile3);p.add(profile4);
		p.add(b2);p.add(b3);
		
		add(b4);add(b5);add(b6);
		add(p);
		
		JPanel p1=new JPanel();				//채팅창+채팅입력창 묶음
		p1.setBounds(10, 410, 350, 200);
		p1.setLayout(null); 	
		js4.setBounds(0, 0, 350, 125);	//채팅
		tf.setBounds(0, 125, 300, 30);	//채팅입력창
		b1.setBounds(300, 125, 50, 30);	//채팅전송버튼
		p1.setOpaque(false);
		p1.add(js4); p1.add(tf); p1.add(b1);
		add(p1);
	}
	
	protected void paintComponent(Graphics g) {			
		g.drawImage(back, 0, 0, getWidth(), getHeight(), this);	//this->JPanel에 배경이미지 뿌림
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

}
