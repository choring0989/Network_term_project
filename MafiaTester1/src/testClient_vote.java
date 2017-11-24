/*������ GUI_chatting Ŭ����*/
/********************************************
 * vote��� Ŭ���̾�Ʈ�� �ؾ�����
 * 
 * 1. �����κ��� �����̸� �޾ƿ���, �гξȿ� �־����
 * 2. �������� Ŭ���̾�Ʈ�� ��ǥ�� �ĺ��� �̸��� �����ֱ�
 *******************************************/

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

/*�ϴ� ��ǥ�г��˾��� �ߴ� �� Ȯ���Ϸ��� pa�� ä��â ��ɿ� vote�Լ��� �߰��� ����*/
// gui�� �ϼ��Ǹ� ���ľ� ��.

public class testClient_vote{

    BufferedReader in;
    PrintWriter out;
    JFrame frame = new JFrame("Chatter");
    JTextField textField = new JTextField(40);
    JTextArea messageArea = new JTextArea(8, 40);

    public testClient_vote() {
        // Layout GUI
        textField.setEditable(false);
        messageArea.setEditable(false);
       
        frame.getContentPane().add(textField, "North"); //ä��â�� �� ���ʿ� ��ġ
        frame.getContentPane().add(new JScrollPane(messageArea), "Center"); //��ũ���� �߾ӿ� ��ġ
        frame.getContentPane().add(new JScrollPane(messageArea), "Center"); 
        frame.pack(); //��ü ä��â�� ������

        // Add Listeners
        textField.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                out.println(textField.getText()); //�ؽ�Ʈ���� �Է¹��� �� ����Ʈ �ϱ�
                textField.setText("");
            }
        });
    }

/*� ������ ������ ������ �Է¹���*/
    private String getServerAddress() {
        return JOptionPane.showInputDialog(
            frame,
            "Enter IP Address of the Server:",
            "Welcome to the Chatter",
            JOptionPane.QUESTION_MESSAGE);
    }

/*���ӿ��� ����� �̸��� �Է¹���*/
    private String getName() {
        return JOptionPane.showInputDialog(
            frame,
            "Choose a screen name:",
            "Screen name selection",
            JOptionPane.PLAIN_MESSAGE);
    }

/*�������Ӱ� �̸��Է�â�� ����*/
   // private void run(String[] players) throws IOException {
    // �����κ��� ���� �̸��� �޾ƿ��� ���� �������� �����ؾߵ�. string���� �޾ƿ��� ����.
    
    // ������ �׽�Ʈ�� ���ؼ� �Ʒ� �������� ������. string���� �޾ƿ��� �ʴ� �����Ͽ� �׽�Ʈ.
    private void run() throws IOException {

        // Make connection and initialize streams
        String serverAddress = getServerAddress();
        Socket socket = new Socket(serverAddress, 9001);
        in = new BufferedReader(new InputStreamReader(
            socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        
        int count=0;
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
            /* ���� �̰� �´� �ڵ�!
             * if(������ 5���� �Ǿ��ٰ� �˷��ָ�)
             *	out.println("vote"+vote(players)); //-> players�� �����̸��� ��� ��Ʈ�� �迭
            */
           if(count==0){//�׽�Ʈ�� ���� ���ư��� �κ�
            out.println("vote "+vote());
           }
           count++;
            //System.out.println("vote "+vote());
        }
    }
    /* ���� �̰� �´� �ڵ�!
     * public String vote (String[] users){
    	String candidate=null;
        String[] selections=users;//��ǥ�� ���� �����̸��� ��Ƴ���. �������� �޾ƿ;� ��.
        candidate=(String) JOptionPane.showInputDialog(null, "5���� �������ϴ�. ������ ������Ű�ڽ��ϱ�?", "vote", JOptionPane.QUESTION_MESSAGE,null,selections,"user1");
        //null���� �� �˾��� ��� pane�� �̸��� ���´�.
        return candidate; //->�������� candidate�� ������.
    }*/
    
    public String vote (){ //�׽�Ʈ�� ��ǥ�г�.
    	String candidate=null;
        String[] selections={"a","b","c"};//��ǥ�� ���� �����̸��� ��Ƴ���. �������� �޾ƿ;� ��.
        candidate=(String) JOptionPane.showInputDialog(null, "5���� �������ϴ�. ������ ������Ű�ڽ��ϱ�?", "vote", JOptionPane.QUESTION_MESSAGE,null,selections,"user1");
        //null���� �� �˾��� ��� pane�� �̸��� ���´�.
        return candidate; //->�������� candidate�� ������.
    } 

	public static void main(String[] args) throws Exception {
		testClient_vote client = new testClient_vote();
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //������ ��ư�� ������ ����
        client.frame.setVisible(true); //ä��â�� ������
        //client.run(players); //��������, �̸��Է� â�� ���, �̰� �´� �ڵ�!!
        
        client.run(); //�׽�Ʈ��
	}

}