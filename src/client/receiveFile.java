package client;

import javax.swing.*;
import java.io.*;

public class receiveFile {
    /**
     * @ClassName: receiveFile
     * @Description: MYZHIBEI Chat Room Client Receive Files
     * @author Pengsj
     * @version V3.0
     * @Date 2019-12-06
     * All rights Reserved, Designed By Pengsj
     */
    private DataInputStream dis;

    public receiveFile(DataInputStream dis) {
        this.dis = dis;
        try {
            Object[] options = {"是", "否"};
            int result = JOptionPane.showOptionDialog(null, "Do you want to receive files?", "Receive File",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (result == JOptionPane.YES_OPTION) {
                System.out.println("==receivefile 1==");
                final JFileChooser chooser = new JFileChooser("Create a new file to accept the file");
                final int returnVal = chooser.showSaveDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    final File selectedfile = chooser.getSelectedFile();
                    String filePath = selectedfile.getAbsolutePath();
                    System.out.println("---" + filePath + "---");
                    System.out.println("==receivefile 2==");
                    DataOutputStream dos = new DataOutputStream(new FileOutputStream(selectedfile));
                    int m, len;
                    long l = selectedfile.length();
                    if ((l % 1024) != 0)
                        len = m = 1024;
                    else
                        len = m = 1023;
                    byte buffer[] = new byte[m];
                    int i = 0;
                    while (len >= m) {
                        len = this.dis.read(buffer, 0, m);
                        dos.write(buffer, 0, len);
                        dos.flush();
                        i++;
                        TalkClient.rec.append("\n" + "==receivefile " + i + "KB");
                    }
                    dos.close();
                    TalkClient.rec.append("\n---File download success!---\n");
                    System.out.println("==receivefile 4==");
                }
            } else {
                TalkClient.rec.append("\n" + "---Refused to receive the file!---");
                int m = 1023;
                int len = m;
                byte buffer[] = new byte[m];
                while (len >= m) {
                    len = this.dis.read(buffer, 0, m);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
