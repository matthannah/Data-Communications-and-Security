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
 * The Music_Sever class is responsible for maintaining a list of peers and thier
 * song. It is also responsible for providing clients with a address to connect
 * to when they request songs.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Music_Sever
{
    //Create a new array list to store all of the peers the sever knows about
    private ArrayList<Peer> peerList;
    
    //The output stream for writing to the peer list file
    private FileOutputStream out;
          
    //The name of the file which holds the peers the sever knows about. This never changes
    private String fileName;
    // instance variables - replace the example below with your own
    
    public Music_Sever()
    {
        //Create a new array list to store all of the peers the sever knows about
        peerList = new ArrayList<>();
        
        //The name of the file which holds the peers the sever knows about. This never changes
        fileName = "peerList.txt";      
       
        //Update the peer list
        updatePeerListFile();
    }
    
    /**
     * An example of a method - replace this comment with your own
     * 
     * @param  y   a sample parameter for a method
     * @return     the sum of x and y 
     */
    public void updatePeerListFile()
    {
        //Attempt to read the peer list file
        try
        {
            //Create a new buffer and file reader for the peer list file
            BufferedReader buff = new BufferedReader(new FileReader(fileName));
            
            //Create a string to store each line as it's read
            String line;
            
            //Read the next line of the file and check if there's anything there
            while ((line = buff.readLine()) != null) 
            {
                //create a new peer with the it's file name equal to the line read
                Peer p = new Peer(line);
                
                //Checks if that peer is already in the list
                if (!peerList.contains(p))
                {
                    //Adds the peer to the list
                    peerList.add(p);
                }
            }
        }
        //If the reading above fails
        catch (Exception readError)        
        {
            //Print to console error message to tell user that the file coundn't be read
            System.out.println("SERVER - An error occured trying to read file " + fileName);
        }
            
        //Attempt to setup writing to file
        try 
        {
            //Creates an output stream so that the file can be written to 
            out = new FileOutputStream(fileName);
        }
        //If the above setup fails
        catch (Exception fileError)
        {
            //Print to console error message to tell user that the file coundn't be opened
            System.out.println("SERVER - An error occured trying to create file " + fileName);
        }
        
        //Loop through all of the known peers
        for (Peer peer : peerList)
        {
            //Attempt to write to the peer list file all the peers
            try
            {
                //get the peers file name
                String dataToByte = peer.getName() + "\r\n";
        
                //Converts the string into bytes.
                byte dataToWrite[] = dataToByte.getBytes();
        
                //Writes the data to the report file.
                out.write(dataToWrite);
            }
            //If the file isn't created properly
            catch(Exception e)
            {
                //Print to console error message to tell user that the file coundn't be made
                System.out.println("SERVER - An error occured trying to populate file " + fileName);
            }
        }
    }
    
    public static void main(String args[])
    {
        //Create a music sever
        Music_Sever musicServer = new Music_Sever();
        
    }
}
