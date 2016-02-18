package com.sist.client;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class MakeRoom extends JFrame implements ActionListener{
   JLabel la1, la2, la3, la4;
   JTextField tf;
   JRadioButton rb1,rb2;
   JPasswordField pf;
   JComboBox box;
   JButton b1,b2;
   MyPanel panel;

   public MakeRoom(){
	    setUndecorated(true); //Ÿ��Ʋ �ٰ� �������.

		setBounds(470, 310,340,420);
		setBackground(new Color(0, 0, 0, 0));
		panel = new MyPanel("img/CreateRoom.png");
        setContentPane(panel);
       
      la1 = new JLabel("���̸�");
      la2 = new JLabel("����");
      la3 = new JLabel("��й�ȣ");
      la4 = new JLabel("�ο�");
      
      tf= new JTextField("");
      pf = new JPasswordField();
      rb1=new JRadioButton("����");
      rb2=new JRadioButton("�����");
      
      la3.setVisible(false);
      pf.setVisible(false);
      ButtonGroup bg = new ButtonGroup();
      bg.add(rb1); bg.add(rb2);
      rb1.setSelected(true);
      
      box = new JComboBox();
      for(int i=2;i<=4;i++){
         box.addItem(i+"��");
      }
      
      b1=new JButton("Ȯ��");
      b2=new JButton("���");
      
      //260 290
      setLayout(null);
      la1.setBounds(40,65,40,30);
      tf.setBounds(85, 65, 150, 30);
      
      la2.setBounds(40,100,40,30);
      rb1.setBounds(85, 100,70,30);
      rb2.setBounds(160,100,70,30);
      rb1.setOpaque(false);
      rb2.setOpaque(false);
      la3.setBounds(55,145,60,30);
      pf.setBounds(120,135, 100, 30);
      
      la4.setBounds(40,190,40,30);
      box.setBounds(120,190,100,30);
      
      
      JPanel p = new JPanel();
      p.add(b1);
      p.add(b2);
      p.setBounds(40,230,195,35);
      p.setOpaque(false);
      
      //�߰�
      add(la1);add(tf);
      add(la2);add(rb1);add(rb2);
      add(la3);add(pf);
      add(la4);add(box);
      add(p);
      
      //setSize(260,290);
      //setVisible(true);
      
      
      rb1.addActionListener(this);
      rb2.addActionListener(this);
      b2.addActionListener(this);
   }


   public static void main(String[] args) {
      // TODO Auto-generated method stub

      new MakeRoom();
   }


   @Override
   public void actionPerformed(ActionEvent e) {
      // TODO Auto-generated method stub
      if(e.getSource()==rb1){
         pf.setVisible(false);
         la3.setVisible(false);
         pf.setText("");
      }else if(e.getSource()==rb2){
         pf.setVisible(true);
         la3.setVisible(true);
         pf.setText("");
         pf.requestFocus();
      }else if(e.getSource()==b2){
    	  tf.setText("");			//�ʱ�ȭ
    	  rb1.setSelected(true);
    	  la3.setVisible(false);
    	  pf.setVisible(false);
    	  box.setSelectedItem("2��");
    	  dispose();
      }
   }

}

/*class MyPanel extends JPanel	//�游��� JPanelâ 
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
}*/