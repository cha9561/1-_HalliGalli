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


public class GameWindow extends JPanel {	//ūƲ
	Image back;	
	ImageIcon iiBell,hand;		//��,ī������� ��ư�̹���		
	JTextArea ta;
	JTextField tf;
	JButton b1,b2,b3,b4,b5,b6;
	JTextArea profile1,profile2,profile3,profile4;
	JScrollBar bar;
	MyPanel p;
	
	DefaultListModel model = new DefaultListModel(); // ����Ʈ��(����Ʈ ���� DB)
	JList li_Player = new JList(model); 			// ����� ���� ����Ʈ
	
	public GameWindow(){
		back=Toolkit.getDefaultToolkit().getImage("img/monkey_back3.jpg");	//�޹��		
		
		ta=new JTextArea();	
		JScrollPane js4=new JScrollPane(ta);	//��ũ�ѵǴ� ä��â
		bar=js4.getVerticalScrollBar();
		
		iiBell = new ImageIcon("img/Bell.png"); 			// ��
		hand=new ImageIcon("img/hand.png");					//�ո�� 	
		
		tf=new JTextField();					//ä���Է�â
		b1=new JButton("����");					//ä�����۹�ư
		b2=new JButton(hand);					
		b3=new JButton(iiBell);
		b4=new JButton("�غ�");					
		b5=new JButton("����");					
		b6=new JButton("������");				
		profile1=new JTextArea();				//����� ����â
		profile2=new JTextArea();
		profile3=new JTextArea();
		profile4=new JTextArea();
		
		
		setLayout(null);
		
		p = new MyPanel("img/gameBoard.png");					//ī����� ���� �ǳ�
		p.setBounds(10, 10, 775, 395); 			
		p.setLayout(null);
		p.setOpaque(true); 
		profile1.setBounds(0, 0, 120, 80);	
		profile2.setBounds(655, 0, 120, 80);
		profile3.setBounds(0, 320, 120, 80);
		profile4.setBounds(655, 320, 120, 80);	

		b2.setBounds(380,220,60,50);
		b3.setBounds(330,150,110,100);
		b3.setBorderPainted(false);		
		b3.setContentAreaFilled(false);
		b2.setBorderPainted(false);		
		b2.setContentAreaFilled(false);
		b4.setBounds(700,410,80,40);
		b5.setBounds(700,465,80,40);

		b6.setBounds(700,515,80,40);
		p.add(profile1);p.add(profile2);p.add(profile3);p.add(profile4);
		p.add(b2);p.add(b3);
		
		add(b4);add(b5);add(b6);
		add(p);
		
		JPanel p1=new JPanel();				//ä��â+ä���Է�â ����
		p1.setBounds(10, 410, 500, 200);
		p1.setLayout(null); 	
		js4.setBounds(0, 0, 500, 120);	//ä��
		tf.setBounds(0, 125, 430, 30);	//ä���Է�â
		b1.setBounds(435, 125, 65, 30);	//ä�����۹�ư
		p1.setOpaque(false);
		p1.add(js4); p1.add(tf); p1.add(b1);
		add(p1);
	}
	
	protected void paintComponent(Graphics g) {			
		g.drawImage(back, 0, 0, getWidth(), getHeight(), this);	//this->JPanel�� ����̹��� �Ѹ�
		
	}

}


class MyPanel extends JPanel	//����panelâ !!!!
{
    Image image;				//����panel����̹���
    PrintWriter writer;
	ImageIcon iiCard[];			//��ī��56�� 
	ImageIcon iiCardBack;		//ī��޸�
	
	ImageIcon iiPlayerCard[];	//���÷��̾��� ī�� ���� �������
	JLabel laPlayer[];			//����� ��
	JLabel laCardNum[];			//ī�� �̸� 
	String userName[];			//����� �̸�

    MyPanel(String img)			//����panel����̹��� ����
    {
        image = Toolkit.getDefaultToolkit().createImage(img);
        setOpaque(false);      
        iiCardBack = new ImageIcon("cardimg/CardBack.jpg"); // ī�� �޸�
		
        setLayout(null);
		iiPlayerCard = new ImageIcon[2];	//����� �����������? ī��޸�
		iiCard = new ImageIcon[56];		//��ī�� �̹���
		laPlayer = new JLabel[2];	//����� ��
		laCardNum = new JLabel[2];	//ī�� ��
		userName = new String[2];	//������̸�

		for (int i = 0; i < 5; i++) // 1��¥�� ī�� �̹�������
		{
			iiCard[14 * 0 + i] = new ImageIcon("cardimg/banana1.jpg");
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

		
		for (int i = 0; i < 2; i++) {
			iiPlayerCard[i] = iiCardBack;		//����� �� ī��->ī��޸����� ����
		}
		
		laPlayer[0] = new JLabel("Player1");	//��������̸��� ī��� ��
		laPlayer[0].setBounds(1, 1, 50, 15);
		add(laPlayer[0]);
		laCardNum[0] = new JLabel("0��");
		laCardNum[0].setBounds(50, 1, 70, 15);
		add(laCardNum[0]);
		
		laPlayer[1] = new JLabel("Player2");
		laPlayer[1].setBounds(250, 1, 50, 15);
		add(laPlayer[1]);
		laCardNum[1] = new JLabel("0��");
		laCardNum[1].setBounds(300, 1, 70, 15);
		add(laCardNum[1]);
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        if (image != null)
        {
            g.drawImage(image, 0, 0, getWidth(),getHeight(),this);
        }
        
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.0f));
        g2d.setColor(getBackground());
        g2d.fill(getBounds());
        g2d.dispose();

		iiPlayerCard[0].paintIcon(this, g, 10, 20);	//����ī��(�̰���, ,����x��ǥ,����y��ǥ)
		iiCardBack.paintIcon(this, g, 110, 20);		//������ī��
		iiPlayerCard[1].paintIcon(this, g, 250, 20);
		iiCardBack.paintIcon(this, g, 350, 20);
    }
    
    public void setLabel(DefaultListModel model) {	//�󺧿� �̸� ����
		for (int i = 0; i < 2; i++) {
			userName[i] = (String) model.get(i);
			laPlayer[i].setText(userName[i]);
		}
	}
}
