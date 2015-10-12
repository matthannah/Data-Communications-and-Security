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
        try 
        {
            String message;
            String capitalizedSentence;
            serverSocket = new ServerSocket(6789);
            while (listen)
            {
                Socket connectionSocket = serverSocket.accept();             
                BufferedReader inFromPeer = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));             
                DataOutputStream outToPeer = new DataOutputStream(connectionSocket.getOutputStream());             
                message = inFromPeer.readLine();
                if (message.startsWith("SendSong"))
                {
                    String parts[] = message.split(",");
                    System.out.println("...\nSending song " + parts[1] + " to: " 
                        + connectionSocket.getInetAddress().getHostName() + "\n...");
                }
            }
            serverSocket.close();
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
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
