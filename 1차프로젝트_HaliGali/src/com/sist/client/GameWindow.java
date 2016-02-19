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
		back=Toolkit.getDefaultToolkit().getImage("img/monkey_back3.jpg");	//�޹��		
		
		ta=new JTextArea();	
		JScrollPane js4=new JScrollPane(ta);	//��ũ�ѵǴ� ä��â
		bar=js4.getVerticalScrollBar();
		
		tf=new JTextField();					//ä���Է�â
		b1=new JButton("����");					//ä�����۹�ư
		b2=new JButton("������");					//ī�������
		b3=new JButton("Bell");					//bell������
		b4=new JButton("�غ�");					//bell������
		b5=new JButton("����");					//bell������
		b6=new JButton("������");					//bell������
		profile1=new JTextArea();				//����� ����â
		profile2=new JTextArea();
		profile3=new JTextArea();
		profile4=new JTextArea();
		
		setLayout(null);
		
		JPanel p=new JPanel();					//ī����� ���� �ǳ�
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
		
		JPanel p1=new JPanel();				//ä��â+ä���Է�â ����
		p1.setBounds(10, 410, 350, 200);
		p1.setLayout(null); 	
		js4.setBounds(0, 0, 350, 125);	//ä��
		tf.setBounds(0, 125, 300, 30);	//ä���Է�â
		b1.setBounds(300, 125, 50, 30);	//ä�����۹�ư
		p1.setOpaque(false);
		p1.add(js4); p1.add(tf); p1.add(b1);
		add(p1);
	}
	
	protected void paintComponent(Graphics g) {			
		g.drawImage(back, 0, 0, getWidth(), getHeight(), this);	//this->JPanel�� ����̹��� �Ѹ�
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
