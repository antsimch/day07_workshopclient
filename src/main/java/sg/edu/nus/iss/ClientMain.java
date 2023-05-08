package sg.edu.nus.iss;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ClientMain {
    
    public static void main(String[] args) throws IOException {
        
        Path p = Paths.get(args[0]);
        File fileToSend = p.toFile();

        String fileName = fileToSend.getName();
        long fileSize = fileToSend.length();

        System.out.printf(">>>> filename: %s, size %d\n", fileName, fileSize);

        Socket socket = new Socket("localhost", 3000);

        try {
            OutputStream os = socket.getOutputStream();
            BufferedOutputStream bos = new BufferedOutputStream(os);
            DataOutputStream dos = new DataOutputStream(bos);

            dos.writeUTF(fileName);
            dos.writeLong(fileSize);
            dos.flush();

            int size = 0;
            byte[] buffer = new byte[4096];
            InputStream is = new FileInputStream(fileToSend);
            BufferedInputStream bis = new BufferedInputStream(is);
            DataInputStream dis = new DataInputStream(bis);
            
            while ((size = dis.read(buffer)) > 0) {
                dos.write(buffer, 0, size);
            }
            
            dos.flush();

            InputStream inputStatus = socket.getInputStream();
            BufferedInputStream bufferedStatus = new BufferedInputStream(inputStatus);
            DataInputStream dataStatus = new DataInputStream(bufferedStatus);

            String status = dataStatus.readUTF();

            if ("ok".equalsIgnoreCase(status)) {
                System.out.println("File transferred successfully");
            } else {
                System.out.println("An error occurred during the file transfer. Please try again");
            }

            dos.writeUTF("close");
            dos.flush();

            os.close();
            is.close();
            inputStatus.close();         

        } finally {
            socket.close();
        }
    }
}

