package com.sist.client;
/////////////////////////////////////////////////////

// ������ : ����õ(mudchobo@nate.com)
// ��α� : http://mudchobo.tomeii.com/
// ���α׷� : �Ҹ����� Ŭ���̾�Ʈ ���α׷�
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

      for (int i = 0; i < 5; i++) // 1��¥�� ī�� �̹�������
      {
         iiCard[14 * 0 + i] = new ImageIcon("image/banana_1.jpg");
         iiCard[14 * 1 + i] = new ImageIcon("image/lemon_1.jpg");
         iiCard[14 * 2 + i] = new ImageIcon("image/peach_1.jpg");
         iiCard[14 * 3 + i] = new ImageIcon("image/straw_1.jpg");
      }
      for (int i = 0; i < 3; i++) // 2,3��¥�� ī�� �̹�������
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
      for (int i = 0; i < 2; i++) // 4��¥�� ī�� �̹�������
      {
         iiCard[14 * 0 + i + 11] = new ImageIcon("image/banana_4.jpg");
         iiCard[14 * 1 + i + 11] = new ImageIcon("image/lemon_4.jpg");
         iiCard[14 * 2 + i + 11] = new ImageIcon("image/peach_4.jpg");
         iiCard[14 * 3 + i + 11] = new ImageIcon("image/straw_4.jpg");
      }
      // 5��¥�� ī�� �̹�������
      iiCard[14 * 0 + 13] = new ImageIcon("image/banana_5.jpg");
      iiCard[14 * 1 + 13] = new ImageIcon("image/lemon_5.jpg");
      iiCard[14 * 2 + 13] = new ImageIcon("image/peach_5.jpg");
      iiCard[14 * 3 + 13] = new ImageIcon("image/straw_5.jpg");

      iiCardBack = new ImageIcon("image/CardBack.jpg"); // ī�� �޸�
      iiBell = new ImageIcon("image/Bell.gif"); // ��
      for (int i = 0; i < 4; i++) {
         iiPlayerCard[i] = iiCardBack;
      }
      laPlayer[0] = new JLabel("Player1");
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
      laPlayer[2] = new JLabel("Player3");
      laPlayer[2].setBounds(1, 300, 50, 15);
      add(laPlayer[2]);
      laCardNum[2] = new JLabel("0��");
      laCardNum[2].setBounds(50, 300, 70, 15);
      add(laCardNum[2]);
      laPlayer[3] = new JLabel("Player4");
      laPlayer[3].setBounds(250, 300, 50, 15);
      add(laPlayer[3]);
      laCardNum[3] = new JLabel("0��");
      laCardNum[3].setBounds(300, 300, 70, 15);
      add(laCardNum[3]);
   }

   protected void paintComponent(Graphics g) // �׸���
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

   public void UpdateDraw(String name, int CardNum) // �׸���
   {
      for (int i = 0; i < 4; i++) {
         if (name.equals(userName[i])) {
            iiPlayerCard[i] = iiCard[CardNum];
            repaint();
         }
      }
   }

   public void UpdateCardNum(String name, int Count) // ī�带 ������� ������Ʈ
   {
      for (int i = 0; i < 4; i++) {
         if (name.equals(userName[i])) {
            laCardNum[i].setText(Count + "��");
         }
      }
   }

   public void UpdateDead(String name) // ���� �÷��̾����� ������Ʈ
   {
      for (int i = 0; i < 4; i++) {
         if (name.equals(userName[i])) {
            iiPlayerCard[i] = iiCardBack;
            laCardNum[i].setText("GameOver");
            repaint();
         }
      }
   }

   public void CardInit() // ī�带 �ʱ�ȭ
   {
      for (int i = 0; i < 4; i++) {
         iiPlayerCard[i] = iiCardBack;
      }
      repaint();
   }
}

