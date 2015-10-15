import java.util.*;
import java.io.*;
/**
 * SongFileWriter
 * 
 * @author Matthew Hannah
 * @version 14/10/2015
 */
public class SongFileWriter
{

    /**
     * Constructor for objects of class SongFileWriter
     */
    public SongFileWriter()
    {

    }

    /**
     * write -      writes songs received to the file songs.txt, synchronized because we don't want
     *              two threads to be writing to the same file at the same time (will cause exceptions)
     * 
     * @param       ArrayList<Peer> - all peers
     */
    synchronized public void write(ArrayList<Peer> peers)
    {
        //a print writer object which writes objects to a text output stream
        PrintWriter myOutput;
        //enclose code that might throw an exception in a try block
        try
        {
            //create a new file output stream object and set the pathname to songs.txt
            FileOutputStream fos = new FileOutputStream("songs.txt");
            //wrap the file output stream in the print writer and set auto flush to true; this will
            //force bytes out of the stream
            myOutput = new PrintWriter(fos, true);
            //create a new Set of strings to hold the songs temporarily, hashset because we don't want
            //duplicate elements
            Set<String> songs = new HashSet<String>();
            //loop thorugh the peers array list
            for (Peer peer : peers)
            {
                //loop through all the songs the peer has
                for (String song : peer.getSongList())
                {
                    //add the song to the temp set of songs
                    songs.add(song);
                }
            }
            //for all the songs in the set
            for (String song : songs)
            {
                //write the songs to the output stream
                myOutput.write(song);
            }
            //close the output stream
            myOutput.close();
        }
        //enclose exception handling code in a catch block
        catch (IOException e)
        {
            //print the error message of type IOException
            System.err.println("IOException " + e);
        }
    }
}
