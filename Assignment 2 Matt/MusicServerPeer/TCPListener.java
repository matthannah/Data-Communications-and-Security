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
        try //change this to do exception handling for each area that needs it
        {
            String message;
            serverSocket = new ServerSocket(6789);
            while (listen)
            {
                Socket connectionSocket = serverSocket.accept();             
                BufferedReader inFromPeer = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));                          
                message = inFromPeer.readLine();
                System.out.println("received TCP message");
                if (message.startsWith("SendSong"))
                {
                    String parts[] = message.split(",");
                    System.out.println("...\nSending song " + parts[1] + " to: " 
                        + connectionSocket.getInetAddress().getHostName() + "\n...");
                        
                    //if i comment all  below out, the above works
                    File songFile = new File("songs/"+parts[1]);
                    byte[] byteArray = new byte[(int) songFile.length()]; //cast long to int
                    FileInputStream fis = new FileInputStream(songFile); //handle file not found exception???
                    BufferedInputStream bis = new BufferedInputStream(fis);
                    bis.read(byteArray, 0, byteArray.length);
                    OutputStream outToPeer = connectionSocket.getOutputStream();
                    System.out.println("Sending...");
                    outToPeer.write(byteArray, 0, byteArray.length);
                    outToPeer.flush(); //what does this do??
                    System.out.println("File sent size: " + songFile.length() + " bytes");                  
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
