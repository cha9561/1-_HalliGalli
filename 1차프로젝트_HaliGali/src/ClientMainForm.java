import java.awt.*; 		//Layout�������
import javax.swing.*;	//window���� ��ư����� �������
import java.awt.event.*;

public class ClientMainForm extends JFrame implements ActionListener{
		CardLayout card=new CardLayout();			//â ��ȯ
		Loading ld=new Loading();					//LOADINGâ
		Login login=new Login();					//LOGINâ
		WaitRoom wr=new WaitRoom();					//WAITROOMâ
		GameWindow gw=new GameWindow();				//����WINDOWâ
		MakeID mID=new MakeID();					//ȸ������â
		MakeRoom mr=new MakeRoom();					//�游���â
		
		
		public ClientMainForm(){

			setLayout(card);		//BorderLayout
			
			add("LOG",login);		//2.loginâ
			setSize(800,600);		//windowâ ũ�� ����
			setLocation(270, 170);	//windowâ ��ġ ����
			setVisible(true);		//�������� ��
			setResizable(false);    //windowâ ����(�þ�� ����)
					
			add("WR",wr);						//3.WaitRoomâ
			login.bt1.addActionListener(this);	//ȸ������ ��ư ������
			login.bt2.addActionListener(this);	//�α��� ��ư ������
			wr.b1.addActionListener(this);		//�α��� ��ư ������
			wr.b2.addActionListener(this);		//���� ��ư ������
			wr.b3.addActionListener(this);      //�游��� ��ư ������
			wr.tf.addActionListener(this);		//����� �Է°� ������ 
			
			add("GW",gw);						//4.GAME Windowâ
			wr.b3.addActionListener(this); 		//�游����ư ������
			
	
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource()==login.bt1){				    //��ư(bt1)�� �׼��� ��������
				mID.setBounds(470, 310,340,420);
				mID.setVisible(true);											//�˾�â���� ȸ������â ����
			}else if(e.getSource()==login.bt2){
				card.show(getContentPane(), "WR");			//Layout�� "WR"card�� ������� �������� �϶�
			}else if(e.getSource()==wr.tf || e.getSource()==wr.b1){			//ä��â(tf)�� �׼��� ��������
				String data=wr.tf.getText();								//�Է��� �� ��������
				wr.ta.append(data+"\n");
				wr.tf.setText("");
			}else if(e.getSource()==wr.b3){					//�游����ư->GAME Windowâ
				mr.setBounds(500, 300, 260,290);
		        mr.setVisible(true);
			}else if(e.getSource()==wr.b2){
				card.show(getContentPane(), "GW"); 		//���� â���� ��ȯ
			}
		}
		
		
		
		public static void main(String[] args) {
			 
			try{
				//UIManager.setLookAndFeel("com.jtattoo.plaf.noire.NoireLookAndFeel");	//�������� �ܾ�°�
			}catch(Exception ex){}
			
			ClientMainForm cm=new ClientMainForm();

		}
}
