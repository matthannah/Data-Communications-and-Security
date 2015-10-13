import java.io.*;
import java.net.*;
/**
 * Write a description of class TCPListener here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class TCPListener implements Runnable
{
    private MusicServerPeer musicServerPeer;
    private ServerSocket serverSocket;
    private ServerSocket servsock;
    private boolean listen = true;
    
    /**
     * Constructor for objects of class TCPListener
     */
    public TCPListener(MusicServerPeer musicServerPeer)
    {
        this.musicServerPeer = musicServerPeer;
    }
    
    public void run()
    {
        try //change this to do exception handling for each area that needs it
        {
            String message;
            serverSocket = new ServerSocket(6789);
            servsock = new ServerSocket(12345);
            while (listen)
            {
                Socket connectionSocket = serverSocket.accept();  
                Socket sock = servsock.accept();
                BufferedReader inFromPeer = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));                          
                message = inFromPeer.readLine();
                System.out.println("received TCP message");
                if (message.startsWith("SendSong"))
                {
                    String parts[] = message.split(",");
                    System.out.println("...\nSending song " + parts[1] + " to: " 
                        + connectionSocket.getInetAddress().getHostName() + "\n...");
                    String songFile = "songs/"+parts[1];
                    sendSong(sock, songFile);               
                }
            }
            serverSocket.close();
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }
    
    public void sendSong(Socket sock, String songFile) throws Exception
    {
        File myFile = new File(songFile);
        byte[] mybytearray = new byte[(int) myFile.length()];
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(myFile));
        bis.read(mybytearray, 0, mybytearray.length);
        OutputStream os = sock.getOutputStream();
        os.write(mybytearray, 0, mybytearray.length);
        os.flush();
        sock.close();
    }

    public void finish()
    {
        listen = false;
        try
        {
            serverSocket.close();
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }
}
