package com.sist.client;
import java.awt.*; 		//Layout들어있음
import javax.swing.*;	//window관련 버튼등등이 들어있음

import com.sist.common.Function;

import java.awt.event.*;

//네트워크 관련
import java.io.*;
import java.net.*;
import java.util.*;

public class ClientMainForm extends JFrame implements 
ActionListener, Runnable{
		CardLayout card=new CardLayout();			//창 전환
		Loading ld=new Loading();					//LOADING창
		Login login=new Login();					//LOGIN창
		WaitRoom wr=new WaitRoom();					//WAITROOM창
		GameWindow gw=new GameWindow();				//게임WINDOW창
		MakeID mID=new MakeID();					//회원가입창
		MakeRoom mr=new MakeRoom();					//방만들기창
		Help help=new Help();
		
		// id|대화명|성별
	    Socket s;
	    BufferedReader in;// 서버에서 값을 읽는다
	    OutputStream out; // 서버로 요청값을 보낸다
		
		public ClientMainForm(){

			setLayout(card);		//BorderLayout
			
			add("LOG",login);		//2.login창
			setSize(800,600);		//window창 크기 설정
			setLocation(270, 170);	//window창 위치 설정
			setVisible(true);		//보여지게 함
			setResizable(false);    //window창 고정(늘어나지 않음)
					
			add("WR",wr);						//3.WaitRoom창
			login.bt1.addActionListener(this);	//회원가입 버튼 누르면
			login.bt2.addActionListener(this);	//로그인 버튼 누르면
			wr.b1.addActionListener(this);		//로그인 버튼 누르면
			wr.b2.addActionListener(this);		//방만들기 버튼 누르면
			wr.b3.addActionListener(this);      //방들어가기 버튼 누르면
			wr.b8.addActionListener(this);		//도움말 버튼 누르면
			wr.b9.addActionListener(this);      //게임종료 버튼 누르면
			mr.b1.addActionListener(this);      //방만들기창에서 확인버튼 누르면
			wr.tf.addActionListener(this);		//사용자 입력값 받으면 
			wr.b3.addActionListener(this); 		//방만들기버튼 누르면
			
			add("GW",gw);						//4.GAME Window창
			gw.b1.addActionListener(this); 		//게임창에서 전송버튼 누르면
			gw.tf.addActionListener(this);
			mID.b1.addActionListener(this);
			mID.b2.addActionListener(this);
			mID.b3.addActionListener(this);
			
			
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource()==login.bt1)
			{				    
				//버튼(bt1)에 액션이 가해지면
				mID.setBounds(470, 310,340,420);
				mID.setVisible(true);				//팝업창으로 회원가입창 띄우기
			}
			else if(e.getSource()==login.bt2)
			{
				String id=login.tf.getText().trim();
				if(id.length()<1)
				{
					JOptionPane.showMessageDialog(this,
							"ID를 입력하세요");
					login.tf.requestFocus();
					return;
				}
				String pass=new String(login.pf.getPassword());
				if(pass.length()<1)
				{
					JOptionPane.showMessageDialog(this,
							"Password를 입력하세요");
					login.pf.requestFocus();
					return;
				}
				
				connection();		//connection()메소드로 이동!!
				
				try{
					out.write((Function.LOGIN+"|"+id+"|"					//로그인버튼 눌러서 server에게 요청
							+pass+"\n").getBytes());
				}catch(Exception ex){}
			}
			else if(e.getSource()==wr.tf || e.getSource()==wr.b1)			//1.waitroom에서 채팅입력할 때
			{			
				String data=wr.tf.getText();								//입력한 값 가져오기
				if(data.length()<1)
					return;				
				try
				{
					out.write((Function.WAITCHAT1+"|"+data+"\n").getBytes());	//채팅전송을 server에게 요청
				}catch(Exception ex){}
				wr.tf.setText("");
			}
			else if(e.getSource()==gw.tf || e.getSource()==gw.b1)			//2.gameWindow에서 채팅입력할 때
			{	
				String data=gw.tf.getText();								//입력한 값 가져오기
				if(data.length()<1)
					return;				
				try{
					out.write((Function.WAITCHAT2+"|"+data+"\n").getBytes());	//채팅전송을 server에게 
				}catch(Exception ex){}
				gw.tf.setText("");
			}
			else if(e.getSource()==wr.b2) //방만들기버튼->GAME Window창
			{		
				mr.setBounds(500, 300, 260,290);
		        mr.setVisible(true);
			}
			else if(e.getSource()==wr.b3) //방들어가기 버튼처리
			{
				
			}
			else if(e.getSource()==mr.b1)  //방만들기창에서 확인 눌렀을때 처리
			{
				String subject=mr.tf.getText().trim();
		        if(subject.length()<1)
		        {
		        	JOptionPane.showMessageDialog(this,
							"방이름을 입력하세요");
		        	mr.tf.requestFocus();
		        	return;
		        }
		        String pw=new String(mr.pf.getPassword());
		        if(mr.rb2.isSelected())
		        {
		        	if(pw.length()<1)
		   	        {
		   	        	JOptionPane.showMessageDialog(this,
		   						"비밀번호를 입력하세요");
		   	        	mr.pf.requestFocus();
		   	        	return;
		   	        }
		        }
		        int a=0;
		        mr.dispose();
		        card.show(getContentPane(), "GW");
			}
			else if(e.getSource()==wr.b8) //도움말 버튼처리
			{		
				help.setVisible(true);	
				repaint();
			}else if(e.getSource()==wr.b9) //게임종료 버튼처리
			{
				
			}
			else if(e.getSource()==mID.b1)					//가입완료버튼
			{
				String name=mID.tf1.getText().trim();
				String id=mID.tf2.getText().trim();
				String pass1=new String(mID.pf1.getPassword());
				String pass2=new String(mID.pf2.getPassword());
				if(name.length()<1)
				{
					JOptionPane.showMessageDialog(this,
							"이름을 입력하세요");
					mID.tf1.requestFocus();
					return;
				}
				else if(id.length()<1)
				{
					JOptionPane.showMessageDialog(this,
							"ID를 입력하세요");
					mID.tf2.requestFocus();
					return;
				}
				else if(mID.ck==false)
				{
					JOptionPane.showMessageDialog(this,
							"ID 중복체크 하시오");
					mID.tf2.requestFocus();
					return;
				}
				else if(pass1.length()<1)
				{
					JOptionPane.showMessageDialog(this,
							"비밀번호를 입력하세요");
					mID.pf1.requestFocus();
					return;
				}
				else if(pass2.length()<1)
				{
					JOptionPane.showMessageDialog(this,
							"비밀번호 확인을 입력하세요");
					mID.pf2.requestFocus();
					return;
				}
				else if(!(pass1.equals(pass2)))
				{
					JOptionPane.showMessageDialog(this,
							"비밀번호가 동일하지 않습니다");
					mID.pf1.requestFocus();
					return;
				}
				JOptionPane.showMessageDialog(this, "회원가입완료");
				mID.dispose();
			}
			else if(e.getSource()==mID.b2)
			{
				mID.tf1.setText("");
				mID.tf2.setText("");
				mID.pf1.setText("");
				mID.pf2.setText("");
				mID.dispose();
				System.out.println("취소");
			}
			else if(e.getSource()==mID.b3)				//ID중복체크
			{
				String id=mID.tf2.getText().trim();
				if(id.length()<1)
				{
					JOptionPane.showMessageDialog(this,
							"ID를 입력하세요");
					mID.tf2.requestFocus();
					return;
				}
				System.out.println(mID.num);
				
				if(mID.num==0)						//한번도 소켓을 연결하지 않았다면
				{
					System.out.println("연결시도");	
					connection();
					mID.num++;
				}
				
				System.out.println("ID중복체크");
				try
				{
					System.out.println(id);
					out.write((Function.IDCHECK+"|"+id+"\n").getBytes());		//ID중복체크를 server에게 요청
				}catch(Exception ex){}
			}
			
		}
		 // 서버와 연결
