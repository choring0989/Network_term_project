
package mafia_test;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.*;
import java.util.*;

public class Send_socket implements Runnable {
	BufferedReader in;
	PrintWriter out;
	JFrame frame = new JFrame();
	JPanel panel = new JPanel();
	JTextField textField = new JTextField(20);
	JTextArea messageArea = new JTextArea(4, 40);
	public String[] vote_name = new String[7];

	public Send_socket() {
		messageArea.setEditable(false);
		textField.setEditable(false);
		RoomGUI.frame.getContentPane().add(textField, "South");
		RoomGUI.frame.getContentPane().add(new JScrollPane(messageArea), "East");
		RoomGUI.frame.setVisible(true);
		textField.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				out.println(textField.getText());
				textField.setText("");
			}
		});
	}

	private String getServerAddress() {
		return JOptionPane.showInputDialog(frame, "Enter IP Address of the Server:", "Who is the mafia",
				JOptionPane.PLAIN_MESSAGE);
	}

	/* ���ӿ��� ����� �̸��� �Է¹��� */
	private String getsName() {
		return JOptionPane.showInputDialog(frame, "Choose a User's nikname:", "Who is the mafia",
				JOptionPane.PLAIN_MESSAGE);
	}

	/* �Ʒ� run �Լ��� int page�� ����ȭ�鿡�� ������ �� �� ������ �г����� �ް� �;� ���� �����Դϴ�. */
	void runChat(String[] players, int page) throws IOException {
		// Make connection and initialize streams
		String serverAddress = new String(getServerAddress());
		Socket socket = new Socket(serverAddress, 9001);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);
		/* �Ʒ� while���� ���� �� �������ݿ��� KICKED���� ������ GUI�� ������ ���� �� �Ǵ� ������ �ֽ��ϴ�.. */

		while (true) {
			String line = in.readLine();
			if (line.startsWith("SUBMITNAME")) {
				out.println(getsName());
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
			} else if (line.startsWith("DOCTOR")) {
				line = line.substring(6);
				String protect = doctor(line);
				System.out.println(protect);
				out.println("/protect" + protect);
			} else if (line.startsWith("KICKED")) {
				System.exit(0);
			}
		}
	}

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

	public String doctor(String line) {
		String protect = null;
		String[] selections = line.split(",");
		for (int i = 0; i < selections.length; i++)
			System.out.println(selections[i]);// ��ǥ�� ���� �����̸��� ��Ƴ���. �������� �޾ƿ;� ��.

		protect = (String) JOptionPane.showInputDialog(null, "������ ��Ű�� �ǰ���?", "protect", JOptionPane.QUESTION_MESSAGE,
				null, selections, "user1");
		// null���� �� �˾��� ��� pane�� �̸��� ���´�.
		return protect;
	}

	public void run() {
		try {
			this.runChat(null, 1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}