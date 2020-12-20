package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Send implements Runnable {
    /**
     * @ClassName: Send
     * @Description: MYZHIBEI Chat Room Client Sending Thread
     * @author Pengsj
     * @version V3.0
     * @Date 2019-12-06
     * All rights Reserved, Designed By Pengsj
     */
    private Socket client;
    private DataOutputStream dos;
    private String name;
    private String msc = "";

    public Send(Socket client, String name) {
        this.client = client;
        this.name = name;
        try {
            msc = TalkClient.msg.getText();
            dos = new DataOutputStream(this.client.getOutputStream());
        } catch (IOException e) {
            TalkClient.rec.append("\n" + "---Message sending connection failed!---");
            try {
                dos.close();
            } catch (IOException e1) {
                TalkClient.rec.append("\n" + "---console or dos close exception!---");
            }
        }
        send(this.name);

    }

    public void send(String msc) {
        try {
            dos.writeUTF(msc);
            dos.flush();
        } catch (IOException e) {
            TalkClient.rec.append("\n" + "---Message failed to send!---");
        }
    }

    public void run() {
        TalkClient.sendM.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                msc = TalkClient.msg.getText();
                TalkClient.msg.setText("");
                TalkClient.rec.append("\n" + "@" + TalkClient.ToNm.getText() + ":" + msc + "\n");
                send("@" + TalkClient.ToNm.getText() + ":" + msc);
            }
        });
        TalkClient.sendF.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new sendFile(dos);
            }
        });
    }
}
