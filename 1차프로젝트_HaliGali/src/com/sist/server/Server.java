package com.sist.server;
import java.util.*;
import com.sist.common.Function;
import com.sist.server.Server.ClientThread;

import java.io.*;
import java.net.*;

class GameRoom		//���ӷ� ���� Ŭ���� 
{

	int roomNum;		//���ȣ
	String sCapaNum;	//
	int capaNum;		//�� �� �ִ� �ִ��ο�
	int humanNum;		//�����ο�
	String name;		//���̸�
	ClientThread cliT[] = new ClientThread[4];	//�濡�ִ� �÷��̾��
	int readyNum=0;		//�غ� ���� �ο�
	String Type;
	String nnum;
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
		int clientroomNumber;		//client�� �ִ� �� ��ȣ

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
							messageTo(Function.ROOMINFORM+"|"+room.Type+"|"+room.name+"|"+room.nnum+"|"+room.sCapaNum+"|"+"���Ӵ����");
						}
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
						String data=st.nextToken();
						messageRoom(Function.ROOMCHAT+"|["+id+"]"+data,clientroomNumber);
						//messageRoom(Function.ROOMCHAT+"|"+id+"���� �����Ͽ����ϴ�",roomNumber);
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
						pos="���ӹ�";
						String roomType=st.nextToken();		//���� ���� ���ӷ��� ��������
						String roomName=st.nextToken();		//���� ���� ���ӷ��� �̸�
						String nowNum=st.nextToken();		//���� ���� ���ӷ��� �������
						String capaNumImsi=st.nextToken();	//���� ���� ���ӷ���  �����ο���(2���϶�->2)
						

						GameRoom gr=new GameRoom();   	//���ӷ� Ŭ���� ����!(�ӽ÷� �ޱ�)

						//gr.sCapaNum=capaNum;			//���� ���� ���ӷ��� �����ο��� ����(String����)
	
						gr.Type=roomType;				//���� ���� ���ӷ��� �������� ����
						gr.nnum=nowNum;					//���� ���� ���ӷ��� �����ο� ����
						gr.sCapaNum=capaNumImsi;			//���� ���� ���ӷ��� �����ο��� ����
						System.out.println("-->"+capaNumImsi);
						if(capaNumImsi.equals("2��"))	//2.�游���ڸ��� �ִ��ο��� ����
						{
							gr.capaNum=2;
						}
						else if(capaNumImsi.equals("3��"))
						{
							gr.capaNum=3;
						}else 
						{
							gr.capaNum=4;
						}
						
						String pos="���ӹ�";
						int i=0;
						gr.name=roomName;					//1.���� ���� ���ӷ��� ���̸� ����
						for(GameRoom room:gameRoom)			//���� �ִ� �氹�� ���� (1����������ϱ� 1��????)
						{
							i++;
						}
						gr.roomNum=i;					//���� ���� ���ӷ��� ���ȣ ����(1����������ϱ� 1��????)
						clientroomNumber=i;				//client�� �ִ� �� ��ȣ ����(1��????)
						gameRoom.addElement(gr);		//���ӷ� ����Ʈ�� ���� ���� ���ӷ� �߰�
						
						System.out.println("�� ��ȣ�� :"+i);
						//
						gr.humanNum++;			//�����ο��� +1(2����� ������� �� �����ο���=1)
						System.out.println("�� ��ȣ�� :"+i);		//0��??????????	
						gr.cliT[0]=this;		//���ӷ�  0��° Ŭ���̾�Ʈ�� ������ �߰�
						System.out.println("������ id���"+gr.cliT[0].id+",�ִ��ο���:"+gr.capaNum+"�����ο���:"+gr.humanNum);//������id���
						//
						gr.cliT[0]=this;
						System.out.println("capa"+gr.capaNum);
						messageTo(Function.MAKEROOM+"|"+id+"|"+roomType+"|"+roomName+"|"+nowNum+"|"+gr.capaNum+"|"+pos);	//���� ���� ������Ը�			
						messageAll(Function.ROOMINFORM+"|"+roomType+"|"+roomName+"|"+nowNum+"|"+gr.capaNum+"|"+"���Ӵ����");	//��ο���
						
						/*[�������º���] ->*/
						int userRow=0;
						for(ClientThread client:waitVc)
						{
							if((waitVc.get(userRow).id).equals(id))
								break;
							userRow++;
						} //���° �������� �ľ��Ͽ� List ��  ���� �ʿ��� Row �� �˾Ƴ��� ����
						messageAll(Function.CHGUSERPOS+"|"+userRow+"|"+pos);
						/*<- [�������º���]*/
					}
					break;
					
					case Function.JOINROOM:								//���ӷ뿡 ����
					{

						String roomNum=st.nextToken();					//���ӷ��ȣ(0���� ����)
						clientroomNumber=Integer.parseInt(roomNum);		//client�� �ִ� ���ӷ��ȣ �ο�
						int roomCapa=gameRoom.get(clientroomNumber).capaNum;		//���ӷ��� �ִ��ο�(2���� ������ ��  2)
						int humNum=gameRoom.get(clientroomNumber).humanNum;			//���ӷ��� �����ο�(2���� ������ �� ��������ڸ��� �����ο��� 1�̵�)

						String decision="FALSE";
						if(humNum<roomCapa)										//���� �� ���� �ʾ��� ���(1<2)
						{

							gameRoom.get(clientroomNumber).humanNum++;			//�ش� �� ��ȣ�� �����ο�+1(2���϶� 2�� ��!!)
							humNum=gameRoom.get(clientroomNumber).humanNum;		//�ش�濡 ������ �����ο� ������Ʈ
							pos="���ӹ�";

							gameRoom.get(clientroomNumber).cliT[humNum-1]=this;			//(2��°���-> 1�� ��)
							decision="TRUE";
							
							//
							System.out.println("1111->"+gameRoom.get(clientroomNumber).cliT[0].id);
							System.out.println("2222->"+gameRoom.get(clientroomNumber).cliT[1].id);
							System.out.println("�ִ��ο���:"+gameRoom.get(clientroomNumber).capaNum+"�����ο���:"+gameRoom.get(clientroomNumber).humanNum);
							//	
							messageTo(Function.JOINROOM+"|"+decision);		//������ ������� true�Ѱ���
							messageRoom(Function.ROOMCHAT+"|"+id+"���� �����Ͽ����ϴ�",clientroomNumber);	//��ο��� ��� ���� �˸���,���ȣ 
							
							/*[���ο����� -�߰���] ->*/
							messageAll(Function.CHGROOMUSER+"|"+roomNum+"|"+humNum); //���ο��� ����� ��ID(table�� Row)+����� User���� Client�� ����
							/*<- [���ο����� -�߰���]*/
							/*[�������º���] ->*/
							int userRow=0;
							for(ClientThread client:waitVc)
							{
								if((waitVc.get(userRow).id).equals(id))
									break;
								userRow++;
							} //���° �������� �ľ��Ͽ� List ��  ���� �ʿ��� Row �� �˾Ƴ��� ����
							messageAll(Function.CHGUSERPOS+"|"+userRow+"|"+pos);
							/*<- [�������º���]*/
						}
						else 				//�� ��á�� ���
						{

							messageTo(Function.JOINROOM+"|"+decision);		//������ ������� false�Ѱ���
							clientroomNumber=-1;
						}
					}
					break;
					case Function.ROOMREADY:			//�غ��ư ������ ��
					{
						System.out.println("����濡�ִ»����"+gameRoom.get(clientroomNumber).humanNum);		//1���� �� humanNum=1
						for(int j=0; j<gameRoom.get(clientroomNumber).humanNum; j++){			//�� ���ӹ��� ���� �ο��� ��ŭ ������
								if((gameRoom.get(clientroomNumber).cliT[j].id).equals(id)){
									System.out.println(id+"�غ�");
									messageRoom(Function.ROOMCHAT+"|"+"["+id+"]"+"�� �غ�Ϸ�",gameRoom.get(clientroomNumber).roomNum);	//���ӷ�ä�ù濡 �Ѹ���
									
									gameRoom.get(clientroomNumber).readyNum++;			//readyNum(�غ��ѻ����)=1(1�����غ����� ��)
									System.out.println("�غ��� ��� ��:"+gameRoom.get(clientroomNumber).readyNum+"\n");
									return;
								}
						}
					}
					break;
					/*[�泪����] ->*/
					case Function.EXITROOM:
					{
						pos="����";
						System.out.println("humanNum=> "+gameRoom.get(clientroomNumber).humanNum);
						
						gameRoom.get(clientroomNumber).humanNum--; //��� ���� ��Ŵ
						int humNum=gameRoom.get(clientroomNumber).humanNum;
								
						System.out.println("humanNum=> "+gameRoom.get(clientroomNumber).humanNum);
						
						messageTo(Function.MYLOG+"|"+id+"|"+pos); 			//���� ȭ������ �ٲٱ� ����
						messageAll(Function.CHGROOMUSER+"|"+clientroomNumber+"|"+humNum);
						
						int userRow=0;
						for(ClientThread client:waitVc)
						{
							if((waitVc.get(userRow).id).equals(id))
								break;
							userRow++;
						} //���° �������� �ľ��Ͽ� List ��  ���� �ʿ��� Row �� �˾Ƴ��� ����
						messageAll(Function.CHGUSERPOS+"|"+userRow+"|"+pos); //Ÿ ������ ������ �����ϵ���
						messageRoom(Function.ROOMCHAT+"|"+id+"���� �����Ͽ����ϴ�",clientroomNumber);	//�������� ����޽��� ������
						
						if(gameRoom.get(clientroomNumber).humanNum<=0) //�濡 ���� ����� ������
						{
							messageAll(Function.DELROOM+"|"+clientroomNumber+"|");
							//���Ϳ��� ����
							gameRoom.removeElementAt(clientroomNumber);
						}
						else
						{
							//�泪�� Ŭ���̾�Ʈ�� �迭���� �����ϰ� �� Ŭ���̾�Ʈ�� ������ ����
							int userCount= gameRoom.get(clientroomNumber).humanNum;

							for(int i=0; i<=userCount; i++) //�濡�� ���� Thread �� Room Vector�� �迭���� �����ϱ� ����
							{
								System.out.println("I��------>"+i);
								if((gameRoom.get(clientroomNumber).cliT[i].id).equals(id))
								{
									System.out.println("������ I��------>"+i);
									if(i==userCount)
									{
										gameRoom.get(clientroomNumber).cliT[i]=null;
										break;
									}
									else
									{
										for(int j=userCount;j>i;j--)
										{
											System.out.println("J��------>"+j);
											gameRoom.get(clientroomNumber).cliT[j-1]
													=gameRoom.get(clientroomNumber).cliT[j];
										}
										break;
									}
								}
							}
						}
					}
					break;
					/*<- [�泪����]*/
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
		
		public synchronized void messageRoom(String msg, int roomIndex)		//�ش� ���� ����鿡�� �޼��� ����(�޼���,���ȣ)
		{
			for(int i=0; i<gameRoom.get(roomIndex).humanNum;i++)			//�ش� ���� ����鿡�� �޼��� ���� /*bugfix '<=gameRoom.get(roomIndex)'�� �ȵ�*/
			{
				gameRoom.get(roomIndex).cliT[i].messageTo(msg);
			}
		}
	}

}
/*ȸ������*/
