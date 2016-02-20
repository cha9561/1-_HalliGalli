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
	String Type;		//공개비공개
	String pos;			//상태
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
		String id,name,sex,posUser;		//id,이름,성별,상태
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
						messageAll(Function.CLIENTEXIT+"|"+id+exitMsg);		//채팅창에 00님이 나가셨습니다 뿌리기 
						messageAll(Function.DELROW+"|"+delIndex);			//
						
						waitVc.remove(delIndex);
						
						interrupt();				
					}
					break;
					case Function.LOGIN:				//client가 로그인 버튼을 요청했을 때
					{
						id=st.nextToken();
						//name=st.nextToken();
						//sex=st.nextToken();
						posUser="대기실";
						// messageAll(Function.LOGIN+"|"+id+"|"+name+"|"+sex+"|"+pos);
						messageAll(Function.LOGIN+"|"+id+"|"+posUser);
						waitVc.addElement(this);
						messageTo(Function.MYLOG+"|"+id+"|"+posUser);
						for(ClientThread client:waitVc)			//다른 사용자들에게 나를 접속자 리스트에 올림
						{
							messageTo(Function.LOGIN+"|"+client.id+"|"+client.posUser);
						}
						for(GameRoom room:gameRoom)
						{
							messageTo(Function.ROOMINFORM+"|"+room.Type+"|"+room.name+"|"+room.humanNum+"|"+room.sCapaNum+"|"+room.pos);
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
					
					case Function.MAKEROOM:					//방만들기 확인버튼 눌렀을 때////////////////////////////합친것1
					{
						
						String roomType=st.nextToken();		//새로 만든 게임룸의 공개정보
						String roomName=st.nextToken();		//새로 만든 게임룸의 방이름
						//String nowNum=st.nextToken();		//새로 만든 게임룸의 현재인원
						String capaNumImsi=st.nextToken();	//새로 만든 게임룸의 최대인원(2명일때->2)

						GameRoom gr=new GameRoom();   	//게임룸 클래스 생성!(임시로 받기)
						gr.pos="게임대기중";				//게임룸의 상태정보
						gr.Type=roomType;				//새로 만든 게임룸의 공개정보 대입
						gr.name=roomName;				//새로 만든 게임룸의 방이름 대입
						gr.sCapaNum=capaNumImsi;		//새로 만든 게임룸의 최대인원 대입

						if(capaNumImsi.equals("2명"))	//2.방만들자마자 최대인원수 대입
						{
							gr.capaNum=2;				//sCapaNumr(string)과 동일한 값 가짐
						}
						else if(capaNumImsi.equals("3명"))
						{
							gr.capaNum=3;
						}else 
						{
							gr.capaNum=4;
						}
						int i=0;		
						posUser="게임룸";

						for(GameRoom room:gameRoom)			//현재 있는 방갯수 세기 (1개만들었으니까 1개????)
						{
							i++;
						}						
						gr.roomNum=i;					//gameroom 정보=>새로 만든 게임룸의 방번호 대입(1개만들었으니까 1번????)
						clientroomNumber=i;				//client 정보=>client가 있는 방 번호 대입(1번????)
						gameRoom.addElement(gr);		//게임룸 리스트에 새로 만든 게임룸 추가
						gr.humanNum=1;			//현재인원수=1 dafault값 주기
						gr.cliT[0]=this;		//client에 만든이 추가==방장
						System.out.println("방 번호:"+gr.roomNum+",만든사람 id:"+gr.cliT[0].id+",최대인원수:"+gr.capaNum
								+"현재인원수:"+gr.humanNum+",공개여부:"+gr.Type);
						
						messageTo(Function.MAKEROOM+"|"+id+"|"+roomName+"|"+gr.humanNum+"|"+gr.capaNum);//1.game창으로 전환
						messageAll(Function.ROOMINFORM+"|"+roomType+"|"+roomName+"|"+gr.humanNum+"|"+gr.capaNum+"|"+gr.pos);//2.방목록에 띄움
		
						/*3.[유저상태변경] ->*/
						int userRow=0;
						for(ClientThread client:waitVc)
						{
							if((waitVc.get(userRow).id).equals(id))
								break;
							userRow++;
						} //몇번째 유저인지 파악하여 List 의  변경 필요한 Row 값 알아내기 위해
						messageAll(Function.CHGUSERPOS+"|"+userRow+"|"+posUser);
					}
					break;

					
					case Function.JOINROOM:								//게임룸에 들어가기
					{
						String roomNum=st.nextToken();					//게임룸번호(0부터 시작)
						clientroomNumber=Integer.parseInt(roomNum);		//client가 있는 게임룸번호 부여
						int roomCapa=gameRoom.get(clientroomNumber).capaNum;		//게임룸의 최대인원(2명이 정원일 때  2)
						int humNum=gameRoom.get(clientroomNumber).humanNum;			//게임룸의 현재인원(방생성하자마자 현재인원은 1)

						String decision="FALSE";
						if(humNum<roomCapa)										//방이 꽉 차지 않았을 경우(1<2)
						{
							gameRoom.get(clientroomNumber).humanNum++;			//해당 방 번호에 현재인원+1(2명일때 2가 됨!!)
							humNum=gameRoom.get(clientroomNumber).humanNum;		//해당방에 증가된 현재인원 업데이트
							posUser="게임룸";
							gameRoom.get(clientroomNumber).cliT[humNum-1]=this;			//(2번째사람-> 1이 됨)
							decision="TRUE";				
							//
							System.out.println("cliT[0].id->"+gameRoom.get(clientroomNumber).cliT[0].id);
							System.out.println("cliT[1].id->"+gameRoom.get(clientroomNumber).cliT[1].id);
							System.out.println("최대인원수:"+roomCapa+"현재인원수:"+humNum);
							//	
					
							messageTo(Function.JOINROOM+"|"+decision+"|"+gameRoom.get(clientroomNumber).cliT[0].id+"|"+gameRoom.get(clientroomNumber).name);		//증가된 사람에게 true넘겨줌
							//messageTo(Function.JOINROOM+"|"+decision);		//증가된 사람에게 true넘겨줌
							messageRoom(Function.ROOMCHAT+"|"+id+"님이 입장하였습니다",clientroomNumber);	//모두에게 사람 입장 알리기,방번호 
							
							/*[방인원변경 -추가시] ->*/
							messageAll(Function.CHGROOMUSER+"|"+roomNum+"|"+humNum); //방인원이 변경된 방ID(table의 Row)+변경된 User수를 Client로 보냄
							/*[유저상태변경] ->*/
							int userRow=0;
							for(ClientThread client:waitVc)
							{
								if((waitVc.get(userRow).id).equals(id))
									break;
								userRow++;
							} //몇번째 유저인지 파악하여 List 의  변경 필요한 Row 값 알아내기 위해
							messageAll(Function.CHGUSERPOS+"|"+userRow+"|"+posUser);
						}
						else 				//방 꽉찼을 경우
						{
							System.out.println("first");
							messageTo(Function.JOINROOM+"|"+decision);		//들어가려는 사람에게 false넘겨줌
							System.out.println("second");
							clientroomNumber=-1;
						}
					}
					break;
					
					
					case Function.ROOMREADY:			//준비버튼 눌렀을 때
					{
						int humanNum=gameRoom.get(clientroomNumber).humanNum;	//현재인원
						int roomNum=gameRoom.get(clientroomNumber).roomNum;		//방번호

						gameRoom.get(clientroomNumber).readyNum++;			//ex_1명이준비했을 때 readyNum(준비한사람수)=1
						System.out.println("현재방에있는사람수"+humanNum+","+id+"님 준비, 준비한 사람 수:"
														+gameRoom.get(clientroomNumber).readyNum);		//1명일 때 humanNum=1	
			
						if(humanNum==gameRoom.get(clientroomNumber).readyNum){		//모든사람이 ready 하면				
							messageTest(Function.ROOMREADYBUTTON+"|",roomNum);
							messageTo(Function.ROOMREADY+"|");
						}else{														//모두ready는 하지 않았지만 나는 ready하면
							messageTo(Function.ROOMREADY+"|");	
						}		
						messageRoom(Function.ROOMCHAT+"|"+"["+id+"]"+"님 준비완료",roomNum);	//게임룸채팅방에 뿌리기
					}
					break;

					
					case Function.ROOMSTART:			//시작버튼 눌렀을 때
					{
						int roomNum=gameRoom.get(clientroomNumber).roomNum;		//방번호
						System.out.println("게임을 시작합니다!!!");
						posUser="게임중";
						messageAll(Function.CHGROOMSTATE+"|"+roomNum+"|"+posUser); //게임중이라고 표시
						messageRoom(Function.ROOMCHAT+"|"+"게임 START",roomNum);	//게임룸채팅방에 뿌리기
					}
					break;					

					/*[방나가기] ->*/
					case Function.EXITROOM:
					{
						posUser="대기실";
						System.out.println("humanNum=> "+gameRoom.get(clientroomNumber).humanNum);	//현재인원						
						gameRoom.get(clientroomNumber).humanNum--; 					//사람 감소 시킴
						int humNum=gameRoom.get(clientroomNumber).humanNum;			//변화된 현재인원
						System.out.println("humanNum=> "+gameRoom.get(clientroomNumber).humanNum);	
						
						messageTo(Function.MYLOG+"|"+id+"|"+posUser); 							//대기실 화면으로 바꾸기 위해
						messageAll(Function.CHGROOMUSER+"|"+clientroomNumber+"|"+humNum);	//방상태변화시키기 위해
						
						int userRow=0;
						for(ClientThread client:waitVc)
						{
							if((waitVc.get(userRow).id).equals(id))
								break;
							userRow++;
						} //몇번째 유저인지 파악하여 List 의  변경 필요한 Row 값 알아내기 위해
						messageAll(Function.CHGUSERPOS+"|"+userRow+"|"+posUser); 						//타 유저에 내상태 변경하도록
						messageRoom(Function.ROOMCHAT+"|"+id+"님이 퇴장하였습니다",clientroomNumber);	//방사람에게 퇴장메시지 보내기
						
						if(gameRoom.get(clientroomNumber).humanNum<=0) //방에 남은 사람이 없으면
						{
							messageAll(Function.DELROOM+"|"+clientroomNumber);	
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
		
		public synchronized void messageTest(String msg, int roomIndex)
		{
			gameRoom.get(roomIndex).cliT[0].messageTo(msg);
		}
	}

}
/*회원관리*/
