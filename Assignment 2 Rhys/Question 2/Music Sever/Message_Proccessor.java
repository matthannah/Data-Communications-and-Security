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
    //Stores a refference to the systems music server
    private Music_Sever musicServer;
    
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
     * music server to be a refference to the systems music sever
     * 
     * @param       Music_Sever
     * @return      Message_Proccessor
     */
    public Message_Proccessor(DatagramPacket msg, Music_Sever ms)
    {
        //Sets the music server
        musicServer = ms;
        
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
     * Implementation of runnables run function. Tells the music server what actions need to be undertaken given
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
            //Tell the music server about the peer being online and tells of its current address
            String peerName = musicServer.peerOnline(message, messagePacket.getAddress().toString());
            
            //Tells user a peer is online
            System.out.println(peerName + " Online");
            
            //Create a reply message for the client letting them know thier name on the system
            sendMessage = "ONLINE-" + peerName;
        }
        
        //Checks if the message type is "NEWSONG"
        else if (messageType.equals("NEWSONGS"))
        {
            //Ask the music server for the peer that this message is meant for based on its address
            Peer peer = musicServer.getPeerByAddress(messagePacket.getAddress().toString());
            
            //Split the message into an array of strings. Each of these string will be a song title
            String songString = (new String(messagePacket.getData()));
            
            //Tell that peer to change it's song list to match the one it's just received from the client
            peer.changeSongList(songString);
            
            //Create a reply message for the client letting them know the song list has been updated
            sendMessage = "NEWSONGS-Received";
        }
        
        //Checks if the message type is "GETSONG"
        else if (messageType.equals("GETSONG"))
        {
            //Creates a string to store the address of a peer who has that song
            String peerAddress = musicServer.getSong(message);
            
            //Create a reply for the client with the address of a peer who has the song they want
            sendMessage = "GETSONG-" + message + peerAddress;
        }
        
        //Checks if the message type is "GETALL"
        else if (messageType.equals("GETALL"))
        {
            //Creates a string and populates it with the title of every song the serverknows about
            String allSongs = musicServer.getAllSongs();
            
            //Create a reply message for the client with every available song
            sendMessage = "GETALL" + allSongs;
        }
        
        //If the message type is not one that the server recognises
        else
        {
            //Create a reply message for client to tell them something went wrong
            sendMessage = "FUCKKNOWS";
        }
        
        //Attempt to reply to the client
        try
        {
            //Create a new socket that is different to the one for receiving messages
            DatagramSocket messageSocket = new DatagramSocket();                      
            
            //Byte array to store the message being sent
            byte[] dataToWrite = new byte[1024]; 
            
            //Convert the message into its byte array form so it can be sent
            dataToWrite = sendMessage.getBytes();                   
            
            //Create a packet for the message
            DatagramPacket sendablePacket = new DatagramPacket(dataToWrite, dataToWrite.length, messagePacket.getAddress(), 9101);                   
            
            //Send the newly created packet
            messageSocket.send(sendablePacket); 
            
            //Close the new socket
            messageSocket.close();
        }
        catch (Exception send)
        {
            System.err.println(send);
        }
    }
}
