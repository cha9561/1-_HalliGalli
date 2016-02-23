package com.sist.server;
import java.util.*;

import javax.annotation.processing.Messager;

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
	
	/*���� ī�� ����*/
	Random rnd = new Random();
	int card[] = new int[56]; // ī�� �������� ����
	int turnCard[][] = new int[humanNum][]; // �������� ī�带 �����ϴ� ����
	int turnCardCount[] = new int[humanNum]; // �������� ī���� ���� (�����������ʿ�)
	int cardType[] = new int[4]; // ī���� ������ �˱����� ����
	int cardNum[] = new int[4]; // ī��� ���� ���� �˱����� ���� 
	int clientCard[][] = new int[humanNum][]; // Ŭ���̾�Ʈ ī�� ���� (�����������ʿ�)
	int clientCardCount[] = new int[humanNum]; // Ŭ���̾�Ʈ ī�� ���� ���� (�����������ʿ�)
	int nowPlayer; // ���� ���ʰ� �������� ����
	boolean isSuccess = false; // ��ġ�⿡ �����ߴ��� Ȯ��
	boolean dead[] = new boolean[humanNum]; // �׾����� ��Ҵ��� Ȯ�� (�����������ʿ�)
	int deadCount=0;
	boolean EndGame = false; // ������ ������ Ȯ��.
	boolean isBell = false; // ������ ���� �ƴ��� Ȯ��
	String Player[] = new String[humanNum]; // �÷��̾��̸� ���Ӽ������ ���� (�����������ʿ�)
	
	
	public void GameInit() // ���� �ʱ�ȭ
	{
		for (int i = 0; i < humanNum; i++) {
			dead[i] = false;
			turnCard[i] = new int[56];
			turnCardCount[i] = 0;
			clientCard[i] = new int[56];
			clientCardCount[i] = 0;
		}

		for (int i = 0; i < 56; i++) // ī���ȣ ����
		{
			card[i] = i;
		}

		for (int i = 55; i > 0; i--) // ī�� ����
		{
			int temp;
			int j = rnd.nextInt(56);
			temp = card[i];
			card[i] = card[j];
			card[j] = temp;
		}
	}
	public void DivideCard() // ī�带 Ŭ���̾�Ʈ���� ������
	{
		for (int i = 0; i < humanNum; i++) {
			for (int j = 0; j < 14; j++) {
				clientCard[i][j] = card[i * 14 + j];
				clientCardCount[i]++;
			}
		}
	}
	public void UpdateCardNum() // Ŭ���̾�Ʈ�鿡�� ī�������� ������Ʈ���� �˸�.
	{
		for (int i = 0; i < humanNum; i++) {
			if (!dead[i]) {
				this.cliT[i].messageTo(Function.CARDNUM+"|"+this.cliT[i].id+"|"+this.clientCardCount[i]);	
			}
		}
	}
	public void NextPlayer() {
		nowPlayer++;
		if (nowPlayer == humanNum) {
			nowPlayer = 0;
		}

		while (dead[nowPlayer]) {
			nowPlayer++;
			if (nowPlayer == humanNum) {
				nowPlayer = 0;
			}
		}
	}
	public void SuccessBell() {
		for (int i = 0; i < humanNum; i++) {
			if (!dead[i]) {
				//bMan.sendTo(i, "[SUCCESS]" + Player[i]);
			}
		}
	}

	public void FailBell() {
		for (int i = 0; i < humanNum; i++) {
			if (!dead[i]) {
				//bMan.sendTo(i, "[FAIL]" + Player[i]);
			}
		}
	}

	public int isEndGame() // ������ �������� �˻�
	{
		int count = 0;
		for (int i = 0; i < humanNum; i++) {
			if (dead[i]) {
				count++;
			}
		}
		if (count == 3) {
			for (int i = 0; i < humanNum; i++) {
				if (!dead[i]) {
					return i;
				}
			}
		}
		return -1;
	}
}
public class Server implements Runnable{

	Vector<ClientThread> waitVc=new Vector<ClientThread>();		//����� �迭
	Vector<GameRoom> gameRoom=new Vector<GameRoom>();			//���ӷ� �迭
	
