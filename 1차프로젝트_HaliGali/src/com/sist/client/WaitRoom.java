package com.sist.client;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.table.*;


public class WaitRoom extends JPanel{ //
	Image back;
	ImageIcon roomlist,chattingroom,friends,ranking;
	JTable table1,table2;				//테이블
	DefaultTableModel model1,model2;		
	
	JTextArea ta;		//채팅 창
	JTextField tf;		//채팅 입력 창
	JComboBox box;		//채팅 ENTER창
	JButton b1,b2,b3,b8,b9;	//버튼
	JButton b4,b5,b6;//타이틀이미지  
	
	JTextArea profile;	//사용자정보
	JScrollBar bar;
	
	@SuppressWarnings("deprecation")
	public WaitRoom(){
		back=Toolkit.getDefaultToolkit().getImage("img/monkey_back.jpg");	//뒷배경
		roomlist=new ImageIcon("img/RoomList.png");			//타이틀이미지
		chattingroom=new ImageIcon("img/ChattingRoom.png");
		ranking=new ImageIcon("img/Ranking.png");
		friends=new ImageIcon("img/Friends.png");
		
		String[] col1={"방이름","인원","게임상태"};	//방테이블
		String[][] row1=new String[0][3];	//col1열의 수에 맞게 5줄로 생성
		model1=new DefaultTableModel(row1, col1);
		table1=new JTable(model1);
		JScrollPane js1=new JScrollPane(table1);
		
		String[] col2={"ID","상태","승률"};				//접속자테이블
		String[][] row2=new String[0][3];
		model2=new DefaultTableModel(row2, col2);
		table2=new JTable(model2);
		JScrollPane js2=new JScrollPane(table2);
		
		ta=new JTextArea();						//채팅창
		JScrollPane js3=new JScrollPane(ta);	//textarea근처에 스크롤바
		bar=js3.getVerticalScrollBar();
		tf=new JTextField();					//채팅입력창
		b1=new JButton("전송");					//채팅전송버튼
		
		b2=new JButton("방만들기");					//도움말버튼
		b3=new JButton("방들어가기");				//방만들기버튼
		b8=new JButton("도움말");
		b9=new JButton("게임종료");
		b4=new JButton(roomlist);
		b5=new JButton(chattingroom);
		b6=new JButton(friends);
		
		profile=new JTextArea();				//사용자정보
		
		
		JPanel p=new JPanel();					//도움말과 방만들기버튼 p로 묶기
		p.setLayout(new GridLayout(2, 2, 5, 5));
		p.add(b2);  p.add(b3);  
		p.add(b8);  p.add(b9);
		p.setOpaque(false); 
		
		setLayout(null);					//나머지는 사용자 지정 레이아웃으로!
		
		b4.setBorderPainted(false);
		b4.setContentAreaFilled(false);
		b5.setBorderPainted(false);
		b5.setContentAreaFilled(false);
		b6.setBorderPainted(false);
		b6.setContentAreaFilled(false);
		
		b4.setBounds(10, 15, 500, 30);
		b5.setBounds(10, 335, 500, 30);		//////채팅타이틀
		b6.setBounds(515, 15, 270, 30);
		js1.setBounds(10, 50, 500, 280);	//방목록
		js2.setBounds(515, 50, 270, 240);	//접속자	
		js3.setBounds(10, 370, 500, 150);	//채팅///////
		tf.setBounds(10, 525, 450, 30);		//채팅입력창/////
		b1.setBounds(465, 525, 45, 30);		//채팅전송버튼/////
		profile.setBounds(515, 335, 270, 140);	//사용자 정보
		p.setBounds(515, 480, 270, 75);		//버튼묶음
		
		
		add(js1);add(js2);add(js3);
		add(tf);add(b1);add(profile);
		add(p);add(b4);add(b5);add(b6);;

	}
	
	protected void paintComponent(Graphics g) {			
		g.drawImage(back, 0, 0, getWidth(), getHeight(), this);	//this->JPanel에 배경이미지 뿌림
	}


}
