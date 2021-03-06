import java.net.*;
import java.io.*;

/**
 * The Message_Listener waits for messages from the server or other clients and then starts up a 
 * Message_Proccessor in a new thread which carries out what ever the message asks
 * 
 * @author      Rhys Hill 
 * @version     1.0
 */
public class Message_Listener implements Runnable
{
    //The music server object here is used to store a refference to the music sever used for this system
    Peer peer;
    
    //Used to store the socket that is listening for messages
    DatagramSocket messageSocket;
    
    /**
     * Constructor for the Message_Listener class. Simply sets the peer
     * 
     * @param       Music_Sever
     * @return      Message_Listener
     */
    public Message_Listener (Peer p)
    {
        //Sets the peer equal to the one passed in
        peer = p;
    }
    
    /**
     * Implementation of runnables run function. Attempts to creatre a new socket to listen for messages.
     * Waits until a message is recived and then reports that message back to the peer
     * 
     * @param       void
     * @return      void
     */
    public void run()
    {
        //Attempts to carry out its purpose
        try 
        {
            //Creates a new socket on which to listen with the port number 9101
            messageSocket = new DatagramSocket(9101);
            
            //Repeat this loop for the life of the program
            while (peer.listen())
            {
                //Creates a byte array to store a received message
                byte[] message = new byte[1024];
                
                //Creates a packet that can store that message of the same size as the byte array 
                DatagramPacket messagePacket = new DatagramPacket(message, message.length);
                                
                //Waits until there is actually a message available
                messageSocket.receive(messagePacket);
                
                //Starts a new thread so that the message can be proccessed without missing any more messages
                (new Thread(new Message_Proccessor(messagePacket, peer))).start();
            }
            
            //Closes the connection. Nvere actually used
            messageSocket.close();
        }
        
        //If error occurs while trying to read messages
        catch (Exception messageError)
        {
            //Print to the console to let the user know an error has occured
            System.out.println("MESSAGE_LISTENER - An error has occured");
            
            System.err.println(messageError);
        }
    }
}