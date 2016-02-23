package com.sist.client;
/////////////////////////////////////////////////////

// 제작자 : 성종천(mudchobo@nate.com)
// 블로그 : http://mudchobo.tomeii.com/
// 프로그램 : 할리갈리 클라이언트 프로그램
/////////////////////////////////////////////////////

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

class HalliGalli_Board extends JPanel {
   PrintWriter writer;
   ImageIcon iiCard[];
   ImageIcon iiCardBack;
   ImageIcon iiBell;
   ImageIcon iiPlayerCard[];
   JLabel laPlayer[];
   JLabel laCardNum[];
   String userName[];

   public HalliGalli_Board() {
      setLayout(null);
      iiPlayerCard = new ImageIcon[4];
      iiCard = new ImageIcon[56];
      laPlayer = new JLabel[4];
      laCardNum = new JLabel[4];
      userName = new String[4];

      for (int i = 0; i < 5; i++) // 1개짜리 카드 이미지설정
      {
         iiCard[14 * 0 + i] = new ImageIcon("image/banana_1.jpg");
         iiCard[14 * 1 + i] = new ImageIcon("image/lemon_1.jpg");
         iiCard[14 * 2 + i] = new ImageIcon("image/peach_1.jpg");
         iiCard[14 * 3 + i] = new ImageIcon("image/straw_1.jpg");
      }
      for (int i = 0; i < 3; i++) // 2,3개짜리 카드 이미지설정
      {
         iiCard[14 * 0 + i + 5] = new ImageIcon("image/banana_2.jpg");
         iiCard[14 * 1 + i + 5] = new ImageIcon("image/lemon_2.jpg");
         iiCard[14 * 2 + i + 5] = new ImageIcon("image/peach_2.jpg");
         iiCard[14 * 3 + i + 5] = new ImageIcon("image/straw_2.jpg");
         iiCard[14 * 0 + i + 8] = new ImageIcon("image/banana_3.jpg");
         iiCard[14 * 1 + i + 8] = new ImageIcon("image/lemon_3.jpg");
         iiCard[14 * 2 + i + 8] = new ImageIcon("image/peach_3.jpg");
         iiCard[14 * 3 + i + 8] = new ImageIcon("image/straw_3.jpg");
      }
      for (int i = 0; i < 2; i++) // 4개짜리 카드 이미지설정
      {
         iiCard[14 * 0 + i + 11] = new ImageIcon("image/banana_4.jpg");
         iiCard[14 * 1 + i + 11] = new ImageIcon("image/lemon_4.jpg");
         iiCard[14 * 2 + i + 11] = new ImageIcon("image/peach_4.jpg");
         iiCard[14 * 3 + i + 11] = new ImageIcon("image/straw_4.jpg");
      }
      // 5개짜리 카드 이미지설정
      iiCard[14 * 0 + 13] = new ImageIcon("image/banana_5.jpg");
      iiCard[14 * 1 + 13] = new ImageIcon("image/lemon_5.jpg");
      iiCard[14 * 2 + 13] = new ImageIcon("image/peach_5.jpg");
      iiCard[14 * 3 + 13] = new ImageIcon("image/straw_5.jpg");

      iiCardBack = new ImageIcon("image/CardBack.jpg"); // 카드 뒷면
      iiBell = new ImageIcon("image/Bell.gif"); // 종
      for (int i = 0; i < 4; i++) {
         iiPlayerCard[i] = iiCardBack;
      }
      laPlayer[0] = new JLabel("Player1");
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
      laPlayer[2] = new JLabel("Player3");
      laPlayer[2].setBounds(1, 300, 50, 15);
      add(laPlayer[2]);
      laCardNum[2] = new JLabel("0장");
      laCardNum[2].setBounds(50, 300, 70, 15);
      add(laCardNum[2]);
      laPlayer[3] = new JLabel("Player4");
      laPlayer[3].setBounds(250, 300, 50, 15);
      add(laPlayer[3]);
      laCardNum[3] = new JLabel("0장");
      laCardNum[3].setBounds(300, 300, 70, 15);
      add(laCardNum[3]);
   }

