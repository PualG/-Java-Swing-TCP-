import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;


public class Client {
	private JFrame frame;
	private static JTextArea textArea;
	private JTextField textField;
	private JTextField port;
	private JTextField hostIp;
	private JTextField txt_name;
	private JButton btn_start;
	private JButton btn_stop;
	private JButton btn_send;
	private JLabel isConJLabel;
	private  ReceiveMsg receiveMsg;
	private  Socket socket;
	private  PrintWriter pw;
	private  BufferedReader br;


	public  static boolean isConnected = false;
	public static void main(String[] args) {
		new WindowLogin();
	}

	Client(){
		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setForeground(Color.black);
		port = new JTextField("6666");
		hostIp = new JTextField("127.0.0.1");
		txt_name = new JTextField("名字");
		btn_start = new JButton("连接");
		btn_stop = new JButton("断开");
		btn_stop.setEnabled(false);
		btn_send = new JButton("发送");
		btn_send.setBackground(Color.yellow);
		textField = new JTextField();
		isConJLabel = new JLabel("[未连接]");
		isConJLabel.setForeground(Color.red);
		JPanel panel1 = new JPanel(new FlowLayout());
		panel1.add(new JLabel("端口"));
		panel1.add(port);
		panel1.add(new JLabel("IP"));
		panel1.add(hostIp);
		panel1.add(new JLabel("昵称"));
		panel1.add(txt_name);
		panel1.add(isConJLabel);
		panel1.add(btn_start);
		panel1.add(btn_stop);
		panel1.setBorder(new LineBorder(Color.BLACK));

		JPanel panel2 = new JPanel(new BorderLayout());
		JScrollPane scrollPanel = new JScrollPane(textArea);
		scrollPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);//设置水平滚动条
		scrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);//设置垂直滚动条
		textArea.setEnabled(false);
		panel2.add(scrollPanel, BorderLayout.CENTER);

		panel2.setBorder(new LineBorder(Color.BLACK));

		JPanel panel3 = new JPanel(new BorderLayout());
		panel3.add(textField,BorderLayout.CENTER);
		panel3.add(btn_send,BorderLayout.EAST);
		panel3.setBorder(new LineBorder(Color.BLACK));

		panel2.setBorder(new TitledBorder("聊天记录"));
		frame = new JFrame("客户端");
		frame.setSize(600, 400);
		frame.add(panel1,BorderLayout.NORTH);
		frame.add(panel2,BorderLayout.CENTER);
		frame.add(panel3,BorderLayout.SOUTH);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);



		btn_start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int port1;
				if (isConnected) {
					JOptionPane.showMessageDialog(frame, "已连接", "错误", JOptionPane.ERROR_MESSAGE);
					return;
				}
				try {
					try {
						port1 = Integer.parseInt(port.getText().trim());
					} catch (NumberFormatException e2) {
						throw new Exception("端口号不符合要求");
					}
					String hostIp1 = hostIp.getText().trim();
					String name = txt_name.getText().trim();
					if (name.equals("") || hostIp1.equals("")) {
						throw new Exception("昵称、服务器IP不能为空");
					}
					boolean flag = connectServer(port1, hostIp1, name);
					if (flag == false) {
						throw new Exception("与服务器连接失败");
					}
					JOptionPane.showMessageDialog(frame, "连接成功");
				} catch (Exception exc) {
					JOptionPane.showMessageDialog(frame, exc.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btn_stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!isConnected) {
					JOptionPane.showMessageDialog(frame, "已断开", "错误", JOptionPane.ERROR_MESSAGE);
					return;
				}
				try {
					boolean flag = closeConnection();// 断开连接
					if (flag == false) {
						throw new Exception("断开异常");
					}
					JOptionPane.showMessageDialog(frame, "成功断开");
				} catch (Exception exc) {
					JOptionPane.showMessageDialog(frame, exc.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		textField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				send();
			}
		});
		btn_send.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				send();
			}
		});
	}

	public boolean connectServer(int port, String hostIp, String name) {

		try {
			socket = new Socket(hostIp, port);
			pw = new PrintWriter(socket.getOutputStream());
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			sendMsg(name + "@" + socket.getLocalAddress().toString());
			receiveMsg = new ReceiveMsg(br,textArea);
			receiveMsg.start();
			isConJLabel.setForeground(Color.green);
			isConJLabel.setText("[连接成功]");
			btn_start.setEnabled(false);
			btn_stop.setEnabled(true);
			isConnected = true;
			return true;
		} catch (Exception e) {
			textArea.append("与端口号为：" + port + "    IP地址为：" + hostIp + "   的服务器连接失败!" + "\r\n");
			isConnected = false;
			return false;
		}
	}

	public  synchronized boolean closeConnection() {
		try {
			sendMsg("CLOSE");// 发送断开连接命令给服务器
			receiveMsg.stop();
			// 释放资源
			if (br != null) {
				br.close();
			}
			if (pw != null) {
				pw.close();
			}
			if (socket != null) {
				socket.close();
			}
			btn_start.setEnabled(true);
			btn_stop.setEnabled(false);
			isConnected = false;
			return true;
		} catch (IOException e1) {
			e1.printStackTrace();
			isConnected = true;
			return false;
		}
	}

	public void send(){
		if (!isConnected) {
			JOptionPane.showMessageDialog(frame, "未连接服务器", "错误", JOptionPane.ERROR_MESSAGE);
			return;
		}
		String message = textField.getText().trim();
		if (message == null || message.equals("")) {
			JOptionPane.showMessageDialog(frame, "消息为空", "错误", JOptionPane.ERROR_MESSAGE);
			return;
		}
		sendMsg(txt_name.getText() + "@" + "|" + "@" + message);
		textField.setText(null);
	}


	public  void sendMsg(String message) {
		pw.println(message);
		pw.flush();
	}

	public static class ReceiveMsg extends Thread{
		private BufferedReader reader;
		private JTextArea textArea;
		public ReceiveMsg(BufferedReader reader, JTextArea textArea) {
			this.reader = reader;
			this.textArea = textArea;
		}

		@Override
		public void run() {
			String msg;
			while (true){
				try {
					msg = reader.readLine();
					textArea.append(msg+"\r\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}