import java.net.*;
import java.io.*;
/**
 * Write a description of class UDPListener here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class UDPListener implements Runnable
{
    MusicServer server;
    boolean listen = true;
    /**
     * Constructor for objects of class UDPListener
     */
    public UDPListener(MusicServer s)
    {
        server = s;
    }

    /**
     * An example of a method - replace this comment with your own
     * 
     * @param  y   a sample parameter for a method
     * @return     the sum of x and y 
     */
    public void run()
    {
        try 
        {
            DatagramSocket socket = new DatagramSocket(9876);
            while (listen)
            {
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket); //method blocks until a datagram is received
                (new Thread(new UDPMessageHandler(server, receivePacket))).start(); //start 
            }
            socket.close();
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }
}
