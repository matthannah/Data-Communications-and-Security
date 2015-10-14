import java.util.*;
/**
 * Music Server - management of peers and processes UDP requests/responses
 * 
 * @author Matthew Hannah
 * @version 14/10/2015
 */
public class MusicServer
{
    PeerManager peerManager;
    SongFileWriter songFileWriter;

    /**
     * Constructor for objects of class MusicServer
     */
    public MusicServer()
    {
        peerManager = new PeerManager();
        songFileWriter = new SongFileWriter();
    }

    /**
     * main -   starts the server
     * 
     */
    public static void main(String args[])
    {
        MusicServer server = new MusicServer();
        (new Thread(new UDPListener(server))).start();
        System.out.println("Running...");
    }
    
    /**
     * getPeerManager -     gets the peer manager
     * 
     * @return -            PeerManager - returns the peer manager
     */
    public PeerManager getPeerManager()
    {
        return peerManager;
    }  
    
    /**
     * getSongFileWriter -  gets the song file writer
     * 
     * @return -            SongFileWriter - returns the song file writer
     */
    public SongFileWriter getSongFileWriter()
    {
        return songFileWriter;
    }    
}
