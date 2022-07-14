import com.sun.security.ntlm.Server;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.*;


class WindowLogin extends JFrame implements ActionListener {
    JLabel jLabelName;
    JLabel jLabelPsw;
    JTextField jTextFieldName;
    JPasswordField jPasswordFieldPsw;
    JButton jButtonLogin;
    JButton jButtonExit;
    JButton jButtonregister;
    WindowLogin() {
        init();
        setLayout(new FlowLayout());
        setBounds(500,500,300,130);
        setTitle("登录窗口");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
    void init() {
        jLabelName = new JLabel("学号:");
        jTextFieldName = new JTextField(20);
        jLabelPsw = new JLabel("密码:");
        jPasswordFieldPsw = new JPasswordField(20);
        jButtonLogin = new JButton("登录");
        jButtonExit = new JButton("退出");
        jButtonregister = new JButton("注册");
        add(jLabelName);
        add(jTextFieldName);
        add(jLabelPsw);
        add(jPasswordFieldPsw);
        add(jButtonLogin);
        add(jButtonExit);
        add(jButtonregister);
        jButtonLogin.addActionListener(this);
        jButtonExit.addActionListener(this);
        jButtonregister.addActionListener(this);
    }
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("登录")) {
            String name = jTextFieldName.getText();
            String psw = new String(jPasswordFieldPsw.getPassword());
            if (psw.equals("123456")) {
                new Client();
                dispose();
            } else {
                JOptionPane.showMessageDialog(null, "密码输入错误！");
            }
        } else if (e.getActionCommand().equals("退出")) {
            System.exit(0);
        }
        else if (e.getActionCommand().equals("注册")) {
            new Register();
            dispose();
        }
    }
}

class Register extends JFrame implements ActionListener {
    JLabel jLabelName;
    JTextField jTextFieldname;
    JLabel jLabelnum;
    JTextField jTextFieldnum;
    JLabel jLabelmima;
    JPasswordField jPasswordField;
    JButton jButtonsure;
    Register() {
        init();
        setLayout(new FlowLayout());
        setBounds(500,500,300,130);
        setTitle("注册");
        setVisible(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    void init() {
        jLabelName = new JLabel("姓名:");
        jTextFieldname = new JTextField(10);
        jLabelnum = new JLabel("学号:");
        jTextFieldnum = new JTextField(10);
        jLabelmima = new JLabel("密码:");
        jPasswordField = new JPasswordField(10);
        jButtonsure = new JButton("确定");
        add(jLabelName);
        add(jTextFieldname);
        add(jLabelnum);
        add(jTextFieldnum);
        add(jLabelmima);
        add(jPasswordField);
        add(jButtonsure);
        jButtonsure.addActionListener(this);
    }
    public void savaMsg(String msg) {
        try {
            File file = new File("D:/用户.txt");
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
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("确定")) {
            savaMsg("姓名:"+jTextFieldname.getText());
            savaMsg("学号"+jTextFieldnum.getText());
            savaMsg("密码"+jPasswordField.getText());
            JOptionPane.showMessageDialog(null, "注册成功");
            dispose();
            new WindowLogin();
        } else if (e.getActionCommand().equals("返回")) {
            dispose();
        }
    }
}
