package com.sist.common;

public class Function {
	/**/
	public static final int LOGIN=100;		//WaitRoom�� ������ ����Ʈ�� ������Ʈ
	public static final int MYLOG=110;		//�α��� �� WaitRoom���� â ����
	//public static final int
	
	//���Ӱ���
	
	/* ȸ������ */
	//ID �ߺ� üũ
	public static final int IDCHECK=200;
	public static final int NOTOVERLAP=201;
	public static final int OVERLAP=202;
	public static final int SUCCESSJOIN=203;
	//ID �ߺ�
	//ID �ߺ� X
	//ȸ������ �Ϸ�
	
	/*����*/
	//�����
	public static final int ROOMREADY=300;			//�濡�� �غ��ư ������ ��
	public static final int ROOMREADYBUTTON=301;	//��ΰ� �غ񴭷��� ��
	public static final int ROOMSTART=302;			//���۹�ư ������ ��
	
	//�游���
	public static final int MAKEROOM=400;		//�游��� Ȯ�ι�ư ������ ��//400|1|1|2��|���ӷ�
	public static final int ROOMINFORM=401;		//WaitRoom���Ͽ� ���� ������ �� ������Ʈ//401|1|2��|���Ӵ����
	public static final int JOINROOM=402;		//�濡 ��� �� ��
	public static final int ROOMCHAT=403;		//���ӷ뿡�� ä���� ��

	/*[���ο�����] �Ȱ� ����ϱ� ���� Protocol*/
	public static final int CHGROOMUSER=404; 	//�濡 �����ִ� ������� �����.
	public static final int CHGUSERPOS=405; 	//[�������º���] ������ �游��ų� �������� ���� ����� ���ӵ� ������ ������ �޽��� ����
	public static final int EXITROOM=406;		//[�泪����] ���ӷ뿡 �ִ� ������ ���ӷ��� ������
	public static final int DELROOM=407;		//[�泪����] �濡 �ִ� ����� ������ �� ��������
	public static final int CHGROOMSTATE=408;	//����� ����
	
	public static final int ROOMUSER=410;	//���ӹ� ��������Ʈ ������Ʈ
	public static final int OUTUSER=411;    //���ӹ� ��������Ʈ ����
	
	//ä�ð���
	public static final int WAITCHAT1=500;
	public static final int WAITCHAT2=501;	
	/*����*/
	public static final int BELL=600; 		//Ŭ���̾�Ʈ �� ��������
	public static final int CARDOPEN=601; 	//Ŭ���̾�Ʈ ī�� ����������
	public static final int BELLFAIL=602; 	//Ŭ���̾�Ʈ ������ ����
	public static final int BELLSUCCESS=603;	//Ŭ���̾�Ʈ ������ ����
	public static final int YOURTURN=604; 	//���� �˷��� (Ŭ���̾�Ʈ���� �޾����� ������ Ȱ��)
	public static final int CARDNUM=605;
	public static final int TURNINFO=606;
	public static final int UPDATEDEAD=607;
	public static final int GAMEEXIT=608;
	public static final int REPAINT=609;
	public static final int DEAD=610;
	public static final int GAMESTART=611;
	public static final int EXITFALSE=612;		//���ӽ��۽� �泪���� ��Ȱ��ȭ
	public static final int IDLABEL=613;		//���ӽ��۽� id�� �Է�
	// Client ���� ��
	public static final int CLIENTEXIT=900;
	public static final int DELROW=901;
}
