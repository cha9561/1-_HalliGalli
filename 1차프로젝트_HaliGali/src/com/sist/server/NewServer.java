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
	int capaNum;		//들어갈 수 있는 최대인원 2~4
	int preNum;			//현재 인원 1~5
	String name;		//방이름
	int readyNum;		//준비 누른 인원
	String Type;		//공개 비공개
	String pos;
	
	/*게임 관련*/
	int Card[] = new int[56]; // 카드 섞기위한 변수
	int TurnCard[][] = new int[4][]; // 뒤집어진 카드를 저장하는 변수
	int TurnCardCount[] = new int[4]; // 뒤집어진 카드의 개수
	int CardType[] = new int[4]; // 카드의 종류를 알기위한 변수
	int CardNum[] = new int[4]; // 카드속 과일 개수 알기위한 변수
	int ClientCard[][] = new int[4][]; // 클라이언트 카드 변수
	int ClientCardCount[] = new int[4]; // 클라이언트 카드 개수 변수
	int NowPlayer; // 현재 차례가 누구인지 저장
	boolean isSuccess = false; // 종치기에 성공했는지 확인
	boolean dead[] = new boolean[4]; // 죽었는지 살았는지 확인
	boolean EndGame = false; // 게임이 끝인지 확인.
	boolean isBell = false; // 상대방이 종을 쳤는지 확인
	String Player[] = new String[4]; // 플레이어이름 접속순서대로 저장
	Random rnd = new Random();
	
	public void GameInit() // 게임 초기화
	   {
	      for (int i = 0; i < 4; i++) {
	         dead[i] = false;
	         TurnCard[i] = new int[56];
	         TurnCardCount[i] = 0;
	         ClientCard[i] = new int[56];
	         ClientCardCount[i] = 0;
	      }

	      for (int i = 0; i < 56; i++) // 카드번호 삽입
	      {
	         Card[i] = i;
	      }

	      for (int i = 55; i > 0; i--) // 카드 섞기
	      {
	         int temp;
	         int j = rnd.nextInt(56);
	         temp = Card[i];
	         Card[i] = Card[j];
	         Card[j] = temp;
	      }
	   }
	 public void DivideCard() // 카드를 클라이언트에게 나눠줌
	   {
		 System.out.println("DivideCard-id: "+this.inRoomVc.get(0).id);
	      for (int i = 0; i < 4; i++) {
	         for (int j = 0; j < 14; j++) {
	            ClientCard[i][j] = Card[i * 14 + j];
	            ClientCardCount[i]++;
	         }
	      }
	   }

	   public void UpdateCardNum() // 클라이언트들에게 카드정보가 업데이트됨을 알림.
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

	   public int isEndGame() // 게임이 끝인지를 검사
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
	/*게임 관련*/
}
public class NewServer implements Runnable{
	Vector<ClientThread> waitVc=new Vector<ClientThread>();		//사용자 배열
	Vector<Room> roomVc=new Vector<Room>();
	
	ServerSocket ss=null;// 서버에서 접속시 처리 (교환 소켓)
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
	public void run()							// 1. 접속을 처리
    {
    	while(true){
	    	try{
	    		Socket s=ss.accept();		//s => client    		
	    		ClientThread ct=new ClientThread(s);
	    		ct.start();					// 통신 시작 	
	    	}catch(Exception ex){}
    	}	
    }
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		 NewServer server=new NewServer();		        // 서버 가동
		 
