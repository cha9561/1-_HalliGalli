package com.sist.client;
import java.awt.*;       //Layout�������
import javax.swing.*;   //window���� ��ư����� �������
import java.awt.event.*;

public class Help extends JFrame implements ActionListener{
      
      Image[] img;
      JPanel panel;
      JButton previous,next,close;
      int page;
      
      public Help()
      {
         
         System.out.println("How To Play");
         img = new Image[5];
         for(int i=0; i<img.length; i++)
            img[i]=Toolkit.getDefaultToolkit().getImage("img\\how"+(i+1)+".png");
         setLayout(null);
         ///setBounds(0,0,450,250);
         
         panel = new JPanel();
         panel.setLayout(null);
         panel.setBounds(100,200,50,70);
         panel.setOpaque(false);
         add(panel);
         
         ImageIcon previcon = new ImageIcon("C:\\image\\previous.png"); // ������
         ImageIcon nexticon = new ImageIcon("C:\\image\\next.png"); // ������
         ImageIcon closeicon = new ImageIcon("c:\\image\\close.png"); // ������
         
         previous = new JButton("����",previcon);
         previous.setBounds(20,30,60,40);
         panel.add(previous);
         
         next = new JButton("����",nexticon);
         next.setBounds(120,30,60,40);
         panel.add(next);
         
         close = new JButton("�ݱ�",closeicon);
         close.setBounds(150,150,60,40);
         panel.add(close);
         
         previous.addActionListener(this);
         next.addActionListener(this);
         close.addActionListener(this);
         previous.setEnabled(false);

         setBounds(350,200,500,500);
         //setVisible(true);
      }      
      
      

      protected void paintComponent(Graphics g) {
         // TODO Auto-generated method stub
         g.drawImage(img[page], 0, 0, getWidth(), getHeight(), this);
      }
      


      @Override
      public void actionPerformed(ActionEvent e) {
         // TODO Auto-generated method stub
         if(e.getSource()==previous)
         {
            page--;
            next.setEnabled(true);
            if(page==0)
               previous.setEnabled(false);
         }
         else if(e.getSource()==next)
         {
            page++;
            previous.setEnabled(true);
            if(page == img.length-1)
               next.setEnabled(false);
            
         }
         else if(e.getSource()==close)
         {
            dispose();
         }
         repaint();
      }
   }      
