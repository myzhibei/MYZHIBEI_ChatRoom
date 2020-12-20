package chatServer;

import java.io.*;
import java.net.*;

public class ServerThread extends Thread {
    /**
     * @ClassName: ServerThread
     * @Description: MYZHIBEI Chat Room Server Thread For Each Client
     * @author Pengsj
     * @version V3.0
     * @Date 2019-12-06
     * All rights Reserved, Designed By Pengsj
     */
    private DataInputStream dis;
    private DataOutputStream dos;
    private Socket client;
    private boolean running;
    private String name;

    // 服务器线程初始化
    public ServerThread(Socket client) {
        this.client = client;
        try {
            dis = new DataInputStream(this.client.getInputStream());
            dos = new DataOutputStream(this.client.getOutputStream());
            running = true;
            this.name = receive();
            if (!this.name.equals("admin")) {
                this.send("\n---Welcome to MYZHIBEI chat room!---\n");
            } else {
                this.send("\n---Welcome to MYZHIBEI chat room!---\n"
                        + "---You can close the chat room server by send \"Close chat room\"!---\n");
            }
            sendEveryone(this.name + " has joined the chat room!", true, null, 0, false);
            for (ServerThread every : MultiTalkServer.totalserver) {
                if (!every.name.equals(this.name)) {
                    this.send("---" + every.name + " has joined the chat room!---");
                }
            }
        } catch (IOException e) {
            System.out.println("---Chat room failed to start!---");
        }
    }

    // 信息接收
    public String receive() {
        String msc = "";
        try {
            msc = dis.readUTF();
        } catch (IOException e) {
            System.out.println("---Receive Message Failed!---");
            running = false;
        }
        return msc;
    }

    // 接受文件
    public void receiveFile(String msc) {
        int m, len;
        len = m = 1024;
        byte buffer[] = new byte[m];// 读取输入流 ，一次读取1023字节
        try {
            while (len >= m) {
                len = dis.read(buffer, 0, m);
                sendSomeone(msc, buffer, len, true);
            }
            System.out.println("---File receive success!---");
        } catch (IOException e) {
            System.out.println("---File receive failed!---");
            e.printStackTrace();
        }
    }

    // 信息发送
    public void send(String msc) {
        try {
            dos.writeUTF(msc);
            dos.flush();
        } catch (IOException e) {
            System.out.println("---Send Message Failed!---");
            running = false;
        }
    }

    // 发送文件
    public void sendFile(byte[] buf, int buflen) {
        try {
            dos.write(buf, 0, buflen);
            dos.flush();
            System.out.println("==send file success!");
        } catch (IOException e) {
            System.out.println("---Send  File Failed!---");
            running = false;
        }
    }

    // 群发
    public void sendEveryone(String msc, boolean sys, byte[] buf, int buflen, boolean isFile) {
        for (ServerThread every : MultiTalkServer.totalserver) {
            if (!sys) {
                if (!every.name.equals(this.name)) {
                    if (isFile) {
                        System.out.println("---send everyone file---");
                        every.sendFile(buf, buflen);
                    } else {
                        if (msc.startsWith("file")) {
                            every.send(msc);
                        } else {
                            every.send(this.name + msc);
                        }
                    }
                }
            } else {
                every.send("---" + msc + "---");
            }
        }
    }

    // 私发
    public void sendSomeone(String msc, byte[] buf, int buflen, boolean isFile) {
        int idf;
        int ide;
        System.out.println("=== msc:\t" + msc);
        idf = msc.indexOf("@");
        ide = msc.indexOf(":");
        String toName = msc.substring(idf + 1, ide);
        System.out.println("---toname:\t" + toName + "---");
        if (toName.equals("all")) {
            System.out.println("---sendEveryone---");
            sendEveryone(msc, false, buf, buflen, isFile);
        } else {
            if (!msc.startsWith("file")) {
                msc = msc.substring(ide + 1);
            }
            for (ServerThread every : MultiTalkServer.totalserver) {
                if (every.name.equals(toName)) {
                    if (isFile) {
                        every.sendFile(buf, buflen);
                    } else {
                        if (!msc.startsWith("file")) {
                            every.send("---" + this.name + " sent you a message ---\n" + msc);
                        } else {
                            every.send("---" + this.name + " sent you a file ---\n");
                            every.send(msc);
                        }
                        break;
                    }
                }
            }
        }
    }

    // 线程主体
    public void run() {
        try {
            while (this.running) {
                String msc = receive();
                if (!msc.equals("")) {
                    if (msc.indexOf("Close chat room") != -1) {
                        sendEveryone(msc, true, null, 0, false);
                        sendEveryone("Chat Room Closed!", true, null, 0, false);
                        this.running = false;
                        MultiTalkServer.shutdown();
                    } else if (msc.startsWith("@")) {
                        sendSomeone(msc, null, 0, false);
                    } else {
                        if (msc.startsWith("file")) {
                            sendSomeone(msc, null, 0, false);
                            receiveFile(msc);
                            System.out.println("--recieve file end---");
                        } else {
                            sendEveryone(msc, false, null, 0, false);
                        }
                    }
                }
            }
            sendEveryone(this.name + " has quit the chat room!", true, null, 0, false);
            System.out.println("---" + this.name + " is disconnected---");
            MultiTalkServer.totalserver.remove(this);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("---Server running failed!---");
        }
    }
}
