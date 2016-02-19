package com.sist.server;
import java.util.*;
import com.sist.common.Function;
import com.sist.server.Server.ClientThread;

import java.io.*;
import java.net.*;

class GameRoom		//게임룸 정보 클래스 
{

	int roomNum;		//방번호
	String sCapaNum;	//
	int capaNum;		//들어갈 수 있는 최대인원
	int humanNum;		//현재인원
	String name;		//방이름
	ClientThread cliT[] = new ClientThread[4];	//방에있는 플레이어들
	int readyNum=0;		//준비 누른 인원
	String Type;
	String nnum;
}
public class Server implements Runnable{

	Vector<ClientThread> waitVc=new Vector<ClientThread>();		//사용자 배열
	Vector<GameRoom> gameRoom=new Vector<GameRoom>();			//게임룸 배열
	
	ServerSocket ss=null;// 서버에서 접속시 처리 (교환 소켓)
	
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
		Server server=new Server();		        // 서버 가동
		new Thread(server).start();
	}
	
	
	
	class ClientThread extends Thread
	{
		String id,name,sex,pos;
		Socket s;
		BufferedReader in;	// client요청값을 읽어온다
		OutputStream out;	//client로 결과값을 응답할때 
		int clientroomNumber;		//client가 있는 방 번호

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

					case Function.CLIENTEXIT:
					{
						System.out.println("종료 메시지 받음");
						String exitMsg="님이 나갔습니다.";
						s.close();
						messageAll(Function.CLIENTEXIT+"|"+id+exitMsg);
						
						messageAll(Function.DELROW+"|"+delIndex);
						
						waitVc.remove(delIndex);
						
						interrupt();				
					}
					break;
					case Function.LOGIN:				//client가 로그인 버튼을 요청했을 때
					{
						id=st.nextToken();
						//name=st.nextToken();
						//sex=st.nextToken();
						pos="대기실";
						// messageAll(Function.LOGIN+"|"+id+"|"+name+"|"+sex+"|"+pos);
						messageAll(Function.LOGIN+"|"+id+"|"+pos);
						waitVc.addElement(this);
						messageTo(Function.MYLOG+"|"+id+"|"+pos);
						for(ClientThread client:waitVc)			//다른 사용자들에게 나를 접속자 리스트에 올림
						{
							messageTo(Function.LOGIN+"|"+client.id+"|"+client.pos);
						}
						for(GameRoom room:gameRoom)
						{
							messageTo(Function.ROOMINFORM+"|"+room.Type+"|"+room.name+"|"+room.nnum+"|"+room.sCapaNum+"|"+"게임대기중");
						}
					}
					break;

					case Function.WAITCHAT1:			//client가 채팅전송을 요청했을 때(waitroom)
					{
						String data=st.nextToken();
						messageAll(Function.WAITCHAT1+"|["+id+"]"+data);
					}
					break;


					case Function.ROOMCHAT:			//client가 채팅전송을 요청했을 때(gamewindow)
					{
						String data=st.nextToken();
						messageRoom(Function.ROOMCHAT+"|["+id+"]"+data,clientroomNumber);
						//messageRoom(Function.ROOMCHAT+"|"+id+"님이 입장하였습니다",roomNumber);
					}
					break;

					case Function.IDCHECK:			//client가 ID중복체크를 요청했을 때

					{
						System.out.println("ID중복체크");
						String id=st.nextToken();
						System.out.println(id);
						/*ID 중복 체크 구현부*/
					}
					break;
					case Function.SUCCESSJOIN:
					{
						System.out.println("회원 정보 추가");
						String name=st.nextToken();
						String id=st.nextToken();
						String pass=st.nextToken();
					}
					break;
					/*회원관리*/
					
					case Function.MAKEROOM:					//방만들기 확인버튼 눌렀을 때
					{
						pos="게임방";
						String roomType=st.nextToken();		//새로 만든 게임룸의 공개정보
						String roomName=st.nextToken();		//새로 만든 게임룸의 이름
						String nowNum=st.nextToken();		//새로 만든 게임룸의 현재원수
						String capaNumImsi=st.nextToken();	//새로 만든 게임룸의  제한인원수(2명일때->2)
						

						GameRoom gr=new GameRoom();   	//게임룸 클래스 생성!(임시로 받기)

						//gr.sCapaNum=capaNum;			//새로 만든 게임룸의 제한인원수 대입(String으로)
	
						gr.Type=roomType;				//새로 만든 게임룸의 공개정보 대입
						gr.nnum=nowNum;					//새로 만든 게임룸의 현재인원 대입
						gr.sCapaNum=capaNumImsi;			//새로 만든 게임룸의 제한인원수 대입
						System.out.println("-->"+capaNumImsi);
						if(capaNumImsi.equals("2명"))	//2.방만들자마자 최대인원수 대입
						{
							gr.capaNum=2;
						}
						else if(capaNumImsi.equals("3명"))
						{
							gr.capaNum=3;
						}else 
						{
							gr.capaNum=4;
						}
						
						String pos="게임방";
						int i=0;
						gr.name=roomName;					//1.새로 만든 게임룸의 방이름 대입
						for(GameRoom room:gameRoom)			//현재 있는 방갯수 세기 (1개만들었으니까 1개????)
						{
							i++;
						}
						gr.roomNum=i;					//새로 만든 게임룸의 방번호 대입(1개만들었으니까 1개????)
						clientroomNumber=i;				//client가 있는 방 번호 대입(1번????)
						gameRoom.addElement(gr);		//게임룸 리스트에 새로 만든 게임룸 추가
						
						System.out.println("방 번호는 :"+i);
						//
						gr.humanNum++;			//현재인원수 +1(2명방을 만들었을 때 현재인원수=1)
						System.out.println("방 번호는 :"+i);		//0개??????????	
						gr.cliT[0]=this;		//게임룸  0번째 클라이언트에 만든이 추가
						System.out.println("만든사람 id출력"+gr.cliT[0].id+",최대인원수:"+gr.capaNum+"현재인원수:"+gr.humanNum);//만든사람id출력
						//
						gr.cliT[0]=this;
						System.out.println("capa"+gr.capaNum);
						messageTo(Function.MAKEROOM+"|"+id+"|"+roomType+"|"+roomName+"|"+nowNum+"|"+gr.capaNum+"|"+pos);	//방을 만든 사람에게만			
						messageAll(Function.ROOMINFORM+"|"+roomType+"|"+roomName+"|"+nowNum+"|"+gr.capaNum+"|"+"게임대기중");	//모두에게
						
						/*[유저상태변경] ->*/
						int userRow=0;
						for(ClientThread client:waitVc)
						{
							if((waitVc.get(userRow).id).equals(id))
								break;
							userRow++;
						} //몇번째 유저인지 파악하여 List 의  변경 필요한 Row 값 알아내기 위해
						messageAll(Function.CHGUSERPOS+"|"+userRow+"|"+pos);
						/*<- [유저상태변경]*/
					}
					break;
					
					case Function.JOINROOM:								//게임룸에 들어가기
					{

						String roomNum=st.nextToken();					//게임룸번호(0부터 시작)
						clientroomNumber=Integer.parseInt(roomNum);		//client가 있는 게임룸번호 부여
						int roomCapa=gameRoom.get(clientroomNumber).capaNum;		//게임룸의 최대인원(2명이 정원일 때  2)
						int humNum=gameRoom.get(clientroomNumber).humanNum;			//게임룸의 현재인원(2명이 정원일 때 방생성하자마자 현재인원은 1이됨)

						String decision="FALSE";
						if(humNum<roomCapa)										//방이 꽉 차지 않았을 경우(1<2)
						{

							gameRoom.get(clientroomNumber).humanNum++;			//해당 방 번호에 현재인원+1(2명일때 2가 됨!!)
							humNum=gameRoom.get(clientroomNumber).humanNum;		//해당방에 증가된 현재인원 업데이트
							pos="게임방";

							gameRoom.get(clientroomNumber).cliT[humNum-1]=this;			//(2번째사람-> 1이 됨)
							decision="TRUE";
							
							//
							System.out.println("1111->"+gameRoom.get(clientroomNumber).cliT[0].id);
							System.out.println("2222->"+gameRoom.get(clientroomNumber).cliT[1].id);
							System.out.println("최대인원수:"+gameRoom.get(clientroomNumber).capaNum+"현재인원수:"+gameRoom.get(clientroomNumber).humanNum);
							//	
							messageTo(Function.JOINROOM+"|"+decision);		//증가된 사람에게 true넘겨줌
							messageRoom(Function.ROOMCHAT+"|"+id+"님이 입장하였습니다",clientroomNumber);	//모두에게 사람 입장 알리기,방번호 
							
							/*[방인원변경 -추가시] ->*/
							messageAll(Function.CHGROOMUSER+"|"+roomNum+"|"+humNum); //방인원이 변경된 방ID(table의 Row)+변경된 User수를 Client로 보냄
							/*<- [방인원변경 -추가시]*/
							/*[유저상태변경] ->*/
							int userRow=0;
							for(ClientThread client:waitVc)
							{
								if((waitVc.get(userRow).id).equals(id))
									break;
								userRow++;
							} //몇번째 유저인지 파악하여 List 의  변경 필요한 Row 값 알아내기 위해
							messageAll(Function.CHGUSERPOS+"|"+userRow+"|"+pos);
							/*<- [유저상태변경]*/
						}
						else 				//방 꽉찼을 경우
						{

							messageTo(Function.JOINROOM+"|"+decision);		//들어가려는 사람에게 false넘겨줌
							clientroomNumber=-1;
						}
					}
					break;
					case Function.ROOMREADY:			//준비버튼 눌렀을 때
					{
						System.out.println("현재방에있는사람수"+gameRoom.get(clientroomNumber).humanNum);		//1명일 때 humanNum=1
						for(int j=0; j<gameRoom.get(clientroomNumber).humanNum; j++){			//각 게임방의 현재 인원수 만큼 돌려라
								if((gameRoom.get(clientroomNumber).cliT[j].id).equals(id)){
									System.out.println(id+"준비");
									messageRoom(Function.ROOMCHAT+"|"+"["+id+"]"+"님 준비완료",gameRoom.get(clientroomNumber).roomNum);	//게임룸채팅방에 뿌리기
									
									gameRoom.get(clientroomNumber).readyNum++;			//readyNum(준비한사람수)=1(1명이준비했을 때)
									System.out.println("준비한 사람 수:"+gameRoom.get(clientroomNumber).readyNum+"\n");
									return;
								}
						}
					}
					break;
					/*[방나가기] ->*/
					case Function.EXITROOM:
					{
						pos="대기실";
						System.out.println("humanNum=> "+gameRoom.get(clientroomNumber).humanNum);
						
						gameRoom.get(clientroomNumber).humanNum--; //사람 감소 시킴
						int humNum=gameRoom.get(clientroomNumber).humanNum;
								
						System.out.println("humanNum=> "+gameRoom.get(clientroomNumber).humanNum);
						
						messageTo(Function.MYLOG+"|"+id+"|"+pos); 			//대기실 화면으로 바꾸기 위해
						messageAll(Function.CHGROOMUSER+"|"+clientroomNumber+"|"+humNum);
						
						int userRow=0;
						for(ClientThread client:waitVc)
						{
							if((waitVc.get(userRow).id).equals(id))
								break;
							userRow++;
						} //몇번째 유저인지 파악하여 List 의  변경 필요한 Row 값 알아내기 위해
						messageAll(Function.CHGUSERPOS+"|"+userRow+"|"+pos); //타 유저에 내상태 변경하도록
						messageRoom(Function.ROOMCHAT+"|"+id+"님이 퇴장하였습니다",clientroomNumber);	//방사람에게 퇴장메시지 보내기
						
						if(gameRoom.get(clientroomNumber).humanNum<=0) //방에 남은 사람이 없으면
						{
							messageAll(Function.DELROOM+"|"+clientroomNumber+"|");
							//벡터에서 삭제
							gameRoom.removeElementAt(clientroomNumber);
						}
						else
						{
							//방나간 클라이언트를 배열에서 삭제하고 뒷 클라이언트를 앞으로 땡김
							int userCount= gameRoom.get(clientroomNumber).humanNum;

							for(int i=0; i<=userCount; i++) //방에서 나간 Thread 를 Room Vector의 배열에서 삭제하기 위해
							{
								System.out.println("I는------>"+i);
								if((gameRoom.get(clientroomNumber).cliT[i].id).equals(id))
								{
									System.out.println("삭제할 I는------>"+i);
									if(i==userCount)
									{
										gameRoom.get(clientroomNumber).cliT[i]=null;
										break;
									}
									else
									{
										for(int j=userCount;j>i;j--)
										{
											System.out.println("J는------>"+j);
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
					/*<- [방나가기]*/
					}
				}catch(Exception ex)
				{
					/*접속되어있던 Client 접속 종료시*/
					interrupt();
				}
			}
		}

		// 개인적으로 client에게 메세지 보냄
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
		
		public synchronized void messageAll(String msg)		// 전체적으로 client에게 메세지 보냄
		{
			for(ClientThread client:waitVc)
			{
				client.messageTo(msg);
			}
		}
		
		public synchronized void messageRoom(String msg, int roomIndex)		//해당 방의 사람들에게 메세지 보냄(메세지,방번호)
		{
			for(int i=0; i<gameRoom.get(roomIndex).humanNum;i++)			//해당 방의 사람들에게 메세지 보냄 /*bugfix '<=gameRoom.get(roomIndex)'면 안됨*/
			{
				gameRoom.get(roomIndex).cliT[i].messageTo(msg);
			}
		}
	}

}
/*회원관리*/
