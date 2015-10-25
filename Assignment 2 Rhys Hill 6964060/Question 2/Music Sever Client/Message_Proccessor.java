import java.net.*;
import java.io.*;
import java.util.*;
/**
 * Controls what actions are taken out depnding on what message it is given and replies to the client that sent
 * the message
 * 
 * @author      Rhys Hill 
 * @version     1.0
 */
public class Message_Proccessor implements Runnable
{
    //Stores a refference to the peer
    private Peer peer;
    
    //Stores the message that the listener received
    private DatagramPacket messagePacket;
    
    //Stores the type of message that was sent
    private String messageType;
    
    //Stores the actual message
    private String message;
    
    //The reply message that will be sent
    private String sendMessage;

    /**
     * Constructor for objects of class Message_Proccessor. Sets the packet that was given. Also sets the
     * peer to be a refference to the peer
     * 
     * @param       Peer
     * @return      Message_Proccessor
     */
    public Message_Proccessor(DatagramPacket msg, Peer p)
    {
        //Sets the peer
        peer = p;
        
        //Sets the message packet
        messagePacket = msg;
                      
        //Gets the message type as everything before the "-"
        messageType = new String(messagePacket.getData()).split("-")[0];
        
        //Gets rid of unwanted white space
        messageType = messageType.trim();
        
        //Gets the message as everything after the "-"
        message = new String(messagePacket.getData()).split("-")[1];
        
        //Gets rid of unwanted white space
        message = message.trim();
    }

    /**
     * Implementation of runnables run function. Tells the peer what actions need to be undertaken given
     * the message that was received
     * 
     * @param       void
     * @return      void
     */
    public void run()
    {
        //Checks if the message type is "ONLINE"
        if (messageType.equals("ONLINE"))
        {
            //Set the peer as online. A connection has been made to the server
            peer.setOnline(true);
            
            //Set the name as given by the server
            peer.setName(message);
        }
        
        //Checks if the message type is "NEWSONG"
        else if (messageType.equals("NEWSONGS"))
        {
            //Do Nothing. This is a confrimatin message
        }
        
        //Checks if the message type is "GETSONG"
        else if (messageType.equals("GETSONG"))
        {          
            //Strores the IPs that the user can select
            ArrayList<String> possibleIP = new ArrayList<>();
                              
            //Used to store the users IP request
            String requestedIP = "";
            
            //Create an input stream to get user inputs from the console
            InputStreamReader isr = new InputStreamReader(System.in);
            
            //Create a buffer reader to read data gotten from the console
            BufferedReader br = new BufferedReader(isr);
            
            //Split the message into an array of strings. Each of these is the IP of a peer with the song 
            String[] ipArray = (new String(messagePacket.getData())).split("-");
            
            //Let the user know which peers have the song starting with the first IP address
            System.out.println(message + " is at " + ipArray[2].trim());
            
            //Add the first IP to the list of possible IPs
            possibleIP.add(ipArray[2].trim());
            
            //Convert the Array into an array list
            ArrayList<String> ipArrayList = new ArrayList<>(Arrays.asList(ipArray));
            
            //Loops through the remaining IPs returned by the server. Ignores positions 0, 1, and 2
            for (int i = ipArrayList.size() - 1; i > 2; i--)
            {
                //Print the next IP which has the song to the console 
                System.out.println("and " + ipArrayList.get(i).trim());
                
                //Add the next IP to the list of possible IPs
                possibleIP.add(ipArray[i].trim());
            }
            
            //Check if a viable IP has been requested
            while(!possibleIP.contains(requestedIP))
            {
                //Ask the user for an input via the console
                System.out.println("Please enter the IP of the peer you want to request the song from");
                
                //Attempt to read the IP the user wants to use
                try
                {
                    //Read the next line input by the user
                    requestedIP = br.readLine();
                }
                //If the reading fails
                catch(IOException e)
                {
                    //Print error message to the console so the user knows
                    System.err.println(e);
                } 
            }
            
            //Request the song from the first IP in the list
            peer.TCPRequestSong(requestedIP, ipArray[1].trim());
        }
        
         //Checks if the message type is "GETALL"
        else if (messageType.equals("GETALL"))
        {
            //Split the message into an array of strings. Each of these string will be a song title
            String[] songArray = (new String(messagePacket.getData())).split("-");
           
            //Cast the String array made above as an array list
            ArrayList<String> songArrayList = new ArrayList<>(Arrays.asList(songArray));
            
            //Loop through the string array created skipping number one
            for (String song : songArrayList)
            {
                //Get rid of white space
                song = song.trim();
                
                //Check for the message type string so it can be ignored
                if (!song.equals("GETALL"))
                {
                    //Print the song title to the console for the user
                    System.out.println(song);
                }
            }
        }
        
        //Checks if the message type is "HEARTBEAT"
        else if (messageType.equals("HEARTBEAT"))
        {
            //Reply message for the heartbeat message to let the server know the peer is still online
            sendMessage = "HEARTBEAT-";
        }
        
        //Checks if the message type is "OFFLINE"
        else if (messageType.equals("OFFLINE"))
        {
            //Print goodbye message to let the user know the server knows they're gone
            System.out.println(message);
        }
        
        //If the message type is not one that the server recognises
        else
        {
            //Create a reply message for client to tell them something went wrong
            sendMessage = "NOTRECOGNISED-";
        }
        
        //Check if there is a message to send
        if (sendMessage != null)
        {
            //Attempt to reply to the server
            try
            {
                //Create a new socket that is different to the one for receiving messages
                DatagramSocket messageSocket = new DatagramSocket(9876);                      
                
                //Byte array to store the message being sent
                byte[] dataToWrite = new byte[1024]; 
                
                //Convert the message into its byte array form so it can be sent
                dataToWrite = sendMessage.getBytes();                   
                
                //Create a packet for the message
                DatagramPacket sendablePacket = new DatagramPacket(dataToWrite, dataToWrite.length, messagePacket.getAddress(), 9001);                   
                
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
    }
}