   protected void paintComponent(Graphics g) // 그리기
   {
      super.paintComponent(g);
      iiPlayerCard[0].paintIcon(this, g, 10, 20);
      iiCardBack.paintIcon(this, g, 110, 20);
      iiPlayerCard[1].paintIcon(this, g, 250, 20);
      iiCardBack.paintIcon(this, g, 350, 20);
      iiPlayerCard[2].paintIcon(this, g, 10, 320);
      iiCardBack.paintIcon(this, g, 110, 320);
      iiPlayerCard[3].paintIcon(this, g, 250, 320);
      iiCardBack.paintIcon(this, g, 350, 320);
      iiBell.paintIcon(this, g, 180, 180);
   }

   public void setWriter(PrintWriter writer) {
      this.writer = writer;
   }

   public void setLabel(DefaultListModel model) {
      for (int i = 0; i < 4; i++) {
         userName[i] = (String) model.get(i);
         laPlayer[i].setText(userName[i]);
      }
   }

   public void UpdateDraw(String name, int CardNum) // 그리기
   {
      for (int i = 0; i < 4; i++) {
         if (name.equals(userName[i])) {
            iiPlayerCard[i] = iiCard[CardNum];
            repaint();
         }
      }
   }

   public void UpdateCardNum(String name, int Count) // 카드를 냈을경우 업데이트
   {
      for (int i = 0; i < 4; i++) {
         if (name.equals(userName[i])) {
            laCardNum[i].setText(Count + "장");
         }
      }
   }

   public void UpdateDead(String name) // 죽은 플레이어정보 업데이트
   {
      for (int i = 0; i < 4; i++) {
         if (name.equals(userName[i])) {
            iiPlayerCard[i] = iiCardBack;
            laCardNum[i].setText("GameOver");
            repaint();
         }
      }
   }

   public void CardInit() // 카드를 초기화
   {
      for (int i = 0; i < 4; i++) {
         iiPlayerCard[i] = iiCardBack;
      }
      repaint();
   }
}

public class HalliGalli_Client extends JFrame implements Runnable, ActionListener {
   JTextArea ta_MsgView; // 메시지를 보여주는 텍스트영역
   JScrollPane scPane; // 스크롤
   JTextField tf_Send = new JTextField(""); // 보낼 메시지를 적는필드
   JTextField tf_Name = new JTextField(); // 사용자 이름 상자
   JTextField tf_ipaddress = new JTextField(); // 서버에 접속할 ip주소입력필드
   DefaultListModel model = new DefaultListModel(); // 리스트모델
   JList li_Player = new JList(model); // 사용자 리스트
   JButton connectButton = new JButton("접속"); // 시작 버튼
   JButton readyButton = new JButton("준비"); // 종료버튼
   JButton turnButton = new JButton("카드뒤집기"); // 카드 뒤집기 버튼
   JButton bellButton = new JButton("종치기"); // 종치기 버튼
   JLabel la_GameInfo = new JLabel("<정보창>"); // 정보창
   JLabel la_PlayerInfo = new JLabel("<인원정보>"); // 인원정보
   boolean ready = false;

   HalliGalli_Board board = new HalliGalli_Board();
   BufferedReader reader; // 입력스트림
   PrintWriter writer; // 출력스트림
   Socket socket; // 소켓
   String userName = null; // 사용자 이름

   public HalliGalli_Client() {
      super("할리갈리 온라인게임");
      Container ct = getContentPane();
      ct.setLayout(null);

      ta_MsgView = new JTextArea(1, 1);
      scPane = new JScrollPane(ta_MsgView);

      // 각종 컴포넌트를 생성하고 배치한다.
      EtchedBorder eborder = new EtchedBorder(EtchedBorder.RAISED);
      ta_MsgView.setEditable(false);
      la_GameInfo.setBounds(10, 30, 480, 30);
      ct.add(la_GameInfo);
      board.setBounds(10, 70, 480, 480);
      board.setBorder(eborder);
      ct.add(board);

      JPanel p1 = new JPanel();
      p1.setLayout(new GridLayout(4, 4));
      p1.add(new Label("서버주소 : ", 2));
      p1.add(tf_ipaddress);
      p1.add(new Label("이름 : ", 2));
      p1.add(tf_Name);
      p1.add(connectButton);
      p1.add(readyButton);
      readyButton.setEnabled(false);
      p1.setBounds(500, 30, 250, 70);

      JPanel p2 = new JPanel();
      p2.setLayout(new BorderLayout());
      JPanel p2_1 = new JPanel();
      p2_1.add(turnButton);
      p2_1.add(bellButton);
      p2.add(la_PlayerInfo, "North");
      p2.add(li_Player, "Center");
      p2.add(p2_1, "South");
      turnButton.setEnabled(false);
      bellButton.setEnabled(false);
      p2.setBounds(500, 110, 250, 180);
      p2.setBorder(eborder);

      JPanel p3 = new JPanel();
      p3.setLayout(new BorderLayout());
      p3.add(scPane, "Center");
      p3.add(tf_Send, "South");
      p3.setBounds(500, 300, 250, 250);
      p3.setBorder(eborder);

      ct.add(p1);
      ct.add(p2);
      ct.add(p3);

      tf_Send.addActionListener(this);
      readyButton.addActionListener(this);
      connectButton.addActionListener(this);
      turnButton.addActionListener(this);
      bellButton.addActionListener(this);

      ta_MsgView.append("이름을 입력하고 접속버튼을 누르세요\n");
   }

