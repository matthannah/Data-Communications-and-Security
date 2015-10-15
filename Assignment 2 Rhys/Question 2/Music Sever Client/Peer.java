import java.util.concurrent.*;
import java.util.*;
import java.net.*;
import java.io.*;

/**
 * Peer is the clients representation of a peer that is known and is responisble for 
 * keeping track of thier song list as well as whether or not they are online.
 * 
 * @author Rhys Hill 
 * @version 1.0
 */
public class Peer
{    
    //Stores the name of the file which contains the unique peer name name given by the server
    private String nameFile;
    
    //The name of the peer as decided by the server
    private String peerName;
    
    //A string array version of the song list to make it easier manage and search
    private ArrayList<String> songList;
    
    //The output stream for writing to the song list file
    private FileOutputStream out;
    
    //Boolean to represent whether or not the peer is online
    private Boolean online;
    
    //The address of the server
    private String serverAddress;

    /**
     * Constructor for objects of class Peer. The constructor sets the file name, sets the default
     * connection status to offline, and initialises the array list of songs, before updating the
     * list from file.
     * 
     * @param       fileName    Name of the file which holds, or will hold that peer's song list
     * @return      Peer 
     */
    public Peer()
    {
        //Initialises name file as myName.txt. This is always the same and file may contain a name or be empty 
        nameFile = "myName.txt";
               
        //Default as an empty string
        serverAddress = "";
        
        //Initialise the songList as a new List. String qualifier not need as it is implied above
        songList = new ArrayList<>();
        songList.add("Song2.mp3");
        
        //Check the myName file for this peers name
        peerName = findName();
        
        //set to false by default until a connection is made
        online = false;
    }
    
    /**
     * Method to find the name of the peer or set it equal to unnamed
     * 
     * @param       void
     * @return      String     Either the name found from the file or a message that a name is needed
     */
    public String findName()
    {
        //Attempt to read the peer name file
        try
        {
            //Create a new buffer and file reader for the peers name file
            BufferedReader buff = new BufferedReader(new FileReader(nameFile));
            
            //Create a string to store the line read
            String line;
            
            //Read the first line of the file and check if there's anything there
            if((line = buff.readLine()) != null) 
            {
                //Set the peers name equal to that line 
                return line;
            }
            //If line is null, file is empty
            else
            {
                //set name as unnamed
                return "unnamed";
            }
        }
        
        //If the reading above fails
        catch (Exception readError)        
        {
            //Print to console error message to tell user that the file coundn't be read
            System.out.println("PEER - User is new to the system. Creating file " + nameFile + "...");
            
            //Attempt to setup writing to the file
            try 
            {
                //Creates an output stream so that the file can be written to 
                out = new FileOutputStream(nameFile);
            }
            //If the above setup fails
            catch (Exception fileError)
            {
                //Print to console error message to tell user that the file coundn't be created
                System.out.println("PEER - An error occured trying to open file" + nameFile);
            }
        }
        //If a new file needed to be made unnamed still needs to be returned
        return "unnamed";
    }

    /**
     * Method to get the name of the peer
     * 
     * @param       void
     * @return      String      listFileName
     */
    public String getName()
    {
        //return the name of the list file
        return peerName;
    }
    
