package net_hw2;

//hanjin's client

/*client_ä��â�� ��ǥ�г�GUI*/
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;

public class testClient_vote {

	BufferedReader in;
	PrintWriter out;
	JFrame frame = new JFrame("Chatter");
	JTextField textField = new JTextField(40);
	JTextArea messageArea = new JTextArea(8, 40);
	public String[] vote_name = new String[7];

	public testClient_vote() {
		// Layout GUI
		textField.setEditable(false);
		messageArea.setEditable(false);

		frame.getContentPane().add(textField, "North"); // ä��â�� �� ���ʿ� ��ġ
		frame.getContentPane().add(new JScrollPane(messageArea), "Center"); // ��ũ���� �߾ӿ� ��ġ
		frame.getContentPane().add(new JScrollPane(messageArea), "Center");
		frame.pack(); // ��ü ä��â�� ������

		// Add Listeners
		textField.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				out.println(textField.getText()); // �ؽ�Ʈ���� �Է¹��� �� ����Ʈ �ϱ�
				textField.setText("");
			}
		});
	}

	/* � ������ ������ ������ �Է¹��� */
	private String getServerAddress() {
		return JOptionPane.showInputDialog(frame, "Enter IP Address of the Server:", "Welcome to the Chatter",
				JOptionPane.QUESTION_MESSAGE);
	}

	/* ���ӿ��� ����� �̸��� �Է¹��� */
	private String getName() {
		return JOptionPane.showInputDialog(frame, "Choose a screen name:", "Screen name selection",
				JOptionPane.PLAIN_MESSAGE);
	}

	/* �������Ӱ� �̸��Է�â�� ���� */
	// private void run(String[] players) throws IOException {
	private void run() throws IOException {

		// Make connection and initialize streams
		String serverAddress = getServerAddress();
		Socket socket = new Socket(serverAddress, 9001);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);

		int count = 0;
		// Process all messages from server, according to the protocol.
		while (true) {
			String line = in.readLine();
			if (line.startsWith("SUBMITNAME")) {
				out.println(getName());
			} else if (line.startsWith("NAMEACCEPTED")) {
				textField.setEditable(true);
			} else if (line.startsWith("MESSAGE")) {
				messageArea.append(line.substring(8) + "\n");
			}
			// if(������ 5���� �Ǿ��ٰ� �˷��ָ�)

			// out.println("vote"+vote(players)); //-> players�� �����̸��� ��� ��Ʈ�� �迭
			else if (line.startsWith("JOB")) {
				line = line.substring(3);
				String selected = police(line);
				System.out.println("police" + selected);
				out.println("/is_he_mafia?" + selected);
			} else if (line.startsWith("IS_MAFIA?")) {
				messageArea.append(line.substring(9) + "\n");
			} else if (line.startsWith("VOTENAME ")) {// �׽�Ʈ�� ���� ���ư��� �κ�
				line = line.substring(9);
				String victim = vote(line);
				System.out.println(victim);
				out.println("/victim" + victim);
			} else if (line.startsWith("KILL")) {
				line = line.substring(4);
				String victim = vote(line);
				System.out.println(victim);
				out.println("/dead" + victim);
			} else if (line.startsWith("DEAD")) {
				messageArea.append(line.substring(4) + "\n");
			} else if (line.startsWith("KICKED")) {
				System.exit(0);
			}
			count++;
			// System.out.println("vote "+vote());
		}
	}

	/*
	 * public String vote (String[] users){ String candidate=null; String[]
	 * selections=users;//��ǥ�� ���� �����̸��� ��Ƴ���. �������� �޾ƿ;� ��. candidate=(String)
	 * JOptionPane.showInputDialog(null, "5���� �������ϴ�. ������ ������Ű�ڽ��ϱ�?", "vote",
	 * JOptionPane.QUESTION_MESSAGE,null,selections,"user1"); //null���� �� �˾��� ���
	 * pane�� �̸��� ���´�. return candidate; //->�������� candidate�� ������. }
	 */
	public String vote(String line) { // �׽�Ʈ��
		String candidate = null;
		String[] selections = line.split(",");
		for (int i = 0; i < selections.length; i++)
			System.out.println(selections[i]);// ��ǥ�� ���� �����̸��� ��Ƴ���. �������� �޾ƿ;� ��.

		candidate = (String) JOptionPane.showInputDialog(null, "������ ���̽ðڽ��ϱ�?", "vote", JOptionPane.QUESTION_MESSAGE,
				null, selections, "user1");
		// null���� �� �˾��� ��� pane�� �̸��� ���´�.
		return candidate; // ->�������� candidate�� ������.
	}

	public String police(String line) { // �׽�Ʈ��
		String selected = null;
		String[] selections = line.split(",");
		for (int i = 0; i < selections.length; i++)
			System.out.println(selections[i]);// ��ǥ�� ���� �����̸��� ��Ƴ���. �������� �޾ƿ;� ��.

		selected = (String) JOptionPane.showInputDialog(null, "������ ������ �ñ��ϽŰ���?", "select", JOptionPane.QUESTION_MESSAGE,
				null, selections, "user1");
		// null���� �� �˾��� ��� pane�� �̸��� ���´�.
		return selected;
	}

	public static void main(String[] args) throws Exception {
		testClient_vote client = new testClient_vote();
		client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // ������ ��ư�� ������ ����
		client.frame.setVisible(true); // ä��â�� ������
		// client.run(players); //��������, �̸��Է� â�� ���-->player�����������κ��͹޾ƿ;���

		client.run();
	}

}