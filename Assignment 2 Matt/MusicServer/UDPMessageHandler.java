import java.net.*;
import java.io.*;
import java.util.*;
/**
 * Write a description of class UDPMessageHandler here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class UDPMessageHandler implements Runnable
{
    MusicServer server;
    DatagramPacket packet;
    
    public UDPMessageHandler(MusicServer server, DatagramPacket packet)
    {
        this.server = server;
        this.packet = packet;
    }

    /**
     * An example of a method - replace this comment with your own
     * 
     * @param  y   a sample parameter for a method
     * @return     the sum of x and y 
     */
    public void run()
    {
        String message = new String( packet.getData());
        message = message.trim();
        if(message.startsWith("Online"))
        {
            if (!server.getPeerManager().peerExists(packet.getAddress().toString()))
            {      
                Peer peer = new Peer(packet.getAddress().toString());
                server.getPeerManager().addPeer(peer);
                String parts[] = message.split(",");              
                for( int i = Integer.valueOf(parts[1]); i > 0; i--)
                {
                    peer.addSong(parts[i+1]);
                }
                
                for (int i = peer.getSongList().size(); i > 0; i--)
                {
                    System.out.println(peer.getSongList().get(i-1));
                }            
                server.getSongFileWriter().write(peer.getSongList());
            }
        }
        if(message.startsWith("Song"))
        {
            String parts[] = message.split(","); 
            String song = parts[1];
            Integer numberOwners;
            ArrayList<String> ips;
            ips = server.getPeerManager().getSongOwnerList(song);
            numberOwners = ips.size();
            message = numberOwners.toString();
            if(!ips.isEmpty())
            {
                for (String ip : ips)
                {
                    message = message + "," + ip;
                }
            }
            try
            {
                DatagramSocket serverSocket = new DatagramSocket(9877);   //different port because we are already listening on port 9876                      
                byte[] sendData = new byte[1024]; 
                sendData = message.getBytes();                   
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());                   
                serverSocket.send(sendPacket); 
                serverSocket.close();
            }
            catch (Exception e)
            {
                System.err.println(e);
            }
        }
        if(message.startsWith("Update"))
        {
            if (server.getPeerManager().peerExists(packet.getAddress().toString()))
            {
                Peer peer = server.getPeerManager().getPeer(packet.getAddress().toString());
                String parts[] = message.split(",");   
                peer.getSongList().clear();
                for( int i = Integer.valueOf(parts[1]); i > 0; i--)
                {
                    peer.addSong(parts[i+1]);
                }
                System.out.println("Updated peer song list");
                for (int i = peer.getSongList().size(); i > 0; i--)
                {
                    System.out.println(peer.getSongList().get(i-1));
                }        
                server.getSongFileWriter().write(peer.getSongList());
            }
        }
        if(message.startsWith("List"))
        {
            ArrayList<Peer> peers = server.getPeerManager().getPeerList();
            Set<String> songs = new HashSet<String>(); //we use a set because we do not want duplicate elements in our song list
            for (Peer peer : peers) //loop through all peers
            {
                ArrayList<String> peerSongList = peer.getSongList();
                for (String song : peerSongList) //loop through the peers song list
                {
                    songs.add(song);
                }
            }
            message = new String();
            Integer numberOfSongs = songs.size();
            message = numberOfSongs.toString();
            for (String song : songs)
            {
                message = message + "," + song;
            }
            try
            {
                DatagramSocket serverSocket = new DatagramSocket(9877);   //different port because we are already listening on port 9876                      
                byte[] sendData = new byte[1024]; 
                sendData = message.getBytes();                   
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());                   
                serverSocket.send(sendPacket); 
                serverSocket.close();
            }
            catch (Exception e)
            {
                System.err.println(e);
            }
        }
    }
}
