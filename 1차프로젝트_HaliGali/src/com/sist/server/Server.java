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
		String id,name,sex,pos;		//id,이름,성별,상태
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
						/*게임 방에 있을때 messageAll 재정의 필요*/
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
					
					case Function.MAKEROOM:					//방만들기 확인버튼 눌렀을 때////////////////////////////
					{
						pos="게임대기중";					//게임룸 만든 사람의 상태정보
						String roomType=st.nextToken();		//새로 만든 게임룸의 공개정보
						String roomName=st.nextToken();		//새로 만든 게임룸의 방이름
						//String nowNum=st.nextToken();		//새로 만든 게임룸의 현재인원
						String capaNumImsi=st.nextToken();	//새로 만든 게임룸의 최대인원(2명일때->2)

						GameRoom gr=new GameRoom();   	//게임룸 클래스 생성!(임시로 받기)
	
						gr.Type=roomType;				//새로 만든 게임룸의 공개정보 대입
						gr.name=roomName;				//새로 만든 게임룸의 방이름 대입
						//gr.nnum=nowNum;					//새로 만든 게임룸의 현재인원 대입(1명)
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
						for(GameRoom room:gameRoom)			//현재 있는 방갯수 세기 (1개만들었으니까 1개????)
						{
							i++;
						}						
						gr.roomNum=i;					//gameroom 정보=>새로 만든 게임룸의 방번호 대입(1개만들었으니까 1번????)
						clientroomNumber=i;				//client 정보=>client가 있는 방 번호 대입(1번????)
						gameRoom.addElement(gr);		//게임룸 리스트에 새로 만든 게임룸 추가
						gr.humanNum=1;			//현재인원수=1 dafault값 주기
						gr.cliT[0]=this;		//client에 만든이 추가==방장
						System.out.println("방 번호:"+gr.roomNum+",만든사람 id:"+gr.cliT[0]+",최대인원수:"+gr.capaNum
								+"현재인원수:"+gr.humanNum+",공개여부:"+gr.Type);
						
						//game창으로 전환
						//messageTo(Function.MAKEROOM+"|"+id+"|"+roomType+"|"+roomName+"|"+gr.humanNum+"|"+gr.capaNum+"|"+pos);	
						messageTo(Function.MAKEROOM+"|"+id+"|"+roomName+"|"+gr.humanNum+"|"+gr.capaNum);
						//방목록에 띄움
						messageAll(Function.ROOMINFORM+"|"+roomType+"|"+roomName+"|"+gr.humanNum+"|"+gr.capaNum+"|"+"게임룸");
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
							pos="게임룸";
							gameRoom.get(clientroomNumber).cliT[humNum-1]=this;			//(2번째사람-> 1이 됨)
							decision="TRUE";				
							//
							System.out.println("cliT[0].id->"+gameRoom.get(clientroomNumber).cliT[0].id);
							System.out.println("cliT[1].id->"+gameRoom.get(clientroomNumber).cliT[1].id);
							System.out.println("최대인원수:"+roomCapa+"현재인원수:"+humNum);
							//	
							
							messageTo(Function.JOINROOM+"|"+decision+"|"+gameRoom.get(clientroomNumber).cliT[0].id+"|"+gameRoom.get(clientroomNumber).name);		//증가된 사람에게 true넘겨줌
							messageRoom(Function.ROOMCHAT+"|"+id+"님이 입장하였습니다",clientroomNumber);	//모두에게 사람 입장 알리기,방번호 
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
						messageRoom(Function.ROOMCHAT+"|"+"게임 START",roomNum);	//게임룸채팅방에 뿌리기
					}
					break;
					
					case Function.ROOMBACK:				//나가기버튼 눌렀을 때
					{
						int roomNum=gameRoom.get(clientroomNumber).roomNum;		//방번호		
						int lastIdx=gameRoom.get(clientroomNumber).humanNum-1;	//마지막 index
						messageTo(Function.ROOMBACK+"|"+id);					//WaitRoom으로 이동
						
						for(int i=0; i<gameRoom.get(clientroomNumber).humanNum; i++){		
							if((gameRoom.get(clientroomNumber).cliT[i].id).equals(id)){		//나가려는 사람이 방에 있으면
								gameRoom.get(clientroomNumber).humanNum--;					//현재인원--1;		
								System.out.println(gameRoom.get(clientroomNumber).humanNum+"<-이만큼 인원이 남았습니다!"+
										gameRoom.get(clientroomNumber).cliT[i].id+"<-나간사람");			//테스트용
								
								if(gameRoom.get(clientroomNumber).humanNum==0){				//방에사람이 아무도 없으면
									System.out.println("방폭발");
									messageAll(Function.ROOMINFORMDELETE+"|"+gameRoom.get(clientroomNumber).name);
									return;
								}
								
								for(int j=i; j<gameRoom.get(clientroomNumber).humanNum; j++){	//나간사람 인덱스를 뒤에사람이 대신함
									gameRoom.get(clientroomNumber).cliT[j]=gameRoom.get(clientroomNumber).cliT[j+1];
								}
								gameRoom.get(clientroomNumber).cliT[lastIdx]=null;		//마지막인덱스에 null값 부여	
							}
						}
						System.out.println(gameRoom.get(clientroomNumber).humanNum+"명이 현재 남음");		//테스트용
						System.out.println("0번"+gameRoom.get(clientroomNumber).cliT[0].id);
						System.out.println("1번"+gameRoom.get(clientroomNumber).cliT[1].id);
						System.out.println("2번"+gameRoom.get(clientroomNumber).cliT[2].id);
						System.out.println("3번"+gameRoom.get(clientroomNumber).cliT[3].id);
						
						
						messageRoom(Function.ROOMCHAT+"|"+"["+id+"]"+"님 퇴장",roomNum);		//게임룸의 채팅방
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
			for(int i=0; i<=gameRoom.get(roomIndex).humanNum;i++)			//해당 방의 사람들에게 메세지 보냄
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
