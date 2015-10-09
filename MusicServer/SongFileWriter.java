import java.util.*;
import java.io.*;
/**
 * Write a description of class SongFileWriter here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class SongFileWriter
{
    ArrayList<String> songs;

    /**
     * Constructor for objects of class SongFileWriter
     */
    public SongFileWriter(ArrayList<String> songs)
    {
        this.songs = songs;
    }

    public void write()
    {
        PrintWriter myOutput;
        try
        {
            FileOutputStream fos = new FileOutputStream("songs.txt");
            myOutput = new PrintWriter(fos, true);
            for (String song : songs)
            {
                myOutput.println(song);
            }
            myOutput.close();
        }
        catch(IOException e)
        {
            System.err.println(e);
        }
    }
}
