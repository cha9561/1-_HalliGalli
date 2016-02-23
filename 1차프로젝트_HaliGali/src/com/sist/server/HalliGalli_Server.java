package com.sist.server;
/////////////////////////////////////////////////////

// ������ : ����õ(mudchobo@nate.com)
// ��α� : http://mudchobo.tomeii.com/
// ���α׷� : �Ҹ����� ���� ���α׷�
/////////////////////////////////////////////////////

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.Vector;

public class HalliGalli_Server {
   /// Field
   ServerSocket server;
   Random rnd = new Random();
   int Card[] = new int[56]; // ī�� �������� ����
   int TurnCard[][] = new int[4][]; // �������� ī�带 �����ϴ� ����
   int TurnCardCount[] = new int[4]; // �������� ī���� ����
   int CardType[] = new int[4]; // ī���� ������ �˱����� ����
   int CardNum[] = new int[4]; // ī��� ���� ���� �˱����� ����
   int ClientCard[][] = new int[4][]; // Ŭ���̾�Ʈ ī�� ����
   int ClientCardCount[] = new int[4]; // Ŭ���̾�Ʈ ī�� ���� ����
   int NowPlayer; // ���� ���ʰ� �������� ����
   boolean isSuccess = false; // ��ġ�⿡ �����ߴ��� Ȯ��
   boolean dead[] = new boolean[4]; // �׾����� ��Ҵ��� Ȯ��
   boolean EndGame = false; // ������ ������ Ȯ��.
   boolean isBell = false; // ������ ���� �ƴ��� Ȯ��
   String Player[] = new String[4]; // �÷��̾��̸� ���Ӽ������ ����
   BManager bMan = new BManager(); // Ŭ���̾�Ʈ���� ������ִ� ��ü

   /// Constructor
   public HalliGalli_Server() {
   }

   /// Method
   public void startServer() {
      try {
         // server = new ServerSocket(7777, 4);
         InetAddress addr = InetAddress.getByName("211.238.142.88");
         server = new ServerSocket(7777, 4, addr);
         System.out.println("���������� �����Ǿ����ϴ�.");
         while (true) {
            // Ŭ���̾�Ʈ�� ����� �����带 ��´�.
            Socket socket = server.accept();

            // �����带 ����� �����Ų��.
            HalliGalli_Thread ht = new HalliGalli_Thread(socket);
            ht.start();

            // bMan�� �����带 �߰��Ѵ�.
            bMan.add(ht);

            System.out.println("������ ��: " + bMan.size());
         }
      } catch (Exception e) {
         System.out.println(e);
      }
   }

   public void GameInit() // ���� �ʱ�ȭ
   {
      for (int i = 0; i < 4; i++) {
         dead[i] = false;
         TurnCard[i] = new int[56];
         TurnCardCount[i] = 0;
         ClientCard[i] = new int[56];
         ClientCardCount[i] = 0;
      }

      for (int i = 0; i < 56; i++) // ī���ȣ ����
      {
         Card[i] = i;
      }

      for (int i = 55; i > 0; i--) // ī�� ����
      {
         int temp;
         int j = rnd.nextInt(56);
         temp = Card[i];
         Card[i] = Card[j];
         Card[j] = temp;
      }
   }

   public void DivideCard() // ī�带 Ŭ���̾�Ʈ���� ������
   {
      for (int i = 0; i < 4; i++) {
         for (int j = 0; j < 14; j++) {
            ClientCard[i][j] = Card[i * 14 + j];
            ClientCardCount[i]++;
         }
      }
   }

   public void UpdateCardNum() // Ŭ���̾�Ʈ�鿡�� ī�������� ������Ʈ���� �˸�.
   {
      for (int i = 0; i < 4; i++) {
         if (!dead[i]) {
            bMan.sendToAll("[CARDNUM]" + Player[i] + "|" + ClientCardCount[i]);
         }
      }
   }

   public void NextPlayer() {
      NowPlayer++;
      if (NowPlayer == 4) {
         NowPlayer = 0;
      }

      while (dead[NowPlayer]) {
         NowPlayer++;
         if (NowPlayer == 4) {
            NowPlayer = 0;
         }
      }
   }

