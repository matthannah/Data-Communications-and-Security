import java.util.*;
/**
 * Peer - the peer object containing all necessary information about a peer
 * 
 * @author Matthew Hannah
 * @version 14/10/2015
 */
public class Peer
{
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
     * getIP
     * 
     * @return     String ip - the ip of the peer
     */
    public String getIP()
    {
        return ip;
    }
    
    /**
     * getISongList
     * 
     * @return     ArrayList<String> songs - the peers song list
     */
    public ArrayList<String> getSongList()
    {
        return songs;
    }
    
    /**
     * addSong - adds the song to the peers list
     * 
     * @param  - String song - the song to add
     */
    public void addSong(String song)
    {
        songs.add(song);
    }
    
    /**
     * hasSong - checks if the peer has the song specified
     * 
     * @param  - String songRequested - the song to check
     * @return - whether or not the peer has the song (yes = true, no = false)
     */
    public boolean hasSong(String songRequested)
    {
        //loops through the songs
        for (String song : songs)
        {
            //if the peer has the song requested
            if (song.equals(songRequested))
            {
                //return true; the peer has the song
                return true;
            }
        }
        //return false; the peer does not have the song
        return false;
    }
}
