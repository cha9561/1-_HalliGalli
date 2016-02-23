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
   JTextArea profile1,profile2,profile3,profile4;
   JScrollBar bar;
   JButton bell,cardOpen;      //bell,ī�������
   ImageIcon iiBell,hand;      //��,ī������� ��ư�̹���   
   
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
      
      iiPlayerCard = new ImageIcon[4]; //  ����� �����������? ī��޸�
      iiCard = new ImageIcon[56];     // ��ī�� �̹���
      laPlayer = new JLabel[2];   //����� ��
      laCardNum = new JLabel[2];  // ī�� ��
      userName = new String[2];  // ������̸�
      
      ta=new JTextArea();   
      JScrollPane js4=new JScrollPane(ta);   //��ũ�ѵǴ� ä��â
      bar=js4.getVerticalScrollBar();
         
      tf=new JTextField();               //ä���Է�â
      b1=new JButton("����");               //ä�����۹�ư      
      b4=new JButton("�غ�");               
      b5=new JButton("����");               
      b6=new JButton("������");            
      profile1=new JTextArea();            //����� ����â
      profile2=new JTextArea();
      profile3=new JTextArea();
      profile4=new JTextArea();
      bell=new JButton(iiBell);            //����ư
      cardOpen=new JButton(hand);            //ī��������ư
   
      setLayout(null);      

      profile1.setBounds(0, 0, 120, 80);   
      profile2.setBounds(655, 0, 120, 80);
      profile3.setBounds(0, 320, 120, 80);
      profile4.setBounds(655, 320, 120, 80);   
                  
      bell.setBounds(330,150,110,100);
      cardOpen.setBounds(380,220,60,50);   
      b4.setBounds(700,410,80,40);
      b5.setBounds(700,465,80,40);
      b6.setBounds(700,515,80,40);
      
      bell.setBorderPainted(false);      
      bell.setContentAreaFilled(false);
      cardOpen.setBorderPainted(false);      
      cardOpen.setContentAreaFilled(false);
      
      JPanel p1=new JPanel();            //ä��â+ä���Է�â ����
      p1.setBounds(10, 410, 350, 200);
      p1.setLayout(null);    
      js4.setBounds(0, 0, 350, 125);   //ä��
      tf.setBounds(0, 125, 300, 30);   //ä���Է�â
      b1.setBounds(300, 125, 50, 30);   //ä�����۹�ư
      p1.setOpaque(false);   
      p1.add(js4); p1.add(tf); p1.add(b1);         
      
      add(b4);add(b5);add(b6);//add(p);
      add(profile1);add(profile2);add(profile3);add(profile4);
      add(bell);add(cardOpen);
      add(p1);

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

      
      for (int i = 0; i < 4; i++) {
         iiPlayerCard[i] = iiCardBack;      //����� �� ī��->ī��޸����� ����
      }
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
   11
   public void setLabel(DefaultListModel model) {   //�󺧿� �̸� ����
      for (int i = 0; i < 2; i++) {
         userName[i] = (String) model.get(i);
         laPlayer[i].setText(userName[i]);
      }
   }
}
