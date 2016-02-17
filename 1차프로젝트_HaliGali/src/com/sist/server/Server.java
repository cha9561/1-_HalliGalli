package com.sist.server;
import java.util.*;
import com.sist.common.Function;
import java.io.*;
import java.net.*;

public class Server implements Runnable{

	Vector<ClientThread> waitVc=
			new Vector<ClientThread>();
	ServerSocket ss=null;// �������� ���ӽ� ó�� (��ȯ ����)
	/*ȸ������*/
	FileIO cliFile=new FileIO();
	/*ȸ������*/

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
    	while(true)
    	{
	    	try
	    	{
	    		// Ŭ���̾�Ʈ�� ���� => ip,port(Socket)
	    		Socket s=ss.accept();
	    		// s => client
	    		ClientThread ct=new ClientThread(s);
	    		ct.start();// ��� ���� 
	    		
	    	}catch(Exception ex){}
    	}
    	
    }
    
	public static void main(String[] args) {
        // ���� ����
		Server server=new Server();
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
			try
			{
				this.s=s;
				in=new BufferedReader(new InputStreamReader(s.getInputStream()));
				out=s.getOutputStream();
			}catch(Exception ex){}
		}
		
		public void run()			//2.client�� server���� ����� ó�� 
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
						 for(ClientThread client:waitVc)
						 {
							 messageTo(Function.LOGIN+"|"+client.id+"|"+client.pos);
						 }
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
						  boolean ck=cliFile.searchId(id);
						  /*ID �ߺ� üũ ������*/
						  if(ck==true) //ID �� ������
							 messageTo(Function.NOTOVERLAP+"|");
						  else //ID �� ������
							  messageTo(Function.OVERLAP+"|");
					  }
					  break;
					  case Function.SUCCESSJOIN:
					  {
						  System.out.println("ȸ�� ���� �߰�");
						  String name=st.nextToken();
						  String id=st.nextToken();
						  String pass=st.nextToken();
						  cliFile.insertCli(name+","+id+","+pass+"\n");
					  }
					  /*ȸ������*/
					}
				}catch(Exception ex){}
			}
		}
		// ���������� client���� �޼��� ����
		public synchronized void messageTo(String msg)
		{
			try
			{
				out.write((msg+"\n").getBytes());
			}catch(Exception ex){}
		}
		// ��ü������ client���� �޼��� ����
		public synchronized void messageAll(String msg)
		{
			for(ClientThread client:waitVc)
			{
				client.messageTo(msg);
			}
		}
	}

}
/*ȸ������*/
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
				String head="�̸�,ID,Password,ĳ����,��,��,�·�\n";
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
					System.out.println("ID�ߺ� �߻�");
					return false;
				}
			}
			//return true;
		}catch(Exception ex){}
		System.out.println("ID�ߺ� ����");
		return true;
	}
	void insertCli(String data)
	{
		System.out.println("�߰�");
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
/*ȸ������*/



