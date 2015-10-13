import java.util.*;
/**
 * Write a description of class MusicServer here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class MusicServer
{
    PeerManager peerManager;

    /**
     * Constructor for objects of class MusicServer
     */
    public MusicServer()
    {
        peerManager = new PeerManager();
    }

    /**
     * An example of a method - replace this comment with your own
     * 
     * @param  y   a sample parameter for a method
     * @return     the sum of x and y 
     */
    public static void main(String args[])
    {
        MusicServer server = new MusicServer();
        (new Thread(new UDPListener(server))).start();
        System.out.println("Running...");
    }
    
    public PeerManager getPeerManager()
    {
        return peerManager;
    }    
}
