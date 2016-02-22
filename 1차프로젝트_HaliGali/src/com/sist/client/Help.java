package com.sist.client;
import java.awt.*;       //Layout들어있음
import javax.swing.*;   //window관련 버튼등등이 들어있음
import java.awt.event.*;

public class Help extends JFrame implements ActionListener{
      
      Image[] img;
      JLabel l1;
      JPanel panel, pan;
      JButton previous,next,close;
      int page=1;
      ImageIcon backButton=new ImageIcon("img/BackButton.png");
      ImageIcon h1=new ImageIcon("img/how1.jpg");
      ImageIcon h2=new ImageIcon("img/how2.jpg");
      ImageIcon h3=new ImageIcon("img/how3.jpg");
      ImageIcon h4=new ImageIcon("img/how4.jpg");
      ImageIcon h5=new ImageIcon("img/how5.jpg");
      ImageIcon h6=new ImageIcon("img/how6.jpg");
      public Help()
      {
    	  setUndecorated(true);
    	  setLayout(null);
    	

    	 l1=new JLabel();
    	 
    	 l1.setBounds(30, 50, 440, 440);
    	 l1.setIcon(new ImageIcon(setImage("img/how1.jpg", l1.getWidth(), l1.getHeight())));
    	 previous=new JButton("previous");	//previous
  		 next =new JButton("next");	//next
  		close = new JButton(backButton); // 닫기
  		close.setBorderPainted(false);
		close.setContentAreaFilled(false); 
  		
  		JPanel p1=new JPanel(new GridLayout(1, 2));//이전,다음 버튼
    	  JPanel p2=new JPanel();//이전,다음 버튼
    	  previous.setEnabled(false);
    	  p1.add(previous);
  		p1.add(next);
  		
  		p2.add(close);
  		add(l1);
      
  		p1.setOpaque(false);
  		p2.setOpaque(false);
  		
  		setBounds(350,200,500,500);
  		
  		
  		
  		p1.setBounds(140, 10, 190, 30);
  		p2.setBounds(400, 3, 100, 60);
  		
  		
  		
  		add(p1);
  		add(p2);

  		
  	
  		previous.addActionListener(this);
  		next.addActionListener(this);
  		close.addActionListener(this);
  		

        
      }      
      
      

      
    
      protected void paintComponent(Graphics g) {
         // TODO Auto-generated method stub
         g.drawImage(img[page], 0, 0, getWidth(), getHeight(), this);
      }
      
      public Image setImage(String filename,int width, int height)
      {
      	ImageIcon ii = new ImageIcon(filename);
      	Image image = ii.getImage().getScaledInstance(width, height,Image.SCALE_SMOOTH);
      	return image;
      }


      @Override
      public void actionPerformed(ActionEvent e) {
         // TODO Auto-generated method stub
        
    	  if(e.getSource()==previous)
         {
    		  next.setEnabled(true);
        	 if(page==2)
                 previous.setEnabled(false);
        	 page--;
            
            l1.setIcon(new ImageIcon(setImage("img/how"+(page)+".jpg", l1.getWidth(), l1.getHeight())));
            add(l1);
            
            
         }
         else if(e.getSource()==next)
         {
        	 previous.setEnabled(true);
        	 if(page>4){
        		 next.setEnabled(false);
        	 }
        	 
        	 page++;
             	l1.setIcon(new ImageIcon(setImage("img/how"+(page)+".jpg", l1.getWidth(), l1.getHeight())));
             	add(l1);
             
             	
        	 
         }
         else if(e.getSource()==close)
         {
            dispose();
         }
         repaint();
      }
   }      
