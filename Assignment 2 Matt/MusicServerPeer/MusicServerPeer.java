import java.io.*; 
import java.net.*;
import java.util.*;
/**
 * Write a description of class MusicServerPeer here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class MusicServerPeer
{
    private ArrayList<String> songs;
    private boolean running = true;
    /**
     * Constructor for objects of class MusicServerPeer
     */
    public MusicServerPeer()
    {
        songs = new ArrayList<String>();
    }

    public static void main(String args[])  
    { 
        MusicServerPeer peer = new MusicServerPeer();    
        peer.updateSongList();
        peer.register();
        while(peer.isRunning())
        {
            peer.menu();
        }
    } 
    
    public ArrayList<String> getSongList()
    {
        return songs;
    }
    
    public void register()
    {
        try {
            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));       
            DatagramSocket clientSocket = new DatagramSocket();       
            InetAddress IPAddress = InetAddress.getByName("localhost");       
            byte[] sendData = new byte[1024];            
            String sentence = "Online," + this.getSongList().size();       
            for (int i = this.getSongList().size(); i > 0; i--)
            {
                sentence = sentence + "," + this.getSongList().get(i - 1);
            }
            sendData = sentence.getBytes(); 
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);       
            clientSocket.send(sendPacket);  
            clientSocket.close(); 
        }
        catch (Exception e)
        {
            System.err.println(e);
        }
    }
    
    public String selectSong()
    {
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        String input;
        try
        {
            System.out.println("Find peer with the song: ");
            input = br.readLine();
            return input;
        }
        catch (IOException e)
        {
            System.err.println(e);
        }
        return null;
    }
    
    public String requestPeerWithSong(String song)
    {
        try
        {
            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));       
            DatagramSocket clientSocket = new DatagramSocket();       
            InetAddress IPAddress = InetAddress.getByName("localhost");       
            byte[] data = new byte[1024];       
            byte[] receiveData = new byte[1024]; 
            song = "Song," + song;
            data = song.getBytes();       
            DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress, 9876);       
            clientSocket.send(sendPacket);       
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);       
            clientSocket.receive(receivePacket);       
            String ip = new String(receivePacket.getData());       
            System.out.println("FROM SERVER:" + ip);       
            clientSocket.close();
            return ip;
        }
        catch (Exception e)
        {
            System.err.println(e);
        }
        return null;
    }
    
    public void notifyUpdateSongList()
    {
        try {
            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));       
            DatagramSocket clientSocket = new DatagramSocket();       
            InetAddress IPAddress = InetAddress.getByName("localhost");       
            byte[] sendData = new byte[1024];            
            String sentence = "Update," + this.getSongList().size();       
            for (int i = this.getSongList().size(); i > 0; i--)
            {
                sentence = sentence + "," + this.getSongList().get(i - 1);
            }
            sendData = sentence.getBytes(); 
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);       
            clientSocket.send(sendPacket);  
            clientSocket.close(); 
        }
        catch (Exception e)
        {
            System.err.println(e);
        }
    }
    
    public void menu()
    {
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        int input = 0;
        try
        {
            System.out.println("Select functionality: ");
            System.out.println("1: Play Song | 2: Show Songs | 3: Request Song | 4: Add Song | 5: Remove Song | 6: Exit");
            input = Integer.valueOf(br.readLine());
        }
        catch (IOException e)
        {
            System.err.println(e);
        }
        switch (input) 
        {
            case 1: //play song
                System.out.println("Option 1 selected");
                break;
            case 2: //show songs
                this.requestSongList();
                break;
            case 3: //request song
                this.requestSong();
                break;
            case 4: //Add Song
                this.addSong();
                break;
            case 5: //Remove Song
                this.removeSong();
                break;
            case 6:
                //maybe add in unregister()
                System.out.println("Exiting...");
                this.exit();
                break;
            default:
                
        }
    }
    
    public boolean isRunning()
    {
        return running;
    }
    
    public void exit()
    {
        running = false;
    }
    
    public ArrayList<String> getSongsFromFile()
    {
        ArrayList<String> results = new ArrayList<String>();  
        File[] files = new File("songs").listFiles();
        //If this pathname does not denote a directory, then listFiles() returns null.        
        for (File file : files) 
        {
            if (file.isFile()) 
            {
                results.add(file.getName());
            }
        }
        return results;
    }
    
    public void addSong()
    {
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        Integer numberOfSongs = this.getSongList().size();
        String input = "c";
        try
        {
            System.out.println("press 'c' to cancel");
            System.out.println("Move a song into song directory and enter any other key to continue");
            input = br.readLine();
        }
        catch (IOException e)
        {
            System.err.println(e);
        }
        if (!input.equals("c"))
        {
            this.updateSongList();
            if(numberOfSongs<this.getSongList().size())
            {
                System.out.println("Song added - notifying server");
                this.notifyUpdateSongList();
            }
            else if(numberOfSongs>this.getSongList().size())
            {
                System.out.println("Perhaps you meant to remove a song?");
            }
            else
            {
                System.out.println("Did you forget to move a song to the directory?");
            }
        }
        else
        {
            System.out.println("operation cancelled");
        }
    }
    
    public void updateSongList()
    {
        songs = this.getSongsFromFile();
    }
    
    public void removeSong()
    {
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        Integer numberOfSongs = this.getSongList().size();
        String input = "c";
        try
        {
            System.out.println("press 'c' to cancel");
            System.out.println("Remove a song from song directory and enter any other key to continue");
            input = br.readLine();
        }
        catch (IOException e)
        {
            System.err.println(e);
        }
        if (!input.equals("c"))
        {
            this.updateSongList();
            if(numberOfSongs<this.getSongList().size())
            {
                System.out.println("Perhaps you meant to add a song?");
            }
            else if(numberOfSongs>this.getSongList().size())
            {
                System.out.println("Song removed - notifying server");
                this.notifyUpdateSongList();
            }
            else
            {
                System.out.println("Did you forget to remove a song from the directory?");
            }
        }
        else
        {
            System.out.println("operation cancelled");
        }
    }
    
    public void requestSongList()
    {
        try {
            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));       
            DatagramSocket clientSocket = new DatagramSocket();       
            InetAddress IPAddress = InetAddress.getByName("localhost");       
            byte[] sendData = new byte[1024]; 
            byte[] receiveData = new byte[1024];
            String sentence = "List";      
            sendData = sentence.getBytes(); 
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);       
            clientSocket.send(sendPacket);  
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);       
            clientSocket.receive(receivePacket);       
            String songList = new String(receivePacket.getData()); 
            String parts[] = songList.split(",");   
            System.out.println("------SONG LIST-------");
                for( int i = Integer.valueOf(parts[0]); i > 0; i--)
                {
                    System.out.println(parts[i]);
                }  
            clientSocket.close(); 
        }
        catch (Exception e)
        {
            System.err.println(e);
        }
    }
    
    void requestSong()
    {
        String songRequested = this.selectSong(); //ask the peer what song they would like
        String message = this.requestPeerWithSong(songRequested); //ask the server what peers have the song
        String parts[] = message.split(",");
        ArrayList<String> peersWithSong = new ArrayList<String>();
        for( int i = Integer.valueOf(parts[0]); i > 0; i--)
        {
           peersWithSong.add(parts[i].substring(1));
        } 
        
    }
}
