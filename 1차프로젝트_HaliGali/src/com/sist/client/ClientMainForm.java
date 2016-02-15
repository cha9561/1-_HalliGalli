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
				connection(id, pass);
				//card.show(getContentPane(), "WR");			//Layout�� "WR"card�� ������� �������� �϶�
			}
			else if(e.getSource()==wr.tf)
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
			else if(e.getSource()==wr.b1)
			{
				
			}
			else if(e.getSource()==wr.b3){					//�游����ư->GAME Windowâ
				mr.setBounds(500, 300, 260,290);
		        mr.setVisible(true);
			}else if(e.getSource()==wr.b2){
				card.show(getContentPane(), "GW"); 		//���� â���� ��ȯ
			}
		}
		 // ������ ����
	    public void connection(String id,String pass)
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
							st.nextToken(),
							st.nextToken(),
							st.nextToken()
						  };
						  wr.model2.addRow(data);
					  }
					  break;
					  case Function.MYLOG:
					  {
						  System.out.println("MYLOG");
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
					}
				}catch(Exception ex){}
			}
		}
}
