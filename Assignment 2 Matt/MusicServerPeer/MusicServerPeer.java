import java.io.*; 
import java.net.*;
import java.util.*;
import java.awt.Desktop;
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
    private String serverIP;
    private TCPListener tcpListener;
    
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
        peer.tcpListener = new TCPListener(peer);
        new Thread(peer.tcpListener).start();
        peer.serverIP = args[0];
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
            InetAddress IPAddress = InetAddress.getByName(serverIP);       
            byte[] sendData = new byte[1024];            
            String sentence = "Online," + songs.size();       
            for (int i = songs.size(); i > 0; i--)
            {
                sentence = sentence + "," + songs.get(i - 1);
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
            InetAddress IPAddress = InetAddress.getByName(serverIP);       
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
            InetAddress IPAddress = InetAddress.getByName(serverIP);       
            byte[] sendData = new byte[1024];            
            String sentence = "Update," + songs.size();       
            for (int i = songs.size(); i > 0; i--)
            {
                sentence = sentence + "," + songs.get(i - 1);
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
                playSong();
                break;
            case 2: //show songs
                requestSongList();
                break;
            case 3: //request song
                requestSong();
                break;
            case 4: //Add Song
                addSong();
                break;
            case 5: //Remove Song
                removeSong();
                break;
            case 6:
                //maybe add in unregister()
                System.out.println("Exiting...");
                exit();
                break;
            default:
                //do nothing
        }
    }
    
    public boolean isRunning()
    {
        return running;
    }
    
    public void exit()
    {
        tcpListener.finish();
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
                if (file.getName().endsWith(".mp3"))
                {                   
                    results.add(file.getName());
                }
            }
        }
        return results;
    }
    
    public void addSong()
    {
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        Integer numberOfSongs = songs.size();
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
            updateSongList();
            if(numberOfSongs<songs.size())
            {
                System.out.println("Song added - notifying server");
                notifyUpdateSongList();
            }
            else if(numberOfSongs>songs.size())
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
        songs = getSongsFromFile();
    }
    
    public void removeSong()
    {
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        Integer numberOfSongs = getSongList().size();
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
            updateSongList();
            if(numberOfSongs<getSongList().size())
            {
                System.out.println("Perhaps you meant to add a song?");
            }
            else if(numberOfSongs>getSongList().size())
            {
                System.out.println("Song removed - notifying server");
                notifyUpdateSongList();
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
            InetAddress IPAddress = InetAddress.getByName(serverIP);       
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
    
    public void requestSong()
    {
        String songRequested = selectSong(); //ask the peer what song they would like
        String message = requestPeerWithSong(songRequested); //ask the server what peers have the song
        if (!message.startsWith("0"))
        {
            String parts[] = message.split(",");
            ArrayList<String> peersWithSong = new ArrayList<String>();
            for( int i = Integer.valueOf(parts[0]); i > 0; i--)
            {
               peersWithSong.add(parts[i].substring(1));
            } 
            if (!peersWithSong.isEmpty()) {
                System.out.println("Requesting song from: " + peersWithSong.get(0));
                TCPRequestSong(peersWithSong.get(0), songRequested); //only the first ip in the list, maybe fix this to let user choose
            }
        }
    }
    
    public void TCPRequestSong(String ip, String songRequested) {
        try 
        {
            String message = "SendSong," + songRequested +",\n"; 
            final String fileOutput = "songs/"+songRequested;
            BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));   
            Socket clientSocket = new Socket(ip, 6789);   
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());      
            outToServer.writeBytes(message);
            
            System.out.println("Receiving song...");
            
            byte[] aByte = new byte[1];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            InputStream is = clientSocket.getInputStream();
            FileOutputStream fos = new FileOutputStream(fileOutput);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            int bytesRead = is.read(aByte, 0, aByte.length);
            do 
            {
                baos.write(aByte);
                bytesRead = is.read(aByte);         
            } while (bytesRead != -1);
            bos.write(baos.toByteArray());
            bos.flush();
            bos.close();
            clientSocket.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println("Song transfer complete");
        songs.add(songRequested);
        notifyUpdateSongList();
    }
    
    public void playSong()
    {
        System.out.println("----- Enter the name of the song you'd like to play -----");
        for (String song : songs)
        {
            System.out.println(song);
        }
        System.out.println("---------------------------------------------------------");
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        String input = "";
        try
        {
            input = br.readLine();
        }
        catch (IOException e)
        {
            System.err.println(e);
        }
        boolean songExists = false;
        for (String song : songs)
        {
            if (input.equals(song))
            {
                songExists = true;
            }
        }
        if (songExists)
        {
            File f = new File("songs/"+input);
            if (f.exists()) 
            {
                if (Desktop.isDesktopSupported()) 
                {
                    try
                    {
                        Desktop.getDesktop().open(f);
                    }
                    catch (Exception e)
                    {
                        System.out.println(e);
                    }
                } 
     
                else
                {
                    System.out.println("File does not exists!");
                }
 
            }
        }
        else 
        {
            System.out.println("Song name does not exist");
        }
    }
}
