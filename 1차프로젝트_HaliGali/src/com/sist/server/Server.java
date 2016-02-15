package com.sist.server;
import java.util.*;

import com.sist.common.Function;

import java.io.*;
import java.net.*;
public class Server implements Runnable{
	Vector<ClientThread> waitVc=
			new Vector<ClientThread>();
	ServerSocket ss=null;// 서버에서 접속시 처리 (교환 소켓)
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
    public void run()
    {
    	// 접속을 처리
    	while(true)
    	{
	    	try
	    	{
	    		// 클라이언트의 정보 => ip,port(Socket)
	    		Socket s=ss.accept();
	    		// s => client
	    		ClientThread ct=
	    				new ClientThread(s);
	    		ct.start();// 통신 시작 
	    		
	    	}catch(Exception ex){}
    	}
    	
    }
	public static void main(String[] args) {
		// TODO Auto-generated method stub
        // 서버 가동
		Server server=new Server();
		new Thread(server).start();
	}
	class ClientThread extends Thread
	{
		String id,name,sex,pos;
		Socket s;
		BufferedReader in;// client요청값을 읽어온다
		OutputStream out;//client로 결과값을 응답할때 
		public ClientThread(Socket s)
		{
			try
			{
				this.s=s;
				in=new BufferedReader(
						new InputStreamReader(s.getInputStream()));
				out=s.getOutputStream();
			}catch(Exception ex){}
		}
		// 통신 부분 
		public void run()
		{
			while(true)
			{
				try
				{
					String msg=in.readLine();
					System.out.println("Client=>"+msg);
					// 100|id|name|sex
					StringTokenizer st=
							new StringTokenizer(msg, "|");
					int protocol=Integer.parseInt(st.nextToken());
					switch(protocol)
					{
					  case Function.LOGIN:
					  {
						 id=st.nextToken();
						 //name=st.nextToken();
						 //sex=st.nextToken();
						 pos="대기실";
						 //messageAll(Function.LOGIN+"|"+id+"|"+name+"|"+sex+"|"+pos);
						 messageAll(Function.LOGIN+"|"+id+"|"+pos);
						 waitVc.addElement(this);
						 messageTo(Function.MYLOG+"|"+id);
						 for(ClientThread client:waitVc)
						 {
							 messageTo(Function.LOGIN+"|"+client.id+"|"
						          +client.pos);
						 }
						 // 방정보 전송 
					  }
					 break;
					  case Function.WAITCHAT:
					  {
						  String data=st.nextToken();
						  messageAll(Function.WAITCHAT+"|["
								  +id+"]"+data);
					  }
					  break;
					}
				}catch(Exception ex){}
			}
		}
		// 개인적으로 
		public synchronized void messageTo(String msg)
		{
			try
			{
				out.write((msg+"\n").getBytes());
			}catch(Exception ex){}
		}
		// 전체적으로 
		public synchronized void messageAll(String msg)
		{
			for(ClientThread client:waitVc)
			{
				client.messageTo(msg);
			}
		}
	}

}




