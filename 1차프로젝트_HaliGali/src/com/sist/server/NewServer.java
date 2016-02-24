package com.sist.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.Vector;

import com.sist.common.Function;
import com.sist.server.NewServer.ClientThread;
//import com.sist.server.Server.ClientThread;

class Room
{
	Vector<ClientThread> inRoomVc=new Vector<ClientThread>();
	int capaNum;		//�� �� �ִ� �ִ��ο� 2~4
	int preNum;			//���� �ο� 1~5
	String name;		//���̸�
	int readyNum;		//�غ� ���� �ο�
	String Type;		//���� �����
	String pos;
}
public class NewServer implements Runnable{
	Vector<ClientThread> waitVc=new Vector<ClientThread>();		//����� �迭
	Vector<Room> roomVc=new Vector<Room>();
	
	ServerSocket ss=null;// �������� ���ӽ� ó�� (��ȯ ����)
	public NewServer()
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
		// TODO Auto-generated method stub
		Server server=new Server();		        // ���� ����
		new Thread(server).start();
	}
	
	class ClientThread extends Thread
	{
		String id,posUser;		//id,�̸�,����,����
		int myIndex; //0���� ���� ù��° ������ 0��...
		Socket s;
		BufferedReader in;	// client��û���� �о�´�
		OutputStream out;	//client�� ������� �����Ҷ� 
		int myRoomIndex=-1;		//client�� �ִ� �� ��ȣ, 0���� ����
		
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
						case Function.LOGIN:
						{
							/*�α��� ����ó��*/
							System.out.println("IN-LOGIN");
							id=st.nextToken();
							posUser="����";
							messageAll(Function.LOGIN+"|"+id+"|"+posUser);
							waitVc.addElement(this);
							messageTo(Function.MYLOG+"|"+id+"|"+posUser);
							
							int i=0;
							for(ClientThread client:waitVc)			//�ٸ� ����ڵ鿡�� ���� ������ ����Ʈ�� �ø�
							{
								i++;
								messageTo(Function.LOGIN+"|"+client.id+"|"+client.posUser);
							}
							myIndex=i-1;  //myIndex�� 0���� �����ϹǷ�
							
							for(Room room:roomVc)			//���� �ִ� �氹�� ���� (1����������ϱ� 1��????)
							{
								messageTo(Function.ROOMINFORM+"|"+room.Type+"|"+room.name+
										"|"+room.preNum+"|"+room.capaNum+"|"+room.pos);
							}
						} 
						break;
						case Function.CLIENTEXIT:
						{
							/*Ŭ���̾�Ʈ ���������*/
							System.out.println("IN-CLIENTEXIT");
							String exitMsg="���� �������ϴ�.";
							s.close();
							messageAll(Function.CLIENTEXIT+"|"+id+exitMsg);		//ä��â�� 00���� �����̽��ϴ� �Ѹ��� 
							messageAll(Function.DELROW+"|"+myIndex);
							
							/*��� ���������� �ľ�*/
							int i=preTotalUserCount(); //���� ������ user 1��->1
							/*���� Ŭ���̾�Ʈ ���� Ŭ���̾�Ʈ���� myIndex�� ���ҽ����ش�*/
							for(int j=(myIndex+1); j<(i-1); j++)
							{
								(waitVc.get(j).myIndex)--;
							}
							/*���Ϳ��� ����*/
							waitVc.remove(myIndex);
							interrupt();
						}
						break;
						case Function.WAITCHAT1:
						{
							/*Ŭ���̾�Ʈ ���� ä��*/
							System.out.println("IN-WAITCHAT1");
							String data=st.nextToken();
							messageAll(Function.WAITCHAT1+"|["+id+"]"+data);
						}
						break;
						case Function.IDCHECK:
						{
							/*ID �ߺ�üũ ��û��*/
							System.out.println("ID�ߺ�üũ");
							String id=st.nextToken();
							System.out.println(id);
							/*ID �ߺ� üũ ������*/
						}
						break;
						case Function.SUCCESSJOIN:
						{
							/*ȸ�� ���� ����*/
							System.out.println("ȸ�� ���� �߰�");
							String name=st.nextToken();
							String id=st.nextToken();
							String pass=st.nextToken();
						}
						break;
						/*���ӹ� ����*/
						case Function.MAKEROOM:
						{
							/*�游���*/
							System.out.println("IN-MAKEROOM");
							String roomType=st.nextToken();		//���� ���� ���ӷ��� ��������
							String roomName=st.nextToken();		//���� ���� ���ӷ��� ���̸�
							String capaNumImsi=st.nextToken();	//���� ���� ���ӷ��� �ִ��ο�(2���϶�->2)
							
							Room room=new Room();
							room.pos="���Ӵ����";				//���ӷ��� ��������
							room.Type=roomType;				//���� ���� ���ӷ��� �������� ����
							room.name=roomName;				//���� ���� ���ӷ��� ���̸� ����
							
							if(capaNumImsi.equals("2��"))	//2.�游���ڸ��� �ִ��ο��� ����
							{
								room.capaNum=2;				
							}
							else if(capaNumImsi.equals("3��"))
							{
								room.capaNum=3;
							}else 
							{
								room.capaNum=4;
							}
							posUser="���ӷ�";
							int roomCount=preRoomCount(); //���� ��� �ľ� 1�� -> 1
							myRoomIndex=roomCount;
							System.out.println(id+"�� ���ȣ��(0���� ����):"+myRoomIndex);
							room.preNum=1;
							room.inRoomVc.addElement(this);
							messageTo(Function.MAKEROOM+"|"+id+"|"+roomName+"|"+room.preNum+"|"+room.capaNum);//1.gameâ���� ��ȯ
							messageAll(Function.ROOMINFORM+"|"+roomType+"|"+roomName+"|"+room.preNum+"|"+room.capaNum+"|"+room.pos);//2.���Ͽ� ���
							
							/*���� ���� ����*/
							int tmpMyRow=0;
							for(ClientThread client:waitVc)
							{
								if((waitVc.get(tmpMyRow).id).equals(id))
									break;
								tmpMyRow++;
							} //���° �������� �ľ��Ͽ� List ��  ���� �ʿ��� Row �� �˾Ƴ��� ����
							messageAll(Function.CHGUSERPOS+"|"+tmpMyRow+"|"+posUser);
						}
						break;
						case Function.JOINROOM:
						{
							/*�����*/
							System.out.println("IN-JOINROOM");
							String tmpRoomIndex=st.nextToken();	//0�� ���� ����
							int tmpIndex=(Integer.parseInt(tmpRoomIndex));
							
							int tmpRoomCapa=roomVc.get(tmpIndex).capaNum;
							int tmpPreNum=roomVc.get(tmpIndex).preNum;
							String decision="FALSE"; 
							if(tmpPreNum<tmpRoomCapa)
							{
								(roomVc.get(tmpIndex).preNum)++;
								tmpPreNum=roomVc.get(tmpIndex).preNum;
								posUser="���ӷ�";
								roomVc.get(tmpIndex).inRoomVc.addElement(this);
								decision="TRUE";
								String tmpMakerId=roomVc.get(tmpIndex).inRoomVc.get(0).id;
								String tmpRoomName=roomVc.get(tmpIndex).name;
								messageTo(Function.JOINROOM+"|"+decision+"|"+tmpMakerId+"|"+tmpRoomName);		//������ ������� true�Ѱ���
								
								messageRoom(Function.ROOMCHAT+"|"+id+"���� �����Ͽ����ϴ�",tmpIndex);
								myRoomIndex=tmpIndex;
								messageAll(Function.CHGROOMUSER+"|"+myRoomIndex+"|"+tmpPreNum);
								
								messageAll(Function.CHGUSERPOS+"|"+myIndex+"|"+posUser);
							}
							else //�����ʰ�
							{
								messageTo(Function.JOINROOM+"|"+decision);		//������ ������� false�Ѱ���
								System.out.println("second");
							}
						}
						break;
						case Function.ROOMCHAT:
						{
							/*Ŭ���̾�Ʈ ���ӷ� ä��*/
							System.out.println("IN-ROOMCHAT");
							String data=st.nextToken();
							messageRoom(Function.ROOMCHAT+"|["+id+"]"+data,myRoomIndex);
						}
						break;
						case Function.ROOMREADY:
						{
							/*�غ� ����*/
							System.out.println("IN-ROOMREADY");
							int tmpUserNum=roomVc.get(myRoomIndex).preNum;
							int maxUsetNum=roomVc.get(myRoomIndex).capaNum;
							roomVc.get(myRoomIndex).readyNum++;
							System.out.println("����濡�ִ»����"+tmpUserNum+","+id+"�� �غ�, �غ��� ��� ��:"
									+roomVc.get(myRoomIndex).readyNum);		//1���� �� humanNum=1
							if(roomVc.get(myRoomIndex).readyNum>=maxUsetNum)
							{
								roomVc.get(myRoomIndex).inRoomVc.get(0).messageTo(Function.ROOMREADYBUTTON+"|");
								messageTo(Function.ROOMREADY+"|");
							}
							else//���ready�� ���� �ʾ����� ���� ready�ϸ�
							{
								messageTo(Function.ROOMREADY+"|");	
							}
							messageRoom(Function.ROOMCHAT+"|"+"["+id+"]"+"�� �غ�Ϸ�",myRoomIndex);	//���ӷ�ä�ù濡 �Ѹ���
						}
						break;
						case Function.ROOMSTART:
						{
							/*���� ����*/
							System.out.println("IN-ROOMSTART");
							//����� �ٲٱ�
							roomVc.get(myRoomIndex).pos="������";
							String tmpRoomPos=roomVc.get(myRoomIndex).pos;
							//���� ���ӻ��¸� ���������� �ٲٱ�
							messageAll(Function.CHGROOMSTATE+"|"+myRoomIndex+"|"+tmpRoomPos); //�������̶�� ǥ��
							//�泻 ���ӽ�ŸƮ �޽��� ������
							messageRoom(Function.ROOMCHAT+"|"+"���� START", myRoomIndex);
							int tmpInRoomUser=roomVc.get(myRoomIndex).preNum;
							for(int i=0; i<tmpInRoomUser; i++) //�������� ���� �����ؼ� �����ֱ�
							{
								roomVc.get(myRoomIndex).inRoomVc.get(i).messageTo(Function.TURNINFO+"|"+"����� "+(i+1)+"��° �Դϴ�");
								
							}
							//���� ������ ���Ӱ��� ó�� �߰��� �ڸ�
							
						}
						break;
						case Function.EXITROOM:
						{
							/*����� ����*/
							System.out.println("IN->EXITROOM");
							posUser="����";
							System.out.println("EXITROOM Number:"+myRoomIndex);
							//�濡 �����ο� ���ҽ���
							System.out.println(myRoomIndex+"1.���� ���� �ο���:"+roomVc.get(myRoomIndex).preNum);
							(roomVc.get(myRoomIndex).capaNum)--;
							int tmpUserCount=roomVc.get(myRoomIndex).preNum; //���� �� �ο� �ӽ� ����
							System.out.println(myRoomIndex+"2.���� ���� �ο���:"+roomVc.get(myRoomIndex).preNum);
							//���Ƿ� �ٲ���
							messageTo(Function.MYLOG+"|"+id+"|"+posUser); 	
							messageAll(Function.CHGROOMUSER+"|"+myRoomIndex+"|"+tmpUserCount);
							
							int myRow=0;
							for(ClientThread client:waitVc)
							{
								if((waitVc.get(myRow).id).equals(id)) //i��° id�� �� id �� ������.
									break;
								myRow++;
							}
							//���� row(���° ����)�� ���� �ش� row�� ���¸� �����ϵ��� Ŭ���̾�Ʈ�� ����
							messageAll(Function.CHGUSERPOS+"|"+myRow+"|"+posUser);
							messageRoom(Function.ROOMCHAT+"|"+id+"���� �����Ͽ����ϴ�.",myRoomIndex);
							//���� �� ���� �ľ�
							int tmpRoomCount=preRoomCount();
							
							if(tmpUserCount<=0) //�濡 ���� ����� ������
							{
								messageAll(Function.DELROOM+"|"+myRoomIndex);
								System.out.println("���� �� Index :"+myRoomIndex);
								
								/*���� �ڿ��ִ� Ŭ���̾�Ʈ �������� myRoomIndex�� ���ҽ��������*/
								for(int i=(myRoomIndex+1);i<tmpRoomCount;i++)
								{
									int tmpInUser=roomVc.get(i).preNum;
									for(int j=0; j<tmpInUser; j++)
									{
										(roomVc.get(i).inRoomVc.get(j).myIndex)--;
									}
								}
								System.out.println("roomVc ���� �ش� �� ����");
								roomVc.remove(myRoomIndex);
							}
							else //�濡 ���� ����� ������
							{
								roomVc.get(myIndex).inRoomVc.remove(this);
							}
							myRoomIndex=-1; //�ƹ��濡�� ������ ���� ������
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
		int preTotalUserCount()
		{
			int i=0;
			for(ClientThread client:waitVc)
			{
				i++;
			}
			return i;
		}
		int preRoomCount()
		{
			int i=0;
			for(Room room:roomVc)			//���� �ִ� �氹�� ���� (1����������ϱ� 1��????)
			{
				i++;
			}
			return i;
		}
		public synchronized void messageRoom(String msg, int roomIndex)
		{
			for(ClientThread client:roomVc.get(roomIndex).inRoomVc)
			{
				client.messageTo(msg);
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
