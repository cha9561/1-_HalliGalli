package com.sist.server;
import java.util.*;
import com.sist.common.Function;
import java.io.*;
import java.net.*;

public class Server implements Runnable{
	Vector<ClientThread> waitVc=new Vector<ClientThread>();
	
	ServerSocket ss=null;	// 서버에서 접속시 처리 (교환 소켓)
	
	public Server()
	{
		try
		{
			ss=new ServerSocket(65535);
			System.out.println("Server Start...");
		}catch(Exception ex)
		{
			System.out.println(ex.getMessage());
		}
	}
	
    public void run()							// 1. 접속을 처리
    {
    	while(true){
	    	try{
	    		Socket s=ss.accept();		//s => client    		
	    		ClientThread ct=new ClientThread(s);
	    		ct.start();					// 통신 시작 	
	    	}catch(Exception ex){}
    	}	
    }
    
	public static void main(String[] args) {
		Server server=new Server();		        // 서버 가동
		new Thread(server).start();
	}
	
	
	
	class ClientThread extends Thread
	{
		String id,name,sex,pos;
		Socket s;
		BufferedReader in;			//client의 요청값을 읽어온다
		OutputStream out;			//client로 결과값을 응답할때 
		
		public ClientThread(Socket s)
		{
			try{
				this.s=s;			//각 클라이언트의 소켓 장착
				in=new BufferedReader(new InputStreamReader(s.getInputStream()));
				out=s.getOutputStream();
			}catch(Exception ex){}
		}
		
		public void run()			//2.client와 server간의 통신을 처리  //Client의 요청을 받음
		{
			while(true)			
			{
				try
				{
					String msg=in.readLine();
					System.out.println("Client=>"+msg);		//Client에서 보낸 메시지를 나타내줌
					StringTokenizer st=new StringTokenizer(msg, "|");
					int protocol=Integer.parseInt(st.nextToken());
					switch(protocol)
					{
					  case Function.LOGIN:				//1.client가 로그인 버튼을 요청했을 때
					  {
						 id=st.nextToken();
						 //name=st.nextToken();
						 //sex=st.nextToken();
						 pos="대기실";
						// messageAll(Function.LOGIN+"|"+id+"|"+name+"|"+sex+"|"+pos);
						 messageAll(Function.LOGIN+"|"+id+"|"+pos);	//WaitRoom의 접속자창에 나를 추가
						 messageTo(Function.MYLOG+"|"+id+"|"+pos);	//나에게 WaitRoom띄우기
						 waitVc.addElement(this);
						 
						 //로그인 한 클라이언트의 정보를 배열에 저장!!
						 for(ClientThread client:waitVc)			//나의 WaitRoom 접속자창에 리스트 출력
						 {
							 messageTo(Function.LOGIN+"|"+client.id+"|"+client.pos);	
						 }
						 // 방정보 전송 
					  }
					  break;
					  
					  case Function.WAITCHAT1:			//2.client가 채팅전송을 요청했을 때(waitroom)
					  {
						  String data=st.nextToken();
						  messageAll(Function.WAITCHAT1+"|["+id+"]"+data);
					  }
					  break;
					  
					  case Function.WAITCHAT2:			//2.client가 채팅전송을 요청했을 때(gamewindow)
					  {
						  String data=st.nextToken();
						  messageAll(Function.WAITCHAT2+"|["+id+"]"+data);
					  }
					  break;
					  
					  case Function.IDCHECK:			//3.client가 ID중복체크를 요청했을 때
					  {
						  System.out.println("ID중복체크 요청");
						  String id=st.nextToken();
						  System.out.println(id);
						  /*ID 중복 체크 구현부*/
						  messageTo(Function.NOTOVERLAP+"|");
					  }
					  break;
					  
					  case Function.MAKEROOM:			//4.client가 방만들기 확인 버튼을 눌렀을 때
					  {
						  System.out.println("방만들기 요청");
						  String roomName=st.nextToken();
						  String num=st.nextToken();
						  pos="게임룸";
						  messageTo(Function.MAKEROOM+"|"+id+"|"+roomName+"|"+num);	//만든ID,방이름,인원수
					  }
					  break;
					}
				}catch(Exception ex){}
			}
		}
		
		public synchronized void messageTo(String msg)		// 개인적으로 client에게 메세지 보냄
		{
			try
			{
				out.write((msg+"\n").getBytes());
			}catch(Exception ex){}
		}
		
		public synchronized void messageAll(String msg)		// 전체적으로 client에게 메세지 보냄
		{
			for(ClientThread client:waitVc)
			{
				client.messageTo(msg);
			}
		}
	}

}




