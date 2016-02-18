package com.sist.server;
import java.util.*;
import com.sist.common.Function;
import java.io.*;
import java.net.*;

public class Server implements Runnable{
	Vector<ClientThread> waitVc=new Vector<ClientThread>();
	
	ServerSocket ss=null;	// �������� ���ӽ� ó�� (��ȯ ����)
	
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
	
    public void run()							// 1. ������ ó��
    {
    	while(true){
	    	try{
	    		Socket s=ss.accept();		//s => client    		
	    		ClientThread ct=new ClientThread(s);
	    		ct.start();					// ��� ���� 	
	    	}catch(Exception ex){}
    	}	
    }
    
	public static void main(String[] args) {
		Server server=new Server();		        // ���� ����
		new Thread(server).start();
	}
	
	
	
	class ClientThread extends Thread
	{
		String id,name,sex,pos;
		Socket s;
		BufferedReader in;			//client�� ��û���� �о�´�
		OutputStream out;			//client�� ������� �����Ҷ� 
		
		public ClientThread(Socket s)
		{
			try{
				this.s=s;			//�� Ŭ���̾�Ʈ�� ���� ����
				in=new BufferedReader(new InputStreamReader(s.getInputStream()));
				out=s.getOutputStream();
			}catch(Exception ex){}
		}
		
		public void run()			//2.client�� server���� ����� ó��  //Client�� ��û�� ����
		{
			while(true)			
			{
				try
				{
					String msg=in.readLine();
					System.out.println("Client=>"+msg);		//Client���� ���� �޽����� ��Ÿ����
					StringTokenizer st=new StringTokenizer(msg, "|");
					int protocol=Integer.parseInt(st.nextToken());
					switch(protocol)
					{
					  case Function.LOGIN:				//1.client�� �α��� ��ư�� ��û���� ��
					  {
						 id=st.nextToken();
						 //name=st.nextToken();
						 //sex=st.nextToken();
						 pos="����";
						// messageAll(Function.LOGIN+"|"+id+"|"+name+"|"+sex+"|"+pos);
						 messageAll(Function.LOGIN+"|"+id+"|"+pos);	//WaitRoom�� ������â�� ���� �߰�
						 messageTo(Function.MYLOG+"|"+id+"|"+pos);	//������ WaitRoom����
						 waitVc.addElement(this);
						 
						 //�α��� �� Ŭ���̾�Ʈ�� ������ �迭�� ����!!
						 for(ClientThread client:waitVc)			//���� WaitRoom ������â�� ����Ʈ ���
						 {
							 messageTo(Function.LOGIN+"|"+client.id+"|"+client.pos);	
						 }
						 // ������ ���� 
					  }
					  break;
					  
					  case Function.WAITCHAT1:			//2.client�� ä�������� ��û���� ��(waitroom)
					  {
						  String data=st.nextToken();
						  messageAll(Function.WAITCHAT1+"|["+id+"]"+data);
					  }
					  break;
					  
					  case Function.WAITCHAT2:			//2.client�� ä�������� ��û���� ��(gamewindow)
					  {
						  String data=st.nextToken();
						  messageAll(Function.WAITCHAT2+"|["+id+"]"+data);
					  }
					  break;
					  
					  case Function.IDCHECK:			//3.client�� ID�ߺ�üũ�� ��û���� ��
					  {
						  System.out.println("ID�ߺ�üũ ��û");
						  String id=st.nextToken();
						  System.out.println(id);
						  /*ID �ߺ� üũ ������*/
						  messageTo(Function.NOTOVERLAP+"|");
					  }
					  break;
					  
					  case Function.MAKEROOM:			//4.client�� �游��� Ȯ�� ��ư�� ������ ��
					  {
						  System.out.println("�游��� ��û");
						  String roomName=st.nextToken();
						  String num=st.nextToken();
						  pos="���ӷ�";
						  messageTo(Function.MAKEROOM+"|"+id+"|"+roomName+"|"+num);	//����ID,���̸�,�ο���
					  }
					  break;
					}
				}catch(Exception ex){}
			}
		}
		
		public synchronized void messageTo(String msg)		// ���������� client���� �޼��� ����
		{
			try
			{
				out.write((msg+"\n").getBytes());
			}catch(Exception ex){}
		}
		
		public synchronized void messageAll(String msg)		// ��ü������ client���� �޼��� ����
		{
			for(ClientThread client:waitVc)
			{
				client.messageTo(msg);
			}
		}
	}

}