   public void SuccessBell() {
      for (int i = 0; i < 4; i++) {
         if (!dead[i]) {
            bMan.sendTo(i, "[SUCCESS]" + Player[i]);
         }
      }
   }

   public void FailBell() {
      for (int i = 0; i < 4; i++) {
         if (!dead[i]) {
            bMan.sendTo(i, "[FAIL]" + Player[i]);
         }
      }
   }

   public int isEndGame() // ������ �������� �˻�
   {
      int count = 0;
      for (int i = 0; i < 4; i++) {
         if (dead[i]) {
            count++;
         }
      }
      if (count == 3) {
         for (int i = 0; i < 4; i++) {
            if (!dead[i]) {
               return i;
            }
         }
      }
      return -1;
   }

   public static void main(String[] args) {
      HalliGalli_Server server = new HalliGalli_Server();
      server.startServer();
   }

   class HalliGalli_Thread extends Thread {
      /// Field
      String userName = null; // �̸�
      Socket socket; // ��������
      boolean ready = false; // �غ񿩺�
      BufferedReader reader; // �ޱ�
      PrintWriter writer; // ������

      /// Constructor
      HalliGalli_Thread(Socket socket) {
         this.socket = socket;
      }

      Socket getSocket() {
         return socket;
      }

      String getUserName() {
         return userName;
      }

      boolean isReady() {
         return ready;
      }

