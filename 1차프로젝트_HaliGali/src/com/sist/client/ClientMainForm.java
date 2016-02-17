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
			wr.b2.addActionListener(this);		//���� ��ư ������
			wr.b3.addActionListener(this);      //�游��� ��ư ������
			wr.tf.addActionListener(this);		//����� �Է°� ������ 
			
			add("GW",gw);						//4.GAME Windowâ
			wr.b3.addActionListener(this); 		//�游����ư ������
			
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
				mID.setVisible(true);											//�˾�â���� ȸ������â ����
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
				connection();
				try
				{
					out.write((Function.LOGIN+"|"+id+"|"
							+pass+"\n").getBytes());
				}catch(Exception ex){}
				//card.show(getContentPane(), "WR");			//Layout�� "WR"card�� ������� �������� �϶�
			}
			else if(e.getSource()==wr.tf || e.getSource()==wr.b1)
			{			//ä��â(tf)�� �׼��� ��������
				String data=wr.tf.getText();								//�Է��� �� ��������
				//wr.ta.append(data+"\n");
				if(data.length()<1)
					return;				
				try
				{
					out.write((Function.WAITCHAT+"|"+data+"\n").getBytes());
				}catch(Exception ex){}
				wr.tf.setText("");
			}
			else if(e.getSource()==wr.b3){					//�游����ư->GAME Windowâ
				mr.setBounds(500, 300, 260,290);
		        mr.setVisible(true);
			}else if(e.getSource()==wr.b2){
				card.show(getContentPane(), "GW"); 		//���� â���� ��ȯ
			}
			else if(e.getSource()==mID.b1)
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
				//dispose();	//�޸� �״�� �� ä�� â�ݱ�
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
			else if(e.getSource()==mID.b3)
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
				if(mID.num==0)
				{
					System.out.println("����õ�");
					connection();
					mID.num++;
				}
				try
				{
					System.out.println(id);
					//out.write((Function.IDCHECK+"|"+id+"\n").getBytes());
					out.write((Function.IDCHECK+"|"+id+"\n").getBytes());
				}catch(Exception ex){}
				System.out.println("ID�ߺ�üũ");
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
	    		in=new BufferedReader(
						new InputStreamReader(s.getInputStream()));
				out=s.getOutputStream();
				/*out.write((Function.LOGIN+"|"+id+"|"
						+pass+"\n").getBytes());*/
	    	}catch(Exception ex){}
	    	
	    	// �����κ��� ���䰪�� �޾Ƽ� ó��
	    	new Thread(this).start();// run()
	    }
		
		public static void main(String[] args) {
			 
			try{
				//UIManager.setLookAndFeel("com.jtattoo.plaf.noire.NoireLookAndFeel");	//�������� �ܾ�°�
			}catch(Exception ex){}
			
			ClientMainForm cm=new ClientMainForm();

		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true)
			{
				try
				{
					String msg=in.readLine();
					System.out.println("Server=>"+msg);
					StringTokenizer st=
						  new StringTokenizer(msg, "|");
					int protocol=Integer.parseInt(st.nextToken());
					switch(protocol)
					{
					  case Function.LOGIN:
					  {
						  String[] data={
							st.nextToken(),	 
							st.nextToken()
						  };
						  wr.model3.addRow(data);
					  }
					  break;
					  case Function.MYLOG:
					  {
						  String id=st.nextToken();
						  setTitle(id);
						  card.show(getContentPane(), "WR");
					  }
					  break;
					  case Function.WAITCHAT:
					  {
						  wr.ta.append(st.nextToken()+"\n");
						  wr.bar.setValue(wr.bar.getMaximum());
					  }
					  break;
					  case Function.NOTOVERLAP:
					  {
						  JOptionPane.showMessageDialog(this,
									"ID�� �ߺ����� �ʽ��ϴ�");
						  mID.ck=true;
						  mID.pf1.requestFocus();
					  }
					  break;
					  case Function.OVERLAP:
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
