package net_hw2;

/*���� �ڵ� �ּ��� �ϴ� ���� �� ����.
 * ������ �� ��
 * 1. ���� �ڵ� �����ϱ�, �Լ��α����ϸ������Ͱ��ƿ�
 * 2. ��! ���� �̸��� ��� set�� Ŭ���̾�Ʈ���� �����־����.
 *      �׷��߸� ��ǥ�Ҷ� �гο��� �ĺ��� �� �� �ְ��� �� ����.
 * 3. Ŭ���̾�Ʈ���� ��ǥ���� ���� �ĺ��� �Ѱܹ�����, kill�� ����� ã��, ���� ��ų�� �ƿ� ��ų�� �ڵ� ¥��ߵ�.
 * 4. ��ǥ�� ���� �����Ұ��� 5�� Ÿ�̸Ӹ� �� ���ε�, Ÿ�̸Ӹ� ���� �������� �־���ߵ�.
 *      Ÿ�̸Ӹ� ¥���� �Լ��� ����꿡 testYet_timer��� ���ε��صξ���.   
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
 * class CharServer
 * 
 * Wait until the 7 players gather. After gathering, assign a job to all
 * players. Alert the starting of game. After 5 minutes from starting, tell
 * players(clients) to vote to kill.
 *
 * @ PORT[int] : port number. @ names[String//HashSet] : hashSet about name of
 * players. @ writers[PrintWriter//HashSet] : hashset about
 * writer????????????????????? � ����� �ϴ� ��������.... -> ������ �ּҸ� �޴� HashSet(�ߺ� ���
 * x) @ info[str,pw//HashMap] : hashmap about set including name and writer. @
 * max_client[int] : the maximum number of player, 7. @ client_count[int] :
 * number for counting players. @ vote[int] : array for storing number after
 * voting. @ user[string] : array of user. @ ID[printWriter] : array of user's
 * ID. @ job[string] : array of job for players. @ random[int] : for allocating
 * jobs to players, initial the information in array.
 **********************************************************************************************************/

public class Chat_Server {
	private static final int PORT = 9001;
	private static HashSet<String> names = new HashSet<String>();
	private static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();
	private static HashMap<String, PrintWriter> info = new HashMap<String, PrintWriter>();
	private static int max_client = 7;
	private static int client_count = 0;
	private static int matrix_size = 7;
	private static int num_of_clue = 7;

	/**************************** modified *******************************/
	private static int timer_flag = 0;
	private static int clickedNum = 0;
	private static int current_client = max_client;
	private static int is_vote = 0;
	private static int mafia_index = 0;
	private static int police_index = 0;
	private static int doctor_index = 0;
	private static int victim_index = 0;
	private static int game_start_flag = 0;
	private static int[] selectedByMafia = new int[max_client];
	private static int[] kicked = new int[max_client];
	private static int[] vote = new int[max_client];
	private static int[][] matrix = new int[matrix_size][matrix_size];
	/**************************************************************/

	private static String[] user = new String[max_client];
	private static PrintWriter[] ID = new PrintWriter[max_client];
	private static String[] job = { "citizen I'm citizen", "doctor I'm doctor", "citizen I'm citizen",
			"mafia I'm mafia", "citizen I'm citizen", "police I'm police", "citizen I'm citizen" };

	private static int[] random = { -1, -1, -1, -1, -1, -1, -1 };

	private static void initializeMatrix(int[][] matrix) {
		for (int i = 0; i < mafia_index; i++) {
			for (int j = 0; j < matrix_size; j++)
				matrix[i][j] = 0;
		}
	}

	/* initialize the informations of vote, user and ID */
	private static void initialize(int[] vote, int[] kicked, String[] user, PrintWriter[] ID) {
		for (int i = 0; i < max_client; i++) {
			/**************************** modified *******************************/
			kicked[i] = 1;
			selectedByMafia[i] = 0;/**************************************************************/
			vote[i] = 0;
			user[i] = "null";
			ID[i] = null;
		}
	}

