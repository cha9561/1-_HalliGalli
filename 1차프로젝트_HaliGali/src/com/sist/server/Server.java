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
	String Type;		//���������
	String pos;			//����
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
		String id,name,sex,posUser;		//id,�̸�,����,����
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
						messageAll(Function.CLIENTEXIT+"|"+id+exitMsg);		//ä��â�� 00���� �����̽��ϴ� �Ѹ��� 
						messageAll(Function.DELROW+"|"+delIndex);			//
						
						waitVc.remove(delIndex);
						
						interrupt();				
					}
					break;
					case Function.LOGIN:				//client�� �α��� ��ư�� ��û���� ��
					{
						id=st.nextToken();
						//name=st.nextToken();
						//sex=st.nextToken();
						posUser="����";
						// messageAll(Function.LOGIN+"|"+id+"|"+name+"|"+sex+"|"+pos);
						messageAll(Function.LOGIN+"|"+id+"|"+posUser);
						waitVc.addElement(this);
						messageTo(Function.MYLOG+"|"+id+"|"+posUser);
						for(ClientThread client:waitVc)			//�ٸ� ����ڵ鿡�� ���� ������ ����Ʈ�� �ø�
						{
							messageTo(Function.LOGIN+"|"+client.id+"|"+client.posUser);
						}
						for(GameRoom room:gameRoom)
						{
							messageTo(Function.ROOMINFORM+"|"+room.Type+"|"+room.name+"|"+room.humanNum+"|"+room.sCapaNum+"|"+room.pos);
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
					
					case Function.MAKEROOM:					//�游��� Ȯ�ι�ư ������ ��////////////////////////////��ģ��1
					{
						
						String roomType=st.nextToken();		//���� ���� ���ӷ��� ��������
						String roomName=st.nextToken();		//���� ���� ���ӷ��� ���̸�
						//String nowNum=st.nextToken();		//���� ���� ���ӷ��� �����ο�
						String capaNumImsi=st.nextToken();	//���� ���� ���ӷ��� �ִ��ο�(2���϶�->2)

						GameRoom gr=new GameRoom();   	//���ӷ� Ŭ���� ����!(�ӽ÷� �ޱ�)
						gr.pos="���Ӵ����";				//���ӷ��� ��������
						gr.Type=roomType;				//���� ���� ���ӷ��� �������� ����
						gr.name=roomName;				//���� ���� ���ӷ��� ���̸� ����
						gr.sCapaNum=capaNumImsi;		//���� ���� ���ӷ��� �ִ��ο� ����

						if(capaNumImsi.equals("2��"))	//2.�游���ڸ��� �ִ��ο��� ����
						{
							gr.capaNum=2;				//sCapaNumr(string)�� ������ �� ����
						}
						else if(capaNumImsi.equals("3��"))
						{
							gr.capaNum=3;
						}else 
						{
							gr.capaNum=4;
						}
						int i=0;		
						posUser="���ӷ�";

						for(GameRoom room:gameRoom)			//���� �ִ� �氹�� ���� (1����������ϱ� 1��????)
						{
							i++;
						}						
						gr.roomNum=i;					//gameroom ����=>���� ���� ���ӷ��� ���ȣ ����(1����������ϱ� 1��????)
						clientroomNumber=i;				//client ����=>client�� �ִ� �� ��ȣ ����(1��????)
						gameRoom.addElement(gr);		//���ӷ� ����Ʈ�� ���� ���� ���ӷ� �߰�
						gr.humanNum=1;			//�����ο���=1 dafault�� �ֱ�
						gr.cliT[0]=this;		//client�� ������ �߰�==����
						System.out.println("�� ��ȣ:"+gr.roomNum+",������ id:"+gr.cliT[0].id+",�ִ��ο���:"+gr.capaNum
								+"�����ο���:"+gr.humanNum+",��������:"+gr.Type);
						
						messageTo(Function.MAKEROOM+"|"+id+"|"+roomName+"|"+gr.humanNum+"|"+gr.capaNum);//1.gameâ���� ��ȯ
						messageAll(Function.ROOMINFORM+"|"+roomType+"|"+roomName+"|"+gr.humanNum+"|"+gr.capaNum+"|"+gr.pos);//2.���Ͽ� ���
		
						/*3.[�������º���] ->*/
						int userRow=0;
						for(ClientThread client:waitVc)
						{
							if((waitVc.get(userRow).id).equals(id))
								break;
							userRow++;
						} //���° �������� �ľ��Ͽ� List ��  ���� �ʿ��� Row �� �˾Ƴ��� ����
						messageAll(Function.CHGUSERPOS+"|"+userRow+"|"+posUser);
					}
					break;

					
					case Function.JOINROOM:								//���ӷ뿡 ����
					{
						String roomNum=st.nextToken();					//���ӷ��ȣ(0���� ����)
						clientroomNumber=Integer.parseInt(roomNum);		//client�� �ִ� ���ӷ��ȣ �ο�
						int roomCapa=gameRoom.get(clientroomNumber).capaNum;		//���ӷ��� �ִ��ο�(2���� ������ ��  2)
						int humNum=gameRoom.get(clientroomNumber).humanNum;			//���ӷ��� �����ο�(��������ڸ��� �����ο��� 1)

						String decision="FALSE";
						if(humNum<roomCapa)										//���� �� ���� �ʾ��� ���(1<2)
						{
							gameRoom.get(clientroomNumber).humanNum++;			//�ش� �� ��ȣ�� �����ο�+1(2���϶� 2�� ��!!)
							humNum=gameRoom.get(clientroomNumber).humanNum;		//�ش�濡 ������ �����ο� ������Ʈ
							posUser="���ӷ�";
							gameRoom.get(clientroomNumber).cliT[humNum-1]=this;			//(2��°���-> 1�� ��)
							decision="TRUE";				
							//
							System.out.println("cliT[0].id->"+gameRoom.get(clientroomNumber).cliT[0].id);
							System.out.println("cliT[1].id->"+gameRoom.get(clientroomNumber).cliT[1].id);
							System.out.println("�ִ��ο���:"+roomCapa+"�����ο���:"+humNum);
							//	
					
							messageTo(Function.JOINROOM+"|"+decision+"|"+gameRoom.get(clientroomNumber).cliT[0].id+"|"+gameRoom.get(clientroomNumber).name);		//������ ������� true�Ѱ���
							//messageTo(Function.JOINROOM+"|"+decision);		//������ ������� true�Ѱ���
							messageRoom(Function.ROOMCHAT+"|"+id+"���� �����Ͽ����ϴ�",clientroomNumber);	//��ο��� ��� ���� �˸���,���ȣ 
							
							/*[���ο����� -�߰���] ->*/
							messageAll(Function.CHGROOMUSER+"|"+roomNum+"|"+humNum); //���ο��� ����� ��ID(table�� Row)+����� User���� Client�� ����
							/*[�������º���] ->*/
							int userRow=0;
							for(ClientThread client:waitVc)
							{
								if((waitVc.get(userRow).id).equals(id))
									break;
								userRow++;
							} //���° �������� �ľ��Ͽ� List ��  ���� �ʿ��� Row �� �˾Ƴ��� ����
							messageAll(Function.CHGUSERPOS+"|"+userRow+"|"+posUser);
						}
						else 				//�� ��á�� ���
						{
							System.out.println("first");
							messageTo(Function.JOINROOM+"|"+decision);		//������ ������� false�Ѱ���
							System.out.println("second");
							clientroomNumber=-1;
						}
					}
					break;
					
					
					case Function.ROOMREADY:			//�غ��ư ������ ��
					{
						int humanNum=gameRoom.get(clientroomNumber).humanNum;	//�����ο�
						int roomNum=gameRoom.get(clientroomNumber).roomNum;		//���ȣ

						gameRoom.get(clientroomNumber).readyNum++;			//ex_1�����غ����� �� readyNum(�غ��ѻ����)=1
						System.out.println("����濡�ִ»����"+humanNum+","+id+"�� �غ�, �غ��� ��� ��:"
														+gameRoom.get(clientroomNumber).readyNum);		//1���� �� humanNum=1	
			
						if(humanNum==gameRoom.get(clientroomNumber).readyNum){		//������� ready �ϸ�				
							messageTest(Function.ROOMREADYBUTTON+"|",roomNum);
							messageTo(Function.ROOMREADY+"|");
						}else{														//���ready�� ���� �ʾ����� ���� ready�ϸ�
							messageTo(Function.ROOMREADY+"|");	
						}		
						messageRoom(Function.ROOMCHAT+"|"+"["+id+"]"+"�� �غ�Ϸ�",roomNum);	//���ӷ�ä�ù濡 �Ѹ���
					}
					break;

					
					case Function.ROOMSTART:			//���۹�ư ������ ��
					{
						int roomNum=gameRoom.get(clientroomNumber).roomNum;		//���ȣ
						System.out.println("������ �����մϴ�!!!");
						posUser="������";
						messageAll(Function.CHGROOMSTATE+"|"+roomNum+"|"+posUser); //�������̶�� ǥ��
						messageRoom(Function.ROOMCHAT+"|"+"���� START",roomNum);	//���ӷ�ä�ù濡 �Ѹ���
					}
					break;					

					/*[�泪����] ->*/
					case Function.EXITROOM:
					{
						posUser="����";
						System.out.println("humanNum=> "+gameRoom.get(clientroomNumber).humanNum);	//�����ο�						
						gameRoom.get(clientroomNumber).humanNum--; 					//��� ���� ��Ŵ
						int humNum=gameRoom.get(clientroomNumber).humanNum;			//��ȭ�� �����ο�
						System.out.println("humanNum=> "+gameRoom.get(clientroomNumber).humanNum);	
						
						messageTo(Function.MYLOG+"|"+id+"|"+posUser); 							//���� ȭ������ �ٲٱ� ����
						messageAll(Function.CHGROOMUSER+"|"+clientroomNumber+"|"+humNum);	//����º�ȭ��Ű�� ����
						
						int userRow=0;
						for(ClientThread client:waitVc)
						{
							if((waitVc.get(userRow).id).equals(id))
								break;
							userRow++;
						} //���° �������� �ľ��Ͽ� List ��  ���� �ʿ��� Row �� �˾Ƴ��� ����
						messageAll(Function.CHGUSERPOS+"|"+userRow+"|"+posUser); 						//Ÿ ������ ������ �����ϵ���
						messageRoom(Function.ROOMCHAT+"|"+id+"���� �����Ͽ����ϴ�",clientroomNumber);	//�������� ����޽��� ������
						
						if(gameRoom.get(clientroomNumber).humanNum<=0) //�濡 ���� ����� ������
						{
							messageAll(Function.DELROOM+"|"+clientroomNumber);	
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
		
		public synchronized void messageTest(String msg, int roomIndex)
		{
			gameRoom.get(roomIndex).cliT[0].messageTo(msg);
		}
	}

}
/*ȸ������*/