		new Thread(server).start();
	}
	
	class ClientThread extends Thread
	{
		String id,posUser;		//id,이름,성별,상태
		int myIndex; //0부터 시작 첫번째 유저는 0번...
		Socket s;
		BufferedReader in;	// client요청값을 읽어온다
		OutputStream out;	//client로 결과값을 응답할때 
		int myRoomIndex=-1;		//client가 있는 방 번호, 0부터 시작
		
		
		public ClientThread(Socket s)
		{
			try{
				this.s=s;			//각 클라이언트의 소켓 장착
				in=new BufferedReader(new InputStreamReader(s.getInputStream()));
				out=s.getOutputStream();
			}catch(Exception ex){}
		}
		
		public void run()			//2.client와 server간의 통신을 처리  //Client의 요청을 받음
		{
			while(true)			
			{
				try
				{
					String msg=in.readLine();
					System.out.println("Client=>"+msg);		//Client에서 보낸 메시지를 나타내줌
					StringTokenizer st=new StringTokenizer(msg, "|");
					int protocol=Integer.parseInt(st.nextToken());
					switch(protocol)
					{
						case Function.LOGIN:
						{	
			
							/*로그인 접속처리*/
							System.out.println("IN-LOGIN");
							id=st.nextToken();
							posUser="대기실";
							messageAll(Function.LOGIN+"|"+id+"|"+posUser);
							waitVc.addElement(this);
							messageTo(Function.MYLOG+"|"+id+"|"+posUser);
							
							
							int i=0;
							for(ClientThread client:waitVc)			//다른 사용자들에게 나를 접속자 리스트에 올림
							{
								i++;
								messageTo(Function.LOGIN+"|"+client.id+"|"+client.posUser);
							}
							myIndex=i-1;  //myIndex는 0부터 시작하므로
							
							for(Room room:roomVc)			//현재 있는 방갯수 세기 (1개만들었으니까 1개????)
							{
								messageTo(Function.ROOMINFORM+"|"+room.Type+"|"+room.name+
										"|"+room.preNum+"|"+room.capaNum+"|"+room.pos);
							}
						} 
						break;
						case Function.CLIENTEXIT:
						{
							/*클라이언트 게임종료시*/
							System.out.println("IN-CLIENTEXIT");
							String exitMsg="님이 나갔습니다.";
							s.close();
							messageAll(Function.CLIENTEXIT+"|"+id+exitMsg);		//채팅창에 00님이 나가셨습니다 뿌리기 
							messageAll(Function.DELROW+"|"+myIndex);
							
							
							
							/*몇명 접속중인지 파악*/
							int i=preTotalUserCount(); //현재 접속중 user 1명->1
							/*나간 클라이언트 뒤쪽 클라이언트들의 myIndex를 감소시켜준다*/
							for(int j=(myIndex+1); j<(i-1); j++)
							{
								(waitVc.get(j).myIndex)--;
							}
							/*벡터에서 삭제*/
							waitVc.remove(myIndex);
							interrupt();
						}
						break;
						case Function.WAITCHAT1:
						{
							/*클라이언트 대기실 채팅*/
							System.out.println("IN-WAITCHAT1");
							String data=st.nextToken();
							messageAll(Function.WAITCHAT1+"|["+id+"]"+data);
						}
						break;
						case Function.IDCHECK:
						{
							/*ID 중복체크 요청시*/
							System.out.println("ID중복체크");
							String id=st.nextToken();
							System.out.println(id);
							/*ID 중복 체크 구현부*/
						}
						break;
						case Function.SUCCESSJOIN:
						{
							/*회원 가입 성공*/
							System.out.println("회원 정보 추가");
							String name=st.nextToken();
							String id=st.nextToken();
							String pass=st.nextToken();
						}
						break;
						/*게임방 관리*/
						case Function.MAKEROOM:
						{
							/*방만들기*/
							System.out.println("IN-MAKEROOM");
							String roomType=st.nextToken();		//새로 만든 게임룸의 공개정보
							String roomName=st.nextToken();		//새로 만든 게임룸의 방이름
							String capaNumImsi=st.nextToken();	//새로 만든 게임룸의 최대인원(2명일때->2)
							
							Room room=new Room();
							room.pos="게임대기중";				//게임룸의 상태정보
							room.Type=roomType;				//새로 만든 게임룸의 공개정보 대입
							room.name=roomName;				//새로 만든 게임룸의 방이름 대입
							
							if(capaNumImsi.equals("2명"))	//2.방만들자마자 최대인원수 대입
							{
								room.capaNum=2;				
							}
							else if(capaNumImsi.equals("3명"))
							{
								room.capaNum=3;
							}else 
							{
								room.capaNum=4;
							}
							posUser="게임룸";
							roomVc.addElement(room);
							int roomCount=preRoomCount(); //현재 방수 파악 1개 -> 1
							System.out.println("roomCount->"+roomCount);
							myRoomIndex=(roomCount-1);
							System.out.println(id+"의 방번호는(0부터 시작):"+myRoomIndex);
							room.preNum=1;
							room.inRoomVc.addElement(this);
							
							messageTo(Function.ROOMUSER+"|"+id);
							
							//messageAll(Function.ROOMUSER+"|"+id);
							messageTo(Function.MAKEROOM+"|"+id+"|"+roomName+"|"+room.preNum+"|"+room.capaNum);//1.game창으로 전환
							messageAll(Function.ROOMINFORM+"|"+roomType+"|"+roomName+"|"+room.preNum+"|"+room.capaNum+"|"+room.pos);//2.방목록에 띄움
							
							/*유저 상태 변경*/
							int tmpMyRow=0;
							for(ClientThread client:waitVc)
							{
								if((waitVc.get(tmpMyRow).id).equals(id))
									break;
								tmpMyRow++;
							} //몇번째 유저인지 파악하여 List 의  변경 필요한 Row 값 알아내기 위해
							messageAll(Function.CHGUSERPOS+"|"+tmpMyRow+"|"+posUser);
						}
						break;
						case Function.JOINROOM:
						{
							/*방들어가기*/
							System.out.println("IN-JOINROOM");
							
							String tmpRoomIndex=st.nextToken();	//0번 부터 시작
							int tmpIndex=(Integer.parseInt(tmpRoomIndex));
							
							int tmpRoomCapa=roomVc.get(tmpIndex).capaNum;
							int tmpPreNum=roomVc.get(tmpIndex).preNum;
							String decision="FALSE"; 
							if(tmpPreNum<tmpRoomCapa)
							{
								(roomVc.get(tmpIndex).preNum)++;
								tmpPreNum=roomVc.get(tmpIndex).preNum;
								posUser="게임룸";
								
								decision="TRUE";
								String tmpMakerId=roomVc.get(tmpIndex).inRoomVc.get(0).id;
								String tmpRoomName=roomVc.get(tmpIndex).name;
								messageTo(Function.JOINROOM+"|"+decision+"|"+tmpMakerId+"|"+tmpRoomName);		//증가된 사람에게 true넘겨줌
								
								messageRoom(Function.ROOMCHAT+"|"+id+"님이 입장하였습니다",tmpIndex);
								myRoomIndex=tmpIndex;
								messageRoom(Function.ROOMUSER+"|"+id,myRoomIndex); //내아이디 방에있는 유저들에게 보내기
								roomVc.get(tmpIndex).inRoomVc.addElement(this);
								
								messageAll(Function.CHGROOMUSER+"|"+myRoomIndex+"|"+tmpPreNum);
								System.out.println("방인원:"+roomVc.get(tmpIndex).inRoomVc.size());
								
								for(int i=0;i<roomVc.get(tmpIndex).preNum;i++)	//방에들어있던 사람 아이디 받기
								{
									System.out.println(roomVc.get(tmpIndex).inRoomVc.get(i).id);
									messageTo(Function.ROOMUSER+"|"+roomVc.get(tmpIndex).inRoomVc.get(i).id);
								}
								messageAll(Function.CHGUSERPOS+"|"+myIndex+"|"+posUser);
							}
							else //정원초과
							{
								messageTo(Function.JOINROOM+"|"+decision);		//들어가려는 사람에게 false넘겨줌
								System.out.println("second");
							}
						}
						break;
						case Function.ROOMCHAT:
						{
							/*클라이언트 게임룸 채팅*/
							System.out.println("IN-ROOMCHAT");
							String data=st.nextToken();
							messageRoom(Function.ROOMCHAT+"|["+id+"]"+data,myRoomIndex);
						}
						break;
						case Function.ROOMREADY:
						{
							/*준비 관리*/
							System.out.println("IN-ROOMREADY");
							int tmpUserNum=roomVc.get(myRoomIndex).preNum;
							int maxUsetNum=roomVc.get(myRoomIndex).capaNum;
							roomVc.get(myRoomIndex).readyNum++;
							System.out.println("현재방에있는사람수"+tmpUserNum+","+id+"님 준비, 준비한 사람 수:"
									+roomVc.get(myRoomIndex).readyNum);		//1명일 때 humanNum=1
							if(roomVc.get(myRoomIndex).readyNum>=maxUsetNum)
							{
								roomVc.get(myRoomIndex).inRoomVc.get(0).messageTo(Function.ROOMREADYBUTTON+"|");
								messageTo(Function.ROOMREADY+"|");
							}
							else//모두ready는 하지 않았지만 나는 ready하면
							{
								messageTo(Function.ROOMREADY+"|");	
							}
							messageRoom(Function.ROOMCHAT+"|"+"["+id+"]"+"님 준비완료",myRoomIndex);	//게임룸채팅방에 뿌리기
						}
						break;
						case Function.ROOMSTART:
						{
							/*게임 시작*/
							System.out.println("IN-ROOMSTART");
							//방상태 바꾸기
							roomVc.get(myRoomIndex).pos="게임중";
							String tmpRoomPos=roomVc.get(myRoomIndex).pos;
							//대기실 게임상태를 게임중으로 바꾸기
							messageAll(Function.CHGROOMSTATE+"|"+myRoomIndex+"|"+tmpRoomPos); //게임중이라고 표시
							//방내 게임스타트 메시지 보내기
							messageRoom(Function.ROOMCHAT+"|"+"게임 START", myRoomIndex);
							messageRoom(Function.EXITFALSE+"|",myRoomIndex);		//방나가기 비활성화
							int tmpInRoomUser=roomVc.get(myRoomIndex).preNum;
							messageRoom(Function.TURNINFO+"|"+roomVc.get(myRoomIndex).inRoomVc.get(0).id
									+"|"+roomVc.get(myRoomIndex).inRoomVc.get(1).id
									+"|"+roomVc.get(myRoomIndex).inRoomVc.get(2).id
									+"|"+roomVc.get(myRoomIndex).inRoomVc.get(3).id, myRoomIndex);
							for(int i=0; i<tmpInRoomUser; i++) //방사람에게 차례 지정해서 보내주기
							{
								roomVc.get(myRoomIndex).inRoomVc.get(i).messageTo(Function.ROOMCHAT+"|"+"당신은 "+(i+1)+"번째 입니다");
								//ID라벨에 입력
								messageRoom(Function.IDLABEL+"|"+roomVc.get(myRoomIndex).inRoomVc.get(i).id,myRoomIndex);
								/*게임 관련*/
								//게임방 클래스의 id 저장 장소의 각 스레드가 갖고있는 id를 저장
								roomVc.get(myRoomIndex).Player[i]=
										roomVc.get(myRoomIndex).inRoomVc.get(i).id;
							}
							
							//게임 시작후 게임관련 처리 추가할 자리
							/*게임 관련*/
							roomVc.get(myRoomIndex).GameInit(); // 게임초기화
							roomVc.get(myRoomIndex).NowPlayer=0; //게임 시작은 0번 부터
							messageRoom(Function.ROOMCHAT+"|"+
									roomVc.get(myRoomIndex).Player[roomVc.get(myRoomIndex).NowPlayer]+"님 차례입니다.", 
									myRoomIndex);
							//messageRoom(Function.GAMESTART+"|",myRoomIndex);	
							messageTo(Function.YOURTURN+"|");		//카드뒤집기버튼활성화
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
							/*방삭제 관리*/
							
							System.out.println("IN->EXITROOM");
							System.out.println("myRoomIndex->"+myRoomIndex);
							System.out.println(roomVc.get(0).preNum);
							System.out.println(roomVc.get(myRoomIndex).preNum);
							System.out.println("IN->EXITROOM2");
							posUser="대기실";
							System.out.println("EXITROOM Number:"+myRoomIndex);
							//방에 현재인원 감소시컴
							System.out.println(myRoomIndex+"1.방의 현재 인원은:"+roomVc.get(myRoomIndex).preNum);
							(roomVc.get(myRoomIndex).preNum)--;
							int tmpUserCount=roomVc.get(myRoomIndex).preNum; //감소 후 인원 임시 저장
							System.out.println(myRoomIndex+"2.방의 현재 인원은:"+roomVc.get(myRoomIndex).preNum);
							//대기실로 바꿔줌
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
								if((waitVc.get(myRow).id).equals(id)) //i번째 id와 내 id 가 같으면.
									break;
								myRow++;
							}
							//나의 row(몇번째 유저)를 보내 해당 row의 상태를 변경하도록 클라이언트에 보냄
							messageAll(Function.CHGUSERPOS+"|"+myRow+"|"+posUser);
							messageRoom(Function.ROOMCHAT+"|"+id+"님이 퇴장하였습니다.",myRoomIndex);
							//현재 방 갯수 파악
							int tmpRoomCount=preRoomCount();
							
							if(tmpUserCount<=0) //방에 남은 사람이 없으면
							{
								messageAll(Function.DELROOM+"|"+myRoomIndex);
								System.out.println("삭제 방 Index :"+myRoomIndex);
								
								/*내방 뒤에있던 클라이언트 쓰레드의 myRoomIndex를 감소시켜줘야함*/
								for(int i=(myRoomIndex+1);i<tmpRoomCount;i++)
								{
									int tmpInUser=roomVc.get(i).preNum;
									for(int j=0; j<tmpInUser; j++)
									{
										(roomVc.get(i).inRoomVc.get(j).myRoomIndex)--;
									}
								}
								System.out.println("roomVc 에서 해당 방 삭제");
								roomVc.remove(myRoomIndex);
							}
							else //방에 남은 사람이 있으면
							{
								roomVc.get(myIndex).inRoomVc.remove(this);
							}
							myRoomIndex=-1; //아무방에도 들어가있지 않은 것으로
						}
						break;
						/*게임 관련*/
						case Function.CARDOPEN: //클라이언트에서 카드 뒤집기
						{

							Room tmpRoomClass=roomVc.get(myRoomIndex);   //방번호
							//0번자리에 오픈안된 13번 클라이언트 카드번호를 줘라
							tmpRoomClass.TurnCard[tmpRoomClass.NowPlayer][tmpRoomClass.TurnCardCount[tmpRoomClass.NowPlayer]++] = 
									tmpRoomClass.ClientCard[tmpRoomClass.NowPlayer][--tmpRoomClass.ClientCardCount[tmpRoomClass.NowPlayer]];
							//			                     
							if(tmpRoomClass.ClientCardCount[tmpRoomClass.NowPlayer]==0)
								//현재 플레이어의 카드가 뒤집은 이후 하나도 남지 않으면....
							{
								System.out.println("aby1");
								tmpRoomClass.dead[tmpRoomClass.NowPlayer]=true;   //죽음상태로 바꿈

								messageRoom(Function.UPDATEDEAD+"|"
										+tmpRoomClass.Player[tmpRoomClass.NowPlayer], myRoomIndex);   //죽은사람 상태변경 및 채팅창알림

								int result=tmpRoomClass.isEndGame();
								if(result != -1)
								{      //게임끝0,1,2,3<-승자

									System.out.println(tmpRoomClass.Player[result]+"승리");
									messageRoom(Function.ROOMCHAT+"|"+tmpRoomClass.Player[result]   //채팅방에 승자알림      
											+"님 승리!!", myRoomIndex);
									/*게임 승자에 대한 추가 처리 예정*/
									messageRoom(Function.GAMEEXIT+"|"
                               			 +tmpRoomClass.Player[result]+"|"+"님이 이겼습니다.",
                               			 myRoomIndex);
								}
								else
								{                //게임끝아님,dead[]=-1 
									tmpRoomClass.NextPlayer();
									messageRoom(Function.ROOMCHAT+"|"+tmpRoomClass.Player[tmpRoomClass.NowPlayer]      //다음차례알려줌
											+"님 차례 입니다.", myRoomIndex);
									tmpRoomClass.inRoomVc.get(tmpRoomClass.NowPlayer).messageTo(Function.YOURTURN+"|");   //다음차례
								}
							}
							else //더 뒤집을 카드가 남아있을 때
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
								messageRoom(Function.ROOMCHAT+"|"+tmpRoomClass.Player[tmpRoomClass.NowPlayer]		//다음차례알려줌
											+"님 차례 입니다.", myRoomIndex);
								tmpRoomClass.inRoomVc.get(tmpRoomClass.NowPlayer).messageTo(Function.YOURTURN+"|");
							}
						}
						break;
						case Function.BELL: //클라이언트가 종쳤을때
						{
							System.out.println("In->BELL");
							Room tmpRoomClass=roomVc.get(myRoomIndex);
							if(tmpRoomClass.isBell==true)
							{
								System.out.println("In->BELL1");
								messageTo(Function.ROOMCHAT+"|"+"당신이 늦었습니다");
							}
							else
							{
								System.out.println("In->BELL2");
								tmpRoomClass.isBell=true;
								messageRoom(Function.ROOMCHAT+"|"+id
										+"님이 종을 쳤습니다.", myRoomIndex);
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
			                        { 	// 맨위에 깔린 카드의 과일종류와
			                        	// 수를 구한다.
			                           int temp = tmpRoomClass.TurnCard[i][tmpRoomClass.TurnCardCount[i] - 1];
			                           tmpRoomClass.CardType[i] = temp / 14;
			                           tmpRoomClass.CardNum[i] = temp % 14;
			                        } 
			                        else
			                        { // 4장이 다 깔리지 않은경우를 대비해 0으로 초기화.
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
			                           { // 과일종류가
			                        	   // 더한다.
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
			                        { // 종치기성공시 깔린 카드를 다 가져간다.
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
			                            //bMan.sendToAll(userName + "님이 종치기에 성공했습니다.");
			                        	/*//messageRoom(Function.ROOMCHAT+"|"+id
												+"님이 종치기 성공하였습니다.", myRoomIndex);*/
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
			                            for(int m = tmpRoomClass.ClientCardCount[a]; m > 0; m--) // 카드// 섞기    
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
								if (!(tmpRoomClass.isSuccess)) { // 종치기 실패시 다른플레이어에게 카드를 한장씩 돌린다.
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
			                        		"님이 종치기에 실패했습니다.", myRoomIndex);*/
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
			                                    //bMan.sendToAll(Player[isEndGame()] + "님이 이겼습니다.");
			                                	 messageRoom(Function.GAMEEXIT+"|"
			                                			 +tmpRoomClass.Player[tmpRoomClass.isEndGame()]+"|"+"님이 이겼습니다.",
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
					/*접속되어있던 Client 접속 종료시*/
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
			for(Room room:roomVc)			//현재 있는 방갯수 세기 (1개만들었으니까 1개????)
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
		
		public synchronized void messageAll(String msg)		// 전체적으로 client에게 메세지 보냄
		{
			for(ClientThread client:waitVc)
			{
				client.messageTo(msg);
			}
		}
	}	
}
