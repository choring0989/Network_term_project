/*���� �ڵ� �ּ��� �ϴ� ���� �� ����.
 * 
 * ������ �� ��
 * 
 * 1. ���� �ڵ� �����ϱ�, �Լ��� ���ֽø� ������� �� ����.
 * 2. ��! ���� �̸��� ��� set�� Ŭ���̾�Ʈ���� �����־����.
 *	   �׷��߸� ��ǥ�Ҷ� �гο��� �ĺ��� �� �� �ְ��� �� ����.
 * 3. Ŭ���̾�Ʈ���� ��ǥ���� ���� �ĺ��� �Ѱܹ�����, kill�� ����� ã��, ���� ��ų�� �ƿ� ��ų�� �ڵ� ¥��ߵ�.
 * 4. ��ǥ�� ���� �����Ұ��� 5�� Ÿ�̸Ӹ� �� ���ε�, Ÿ�̸Ӹ� ���� �������� �־���ߵ�.
 * 	  Ÿ�̸Ӹ� ¥���� �Լ��� ����꿡 TimeTestFunc��� ���ε��صξ���.	
 * */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;

/**********************************************************************************************************
* class testServer
* 
* Wait until the 7 players gather.
* After gathering, assign a job to all players.
* Alert the starting of game.
* After 5 minutes from starting, tell players(clients) to vote to kill.
*
* @ PORT[int]						: port number.
* @ names[String//HashSet]			: hashSet about name of players.
* @ writers[PrintWriter//HashSet]	: hashset about writer????????????????????? � ����� �ϴ� ��������....
* @ info[str,pw//HashMap]			: hashmap about set including name and writer.
* @ max_client[int]					: the maximum number of player, 7.
* @ client_count[int]				: number for counting players.
* @ vote[int]						: array for storing number after voting.
* @ user[string]					: array of user.
* @ ID[printWriter]					: array of user's ID.
* @ job[string]						: array of job for players.
* @ random[int]						: for allocating jobs to players, initial the information in array.
**********************************************************************************************************/


public class testServer{

   private static final int PORT = 9001;
   private static HashSet<String> names = new HashSet<String>();
   private static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();
   private static HashMap<String, PrintWriter> info = new HashMap<String, PrintWriter>();
   private static int max_client = 7; 
   private static int client_count = 0;
   private static int[] vote = new int[max_client];
   private static String[] user = new String[max_client]; 
   private static PrintWriter[] ID = new PrintWriter[max_client];
   private static String[] job = { "mafia i'm mafia", "citizen i'm citizen", "citizen i'm citizen",
         "citizen i'm citizen", "citizen i'm citizen", "doctor i'm doctor", "police i'm police" }; 

   private static int[] random = { -1, -1, -1, -1, -1, -1, -1 };

   /*initialize the informations of vote, user and ID*/
   private static void initialize(int[] vote, String[] user, PrintWriter[] ID) {
      for (int i = 0; i < max_client; i++) {
         vote[i] = 0;
         user[i] = "null";
         ID[i] = null;
      }
   }

   /*assign a job to players randomly*/
   private static void randomArray(int[] random) {
      int index = 0;
      while (true) {
         int value = (int) (Math.random() * 7);
         int cnt = 0;
         for (int i = 0; i < random.length; i++) {
            if (value == random[i])
               cnt++;
         }
         if (cnt == 0) {
            random[index] = value;
            index++;
         }
         if (index == random.length)
            break;
      }
   }

   /*assign a job and intialize information. Then, go to handler.*/
   public static void main(String[] args) throws Exception {
	   
      System.out.println("The chat server is running.");
      ServerSocket listener = new ServerSocket(PORT); //error?????????????????*
      
      randomArray(random); //assign a job
      initialize(vote, user, ID); //intialize informations of each players
      
      try { 
         while (true) {
            new Handler(listener.accept()).start();
         }
      } finally {
         listener.close();
      }
   }

   /*************************************************************
    * class Handler
    * 
    * this is for socket programming between server and clients.
    * 
    * @ name[String]		: name
    * @ socket[socket]		: socket
    * @ in[BufferedReader]	: BufferedReader
    * @ out[PrintWriter]	: printwriter
    * *************************************************************/
   private static class Handler extends Thread {

      private String name;
      private Socket socket;
      private BufferedReader in;
      private PrintWriter out;

      public Handler(Socket socket) {
         this.socket = socket;
      }

      /*run for socket programming*/
      public void run() {
         try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream())); //read from user
            out = new PrintWriter(socket.getOutputStream(), true); //print out to console

            while (true) {//read a name of player
               out.println("SUBMITNAME"); 
               name = in.readLine();
               if (name == null) {
                  return;
               }
               synchronized (names) {
            	   //if any user enters, alert to every players.
                  sendToallclient("CONNECT " + name + " is connected.\n"); 
                  
                  if (!names.contains(name)) { //there must be no duplicate in name.
                     names.add(name);
                     
                     for (PrintWriter writer : writers) { //if any user enters, memo in server's console. �³�..???
                        writer.println("MESSAGE " + "[" + name + "] enter");
                     }
                     break;
                  }
               }
            }
            out.println("NAMEACCEPTED"); //if user enters own name, give check-message. ???
            writers.add(out); // ???
            user[client_count] = name; //store a name in array.
            ID[client_count] = out; //store an ID in array.
            
