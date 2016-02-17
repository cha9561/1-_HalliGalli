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
    	while(true)
    	{
	    	try
	    	{
	    		// 클라이언트의 정보 => ip,port(Socket)
	    		Socket s=ss.accept();
	    		// s => client
	    		ClientThread ct=new ClientThread(s);
	    		ct.start();// 통신 시작 
	    		
	    	}catch(Exception ex){}
    	}
    	
    }
    
	public static void main(String[] args) {
        // 서버 가동
		Server server=new Server();
		new Thread(server).start();
	}
	
	
	
	class ClientThread extends Thread
	{
		String id,name,sex,pos;
		Socket s;
		BufferedReader in;	// client요청값을 읽어온다
		OutputStream out;	//client로 결과값을 응답할때 
		public ClientThread(Socket s)
		{
			try
			{
				this.s=s;
				in=new BufferedReader(new InputStreamReader(s.getInputStream()));
				out=s.getOutputStream();
			}catch(Exception ex){}
		}
		
		public void run()			//2.client와 server간의 통신을 처리 
		{
			while(true)
			{
				try
				{
					String msg=in.readLine();
					System.out.println("Client=>"+msg);
					StringTokenizer st=new StringTokenizer(msg, "|");
					int protocol=Integer.parseInt(st.nextToken());
					switch(protocol)
					{
					  case Function.LOGIN:				//client가 로그인 버튼을 요청했을 때
					  {
						 id=st.nextToken();
						 //name=st.nextToken();
						 //sex=st.nextToken();
						 pos="대기실";
						// messageAll(Function.LOGIN+"|"+id+"|"+name+"|"+sex+"|"+pos);
						 messageAll(Function.LOGIN+"|"+id+"|"+pos);
						 waitVc.addElement(this);
						 messageTo(Function.MYLOG+"|"+id+"|"+pos);
						 for(ClientThread client:waitVc)
						 {
							 messageTo(Function.LOGIN+"|"+client.id+"|"+client.pos);
						 }
						 // 방정보 전송 
					  }
					  break;
					  
					  case Function.WAITCHAT1:			//client가 채팅전송을 요청했을 때(waitroom)
					  {
						  String data=st.nextToken();
						  messageAll(Function.WAITCHAT1+"|["+id+"]"+data);
					  }
					  break;
					  
					  case Function.WAITCHAT2:			//client가 채팅전송을 요청했을 때(gamewindow)
					  {
						  String data=st.nextToken();
						  messageAll(Function.WAITCHAT2+"|["+id+"]"+data);
					  }
					  break;
					  
					  case Function.IDCHECK:			//client가 ID중복체크를 요청했을 때
					  {
						  System.out.println("ID중복체크");
						  String id=st.nextToken();
						  System.out.println(id);
						  /*ID 중복 체크 구현부*/
						  messageTo(Function.NOTOVERLAP+"|");
					  }
					  break;
					}
				}catch(Exception ex){}
			}
		}
		// 개인적으로 client에게 메세지 보냄
		public synchronized void messageTo(String msg)
		{
			try
			{
				out.write((msg+"\n").getBytes());
			}catch(Exception ex){}
		}
		// 전체적으로 client에게 메세지 보냄
		public synchronized void messageAll(String msg)
		{
			for(ClientThread client:waitVc)
			{
				client.messageTo(msg);
			}
		}
	}

}




