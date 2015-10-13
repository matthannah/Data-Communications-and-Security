import java.io.*;
import java.net.*;

/**
 * Write a description of class FileTransferServer here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class FileTransferServer
{
    public static void main(String args[]) throws IOException
    {
        ServerSocket servsock = new ServerSocket(12345);
        File myFile = new File("hello.txt");
        while (true) 
        {
            Socket sock = servsock.accept();
            byte[] mybytearray = new byte[(int) myFile.length()];
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(myFile));
            bis.read(mybytearray, 0, mybytearray.length);
            OutputStream os = sock.getOutputStream();
            os.write(mybytearray, 0, mybytearray.length);
            os.flush();
            sock.close();
        }
    }
}
