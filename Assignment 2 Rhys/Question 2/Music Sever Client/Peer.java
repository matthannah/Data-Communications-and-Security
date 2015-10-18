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
        
        //Get the folder which holds the peers songs
        File folder = new File("Songs");
        
        //Get a list of the filenames of the songs in that folder
        File[] listOfSongs = folder.listFiles();

        //Go through every string in the newly made array
        for (int i = 0; i < listOfSongs.length; i++) 
        {
            //Request the names of each file
            String songTitle = listOfSongs[i].getName();
            
            //Add the song to the song list
            songList.add(songTitle);
        }
        
        //Check the myName file for this peers name
        peerName = findName();
        
        //set to false by default until a connection is made
        online = false;
    }
    
    /**
     * Method to update the list of songs the peer has
     * 
     * @param       void
     * @return      String     Either the name found from the file or a message that a name is needed
     */
    public void updateSongs()
    {
        //Clear the song list so it can be updated
        songList.clear();
        
        //Get the folder which holds the peers songs
        File folder = new File("Songs");
        
        //Get a list of the filenames of the songs in that folder
        File[] listOfSongs = folder.listFiles();

        //Go through every string in the newly made array
        for (int i = 0; i < listOfSongs.length; i++) 
        {
            //Request the names of each file
            String songTitle = listOfSongs[i].getName();
            
            //Add the song to the song list
            songList.add(songTitle);
        }
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
                //Close the buffered reader
                buff.close();
                
                //Set the peers name equal to that line 
                return line;
            }
            //If line is null, file is empty
            else
            {
                //Close the buffered reader
                buff.close();
                
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
            //Creates an output stream so that the file can be written to 
            out = new FileOutputStream(nameFile);
            
            //Writes the data to the name file.
            out.write(dataToWrite);
            
            //Close the file output stream
            out.close();
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
            //Get rid of white space
            song = song.trim();
            
            //Add a "-" symbol and the song title to the string
            songsList = songsList + "-" + song;
        }
        
        //Check if the peer has any songs
        if(songList.equals(""))
        {
            //return a message that the peer does not have any songs
            return "-nosongs";
        }
        //If the peer does have songs
        else
        {
            //Return the string of all the songs
            return songsList;
        }
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
     * TCPRequestSong creates a TCP connection with a peer and exchanges message to receive a song file which was requested
     * 
     * @param     ip               the ip of the peer which the songs will be transfered from
     * @param     songRequested    the song requested
     * @return    void
     */
    public void TCPRequestSong(String ip, String songRequested) 
    {  
        //Used to hold a socket between this peer and they peer they are getting the song from
        Socket socket;
        
        //For writing to the output stream
        DataOutputStream out;
        
        //For reading from the input stream
        InputStream in;
        
        //Used to write the mp3 file given
        FileOutputStream fileOut;
        
        //An buffer for writing
        BufferedOutputStream buff;
        
        //Buffer with a variable size
        ByteArrayOutputStream baos;
        
        //Byte array of length 1
        byte[] aByte = new byte[1];
        
        //Integer version of the byte read so it can be checked
        int intOfByte;
        
        //The message that is sent to the other peers listener. Command GIVESONG follwed by the song wanted
        String message = "GIVESONG-" + songRequested + "\n"; 
        
        //Where the mp3 will be written
        final String path = "Songs/"+songRequested;
        
        //Attempt to get the file
        try 
        {
            //Intitialise the socket using the IP given and 9202 as the port. This is know to be where they are listening
            socket = new Socket(ip.trim(), 9203);  
            
            //Set out equal to the output stream of the socket
            out = new DataOutputStream(socket.getOutputStream()); 
            
            //Send the message, created above, to sockets output stream which means it can be read by the other peer
            out.writeBytes(message);
            
            //Tell the user that the song has been requested
            System.out.println("Requested " + songRequested + " from peer at " + ip.trim());
            
            //Initialise the byte array output stream
            baos = new ByteArrayOutputStream();
            
            //Get the input stream of the socket
            in = socket.getInputStream();
            
            //Create a file in the Songs directory with the same name as the song being requested
            fileOut = new FileOutputStream(path);
            
            //Create a buffer for writing to the file 
            buff = new BufferedOutputStream(fileOut);
            
            //Start reading from the input stream. This is what has been sent from the other peer
            intOfByte = in.read(aByte, 0, aByte.length);
            
            //Keep reading bytes and writing them to the byte array output stream until the whole file is read
            do 
            {
                //Write the next byte to the byte array output stream
                baos.write(aByte);
                
                //Read the next byte into the single byte array, aByte. Also set the int version so it can be checked
                intOfByte = in.read(aByte);
                
            } 
            //Check if the end of the file has been reached
            while (intOfByte != -1); 
            
            //Write the file to the buffered output stream effect writing the file
            buff.write(baos.toByteArray());
            
            //Makes sure that the writing to file in the line above is done completely
            buff.flush();
            
            //Close the programs access to the file
            buff.close();
            
            //Close the socket
            socket.close();
        }
        
        //If something went wrong while getting the file
        catch (IOException e)
        {
            //print the error message of type IOException
            System.err.println("IOException " + e);
        }
        
        //Let the user know that the song has been transfered
        System.out.println(songRequested + " was successfully transfered");
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
        
        //A boolean to store whether or not to exit the program and intitialise it as false
        Boolean exit = false;
        
        //Creates a new thread which runs the message listener. musicServer is passed in so results can be sent
        //back and requests made of the music server
        (new Thread(new Message_Listener(peer))).start();
        
        //Start up a thread to reply to song requests
        (new Thread(new Song_Request_Listener())).start();
        
        //Send a message to the server to let it know the peer is online
        peer.sendMessage("ONLINE-" + peer.getName());
        
        //Waiting for a connection message
        System.out.println("Waiting for server connection...");
        
        //Check if the online status has changed  
        while(!peer.getOnline())
        {
            //Do nothing. Waiting for the server to establish connection
            try 
            {
                //Wait for 100ms before checking if the server has responded
                Thread.sleep(100);
            }
            //If something goes wrong while trying to wait
            catch (Exception e)
            {
                //Print a message to the user to let them know th error
                System.err.println(e);
            }
        }
        
        //Connected message
        System.out.println("Connected");
        
        //Send the peers list of songs to make sure the srever is upto date
        peer.sendMessage("NEWSONGS" + peer.getSongList());
        
        //Loop until the user choses to exit
        while (!exit)
        {
            //Ask the user for an input via the console
            System.out.println("To view Available songs     press 1");
            
            //Ask the user for an input via the console
            System.out.println("To get a song               press 2");
            
            //Ask the user for an input via the console
            System.out.println("To update your song list    press 3");
            
            //Ask the user for an input via the console
            System.out.println("To exit the program         press 4");
            
            //Create an input stream to get user inputs from the console
            InputStreamReader isr = new InputStreamReader(System.in);
            
            //Create a buffer reader to read data gotten from the console
            BufferedReader br = new BufferedReader(isr);
            
            //Intitialise an int for the users input as 0. 
            //Needs to be intitialised incase reading input fails
            int input = 0;
            
            //Attempt to read from the console
            try
            {
                //Read the next line input by the user
                input = Integer.valueOf(br.readLine());
            }
            //If the reading fails
            catch(IOException e)
            {
                //Print error message to the console so the user knows
                System.err.println(e);
            }
            
            //Decide what to do based on the input from the user
            switch (input)
            {
                //If the user input is a 1 - View available songs
                case 1:                   
                    //Send a request to the server for all the songs
                    peer.sendMessage("GETALL-");
                
                    //Ignore the rest of the switch statement
                    break;
                
                //If the user input is a 2 - Get a song
                case 2:
                    //Ask the user for an input via the console
                    System.out.println("Please enter the title of the song you want");
                    
                    //Used to store the users song request
                    String songTitle = "";
                    //Attempt to read the song title the user wants
                    try
                    {
                        //Read the next line input by the user
                        songTitle = br.readLine();
                    }
                    //If the reading fails
                    catch(IOException e)
                    {
                        //Print error message to the console so the user knows
                        System.err.println(e);
                    }    
                
                    //Send a request to the server for the address of a peer who has that song
                    peer.sendMessage("GETSONG-" + songTitle);
                
                    //Ignore the rest of the switch statement
                    break;
                
                //If the user input is a 3 - Update peers song list
                case 3:
                    //Update the song list by re-reading the directory
                    peer.updateSongs();
                
                    //Send the server a new list of songs
                    peer.sendMessage("NEWSONGS" + peer.getSongList());
                
                    //Ignore the rest of the switch statement
                    break;
                
                //If the user input is a 4 - Exit the program
                case 4: 
                    //Set exit to true so that this is the last time the loop runs
                    exit = true;
                
                    //Ignore the rest of the switch statement
                    break;
                
                //If the input does not match any of the above cases
                default:
                    //Print message to the console to let the user know the input is not recognised
                    System.out.println("Not a recognised input");
            }
        }
    }
}