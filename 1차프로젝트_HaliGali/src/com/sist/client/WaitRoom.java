package com.sist.client;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.table.*;


public class WaitRoom extends JPanel{ //
	Image back;
	JTable table1,table2,table3;				//���̺�
	DefaultTableModel model1,model2,model3;		
	
	JTextArea ta;		//ä�� â
	JTextField tf;		//ä�� �Է� â
	JComboBox box;		//ä�� ENTERâ
	JButton b1,b2,b3;	//��ư
	JTextArea profile;	//���������
	JScrollBar bar;
	
	public WaitRoom(){
		back=Toolkit.getDefaultToolkit().getImage("C:\\image\\monkey_back.jpg");	//�޹��
		
		String[] col1={"���̸�","�ο�","���ӻ���"};	//�����̺�
		String[][] row1=new String[5][3];	//col1���� ���� �°� 5�ٷ� ����
		model1=new DefaultTableModel(row1, col1);
		table1=new JTable(model1);
		JScrollPane js1=new JScrollPane(table1);
		
		String[] col2={"����","ID","�·�"};		//����ǥ���̺�
		String[][] row2=new String[0][3];
		model2=new DefaultTableModel(row2, col2);
		table2=new JTable(model2);
		JScrollPane js2=new JScrollPane(table2);
		
		String[] col3={"ID","�·�"};				//���������̺�
		String[][] row3=new String[0][3];
		model3=new DefaultTableModel(row3, col3);
		table3=new JTable(model3);
		JScrollPane js3=new JScrollPane(table3);
		
		
		ta=new JTextArea();						//ä��â
		JScrollPane js4=new JScrollPane(ta);	//textarea��ó�� ��ũ�ѹ�
		bar=js3.getVerticalScrollBar();
		tf=new JTextField();					//ä���Է�â
		b1=new JButton("����");					//ä�����۹�ư
		
		b2=new JButton("����");					//���򸻹�ư
		b3=new JButton("�游���");				//�游����ư

		profile=new JTextArea();				//���������
		
		
		JPanel p=new JPanel();					//���򸻰� �游����ư p�� ����
		p.setLayout(new GridLayout(1, 2, 5, 5));
		p.add(b2);  p.add(b3);  
		p.setOpaque(false); 
		
		setLayout(null);					//�������� ����� ���� ���̾ƿ�����!
		js1.setBounds(10, 15, 500, 320);	//����	
		js2.setBounds(515, 10, 270, 160);	//����ǥ
		js3.setBounds(515, 175, 270, 160);	//������	
		js4.setBounds(10, 340, 500, 190);	//ä��
		tf.setBounds(10, 535, 410, 30);		//ä���Է�â
		b1.setBounds(425, 535, 85, 30);		//ä�����۹�ư
		profile.setBounds(515, 340, 270, 180);	//����� ����
		p.setBounds(515, 525, 270, 40);		//��ư����
		
		
		add(js1);add(js2);add(js3);
		add(js4);add(tf);add(b1);add(profile);
		add(p);

		
	}
	
	protected void paintComponent(Graphics g) {			
		g.drawImage(back, 0, 0, getWidth(), getHeight(), this);	//this->JPanel�� ����̹��� �Ѹ�
	}


}
