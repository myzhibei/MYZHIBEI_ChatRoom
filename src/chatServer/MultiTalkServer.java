package chatServer;

import java.io.*;
import java.net.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class MultiTalkServer {
    /**
     * @ClassName: MultiTalkServer
     * @Description: MYZHIBEI Chat Room Server Main Thread
     * @author Pengsj
     * @version V3.0
     * @Date 2019-12-06
     * All rights Reserved, Designed By Pengsj
     */

    /**
     * 默认服务器IP为127.0.0.1，默认端口为5648
     * 管理员用户名为admin，可通过发送“Close chat room”关闭服务器
     */

    public static CopyOnWriteArrayList<ServerThread> totalserver = new CopyOnWriteArrayList<ServerThread>();
    private static ServerSocket ServerSoct;
    private static boolean running;

    public static void main(String args[]) throws IOException {
        ServerSoct = new ServerSocket(5648);//默认端口5648
        System.out.println("---The chat room server is up!---");
        running = true;
        while (running) {
            Socket client = ServerSoct.accept();
            System.out.println("---A client is connected!---");
            ServerThread c = new ServerThread(client);
            totalserver.add(c);
            new Thread(c).start();
        }
    }

    //关闭服务器
    public static void shutdown() {
        System.out.println("---Chat room closed!---");
        running = false;
        System.exit(0);
    }
}
