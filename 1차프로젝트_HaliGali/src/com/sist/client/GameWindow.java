package com.sist.client;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.*;
import javax.swing.table.*;


public class GameWindow extends JPanel {   //ūƲ
   Image back;      
   JTextArea ta;
   JTextField tf;
   JButton b1,b4,b5,b6;
   JScrollBar bar;
   JButton bell,cardOpen;      //bell,ī�������

   ImageIcon iiBell,hand,send,readyr,startr,exitr;      //��,ī������� ��ư�̹���   

   JTable table;
   TableColumn column;
   DefaultTableModel model1;

   
  PrintWriter writer;
   ImageIcon iiCard[];        // ��ī��56�� 
   ImageIcon iiCardBack;     // ī��޸�   
   ImageIcon iiPlayerCard[];  // ���÷��̾��� ī�� ���� �������
   JLabel laPlayer[];        // ����� ��
   JLabel laCardNum[];        // ī�� �̸� 
   String userName[];        // ����� �̸�      �̸�->id�� �����ؾ��ҵ�   
   DefaultListModel model = new DefaultListModel(); // ����Ʈ��(����Ʈ ���� DB)
   JList li_Player = new JList(model);          // ����� ���� ����Ʈ
   
   public GameWindow(){
      back=Toolkit.getDefaultToolkit().getImage("img/test.png");  // �޹��      
      iiCardBack = new ImageIcon("cardimg/CardBack.jpg");    // ī�� �޸�
      iiBell = new ImageIcon("img/Bell.png");             // ��
      hand=new ImageIcon("img/hand.png");                    // �ո�� 
      send=new ImageIcon("img/send2.png");		
      readyr=new ImageIcon("img/readyr.png"); 
      startr=new ImageIcon("img/startr.png"); 
      exitr=new ImageIcon("img/exitr.png"); 
      iiPlayerCard = new ImageIcon[4]; //  ����� �����������? ī��޸�
      iiCard = new ImageIcon[56];     // ��ī�� �̹���
      laPlayer = new JLabel[4];   //����� ��
      laCardNum = new JLabel[4];  // ī�� ��
      userName = new String[4];  // ������̸�
      
      ta=new JTextArea();   
      JScrollPane js4=new JScrollPane(ta);   //��ũ�ѵǴ� ä��â
      bar=js4.getVerticalScrollBar();
         
      tf=new JTextField();               //ä���Է�â
      b1=new JButton(send);               //ä�����۹�ư      
      b4=new JButton(readyr);               
      b5=new JButton(startr);               
      b6=new JButton(exitr);   
      laPlayer[0]=new JLabel("");		//�ʱ����� �Ⱥ��̰�
      laPlayer[1]=new JLabel("");
      laPlayer[2]=new JLabel("");
      laPlayer[3]=new JLabel("");
      laCardNum[0]=new JLabel("");
      laCardNum[1]=new JLabel("");
      laCardNum[2]=new JLabel("");
      laCardNum[3]=new JLabel("");
      bell=new JButton(iiBell);            //����ư
      cardOpen=new JButton(hand);            //ī��������ư
      String[] col={"ID"};				//���������̺�
		String[][] row=new String[0][1];
		model1=new DefaultTableModel(row, col)
		{//����Ŭ���� ��������
			public boolean isCellEditable(int r,int c)
			{
				return false;
			}
		};
		table=new JTable(model1);
		table.getTableHeader().setResizingAllowed(false); //�÷������� ����
		JScrollPane js=new JScrollPane(table);
		for(int i=0;i<col.length;i++)
		{
			DefaultTableCellRenderer rnd
			= new DefaultTableCellRenderer();
			rnd.setHorizontalAlignment(JLabel.CENTER);
			column=table.getColumnModel().getColumn(0);
			rnd.setHorizontalAlignment(JLabel.LEFT);
			column.setCellRenderer(rnd);
		}
   
      	b1.setBorderPainted(false);
		b1.setContentAreaFilled(false);
      	b4.setBorderPainted(false);
		b4.setContentAreaFilled(false);
		b5.setBorderPainted(false);
		b5.setContentAreaFilled(false);
		b6.setBorderPainted(false);
		b6.setContentAreaFilled(false);
		
      setLayout(null);      

      laPlayer[0].setBounds(20, 20, 100, 30);   
      laPlayer[1].setBounds(675, 20, 100, 30);
      laPlayer[2].setBounds(20, 330, 100, 30);
      laPlayer[3].setBounds(675, 330, 100, 30); 
      laCardNum[0].setBounds(20, 50, 100, 30);
      laCardNum[1].setBounds(675, 50, 100, 30);
      laCardNum[2].setBounds(20, 360, 100, 30);
      laCardNum[3].setBounds(675, 360, 100, 30);         
      bell.setBounds(330,150,110,100);
      cardOpen.setBounds(360,265,60,50);   
      b4.setBounds(700,410,80,40);
      b5.setBounds(700,460,80,40);
      b6.setBounds(700,510,80,40);
      
      bell.setBorderPainted(false);      
      bell.setContentAreaFilled(false);
      cardOpen.setBorderPainted(false);      
      cardOpen.setContentAreaFilled(false);
      cardOpen.setEnabled(false);	//����ڸ��� ��Ȱ��ȭ////////////////////
      
      JPanel p1=new JPanel();            //ä��â+ä���Է�â ����
      p1.setBounds(10, 410, 500, 160);
      p1.setLayout(null);    
      js4.setBounds(0, 0, 500, 110);   //ä��
      tf.setBounds(0, 115, 425, 30);   //ä���Է�â
      b1.setBounds(430, 115, 70, 30);   //ä�����۹�ư
      js.setBounds(520, 410, 170, 150); //���� ����Ʈ ���̺�
      p1.setOpaque(false);   
      
      p1.add(js4); p1.add(tf); p1.add(b1);         
      add(js);
      add(b4);add(b5);add(b6);//add(p);
      add(bell);add(cardOpen);
      add(p1);

      for (int i = 0; i < 5; i++) // 1��¥�� ī�� �̹�������
      {
         iiCard[14 * 0 + i] = new ImageIcon("cardimg/banana_1.jpg");
         iiCard[14 * 1 + i] = new ImageIcon("cardimg/lemon_1.jpg");
         iiCard[14 * 2 + i] = new ImageIcon("cardimg/peach_1.jpg");
         iiCard[14 * 3 + i] = new ImageIcon("cardimg/straw_1.jpg");
      }
     for (int i = 0; i < 3; i++) // 2,3��¥�� ī�� �̹�������
      {
         iiCard[14 * 0 + i + 5] = new ImageIcon("cardimg/banana_2.jpg");
         iiCard[14 * 1 + i + 5] = new ImageIcon("cardimg/lemon_2.jpg");
         iiCard[14 * 2 + i + 5] = new ImageIcon("cardimg/peach_2.jpg");
         iiCard[14 * 3 + i + 5] = new ImageIcon("cardimg/straw_2.jpg");
         
         iiCard[14 * 0 + i + 8] = new ImageIcon("cardimg/banana_3.jpg");
         iiCard[14 * 1 + i + 8] = new ImageIcon("cardimg/lemon_3.jpg");
         iiCard[14 * 2 + i + 8] = new ImageIcon("cardimg/peach_3.jpg");
         iiCard[14 * 3 + i + 8] = new ImageIcon("cardimg/straw_3.jpg");
      }
      for (int i = 0; i < 2; i++) // 4��¥�� ī�� �̹�������
      {
         iiCard[14 * 0 + i + 11] = new ImageIcon("cardimg/banana_4.jpg");
         iiCard[14 * 1 + i + 11] = new ImageIcon("cardimg/lemon_4.jpg");
         iiCard[14 * 2 + i + 11] = new ImageIcon("cardimg/peach_4.jpg");
         iiCard[14 * 3 + i + 11] = new ImageIcon("cardimg/straw_4.jpg");
      }
      // 5��¥�� ī�� �̹�������
      iiCard[14 * 0 + 13] = new ImageIcon("cardimg/banana_5.jpg");
      iiCard[14 * 1 + 13] = new ImageIcon("cardimg/lemon_5.jpg");
      iiCard[14 * 2 + 13] = new ImageIcon("cardimg/peach_5.jpg");
      iiCard[14 * 3 + 13] = new ImageIcon("cardimg/straw_5.jpg");

      
      for (int i = 0; i < 4; i++) {
         iiPlayerCard[i] = iiCardBack;      //����� �� ī��->ī��޸����� ����
      }
      /*���Ӱ���*/
      
      add(laPlayer[0]);
      add(laCardNum[0]);
      add(laPlayer[1]);
      add(laCardNum[1]);
      add(laPlayer[2]);
      add(laCardNum[2]);
      add(laPlayer[3]);
      add(laCardNum[3]);
   }
   public void UpdateDraw(String name, int CardNum) // �׸���
   {
      for (int i = 0; i < 4; i++) {
         if (name.equals(userName[i])) {
            iiPlayerCard[i] = iiCard[CardNum];
            repaint();
         }
      }
   }

