package com.sist.server;
import java.util.*;
import com.sist.common.Function;
import java.io.*;
import java.net.*;

public class Server implements Runnable{

	Vector<ClientThread> waitVc=
			new Vector<ClientThread>();
	ServerSocket ss=null;// 서버에서 접속시 처리 (교환 소켓)
	/*회원관리*/
	FileIO cliFile=new FileIO();
	/*회원관리*/

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
						  boolean ck=cliFile.searchId(id);
						  /*ID 중복 체크 구현부*/
						  if(ck==true) //ID 가 없으면
							 messageTo(Function.NOTOVERLAP+"|");
						  else //ID 가 있으면
							  messageTo(Function.OVERLAP+"|");
					  }
					  break;
					  case Function.SUCCESSJOIN:
					  {
						  System.out.println("회원 정보 추가");
						  String name=st.nextToken();
						  String id=st.nextToken();
						  String pass=st.nextToken();
						  cliFile.insertCli(name+","+id+","+pass+"\n");
					  }
					  /*회원관리*/
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
/*회원관리*/
class FileIO{
	static File clifile=new File("c:\\image\\client.csv");
	public FileIO()
	{
		try
		{			
			if(!clifile.exists())
			{
				clifile.createNewFile();
				BufferedWriter writer = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(clifile),"MS949"));
				String head="이름,ID,Password,캐릭터,승,패,승률\n";
				writer.write(head);
				writer.close();
			}			
		}catch(Exception ex){}
	}
	boolean searchId(String id)
	{
		try
		{
			FileReader in=new FileReader(clifile);
			String data="";
			int i=0;
			while((i=in.read())!=-1)
			{
				data+=String.valueOf((char)i);
			}
			in.close();
			System.out.println(data);
			String[] datas=data.split("\n");
			InfomClient[] infom=new InfomClient[datas.length];
			for(i=0;i<datas.length;i++)
			{
				if(infom[i].getId().equals(id))
				{
					System.out.println("ID중복 발생");
					return false;
				}
			}
			//return true;
		}catch(Exception ex){}
		System.out.println("ID중복 없음");
		return true;
	}
	void insertCli(String data)
	{
		System.out.println("추가");
		try
		{
			System.out.println(data);
			BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(clifile),"MS949"));
			writer.write(data);
			writer.close();
		}catch(Exception ex){}
	}
}
/*회원관리*/



