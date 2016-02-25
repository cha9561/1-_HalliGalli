package com.sist.client;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.table.*;


public class WaitRoom extends JPanel{ //
	Image back;
	ImageIcon roomlist,chattingroom,friends,ranking;

	JTable table1,table2;				//���̺�
	DefaultTableModel model1,model2;			
	TableColumn column;

	
	JTextArea ta;		//ä�� â
	JTextField tf;		//ä�� �Է� â
	JComboBox box;		//ä�� ENTERâ
	JButton b1,b2,b3,b8,b9;	//��ư
	JButton b4,b5,b6;//Ÿ��Ʋ�̹���  
	
	JTextArea profile;	//���������
	JScrollBar bar;
	
	@SuppressWarnings("deprecation")
	public WaitRoom(){
		
		back=Toolkit.getDefaultToolkit().getImage("img/monkey_back.jpg");	//�޹��
		roomlist=new ImageIcon("img/RoomList.png");			//Ÿ��Ʋ�̹���
		chattingroom=new ImageIcon("img/ChattingRoom.png");
		ranking=new ImageIcon("img/Ranking.png");
		friends=new ImageIcon("img/Friends.png");
		
		String[] col1={"������","���̸�","�ο�","�ִ��ο�","���ӻ���"};	//�����̺�
		String[][] row1=new String[0][5];	//col1���� ���� �°� 5�ٷ� ����
		model1=new DefaultTableModel(row1, col1)
		{//����Ŭ���� ��������
			public boolean isCellEditable(int r,int c)
			{
				return false;
			}
		};
		table1=new JTable(model1);
		table1.setShowVerticalLines(false); //����Ʈ ���μ� ����
		table1.getTableHeader().setReorderingAllowed(false);//�÷���ġ ����
		table1.getTableHeader().setResizingAllowed(false); //�÷������� ����
		JScrollPane js1=new JScrollPane(table1);
		
		//�ؽ�Ʈ ����/ ��:���, ���̸�:����, �ο�:���, ���ӻ���:���
		for(int i=0;i<col1.length;i++)
		{
			DefaultTableCellRenderer rnd
			= new DefaultTableCellRenderer();
			rnd.setHorizontalAlignment(JLabel.CENTER);
			column=table1.getColumnModel().getColumn(i);
			if(i==0)
			{
				column.setPreferredWidth(50);
				rnd.setHorizontalAlignment(JLabel.CENTER);
			}
			else if(i==1)
			{
				column.setPreferredWidth(250);
				rnd.setHorizontalAlignment(JLabel.LEFT);
			}
			else if(i==2)
			{
				column.setPreferredWidth(50);
				rnd.setHorizontalAlignment(JLabel.CENTER);
			}
			else if(i==3)
			{
				column.setPreferredWidth(50);
				rnd.setHorizontalAlignment(JLabel.CENTER);
			}
			else if(i==4)
			{
				column.setPreferredWidth(100);
				rnd.setHorizontalAlignment(JLabel.CENTER);
			}
			column.setCellRenderer(rnd);
		}
		
		String[] col2={"ID","����","�·�"};				//���������̺�
		String[][] row2=new String[0][3];
		model2=new DefaultTableModel(row2, col2)
		{//����Ŭ���� ��������
			public boolean isCellEditable(int r,int c)
			{
				return false;
			}
		};
		table2=new JTable(model2);
		table2.setShowVerticalLines(false); //����Ʈ ���μ� ����
		table2.getTableHeader().setReorderingAllowed(false);//�÷���ġ ����
		table2.getTableHeader().setResizingAllowed(false); //�÷������� ����
		JScrollPane js2=new JScrollPane(table2);
		for(int i=0;i<col2.length;i++)
		{
			DefaultTableCellRenderer rnd
			= new DefaultTableCellRenderer();
			rnd.setHorizontalAlignment(JLabel.CENTER);
			column=table2.getColumnModel().getColumn(i);
			if(i==0)
			{
				rnd.setHorizontalAlignment(JLabel.LEFT);
			}
			else if(i==1)
			{
				rnd.setHorizontalAlignment(JLabel.CENTER);
			}
			else if(i==2)
			{
				rnd.setHorizontalAlignment(JLabel.CENTER);
			}
			column.setCellRenderer(rnd);
		}

		ta=new JTextArea();						//ä��â
		JScrollPane js3=new JScrollPane(ta);	//textarea��ó�� ��ũ�ѹ�
		bar=js3.getVerticalScrollBar();
		
		tf=new JTextField();					//ä���Է�â
		b1=new JButton("����");					//ä�����۹�ư
		
		b2=new JButton("�游���");					//���򸻹�ư
		b3=new JButton("�����");				//�游����ư
		b8=new JButton("����");
		b9=new JButton("��������");
		b4=new JButton(roomlist);
		b5=new JButton(chattingroom);
		b6=new JButton(friends);
		
		profile=new JTextArea();				//���������
		
		
		JPanel p=new JPanel();					//���򸻰� �游����ư p�� ����
		p.setLayout(new GridLayout(2, 2, 5, 5));
		p.add(b2);  p.add(b3);  
		p.add(b8);  p.add(b9);
		p.setOpaque(false); 
		
		setLayout(null);					//�������� ����� ���� ���̾ƿ�����!
		
		b4.setBorderPainted(false);
		b4.setContentAreaFilled(false);
		b5.setBorderPainted(false);
		b5.setContentAreaFilled(false);
		b6.setBorderPainted(false);
		b6.setContentAreaFilled(false);
		
		b4.setBounds(10, 15, 500, 30);
	    b5.setBounds(10, 335, 500, 30);      //////ä��Ÿ��Ʋ
	    b6.setBounds(515, 15, 270, 30);
	    js1.setBounds(10, 50, 500, 280);   //����
	    js2.setBounds(515, 50, 270, 425);   //������   
	    js3.setBounds(10, 370, 500, 150);   //ä��///////
	    tf.setBounds(10, 525, 430, 30);      //ä���Է�â/////
	    b1.setBounds(445, 525, 65, 30);      //ä�����۹�ư/////
	    //profile.setBounds(515, 335, 270, 140);   //����� ����
	    p.setBounds(515, 480, 270, 75);      //��ư����
	      
	    add(js1);add(js2);add(js3);
	    add(tf);add(b1);add(profile);
	    add(p);add(b4);add(b5);add(b6);

		b4.setBounds(10, 15, 500, 30);		//roomlistŸ��Ʋ
		b5.setBounds(10, 335, 500, 30);	//chattingroomŸ��Ʋ
		b6.setBounds(515, 15, 270, 30);		//friendsŸ��Ʋ
		js1.setBounds(10, 50, 500, 280);	//����
		js2.setBounds(515, 50, 270, 280);	//������	
		//js3.setBounds(10, 370, 500, 150);	//ä��///////
		//tf.setBounds(10, 525, 450, 30);		//ä���Է�â/////
		//b1.setBounds(465, 525, 45, 30);		//ä�����۹�ư/////
		profile.setBounds(515, 335, 270, 140);	//����� ����
		p.setBounds(515, 480, 270, 75);		//��ư����
		
		JPanel p1=new JPanel();			//ä��â+ä���Է�â ����
		p1.setBounds(10, 370, 500, 200);
		p1.setLayout(null); 
		js3.setBounds(0, 0, 500, 150);
		tf.setBounds(0, 155, 430, 30);
		b1.setBounds(435, 155, 65, 30);
		p1.setOpaque(false);
		
		add(js1);add(js2);
		p1.add(js3);p1.add(tf);p1.add(b1);
		add(p1);
		add(profile);add(p);
		add(b5);add(b4);add(b6);;

	}
	
	protected void paintComponent(Graphics g) {			
		g.drawImage(back, 0, 0, getWidth(), getHeight(), this);	//this->JPanel�� ����̹��� �Ѹ�
	}


}