	/* assign a job to players randomly */
	private static void randomArray(int[] random, int[][] matrix) {
		int index = 0;
		int matrix_count = 0;
		while (true) {
			int client_value = (int) (Math.random() * max_client);
			int cnt = 0;
			for (int i = 0; i < max_client; i++) {
				if (client_value == random[i])
					cnt++;
			}
			if (cnt == 0) {
				random[index] = client_value;
				index++;
			}
			if (index == max_client)
				break;
		}

		while (true) {
			int matrix_value_column = (int) (Math.random() * matrix_size);
			int matrix_value_row = (int) (Math.random() * matrix_size);
			if (matrix[matrix_value_column][matrix_value_row] == 0) {
				matrix[matrix_value_column][matrix_value_row] = 1;
				matrix_count++;
			}

			if (matrix_count == matrix_size)
				break;
		}
	}

	public static void storeIndex() {
		for (int i = 0; i < max_client; i++) {
			if ((job[i].substring(0, job[i].indexOf(" "))).equals("mafia"))
				mafia_index = i;
			else if ((job[i].substring(0, job[i].indexOf(" "))).equals("police"))
				police_index = i;
			else if ((job[i].substring(0, job[i].indexOf(" "))).equals("doctor"))
				doctor_index = i;
		}
	}

	/* assign a job and intialize information. Then, go to handler. */
	public static void main(String[] args) throws Exception {
		System.out.println("The chat server is running.");
		ServerSocket listener = new ServerSocket(PORT); // error?????????????????

		randomArray(random, matrix); // assign a job
		initialize(vote, kicked, user, ID); // intialize informations of each players
		initializeMatrix(matrix);
		for (int i = 0; i < matrix_size; i++) {
			for (int j = 0; j < matrix_size; j++)
				System.out.print(matrix[i][j] + " ");
			System.out.println();
		}
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
	 * @ name[String] : name @ socket[socket] : socket @ in[BufferedReader] :
	 * BufferedReader @ out[PrintWriter] : printwriter
	 *************************************************************/
	private static class Handler extends Thread {

		private String name;
		private Socket socket;
		private BufferedReader in;
		private PrintWriter out;

		public Handler(Socket socket) {
			this.socket = socket;
		}

		/* run for socket programming */
		public void run() {
			try {
				in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // read from user
				out = new PrintWriter(socket.getOutputStream(), true); // print out to console

				while (true) {// read a name of player
					out.println("SUBMITNAME");
					name = in.readLine();
					if (name == null) {
						return;
					}
					synchronized (names) {
						// if any user enters, alert to every players.
						sendToallclient("CONNECT " + name + " is connected.\n");

						if (!names.contains(name)) { // there must be no duplicate in name.
							names.add(name);

							for (PrintWriter writer : writers) { // if any user enters, memo in server's console.
								// �³�..??? -> ���ο� ������ ���� ��� ä��â���� �����鿡�� [name]
								// enter��� �˷���
								writer.println("MESSAGE " + "\"" + name + "\"���� ������ �����߽��ϴ�.");
							}
							break;
						}
						else {
							System.out.println("�ߺ�");
							for (PrintWriter writer : writers) writer.println("ERROR " + "�̹� ������� �г����Դϴ�.");
						}
					}
				}
				out.println("NAMEACCEPTED"); // if user enters own name, give check-message. ??? ->Ŭ���̾�Ʈ����
				// NAMEACCPETED��� �������ݸ޼����� ������ ->������ ä��â Ȱ��ȭ ������
				writers.add(out); // ??? -> ������ �ּҸ� hashset�� ����
				user[client_count] = name; // store a name in array.
				ID[client_count] = out; // store an ID in array.

				client_count++; // for next users, do count++.

				System.out.println(user[client_count - 1] + "���� �����ϼ̽��ϴ�.");
				System.out.println("���� �ο� " + client_count + "��");

				info.put(name, out);

				if (client_count == max_client) {
					game_start_flag = 1;
					// if all player gathers, start the game.
					for (PrintWriter writer : writers) {
						writer.println("MESSAGE " + "game start");
					}

					// assign a job to player. ..? �´°ǰ�..??
					for (int i = 0; i < max_client; i++) {
						String temp = job[i];
						job[i] = job[random[i]];
						job[random[i]] = temp;
					}

					for (int i = 0; i < max_client; i++) {
						if ((job[i].substring(0, job[i].indexOf(" "))).equals("mafia"))
							mafia_index = i;
						else if ((job[i].substring(0, job[i].indexOf(" "))).equals("police"))
							police_index = i;
						else if ((job[i].substring(0, job[i].indexOf(" "))).equals("doctor"))
							doctor_index = i;
					}
					// tell a job to each players.? �� ������ �³�..?--������������ ������ ���Ͽ� ��� Ŭ���̾�Ʈ���� �ָ� �˾�â����
					// �˷��ִ� �� ���� �� ����...
					for (int i = 0; i < max_client; i++)
						System.out.println(job[i]);
				}

				while (true) {
					// ���⼭���� � �������� �� �𸣰���...
					String input = in.readLine();

					if (input == null) { // if input is null,
						return;
					}

					// whisper..?????? �ʿ��� ����..??? -> PA�Ҷ� ���� �ڵ带 �״�� �Ἥ ���� �����ص� ����
					//else if (input.startsWith("<") && input.indexOf("/>") != -1) {
					//	String whisper;
					//	whisper = input.substring(1, input.indexOf("/>"));
//
	//					if (names.contains(whisper)) {
		//					PrintWriter sender = info.get(name);
			//				PrintWriter receiver = info.get(whisper);
				//			receiver.println("MESSAGE " + "<whisper from " + name + "> : "
					//				+ input.substring(whisper.length() + 3));
						//	sender.println("MESSAGE " + "<whisper to " + whisper + "> : "
							//		+ input.substring(whisper.length() + 3));
					//	} else {
					//		PrintWriter sender = info.get(name);
						//	sender.println("MESSAGE " + "This user does not exist.");
						//}

					//}
					// tell a job --��ɾ ��߸� ������ �˷��ִ�..??�� ���ٴ� �Ϲ������� �˷��ִ°� ���� �� ���ƿ�~
					// ������������ ������ ���Ͽ� ��� Ŭ���̾�Ʈ���� �ָ� �˾�â���� �˷��ִ� �� ���� �� ����... !
					// ���� ������ �����س��� ����� gui�� �������� �ʰ� ���� ��ɸ� ������ ���ҽ��ϴ�. ���� gui�� �ϼ��Ǹ� ���� ����
					else if (input.startsWith("/") && input.indexOf("job") != -1) {
						int temp_index = 0;

						for (int i = 0; i < client_count; i++) {
							if (name == user[i])
								temp_index = i;
						}

						PrintWriter sender = info.get(name);
						sender.println("MESSAGE " + "your job is "
								+ job[temp_index].substring(0, job[temp_index].indexOf(" ")));
					}
					// job�� role�� ����??? job : ���� role : ����(�ɷ�)
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
					// �̸�??? ���ϴ� �κ����� �� �𸣰ھ��...
					// 11��24�� ���Ӷ� �����ϼ̴� ��ǥ�� �Ҷ� �������� Ŭ���̾�Ʈ���� �̸��� �޾ƿ��� ���� �����Դϴ�. ������ ä��â�� /name�̶�� ġ��
					// ���� ���� �ִ� �������� �̸��� gui�˾�â���� ��
					// ���� �̿ϼ�
					/***************************************
					 * modified
					 *******************************************/
					else if (input.startsWith("/") && input.indexOf("timeout") != -1) {
						timer_flag++;
						if (timer_flag == current_client) {
							String temp = null;
							for (int i = 0; i < max_client; i++) {
								if (kicked[i] != 0) {
									if (temp == null) {
										temp = user[i];
									} else {
										temp += ("," + user[i]);
									}
								}
							}
							System.out.println(temp);
							for (PrintWriter writer : writers) {
								writer.println("VOTENAME " + temp);
							}
						}
					} else if (input.startsWith("/") && input.indexOf("victim") != -1) {
						String victim = input.substring(7);
						int temp_index = 0;
						for (int i = 0; i < max_client; i++) {
							if (user[i].equals(victim) && kicked[i] != 0)
								temp_index = i;
						}
						is_vote++;
						vote[temp_index]++;
					} else if (input.startsWith("/") && input.indexOf("police") != -1) {
						PrintWriter police = info.get(name);
						String temp = null;
						for (int i = 0; i < max_client; i++) {
							if (kicked[i] != 0 && !user[i].equals(user[police_index])) {
								if (temp == null) {
									temp = user[i];
								} else {
									temp += ("," + user[i]);
								}
							}
						}
						System.out.println(temp);
						if (name.equals(user[police_index]))
							police.println("JOB" + temp);
					} else if (input.startsWith("/") && input.indexOf("kill") != -1) {
						System.out.println("���ǾƳ�?");
						PrintWriter mafia = info.get(name);
						String temp = null;
						for (int i = 0; i < max_client; i++) {
							if (kicked[i] != 0 && !user[i].equals(user[mafia_index])) {
								if (temp == null) {
									temp = user[i];
								} else {
									temp += ("," + user[i]);
								}
							}
						}
						System.out.println(temp);
						if (name.equals(user[mafia_index])) {
							mafia.println("KILL" + temp);
						}
					} else if (input.startsWith("/") && input.indexOf("dead") != -1) {
						PrintWriter mafia = info.get(user[mafia_index]);
						String selected = input.substring(5);
						PrintWriter dead = info.get(selected);
						for (int i = 0; i < max_client; i++) {
							if (user[i].equals(selected) && kicked[i] != 0)
								victim_index = i;
						}
						if (kicked[doctor_index] != 0) {
							PrintWriter doctor = info.get(user[doctor_index]);
							selectedByMafia[victim_index] = 1;

							String temp = null;
							for (int i = 0; i < max_client; i++) {
								if (kicked[i] != 0) {
									if (temp == null) {
										temp = user[i];
									} else {
										temp += ("," + user[i]);
									}
								}
							}
							doctor.println("DOCTOR" + temp);
						} else {
							dead.println("KICKED");
							for (PrintWriter writer : writers) {
								writer.println("D_START" + user[victim_index] + " dead, he was "
										+ job[victim_index].substring(0, job[victim_index].indexOf(" ")));
							}
							kicked[victim_index] = 0;
							current_client--;
						}
					} else if (input.startsWith("/") && input.indexOf("protect") != -1) {
						PrintWriter dead = info.get(user[victim_index]);
						int temp_index = 9999;
						String protect = input.substring(8);

						for (int i = 0; i < max_client; i++) {
							if (protect.equals(user[i]))
								temp_index = i;
						}

						selectedByMafia[temp_index] = 0;

						if (selectedByMafia[victim_index] == 1) {
							dead.println("KICKED");
							for (PrintWriter writer : writers) {
								writer.println("D_START" + user[victim_index] + " dead, he was "
										+ job[victim_index].substring(0, job[victim_index].indexOf(" ")));
							}
							kicked[victim_index] = 0;
							current_client--;
						} else {
							for (PrintWriter writer : writers) {
								writer.println("D_START" + "Doctor saved victim");
							}
						}
					}
					// else if (input.startsWith("/") && input.indexOf("police") != -1) {
					// PrintWriter police = info.get(name);
					// String temp = null;
					// for (int i = 0; i < max_client; i++) {
					// if (kicked[i] != 0 && !user[i].equals(user[police_index])) {
					// if (temp == null) {
					// temp = user[i];
					// } else {
					// temp += ("," + user[i]);
					// }
					// }
					// }
					// System.out.println(temp);
					// if (name.equals(user[police_index])) {
					// police.println("JOB" + temp);
					// } else {
					// police.println("MESSAGE " + "You are not police");
					// }
					// }
					else if (input.startsWith("/") && input.indexOf("is_he_mafia?") != -1) {
						PrintWriter police = info.get(user[police_index]);
						String selected = input.substring(13);
						int temp_index = 9999;
						System.out.println("selected : " + selected);
						for (int i = 0; i < max_client; i++) {
							if (user[i].equals(selected) && kicked[i] != 0)
								temp_index = i;
							System.out.println("user[" + i + "] : " + user[i]);
						}
						police.println("IS_MAFIA?" + user[temp_index] + "' job is "
								+ job[temp_index].substring(0, job[temp_index].indexOf(" ")));
					}
					// ���ϴ� �κ����� �� �𸣰ھ��..�Ф�
					// ������ ä���� ġ�� �ٸ� �����鿡�� �����ִ� ���

					else if (input.startsWith("/") && input.indexOf("matrix") != -1) {
						String temp = null;
						for (int i = 0; i < matrix_size; i++) {
							for (int j = 0; j < matrix_size; j++) {
								if (temp == null)
									temp = Integer.toString(matrix[i][j]) + " ";
								else if (i == 6 && j == 6)
									temp += Integer.toString(matrix[i][j]);
								else
									temp += Integer.toString(matrix[i][j]) + " ";
							}

						}
						for (PrintWriter writer : writers) {
							writer.println("MATRIX" + temp);
						}
					} else if (input.startsWith("object_clicked")) {
						clickedNum++;
						if (game_start_flag == 1 && clickedNum == current_client) {
							for (PrintWriter writer : writers) {
								writer.println("T_START");
							}

						}
					} else {
						for (PrintWriter writer : writers) {
							writer.println("MESSAGE " + name + ": " + input);
						}
					}

					if (is_vote == client_count) {
						int count = 0;
						int temp_index = 0;
						int same = 0;
						for (int i = 0; i < max_client; i++) {
							if (vote[i] > count) {
								count = vote[i];
								temp_index = i;
							}
						}
						for (int i = 0; i < max_client; i++) {
							if (count == vote[i] && i != temp_index)
								same = 1;
						}

						if (count == 0)
							same = 0;

						if (same != 1) {
							PrintWriter victim = info.get(user[temp_index]);
							victim.println("KICKED");
							for (PrintWriter writer : writers) {
								writer.println("V_END" + user[victim_index] + " dead, he was "
										+ job[victim_index].substring(0, job[victim_index].indexOf(" ")));
							}
							kicked[temp_index] = 0;
							current_client--;
						} else {
							for (PrintWriter writer : writers) {
								writer.println("V_END" + "Nothing happened");
							}
						}
						is_vote = 0;
						count = 0;
						for (int i = 0; i < max_client; i++)
							vote[i] = 0;
					}

					if (kicked[mafia_index] == 0) {
						for (PrintWriter writer : writers) {
							writer.println("MESSAGE " + "mafia dead!, citizen win!");
						}
						System.exit(0);
					} else if (current_client == 2) {
						for (PrintWriter writer : writers) {
							writer.println("MESSAGE " + "mafia win!");
						}
						System.exit(0);
					}
					/*****************************************************************************/
				}
			} catch (IOException e) { // ���ڱ� catch�� ���� ����..? -> ���� �߻���
				System.out.println(e);
			} finally { // if client is out, alert.
				if (name != null) {
					// for (PrintWriter writer : writers) {
					// writer.println("MESSAGE " + "[" + name + "] exit");
					// }
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

	// Ŭ���̾�Ʈ ���ο��� �ý��� �޼��� ������ �޼ҵ�
	public static void sendToallclient(String mssg) {
		for (PrintWriter writer : writers) {
			writer.println(mssg);
			writer.flush();
		}
	}
}