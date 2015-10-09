import java.util.*;
/**
 * Write a description of class PeerManager here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
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

    public ArrayList<Peer> getPeerList()
    {
        return peers;
    }
    
    public Peer getPeer(String ip)
    {
        for (int i = peers.size(); i > 0; i--)
        {
            if (peers.get(i-1).getIP().equals(ip))
            {
                return peers.get(i-1);
            }
        }
        return null;
    }
    
    public boolean peerExists(String ip)
    {
        for (int i = peers.size(); i > 0; i--)
        {
            if (peers.get(i-1).getIP().equals(ip))
            {
                return true;
            }
        }
        return false;
    }
    
    synchronized public void addPeer(Peer peer)
    {
        System.out.println("Peer added: " + peer.getIP());
        peers.add(peer);
    }
    
    public ArrayList<String> getSongOwnerList(String songRequested)
    {
        ArrayList<String> ips = new ArrayList<String>();
        for (Peer peer : this.peers)
        {
            if (peer.hasSong(songRequested))
            {
                ips.add(peer.getIP());
            }
        }
        return ips;
    }
}