    /**
     * Method to set the name of the peer and attempt to write it to file
     * 
     * @param       name        The name that is to be used as the peers name
     * @return      void
     */
    public void setName(String name)
    {
        //Set the name of the peer as given
        peerName = name;
        
        //Create byte version of name to write to file
        byte[] dataToWrite = peerName.getBytes();
        
        //Attempt to write name to file
        try
        {
            //Writes the data to the name file.
            out.write(dataToWrite);
        }
        //If the write above fails
        catch (Exception nameFile)
        {
            //Print an error message
            System.out.println("Couldn't write to name file");
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
     * Method to set the current address of the server
     * 
     * @param       add      The address of the peer
     * @return      void
     */
    public void setAddress(String add)
    {
        //Set the address for the peer
        serverAddress = add;
    }
    
    /**
     * Method to get whether or not a peer is online
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
     * Method tells the listener whether or not to keep listening
     * 
     * @param       void
     * @return      Boolean    Whether or not to keep listening
     */
    public Boolean listen()
    {
        //Always returns true as listener should always listen
        return true;
    }
    
    /**
     * Method to get the server address
     * 
     * @param       void
     * @return      String      The address of the peer
     */
    public String getAddress()
    {
        //Return the address of the server
        return serverAddress;
    }
    
    /**
     * Method to get the String containing all of the peers songs
     * 
     * @param       void
     * @return      String      All the peers songs seperated by "-"
     */
    public String getSongList()
    {
        //Create a string to hold the song list
        String songsList = "";
        
        //Loop through all the peers songs
        for (String song : songList)
        {
            //Add a "-" symbol and the song title to the string
            songsList = songsList + "-" + song;
        }
        
        //Return the string of all the songs
        return songsList;
    }
    
    /**
     * Attempt to send a message to the server
     * 
     * @param       message     The string message that is to be sent to the server
     * @return      void
     */
    public void sendMessage (String message)
    {
        //Attempt to send a message to the server
        try
        {
            //Create a new socket that is different to the one for receiving messages
            DatagramSocket messageSocket = new DatagramSocket();   
            
            System.out.println("Made socket");
            
            //Byte array to store the message being sent
            byte[] dataToWrite = new byte[1024]; 
            
            //Convert the message into its byte array form so it can be sent
            dataToWrite = message.getBytes();                   
            
            //Create a packet for the message using the address of the server and the known port number
            DatagramPacket sendablePacket = new DatagramPacket(dataToWrite, dataToWrite.length, InetAddress.getByName(getAddress()), 9001);                   
            
            //Send the newly created packet
            messageSocket.send(sendablePacket); 
            
            //Close the new socket
            messageSocket.close();
        }
        //Message could not be sent
        catch (Exception send)
        {
            System.err.println(send);
        }
    }
    
    /**
     * Controls the console interactions with the user
     * 
     * @param       args    An array of strings. The first of these is all thats used and is the server IP
     * @return      void
     */
    public static void main(String args[])
    {              
        //Create a music sever
        Peer peer = new Peer();
        
        //Gets the address of the server as an inout from the user
        peer.setAddress(args[0]);
        
        //Creates a new thread which runs the message listener. musicServer is passed in so results can be sent
        //back and requests made of the music server
        (new Thread(new Message_Listener(peer))).start();
        
        //Send a message to the server to let it know the peer is online
        peer.sendMessage("ONLINE-" + peer.getName());
        
        //Waiting for a connection message
        System.out.println("Waiting for server connection...");
        
        //Check if the online status has changed  
        while(!peer.getOnline())
        {
            //Do nothing. Waiting for the server to establish connection
        }
        
        //Waiting for a connection message
        System.out.println("Connected");
        
        //Send the peers list of songs to make sure the srever is upto date
        peer.sendMessage("NEWSONGS" + peer.getSongList());
        
        //Ask the user for an input via the console
        System.out.println("To view Available songs     press 1");
        
        //Ask the user for an input via the console
        System.out.println("To view get a song          press 2");
        
        //Ask the user for an input via the console
        System.out.println("To update your song list    press 3");
        
        InputStreamReader isr = new InputStreamReader(System.in);
        
        BufferedReader br = new BufferedReader(isr);
        
        int input = 0;
        
        try
        {
            input = Integer.valueOf(br.readLine());
        }
        catch(IOException e)
        {
            System.err.println(e);
        }
        
        switch (input)
        { 
            case 1: 
            System.out.println("1");
            break;
            case 2: 
            System.out.println("2");
            break;
            case 3: 
            System.out.println("3");
            break;
            default:
            System.out.println("6");
        }
    }
}