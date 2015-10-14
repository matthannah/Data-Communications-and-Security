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

    synchronized public void write(ArrayList<String> songs)
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
