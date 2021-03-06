import java.util.concurrent.*;
import java.util.*;
import java.net.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;

/**
 * Peer is the seververs representation of a peer that is known and is responisble for 
 * keeping track of thier song list as well as whether or not they are online.
 * 
 * @author Rhys Hill 
 * @version 1.0
 */
public class Peer
{    
    //The name of the file that the peer uses to store it's song list
    private String listFileName;
    
    //A string array version of the song list to make it easier to pass between classes
    private ArrayList<String> songList;
    
    //The output stream for writing to the song list file
    private FileOutputStream out;
    
    //Boolean to represent whether or not the peer is online
    private Boolean online;
    
    //The address of that peer. Only important while the peer is online
    private String address;
    
    //Whether or not the client has sent it's latest heartbeat reply
    private Boolean heartBeating;

    /**
     * Constructor for objects of class Peer. The constructor sets the file name, sets the default
     * connection status to offline, and initialises the array list of songs, before updating the
     * list from file.
     * 
     * @param       fileName    Name of the file which holds, or will hold that peer's song list
     * @return      Peer 
     */
    public Peer(String fileName)
    {
        //Initialises listFileName with the name given to the constructor
        listFileName = fileName.trim();
        
        //By default the peer is set to offline
        online = false;
        
        //Default as an empty string
        address = "";
        
        //Initialise the songList as a new List. String qualifier not need as it implied above
        songList = new ArrayList<>();        
              
        //Updates the list of songs file
        updateSongListFile();
    }

    /**
     * Method to get the name of a particular peers song list file
     * 
     * @param       void
     * @return      String      listFileName
     */
    public String getName()
    {
        //return the name of the list file
        return listFileName;
    }
    
    /**
     * Updates the list of songs file for a particular peer 
     * 
     * @param       void
     * @return      void 
     */
    public void updateSongListFile()
    {
         //Attempt to read the peer list file
        try
        {
            //Create a new buffer and file reader for the peer list file
            BufferedReader buff = new BufferedReader(new FileReader(listFileName));
            
            //Create a string to store each line as it's read
            String line;
            
            //Read the next line of the file and check if there's anything there
            while ((line = buff.readLine()) != null) 
            {
                //Get rid of white space
                line = line.trim();
                
                //Checks if that song is already in the list of song
                if (!songList.contains(line))
                {
                    //Adds the song to the songs list
                    songList.add(line);
                    
                    System.out.println(listFileName + " " + line);
                }
            }
        }
        //If the reading above fails
        catch (Exception readError)        
        {
            //Print to console error message to tell user that the file coundn't be read
            System.out.println("PEER - An error occured trying to read file " + listFileName);
        }
        
        //Attempt to setup writing to the file
        try 
        {
            //Creates an output stream so that the file can be written to 
            out = new FileOutputStream(listFileName);
        }
        //If the above setup fails
        catch (Exception fileError)
        {
            //Print to console error message to tell user that the file coundn't be created
            System.out.println("PEER - An error occured trying to open file" + listFileName);
        }
        
        //Loop through all of the known peers
        for (String song : songList)
        {
            //Attempt to write to the peer list file all the peers
            try
            {
                //get the peers file name
                String dataToByte = song + "\r\n";
        
                //Converts the string into bytes.
                byte dataToWrite[] = dataToByte.getBytes();
        
                //Writes the data to the report file.
                out.write(dataToWrite);
            }
            catch(Exception e)
            {
                //Print to console error message to tell user that the file coundn't be made
                System.out.println("PEER - An error occured trying to create file " + listFileName);
            }
        }
        //Attempt to close the file output stream
        try
        {
            //Close the file output stream
            out.close();
        }
        //If it is unable to be closed
        catch(Exception closeError)
        {
            //Print error message to the console for the user
            System.err.println(closeError);
        }
    }
    
    /**
     * Method to set whether or not a peer is online
     * 
     * @param       ol      Whether or not this peer is online
     * @return      void
     */
    public void setOnline(Boolean ol)
    {
        //Set the online varriable
        online = ol;
    }
    
    /**
     * Method to set the current address of the peer
     * 
     * @param       add      The address of the peer
     * @return      void
     */
    public void setAddress(String add)
    {
        //Set the address for the peer
        address = add;
    }
    
    /**
     * Method to set whether or not a peer is online
     * 
     * @param       void
     * @return      Boolean    Whether or not this peer is online
     */
    public Boolean getOnline()
    {
        //Return the online varriable
        return online;
    }
    
    /**
     * Method to set the current address of the peer
     * 
     * @param       void
     * @return      String      The address of the peer
     */
    public String getAddress()
    {
        //Return the address for the peer
        return address;
    }
    
    /**
     * Scraps the old list of songs for a peer and replaces it with a new one provided by the client 
     * 
     * @param       newSongList     A string containing all of the songs that the peer has
     * @return      void 
     */
    public void changeSongList(String newSongList)
    {
        //Break the string from the client into an array of strings
        String[] songs = newSongList.split("-");
       
        //Clear the previous song list
        songList.clear();
        
        //Check if the new list has any songs
        if(!songs[1].equals("nosongs"))
        {
            //Create an array list with the songs given from the client
            ArrayList<String> songArrayList = new ArrayList<String>(Arrays.asList(songs));
            
            //Loop through the newly created array list
            for(String song : songArrayList)
            {
                //Get rid of white space
                song = song.trim();
                
                //Check for the message type so it can be ignored
                if(!song.equals("NEWSONGS"))
                {
                    //Add to the peers actual list of songs
                    songList.add(song);
                }
            }
                    
            //Attempt to setup writing to the file. This will clear the file contents
            try 
            {
                //Creates an output stream so that the file can be written to 
                out = new FileOutputStream(listFileName);
                
                //Close the file output stream
                out.close();
            }
            //If the above setup fails
            catch (Exception fileError)
            {
                //Print to console error message to tell user that the file coundn't be created
                System.out.println("PEER - An error occured trying to clear file" + listFileName);
            }
        }
        
        //Update the song list file
        updateSongListFile();
    }
    
    /**
     * Method to get the ArrayList that holds all the peers songs
     * 
     * @param       void
     * @return      ArrayList<String> A list of the peers songs
     */
    public ArrayList<String> getSongList()
    {
        //Return the address for the peer
        return songList;
    }
    
    /**
     * Heartbeat requested starts up a counter to see if the client responeds to a heartbeat in time.
     * The counter checks whether or not the heartbeating flag has been set true in time. If not it
     * sets that peer as offline
     * 
     * @param       void
     * @return      void
     */
    public void heartbeatRequested()
    {
        //Return the address for the peer
        heartBeating = false;
        
        //Starts up a thread to countdown until a client has taken too long to respond
        (new Thread(new Heartbeat_Counter(this))).start();
    }
    
    /**
     * Sets the heartbeating flag
     * 
     * @param       beating     Whether or not the peer has reponded to it's latest heartbeat
     * @return      void
     */
    public void setHeartbeating(Boolean beating)
    {
        //Sets the flag
        heartBeating = beating;
    }
    
    /**
     * Get the heartbeating flag
     * 
     * @param       beating     Whether or not the peer has reponded to it's latest heartbeat
     * @return      void
     */
    public Boolean heartBeating()
    {
        //Return the flag
        return heartBeating;
    }
}
