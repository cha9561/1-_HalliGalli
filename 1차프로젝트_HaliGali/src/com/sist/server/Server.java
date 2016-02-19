package com.sist.server;
import java.util.*;
import com.sist.common.Function;
import java.io.*;
import java.net.*;

public class Server implements Runnable{

	Vector<ClientThread> waitVc=
			new Vector<ClientThread>();
	ServerSocket ss=null;// �������� ���ӽ� ó�� (��ȯ ����)
	
	static int delIndex;
	
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
		BufferedReader in;	// client��û���� �о�´�
		OutputStream out;	//client�� ������� �����Ҷ� 
		
		
		
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

					case Function.CLIENTEXIT:
					{
						System.out.println("���� �޽��� ����");
						String exitMsg="���� �������ϴ�.";
						s.close();
						messageAll(Function.CLIENTEXIT+"|"+id+exitMsg);
						System.out.println("test4");
						messageAll(Function.DELROW+"|"+delIndex);
						System.out.println("delIndex->"+delIndex);
						waitVc.remove(delIndex);
						
						interrupt();
						System.out.println("test2");

					}
					break;
					case Function.LOGIN:				//client�� �α��� ��ư�� ��û���� ��
					{
						id=st.nextToken();
						//name=st.nextToken();
						//sex=st.nextToken();
						pos="����";
						// messageAll(Function.LOGIN+"|"+id+"|"+name+"|"+sex+"|"+pos);
						messageAll(Function.LOGIN+"|"+id+"|"+pos);
						waitVc.addElement(this);
						messageTo(Function.MYLOG+"|"+id+"|"+pos);
						for(ClientThread client:waitVc)			//�ٸ� ����ڵ鿡�� ���� ������ ����Ʈ�� �ø�
						{
							messageTo(Function.LOGIN+"|"+client.id+"|"+client.pos);
						}
						//messageTo(Function.MAKEROOM2+"|"+roomName+"|"+num+"|"+"���Ӵ����");
						// ������ ���� 
					}
					break;

					case Function.WAITCHAT1:			//client�� ä�������� ��û���� ��(waitroom)
					{
						String data=st.nextToken();
						messageAll(Function.WAITCHAT1+"|["+id+"]"+data);
					}
					break;


					case Function.WAITCHAT2:			//client�� ä�������� ��û���� ��(gamewindow)
					{
						String data=st.nextToken();
						messageAll(Function.WAITCHAT2+"|["+id+"]"+data);
					}
					break;

					case Function.IDCHECK:			//client�� ID�ߺ�üũ�� ��û���� ��

					{
						System.out.println("ID�ߺ�üũ");
						String id=st.nextToken();
						System.out.println(id);
						/*ID �ߺ� üũ ������*/
					}
					break;
					case Function.SUCCESSJOIN:
					{
						System.out.println("ȸ�� ���� �߰�");
						String name=st.nextToken();
						String id=st.nextToken();
						String pass=st.nextToken();
					}
					break;
					/*ȸ������*/
					
					case Function.MAKEROOM:					//�游��� Ȯ�ι�ư ������ ��
					{
						String roomName=st.nextToken();
						String num=st.nextToken();
						String pos="���ӷ�";
						messageTo(Function.MAKEROOM+"|"+id+"|"+roomName+"|"+num+"|"+pos);	//id,���̸�,�ο�,���� //promptâ�� ���				
						messageAll(Function.MAKEROOM2+"|"+roomName+"|"+num+"|"+"���Ӵ����");
					}
					break;
					
					
					}
				}catch(Exception ex)
				{
					/*���ӵǾ��ִ� Client ���� �����*/
					//System.out.println("test");
					interrupt();
					//System.out.println("test9");
				}
			}
		}

		// ���������� client���� �޼��� ����
		public synchronized void messageTo(String msg, int num)
		{
			System.out.println("messageTo-"+num);
			try
			{
				out.write((msg+"\n").getBytes());
			}catch(Exception ex)
			{
				System.out.println(num +"����1");
				delIndex=num;
				System.out.println(num +"����2");
			}
		}
		public synchronized void messageTo(String msg)
		{
			try
			{
				out.write((msg+"\n").getBytes());
			}catch(Exception ex){	}
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
/*ȸ������*/
/*ȸ������*/



