package client;

import javax.swing.*;
import java.io.*;

public class sendFile {
    /**
     * @ClassName: sendFile
     * @Description: MYZHIBEI Chat Room Client Send Files
     * @author Pengsj
     * @version V3.0
     * @Date 2019-12-06
     * All rights Reserved, Designed By Pengsj
     */
    private DataOutputStream dos;

    public sendFile(DataOutputStream dos) {

        this.dos = dos;
        try {
            System.out.println("==sendfile 1==");
            final JFileChooser chooser = new JFileChooser("Select the file to send");
            final int returnVal = chooser.showOpenDialog(TalkClient.sendF);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                final File selectedfile = chooser.getSelectedFile();
                String filePath = selectedfile.getAbsolutePath();
                try {
                    dos.writeUTF("file@" + TalkClient.ToNm.getText() + ":" + selectedfile.getName());
                    dos.flush();
                } catch (IOException e) {
                    TalkClient.rec.append("\n" + "---Failed to send the file name!---");
                }
                System.out.println("===file===");
                System.out.println("---" + filePath + "---");
                System.out.println("==sendfile 2==");
                DataInputStream dfis = new DataInputStream(new FileInputStream(selectedfile));
                int m, len;
                long l = selectedfile.length();
                if ((l % 1024) != 0)
                    len = m = 1024;
                else
                    len = m = 1023;
                byte buffer[] = new byte[m];
                int i = 0;
                while (len >= m) {
                    len = dfis.read(buffer, 0, m);
                    this.dos.write(buffer, 0, len);
                    this.dos.flush();
                    i++;
                    TalkClient.rec.append("\n" + "==sendfile " + i + "KB/" + l + "KB ==");
                }
                dfis.close();
                TalkClient.rec.append("\n" + "---File updownload success!---\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
