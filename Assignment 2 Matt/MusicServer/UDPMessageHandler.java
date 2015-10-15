import java.net.*;
import java.io.*;
import java.util.*;
/**
 * UDPMessageHandler - the class that implements a runnable interface; this class handles all messages received
 *                     by the UDPListener thread
 * 
 * @author Matthew Hannah
 * @version 15/10/2015
 */
public class UDPMessageHandler implements Runnable
{
    MusicServer server;
    DatagramPacket packet;
    
    /**
     * Constructor for objects of class UDPMessageHandler
     */
    public UDPMessageHandler(MusicServer server, DatagramPacket packet)
    {
        this.server = server;
        this.packet = packet;
    }

    /**
     * run -    method that must be implemented as this class implements a
     *          runnable interface
     * 
     */
    public void run()
    {
        //create a new string object and set it equal to the data received in the packet
        String message = new String( packet.getData());
        //trim the message String, removing any whitespace on the front or the end
        message = message.trim();
        //If the message starts with Online, then a peer is registering
        if(message.startsWith("Online"))
        {
            //if the peer already exists, something is wrong, do not register the peer again
            if (!server.getPeerManager().peerExists(packet.getAddress().toString()))
            {    
                //create a new peer object and pass the IP address of the packet to the constructor
                Peer peer = new Peer(packet.getAddress().toString());
                //add the peer to the peer managers list of peers
                server.getPeerManager().addPeer(peer);
                //split the message by commas 
                //MESSAGE SYNTAX: Online,number of songs,song1,song2,song3
                String parts[] = message.split(",");           
                //for all the songs sent in the message
                for( int i = Integer.valueOf(parts[1]); i > 0; i--)
                {
                    //add the song to the peers list of songs
                    peer.addSong(parts[i+1]);
                }
                //for all the songs in the peers song list
                for (int i = peer.getSongList().size(); i > 0; i--)
                {
                    //print them out
                    System.out.println("-----peers songs------");
                    System.out.println(peer.getSongList().get(i-1));
                    System.out.println("Running...");
                }            
                //write all the songs to the songs.txt file
                server.getSongFileWriter().write(server.getPeerManager().getPeerList());
            }
        }
        //If the message starts with Song, the peer is requesting who has that song specified in the message
        if(message.startsWith("Song"))
        {
            //split the message by commas 
            //MESSAGE SYNTAX: Song,song1
            String parts[] = message.split(","); 
            //create a new string and set it equal to the song name sent by the message
            String song = parts[1];
            //create a new integer number of owners which represents how many peers have that song
            Integer numberOwners;
            //create new array list ips, list of peer ips that have the song
            ArrayList<String> ips;
            //set ips equal to the array list returned by get owners list
            ips = server.getPeerManager().getSongOwnerList(song);
            //set number of owners equal to the size of the array list
            numberOwners = ips.size();
            //start constructing a new message to be sent and set it equal to the number of owners
            message = numberOwners.toString();
            //if the number of owners is not zero
            if(!ips.isEmpty())
            {
                //loop through all owners
                for (String ip : ips)
                {
                    //add their ip to the message seperated by commas
                    message = message + "," + ip;
                }
            }
            //datagram packet to be sent to the peer
            DatagramPacket sendPacket;
            //datagram socket that will provide connectionless communication with the peer
            DatagramSocket serverSocket;
            //new byte array of size 1kB that will hold out message data
            byte[] sendData = new byte[1024]; 
            //convert the message to bytes and set it equal to the sendData byte array
            sendData = message.getBytes(); 
            //enclose code that might throw an exception in a try block
            try
            {
                //create a new datagram socket object on port 9877; different from out other coekt 9876 because
                //that port is being used for listening
                serverSocket = new DatagramSocket(9877); 
                //create a new datagram packet object and pass it our byte array (message), address of the peer,
                //and the port the peer is listening on
                sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort()); 
                //write the packet to the output stream
                serverSocket.send(sendPacket); 
                //close the socket
                serverSocket.close();
            }
            //enclose exception handling code in a catch block
            catch (IOException e)
            {
                //print the error message of type IOException
                System.err.println("IOException " + e);
            }
        }
        //If the message starts with Update, then a peer is updating the server with its song list
        if(message.startsWith("Update"))
        {
            //if the peer exists
            if (server.getPeerManager().peerExists(packet.getAddress().toString()))
            {
                //get the peer from the peer manager with ip equal to the ip of packet received
                Peer peer = server.getPeerManager().getPeer(packet.getAddress().toString());
                //split the message by commas 
                //MESSAGE SYNTAX: Update,numberOfSongs,song1,song2,song3
                String parts[] = message.split(",");   
                //clear the peers song list
                peer.getSongList().clear();
                //loop through all the songs received in the message
                for( int i = Integer.valueOf(parts[1]); i > 0; i--)
                {
                    //add them to the peers song list
                    peer.addSong(parts[i+1]);
                }
                //print a message to the screen
                System.out.println("Updated peer song list");
                //loop through the peers song list
                for (int i = peer.getSongList().size(); i > 0; i--)
                {
                    //print each song the peer has
                    System.out.println(peer.getSongList().get(i-1));
                }        
                //update the song.txt file with all the songs
                server.getSongFileWriter().write(server.getPeerManager().getPeerList());
            }
        }
        //if the message starts with List, the peer is requesting a song list of all songs
        if(message.startsWith("List"))
        {
            //get the peer list from the peer manager
            ArrayList<Peer> peers = server.getPeerManager().getPeerList();
            //create a new set of string, we use a set because we do not want duplicate elements in our song list
            Set<String> songs = new HashSet<String>(); 
            //loop through all peers in the peer list
            for (Peer peer : peers)
            {
                //get the peers song list
                ArrayList<String> peerSongList = peer.getSongList();
                //loop through the peers song list
                for (String song : peerSongList)
                {
                    //add each song the peer has to the song hashset, only unique songs will be added
                    songs.add(song);
                }
            }
            //create an integer equal to the number of unique songs in the list
            Integer numberOfSongs = songs.size();
            //start constructing a new message to be sent and set it equal to the number of owners
            message = numberOfSongs.toString();
            //loop through all the unique songs
            for (String song : songs)
            {
                //add the song to the message
                message = message + "," + song;
            }
            //datagram packet to be sent to the peer
            DatagramPacket sendPacket;
            //datagram socket that will provide connectionless communication with the peer
            DatagramSocket serverSocket;
            //new byte array of size 1kB that will hold out message data
            byte[] sendData = new byte[1024]; 
            //convert the message to bytes and set it equal to the sendData byte array
            sendData = message.getBytes(); 
            //enclose code that might throw an exception in a try block
            try
            {
                //create a new datagram socket object on port 9877; different from out other coekt 9876 because
                //that port is being used for listening
                serverSocket = new DatagramSocket(9877); 
                //create a new datagram packet object and pass it our byte array (message), address of the peer,
                //and the port the peer is listening on
                sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort()); 
                //write the packet to the output stream
                serverSocket.send(sendPacket); 
                //close the socket
                serverSocket.close();
            }
            //enclose exception handling code in a catch block
            catch (IOException e)
            {
                //print the error message of type IOException
                System.err.println("IOException " + e);
            }
        }
    }
}
