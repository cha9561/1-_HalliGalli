package com.sist.client;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.ImageObserver;

public class MakeID extends  JFrame /*implements ActionListener*/{
	JLabel la1,la2,la3,la4,la5;
	JLabel rl1,rl2,rl3,rl4;		//캐릭터
	JTextField tf1,tf2;
	JRadioButton rb1,rb2,rb3,rb4;
	JPasswordField pf1,pf2;
	JButton b1,b2,b3;
	MyPanel panel;
	
	ImageIcon i1=new ImageIcon("img/11_50.png");
	ImageIcon i2=new ImageIcon("img/22_50.png");
	ImageIcon i3=new ImageIcon("img/33_50.png");
	ImageIcon i4=new ImageIcon("img/44_50.png");
	ImageIcon joinButton=new ImageIcon("img/JoinButton.png");
	ImageIcon backButton=new ImageIcon("img/BackButton.png");
	
	int num=0;
	boolean ck=false;
	
	public MakeID(){
		setUndecorated(true); //타이틀 바가 사라진다.

		setBounds(470, 310,340,420);
		setBackground(new Color(0, 0, 0, 0));
		panel = new MyPanel("img/join.png");
        setContentPane(panel);
        			
		la1=new JLabel("이름");
		la2=new JLabel("ID");
		la3=new JLabel("PW");
		la4=new JLabel("PW확인");
		la5=new JLabel("캐릭터");
		
		tf1=new JTextField();
		tf2=new JTextField();
		pf1=new JPasswordField();
		pf2=new JPasswordField();

		rb1=new JRadioButton("");
		rb2=new JRadioButton("");
		rb3=new JRadioButton("");
		rb4=new JRadioButton("");
		
		rl1=new JLabel(i1);
		rl2=new JLabel(i2);
		rl3=new JLabel(i3);
		rl4=new JLabel(i4);
		
		ButtonGroup bg=new ButtonGroup();	//캐릭터이미지
		bg.add(rb1);  bg.add(rb2);	bg.add(rb3);	bg.add(rb4);
		rb1.setSelected(true);
		rb1.setOpaque(false);rb2.setOpaque(false);
		rb3.setOpaque(false);rb4.setOpaque(false);
		
		b1=new JButton(joinButton);	//join
		b2=new JButton(backButton);	//back
		b3=new JButton("확인");
		b1.setBorderPainted(false);
		b1.setContentAreaFilled(false); 
		b2.setBorderPainted(false);
		b2.setContentAreaFilled(false); 

		//배치
		setLayout(null);
		la1.setBounds(40,65,40,30);
		tf1.setBounds(100,65,140,30);
		
		la2.setBounds(40,100,40,30);
		tf2.setBounds(100,100,140,30);
		b3.setBounds(245,100,60,30);
		
		la3.setBounds(40,145,40,30);
		pf1.setBounds(100,145,140,30);
		
		la4.setBounds(40,180,50,30);
		pf2.setBounds(100,180,140,30);
		
		la5.setBounds(40,215,40,30);
		
		rb1.setBounds(90,215,20,20);
		rb2.setBounds(170,215,20,20);
		rb3.setBounds(90,285,20,20);
		rb4.setBounds(170,285,20,20);
		
		rl1.setBounds(90,215,80,80);
		rl2.setBounds(170,215,80,80);
		rl3.setBounds(90,285,80,80);
		rl4.setBounds(170,285,80,80);

		JPanel p=new JPanel(new GridLayout(1, 2));		//Join,Back버튼
		p.add(b1);
		p.add(b2);
		p.setOpaque(false);
		p.setBounds(60, 355, 200, 40);
		
		
		add(la1);add(tf1);
		add(la2);add(tf2);add(b3);
		add(la3);add(pf1);
		add(la4);add(pf2);
		add(la5);add(rb1);add(rb2);add(rb3);add(rb4);
		add(rl1);add(rl2);add(rl3);add(rl4);
		add(p);
		
	/*	b1.addActionListener(this);
		b2.addActionListener(this);
		b3.addActionListener(this);*/
	}

	public static void main(String[] args){
		new MakeID();
	}

	/*@Override
	public void actionPerformed(ActionEvent e) {
		if(b1==e.getSource()){
			JOptionPane.showMessageDialog(this, "회원가입완료");
			dispose();	//메모리 그대로 둔 채로 창닫기 
			//System.exit(0); //어플을 종료시키려면 
		}else if(b2==e.getSource()){
			System.out.println("취소");
		}else if(b3==e.getSource()){
			System.out.println("ID중복체크");
		}
		
	}*/
}

	
class MyPanel extends JPanel	//회원가입 JPanel창 
{
    Image image;

    MyPanel(String img)
    {
        image = Toolkit.getDefaultToolkit().createImage(img);
        setOpaque(false);
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
    }
}