   public void actionPerformed(ActionEvent ae) {
      try {
         if (ae.getSource() == tf_Send) // 보내기텍스트필드에 글을 입력했을 경우
         {
            String msg = tf_Send.getText();
            if (msg.length() == 0) {
               return;
            }
            if (msg.length() >= 30) {
               msg = msg.substring(0, 30);
            }
            writer.println("[MSG]" + msg);
            tf_Send.setText("");
         } else if (ae.getSource() == connectButton) // 접속버튼을 눌렀을 경우
         {
            String name = tf_Name.getText().trim();
            if (name.length() <= 2 || name.length() > 10) {
               la_GameInfo.setText("이름이 잘못되었습니다.");
               tf_Name.requestFocus();
               return;
            }
            connect();
            userName = name;
            tf_Name.setText(userName);
            tf_Name.setEditable(false);
            tf_ipaddress.setEditable(false);
            la_GameInfo.setText("접속성공");
            writer.println("[CONNECT]" + userName);
            connectButton.setEnabled(false);
            readyButton.setEnabled(true);
         } else if (ae.getSource() == readyButton) // 레디해제버튼을 눌렀을 경우
         {

            if (!ready) {
               ready = true;
               writer.println("[READY]");
               readyButton.setText("준비해제");
               la_GameInfo.setText("준비완료");
            } else {
               ready = false;
               writer.println("[NOREADY]");
               readyButton.setText("준비");
               la_GameInfo.setText("준비해제");
            }
         } else if (ae.getSource() == turnButton) // 카드뒤집기버튼을 눌렀을 경우
         {
            la_GameInfo.setText("당신이 카드를 뒤집습니다.");
            writer.println("[TURN]" + userName);
         } else if (ae.getSource() == bellButton) // 종치기버튼을 눌렀을 경우
         {
            writer.println("[BELL]" + userName);
         }
      } catch (Exception e) {
      }
   }

