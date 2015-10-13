import java.util.concurrent.*;
import java.util.*;
import java.net.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;

/**
 * Peer is the seververs representation of a peer that is known and thier song list
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

    /**
     * Constructor for objects of class Peer
     */
    public Peer(String fileName)
    {
        //Initialises listFileName with the name given to the constructor
        listFileName = fileName;
        
        //By default the peer is set to offline
        online = false;
        
        //Initialise the songList as a new List. String qualifier not need as it implied above
        songList = new ArrayList<>();        
              
        //Updates the list of songs file
        updateSongListFile();
    }

    /**
     * Method to get the name of a particular peers song list file
     * 
     * @param       void
     * @return      String - listFileName
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
                //Checks if that song is already in the list of song
                if (!songList.contains(line))
                {
                    //Adds the song to the songs list
                    songList.add(line);
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
    }
}
