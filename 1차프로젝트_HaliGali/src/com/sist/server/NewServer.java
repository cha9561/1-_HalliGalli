package com.sist.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
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
	
	/*���� ����*/
	int Card[] = new int[56]; // ī�� �������� ����
	int TurnCard[][] = new int[4][]; // �������� ī�带 �����ϴ� ����
	int TurnCardCount[] = new int[4]; // �������� ī���� ����
	int CardType[] = new int[4]; // ī���� ������ �˱����� ����
	int CardNum[] = new int[4]; // ī��� ���� ���� �˱����� ����
	int ClientCard[][] = new int[4][]; // Ŭ���̾�Ʈ ī�� ����
	int ClientCardCount[] = new int[4]; // Ŭ���̾�Ʈ ī�� ���� ����
	int NowPlayer; // ���� ���ʰ� �������� ����
	boolean isSuccess = false; // ��ġ�⿡ �����ߴ��� Ȯ��
	boolean dead[] = new boolean[4]; // �׾����� ��Ҵ��� Ȯ��
	boolean EndGame = false; // ������ ������ Ȯ��.
	boolean isBell = false; // ������ ���� �ƴ��� Ȯ��
	String Player[] = new String[4]; // �÷��̾��̸� ���Ӽ������ ����
	Random rnd = new Random();
	
	public void GameInit() // ���� �ʱ�ȭ
	   {
	      for (int i = 0; i < 4; i++) {
	         dead[i] = false;
	         TurnCard[i] = new int[56];
	         TurnCardCount[i] = 0;
	         ClientCard[i] = new int[56];
	         ClientCardCount[i] = 0;
	      }

	      for (int i = 0; i < 56; i++) // ī���ȣ ����
	      {
	         Card[i] = i;
	      }

	      for (int i = 55; i > 0; i--) // ī�� ����
	      {
	         int temp;
	         int j = rnd.nextInt(56);
	         temp = Card[i];
	         Card[i] = Card[j];
	         Card[j] = temp;
	      }
	   }
	 public void DivideCard() // ī�带 Ŭ���̾�Ʈ���� ������
	   {
		 System.out.println("DivideCard-id: "+this.inRoomVc.get(0).id);
	      for (int i = 0; i < 4; i++) {
	         for (int j = 0; j < 14; j++) {
	            ClientCard[i][j] = Card[i * 14 + j];
	            ClientCardCount[i]++;
	         }
	      }
	   }

	   public void UpdateCardNum() // Ŭ���̾�Ʈ�鿡�� ī�������� ������Ʈ���� �˸�.
	   {
	      for (int i = 0; i < 4; i++) 
	      {
	         if (!(this.dead[i])) 
	         {
	            //bMan.sendToAll("[CARDNUM]" + Player[i] + "|" + ClientCardCount[i]);
	        	 
	         }
	      }
	   }

	   public void NextPlayer() 
	   {
		  System.out.println("NextPlayer-id: "+this.inRoomVc.get(0).id);
	      this.NowPlayer++;
	      if (this.NowPlayer == 4) {
	         this.NowPlayer = 0;
	      }

	      while (this.dead[this.NowPlayer]) 
	      {
	         this.NowPlayer++;
	         if (this.NowPlayer == 4) 
	         {
	            this.NowPlayer = 0;
	         }
	      }
	   }

	   /*public void SuccessBell() {
	      for (int i = 0; i < 4; i++) {
	         if (!dead[i]) {
	            bMan.sendTo(i, "[SUCCESS]" + Player[i]);
	        	 
	         }
	      }
	   }

	   public void FailBell() {
	      for (int i = 0; i < 4; i++) {
	         if (!dead[i]) {
	            bMan.sendTo(i, "[FAIL]" + Player[i]);
	         }
	      }
	   }*/

	   public int isEndGame() // ������ �������� �˻�
	   {
	      int count = 0;
	      for (int i = 0; i < 4; i++) 
	      {
	         if (this.dead[i]) 
	         {
	        	System.out.println("id: "+this.inRoomVc.get(i).id); 
	            count++;
	         }
	      }
	      if (count == 3) 
	      {
	         for (int i = 0; i < 4; i++) 
	         {
	            if (!(this.dead[i])) 
	            {
	               return i;
	            }
	         }
	      }
	      return -1;
	   }
	/*���� ����*/
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
			System.out.println("NEW Server Start...");
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
		 NewServer server=new NewServer();		        // ���� ����
		 
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
							roomVc.addElement(room);
							int roomCount=preRoomCount(); //���� ��� �ľ� 1�� -> 1
							System.out.println("roomCount->"+roomCount);
							myRoomIndex=(roomCount-1);
							System.out.println(id+"�� ���ȣ��(0���� ����):"+myRoomIndex);
							room.preNum=1;
							room.inRoomVc.addElement(this);
							
							messageTo(Function.ROOMUSER+"|"+id);
							
							//messageAll(Function.ROOMUSER+"|"+id);
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
								
								decision="TRUE";
								String tmpMakerId=roomVc.get(tmpIndex).inRoomVc.get(0).id;
								String tmpRoomName=roomVc.get(tmpIndex).name;
								messageTo(Function.JOINROOM+"|"+decision+"|"+tmpMakerId+"|"+tmpRoomName);		//������ ������� true�Ѱ���
								
								messageRoom(Function.ROOMCHAT+"|"+id+"���� �����Ͽ����ϴ�",tmpIndex);
								myRoomIndex=tmpIndex;
								messageRoom(Function.ROOMUSER+"|"+id,myRoomIndex); //�����̵� �濡�ִ� �����鿡�� ������
								roomVc.get(tmpIndex).inRoomVc.addElement(this);
								
								messageAll(Function.CHGROOMUSER+"|"+myRoomIndex+"|"+tmpPreNum);
								System.out.println("���ο�:"+roomVc.get(tmpIndex).inRoomVc.size());
								
								for(int i=0;i<roomVc.get(tmpIndex).preNum;i++)	//�濡����ִ� ��� ���̵� �ޱ�
								{
									System.out.println(roomVc.get(tmpIndex).inRoomVc.get(i).id);
									messageTo(Function.ROOMUSER+"|"+roomVc.get(tmpIndex).inRoomVc.get(i).id);
								}
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
							messageRoom(Function.EXITFALSE+"|",myRoomIndex);		//�泪���� ��Ȱ��ȭ
							int tmpInRoomUser=roomVc.get(myRoomIndex).preNum;
							messageRoom(Function.TURNINFO+"|"+roomVc.get(myRoomIndex).inRoomVc.get(0).id
									+"|"+roomVc.get(myRoomIndex).inRoomVc.get(1).id
									+"|"+roomVc.get(myRoomIndex).inRoomVc.get(2).id
									+"|"+roomVc.get(myRoomIndex).inRoomVc.get(3).id, myRoomIndex);
							for(int i=0; i<tmpInRoomUser; i++) //�������� ���� �����ؼ� �����ֱ�
							{
								roomVc.get(myRoomIndex).inRoomVc.get(i).messageTo(Function.ROOMCHAT+"|"+"����� "+(i+1)+"��° �Դϴ�");
								//ID�󺧿� �Է�
								messageRoom(Function.IDLABEL+"|"+roomVc.get(myRoomIndex).inRoomVc.get(i).id,myRoomIndex);
								/*���� ����*/
								//���ӹ� Ŭ������ id ���� ����� �� �����尡 �����ִ� id�� ����
								roomVc.get(myRoomIndex).Player[i]=
										roomVc.get(myRoomIndex).inRoomVc.get(i).id;
							}
							
							//���� ������ ���Ӱ��� ó�� �߰��� �ڸ�
							/*���� ����*/
							roomVc.get(myRoomIndex).GameInit(); // �����ʱ�ȭ
							roomVc.get(myRoomIndex).NowPlayer=0; //���� ������ 0�� ����
							messageRoom(Function.ROOMCHAT+"|"+
									roomVc.get(myRoomIndex).Player[roomVc.get(myRoomIndex).NowPlayer]+"�� �����Դϴ�.", 
									myRoomIndex);
							//messageRoom(Function.GAMESTART+"|",myRoomIndex);	
							messageTo(Function.YOURTURN+"|");		//ī��������ưȰ��ȭ
							roomVc.get(myRoomIndex).DivideCard();
							//roomVc.get(myRoomIndex).UpdateCardNum();
							for(int k=0;k<4;k++)
							{
								if(!(roomVc.get(myRoomIndex).dead[k]))
								{
									messageRoom(Function.CARDNUM+"|"+
											roomVc.get(myRoomIndex).Player[k]
											+"|"+roomVc.get(myRoomIndex).ClientCardCount[k], myRoomIndex);
								}
							}
						}
						break;
						case Function.EXITROOM:
						{
							/*����� ����*/
							
							System.out.println("IN->EXITROOM");
							System.out.println("myRoomIndex->"+myRoomIndex);
							System.out.println(roomVc.get(0).preNum);
							System.out.println(roomVc.get(myRoomIndex).preNum);
							System.out.println("IN->EXITROOM2");
							posUser="����";
							System.out.println("EXITROOM Number:"+myRoomIndex);
							//�濡 �����ο� ���ҽ���
							System.out.println(myRoomIndex+"1.���� ���� �ο���:"+roomVc.get(myRoomIndex).preNum);
							(roomVc.get(myRoomIndex).preNum)--;
							int tmpUserCount=roomVc.get(myRoomIndex).preNum; //���� �� �ο� �ӽ� ����
							System.out.println(myRoomIndex+"2.���� ���� �ο���:"+roomVc.get(myRoomIndex).preNum);
							//���Ƿ� �ٲ���
							messageTo(Function.MYLOG+"|"+id+"|"+posUser); 	
							messageAll(Function.CHGROOMUSER+"|"+myRoomIndex+"|"+tmpUserCount);
							int Row=0;
							for(Room room:roomVc)
							{
								if((roomVc.get(myRoomIndex).inRoomVc.get(Row).id).equals(id))
									break;
								Row++;
							}
							messageRoom(Function.OUTUSER+"|"+Row,myRoomIndex);
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
										(roomVc.get(i).inRoomVc.get(j).myRoomIndex)--;
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
						/*���� ����*/
						case Function.CARDOPEN: //Ŭ���̾�Ʈ���� ī�� ������
						{

							Room tmpRoomClass=roomVc.get(myRoomIndex);   //���ȣ
							//0���ڸ��� ���¾ȵ� 13�� Ŭ���̾�Ʈ ī���ȣ�� ���
							tmpRoomClass.TurnCard[tmpRoomClass.NowPlayer][tmpRoomClass.TurnCardCount[tmpRoomClass.NowPlayer]++] = 
									tmpRoomClass.ClientCard[tmpRoomClass.NowPlayer][--tmpRoomClass.ClientCardCount[tmpRoomClass.NowPlayer]];
							//			                     
							if(tmpRoomClass.ClientCardCount[tmpRoomClass.NowPlayer]==0)
								//���� �÷��̾��� ī�尡 ������ ���� �ϳ��� ���� ������....
							{
								System.out.println("aby1");
								tmpRoomClass.dead[tmpRoomClass.NowPlayer]=true;   //�������·� �ٲ�

								messageRoom(Function.UPDATEDEAD+"|"
										+tmpRoomClass.Player[tmpRoomClass.NowPlayer], myRoomIndex);   //������� ���º��� �� ä��â�˸�

								int result=tmpRoomClass.isEndGame();
								if(result != -1)
								{      //���ӳ�0,1,2,3<-����

									System.out.println(tmpRoomClass.Player[result]+"�¸�");
									messageRoom(Function.ROOMCHAT+"|"+tmpRoomClass.Player[result]   //ä�ù濡 ���ھ˸�      
											+"�� �¸�!!", myRoomIndex);
									/*���� ���ڿ� ���� �߰� ó�� ����*/
									messageRoom(Function.GAMEEXIT+"|"
                               			 +tmpRoomClass.Player[result]+"|"+"���� �̰���ϴ�.",
                               			 myRoomIndex);
								}
								else
								{                //���ӳ��ƴ�,dead[]=-1 
									tmpRoomClass.NextPlayer();
									messageRoom(Function.ROOMCHAT+"|"+tmpRoomClass.Player[tmpRoomClass.NowPlayer]      //�������ʾ˷���
											+"�� ���� �Դϴ�.", myRoomIndex);
									tmpRoomClass.inRoomVc.get(tmpRoomClass.NowPlayer).messageTo(Function.YOURTURN+"|");   //��������
								}
							}
							else //�� ������ ī�尡 �������� ��
							{
								messageRoom(Function.REPAINT+"|"
										+tmpRoomClass.Player[tmpRoomClass.NowPlayer]+"|"
										+tmpRoomClass.TurnCard[tmpRoomClass.NowPlayer][tmpRoomClass.TurnCardCount[tmpRoomClass.NowPlayer]-1],myRoomIndex);
								//tmpRoomClass.UpdateCardNum(myRoomIndex);
								for(int k=0;k<4;k++)
								{
									if(!(roomVc.get(myRoomIndex).dead[k]))
									{
										messageRoom(Function.CARDNUM+"|"+
												roomVc.get(myRoomIndex).Player[k]
												+"|"+roomVc.get(myRoomIndex).ClientCardCount[k], myRoomIndex);
									}
								}
								tmpRoomClass.NextPlayer();
								messageRoom(Function.ROOMCHAT+"|"+tmpRoomClass.Player[tmpRoomClass.NowPlayer]		//�������ʾ˷���
											+"�� ���� �Դϴ�.", myRoomIndex);
								tmpRoomClass.inRoomVc.get(tmpRoomClass.NowPlayer).messageTo(Function.YOURTURN+"|");
							}
						}
						break;
						case Function.BELL: //Ŭ���̾�Ʈ�� ��������
						{
							System.out.println("In->BELL");
							Room tmpRoomClass=roomVc.get(myRoomIndex);
							if(tmpRoomClass.isBell==true)
							{
								System.out.println("In->BELL1");
								messageTo(Function.ROOMCHAT+"|"+"����� �ʾ����ϴ�");
							}
							else
							{
								System.out.println("In->BELL2");
								tmpRoomClass.isBell=true;
								messageRoom(Function.ROOMCHAT+"|"+id
										+"���� ���� �ƽ��ϴ�.", myRoomIndex);
								messageRoom(Function.BELL+"|",myRoomIndex);
								System.out.println("In->BELL2_1");
								Thread.sleep(500);
								System.out.println("In->BELL2_2");
								tmpRoomClass.isSuccess=false;
								System.out.println("In->BELL2_3");
								int tmpCardSum=0;
								System.out.println("In->BELL3");
								for (int i = 0; i < 4; i++) 
								{
									System.out.println("In->BELL4");
			                        if (tmpRoomClass.TurnCardCount[i] != 0) 
			                        { 	// ������ �� ī���� ����������
			                        	// ���� ���Ѵ�.
			                           int temp = tmpRoomClass.TurnCard[i][tmpRoomClass.TurnCardCount[i] - 1];
			                           tmpRoomClass.CardType[i] = temp / 14;
			                           tmpRoomClass.CardNum[i] = temp % 14;
			                        } 
			                        else
			                        { // 4���� �� ���� ������츦 ����� 0���� �ʱ�ȭ.
			                        	tmpRoomClass.CardType[i] = -1;
			                        	tmpRoomClass.CardNum[i] = -1;
			                        }
			                     }
								System.out.println("In->BELL5");
								for (int i = 0; i < 4; i++) 
								{
									System.out.println("In->BELL6");
									tmpCardSum = 0;
			                        for (int j = 0; j < 4; j++) 
			                        {
			                           if (tmpRoomClass.CardType[i] == tmpRoomClass.CardType[j])
			                           { // ����������
			                        	   // ���Ѵ�.
			                              if (tmpRoomClass.CardNum[j] >= 0 && tmpRoomClass.CardNum[j] <= 4) 
			                              {
			                            	  tmpCardSum += 1;
			                              } 
			                              else if (tmpRoomClass.CardNum[j] >= 5 && tmpRoomClass.CardNum[j] <= 7) 
			                              {
			                            	  tmpCardSum += 2;
			                              } 
			                              else if (tmpRoomClass.CardNum[j] >= 8 && tmpRoomClass.CardNum[j] <= 10) 
			                              {
			                            	  tmpCardSum += 3;
			                              } 
			                              else if (tmpRoomClass.CardNum[j] >= 11 && tmpRoomClass.CardNum[j] <= 12) 
			                              {
			                            	  tmpCardSum += 4;
			                              } 
			                              else if (tmpRoomClass.CardNum[j] == 13) 
			                              {
			                            	  tmpCardSum += 5;
			                              }
			                           }
			                        }
			                        System.out.println("In->BELL7");
			                        if (tmpCardSum == 5) 
			                        { // ��ġ�⼺���� �� ī�带 �� ��������.
			                        	System.out.println("In->BELL8");
			                        	//tmpRoomClass.SuccessBell();
			                        	for(int k=0;k<4;k++)
										{
			                        		System.out.println("In->BELL9");
											if(!(roomVc.get(myRoomIndex).dead[k]))
											{
												/*messageTo(Function.BELLSUCCESS+"|"+
														roomVc.get(myRoomIndex).Player[k]
														+"|"+roomVc.get(myRoomIndex).ClientCardCount[k]);*/
												roomVc.get(myRoomIndex).inRoomVc.get(k)
												.messageTo(Function.BELLSUCCESS+"|"+id);
											}
										}
			                        	System.out.println("In->BELL10");
			                        	tmpRoomClass.isBell = false;
			                            //bMan.sendToAll(userName + "���� ��ġ�⿡ �����߽��ϴ�.");
			                        	/*//messageRoom(Function.ROOMCHAT+"|"+id
												+"���� ��ġ�� �����Ͽ����ϴ�.", myRoomIndex);*/
			                        	//int a = bMan.getNum(userName);
			                            int tmp=tmpRoomClass.preNum;
			                            System.out.println("In->BELL11");
			                            int a=0;
			                            for(a=0;a<tmp;a++)
			                            {
			                            	System.out.println("In->BELL12");
			                            	if(id.equals(tmpRoomClass.inRoomVc.get(a).id))
			                            		break;
			                            }
			                        	for (i = 0; i < 4; i++) 
			                            {
			                        		System.out.println("In->BELL13");
			                               for (int j = 0; j < tmpRoomClass.TurnCardCount[i]; j++) 
			                               {
			                            	   System.out.println("In->BELL14");
			                            	   tmpRoomClass.ClientCard[a][tmpRoomClass.ClientCardCount[a]++] = tmpRoomClass.TurnCard[i][j];
			                               }
			                               tmpRoomClass.TurnCardCount[i] = 0;
			                            }
			                        	System.out.println("In->BELL15");
			                            for(int m = tmpRoomClass.ClientCardCount[a]; m > 0; m--) // ī��// ����    
			                            {
			                            	System.out.println("In->BELL16");
			                               int temp;
			                               int n = tmpRoomClass.rnd.nextInt(tmpRoomClass.ClientCardCount[a]);
			                               temp = tmpRoomClass.ClientCard[a][m];
			                               tmpRoomClass.ClientCard[a][m] = tmpRoomClass.ClientCard[a][n];
			                               tmpRoomClass.ClientCard[a][n] = temp;
			                            }
			                            tmpRoomClass.isSuccess = true;
			                            //tmpRoomClass.UpdateCardNum(myRoomIndex);
			                            for(int k=0;k<4;k++)
										{
			                            	System.out.println("In->BELL17");
											if(!(roomVc.get(myRoomIndex).dead[k]))
											{
												System.out.println("In->BELL18");
												messageRoom(Function.CARDNUM+"|"+
														roomVc.get(myRoomIndex).Player[k]
														+"|"+roomVc.get(myRoomIndex).ClientCardCount[k], myRoomIndex);
											}
										}
			                            break;
			                        }
								}
								System.out.println("In->BELL19");
								if (!(tmpRoomClass.isSuccess)) { // ��ġ�� ���н� �ٸ��÷��̾�� ī�带 ���徿 ������.
									//tmpRoomClass.FailBell();
									System.out.println("In->BELL20");
									for(int k=0;k<4;k++)
									{
										System.out.println("In->BELL21");
										if(!(roomVc.get(myRoomIndex).dead[k]))
										{
											System.out.println("In->BELL22");
											/*messageRoom(Function.BELLFAIL+"|"+
													id
													+"|"+roomVc.get(myRoomIndex).ClientCardCount[k], myRoomIndex);*/
											tmpRoomClass.inRoomVc.get(k).messageTo(Function.BELLFAIL+"|"+id);
										}
									}
									System.out.println("In->BELL23");
									tmpRoomClass.isBell = false;
			                        /*messageRoom(Function.ROOMCHAT+"|"+id+
			                        		"���� ��ġ�⿡ �����߽��ϴ�.", myRoomIndex);*/
			                        for (int i = 0; i < 4; i++) {
			                        	System.out.println("In->BELL24");
			                           if (!(id.equals(tmpRoomClass.Player[i]))
			                        		   && !(tmpRoomClass.dead[i])) {
			                        	   int a=0;
			                        	   int tmp=tmpRoomClass.preNum;
				                            for(a=0;a<tmp;a++)
				                            {
				                            	if(id.equals(tmpRoomClass.inRoomVc.get(a).id))
				                            		break;
				                            	a++;
				                            }
			                              
			                              tmpRoomClass.ClientCard[i][tmpRoomClass.ClientCardCount[i]++] 
			                            		  = tmpRoomClass.ClientCard[a][--tmpRoomClass.ClientCardCount[a]];
			                              if (tmpRoomClass.ClientCardCount[a] == 0) {
			                            	  System.out.println("In->BELL25");
			                            	  tmpRoomClass.dead[a] = true;
			                                 //bMan.sendToAll("[UPDATEDEAD]" + userName);
			                                 messageRoom(Function.UPDATEDEAD+"|"+id,myRoomIndex);
			                                 //writer.println("[DEAD]" + userName);
			                                 messageTo(Function.DEAD+"|"+id);
			                                 if (id.equals(tmpRoomClass.Player[tmpRoomClass.NowPlayer])) {
			                                	 tmpRoomClass.NextPlayer();
			                                 }
			                                 if (tmpRoomClass.isEndGame() != -1) {
			                                    //bMan.sendToAll(Player[isEndGame()] + "���� �̰���ϴ�.");
			                                	 messageRoom(Function.GAMEEXIT+"|"
			                                			 +tmpRoomClass.Player[tmpRoomClass.isEndGame()]+"|"+"���� �̰���ϴ�.",
			                                			 myRoomIndex);
			                                	 //bMan.sendToAll("[GAMEINIT]");
			                                    //bMan.sendTo(isEndGame(), "[WIN]");
			                                    //bMan.unReady();
			                                 }
			                                 break;
			                              }
			                           }
			                           System.out.println("In->BELL26");
			                        }
			                        //tmpRoomClass.UpdateCardNum(myRoomIndex);
			                        for(int k=0;k<4;k++)
									{
			                        	System.out.println("In->BELL27");
										if(!(roomVc.get(myRoomIndex).dead[k]))
										{
											System.out.println("In->BELL28");
											messageRoom(Function.CARDNUM+"|"+
													roomVc.get(myRoomIndex).Player[k]
													+"|"+roomVc.get(myRoomIndex).ClientCardCount[k], myRoomIndex);
										}
									}
			                     }
							}
							System.out.println("In->BELL29");
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
