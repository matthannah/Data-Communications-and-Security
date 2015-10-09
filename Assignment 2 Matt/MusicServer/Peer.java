import java.util.*;
/**
 * Write a description of class Peer here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Peer
{
    // instance variables - replace the example below with your own
    private String ip;
    private ArrayList<String> songs;

    /**
     * Constructor for objects of class Peer
     */
    public Peer(String address)
    {
        ip = address;
        songs = new ArrayList<String>();
    }

    /**
     * An example of a method - replace this comment with your own
     * 
     * @param  y   a sample parameter for a method
     * @return     the sum of x and y 
     */
    public String getIP()
    {
        return ip;
    }
    
    public ArrayList<String> getSongList()
    {
        return songs;
    }
    
    public void addSong(String song)
    {
        songs.add(song);
    }
    
    public boolean hasSong(String songRequested)
    {
        for (String song : songs)
        {
            if (song.equals(songRequested))
            {
                return true;
            }
        }
        return false;
    }
}
