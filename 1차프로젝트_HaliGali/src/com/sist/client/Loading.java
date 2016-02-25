package com.sist.client;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*; 
import java.awt.event.*;
import java.io.File;
import java.net.MalformedURLException;
import javax.swing.*; 
import javax.swing.event.*;

public class Loading extends JPanel implements Runnable{
	
	CardLayout card=new CardLayout();
	Image back;				
	JProgressBar pb; 		
    JLabel status; 
    JButton st;
    ImageIcon sti=new ImageIcon("img/enter1.png"); 
    AudioClip clip;
    Login log;

	public Loading(){
		
		back=Toolkit.getDefaultToolkit().getImage("img/loading.png");		 
        pb = new JProgressBar(); 
        pb.setMinimum(0); 
        pb.setMaximum(100); 
        pb.setValue(0); 
        pb.setBackground(Color.white);
        pb.setForeground(Color.pink);
        
        st=new JButton("Enter",sti); // icon 제작해서 추가
        st.setBounds(350,400,110,50);
        st.setBorderPainted(false);
        st.setFocusPainted(false);
        st.setContentAreaFilled(false);
        st.setVisible(false); 
       
        status = new JLabel(""); 
        status.setBounds(20,540,200,30);
        add(pb,"Center"); 
        add(status); 
        
        setLayout(null);
        pb.setBounds(0,500,792,40);
        add(pb);
        add(st);
        
        
        // 게임 BGM띄우는 Code
        try {
            File file = new File("res/loading.wav"); 
            clip = Applet.newAudioClip(file.toURL());
            clip.play(); 
            clip.loop();
           
        } catch (MalformedURLException e){
            e.printStackTrace();
        }
	}
	
	
	 public void go() 
	 { 
		 
         try        
         { 
                 for(int i=0;i<=100;i++) 
                 { 
                         pb.setValue(i); 
                         Thread.sleep(5); 
                         if(i<=50)
                         {
                         	status.setText(i+"% Now Loading..."); 
                         }
                         if(i>50 && i<100)
                         {
                         	status.setText(i+"% Please Wait..."); 
                         }
                         if(i==100)
                         {
                         	status.setText(i+"% Press Enter!!");
                         	st.setVisible(true);
                         }
                 }
         }catch (Exception e) {} 
 } 


	protected void paintComponent(Graphics g) {				
		g.drawImage(back, 0, 0, getWidth(), getHeight(), this);	//this->JPanel에 배경이미지 뿌림
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		go();
	}


//	@Override
//	public void actionPerformed(ActionEvent e) {
//		// TODO Auto-generated method stub
//		if(e.getSource()==st)
//		{
//			System.out.println("할리갈리 안쪽으로 접근중...");
//			
//		}
//	}


	

}
         
		
       
