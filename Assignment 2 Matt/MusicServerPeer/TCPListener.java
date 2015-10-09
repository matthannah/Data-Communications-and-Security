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
    MusicServerPeer musicServerPeer;
    boolean listen = true;
    
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
            ServerSocket serverSocket = new ServerSocket(6789);
            while (listen)
            {
                Socket connectionSocket = serverSocket.accept();             
                BufferedReader inFromPeer = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));             
                DataOutputStream outToPeer = new DataOutputStream(connectionSocket.getOutputStream());             
                message = inFromPeer.readLine();             
                System.out.println("Received: " + message);             
                capitalizedSentence = message.toUpperCase() + '\n';             
                outToPeer.writeBytes(capitalizedSentence); 
            }
            serverSocket.close();
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }

}
