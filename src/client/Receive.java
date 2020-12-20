package client;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class Receive implements Runnable {
    /**
     * @ClassName: Receive
     * @Description: MYZHIBEI Chat Room Client Receiving Thread
     * @author Pengsj
     * @version V3.0
     * @Date 2019-12-06
     * All rights Reserved, Designed By Pengsj
     */
    private DataInputStream dis;
    private Socket client;
    private String msc;
    private boolean running;

    public Receive(Socket client) {
        this.client = client;
        try {
            dis = new DataInputStream(this.client.getInputStream());
            this.running = true;
        } catch (IOException e) {
            TalkClient.rec.append("\n" + "---Receive Failed!---");
            running = false;
        }
    }

    public void AddClientName(String msc) {
        int nameend = msc.indexOf(" has joined the chat room!");
        if (nameend != -1) {
            TalkClient.NamesModel.addElement((String) msc.subSequence(3, nameend));
            return;
        }
        nameend = msc.indexOf(" has quit the chat room!");
        if (nameend != -1) {
            TalkClient.NamesModel.removeElement((String) msc.subSequence(3, nameend));
            return;
        }
    }

    public void run() {
        while (running) {
            try {
                msc = dis.readUTF();
                TalkClient.rec.append(msc + "\n");
                AddClientName(msc);
                if (msc.startsWith("file")) {
                    System.out.println("===main receiving file");
                    new receiveFile(this.dis);
                } else {
                    if (msc.equals("---Chat Room Closed!---")) {
                        this.running = false;
                        dis.close();
                        TalkClient.shutdownClient();
                    }
                }
            } catch (IOException e) {
                TalkClient.rec.append("\n" + "---Receive Failed!---");
                running = false;
            }
        }
    }

}