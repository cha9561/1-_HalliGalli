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
	public static final int ROOMREADY=300;		//�濡�� �غ��ư ������ ��
	
	//�游���

	public static final int MAKEROOM=400;		//�游��� Ȯ�ι�ư ������ ��//400|1|1|2��|���ӷ�
	public static final int ROOMINFORM=401;		//WaitRoom���Ͽ� ���� ������ �� ������Ʈ//401|1|2��|���Ӵ����
	public static final int JOINROOM=402;		//�濡 ��� �� ��
	public static final int ROOMCHAT=403;		//���ӷ뿡�� ä���� ��
	
	/*[���ο�����] �Ȱ� ����ϱ� ���� Protocol*/
	public static final int CHGROOMUSER=404; 	//�濡 �����ִ� ������� �����.
	
	//ä�ð���
	public static final int WAITCHAT1=500;
	public static final int WAITCHAT2=501;	
	/*����*/
	
	// Client ���� ��
	public static final int CLIENTEXIT=900;
	public static final int DELROW=901;
}
