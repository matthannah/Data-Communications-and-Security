import java.io.*;
import java.net.*;
/**
 * Write a description of class Song_Request_Listener here.
 * 
 * @author      Rhys Hill
 * @version     1.0
 */
public class Song_Request_Listener implements Runnable
{
    //The socket on which the peer will listen for song requests
    private ServerSocket serverSocket;
    
    //A refference to the peer object used to see if the listener should keep listening
    private Peer peer;
    
    /**
     * Constructor for objects of class Song_Request_Listener. Sets the peer refference and lets the 
     * user know that the listener is now listening for song requests
     * 
     * @param   void
     * @return  Song_Request_Listener
     */
    public Song_Request_Listener(Peer p)
    {
        //Let the user know the TCP listener is active
        System.out.println("Listening for song requests");
        
        //Set the peer
        peer = p;
    }
    
    /**
     * The implementation runnables run function. Waits for a message to be received and then transfers
     * the requested song
     * 
     */
    public void run()
    { 
        //Used to hold the message type received 
        String messageType;
        
        //Attempt to create a socket on which to listen
        try
        {
            //create a new socket object on port 9202
            serverSocket = new ServerSocket(9202);
        }
        
        //If creating port fails
        catch (IOException TCPListener)
        {
            //Let the user know what error has occured
            System.err.println(TCPListener);
        }
        
        //Check if it shoudl still be listening. Always true
        while (peer.listen())
        {
            //Attempt to receive and process a message
            try
            {   
                //Wait for a connection to be made to the socket
                Socket connectionSocket = serverSocket.accept();  
                
                //Get the input stream for the socket
                InputStreamReader isr = new InputStreamReader(connectionSocket.getInputStream());
                
                //Make a bufferreader from that input stream
                BufferedReader buff = new BufferedReader(isr);   
                
                //Read the message that was received
                messageType = buff.readLine();
                               
                //Makes sure the message has the right type. Prevents processing of messages from other sources
                if (messageType.startsWith("GIVESONG"))
                {
                    //split the message which is seperated by the "-" symbol
                    String song = messageType.split("-")[1];
                    
                    //Let the user know that they are sending a song to a peer at the address the request came from
                    System.out.println(song + " is being sent to the peer at " + connectionSocket.getInetAddress().getHostName());
                    
                    //Set the name of the file that will be sent to the song requested
                    String path = "Songs/" + song;
                    
                    //Create a refferance to that file
                    File songFile = new File(path);
                    
                    //Create a byte array to read the file into and set it's size to the same as the file
                    byte[] bytearray = new byte[(int) songFile.length()];
                    
                    //Create an input stream to read from that file
                    FileInputStream in = new FileInputStream(songFile);
                    
                    //Create a buffer from the file input stream
                    BufferedInputStream fileBuff = new BufferedInputStream(in);
                    
                    //Read from the file and write it to the byte array
                    fileBuff.read(bytearray, 0, bytearray.length);
                    
                    //Get the output stream for the socket
                    OutputStream out = connectionSocket.getOutputStream();
                    
                    //Write the file to the output stream of the socket so that other peer can read it
                    out.write(bytearray, 0, bytearray.length);
                    
                    //Makes sure all the writing gets completed
                    out.flush();
                    
                    //Close the socket
                    connectionSocket.close();
                    
                    //Let the user know the song has been sent
                    System.out.println(song + " has been successfully sent");
                }
                
                //Check if the message type is an offline message
                else if (messageType.startsWith("OFFLINE"))
                {
                    //Let the user know they are not listening to other peers
                    System.out.println("No longer taking song requests");
                }
            }
            
            //If an exception occurs during transfer
            catch (IOException TCP)
            {
                //print the error message of type IOException
                System.err.println(TCP);
            }
        }
        
        //Attempt to close the listening socket
        try
        {
            //Close the socket
            serverSocket.close();
        }
        
        //If closing fails
        catch (IOException listenCloseError)
        {
            //Let the user know what error has occured
            System.err.println(listenCloseError);
        }
    }
}
