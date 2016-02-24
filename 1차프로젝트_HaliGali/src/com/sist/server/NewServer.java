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
	int capaNum;		//들어갈 수 있는 최대인원 2~4
	int preNum;			//현재 인원 1~5
	String name;		//방이름
	int readyNum;		//준비 누른 인원
	String Type;		//공개 비공개
	String pos;
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
		// TODO Auto-generated method stub
		Server server=new Server();		        // 서버 가동
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
							int roomCount=preRoomCount(); //현재 방수 파악 1개 -> 1
							myRoomIndex=roomCount;
							System.out.println(id+"의 방번호는(0부터 시작):"+myRoomIndex);
							room.preNum=1;
							room.inRoomVc.addElement(this);
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
								roomVc.get(tmpIndex).inRoomVc.addElement(this);
								decision="TRUE";
								String tmpMakerId=roomVc.get(tmpIndex).inRoomVc.get(0).id;
								String tmpRoomName=roomVc.get(tmpIndex).name;
								messageTo(Function.JOINROOM+"|"+decision+"|"+tmpMakerId+"|"+tmpRoomName);		//증가된 사람에게 true넘겨줌
								
								messageRoom(Function.ROOMCHAT+"|"+id+"님이 입장하였습니다",tmpIndex);
								myRoomIndex=tmpIndex;
								messageAll(Function.CHGROOMUSER+"|"+myRoomIndex+"|"+tmpPreNum);
								
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
							int tmpInRoomUser=roomVc.get(myRoomIndex).preNum;
							for(int i=0; i<tmpInRoomUser; i++) //방사람에게 차례 지정해서 보내주기
							{
								roomVc.get(myRoomIndex).inRoomVc.get(i).messageTo(Function.TURNINFO+"|"+"당신은 "+(i+1)+"번째 입니다");
								
							}
							//게임 시작후 게임관련 처리 추가할 자리
							
						}
						break;
						case Function.EXITROOM:
						{
							/*방삭제 관리*/
							System.out.println("IN->EXITROOM");
							posUser="대기실";
							System.out.println("EXITROOM Number:"+myRoomIndex);
							//방에 현재인원 감소시컴
							System.out.println(myRoomIndex+"1.방의 현재 인원은:"+roomVc.get(myRoomIndex).preNum);
							(roomVc.get(myRoomIndex).capaNum)--;
							int tmpUserCount=roomVc.get(myRoomIndex).preNum; //감소 후 인원 임시 저장
							System.out.println(myRoomIndex+"2.방의 현재 인원은:"+roomVc.get(myRoomIndex).preNum);
							//대기실로 바꿔줌
							messageTo(Function.MYLOG+"|"+id+"|"+posUser); 	
							messageAll(Function.CHGROOMUSER+"|"+myRoomIndex+"|"+tmpUserCount);
							
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
										(roomVc.get(i).inRoomVc.get(j).myIndex)--;
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