/*	    public void connection(String id,String pass)
	    {
	    	try
	    	{
	    		s=new Socket("localhost", 65535);
	    		// s=>server
	    		in=new BufferedReader(
						new InputStreamReader(s.getInputStream()));
				out=s.getOutputStream();
				out.write((Function.LOGIN+"|"+id+"|"
						+pass+"\n").getBytes());
	    	}catch(Exception ex){}
	    	
	    	// 서버로부터 응답값을 받아서 처리
	    	new Thread(this).start();// run()
	    }*/
		public void connection()
	    {
	    	try
	    	{
	    		s=new Socket("localhost", 65535);
	    		// s=>server
	    		in=new BufferedReader(new InputStreamReader(s.getInputStream()));		//서버로 값을 읽어들임
				out=s.getOutputStream();												//서버로 값을 보냄
				/*out.write((Function.LOGIN+"|"+id+"|"
						+pass+"\n").getBytes());*/
	    	}catch(Exception ex){}
	    	
	    	new Thread(this).start();	// run()으로 이동  // 서버로부터 응답값을 받아서 처리
	    }
		
		public static void main(String[] args) {
			 
			try{
				//UIManager.setLookAndFeel("com.jtattoo.plaf.noire.NoireLookAndFeel");	//폼디자인 긁어온것
			}catch(Exception ex){}
			
			ClientMainForm cm=new ClientMainForm();

		}

		@Override
		public void run() {			//client와 server간의 통신 

			while(true){
				try{
					String msg=in.readLine();
					System.out.println("Server=>"+msg);
					StringTokenizer st=new StringTokenizer(msg, "|");
					int protocol=Integer.parseInt(st.nextToken());
					switch(protocol)
					{
					  case Function.MYLOG:				//1.window타이틀에 사용자이름 업데이트
					  {
						  String id=st.nextToken();
						  setTitle(id);
						  card.show(getContentPane(), "WR");	//waitingroom으로 창 전환
					  }
					  break;
					  
					  case Function.LOGIN:				//2.접속자테이블에 사용자 업데이트
					  {
						  String[] data={
							st.nextToken(),	 
							st.nextToken()
						  };
						  wr.model3.addRow(data);		
					  }
					  break;
					  
					  case Function.WAITCHAT1:			//3.채팅할 때(waitroom)
					  {
						  wr.ta.append(st.nextToken()+"\n");
						  wr.bar.setValue(wr.bar.getMaximum());
					  }
					  break;
					  
					  case Function.WAITCHAT2:			//3.채팅할 때(gameWindow)
					  {
						  gw.ta.append(st.nextToken()+"\n");
						  gw.bar.setValue(gw.bar.getMaximum());
					  }
					  break;
					  
					  case Function.NOTOVERLAP:			//4.ID가 중복되지 않을 때
					  {
						  JOptionPane.showMessageDialog(this,"ID가 중복되지 않습니다");
						  mID.ck=true;
						  mID.pf1.requestFocus();
					  }
					  break;
					  
					  case Function.OVERLAP:			//4.ID가 중복될 때
					  {
						  JOptionPane.showMessageDialog(this,
									"ID가 중복됩니다. 다시 입력하세요.");
						  mID.ck=false;
						  mID.pf1.requestFocus();
					  }
					  break;
					}
				}catch(Exception ex){}
			}
		}
}
