import java.net.*;
import java.io.*;
/**
 * UDPListener - contantly listens on port 9876 for UDP messages, messages are handles in another thread
 * 
 * @author Matthew Hannah
 * @version 14/10/2015
 */
public class UDPListener implements Runnable
{
    MusicServer server;
    boolean listen = true;
    DatagramSocket socket;
    
    /**
     * Constructor for objects of class UDPListener
     */
    public UDPListener(MusicServer s)
    {
        server = s;
    }

    /**
     * run -    method that must be implemented as this class implements a
     *          runnable interface
     * 
     */
    public void run()
    {
        //enclose code that might throw an exception in a try block
        try 
        {
            //create a new datagram socket object on port 9876
            socket = new DatagramSocket(9876);
        }
        //enclose exception handling code in a catch block
        catch (IOException e)
        {
            //print the error message of type IOException
            System.err.println("IOException " + e);
        }
        //while listening
        while (listen)
        {
            //create a new byte array of size 1kB
            byte[] receiveData = new byte[1024];
            //enclose code that might throw an exception in a try block
            try
            {
                //create a new Datagram Packet object
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                //receive data from the socket; method blocks until a datagram is received
                socket.receive(receivePacket); 
                //start a new thread to handle the message received
                (new Thread(new UDPMessageHandler(server, receivePacket))).start();
            }
            //enclose exception handling code in a catch block
            catch (IOException e)
            {
                //print the error message of type IOException
                System.err.println("IOException " + e);
            }
        }
        //close the socket
        socket.close();      
    }
}