   public void UpdateCardNum(String name, int Count) // ī�带 ������� ������Ʈ
   {
      for (int i = 0; i < 4; i++) {
         if (name.equals(userName[i])) {
            laCardNum[i].setText("���� ī��: "+Count + "��");
         }
      }
   }

   public void UpdateDead(String name) // ���� �÷��̾����� ������Ʈ
   {
      for (int i = 0; i < 4; i++) {
         if (name.equals(userName[i])) {
            iiPlayerCard[i] = iiCardBack;
            laCardNum[i].setText("GameOver");
            repaint();
         }
      }
   }
   public void CardInit() // ī�带 �ʱ�ȭ
   {
      for (int i = 0; i < 4; i++) {
         iiPlayerCard[i] = iiCardBack;
      }
      repaint();
   }
   protected void paintComponent(Graphics g) {         
      g.drawImage(back, 0, 0, getWidth(), getHeight(), this);   //this->JPanel�� ����̹��� �Ѹ�         
      iiPlayerCard[0].paintIcon(this, g, 140, 20);   //����ī��(�̰���, ,����x��ǥ,����y��ǥ)
      iiCardBack.paintIcon(this, g, 240, 20);      //������ī��1
      iiPlayerCard[1].paintIcon(this, g, 440, 20);
      iiCardBack.paintIcon(this, g, 540, 20);
      iiPlayerCard[2].paintIcon(this, g, 140, 240);
      iiCardBack.paintIcon(this, g, 240, 240);
      iiPlayerCard[3].paintIcon(this, g, 440, 240);
      iiCardBack.paintIcon(this, g, 540, 240);
   }
   
   public void setLabel(DefaultListModel model) {   //�󺧿� �̸� ����
      for (int i = 0; i < 2; i++) {
         userName[i] = (String) model.get(i);
         laPlayer[i].setText(userName[i]);
      }
   }
}
