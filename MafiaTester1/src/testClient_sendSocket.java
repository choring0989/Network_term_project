
/*������ Send_socket Ŭ����*/
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

public class testClient_sendSocket {
	BufferedReader in;
	PrintWriter out;
	JFrame frame = new JFrame();
	JTextField textField = new JTextField(80);
	JTextArea messageArea = new JTextArea(16, 80);
	  private String getServerAddress(){
	      return JOptionPane.showInputDialog(
	          frame,
	          "Enter IP Address of the Server:",
	          "Who is the mafia",
	          JOptionPane.PLAIN_MESSAGE);
	  }
	  
	 /*���ӿ��� ����� �̸��� �Է¹���*/
	  private String getsName(){
	      return JOptionPane.showInputDialog(
	          frame,
	          "Choose a User's ninkname:",
	          "Who is the mafia",
	          JOptionPane.PLAIN_MESSAGE);
	  }
	  /*�Ʒ� run �Լ��� int page�� ����ȭ�鿡�� ������ �� �� ������ �г����� �ް� �;� ���� �����Դϴ�.*/
	  void run(String[] players, int page) throws IOException {
	      // Make connection and initialize streams
		  String serverAddress = new String(getServerAddress());
		  Socket socket = new Socket(serverAddress, 9001);
	      in = new BufferedReader(new InputStreamReader(
	          socket.getInputStream()));
	      out = new PrintWriter(socket.getOutputStream(), true);
	      /*�Ʒ� while���� ���� �� �������ݿ��� KICKED���� ������ GUI�� ������ ���� �� �Ǵ� ������ �ֽ��ϴ�..*/
        while (true) {
			String line = in.readLine();
			/*���� �ּҸ� �ι� ġ�� ���� �ϴ� ������ �ֽ��ϴ�..., ���ÿ����� GUIâ�� ������ ����� ���μ����� �� �ϳ��� ����˴ϴ�.*/
			if (page == 1 && line.startsWith("SUBMITNAME")) {
				out.println(getsName());
				break;// input name
			} else if (line.startsWith("NAMEACCEPTED")) {
				textField.setEditable(true);
			} else if (line.startsWith("MESSAGE")) {
				messageArea.append(line.substring(8) + "\n");// input message
			} else if (line.startsWith("KICKED")) {
				textField.setEditable(false);
				break;
			}
		}
	  }
}
