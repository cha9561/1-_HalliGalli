package com.sist.common;

public class Function {
	/**/
	public static final int LOGIN=100;		//WaitRoom의 접속자 리스트에 업데이트
	public static final int MYLOG=110;		//로그인 후 WaitRoom으로 창 생성
	//public static final int
	
	//게임관련
	
	/* 회원가입 */
	//ID 중복 체크
	public static final int IDCHECK=200;
	public static final int NOTOVERLAP=201;
	public static final int OVERLAP=202;
	public static final int SUCCESSJOIN=203;
	//ID 중복
	//ID 중복 X
	//회원가입 완료
	
	/*대기실*/
	//방관련
	public static final int ROOMREADY=300;		//방에서 준비버튼 눌렀을 때
	
	//방만들기

	public static final int MAKEROOM=400;		//방만들기 확인버튼 눌렀을 때//400|1|1|2명|게임룸
	public static final int ROOMINFORM=401;		//WaitRoom방목록에 새로 생성된 방 업데이트//401|1|2명|게임대기중
	public static final int JOINROOM=402;		//방에 들어 갈 때
	public static final int ROOMCHAT=403;		//게임룸에서 채팅할 때
	
	/*[방인원변경] 된것 출력하기 위한 Protocol*/
	public static final int CHGROOMUSER=404; 	//방에 들어와있는 사람수가 변경됨.
	
	//채팅관련
	public static final int WAITCHAT1=500;
	public static final int WAITCHAT2=501;	
	/*게임*/
	
	// Client 종료 시
	public static final int CLIENTEXIT=900;
	public static final int DELROW=901;
}
