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


public class GameWindow extends JPanel {	//큰틀
	Image back;	
	ImageIcon iiBell,hand;		//종,카드뒤집기 버튼이미지		
	JTextArea ta;
	JTextField tf;
	JButton b1,b2,b3,b4,b5,b6;
	JTextArea profile1,profile2,profile3,profile4;
	JScrollBar bar;
	MyPanel p;
	
	DefaultListModel model = new DefaultListModel(); // 리스트모델(리스트 내용 DB)
	JList li_Player = new JList(model); 			// 사용자 접속 리스트
	
	public GameWindow(){
		back=Toolkit.getDefaultToolkit().getImage("img/monkey_back3.jpg");	//뒷배경		
		
		ta=new JTextArea();	
		JScrollPane js4=new JScrollPane(ta);	//스크롤되는 채팅창
		bar=js4.getVerticalScrollBar();
		
		iiBell = new ImageIcon("img/Bell.png"); 			// 종
		hand=new ImageIcon("img/hand.png");					//손모양 	
		
		tf=new JTextField();					//채팅입력창
		b1=new JButton("전송");					//채팅전송버튼
		b2=new JButton(hand);					
		b3=new JButton(iiBell);
		b4=new JButton("준비");					
		b5=new JButton("시작");					
		b6=new JButton("나가기");				
		profile1=new JTextArea();				//사용자 정보창
		profile2=new JTextArea();
		profile3=new JTextArea();
		profile4=new JTextArea();
		
		
		setLayout(null);
		
		p = new MyPanel("img/gameBoard.png");					//카드게임 실행 판넬
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
		
		JPanel p1=new JPanel();				//채팅창+채팅입력창 묶음
		p1.setBounds(10, 410, 500, 200);
		p1.setLayout(null); 	
		js4.setBounds(0, 0, 500, 120);	//채팅
		tf.setBounds(0, 125, 430, 30);	//채팅입력창
		b1.setBounds(435, 125, 65, 30);	//채팅전송버튼
		p1.setOpaque(false);
		p1.add(js4); p1.add(tf); p1.add(b1);
		add(p1);
	}
	
	protected void paintComponent(Graphics g) {			
		g.drawImage(back, 0, 0, getWidth(), getHeight(), this);	//this->JPanel에 배경이미지 뿌림
		
	}

}


class MyPanel extends JPanel	//게임panel창 !!!!
{
    Image image;				//게임panel배경이미지
    PrintWriter writer;
	ImageIcon iiCard[];			//총카드56개 
	ImageIcon iiCardBack;		//카드뒷면
	
	ImageIcon iiPlayerCard[];	//각플레이어의 카드 내기 직전모양
	JLabel laPlayer[];			//사용자 라벨
	JLabel laCardNum[];			//카드 이름 
	String userName[];			//사용자 이름

    MyPanel(String img)			//게임panel배경이미지 설정
    {
        image = Toolkit.getDefaultToolkit().createImage(img);
        setOpaque(false);      
        iiCardBack = new ImageIcon("cardimg/CardBack.jpg"); // 카드 뒷면
		
        setLayout(null);
		iiPlayerCard = new ImageIcon[2];	//사용자 내기직전모양? 카드뒷면
		iiCard = new ImageIcon[56];		//총카드 이미지
		laPlayer = new JLabel[2];	//사용자 라벨
		laCardNum = new JLabel[2];	//카드 라벨
		userName = new String[2];	//사용자이름

		for (int i = 0; i < 5; i++) // 1개짜리 카드 이미지설정
		{
			iiCard[14 * 0 + i] = new ImageIcon("cardimg/banana1.jpg");
			iiCard[14 * 1 + i] = new ImageIcon("cardimg/lemon_1.jpg");
			iiCard[14 * 2 + i] = new ImageIcon("cardimg/peach_1.jpg");
			iiCard[14 * 3 + i] = new ImageIcon("cardimg/straw_1.jpg");
		}
		for (int i = 0; i < 3; i++) // 2,3개짜리 카드 이미지설정
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
		for (int i = 0; i < 2; i++) // 4개짜리 카드 이미지설정
		{
			iiCard[14 * 0 + i + 11] = new ImageIcon("cardimg/banana_4.jpg");
			iiCard[14 * 1 + i + 11] = new ImageIcon("cardimg/lemon_4.jpg");
			iiCard[14 * 2 + i + 11] = new ImageIcon("cardimg/peach_4.jpg");
			iiCard[14 * 3 + i + 11] = new ImageIcon("cardimg/straw_4.jpg");
		}
		// 5개짜리 카드 이미지설정
		iiCard[14 * 0 + 13] = new ImageIcon("cardimg/banana_5.jpg");
		iiCard[14 * 1 + 13] = new ImageIcon("cardimg/lemon_5.jpg");
		iiCard[14 * 2 + 13] = new ImageIcon("cardimg/peach_5.jpg");
		iiCard[14 * 3 + 13] = new ImageIcon("cardimg/straw_5.jpg");

		
		for (int i = 0; i < 2; i++) {
			iiPlayerCard[i] = iiCardBack;		//사용자 각 카드->카드뒷면으로 지정
		}
		
		laPlayer[0] = new JLabel("Player1");	//각사용자이름과 카드수 라벨
		laPlayer[0].setBounds(1, 1, 50, 15);
		add(laPlayer[0]);
		laCardNum[0] = new JLabel("0장");
		laCardNum[0].setBounds(50, 1, 70, 15);
		add(laCardNum[0]);
		
		laPlayer[1] = new JLabel("Player2");
		laPlayer[1].setBounds(250, 1, 50, 15);
		add(laPlayer[1]);
		laCardNum[1] = new JLabel("0장");
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

		iiPlayerCard[0].paintIcon(this, g, 10, 20);	//왼쪽카드(이곳에, ,시작x좌표,시작y좌표)
		iiCardBack.paintIcon(this, g, 110, 20);		//오른쪽카드
		iiPlayerCard[1].paintIcon(this, g, 250, 20);
		iiCardBack.paintIcon(this, g, 350, 20);
    }
    
    public void setLabel(DefaultListModel model) {	//라벨에 이름 쓰기
		for (int i = 0; i < 2; i++) {
			userName[i] = (String) model.get(i);
			laPlayer[i].setText(userName[i]);
		}
	}
}