            client_count++; //for next users, do count++.
            
            System.out.println(user[client_count - 1] + "���� �����ϼ̽��ϴ�.");
            System.out.println("���� �ο� " + client_count + "��");
            
            info.put(name, out);

            if (client_count == max_client) {
            	//if all player gathers, start the game.
               for (PrintWriter writer : writers) {
                  writer.println("MESSAGE " + "game start");
               }
               
               //assign a job to player. ..? �´°ǰ�..??
               for (int i = 0; i < max_client; i++) {
                  String temp = job[i];
                  job[i] = job[random[i]];
                  job[random[i]] = temp;
               }
               
               //tell a job to each players.? �� ������ �³�..?--������������ ������ ���Ͽ� ��� Ŭ���̾�Ʈ���� �ָ� �˾�â���� �˷��ִ� �� ���� �� ����... 
               for (int i = 0; i < max_client; i++)
                  System.out.println(job[i]);
            }

            while (true) {
            	//���⼭���� � �������� �� �𸣰���...
               String input = in.readLine();

               if (input == null) { //if input is null,
                  return;
               } 
               
               //whisper..?????? �ʿ��� ����..???
               else if (input.startsWith("<") && input.indexOf("/>") != -1) {
                  String whisper;
                  whisper = input.substring(1, input.indexOf("/>"));
                  
                 if (names.contains(whisper)) {
                     PrintWriter sender = info.get(name);
                     PrintWriter receiver = info.get(whisper);
                     receiver.println("MESSAGE " + "<whisper from " + name + "> : "
                           + input.substring(whisper.length() + 3));
                     sender.println("MESSAGE " + "<whisper to " + whisper + "> : "
                           + input.substring(whisper.length() + 3));
                  } else {
                     PrintWriter sender = info.get(name);
                     sender.println("MESSAGE " + "This user does not exist.");
                  }

               } 
               //tell a job --��ɾ ��߸� ������ �˷��ִ�..??�� ���ٴ� �Ϲ������� �˷��ִ°� ���� �� ���ƿ�~
               //������������ ������ ���Ͽ� ��� Ŭ���̾�Ʈ���� �ָ� �˾�â���� �˷��ִ� �� ���� �� ����... !
               else if (input.startsWith("/") && input.indexOf("job") != -1) {
                  int temp_index = 0;
                  
                  for (int i = 0; i < client_count; i++) {
                     if (name == user[i])
                        temp_index = i;
                  }
                  
                  PrintWriter sender = info.get(name);
                  sender.println("MESSAGE " + "your job is "+ job[temp_index].substring(0, job[temp_index].indexOf(" ")));
               }
               //job�� role�� ����???
               else if (input.startsWith("/") && input.indexOf("role") != -1) {
                  int temp_index = 0;
                  for (int i = 0; i < client_count; i++) {
                     if (name == user[i])
                        temp_index = i;
                  }
                  PrintWriter sender = info.get(name);
                  sender.println(
                        "MESSAGE " + "your role is " + job[temp_index].substring(job[temp_index].indexOf(" ")));
               }
               //��ǥ�� 5��Ÿ�̸� ���Ŀ� �г�-�˾�â�� ����� �ٷ� �ϰ� ����°� ���� �� ���ƿ�.
               //��ɾ �Է��ϰ� ��ǥ�ϴ� �����, Ŭ���̾�Ʈ���� ���� �ٸ� �ñ⿡ ������ �� ���Ƽ���~
               else if (input.startsWith("/") && input.indexOf("vote") != -1) {
                  int temp_index = 0;
                  for (int i = 0; i < client_count; i++) {
                     if (name == user[i])
                        temp_index = i;
                  }
               } 
               //�̸�??? ���ϴ� �κ����� �� �𸣰ھ��...
               else if (input.startsWith("/") && input.indexOf("name") != -1) {
                  String temp = null;
                  for (int i = 0; i < user.length; i++) {
                     if (i == 0) {
                        temp = user[i];
                     } else {
                        temp.concat("," + user[i]);
                     }
                  }
                  System.out.println(temp);
                  for (PrintWriter writer : writers) {
                     writer.println("VOTENAME " + temp);
                  }
               }
               //���ϴ� �κ����� �� �𸣰ھ��..�Ф�
               else {
                  for (PrintWriter writer : writers) {
                     writer.println("MESSAGE " + name + ": " + input);
                  }
               }
            }
         } catch (IOException e) { //���ڱ� catch�� ���� ����..?
            System.out.println(e);
         } finally { //if client is out, alert.
            if (name != null) {
               for (PrintWriter writer : writers) {
                  writer.println("MESSAGE " + "[" + name + "] exit");
               }
               names.remove(name);
               info.remove(name);
               client_count--;
               System.out.println("�Ѹ� ������ " + client_count);
            }
            if (out != null) {
               writers.remove(out);
            }
            try {
               socket.close();
            } catch (IOException e) {
            }
         }
      }
   }

   //����� Ŭ���̾�Ʈ���� �˷��ִ� ������ �� �𸣰ھ��~
   public static void sendToallclient(String mssg) {
      for (PrintWriter writer : writers) {
         writer.println(mssg);
         writer.flush();
      }
   }

}