	ServerSocket ss=null;// �������� ���ӽ� ó�� (��ȯ ����)
	
	//static int delIndex;
	//int roomCount;
	
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
		int userIndex;
		
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
						messageAll(Function.DELROW+"|"+userIndex);			//
						
						int i=-1;
						for(ClientThread client:waitVc)
						{
							i++;
						}
						for(int j=userIndex;j<i;j++)
						{
							waitVc.get(j+1).userIndex--;
						}
						
						waitVc.remove(userIndex);
						
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
						int i=-1;
						for(ClientThread client:waitVc)			//�ٸ� ����ڵ鿡�� ���� ������ ����Ʈ�� �ø�
						{
							i++;
							messageTo(Function.LOGIN+"|"+client.id+"|"+client.posUser);
						}
						userIndex=i;
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
								
						posUser="���ӷ�";
						int roomCount=0;
						for(GameRoom room:gameRoom)			//���� �ִ� �氹�� ���� (1����������ϱ� 1��????)
						{
							roomCount++;
						}					
						gr.roomNum=roomCount;					//gameroom ����=>���� ���� ���ӷ��� ���ȣ ����(1����������ϱ� 1��????)
						clientroomNumber=roomCount;				//client ����=>client�� �ִ� �� ��ȣ ����(1��????)
						//roomCount++;
						
						System.out.println("clientroomNumber: "+clientroomNumber);
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
						System.out.println("clientroomNumber: "+clientroomNumber);
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
						
						/*���� �����ؼ� �ѷ��ֱ�*/
						int userCount= gameRoom.get(clientroomNumber).humanNum;
						for(int i=0; i<userCount; i++) //
						{
							System.out.println("I��------>"+i);
							gameRoom.get(clientroomNumber).cliT[i].messageTo(Function.TURNINFO+"|"+"����� "+(i+1)+"��° �Դϴ�");
							gameRoom.get(clientroomNumber).Player[i]=gameRoom.get(clientroomNumber).cliT[i].id; //������� id����				
						}
						gameRoom.get(clientroomNumber).GameInit();
						
