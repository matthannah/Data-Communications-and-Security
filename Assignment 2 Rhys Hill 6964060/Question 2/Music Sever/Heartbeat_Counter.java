import java.net.*;
import java.io.*;
import java.util.concurrent.*;

/**
 * The Message_Listener waits for messages from clients and then starts up a Message_Proccessor in a new
 * thread which carries out what ever the message asks
 * 
 * @author      Rhys Hill 
 * @version     1.0
 */
public class Heartbeat_Counter implements Runnable
{
    //The peer the this counter is monitoring
    Peer peer;
       
    /**
     * Constructor for the Heartbeat_Counter class. Sets the peer refference
     * 
     * @param       Music_Sever
     * @return      Message_Listener
     */
    public Heartbeat_Counter (Peer p)
    {
        //Sets the music server equal to the one that is passed in
        peer = p;
    }
    
    /**
     * Implementation of runnables run function. Counts down to when a client as taken too long
     * to respond. Once this is reached the peer is set to offline.
     * 
     * @param       void
     * @return      void
     */
    public void run()
    {
        //Loop through and check if the flag has been set 5 times, waiting for 1000ms between checks
        for (int i = 0; i < 5; i++)
        {
            //Attempt to wait for 1000ms
            try
            {
                //Wait for 1000ms before checking if the client has responded
                Thread.sleep(1000);
            }
            
            //If something goes wrong while trying to wait
            catch (Exception e)
            {
                //Print a message to the user to let them know about the error
                System.err.println(e);
            }
            
            //Check if the heartbeating flag has been set
            if(peer.heartBeating())
            {
                //Increase i so that the loop can be left early
                i = 10;
            }
        }
        
        //Set the online status to whatever the flag is. If the client responded in time it will be 
        //true. If not it will be false.
        peer.setOnline(peer.heartBeating());
        
        //Check what the result was
        if (!peer.getOnline())
        {
            //Tell the user that the client is offline now
            System.out.println(peer.getName() + " is now offline");
        }
    }
}
    