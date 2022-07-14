
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;





public class Server {
	private JFrame frame;
    private JTextField port;    //���ö˿ں�
    private JList userList;         //�б����
    private DefaultListModel listModel;
    private JButton btn_start;      //��ʼ��ť
    private JButton btn_stop;       //�Ͽ���ť
    private ServerSocket serverSocket;
    private ServerListen serverListen;
    


    private ArrayList<ClientChanal> channalArrayList;
    private boolean isStart = false;



    public static void main(String[] args) {
        new Server();
    }



    Server(){

        port = new JTextField("6666");
        btn_start = new JButton("����");
        btn_stop = new JButton("ֹͣ");
        btn_stop.setEnabled(false);
        listModel = new DefaultListModel();
        userList = new JList(listModel);

        JPanel panel1 = new JPanel(new FlowLayout());
        panel1.add(new JLabel("�˿�"));
        panel1.add(port);
        panel1.add(btn_start);
        panel1.add(btn_stop);

        JPanel panel2 = new JPanel(new BorderLayout());
        JScrollPane scrollPanel = new JScrollPane(userList);
        scrollPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);//����ˮƽ������
        scrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);//���ô�ֱ������
        panel2.add(scrollPanel, BorderLayout.CENTER);
        panel2.setBorder(new TitledBorder("�û�����"));

        frame = new JFrame("������");
        frame.setSize(600, 400);
        frame.add(panel1,BorderLayout.NORTH);
        frame.add(panel2,BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        

        btn_start.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (isStart) {
                    JOptionPane.showMessageDialog(frame, "�������Ѵ�������״̬", "����", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int port1;
                try {
                    try {
                        port1 = Integer.parseInt(port.getText());
                    } catch (Exception e1) {
                        throw new Exception("��������ȷ�Ķ˿ں�");
                    }
                    if (port1 <= 0) {
                        throw new Exception("��������ȷ�Ķ˿ں�");
                    }
                    serverStart(port1);
                    JOptionPane.showMessageDialog(frame, "����������");
                    btn_start.setEnabled(false);
                    port.setEnabled(false);
                    btn_stop.setEnabled(true);
                } catch (Exception exc) {
                    JOptionPane.showMessageDialog(frame, exc.getMessage(),
                            "����", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        btn_stop.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				closeServer();
				JOptionPane.showMessageDialog(frame, "�������ر�");
			}
		});
    }

    public void serverStart(int port){

        try {
            serverSocket = new ServerSocket(port);
            channalArrayList = new ArrayList<ClientChanal>();
            serverListen = new ServerListen(serverSocket);
            serverListen.start();
            isStart = true;
        } catch (IOException e) {
            e.printStackTrace();
            isStart = false;
            try {
                throw new BindException("�˿ں��ѱ�ռ��");
            } catch (BindException bindException) {
                bindException.printStackTrace();
            }
        }
    }
    
    public void closeServer() {
        try {
            if (serverListen != null)
            	serverListen.stop();
            for (int i = channalArrayList.size() - 1; i >= 0; i--) {
            	channalArrayList.get(i).getWriter().println("CLOSE");
            	channalArrayList.get(i).getWriter().flush();
            	channalArrayList.get(i).stop();
            	channalArrayList.get(i).br.close();
            	channalArrayList.get(i).pw.close();
            	channalArrayList.get(i).socket.close();
            	channalArrayList.remove(i);
            }
            if (serverSocket != null) {
                serverSocket.close();
            }
            btn_start.setEnabled(true);
            btn_stop.setEnabled(false);
            listModel.removeAllElements();
            isStart = false;
        } catch (IOException e) {
            e.printStackTrace();
            isStart = true;
        }
    }
    public static String getTime() {
        Calendar calendar = Calendar.getInstance();
        int y, m, d, h, min, s;
        y = calendar.get(Calendar.YEAR);//��ȡ���
        m = calendar.get(Calendar.MONTH);//��ȡ�·�
        d = calendar.get(Calendar.DATE);//��ȡ��
        h = calendar.get(Calendar.HOUR_OF_DAY);//Сʱ
        min = calendar.get(Calendar.MINUTE);//��
        s = calendar.get(Calendar.SECOND);//��
        return "["+y+"/"+m+"/"+d+" "+h+":"+min+":"+s+"]";
    }

    public void savaMsg(String msg) {
    	try {
    		File file = new File("D:/�����¼.txt");
    		if(!file.exists()) {
    			file.createNewFile();
    		}
			FileWriter writer = new FileWriter(file,true);
			writer.write(msg+"\n");
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


    public void sendToother(String message){
        StringTokenizer stringTokenizer = new StringTokenizer(message, "@");
        String source = stringTokenizer.nextToken();
        String owner = stringTokenizer.nextToken();
        String content = stringTokenizer.nextToken();
        message = source + "˵��" + content+getTime(); 
        savaMsg(message);
        for (int i = channalArrayList.size() - 1; i >= 0; i--) {
            channalArrayList.get(i).getWriter().println(message);
            channalArrayList.get(i).getWriter().flush();
        }
    }


    class ServerListen extends Thread{
        private  ServerSocket serverSocket;

        ServerListen(ServerSocket serverSocket){
            this.serverSocket = serverSocket;
        }

        @Override
        public void run() {
            while(true){
                try {
                    Socket socket = serverSocket.accept();
                    ClientChanal clientChanal = new ClientChanal(socket);
                    clientChanal.start();
                    channalArrayList.add(clientChanal);
                    listModel.addElement(clientChanal.getUser().getName());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    class ClientChanal extends Thread{
        private  Socket socket;
        private BufferedReader br;
        private PrintWriter pw;
        private User user;

        public BufferedReader getReader() {
            return br;
        }

        public PrintWriter getWriter() {
            return pw;
        }

        public User getUser() {
            return user;
        }

        ClientChanal(Socket socket){
            try {
                String msg;
                this.socket = socket;
                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                pw = new PrintWriter(socket.getOutputStream());
                msg = br.readLine();
                StringTokenizer st = new StringTokenizer(msg, "@");
                user = new User(st.nextToken(), st.nextToken());
                pw.println(user.getName() + user.getIp() + "����������ӳɹ�");
                pw.flush();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
                while (true){
                    try {
                        String msg = br.readLine();
                        if (msg.equals("CLOSE")){
                            pw.close();
                            br.close();
                            socket.close();
                            listModel.removeElement(user.getName());
                        }
                        else{
                            sendToother(msg);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        }
    }
    
   
}
	