						gameRoom.get(clientroomNumber).nowPlayer = 0; // ���ӽ����� 0������
						String id=gameRoom.get(clientroomNumber).cliT[0].id;
						messageRoom(Function.ROOMCHAT+"|"+id+"|"+"�� �����Դϴ�", clientroomNumber);
						gameRoom.get(clientroomNumber).DivideCard();
						gameRoom.get(clientroomNumber).UpdateCardNum();
					}
					break;		
					case Function.CARDOPEN: 	//ī�� ������ �Ͽ�����
					{
						GameRoom gr=gameRoom.get(clientroomNumber);
						System.out.println("ī����� �Ͽ��� id: "+id);
						int nowPlayerIndex=gameRoom.get(clientroomNumber).nowPlayer;
						
						gr.turnCard[nowPlayerIndex][gr.turnCardCount[nowPlayerIndex]]=
								gr.clientCard[nowPlayerIndex][--(gr.clientCardCount[nowPlayerIndex])];
						if (gr.clientCardCount[nowPlayerIndex] == 0) 	//Ŭ���̾�Ʈ�� ī�尡 ���嵵 �ȳ�������
						{
							gr.dead[nowPlayerIndex] = true; //����
							gr.deadCount++;
							messageRoom(Function.ROOMCHAT+"|"+id+"|"+"�� �׾����ϴ�", clientroomNumber);
							messageRoom(Function.UPDATEDEAD+"|"+id, clientroomNumber);
							if (gr.isEndGame()!=0) 
							{
								String winId=gr.Player[gr.isEndGame()];
								messageRoom(Function.GAMEEXIT+"|"+winId+"|"+"���� �̰���ϴ�", clientroomNumber);
							}
							gr.NextPlayer();
						}else // �׿ܴ� Ŭ���̾�Ʈ���� ī�� �ٽñ׸� ��û
						{
							messageRoom(Function.REPAINT+gr.Player[nowPlayerIndex]+"|"+
							gr.turnCard[nowPlayerIndex][gr.turnCardCount[nowPlayerIndex]-1],clientroomNumber);
							gr.UpdateCardNum();
							gr.NextPlayer();
							messageRoom(Function.ROOMCHAT+"|"+id+"|"+"�� �����Դϴ�", clientroomNumber);
						}
					}
					break;
					case Function.BELL:
					{
						GameRoom gr=gameRoom.get(clientroomNumber);
						if (gr.isBell == true) {
							messageTo(Function.ROOMCHAT+"|"+"����� �ʾ����ϴ�");
						}
						else
						{
							gr.isBell = true;
							messageTo(Function.ROOMCHAT+"|"+id+"���� ���� �ƽ��ϴ�");
							messageRoom(Function.BELL+"|",clientroomNumber);
							Thread.sleep(1000);
							gr.isSuccess=false;
							int CardSum=0;
							for(int i=0;i<gr.humanNum;i++)
							{
								if(gr.turnCardCount[i]!=0)
								{
									int temp=gr.turnCard[i][gr.turnCardCount[i] - 1];
									gr.cardType[i]=temp/14;
									gr.cardNum[i]=temp%14;
								}
								else
								{
									// 4���� �� ���� ������츦 ����� 0���� �ʱ�ȭ.
									gr.cardType[i] = -1;
									gr.cardNum[i] = -1;
								}
							}
							for (int i = 0; i < gr.humanNum; i++) 
							{
								CardSum = 0;
								for (int j = 0; j < 4; j++) 
								{
									if (gr.cardType[i] == gr.cardType[j]) { // ����������
																		// ���� �͸�
																		// ���Ѵ�.
										if (gr.cardNum[j] >= 0 && gr.cardNum[j] <= 4) {
											CardSum += 1;
										} else if (gr.cardNum[j] >= 5 && gr.cardNum[j] <= 7) {
											CardSum += 2;
										} else if (gr.cardNum[j] >= 8 && gr.cardNum[j] <= 10) {
											CardSum += 3;
										} else if (gr.cardNum[j] >= 11 && gr.cardNum[j] <= 12) {
											CardSum += 4;
										} else if (gr.cardNum[j] == 13) {
											CardSum += 5;
										}
									}
								}
								if(CardSum==5)
								{
									gr.SuccessBell();
									gr.isBell = false;
									//bMan.sendToAll(userName + "���� ��ġ�⿡ �����߽��ϴ�.");
									messageRoom(Function.ROOMCHAT+"|"+id+"���� ��ġ�⿡ ����",clientroomNumber);
									int tmpNum=0;
									for(tmpNum=0;tmpNum<gr.humanNum;tmpNum++)
									{
										if((gr.cliT[tmpNum].id).equals(id))
											break;												
									}
									for(i=0;i<gr.humanNum;i++)
									{
										for(int j=0;j<gr.turnCardCount[i];j++)
										{
											gr.clientCard[tmpNum][gr.clientCardCount[tmpNum]++]=
													gr.turnCard[i][j];
										}
										gr.turnCardCount[i]=0;
									}
									for (int m=gr.clientCardCount[tmpNum]; m > 0; m--) //ī�� ����
									{
										int temp;
										int n = gr.rnd.nextInt(gr.clientCardCount[tmpNum]);
										temp = gr.clientCard[tmpNum][m];
										gr.clientCard[tmpNum][m] = gr.clientCard[tmpNum][n];
										gr.clientCard[tmpNum][n] = temp;
									}
									gr.isSuccess = true;
									gr.UpdateCardNum();
									break;
								}
							}
							if(!gr.isSuccess)// ��ġ�� ���н� �ٸ��÷��̾�� ī�带 ���徿 ������.
							{
								gr.FailBell();
								gr.isBell=false;
								messageRoom(Function.ROOMCHAT+"|"+id+"���� ��ġ�⿡ �����߽��ϴ�.", clientroomNumber);
								for (int i = 0; i < gr.humanNum; i++)
								{
									if(!id.equals(gr.Player[i])&&!gr.dead[i])
									{
										int tmpNum=0;
										for(tmpNum=0;tmpNum<gr.humanNum;tmpNum++)
										{
											if((gr.cliT[tmpNum].id).equals(id))
												break;												
										}
										gr.clientCard[i][gr.clientCardCount[i]++] = gr.clientCard[tmpNum][--gr.clientCardCount[tmpNum]];
										if(gr.clientCardCount[tmpNum]==0)
										{
											gr.dead[tmpNum]=true;
											messageRoom(Function.UPDATEDEAD+"|"+id, clientroomNumber);
											messageTo(Function.DEAD+"|");
											if (id.equals(gr.Player[gr.nowPlayer])) 
											{
												gr.NextPlayer();
											}
											if (gr.isEndGame() != -1) 
											{
												String winId=gr.Player[gr.isEndGame()];
												messageRoom(Function.GAMEEXIT+"|"+winId+"|"+"���� �̰���ϴ�", clientroomNumber);
											}
											break;
										}
									}
								}
								gr.UpdateCardNum();
							}
						}
					}
					break;
					/*[�泪����] ->*/
					case Function.EXITROOM:
					{
						posUser="����";
						System.out.println("EXITROOM.clientroomNumber -> "+clientroomNumber);
						System.out.println("humanNum=> "+gameRoom.get(clientroomNumber).humanNum);	//�����ο�						
						gameRoom.get(clientroomNumber).humanNum--; 					//��� ���� ��Ŵ
						int humNum=gameRoom.get(clientroomNumber).humanNum;			//��ȭ�� �����ο�
						System.out.println("humanNum=> "+gameRoom.get(clientroomNumber).humanNum);	
						
						messageTo(Function.MYLOG+"|"+id+"|"+posUser); 							//���� ȭ������ �ٲٱ� ����
						messageAll(Function.CHGROOMUSER+"|"+clientroomNumber+"|"+humNum);	//����º�ȭ��Ű�� ����
						
						
						/*test*/
						int userRow=0;
						for(ClientThread client:waitVc)
						{
							if((waitVc.get(userRow).id).equals(id))
								break;
							userRow++;
						} //���° �������� �ľ��Ͽ� List ��  ���� �ʿ��� Row �� �˾Ƴ��� ����
						messageAll(Function.CHGUSERPOS+"|"+userRow+"|"+posUser); 						//Ÿ ������ ������ �����ϵ���
						messageRoom(Function.ROOMCHAT+"|"+id+"���� �����Ͽ����ϴ�",clientroomNumber);	//�������� ����޽��� ������
						
						int roomCount=-1;
						for(GameRoom room:gameRoom)			//���� �ִ� �氹�� ���� (1����������ϱ� 1��????)
						{
							roomCount++;
						}
						
						if(gameRoom.get(clientroomNumber).humanNum<=0) //�濡 ���� ����� ������
						{
							messageAll(Function.DELROOM+"|"+clientroomNumber);	
							System.out.println("���Ϳ��� ����");
							System.out.println("������ 1 clientroomNumber"+clientroomNumber);
							System.out.println("������ 1 roomCount"+roomCount);
							for(int i=clientroomNumber;i<roomCount;i++)
							{
								System.out.println("������ 12 clientroomNumber"+clientroomNumber);
								System.out.println("������ 12 roomCount"+roomCount);
								int inUser=gameRoom.get(i+1).humanNum;
								for(int j=0; j<inUser; j++)
								{
									System.out.println("zzzzz-> "+i+j+clientroomNumber);
									gameRoom.get(i+1).cliT[j].clientroomNumber--;
								}
							}
							//���Ϳ��� ����
							System.out.println("���Ϳ��� ����");
							gameRoom.removeElementAt(clientroomNumber);
							
							roomCount--;
						}
						else
						{
							//�泪�� Ŭ���̾�Ʈ�� �迭���� �����ϰ� �� Ŭ���̾�Ʈ�� ������ ����
							int userCount= (gameRoom.get(clientroomNumber).humanNum)-1; /*bugfix -> 0223 4��*/
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
						System.out.println("clientroomNumber: "+clientroomNumber);
						clientroomNumber=-1;
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
		/*public synchronized void messageTo(String msg, int num)
		{
			try
			{
				out.write((msg+"\n").getBytes());
			}catch(Exception ex)
			{
				delIndex=num;
			}
		}*/
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
