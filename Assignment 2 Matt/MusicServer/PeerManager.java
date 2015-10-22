import java.util.*;
/**
 * Peer Manager - managers all peers online
 * 
 * @author Matthew Hannah
 * @version 14/10/2015
 */
public class PeerManager
{
    private ArrayList<Peer> peers;

    /**
     * Constructor for objects of class PeerManager
     */
    public PeerManager()
    {
        peers = new ArrayList<Peer>();
    }

    /**
     * getPeerList
     * 
     * @return - ArrayList<Peer> - a list of peers that have come online
     */
    public ArrayList<Peer> getPeerList()
    {
        return peers;
    }
    
    /**
     * getPeer  - returns a peer given a specific ip; returns null if the peer doesn't exist
     * 
     * @param   - String ip - the ip of the peer requested
     * 
     * @return  - Peer - the peer object
     */
    public Peer getPeer(String ip)
    { 
        //loop through the peers array list
        for (Peer peer : peers)
        {
            //if a peer has an IP equal to the ip entered as an argument to the method
            if (peer.getIP().equals(ip))
            {
                //return the peer
                return peer;
            }
        }
        //return null if no peer was found
        return null;
    }
    
    /**
     * peerExists  - checks if the peer exists
     * 
     * @param      - String ip - the ip of the peer requested
     * 
     * @return     - boolean - true; if the peer exists, false; if the peer does not exist
     */
    public boolean peerExists(String ip)
    {
        //loop through peers in peer list
        for (Peer peer : peers)
        {
            //if a peer has an IP equal to the ip entered as an argument to the method
            if (peer.getIP().equals(ip))
            {
                //return true; meaning the peer exists
                return true;
            }
        }
        //return false; meaning the peer does not exist
        return false;
    }
    
    /**
     * addPeer     - adds a peer to the peer list
     * 
     * @param      - Peer peer - the peer to add to the list
     */
    synchronized public void addPeer(Peer peer)
    {
        //print a message saying a peer has been added
        System.out.println("Peer added: " + peer.getIP());
        //add the peer to the list
        peers.add(peer);
    }
    
    /**
     * removePeer     - removes a peer from the peer list
     * 
     * @param         - Peer peer - the peer to be removed from the list
     */
    synchronized public void removePeer(Peer peer)
    {
        //print a message saying a peer has been removed
        System.out.println("Peer removed: " + peer.getIP());
        //loop through peer list
        for (int i = 0; i < peers.size(); i++)
        {
            //find the peer that is going offline
            if(peers.get(i).getIP().equals(peer.getIP()))
            {
                //remove the peer
                peers.remove(i);
                break;
            }
        }  
    }
    
    /**
     * getSongOwnerList - gets a list of ips of all peers that have a song
     * 
     * @param           - String songRequested - the song to be checked
     * 
     * @return          - ArrayList<String> - list containing the ips of all peers who had the song
     */
    public ArrayList<String> getSongOwnerList(String songRequested)
    {
        //create a new string array list object
        ArrayList<String> ips = new ArrayList<String>();
        //loop through all peers
        for (Peer peer : peers)
        {
            //if a peer has the song requested
            if (peer.hasSong(songRequested))
            {
                //add the peers ip to the list
                ips.add(peer.getIP());
            }
        }
        //return the array list of all peer ips who had the song
        return ips;
    }
}