      public void run() {
         try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            String msg;

            while ((msg = reader.readLine()) != null) {
               if (msg.startsWith("[CONNECT]")) {
                  if (bMan.isFull()) {
                     bMan.remove(this);
                     if (reader != null) {
                        reader.close();
                     }
                     if (writer != null) {
                        writer.close();
                     }
                     if (socket != null) {
                        socket.close();
                     }
                     reader = null;
                     writer = null;
                     socket = null;
                     System.out.println(userName + "���� ������ �������ϴ�.");
                     System.out.println("������ ��: " + bMan.size());
                     bMan.sendToAll("[DISCONNECT]" + userName);
                  } else { // ����� ������ �Ȱ��
                     userName = msg.substring(9);
                     bMan.sendToOthers(this, "[ENTER]" + userName);
                     bMan.sendToAll(bMan.getNames());
                  }
               }

               else if (msg.startsWith("[READY]")) // Ŭ���̾�Ʈ���� ������ ���
               {
                  ready = true;
                  bMan.sendToAll(userName + "�� �غ�Ϸ�!");
                  if (bMan.isReady()) {
                     bMan.sendToAll("������ �����մϴ�.");
                     GameInit(); // �����ʱ�ȭ
                     for (int i = 0; i < bMan.size(); i++) // �÷��̾� �̸��� ����
                     {
                        Player[i] = bMan.getHT(i).getUserName();
                     }
                     NowPlayer = 0; // ���ӽ����� 0������
                     bMan.sendToAll(Player[NowPlayer] + "�� �����Դϴ�.");
                     DivideCard();
                     UpdateCardNum();
                  }
               }

               else if (msg.startsWith("[NOREADY]")) // Ŭ���̾�Ʈ���� ���� �ٽ� ��������
                                             // ���
               {
                  ready = false;
                  bMan.sendToAll(userName + "���� ���� �����߽��ϴ�.");
               } else if (msg.startsWith("[TURN]")) // Ŭ���̾�Ʈ���� ī������⸦ ���� ���
               {
                  int a = msg.indexOf("|");
                  if (Player[NowPlayer].equals(msg.substring(6))) // �ڱ����ʿ��ΰ˻�
                  {
                     TurnCard[NowPlayer][TurnCardCount[NowPlayer]++] = ClientCard[NowPlayer][--ClientCardCount[NowPlayer]];
                     if (ClientCardCount[NowPlayer] == 0) { // Ŭ���̾�Ʈ�� ī�尡
                                                   // ���嵵 ���� ����
                                                   // ��� ��������
                                                   // ó��
                        dead[NowPlayer] = true;
                        bMan.sendToAll("[UPDATEDEAD]" + Player[NowPlayer]);
                        writer.println("[DEAD]" + Player[NowPlayer]);
                        if (isEndGame() != 0) {
                           bMan.sendToAll(Player[isEndGame()] + "���� �̰���ϴ�.");
                           bMan.sendToAll("[GAMEINIT]");
                           bMan.sendTo(isEndGame(), "[WIN]");
                           bMan.unReady();
                        }
                        NextPlayer();
                     } else { // �׿ܴ� Ŭ���̾�Ʈ���� ī�� �ٽñ׸� ��û
                        bMan.sendToAll("[REPAINT]" + Player[NowPlayer] + "|"
                              + TurnCard[NowPlayer][TurnCardCount[NowPlayer] - 1]);
                        UpdateCardNum();
                        NextPlayer();
                        bMan.sendToAll(Player[NowPlayer] + "�� �����Դϴ�.");
                     }
                  } else { // �ڱ� ���ʰ� �ƴ� ��� ������ �޽���.
                     writer.println("������ʰ� �ƴմϴ�.");
                  }
               }

               else if (msg.startsWith("[MSG]")) // Ŭ���̾�Ʈ���� �޼����� �޾��� ��
               {
                  bMan.sendToAll("[" + userName + "]:" + msg.substring(5));
               }

               else if (msg.startsWith("[BELL]")) // Ŭ���̾�Ʈ���� ���� ����� ��
               {
                  if (isBell == true) {
                     writer.println("����� �ʾ����ϴ�.");
                  } else {
                     isBell = true;
                     bMan.sendToAll(userName + "���� ���� �ƽ��ϴ�!!!");
                     bMan.sendToAll("[BELL]");
                     Thread.sleep(1000);
                     isSuccess = false;
                     int CardSum = 0;
                     for (int i = 0; i < 4; i++) {
                        if (TurnCardCount[i] != 0) { // ������ �� ī���� ����������
                                                // ���� ���Ѵ�.
                           int temp = TurnCard[i][TurnCardCount[i] - 1];
                           CardType[i] = temp / 14;
                           CardNum[i] = temp % 14;
                        } else { // 4���� �� ���� ������츦 ����� 0���� �ʱ�ȭ.
                           CardType[i] = -1;
                           CardNum[i] = -1;
                        }
                     }
                     for (int i = 0; i < 4; i++) {
                        CardSum = 0;
                        for (int j = 0; j < 4; j++) {
                           if (CardType[i] == CardType[j]) { // ����������
                                                      // ���� �͸�
                                                      // ���Ѵ�.
                              if (CardNum[j] >= 0 && CardNum[j] <= 4) {
                                 CardSum += 1;
                              } else if (CardNum[j] >= 5 && CardNum[j] <= 7) {
                                 CardSum += 2;
                              } else if (CardNum[j] >= 8 && CardNum[j] <= 10) {
                                 CardSum += 3;
                              } else if (CardNum[j] >= 11 && CardNum[j] <= 12) {
                                 CardSum += 4;
                              } else if (CardNum[j] == 13) {
                                 CardSum += 5;
                              }
                           }
                        }
                        if (CardSum == 5) { // ��ġ�⼺���� �� ī�带 �� ��������.
                           SuccessBell();
                           isBell = false;
                           bMan.sendToAll(userName + "���� ��ġ�⿡ �����߽��ϴ�.");
                           int a = bMan.getNum(userName);
                           for (i = 0; i < 4; i++) {
                              for (int j = 0; j < TurnCardCount[i]; j++) {
                                 ClientCard[a][ClientCardCount[a]++] = TurnCard[i][j];
                              }
                              TurnCardCount[i] = 0;
                           }
                           for (int m = ClientCardCount[a]; m > 0; m--) // ī��
                                                               // ����
                           {
                              int temp;
                              int n = rnd.nextInt(ClientCardCount[a]);
                              temp = ClientCard[a][m];
                              ClientCard[a][m] = ClientCard[a][n];
                              ClientCard[a][n] = temp;
                           }
                           isSuccess = true;
                           UpdateCardNum();
                           break;
                        }
                     }
                     if (!isSuccess) { // ��ġ�� ���н� �ٸ��÷��̾�� ī�带 ���徿 ������.
                        FailBell();
                        isBell = false;
                        bMan.sendToAll(userName + "���� ��ġ�⿡ �����߽��ϴ�.");
                        for (int i = 0; i < 4; i++) {
                           if (!userName.equals(Player[i]) && !dead[i]) {
                              int a = bMan.getNum(userName);
                              ClientCard[i][ClientCardCount[i]++] = ClientCard[a][--ClientCardCount[a]];
                              if (ClientCardCount[a] == 0) {
                                 dead[a] = true;
                                 bMan.sendToAll("[UPDATEDEAD]" + userName);
                                 writer.println("[DEAD]" + userName);
                                 if (userName.equals(Player[NowPlayer])) {
                                    NextPlayer();
                                 }
                                 if (isEndGame() != -1) {
                                    bMan.sendToAll(Player[isEndGame()] + "���� �̰���ϴ�.");
                                    bMan.sendToAll("[GAMEINIT]");
                                    bMan.sendTo(isEndGame(), "[WIN]");
                                    bMan.unReady();
                                 }
                                 break;
                              }
                           }
                        }
                        UpdateCardNum();
                     }
                  }
               }
            }
         } catch (Exception e) {
         } finally {
            try {
               bMan.remove(this);
               if (reader != null) {
                  reader.close();
               }
               if (writer != null) {
                  writer.close();
               }
               if (socket != null) {
                  socket.close();
               }
               reader = null;
               writer = null;
               socket = null;
               System.out.println(userName + "���� ������ �������ϴ�.");
               System.out.println("������ ��: " + bMan.size());
               bMan.sendToAll("[DISCONNECT]" + userName);
            } catch (Exception e) {
            }
         }
      }
   }

   // Ŭ������ �����ϴ� Vector�� ��ӹ޾Ƽ� ��������� �����ϴ� Ŭ����
   class BManager extends Vector {
      public BManager() {
      }

      HalliGalli_Thread getHT(int i) // ���� ��ȣ�� �����带 ����.
      {
         return (HalliGalli_Thread) elementAt(i);
      }

      Socket getSocket(int i) // ������ �����´�.
      {
         return getHT(i).getSocket();
      }

      void sendTo(int i, String msg) // i�������忡 �޽����� ����.
      {
         try {
            PrintWriter pw = new PrintWriter(getSocket(i).getOutputStream(), true);
            pw.println(msg);
         } catch (Exception e) {
         }
      }

      synchronized boolean isFull() // ������ �� á���� Ȯ��
      {
         if (size() >= 5) {
            return true;
         }
         return false;
      }

      void sendToAll(String msg) // ��� �����忡�� ������ �޽���
      {
         for (int i = 0; i < size(); i++) {
            sendTo(i, msg);
         }
      }

      void sendToOthers(HalliGalli_Thread ht, String msg) // �ڱ⸦ ������ �������� ������
                                             // �޽���
      {
         for (int i = 0; i < size(); i++) {
            if (getHT(i) != ht) {
               sendTo(i, msg);
            }
         }
      }

      synchronized void unReady() // �غ�����
      {
         for (int i = 0; i < size(); i++) {
            getHT(i).ready = false;
         }
      }

      synchronized boolean isReady() // �����غ񿩺�Ȯ��
      {
         int count = 0;
         for (int i = 0; i < size(); i++) {
            if (getHT(i).isReady()) {
               count++;
            }
         }
         if (count == 4) {
            return true;
         }
         return false;
      }

      String getNames() // ���� ���ӵ� �������� �̸��� ������.
      {
         StringBuffer sb = new StringBuffer("[PLAYERS]");
         for (int i = 0; i < size(); i++) {
            sb.append(getHT(i).getUserName() + "\t");
         }
         return sb.toString();
      }

      int getNum(String name) // ���� ���ӵ� �������� ��ȣ�� ���ؼ� �̸��� ������.
      {
         for (int i = 0; i < size(); i++) {
            String getName = getHT(i).getUserName();
            if (getName.equals(name)) {
               return i;
            }
         }
         return 0;
      }
   }
}