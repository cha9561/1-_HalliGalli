package com.sist.server;
import java.util.*;
import com.sist.common.Function;
import com.sist.server.Server.ClientThread;

import java.io.*;
import java.net.*;

class GameRoom		//게임룸 정보 클래스 
{
	int roomNum;
	String sCapaNum;
	int capaNum;
	int humanNum;
	String name;
	ClientThread cliT[] = new ClientThread[3];

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
							messageTo(Function.ROOMINFORM+"|"+room.name+"|"+room.sCapaNum+"|"+"게임대기중");
						}
						//messageTo(Function.MAKEROOM2+"|"+roomName+"|"+num+"|"+"게임대기중");
						// 방정보 전송 
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
						messageAll(Function.ROOMCHAT+"|["+id+"]"+data);
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
						String roomName=st.nextToken();		//새로 만든 게임룸의 이름
						String capaNum=st.nextToken();		//새로 만든 게임룸의  제한인원수
						GameRoom gr=new GameRoom();   	//게임룸 클래스 생성!(임시로 받기)
						gr.sCapaNum=capaNum;			//새로 만든 게임룸의 제한인원수 대입
						if(capaNum.equals("2명"))
						{
							gr.capaNum=1;
						}
						else if(capaNum.equals("3명"))
						{
							gr.capaNum=2;
						}else //4명
						{
							gr.capaNum=3;
						}
						String pos="게임룸";
						int i=0;
						gr.name=roomName;					//새로 만든 게임룸의 방이름 대입
						for(GameRoom room:gameRoom)			//현재 있는 방갯수 세기 
						{
							i++;
						}
						
						gr.roomNum=i;					//새로 만든 게임룸의 방번호 대입
						gameRoom.addElement(gr);		//게임룸 리스트에 새로 만든 게임룸 추가(정보포함)(영구저장)
						System.out.println("방 번호는 :"+i);
						
						gr.cliT[0]=this;

						messageTo(Function.MAKEROOM+"|"+id+"|"+roomName+"|"+capaNum+"|"+pos);	//방을 만든 사람에게만			
						messageAll(Function.ROOMINFORM+"|"+roomName+"|"+capaNum+"|"+"게임대기중");	//모두에게
					}
					break;
					case Function.JOINROOM:
					{
						String roomNum=st.nextToken();
						int roomNumber=Integer.parseInt(roomNum);
						/*방 사람 찼는지 판별 필요*/
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
							messageRoom(Function.ROOMCHAT+"|"+id+"님이 입장하였습니다",roomNumber);
							
						}
						else //방 꽉참.
						{
							messageTo(Function.JOINROOM+"|"+decision);
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
		
		public synchronized void messageRoom(String msg, int roomIndex)		// 전체적으로 client에게 메세지 보냄
		{
			for(int i=0; i<=gameRoom.get(roomIndex).humanNum;i++)
			{
				gameRoom.get(roomIndex).cliT[i].messageTo(msg);
			}
		}
	}

}
/*회원관리*/
