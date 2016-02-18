package com.sist.client;
import java.awt.*; 		//Layout�������
import javax.swing.*;	//window���� ��ư����� �������

import com.sist.common.Function;

import java.awt.event.*;

//��Ʈ��ũ ����
import java.io.*;
import java.net.*;
import java.util.*;

public class ClientMainForm extends JFrame implements 
ActionListener, Runnable{
		CardLayout card=new CardLayout();			//â ��ȯ
		Loading ld=new Loading();					//LOADINGâ
		Login login=new Login();					//LOGINâ
		WaitRoom wr=new WaitRoom();					//WAITROOMâ
		GameWindow gw=new GameWindow();				//����WINDOWâ
		MakeID mID=new MakeID();					//ȸ������â
		MakeRoom mr=new MakeRoom();					//�游���â
		Help help=new Help();
		
		// id|��ȭ��|����
	    Socket s;
	    BufferedReader in;// �������� ���� �д´�
	    OutputStream out; // ������ ��û���� ������
		
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
			wr.b2.addActionListener(this);		//�游��� ��ư ������
			wr.b3.addActionListener(this);      //����� ��ư ������
			wr.b8.addActionListener(this);		//���� ��ư ������
			wr.b9.addActionListener(this);      //�������� ��ư ������
			mr.b1.addActionListener(this);      //�游���â���� Ȯ�ι�ư ������
			wr.tf.addActionListener(this);		//����� �Է°� ������ 
			wr.b3.addActionListener(this); 		//�游����ư ������
			
			add("GW",gw);						//4.GAME Windowâ
			gw.b1.addActionListener(this); 		//����â���� ���۹�ư ������
			gw.tf.addActionListener(this);
			mID.b1.addActionListener(this);
			mID.b2.addActionListener(this);
			mID.b3.addActionListener(this);
			
			
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource()==login.bt1)
			{				    
				//��ư(bt1)�� �׼��� ��������
				mID.setBounds(470, 310,340,420);
				mID.setVisible(true);				//�˾�â���� ȸ������â ����
			}
			else if(e.getSource()==login.bt2)
			{
				String id=login.tf.getText().trim();
				if(id.length()<1)
				{
					JOptionPane.showMessageDialog(this,
							"ID�� �Է��ϼ���");
					login.tf.requestFocus();
					return;
				}
				String pass=new String(login.pf.getPassword());
				if(pass.length()<1)
				{
					JOptionPane.showMessageDialog(this,
							"Password�� �Է��ϼ���");
					login.pf.requestFocus();
					return;
				}
				
				connection();		//connection()�޼ҵ�� �̵�!!
				
				try{
					out.write((Function.LOGIN+"|"+id+"|"					//�α��ι�ư ������ server���� ��û
							+pass+"\n").getBytes());
				}catch(Exception ex){}
			}
			else if(e.getSource()==wr.tf || e.getSource()==wr.b1)			//1.waitroom���� ä���Է��� ��
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
			else if(e.getSource()==gw.tf || e.getSource()==gw.b1)			//2.gameWindow���� ä���Է��� ��
			{	
				String data=gw.tf.getText();								//�Է��� �� ��������
				if(data.length()<1)
					return;				
				try{
					out.write((Function.WAITCHAT2+"|"+data+"\n").getBytes());	//ä�������� server���� 
				}catch(Exception ex){}
				gw.tf.setText("");
			}
			else if(e.getSource()==wr.b2) //�游����ư->GAME Windowâ
			{		
				mr.setBounds(500, 300, 260,290);
		        mr.setVisible(true);
			}
			else if(e.getSource()==wr.b3) //����� ��ưó��
			{
				
			}
			else if(e.getSource()==mr.b1)  //�游���â���� Ȯ�� �������� ó��
			{
				String subject=mr.tf.getText().trim();
		        if(subject.length()<1)
		        {
		        	JOptionPane.showMessageDialog(this,
							"���̸��� �Է��ϼ���");
		        	mr.tf.requestFocus();
		        	return;
		        }
		        String pw=new String(mr.pf.getPassword());
		        if(mr.rb2.isSelected())
		        {
		        	if(pw.length()<1)
		   	        {
		   	        	JOptionPane.showMessageDialog(this,
		   						"��й�ȣ�� �Է��ϼ���");
		   	        	mr.pf.requestFocus();
		   	        	return;
		   	        }
		        }
		        int a=0;
		        mr.dispose();
		        card.show(getContentPane(), "GW");
			}
			else if(e.getSource()==wr.b8) //���� ��ưó��
			{		
				help.setVisible(true);	
				repaint();
			}else if(e.getSource()==wr.b9) //�������� ��ưó��
			{
				
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
				System.out.println("���");
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
				System.out.println(mID.num);
				
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
			
		}
		 // ������ ����
/*	    public void connection(String id,String pass)
	    {
	    	try
	    	{
	    		s=new Socket("localhost", 65535);
	    		// s=>server
	    		in=new BufferedReader(
						new InputStreamReader(s.getInputStream()));
				out=s.getOutputStream();
				out.write((Function.LOGIN+"|"+id+"|"
						+pass+"\n").getBytes());
	    	}catch(Exception ex){}
	    	
	    	// �����κ��� ���䰪�� �޾Ƽ� ó��
	    	new Thread(this).start();// run()
	    }*/
		public void connection()
	    {
	    	try
	    	{
	    		s=new Socket("localhost", 65535);
	    		// s=>server
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
						  wr.model3.addRow(data);		
					  }
					  break;
					  
					  case Function.WAITCHAT1:			//3.ä���� ��(waitroom)
					  {
						  wr.ta.append(st.nextToken()+"\n");
						  wr.bar.setValue(wr.bar.getMaximum());
					  }
					  break;
					  
					  case Function.WAITCHAT2:			//3.ä���� ��(gameWindow)
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
					}
				}catch(Exception ex){}
			}
		}
}
