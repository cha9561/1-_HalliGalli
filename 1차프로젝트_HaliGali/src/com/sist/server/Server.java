package com.sist.server;
import java.util.*;
import com.sist.common.Function;
import com.sist.server.Server.ClientThread;

import java.io.*;
import java.net.*;

class GameRoom		//���ӷ� ���� Ŭ���� 
{
	int roomNum;
	String sCapaNum;
	int capaNum;
	int humanNum;
	String name;
	ClientThread cliT[] = new ClientThread[3];

}
public class Server implements Runnable{

	Vector<ClientThread> waitVc=new Vector<ClientThread>();		//����� �迭
	Vector<GameRoom> gameRoom=new Vector<GameRoom>();			//���ӷ� �迭
	
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
						
						messageAll(Function.DELROW+"|"+delIndex);
						
						waitVc.remove(delIndex);
						
						interrupt();
						

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
						for(GameRoom room:gameRoom)
						{
							messageTo(Function.ROOMINFORM+"|"+room.name+"|"+room.sCapaNum+"|"+"���Ӵ����");
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


					case Function.ROOMCHAT:			//client�� ä�������� ��û���� ��(gamewindow)
					{
						/*���� �濡 ������ messageAll ������ �ʿ�*/
						String data=st.nextToken();
						messageAll(Function.ROOMCHAT+"|["+id+"]"+data);
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
						String roomName=st.nextToken();		//���� ���� ���ӷ��� �̸�
						String capaNum=st.nextToken();		//���� ���� ���ӷ���  �����ο���
						GameRoom gr=new GameRoom();   	//���ӷ� Ŭ���� ����!(�ӽ÷� �ޱ�)
						gr.sCapaNum=capaNum;			//���� ���� ���ӷ��� �����ο��� ����
						if(capaNum.equals("2��"))
						{
							gr.capaNum=1;
						}
						else if(capaNum.equals("3��"))
						{
							gr.capaNum=2;
						}else //4��
						{
							gr.capaNum=3;
						}
						String pos="���ӷ�";
						int i=0;
						gr.name=roomName;					//���� ���� ���ӷ��� ���̸� ����
						for(GameRoom room:gameRoom)			//���� �ִ� �氹�� ���� 
						{
							i++;
						}
						
						gr.roomNum=i;					//���� ���� ���ӷ��� ���ȣ ����
						gameRoom.addElement(gr);		//���ӷ� ����Ʈ�� ���� ���� ���ӷ� �߰�(��������)(��������)
						System.out.println("�� ��ȣ�� :"+i);
						
						gr.cliT[0]=this;

						messageTo(Function.MAKEROOM+"|"+id+"|"+roomName+"|"+capaNum+"|"+pos);	//���� ���� ������Ը�			
						messageAll(Function.ROOMINFORM+"|"+roomName+"|"+capaNum+"|"+"���Ӵ����");	//��ο���
					}
					break;
					case Function.JOINROOM:
					{
						String roomNum=st.nextToken();
						int roomNumber=Integer.parseInt(roomNum);
						/*�� ��� á���� �Ǻ� �ʿ�*/
						int roomCapa=gameRoom.get(roomNumber).capaNum;
						int humNum=gameRoom.get(roomNumber).humanNum;
						String decision="FALSE";
						if(humNum<roomCapa)
						{
							gameRoom.get(roomNumber).humanNum++;
							humNum=gameRoom.get(roomNumber).humanNum;
							
							gameRoom.get(roomNumber).cliT[humNum]=this;
							decision="TRUE";
							messageTo(Function.JOINROOM+"|"+decision);
							messageRoom(Function.ROOMCHAT+"|"+id+"���� �����Ͽ����ϴ�",roomNumber);
							
						}
						else //�� ����.
						{
							messageTo(Function.JOINROOM+"|"+decision);
						}
					}
					break;
					}
				}catch(Exception ex)
				{
					/*���ӵǾ��ִ� Client ���� �����*/
					interrupt();
				}
			}
		}

		// ���������� client���� �޼��� ����
		public synchronized void messageTo(String msg, int num)
		{
			try
			{
				out.write((msg+"\n").getBytes());
			}catch(Exception ex)
			{
				delIndex=num;
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
		
		public synchronized void messageRoom(String msg, int roomIndex)		// ��ü������ client���� �޼��� ����
		{
			for(int i=0; i<=gameRoom.get(roomIndex).humanNum;i++)
			{
				gameRoom.get(roomIndex).cliT[i].messageTo(msg);
			}
		}
	}

}
/*ȸ������*/
