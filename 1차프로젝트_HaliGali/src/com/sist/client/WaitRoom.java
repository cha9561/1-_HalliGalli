package com.sist.client;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.table.*;


public class WaitRoom extends JPanel{ //
	Image back;
	JTable table1,table2,table3;				//테이블
	DefaultTableModel model1,model2,model3;		
	
	JTextArea ta;		//채팅 창
	JTextField tf;		//채팅 입력 창
	JComboBox box;		//채팅 ENTER창
	JButton b1,b2,b3;	//버튼
	JTextArea profile;	//사용자정보
	JScrollBar bar;
	
	public WaitRoom(){
		back=Toolkit.getDefaultToolkit().getImage("C:\\image\\monkey_back.jpg");	//뒷배경
		
		String[] col1={"방이름","인원","게임상태"};	//방테이블
		String[][] row1=new String[5][3];	//col1열의 수에 맞게 5줄로 생성
		model1=new DefaultTableModel(row1, col1);
		table1=new JTable(model1);
		JScrollPane js1=new JScrollPane(table1);
		
		String[] col2={"순위","ID","승률"};		//순위표테이블
		String[][] row2=new String[0][3];
		model2=new DefaultTableModel(row2, col2);
		table2=new JTable(model2);
		JScrollPane js2=new JScrollPane(table2);
		
		String[] col3={"ID","승률"};				//접속자테이블
		String[][] row3=new String[0][3];
		model3=new DefaultTableModel(row3, col3);
		table3=new JTable(model3);
		JScrollPane js3=new JScrollPane(table3);
		
		
		ta=new JTextArea();						//채팅창
		JScrollPane js4=new JScrollPane(ta);	//textarea근처에 스크롤바
		bar=js3.getVerticalScrollBar();
		tf=new JTextField();					//채팅입력창
		b1=new JButton("전송");					//채팅전송버튼
		
		b2=new JButton("도움말");					//도움말버튼
		b3=new JButton("방만들기");				//방만들기버튼

		profile=new JTextArea();				//사용자정보
		
		
		JPanel p=new JPanel();					//도움말과 방만들기버튼 p로 묶기
		p.setLayout(new GridLayout(1, 2, 5, 5));
		p.add(b2);  p.add(b3);  
		p.setOpaque(false); 
		
		setLayout(null);					//나머지는 사용자 지정 레이아웃으로!
		js1.setBounds(10, 15, 500, 320);	//방목록	
		js2.setBounds(515, 10, 270, 160);	//순위표
		js3.setBounds(515, 175, 270, 160);	//접속자	
		js4.setBounds(10, 340, 500, 190);	//채팅
		tf.setBounds(10, 535, 410, 30);		//채팅입력창
		b1.setBounds(425, 535, 85, 30);		//채팅전송버튼
		profile.setBounds(515, 340, 270, 180);	//사용자 정보
		p.setBounds(515, 525, 270, 40);		//버튼묶음
		
		
		add(js1);add(js2);add(js3);
		add(js4);add(tf);add(b1);add(profile);
		add(p);

		
	}
	
	protected void paintComponent(Graphics g) {			
		g.drawImage(back, 0, 0, getWidth(), getHeight(), this);	//this->JPanel에 배경이미지 뿌림
	}


}
