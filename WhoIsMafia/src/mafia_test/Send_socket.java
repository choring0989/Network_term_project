package net_hw2;

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
	private static int matrixSize = 7;
	static BufferedReader in;
	static PrintWriter out;
	JFrame frame = new JFrame();
	JPanel panel = new JPanel();
	Font font = new Font("�������", Font.PLAIN, 20);
	JTextField textField = new JTextField(22);
	JTextArea messageArea = new JTextArea(4, 22);
	public String[] vote_name = new String[7];

	public Send_socket() {
		messageArea.setEditable(false);
		textField.setEditable(false);
		messageArea.setFont(font);
		textField.setFont(font);
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

	/* ���� �г��� �Է� */
	private String getsName() {
		return JOptionPane.showInputDialog(frame, "Choose a User's nikname:", "Who is the mafia",
				JOptionPane.PLAIN_MESSAGE);
	}

	void runChat(String[] players, int page) throws IOException {
		// Make connection and initialize streams
		int[][] matrix = new int[matrixSize][matrixSize];
		String serverAddress = new String(getServerAddress());
		JFrame actionFrame = new JFrame();
		Socket socket = new Socket(serverAddress, 9001);
		Thread t3 = new Thread(new Timer_start());
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);
		
		while (true) {
			String line = in.readLine();
			if (line.startsWith("SUBMITNAME")) {
				out.println(getsName());
			} else if (line.startsWith("NAMEACCEPTED")) {
				textField.setEditable(true);
			} else if (line.startsWith("MESSAGE")) {
				messageArea.append(line.substring(8) + "\n");
			} else if (line.startsWith("ERROR")){
				JOptionPane.showMessageDialog(null, line.substring(6));
			}

			// out.println("vote"+vote(players)); //-> players ���� �̸��� ��� ����Ʈ
			else if (line.startsWith("JOB")) {
				line = line.substring(3);
				String selected = police(line);
				System.out.println("police" + selected);
				out.println("/is_he_mafia?" + selected);
			} else if (line.startsWith("IS_MAFIA?")) {
				messageArea.append(line.substring(9) + "\n");
				out.println("/kill");
			} else if (line.startsWith("VOTENAME ")) {//�׽��� ����
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
			} else if (line.startsWith("D_START")) {
				messageArea.append("\t[SYSTEM MESSAGE]" + "\n");
				messageArea.append("\t��ħ�� ��Ҵ�." + "\n");
				messageArea.append(line.substring(7) + "\n");
			} else if (line.startsWith("MATRIX")) {
				int count = 0;
				line = line.substring(6);
				String[] temp = line.split(" ");
				for (int i = 0; i < matrixSize; i++) {
					for (int j = 0; j < matrixSize; j++) {
						matrix[i][j] = Integer.parseInt(temp[count]);
						count++;
					}
				}
			} else if (line.startsWith("T_START")) {
				messageArea.append("\t[SYSTEM MESSAGE]" + "\n");
				messageArea.append("      ���Ÿ� ����� ã�� �� ����." + "\n");
				messageArea.append("     ������ �ǳ��Ͽ� ���ǾƸ� ã��." + "\n");
				t3.start();
				JOptionPane.showMessageDialog(actionFrame, line, "Message", 2);
			} else if (line.startsWith("V_END")) {
				messageArea.append(line.substring(5)+"\n\n");
				messageArea.append("\t[SYSTEM MESSAGE]" + "\n");
				messageArea.append("       �ѹ����� �Ǿ���." + "\n");
				messageArea.append("    ���Ȱ��� �ǽ� ���� ����� �ɹ��Ѵ�." + "\n");
				messageArea.append("     ���Ǿƴ� ���� ���� ����� ����." + "\n");
				messageArea.append("      �ǻ�� �� ����� ��ȣ�Ѵ�." + "\n");
				out.println("/police");
			} else if (line.startsWith("KICKED")) {
				System.exit(0);
			}
		}

	}

	public String vote(String line) { //�׽���
		String candidate = null;
		String[] selections = line.split(",");
		for (int i = 0; i < selections.length; i++)
			System.out.println(selections[i]);//��ǥ�� ���� ���� �̸��� ��� ����

		candidate = (String) JOptionPane.showInputDialog(null, "���ǾƷ� �ǽɵǴ� ����� ��������.", "vote", JOptionPane.QUESTION_MESSAGE,
				null, selections, "user1");
		return candidate; // ->�������� candidate ����
	}

	public String police(String line) { //�׽���
		String selected = null;
		String[] selections = line.split(",");
		for (int i = 0; i < selections.length; i++)
			System.out.println(selections[i]);//���� �̸� ���� ����
		selected = (String) JOptionPane.showInputDialog(null, "������ ���ϱ�...", "select", JOptionPane.QUESTION_MESSAGE,
				null, selections, "user1");
		return selected;
	}

	public String doctor(String line) {
		String protect = null;
		String[] selections = line.split(",");
		for (int i = 0; i < selections.length; i++)
			System.out.println(selections[i]);//���� �̸� ���� ����

		protect = (String) JOptionPane.showInputDialog(null, "������ ��ȣ�ұ�...", "protect", JOptionPane.QUESTION_MESSAGE,
				null, selections, "user1");
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