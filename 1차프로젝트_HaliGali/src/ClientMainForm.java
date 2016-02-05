import java.awt.*; 		//Layout들어있음
import javax.swing.*;	//window관련 버튼등등이 들어있음
import java.awt.event.*;

public class ClientMainForm extends JFrame implements ActionListener{
		CardLayout card=new CardLayout();			//창 전환
		Loading ld=new Loading();					//LOADING창
		Login login=new Login();					//LOGIN창
		WaitRoom wr=new WaitRoom();					//WAITROOM창
		GameWindow gw=new GameWindow();				//게임WINDOW창
		MakeID mID=new MakeID();					//회원가입창
		MakeRoom mr=new MakeRoom();					//방만들기창
		
		
		public ClientMainForm(){

			setLayout(card);		//BorderLayout
			
			add("LOG",login);		//2.login창
			setSize(800,600);		//window창 크기 설정
			setLocation(270, 170);	//window창 위치 설정
			setVisible(true);		//보여지게 함
			setResizable(false);    //window창 고정(늘어나지 않음)
					
			add("WR",wr);						//3.WaitRoom창
			login.bt1.addActionListener(this);	//회원가입 버튼 누르면
			login.bt2.addActionListener(this);	//로그인 버튼 누르면
			wr.b1.addActionListener(this);		//로그인 버튼 누르면
			wr.b2.addActionListener(this);		//도움말 버튼 누르면
			wr.b3.addActionListener(this);      //방만들기 버튼 누르면
			wr.tf.addActionListener(this);		//사용자 입력값 받으면 
			
			add("GW",gw);						//4.GAME Window창
			wr.b3.addActionListener(this); 		//방만들기버튼 누르면
			
	
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource()==login.bt1){				    //버튼(bt1)에 액션이 가해지면
				mID.setBounds(470, 310,340,420);
				mID.setVisible(true);											//팝업창으로 회원가입창 띄우기
			}else if(e.getSource()==login.bt2){
				card.show(getContentPane(), "WR");			//Layout에 "WR"card를 상단으로 보여지게 하라
			}else if(e.getSource()==wr.tf || e.getSource()==wr.b1){			//채팅창(tf)에 액션이 가해지면
				String data=wr.tf.getText();								//입력한 값 가져오기
				wr.ta.append(data+"\n");
				wr.tf.setText("");
			}else if(e.getSource()==wr.b3){					//방만들기버튼->GAME Window창
				mr.setBounds(500, 300, 260,290);
		        mr.setVisible(true);
			}else if(e.getSource()==wr.b2){
				card.show(getContentPane(), "GW"); 		//게임 창으로 전환
			}
		}
		
		
		
		public static void main(String[] args) {
			 
			try{
				//UIManager.setLookAndFeel("com.jtattoo.plaf.noire.NoireLookAndFeel");	//폼디자인 긁어온것
			}catch(Exception ex){}
			
			ClientMainForm cm=new ClientMainForm();

		}
}