   public void run() {
      String msg;
      try {
         while ((msg = reader.readLine()) != null) {
            if (msg.startsWith("[FULL]")) // 서버에 접속인원이 다찼을 경우
            {
               la_GameInfo.setText("방에 인원이 다찼습니다.");
            }

            else if (msg.startsWith("[PLAYERS]")) // 플레이어리스트를 받는다.
            {
               nameList(msg.substring(9));
            }

            else if (msg.startsWith("[ENTER]")) // 상대방 입장할 경우
            {
               model.addElement(msg.substring(7));
               playersInfo();
               ta_MsgView.append("[" + msg.substring(7) + "]님이 입장하였습니다.\n");
               scPane.getVerticalScrollBar().setValue(scPane.getVerticalScrollBar().getMaximum());
               validate();
            }

            else if (msg.startsWith("[DISCONNECT]")) // 접속이 끊어진경우
            {
               model.removeElement(msg.substring(6));
               playersInfo();
               ta_MsgView.append("[" + msg.substring(12) + "]님이 나갔습니다.\n");
               scPane.getVerticalScrollBar().setValue(scPane.getVerticalScrollBar().getMaximum());
               validate();
            }

            else if (msg.startsWith("게임을 시작합니다.")) // 서버에서 게임이 시작된 경우
            {
               board.setLabel(model);
               turnButton.setEnabled(true);
               bellButton.setEnabled(true);
               readyButton.setEnabled(false);
            }

            else if (msg.startsWith("[REPAINT]")) // 서버에서 카드뒤집은 뒤 다시 그리기 요청
            {
               int a = msg.indexOf("|");
               int b = Integer.parseInt(msg.substring(a + 1));
               board.UpdateDraw(msg.substring(9, a), b);
            }

            else if (msg.startsWith("[CARDNUM]")) // 현재 카드수를 받는다.
            {
               int a = msg.indexOf("|");
               int b = Integer.parseInt(msg.substring(a + 1));
               board.UpdateCardNum(msg.substring(9, a), b);
            } else if (msg.startsWith("[DEAD]")) // 카드가 없어서 죽었을 경우 받는 메세지
            {
               la_GameInfo.setText("당신은 죽었습니다.");
               turnButton.setEnabled(false);
               bellButton.setEnabled(false);
            }

            else if (msg.startsWith("[UPDATEDEAD]")) // 죽은 유저의 라벨과 카드를 수정
            {
               ta_MsgView.append(msg.substring(12) + "님이 죽었습니다.\n");
               scPane.getVerticalScrollBar().setValue(scPane.getVerticalScrollBar().getMaximum());
               validate();
               board.UpdateDead(msg.substring(12));
            }

            else if (msg.startsWith("[SUCCESS]")) // 종치기 성공했을 경우
            {
               la_GameInfo.setText(msg.substring(9) + "님이 종치기 성공했습니다.");
               bellButton.setEnabled(true);
               board.CardInit();
            } else if (msg.startsWith("[FAIL]")) // 종치기에 실패했을 경우
            {
               la_GameInfo.setText(msg.substring(6) + "님이 종치기 실패했습니다.");
               bellButton.setEnabled(true);
               validate();
            } else if (msg.startsWith("[GAMEINIT]")) // 게임이 끝나서 초기화를 요청한 경우
            {
               board.CardInit();
               readyButton.setEnabled(true);
               readyButton.setText("준비");
               ready = false;
            } else if (msg.startsWith("[WIN]")) {
               la_GameInfo.setText("당신이 이겼습니다.");
               turnButton.setEnabled(false);
               bellButton.setEnabled(false);
            } else if (msg.startsWith("[BELL]")) {
               bellButton.setEnabled(false);
            } else // 그냥 메세지만 왔을경우 그냥 출력
            {
               ta_MsgView.append(msg + "\n");
               scPane.getVerticalScrollBar().setValue(scPane.getVerticalScrollBar().getMaximum());
               validate();
            }
         }
      } catch (IOException ie) {
         ta_MsgView.append(ie + "\n");
         scPane.getVerticalScrollBar().setValue(scPane.getVerticalScrollBar().getMaximum());
         validate();
      }
      ta_MsgView.append("접속이 끊겼습니다.\n");
      scPane.getVerticalScrollBar().setValue(scPane.getVerticalScrollBar().getMaximum());
      validate();
   }

   private void playersInfo() // 플레이어의 현재 인원수정보 출력
   {
      int count = model.getSize();
      la_PlayerInfo.setText("현재 " + count + "명접속");
   }

   private void nameList(String msg) // 서버에서 보낸 플레이어 리스트를 분류해서 리스트에 저장.
   {
      model.removeAllElements();
      StringTokenizer st = new StringTokenizer(msg, "\t");
      while (st.hasMoreElements()) {
         model.addElement(st.nextToken());
      }
      playersInfo();
   }

   private void connect() // 서버에 연결
   {
      try {
         String ip = tf_ipaddress.getText();
         ta_MsgView.append("서버에 연결을 요청합니다.\n");
         socket = new Socket(ip, 7777);
         ta_MsgView.append("--연결 성공--\n");
         reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
         writer = new PrintWriter(socket.getOutputStream(), true);
         new Thread(this).start();
         board.setWriter(writer);
      } catch (Exception e) {
         ta_MsgView.append(e + "\n\n연결 실패..\n");
      }
   }

   public static void main(String[] args) {
      HalliGalli_Client client = new HalliGalli_Client();
      client.setSize(800, 600);
      client.setVisible(true);
      client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
   }
}