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
			wr.b2.addActionListener(this);		//도움말 버튼 누르면
			wr.b3.addActionListener(this);      //방만들기 버튼 누르면
			wr.tf.addActionListener(this);		//사용자 입력값 받으면 
			
			add("GW",gw);						//4.GAME Window창
			wr.b3.addActionListener(this); 		//방만들기버튼 누르면
			
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
				mID.setVisible(true);											//팝업창으로 회원가입창 띄우기
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
				connection();
				try
				{
					out.write((Function.LOGIN+"|"+id+"|"
							+pass+"\n").getBytes());
				}catch(Exception ex){}
				//card.show(getContentPane(), "WR");			//Layout에 "WR"card를 상단으로 보여지게 하라
			}
			else if(e.getSource()==wr.tf || e.getSource()==wr.b1)
			{			//채팅창(tf)에 액션이 가해지면
				String data=wr.tf.getText();								//입력한 값 가져오기
				//wr.ta.append(data+"\n");
				if(data.length()<1)
					return;				
				try
				{
					out.write((Function.WAITCHAT+"|"+data+"\n").getBytes());
				}catch(Exception ex){}
				wr.tf.setText("");
			}
			else if(e.getSource()==wr.b3){					//방만들기버튼->GAME Window창
				mr.setBounds(500, 300, 260,290);
		        mr.setVisible(true);
			}else if(e.getSource()==wr.b2){
				card.show(getContentPane(), "GW"); 		//게임 창으로 전환
			}
			else if(e.getSource()==mID.b1)
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
				try {
					out.write((Function.SUCCESSJOIN+"|"+name+"|"+id+"|"+pass1+"\n").getBytes());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				JOptionPane.showMessageDialog(this, "회원가입완료");
				//dispose();	//메모리 그대로 둔 채로 창닫기
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
			else if(e.getSource()==mID.b3)
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
				if(mID.num==0)
				{
					System.out.println("연결시도");
					connection();
					mID.num++;
				}
				try
				{
					System.out.println(id);
					//out.write((Function.IDCHECK+"|"+id+"\n").getBytes());
					out.write((Function.IDCHECK+"|"+id+"\n").getBytes());
				}catch(Exception ex){}
				System.out.println("ID중복체크");
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
	    		in=new BufferedReader(
						new InputStreamReader(s.getInputStream()));
				out=s.getOutputStream();
				/*out.write((Function.LOGIN+"|"+id+"|"
						+pass+"\n").getBytes());*/
	    	}catch(Exception ex){}
	    	
	    	// 서버로부터 응답값을 받아서 처리
	    	new Thread(this).start();// run()
	    }
		
		public static void main(String[] args) {
			 
			try{
				//UIManager.setLookAndFeel("com.jtattoo.plaf.noire.NoireLookAndFeel");	//폼디자인 긁어온것
			}catch(Exception ex){}
			
			ClientMainForm cm=new ClientMainForm();

		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true)
			{
				try
				{
					String msg=in.readLine();
					System.out.println("Server=>"+msg);
					StringTokenizer st=
						  new StringTokenizer(msg, "|");
					int protocol=Integer.parseInt(st.nextToken());
					switch(protocol)
					{
					  case Function.LOGIN:
					  {
						  String[] data={
							st.nextToken(),	 
							st.nextToken()
						  };
						  wr.model3.addRow(data);
					  }
					  break;
					  case Function.MYLOG:
					  {
						  String id=st.nextToken();
						  setTitle(id);
						  card.show(getContentPane(), "WR");
					  }
					  break;
					  case Function.WAITCHAT:
					  {
						  wr.ta.append(st.nextToken()+"\n");
						  wr.bar.setValue(wr.bar.getMaximum());
					  }
					  break;
					  case Function.NOTOVERLAP:
					  {
						  JOptionPane.showMessageDialog(this,
									"ID가 중복되지 않습니다");
						  mID.ck=true;
						  mID.pf1.requestFocus();
					  }
					  break;
					  case Function.OVERLAP:
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
