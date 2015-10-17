import java.util.concurrent.*;
import java.util.*;
import java.net.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;

/**
 * The Music_Sever class is responsible for maintaining a list of peers and thier
 * song. It is also responsible for providing clients with a address to connect
 * to when they request songs.
 * 
 * @author Rhys Hill
 * @version 1.0
 */
public class Music_Sever
{
    //Create a new array list to store all of the peers the sever knows about
    private ArrayList<Peer> peerList;
    
    //The output stream for writing to the peer list file
    private FileOutputStream out;
          
    //The name of the file which holds the peers the sever knows about. This never changes
    private String fileName;
    
    /**
     * The constructor for the Music_sever class. Sets up the peer list as a new Array List
     * and then sets fileName to the text file used to store peers. This is always the same
     * which is why is can be set here. It then updates the peer list based on its file.
     * 
     * @param       void
     * @return      Music_Sever 
     */
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
     * Reads form file the list of peers that the server knows about and adds them to it's array list
     * which is used to keep track of peers through the program. Also establishes a connection to, or creates
     * if it's not present, the peer list text file.
     * 
     * @param       void
     * @return      void 
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
                if (!hasPeer(p))
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
        //Attempt to close the file
        try
        {
            //close the file output stream
            out.close();
        }
        catch (Exception closeError)
        {
            System.err.println(closeError);
        }
    }
    
    /**
     * Checks to see if the peer list already contains a peer with the same name.
     * 
     * @param       p           A newly created peer that will potentially be added to the peer list
     * @return      Boolean     A true false value to represent whether or not that peer name is listed  
     */
    public Boolean hasPeer (Peer peer)
    {
        //Loop goes through evry peer already in the peer list
        for (Peer p : peerList)
        {
            //Checks to see if the name of the next peer in the list matches the name of the peer being checked
            if (p.getName().equals(peer.getName()))
            {
                //Return true to say that the peer is already in the list
                return true;
            }
        }
        
        //If this function completes the above loop it returns false to say that the peer is not yet in the list
        return false;
    }
    
    /**
     * Searches the peer list for a particular peer
     * 
     * @param       peer    The name of a peer with in system
     * @return      Peer    The peer object for the peer with the searched for name    
     */
    public Peer getPeer(String peerName)
    {
        //Loop goes through evry peer already in the peer list
        for (Peer p : peerList)
        {
            //Checks to see if the name of the next peer in the list matches the name of the peer being checked
            if (p.getName().equals(peerName))
            {
                //Return the peer
                return p;
            }
        }
        
        //return some peer. Should never be reached
        return peerList.get(0);
    }
    
    /**
     * Searches the peer list for a particular peer based on its address 
     * 
     * @param       peer    The address of a peer with the system
     * @return      Peer    The peer object for the peer with the searched for address    
     */
    public Peer getPeerByAddress(String peerAddress)
    {
        //Gid rid of white space
        peerAddress = peerAddress.trim();
        
        //Get rid of the slash at the begining
        peerAddress = peerAddress.replace("/","");
        
        //Loop goes through evry peer already in the peer list
        for (Peer p : peerList)
        {
            //Checks to see if the name of the next peer in the list matches the name of the peer being checked
            if (p.getAddress().equals(peerAddress))
            {
                //Return the peer
                return p;
            }
        }
        
        //return some peer. Should never be reached
        return peerList.get(0);
    }
    
    /**
     * Checks if a peer is already know and if so sets them thier online to true. If not it gives them a new
     * name, adds it to the peer list. Then returns the name and listening port for that peer. 
     * 
     * @param   currentPeerName The suggested name of a peer. If it is unnamed this function will make a name 
     * @return  String          A true false value to represent whether or not that peer name is listed  
     */
    public String peerOnline(String currentPeerName, String address)
    {
        //Stores either the name of the peer given or the new unique name found
        String newName = "";
        
        //Get rid of white space
        currentPeerName = currentPeerName.trim();
        
        System.out.println(currentPeerName);
        
        //Checks to see if the peer is already know
        if (currentPeerName.equals("unnamed"))
        {  
            //Whether or not a unique name has been found for the peer
            Boolean uniqueName = false;
        
            //A counter which forms the end of a peers name
            int i = 0;
            
            //Loops until a unique name is found
            while (!uniqueName)
            {
                //Create a new peer based on the counter
                Peer p = new Peer("peer" + i + ".txt");
                
                //Checks if that peer is already in the list
                if (!hasPeer(p))
                {
                    //Adds the peer to the list
                    peerList.add(p);
                    
                    //Update the peer list
                    updatePeerListFile();
                    
                    //Says that a unique name has been found
                    uniqueName = true;
                    
                    //Sets the peer name
                    newName = "peer" + i + ".txt";
                }
                
                //Increment the counter
                i++;
            }
        }
        //If peer already has a name
        else
        {
            //Sets the peer name
            newName = currentPeerName;
        }
               
        //Get a refference to the peer in question
        Peer peer = getPeer(newName);
        
        //Flag as online
        peer.setOnline(true);
        
        //Set the address
        peer.setAddress(address.replace("/",""));
        System.out.println(address.replace("/",""));
        
        //Return the new name of the peer. This will be the same as what was given for known peers
        return newName;
    }
    
    /**
     * Tells the message listener whether or not it should keep listening
     * 
     * @param   void 
     * @return  Boolean     Whether or not the message listener should keep listening  
     */
    public Boolean listen()
    {
        //Always returns true. This is so that the message listener keeps waiting for new messages
        return true;
    }

    /**
     * Generates and returns a list of all the available songs
     * 
     * @param   void 
     * @return  String     A string containing all the songs available seperated by a "-" symbol  
     */
    public String getAllSongs()
    {
        //An array list to hold all the song titles without doubling up
        ArrayList<String> allSongsList = new ArrayList<>();
        
        //Used to store each of the peers list of songs as they are checked
        ArrayList<String> peerSongsList = new ArrayList<>();
        
        //Creates a string to hold a list of all the songs
        String allSongs = "";
        
        //Loop through all the peers in the peer list
        for (Peer peer : peerList)
        {
            //Get the song list for that peer
            peerSongsList = peer.getSongList();
            
            //Loop through all the songs on that list
            for (String song : peerSongsList)
            {
                //Get rid of white space
                song = song.trim();
                
                //Check if the song is already listed
                if (!allSongsList.contains(song))
                {
                    //If not listed, adds that song to the overall list
                    allSongsList.add(song);
                    
                    //Add the title to the end of the string
                    allSongs = allSongs + "-" + song;
                    
                    System.out.println(song);
                }
            }
        }

        //Returns the song list as one long string seperated by "-"
        return allSongs;
    }
    
    /**
     * Searches all peers for a particular song and then returns the address of a peer who has it
     * 
     * @param   songTitle   The title of a song that the client wants to download
     * @return  String      The address of a client who has that song 
     */
    public String getSong(String songTitle)
    {
        //Used to store each of the peers list of songs as they are checked
        ArrayList<String> peerSongsList = new ArrayList<>();
        
        //A string containing the addresses of all peers who have the song
        String addresses = "";
        
        //Loop through all the peers
        for (Peer peer : peerList)
        {
            //Set the peer song list being checked equal to that peers song list
            peerSongsList = peer.getSongList();
            
            //Check if the peer is online and if they have the song in thier song list 
            if (peer.getOnline() & peerSongsList.contains(songTitle))
            {
                //Creates a string to hold a list of all the songs
                addresses = addresses + "-" + peer.getAddress();
            }  
        }
        
        //If it gets to this point and the string is empty it means that the song couldn't be found. In this 
        //case it is instead made a string which will be recognised as such
        if (addresses.equals(""))
        {
            //Set addresses to show that the song couldn't be found
            addresses = "-notfound";
        }
        
        //return either a list of addresses or a "notfound" as a string
        return addresses;
    }
    
    public static void main(String args[])
    {
        //Create a music sever
        Music_Sever musicServer = new Music_Sever();
        
        //Creates a new thread which runs the message listener. musicServer is passed in so results can be sent
        //back and requests made of the music server
        (new Thread(new Message_Listener(musicServer))).start();
        
        //@TODO Check if online peers are still online
    }
}
