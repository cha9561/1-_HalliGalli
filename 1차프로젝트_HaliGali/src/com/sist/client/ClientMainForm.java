package com.sist.client;
import java.awt.*; 		//Layout들어있음
import javax.swing.*;	//window관련 버튼등등이 들어있음
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.sist.common.Function;
import java.awt.event.*;

//네트워크 관련
import java.io.*;
import java.net.*;
import java.util.*;

public class ClientMainForm extends JFrame implements ActionListener, Runnable{
		CardLayout card=new CardLayout();			//창 전환
		Loading ld=new Loading();					//LOADING창
		Login login=new Login();					//LOGIN창
		WaitRoom wr=new WaitRoom();					//WAITROOM창
		GameWindow gw=new GameWindow();				//게임WINDOW창
		MakeID mID=new MakeID();					//회원가입창
		MakeRoom mr=new MakeRoom();					//방만들기창
		Help help=new Help();
		
		int rowNum=-1;
		
	    Socket s;
	    BufferedReader in;// 서버에서 값을 읽는다
	    OutputStream out; // 서버로 요청값을 보낸다
		
		public ClientMainForm(){
			
			setLayout(card);		//BorderLayout
			
			add("LOG",login);		//2.login창
			add("WR",wr);			//3.WaitRoom창
			add("GW",gw);			//4.GAME Window창
			setSize(800,600);		//window창 크기 설정
			setLocation(270, 170);	//window창 위치 설정
			setVisible(true);		//보여지게 함
			setResizable(false);    //window창 고정(늘어나지 않음)					
			
			login.bt1.addActionListener(this);	//회원가입 버튼 누르면
			login.bt2.addActionListener(this);	//로그인 버튼 누르면
			wr.b1.addActionListener(this);		//로그인 버튼 누르면

			wr.b2.addActionListener(this);		//방만들기 버튼 누르면
			wr.b3.addActionListener(this);      //방들어가기 버튼 누르면
			wr.b8.addActionListener(this);		//도움말 버튼 누르면
			wr.b9.addActionListener(this);      //게임종료 버튼 누르면

			mr.b1.addActionListener(this);      //방만들기창에서 확인버튼 누르면
			gw.b1.addActionListener(this); 		//게임창에서 전송버튼 누르면
			gw.b4.addActionListener(this); 		//게임창에서 준비버튼 누르면
			gw.tf.addActionListener(this);
			mID.b1.addActionListener(this);
			mID.b2.addActionListener(this);
			mID.b3.addActionListener(this);

			wr.table1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {			
				@Override
				public void valueChanged(ListSelectionEvent e) {
					if(wr.table1.getSelectedRow()>-1)				//방번호가 0 이상이면
					{
						rowNum=wr.table1.getSelectedRow();
						System.out.println(rowNum);
					}
				}
			});
			
			
			
			
			this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		}
		
		public void actionPerformed(ActionEvent e) {
			if(e.getSource()==login.bt1)			//1.팝업창으로 회원가입창 띄우기
			{				    
				mID.setBounds(470, 310,340,420);
				mID.setVisible(true);				
			}
			else if(e.getSource()==login.bt2)		//2.로그인버튼 누르기
			{
				String id=login.tf.getText().trim();		//ID입력안했을때
				if(id.length()<1)
				{
					JOptionPane.showMessageDialog(this,
							"ID를 입력하세요");
					login.tf.requestFocus();
					return;
				}
				String pass=new String(login.pf.getPassword());	//PWD입력안했을때
				if(pass.length()<1)
				{
					JOptionPane.showMessageDialog(this,
							"Password를 입력하세요");
					login.pf.requestFocus();
					return;
				}
				
				connection();						//모두 정확히 처리했으면, connection()메소드로 이동!!
				
				try{
					out.write((Function.LOGIN+"|"+id+"|"					//로그인버튼 눌러서 server에게 요청
							+pass+"\n").getBytes());
				}catch(Exception ex){}
			}
			else if(e.getSource()==wr.tf || e.getSource()==wr.b1)			//3.waitroom에서 채팅입력할 때
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
			else if(e.getSource()==gw.tf || e.getSource()==gw.b1)			//4.gameWindow에서 채팅입력할 때
			{	
				String data=gw.tf.getText();								//입력한 값 가져오기
				if(data.length()<1)
					return;				
				try{
					out.write((Function.WAITCHAT2+"|"+data+"\n").getBytes());	//채팅전송을 server에게 
				}catch(Exception ex){}
				gw.tf.setText("");
			}
			
			else if(e.getSource()==wr.b2) 						//5.방만들기창 
			{				
				mr.setBounds(500, 300, 260,290);
		        mr.setVisible(true);
			}
			else if(e.getSource()==wr.b3) 						//6.방들어가기 버튼처리
			{
				if(rowNum>=0)
				{
					try {
						out.write((Function.JOINROOM+"|"+rowNum+"\n").getBytes());
					} catch (Exception e2) {			
					}
				}				
			}

			else if(e.getSource()==mr.b1)  						//6.방만들기창에서 확인 눌렀을때
			{
				String subject=mr.tf.getText().trim();			//방이름 입력 안했을때
		        if(subject.length()<1)
		        {
		        	JOptionPane.showMessageDialog(this,
							"방이름을 입력하세요");
		        	mr.tf.requestFocus();
		        	return;
		        }

		        if(mr.rb2.isSelected()){						//비공개 버튼 눌렀을 때
		        	String pw=new String(mr.pf.getPassword());		
			        if(pw.length()<1)
			        {
			        	JOptionPane.showMessageDialog(this,
								"비밀번호를 입력하세요");
			        	mr.pf.requestFocus();
			        	return;
			        }

		        }

		        mr.dispose();
		        
		        try{
					String roomName=mr.tf.getText();					//방이름
					String capaNum=mr.box.getSelectedItem().toString();	//최대인원수(2명)
					out.write((Function.MAKEROOM+"|"+roomName+"|"+capaNum+"\n").getBytes()); //방이름,최대인원수
				}catch(Exception ex){}

			}
			else if(e.getSource()==wr.b8) //도움말 버튼처리
			{		
				help.setVisible(true);	
				repaint();
			}else if(e.getSource()==wr.b9) //게임종료 버튼처리
			{
				/*서버로 종료 메시지 전송후 프로그램 종료*/
				try 
				{
					out.write((Function.CLIENTEXIT+"|\n").getBytes());
					
				} catch (Exception e2) 
				{
				}
				
				try {
					s.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				System.exit(0);
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
				try {
					out.write((Function.SUCCESSJOIN+"|"+name+"|"+id+"|"+pass1+"\n").getBytes());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
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
			else if(e.getSource()==gw.b4){								//GameWindow에서 준비버튼 눌렀을 때
				try {
					out.write((Function.ROOMREADY+"|"+"\n").getBytes());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			
		}

		//서버와 연결
		public void connection()
	    {
	    	try
	    	{
	    		s=new Socket("localhost", 65535);		// s=>server
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
					case Function.DELROW: //게임종료한 client 정보 접속자 List 에서 삭제
					{
						int rowIndex=(Integer.parseInt(st.nextToken()));
						System.out.println("삭제 줄: "+rowIndex);
						wr.model2.removeRow(rowIndex);
					}
					case Function.CLIENTEXIT:
					{
						wr.ta.append(st.nextToken()+"\n");
						wr.bar.setValue(wr.bar.getMaximum());
					}
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
						  wr.model2.addRow(data);		
					  }
					  break;
					  
					  case Function.WAITCHAT1:			//3.채팅할 때(waitroom)
					  {
						  wr.ta.append(st.nextToken()+"\n");
						  wr.bar.setValue(wr.bar.getMaximum());
					  }
					  break;
					  
					  case Function.ROOMCHAT:			//3.채팅할 때(gameWindow)
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
					  
					  case Function.MAKEROOM:			//5.client가 방만들기 확인 버튼을 눌렀을 때(게임창 전환)
					  {	
						  String roomId=st.nextToken();		//게임룸 만든 사람 	id
						  String roomName=st.nextToken();	//새로 만든 게임룸의 	이름
						  String num=st.nextToken();		//새로 만든 게임룸의  	제한인원수
						  String pos=st.nextToken();		//사용자 위치
						  setTitle(roomId+"님이 만드신 "+roomName);	
						  //String[] data={roomName, num, "게임대기중"};	//?
						  card.show(getContentPane(), "GW"); 		//게임창으로 전환
					  }
					  break;

					  case Function.ROOMINFORM:			//5.client가 방만들기 확인 버튼을 눌렀을 때(waitRoom의 리스트에 방 추가)
					  {	
						  String roomName=st.nextToken();	//새로 만든 게임룸의 	이름
						  String num=st.nextToken();		//새로 만든 게임룸의  	제한인원수
						  String pos=st.nextToken();		//사용자 위치(게임대기중)
						  String[] data={roomName, num, pos};	
						  wr.model1.addRow(data);			//waitRoom의 리스트에 방 추가
						  wr.repaint();
					  }
					  break;
					  
					  case Function.JOINROOM:			//6.방에 들어가기 했을 때(인원 수에따라 입장 가능 여부)
					  {
						  String result=st.nextToken();
						  if(result.equals("TRUE"))
						  {
							  //setTitle(roomId+"님이 만드신 "+roomName);	
							  card.show(getContentPane(), "GW");
						  }
						  else
						  {
							  JOptionPane.showMessageDialog(this,"방이 꽉찼습니다.");
						  }
					  }
					  break;
					  
					  case Function.ROOMREADY:			//6.방에 들어가기 했을 때(인원 수에따라 입장 가능 여부)
					  {
						  System.out.println("최종적으로 준비전달받음");
					  }
					  break;
					}
				}catch(Exception ex){}
			}
		}
}
