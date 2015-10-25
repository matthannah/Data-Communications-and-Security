import java.util.*;
import java.io.*;

/**
 * Music Server - management of peers and processes UDP requests/responses
 * 
 * @author Matthew Hannah
 * @version 14/10/2015
 */
public class MusicServer
{
    private PeerManager peerManager;
    private SongFileWriter songFileWriter;
    private boolean running = true;

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
        while(server.running)
        {
            //while the program is running show the menu to the user
            server.menu();
        }
    }
    
    /**
     * menu -   The user menu which allows the user to execute several functions
     *          of the program
     * 
     */
    public void menu()
    {
        //create a new input stream reader which converts bytes entered by the user
        //at the command line, and convertes these bytes into chracters
        InputStreamReader isr = new InputStreamReader(System.in);
        //wrap the input stream reader in a buffered reader which reads from the
        //character stream and buffers characters into readable strings
        BufferedReader br = new BufferedReader(isr);
        //int that will hold the users input entered
        int input = 0;
        //print the menu to assist the user in executing program functionality
        System.out.println("");
        System.out.println("Select functionality: ");
        System.out.println("1: Show Peers Online | 2: Show All Songs");
        //enclose code that might throw an exception in a try block
        try
        {
            //use the buffered readers method read line to read a line of text
            //which is then cast to an int and set equal to the input; a line of text is one that
            //end in a '\n' (in the users cae pressing the carriage return key)
            input = Integer.valueOf(br.readLine());
        }
        //enclose exception handling code in a catch block
        catch (IOException e)
        {
            //print the error message of type IOException
            System.err.println("IOException " + e);
        }
        //switch statement for different values of input
        switch (input) 
        {
            //Show Peers Online
            case 1:
                //show peers online
                showOnline();
                break;
            //Show All Songs
            case 2:
                //showAllSongs
                showSongs();
                break;
            //Do nothing
            default:
        }
    }
    
    /**
     * showOnline -     shows peers online
     * 
     */
    public void showOnline()
    {
        System.out.println("");
        System.out.println("----- PEERS ONLINE -----");
        for (Peer peer : peerManager.getPeerList())
        {
            System.out.println(peer.getIP());
        }
        System.out.println("");
    } 
    
    /**
     * showSongs -     shows peers songs
     * 
     */
    public void showSongs()
    {
        System.out.println("");
        System.out.println("----- ALL SONGS -----");
        for (Peer peer : peerManager.getPeerList())
        {
            System.out.println("");
            System.out.println(peer.getIP());
            System.out.println("--------------------");
            for (String song : peer.getSongList())
            {
                System.out.println(song);
            }
        }
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
