import java.io.*;
import java.net.*;
/**
 * Write a description of class FileTransferClient here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class FileTransferClient
{
    public static void main(String args[]) throws Exception 
    {
        Socket sock = new Socket("127.0.0.1", 12345);
        byte[] mybytearray = new byte[1024];
        InputStream is = sock.getInputStream();
        FileOutputStream fos = new FileOutputStream("hello.txt");
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        int bytesRead = is.read(mybytearray, 0, mybytearray.length);
        bos.write(mybytearray, 0, bytesRead);
        bos.close();
        sock.close();
    }
}
