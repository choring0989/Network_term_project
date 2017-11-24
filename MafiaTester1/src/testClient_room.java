package net_hw2;
/*������ roomGUI Ŭ����*/
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.lang.Object;
public class testClient_room {
	   JFrame startFrame = new JFrame();
	   JFrame frame = new JFrame("Who is Mafia?");
	   JFrame actionFrame = new JFrame();
	   ImageIcon room = new ImageIcon("room2.jpg");
	   Image newImage = room.getImage();
	   Image changedImage = newImage.getScaledInstance(800,600,Image.SCALE_SMOOTH);
	   ImageIcon newRoom = new ImageIcon(changedImage);
	   JTextField textField = new JTextField(20);
	   JTextArea messageArea = new JTextArea(4,40);
	   
	   JPanel panel = new JPanel(){
	      public void paintComponent(Graphics g)
	      {
	         g.drawImage(newRoom.getImage(),0,0,null);
	      }
	   };
	   
	   public void runGUI()
	   {
	      
	      panel.setLayout(new BorderLayout());
	      panel.add(textField,"South");
	      panel.add(messageArea,"East");
	      panel.setVisible(true);
	      frame.getContentPane().add(panel);
	      frame.setVisible(true);
	      frame.setBounds(0, 0, 1200, 600);  
	      ImageIcon key = new ImageIcon("key.png");
	      ImageIcon picture = new ImageIcon("object.png");
	      this.setObject(key);
	      this.setObject(picture);
	      /*�Ʒ� ���� �ȿ����� while���� ���� ������ ������Ʈ�� ������ �˾� �޽����� �� �ߴ� ������ ����
	       * �ذ��� : ä�� ���� ������� ������Ʈ ã�� �����带 �и��ϸ� �˴ϴ�. �߰�����!*/
	      /*Send_socket chat = new Send_socket();
	      try {
			chat.run(null, 2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	   }
	   
	   public void setObject(ImageIcon object)
	   {
	      
	      panel.setLayout(null);
	      Insets insets = panel.getInsets();
	      int ran1 = (int)(300 * (Math.random()));
	      int ran2 = (int)(300 * (Math.random()));
	      //System.out.println(ran1 +" " + ran2);
	      JButton button = new JButton(object);
	      
	      Dimension size = button.getPreferredSize();
	      button.setBackground(Color.red); 
	        button.setBorderPainted(false); 
	        button.setFocusPainted(false); 
	        button.setContentAreaFilled(false);
	        panel.add(button);
	      button.setBounds(insets.left+ran1,ran2+insets.top,200,200);
	      panel.setVisible(true);
	      frame.setVisible(true);
	      
	      button.addActionListener(new TheHandler());  
	   }
	    private class TheHandler implements ActionListener { 
	           public void actionPerformed(ActionEvent event) {
	              JOptionPane.showMessageDialog(
	                          actionFrame,
	                          "Description of Object",
	                          "Message",
	                          2);
	       }
	           }
}

