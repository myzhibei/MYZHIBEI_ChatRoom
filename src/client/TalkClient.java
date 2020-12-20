package client;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.io.IOException;
import java.net.Socket;

public class TalkClient extends JFrame {
    /**
     * FileName:     Small chat room system
     * @Description: Send messages and files between clients via GUI
     * All rights Reserved, Designed By Pengsj
     * Copyright:    ©2019 Pengsj All Rights Reserved.
     * @author: Pengsj
     * @version V3.0
     * Createdate:    2019-12-06
     * Modification  History:
     * Date         Author        Version        Discription
     * -----------------------------------------------------------------------------------
     * 2019-10-30   Pengsj          1.0      Send messages between clients
     * Why & What is modified: First Creation;Support for sending text only
     * 2019-11-27   Pengsj          2.0      Send messages and files between clients
     * Why & What is modified: Added support for sending files
     * 2019-12-06   Pengsj          3.0      Send messages and files between clients via GUI
     * Why & What is modified: Added support for GUI
     */

    /**
     * @ClassName: TalkClient
     * @Description: MYZHIBEI Chat Room Client Main Thread
     * @author Pengsj
     * @version V3.0
     * @Date 2019-12-06
     * All rights Reserved, Designed By Pengsj
     */

    /**
     * 默认服务器IP为127.0.0.1，默认端口为5648
     * 管理员用户名为admin，可通过发送“Close chat room”关闭服务器
     */
    private static final long serialVersionUID = -9162169540500612942L;
    private static String name;
    private static Socket client;
    protected static DefaultListModel<String> NamesModel;
    protected static JTextArea rec;
    protected static JTextArea msg;
    protected static JButton sendM;
    protected static JButton sendF;
    protected static JLabel ToNm;

    public TalkClient() {

        setTitle("MYZHIBEI Chat Room");
        ImageIcon ic = new ImageIcon("zhibei.png");
        setIconImage(ic.getImage());
        setSize(1118, 775);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        Container ct = getContentPane();
        ct.setLayout(null);


        sendM = new JButton();
        sendM.setText("Send Message");
        sendM.setFont(new Font("微软雅黑", Font.BOLD, 20));
        sendM.setBackground(new Color(55, 55, 55));
        sendM.setForeground(Color.white);
        sendM.setFocusPainted(false);
        sendM.setBounds(new Rectangle(775, 640, 300, 60));
        ct.add(sendM);

        sendF = new JButton();
        sendF.setText("Send File");
        sendF.setFont(new Font("微软雅黑", Font.BOLD, 20));
        sendF.setBackground(new Color(55, 55, 55));
        sendF.setForeground(Color.white);
        sendF.setFocusPainted(false);
        sendF.setBounds(new Rectangle(775, 565, 300, 60));
        ct.add(sendF);

        JLabel cts = new JLabel("	Current chat room member	");
        cts.setFont(new Font("微软雅黑", Font.BOLD, 19));
        cts.setBounds(new Rectangle(775, 30, 300, 45));
        ct.add(cts);

        JLabel MsgTn = new JLabel("Send To:");
        MsgTn.setFont(new Font("微软雅黑", Font.BOLD, 19));
        MsgTn.setBounds(new Rectangle(775, 520, 100, 45));
        ct.add(MsgTn);

        ToNm = new JLabel();
        ToNm.setForeground(new Color(35, 70, 82));
        ToNm.setFont(new Font("微软雅黑", Font.BOLD, 19));
        ToNm.setBounds(new Rectangle(895, 520, 200, 45));
        ct.add(ToNm);

        msg = new JTextArea();
        msg.setColumns(10);
        msg.setRows(10);
        msg.setFont(new Font("微软雅黑", Font.PLAIN, 17));
        JScrollPane Msg = new JScrollPane(msg);
        Msg.setBounds(new Rectangle(30, 510, 713, 200));
        ct.add(Msg);

        rec = new JTextArea();
        rec.setFont(new Font("微软雅黑", Font.PLAIN, 17));
        rec.setEditable(false);
        JScrollPane Rec = new JScrollPane(rec);
        Rec.setBounds(new Rectangle(30, 30, 713, 460));
        ct.add(Rec);

        String names[] = {"all"};
        NamesModel = new DefaultListModel<>();
        for (String tmp : names) {
            NamesModel.addElement(tmp);
        }
        JList<String> contacts = new JList<>();
        contacts.setModel(NamesModel);
        contacts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane Contacts = new JScrollPane(contacts);
        Contacts.setBounds(new Rectangle(775, 65, 300, 420));
        contacts.setFont(new Font("微软雅黑", Font.BOLD, 19));
        ct.add(Contacts);
        contacts.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                ToNm.setText(contacts.getSelectedValue());
            }
        });
        contacts.setSelectedIndex(0);
        setVisible(true);
        validate();
    }

    public static void main(String args[]) {
        new TalkClient();
        name = JOptionPane.showInputDialog(null, "Please input your user name", "User name",
                JOptionPane.QUESTION_MESSAGE);
        msg.setText("");
        NamesModel.addElement(name);
        System.out.println("name:" + name);
        try {
            client = new Socket("127.0.0.1", 5648);//默认端口5648，默认服务器IP为本机
        } catch (IOException e) {
            TalkClient.rec.append("\n" + "---Join failed!---");
        }
        new Thread(new Send(client, name)).start();
        new Thread(new Receive(client)).start();
    }

    // 关闭
    public static void shutdownClient() {
        try {
            client.close();
            TalkClient.rec.append("\n" + "---Client closed!---");
            System.exit(0);
        } catch (IOException e) {
            TalkClient.rec.append("\n" + "---Client shutdown exception!---");
        }
    }
}