public class HalliGalli_Client extends JFrame implements Runnable, ActionListener {
   JTextArea ta_MsgView; // �޽����� �����ִ� �ؽ�Ʈ����
   JScrollPane scPane; // ��ũ��
   JTextField tf_Send = new JTextField(""); // ���� �޽����� �����ʵ�
   JTextField tf_Name = new JTextField(); // ����� �̸� ����
   JTextField tf_ipaddress = new JTextField(); // ������ ������ ip�ּ��Է��ʵ�
   DefaultListModel model = new DefaultListModel(); // ����Ʈ��
   JList li_Player = new JList(model); // ����� ����Ʈ
   JButton connectButton = new JButton("����"); // ���� ��ư
   JButton readyButton = new JButton("�غ�"); // �����ư
   JButton turnButton = new JButton("ī�������"); // ī�� ������ ��ư
   JButton bellButton = new JButton("��ġ��"); // ��ġ�� ��ư
   JLabel la_GameInfo = new JLabel("<����â>"); // ����â
   JLabel la_PlayerInfo = new JLabel("<�ο�����>"); // �ο�����
   boolean ready = false;

   HalliGalli_Board board = new HalliGalli_Board();
   BufferedReader reader; // �Է½�Ʈ��
   PrintWriter writer; // ��½�Ʈ��
   Socket socket; // ����
   String userName = null; // ����� �̸�

   public HalliGalli_Client() {
      super("�Ҹ����� �¶��ΰ���");
      Container ct = getContentPane();
      ct.setLayout(null);

      ta_MsgView = new JTextArea(1, 1);
      scPane = new JScrollPane(ta_MsgView);

      // ���� ������Ʈ�� �����ϰ� ��ġ�Ѵ�.
      EtchedBorder eborder = new EtchedBorder(EtchedBorder.RAISED);
      ta_MsgView.setEditable(false);
      la_GameInfo.setBounds(10, 30, 480, 30);
      ct.add(la_GameInfo);
      board.setBounds(10, 70, 480, 480);
      board.setBorder(eborder);
      ct.add(board);

      JPanel p1 = new JPanel();
      p1.setLayout(new GridLayout(4, 4));
      p1.add(new Label("�����ּ� : ", 2));
      p1.add(tf_ipaddress);
      p1.add(new Label("�̸� : ", 2));
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

      ta_MsgView.append("�̸��� �Է��ϰ� ���ӹ�ư�� ��������\n");
   }

