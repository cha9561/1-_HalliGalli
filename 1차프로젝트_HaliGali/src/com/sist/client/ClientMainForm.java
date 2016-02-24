package com.sist.client;
import java.awt.*; 		//Layout�������
import javax.swing.*;	//window���� ��ư����� �������
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.sist.common.Function;
import java.awt.event.*;

//��Ʈ��ũ ����
import java.io.*;
import java.net.*;
import java.util.*;

public class ClientMainForm extends JFrame implements ActionListener, Runnable{
		CardLayout card=new CardLayout();			//â ��ȯ
		Loading ld=new Loading();					//LOADINGâ
		Login login=new Login();					//LOGINâ
		WaitRoom wr=new WaitRoom();					//WAITROOMâ
		GameWindow gw=new GameWindow();				//����WINDOWâ
		MakeID mID=new MakeID();					//ȸ������â
		MakeRoom mr=new MakeRoom();					//�游���â
		Help help=new Help();
		
		
		int rowNum=-1;
		String id;
	    Socket s;
	    BufferedReader in;// �������� ���� �д´�
	    OutputStream out; // ������ ��û���� ������
		
		public ClientMainForm(){
			
			setLayout(card);		//BorderLayout
			
			add("LOG",login);		//2.loginâ11
			add("WR",wr);			//3.WaitRoomâ
			add("GW",gw);			//4.GAME W11indowâ
			setSize(800,600);		//windowâ ũ�� ����1111
			setLocation(270, 170);	//windowâ ��ġ ����
			setVisible(true);		//�������� ��11
			setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			setResizable(false);    //windowâ ����(�þ�� ����)					
			
			login.bt1.addActionListener(this);	//ȸ������ ��ư ������
			login.bt2.addActionListener(this);	//�α��� ��ư ������
			wr.b1.addActionListener(this);		//�α��� ��ư ������

			wr.b2.addActionListener(this);		//�游��� ��ư ������
			wr.b3.addActionListener(this);      //����� ��ư ������
			wr.b8.addActionListener(this);		//���� ��ư ������
			wr.b9.addActionListener(this);      //�������� ��ư ������

			mr.b1.addActionListener(this);      //�游���â���� Ȯ�ι�ư ������
			gw.b1.addActionListener(this); 		//����â���� ���۹�ư ������
			gw.b4.addActionListener(this); 		//����â���� �غ��ư ������
			gw.b5.addActionListener(this); 		//����â���� ���۹�ư ������
			gw.b6.addActionListener(this); 		//����â���� ������ ������
			gw.tf.addActionListener(this);		//����â���� ä���ϸ�
			gw.cardOpen.addActionListener(this);
			gw.bell.addActionListener(this);
			
			mID.b1.addActionListener(this);
			mID.b2.addActionListener(this);
			mID.b3.addActionListener(this);

			wr.table1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {			
				@Override
				public void valueChanged(ListSelectionEvent e) {
					if(wr.table1.getSelectedRow()>-1)				//���ȣ�� 0 �̻��̸�
					{
						rowNum=wr.table1.getSelectedRow();
						System.out.println(rowNum);
					}
				}
			});

		

		}
		
		public void actionPerformed(ActionEvent e) {
			if(e.getSource()==login.bt1)					//1.�˾�â���� ȸ������â ����
			{				    
				mID.setBounds(470, 310,340,420);
				mID.setVisible(true);				
			}
			else if(e.getSource()==login.bt2)				//2.�α��ι�ư ������
			{
				id=login.tf.getText().trim();		//ID�Է¾�������
				if(id.length()<1)
				{
					JOptionPane.showMessageDialog(this,
							"ID�� �Է��ϼ���");
					login.tf.requestFocus();
					return;
				}
				String pass=new String(login.pf.getPassword());	//PWD�Է¾�������
				if(pass.length()<1)
				{
					JOptionPane.showMessageDialog(this,
							"Password�� �Է��ϼ���");
					login.pf.requestFocus();
					return;
				}
				
				connection();						//��� ��Ȯ�� ó��������, connection()�޼ҵ�� �̵�!!
				
				try{
					out.write((Function.LOGIN+"|"+id+"|"					//�α��ι�ư ������ server���� ��û
							+pass+"\n").getBytes());
				}catch(Exception ex){}
			}
			else if(e.getSource()==wr.tf || e.getSource()==wr.b1)			//3.waitroom���� ä���Է��� ��
			{			
				String data=wr.tf.getText();								//�Է��� �� ��������
				if(data.length()<1)
					return;				
				try
				{
					out.write((Function.WAITCHAT1+"|"+data+"\n").getBytes());	//ä�������� server���� ��û
				}catch(Exception ex){}
				wr.tf.setText("");
			}
			else if(e.getSource()==gw.tf || e.getSource()==gw.b1)			//4.gameWindow���� ä���Է��� ��
			{	
				String data=gw.tf.getText();								//�Է��� �� ��������
				if(data.length()<1)
					return;				
				try{
					out.write((Function.ROOMCHAT+"|"+data+"\n").getBytes());	//ä�������� server���� 
				}catch(Exception ex){}
				gw.tf.setText("");
			}

			else if(e.getSource()==wr.b2) 						//5.�游���â 
			{				
				mr.tf.setText("");	//�游��� �ʱ�ȭ
		        mr.pf.setText("");
		        mr.rb1.setSelected(true);
				mr.setBounds(500, 300, 260,290);
		        mr.setVisible(true);
			}
			else if(e.getSource()==wr.b3) 						//6.����� ��ưó�� /////////////////////////////////
			{
				if(rowNum>=0)
				{
					try {
						gw.tf.setText("");
						out.write((Function.JOINROOM+"|"+rowNum+"\n").getBytes());
					} catch (Exception e2) {			
					}
				}				
			}

			else if(e.getSource()==mr.b1)  						//6.�游���â���� Ȯ�� ��������//////////////////////////////
			{
				String subject=mr.tf.getText().trim();			//���̸� �Է� ��������
		        if(subject.length()<1)
		        {
		        	JOptionPane.showMessageDialog(this,
							"���̸��� �Է��ϼ���");
		        	mr.tf.requestFocus();
		        	return;
		        }

		        if(mr.rb2.isSelected()){						//����� ��ư ������ ��
		        	String pw=new String(mr.pf.getPassword());		
			        if(pw.length()<1)
			        {
			        	JOptionPane.showMessageDialog(this,
								"��й�ȣ�� �Է��ϼ���");
			        	mr.pf.requestFocus();
			        	return;
			        }

		        }	
		        
		        mr.dispose();
		        
		        try{
		        	String roomType="";					//1.����or����� ����
		        	if(mr.rb1.isSelected()){       		
		        		roomType=mr.rb1.getText(); } 	//����
		        	else if(mr.rb2.isSelected()){		
		        		roomType=mr.rb2.getText(); } 	//�����
					String roomName=mr.tf.getText();	//2.���̸�
					String capaNum=mr.box.getSelectedItem().toString();	//3.�ִ��ο���
					out.write((Function.MAKEROOM+"|"+roomType+"|"+roomName+"|"+capaNum+"\n").getBytes()); 
					//��������,���̸�,�ִ��ο� �Ѱ���

				}catch(Exception ex){}

			}
			else if(e.getSource()==wr.b8) 					//���� ��ưó��
			{		
				help.setVisible(true);	
				repaint();
			}else if(e.getSource()==wr.b9) 					//�������� ��ưó��
			{
				/*������ ���� �޽��� ������ ���α׷� ����*/
				try 
				{
					out.write((Function.CLIENTEXIT+"|\n").getBytes());
					
				} catch (Exception e2) {}
				
				try {
					s.close();					//��������
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				System.exit(0);				
			}
			else if(e.getSource()==mID.b1)					//���ԿϷ��ư
			{
				String name=mID.tf1.getText().trim();
				String id=mID.tf2.getText().trim();
				String pass1=new String(mID.pf1.getPassword());
				String pass2=new String(mID.pf2.getPassword());
				if(name.length()<1)
				{
					JOptionPane.showMessageDialog(this,
							"�̸��� �Է��ϼ���");
					mID.tf1.requestFocus();
					return;
				}
				else if(id.length()<1)
				{
					JOptionPane.showMessageDialog(this,
							"ID�� �Է��ϼ���");
					mID.tf2.requestFocus();
					return;
				}
				else if(mID.ck==false)
				{
					JOptionPane.showMessageDialog(this,
							"ID �ߺ�üũ �Ͻÿ�");
					mID.tf2.requestFocus();
					return;
				}
				else if(pass1.length()<1)
				{
					JOptionPane.showMessageDialog(this,
							"��й�ȣ�� �Է��ϼ���");
					mID.pf1.requestFocus();
					return;
				}
				else if(pass2.length()<1)
				{
					JOptionPane.showMessageDialog(this,
							"��й�ȣ Ȯ���� �Է��ϼ���");
					mID.pf2.requestFocus();
					return;
				}
				else if(!(pass1.equals(pass2)))
				{
					JOptionPane.showMessageDialog(this,
							"��й�ȣ�� �������� �ʽ��ϴ�");
					mID.pf1.requestFocus();
					return;
				}
				try {
					out.write((Function.SUCCESSJOIN+"|"+name+"|"+id+"|"+pass1+"\n").getBytes());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				JOptionPane.showMessageDialog(this, "ȸ�����ԿϷ�");
				mID.dispose();
			}
			else if(e.getSource()==mID.b2)
			{
				mID.tf1.setText("");
				mID.tf2.setText("");
				mID.pf1.setText("");
				mID.pf2.setText("");
				mID.dispose();
			}
			else if(e.getSource()==mID.b3)				//ID�ߺ�üũ
			{
				String id=mID.tf2.getText().trim();
				if(id.length()<1)
				{
					JOptionPane.showMessageDialog(this,
							"ID�� �Է��ϼ���");
					mID.tf2.requestFocus();
					return;
				}
				
				if(mID.num==0)						//�ѹ��� ������ �������� �ʾҴٸ�
				{
					System.out.println("����õ�");	
					connection();
					mID.num++;
				}
				
				System.out.println("ID�ߺ�üũ");
				try
				{
					System.out.println(id);
					out.write((Function.IDCHECK+"|"+id+"\n").getBytes());		//ID�ߺ�üũ�� server���� ��û
				}catch(Exception ex){}
			}
			else if(e.getSource()==gw.b4){								//GameWindow���� �غ��ư ������ ��
				try {
					out.write((Function.ROOMREADY+"|"+"\n").getBytes());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			
			else if(e.getSource()==gw.b5){								//GameWindow���� ���۹�ư ������ ��
				try {
					out.write((Function.ROOMSTART+"|"+"\n").getBytes());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}

			else if(e.getSource()==gw.b6)			//GameWindow���� ������ ������ ��
			{
				System.out.println("�泪���� ��ư Click");
				gw.ta.setText("");
				gw.b4.setEnabled(true);
				try{
					out.write((Function.EXITROOM+"|"+"\n").getBytes());
				}catch(Exception ex){}
			}
			else if(e.getSource()==gw.cardOpen)					//ī������� ������ ��!!!
			{
				gw.cardOpen.setBorderPainted(false);      
				gw.cardOpen.setContentAreaFilled(false);
				gw.cardOpen.setEnabled(false);
				try {
					out.write((Function.CARDOPEN+"|"+id+"\n").getBytes());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			else if(e.getSource()==gw.bell) //��ġ�� ��ư
			{
				try {
					out.write((Function.BELL+"|"+id+"\n").getBytes());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			
		}

		//������ ����
		public void connection()
	    {
	    	try
	    	{
	    		s=new Socket("localhost", 65535);		// s=>server
	    		in=new BufferedReader(new InputStreamReader(s.getInputStream()));		//������ ���� �о����
				out=s.getOutputStream();												//������ ���� ����
				/*out.write((Function.LOGIN+"|"+id+"|"
						+pass+"\n").getBytes());*/
	    	}catch(Exception ex){}
	    	
	    	new Thread(this).start();	// run()���� �̵�  // �����κ��� ���䰪�� �޾Ƽ� ó��
	    }
		
		public static void main(String[] args) {
			 
			try{
				//UIManager.setLookAndFeel("com.jtattoo.plaf.noire.NoireLookAndFeel");	//�������� �ܾ�°�
			}catch(Exception ex){}
			
			ClientMainForm cm=new ClientMainForm();

		}

		@Override
		public void run() {			//client�� server���� ��� 

			while(true){
				try{
					String msg=in.readLine();
					System.out.println("Server=>"+msg);
					StringTokenizer st=new StringTokenizer(msg, "|");
					int protocol=Integer.parseInt(st.nextToken());
					switch(protocol)
					{
					case Function.YOURTURN:				//0.�ڱ������� �� ī������� ��ưȰ��ȭ
					{
						gw.cardOpen.setBorderPainted(false);     	
						gw.cardOpen.setContentAreaFilled(false);
						gw.cardOpen.setEnabled(true);
					}
					break;
					case Function.DELROW: 		//1.���������� client ���� ������ List ���� ����
					{
						int rowIndex=(Integer.parseInt(st.nextToken()));		//rowIndex=delIndex
						System.out.println("���� ��: "+rowIndex);
						wr.model2.removeRow(rowIndex);							//�����ڸ���Ʈ���� ����
					}
					break;
					case Function.CLIENTEXIT:	//2.waitRoom ä�ù濡 00���� �����̽��ϴ� ����
					{
						wr.ta.append(st.nextToken()+"\n");
						wr.bar.setValue(wr.bar.getMaximum());
					}
					 break;
					  case Function.MYLOG:				//1.windowŸ��Ʋ�� ������̸� ������Ʈ
					  {
						  String id=st.nextToken();
						  setTitle(id);
						  card.show(getContentPane(), "WR");	//waitingroom���� â ��ȯ
						  
					  }
					  break;
					  
					  case Function.LOGIN:				//2.���������̺� ����� ������Ʈ
					  {
						  String[] data={
							st.nextToken(),	 
							st.nextToken()
						  };
						  wr.model2.addRow(data);	
					  }
					  break;
					  
					  case Function.WAITCHAT1:			//3.ä���� ��(waitroom)
					  {
						  wr.ta.append(st.nextToken()+"\n");
						  wr.bar.setValue(wr.bar.getMaximum());
					  }
					  break;
					  
					  case Function.ROOMCHAT:			//3.ä���� ��(gameWindow)
					  {
						  gw.ta.append(st.nextToken()+"\n");
						  gw.bar.setValue(gw.bar.getMaximum());
					  }
					  break;
					  
					  case Function.NOTOVERLAP:			//4.ID�� �ߺ����� ���� ��
					  {
						  JOptionPane.showMessageDialog(this,"ID�� �ߺ����� �ʽ��ϴ�");
						  mID.ck=true;
						  mID.pf1.requestFocus();
					  }
					  break;
					  
					  case Function.OVERLAP:			//4.ID�� �ߺ��� ��
					  {
						  JOptionPane.showMessageDialog(this,
									"ID�� �ߺ��˴ϴ�. �ٽ� �Է��ϼ���.");
						  mID.ck=false;
						  mID.pf1.requestFocus();
					  }
					  break;
					  
					  case Function.MAKEROOM:			//5.client�� �游��� Ȯ�� ��ư�� ������ ��(����â ��ȯ)
					  {	
						  String roomId=st.nextToken();		//���ӷ� ���� ��� 	id
						  String roomName=st.nextToken();	//���� ���� ���ӷ��� 	�̸�
						  String humanNum=st.nextToken();		//�����ο���	//���� �Ⱦ���
						  String capaNum=st.nextToken();		//�ִ��ο���	//���� �Ⱦ���
						  setTitle("����_"+roomId+"    "+"����_"+roomName);	
						  gw.b5.setEnabled(false); 	//���۹�ư ��Ȱ��ȭ
						  card.show(getContentPane(), "GW"); 		//����â���� ��ȯ
					  }
					  break;

					  case Function.ROOMINFORM:			//5.client�� �游��� Ȯ�� ��ư�� ������ ��(waitRoom�� ����Ʈ�� �� �߰�)
					  {	
						  String roomType=st.nextToken();	//���������
						  String roomName=st.nextToken();	//���ӷ��� �̸�
						  String nnum=st.nextToken();		//�����ο�
						  String num=st.nextToken();		//�ִ��ο�
						  String pos=st.nextToken();		//�����(���Ӵ����)
						  String[] data={roomType, roomName, nnum, num, pos};	
						  wr.model1.addRow(data);			//waitRoom�� ����Ʈ�� �� �߰�						  
						  wr.repaint();
					  }
					  break;
					  
					  
					  case Function.JOINROOM:			//6.�濡 ���� ���� ��(�ο� �������� ���� ���� ����)
					  {
						  String result=st.nextToken();
						  if(result.equals("TRUE"))
						  {
							  String roomMaker=st.nextToken();
							  String roomName=st.nextToken();
							  setTitle("����_"+roomMaker+"    "+"����_"+roomName);	
							  gw.b5.setEnabled(false); 	//���۹�ư ��Ȱ��ȭ
							  gw.tf.setText("");
							  card.show(getContentPane(), "GW");
							  //�غ��ư Ȱ��ȭ
							  gw.b4.setEnabled(true); 
						  }
						  else
						  {
							  JOptionPane.showMessageDialog(this,"���� ��á���ϴ�.");
						  }
					  }
					  break;
					  
					  case Function.ROOMREADY:			//6.�غ��ư ������ �� ��ư ��Ȱ��ȭ
					  {
						  System.out.println("���������� �غ����޹���");
						  gw.b4.setEnabled(false); 							//�غ��ư��Ȱ��ȭ
					  }
					  break;
					  
					  case Function.ROOMREADYBUTTON:			//7.����غ����� �� ���常 ���� Ȱ��ȭ
					  {
						  System.out.println("������ �������� ���۹�ư Ȱ��ȭ");
						  gw.b5.setEnabled(true); 							//�غ��ư��Ȱ��ȭ
						  
					  }
					  break;
//					  case Function.GAMESTART:			//7.����غ����� �� ���常 ���� Ȱ��ȭ
//					  {
//						  System.out.println("������ �������� ���۹�ư Ȱ��ȭ");
//						  gw.cardOpen.setBorderPainted(false);      
//							gw.cardOpen.setContentAreaFilled(false);
//							gw.cardOpen.setEnabled(false);
//						  
//					  }
//					  break;
					  /*[���ο����� ] ->*/
					  case Function.CHGROOMUSER:
					  {
						  //���� �� List table �� Ư�� Row �� ���ο��� �����
						  int row=Integer.parseInt(st.nextToken());
						  String userNum=st.nextToken();
						  wr.model1.setValueAt(userNum, row, 2);
						  wr.repaint();
					  }
					  break;

					  /*[�������º���] ->*/
					  case Function.CHGUSERPOS:
					  {
						  int row=Integer.parseInt(st.nextToken());	//���ȣ
						  System.out.println("\\\\\\--->"+row);
						  String pos=st.nextToken();		//�����ο���
						  wr.model2.setValueAt(pos, row, 1);
						  wr.repaint();
					  }
					  break;
					  
					  /*[����º��� ] ->*/
					  case Function.CHGROOMSTATE:
					  {
						  //���� �� List table �� Ư�� Row �� ���ο��� �����
						  int row=Integer.parseInt(st.nextToken());		//���ȣ
						  String roomState=st.nextToken();				//�����
						  wr.model1.setValueAt(roomState, row, 4);
						  wr.repaint();
					  }
					  break;
					  
					  /*[�泪����] ->*/
					  case Function.DELROOM: //�濡 ����ڰ� ���� ����� �޽��� ����
					  {
						  gw.tf.setText("");
						  int roomRow=Integer.parseInt(st.nextToken());
						  System.out.println(roomRow+"�� ����");
						  wr.model1.removeRow(roomRow);
						  wr.repaint();
					  }
					  break;
					  case Function.REPAINT:
					  {
						  String tmpName=st.nextToken();
						  int b=Integer.parseInt(st.nextToken());
						  gw.UpdateDraw(tmpName, b);						  
					  }
					  break;
					  case Function.CARDNUM:
					  {
						  String tmpName=st.nextToken();			//id
						  int b=Integer.parseInt(st.nextToken());	//ī���
						  gw.UpdateCardNum(tmpName, b);
					  }
					  break;
					  case Function.DEAD:
					  {
						  gw.ta.append("����� �׾����ϴ�");
						  gw.bell.setEnabled(false);
						  gw.cardOpen.setEnabled(false);
					  }
					  break;
					  case Function.UPDATEDEAD:
					  {
						  String tmpName=st.nextToken();
						  gw.ta.append(tmpName+" ���� �׾����ϴ�");
						  gw.UpdateDead(tmpName);
					  }
					  break;
					  case Function.BELLSUCCESS:
					  {
						  String tmpName=st.nextToken();
						  gw.ta.append(tmpName+" ���� ��ġ�� �����߽��ϴ�.");
						  gw.bell.setEnabled(true);
						  gw.CardInit();
					  }
					  break;
					  case Function.BELLFAIL:
					  {
						  String tmpName=st.nextToken();
						  gw.ta.append(tmpName+" ���� ��ġ�� �����߽��ϴ�.");
						  gw.bell.setEnabled(true);
					  }
					  break;
					  case Function.BELL:
					  {
						  gw.bell.setEnabled(false);
					  }
					  break;
					  case Function.TURNINFO:
					  {
						  gw.userName[0]=st.nextToken();
						  gw.userName[1]=st.nextToken();
						  gw.userName[2]=st.nextToken();
						  gw.userName[3]=st.nextToken();
					  }
					  break;
					  case Function.EXITFALSE:			//���ӽ��۽� �������Ȱ��ȭ
					  {
						  gw.b6.setEnabled(false);
					  }
					  break;
					  
					  case Function.IDLABEL:			//���ӽ��۽� id�� �Է�
					  {
						  String ID=st.nextToken();	//id
						  for(int i=0; i<4; i++){
							  if(ID.equals(gw.userName[i])){
								  gw.laPlayer[i].setText("Player"+(i+1)+": "+ID);
							  }
						  }
					  }
					  break;
					}
				}catch(Exception ex){}
			}
		}
}