   public void actionPerformed(ActionEvent ae) {
      try {
         if (ae.getSource() == tf_Send) // �������ؽ�Ʈ�ʵ忡 ���� �Է����� ���
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
         } else if (ae.getSource() == connectButton) // ���ӹ�ư�� ������ ���
         {
            String name = tf_Name.getText().trim();
            if (name.length() <= 2 || name.length() > 10) {
               la_GameInfo.setText("�̸��� �߸��Ǿ����ϴ�.");
               tf_Name.requestFocus();
               return;
            }
            connect();
            userName = name;
            tf_Name.setText(userName);
            tf_Name.setEditable(false);
            tf_ipaddress.setEditable(false);
            la_GameInfo.setText("���Ӽ���");
            writer.println("[CONNECT]" + userName);
            connectButton.setEnabled(false);
            readyButton.setEnabled(true);
         } else if (ae.getSource() == readyButton) // ����������ư�� ������ ���
         {

            if (!ready) {
               ready = true;
               writer.println("[READY]");
               readyButton.setText("�غ�����");
               la_GameInfo.setText("�غ�Ϸ�");
            } else {
               ready = false;
               writer.println("[NOREADY]");
               readyButton.setText("�غ�");
               la_GameInfo.setText("�غ�����");
            }
         } else if (ae.getSource() == turnButton) // ī��������ư�� ������ ���
         {
            la_GameInfo.setText("����� ī�带 �������ϴ�.");
            writer.println("[TURN]" + userName);
         } else if (ae.getSource() == bellButton) // ��ġ���ư�� ������ ���
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
            if (msg.startsWith("[FULL]")) // ������ �����ο��� ��á�� ���
            {
               la_GameInfo.setText("�濡 �ο��� ��á���ϴ�.");
            }

            else if (msg.startsWith("[PLAYERS]")) // �÷��̾��Ʈ�� �޴´�.
            {
               nameList(msg.substring(9));
            }

            else if (msg.startsWith("[ENTER]")) // ���� ������ ���
            {
               model.addElement(msg.substring(7));
               playersInfo();
               ta_MsgView.append("[" + msg.substring(7) + "]���� �����Ͽ����ϴ�.\n");
               scPane.getVerticalScrollBar().setValue(scPane.getVerticalScrollBar().getMaximum());
               validate();
            }

            else if (msg.startsWith("[DISCONNECT]")) // ������ ���������
            {
               model.removeElement(msg.substring(6));
               playersInfo();
               ta_MsgView.append("[" + msg.substring(12) + "]���� �������ϴ�.\n");
               scPane.getVerticalScrollBar().setValue(scPane.getVerticalScrollBar().getMaximum());
               validate();
            }

            else if (msg.startsWith("������ �����մϴ�.")) // �������� ������ ���۵� ���
            {
               board.setLabel(model);
               turnButton.setEnabled(true);
               bellButton.setEnabled(true);
               readyButton.setEnabled(false);
            }

            else if (msg.startsWith("[REPAINT]")) // �������� ī������� �� �ٽ� �׸��� ��û
            {
               int a = msg.indexOf("|");
               int b = Integer.parseInt(msg.substring(a + 1));
               board.UpdateDraw(msg.substring(9, a), b);
            }

            else if (msg.startsWith("[CARDNUM]")) // ���� ī����� �޴´�.
            {
               int a = msg.indexOf("|");
               int b = Integer.parseInt(msg.substring(a + 1));
               board.UpdateCardNum(msg.substring(9, a), b);
            } else if (msg.startsWith("[DEAD]")) // ī�尡 ��� �׾��� ��� �޴� �޼���
            {
               la_GameInfo.setText("����� �׾����ϴ�.");
               turnButton.setEnabled(false);
               bellButton.setEnabled(false);
            }

            else if (msg.startsWith("[UPDATEDEAD]")) // ���� ������ �󺧰� ī�带 ����
            {
               ta_MsgView.append(msg.substring(12) + "���� �׾����ϴ�.\n");
               scPane.getVerticalScrollBar().setValue(scPane.getVerticalScrollBar().getMaximum());
               validate();
               board.UpdateDead(msg.substring(12));
            }

            else if (msg.startsWith("[SUCCESS]")) // ��ġ�� �������� ���
            {
               la_GameInfo.setText(msg.substring(9) + "���� ��ġ�� �����߽��ϴ�.");
               bellButton.setEnabled(true);
               board.CardInit();
            } else if (msg.startsWith("[FAIL]")) // ��ġ�⿡ �������� ���
            {
               la_GameInfo.setText(msg.substring(6) + "���� ��ġ�� �����߽��ϴ�.");
               bellButton.setEnabled(true);
               validate();
            } else if (msg.startsWith("[GAMEINIT]")) // ������ ������ �ʱ�ȭ�� ��û�� ���
            {
               board.CardInit();
               readyButton.setEnabled(true);
               readyButton.setText("�غ�");
               ready = false;
            } else if (msg.startsWith("[WIN]")) {
               la_GameInfo.setText("����� �̰���ϴ�.");
               turnButton.setEnabled(false);
               bellButton.setEnabled(false);
            } else if (msg.startsWith("[BELL]")) {
               bellButton.setEnabled(false);
            } else // �׳� �޼����� ������� �׳� ���
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
      ta_MsgView.append("������ ������ϴ�.\n");
      scPane.getVerticalScrollBar().setValue(scPane.getVerticalScrollBar().getMaximum());
      validate();
   }

   private void playersInfo() // �÷��̾��� ���� �ο������� ���
   {
      int count = model.getSize();
      la_PlayerInfo.setText("���� " + count + "������");
   }

   private void nameList(String msg) // �������� ���� �÷��̾� ����Ʈ�� �з��ؼ� ����Ʈ�� ����.
   {
      model.removeAllElements();
      StringTokenizer st = new StringTokenizer(msg, "\t");
      while (st.hasMoreElements()) {
         model.addElement(st.nextToken());
      }
      playersInfo();
   }

   private void connect() // ������ ����
   {
      try {
         String ip = tf_ipaddress.getText();
         ta_MsgView.append("������ ������ ��û�մϴ�.\n");
         socket = new Socket(ip, 7777);
         ta_MsgView.append("--���� ����--\n");
         reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
         writer = new PrintWriter(socket.getOutputStream(), true);
         new Thread(this).start();
         board.setWriter(writer);
      } catch (Exception e) {
         ta_MsgView.append(e + "\n\n���� ����..\n");
      }
   }

   public static void main(String[] args) {
      HalliGalli_Client client = new HalliGalli_Client();
      client.setSize(800, 600);
      client.setVisible(true);
      client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
   }
}