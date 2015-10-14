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
    private int serverPort = 9876;
    
    /**
     * Constructor for objects of class MusicServerPeer
     */
    public MusicServerPeer()
    {
        //create a new array list object of strings
        songs = new ArrayList<String>();
    }

    public static void main(String args[])  
    { 
        //create a new musicServerPeer object
        MusicServerPeer peer = new MusicServerPeer();
        ///create a new TCPListener object and pass it a reference to this class
        peer.tcpListener = new TCPListener(peer);
        //start the thread tcp listener (this class implements runnable)
        new Thread(peer.tcpListener).start();
        //get the music servers ip from the user, entered as an argument when starting the program
        peer.serverIP = args[0];
        //update the peers song list
        peer.updateSongList();
        //register the peer with the server
        peer.register();
        while(peer.running)
        {
            //while the program is running show the menu to the user
            peer.menu();
        }
    } 
    
    /**
     * Register -   notifies the music server that the peer is online; aswell as sending
     *              the song list with it
     * 
     */
    public void register()
    {
        //socket for sending and receiving datagram packets
        DatagramSocket clientSocket;
        //datagram packet for connectionless packet delivery (udp)
        DatagramPacket sendPacket;
        //internet protocol address
        InetAddress IPAddress;         
        //new byte array object of size 1024 bytes (1kB)
        byte[] sendData = new byte[1024];            
        //message that contains the data to be sent
        String message = "Online," + songs.size();   
        //loop through all the strings in the songs array list
        for (String song : songs) 
        {
            //add each song string to the message 
            message = message + "," + song;
        }
        //set the sendData byte array equal to the message string converted to bytes
        sendData = message.getBytes(); 
        //enclose code that might throw an exception in a try block
        try 
        { 
            //set the ip equal to the server IP entered by the user converted to a InetAddress object
            IPAddress = InetAddress.getByName(serverIP);
            //create a new datagram socket object
            clientSocket = new DatagramSocket(); 
            //create a new datagram packet object, contrcuted by passing the arguments:
            //sendData - the byte array of the message to be sent
            //sendData.length - number of bytes in the message
            //IPAddress - ip of the server
            //serverPort - port number of the server
            sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, serverPort);
            //send the datagram packet along the connectionless client socket to the server
            clientSocket.send(sendPacket);  
            //close the client socket
            clientSocket.close(); 
        }
        //enclose exception handling code in a catch block
        catch (IOException e)
        {
            //print the error message of type IOException
            System.err.println("IOException " + e);
        }
    }
    
    /**
     * selectSong -     receive input from the user asking for a song to be requested
     * 
     * @return -        String input - the input from the user entered via System.in
     */
    public String selectSong()
    {
        //create a new input stream reader which converts bytes entered by the user
        //at the command line, and convertes these bytes into chracters
        InputStreamReader isr = new InputStreamReader(System.in);
        //wrap the input stream reader in a buffered reader which reads from the
        //character stream and buffers characters into readable strings
        BufferedReader br = new BufferedReader(isr);
        //string that will hold the users input entered
        String input = "";
        System.out.println("Enter the song you would like to transfer: ");
        //enclose code that might throw an exception in a try block
        try
        {
            //use the buffered readers method read line to read a line of text
            //which is then set equal to the string input; a line of text is one that
            //end in a '\n' (in the users cae pressing the carriage return key)
            input = br.readLine();
        }
        //enclose exception handling code in a catch block
        catch (IOException e)
        {
            //print the error message of type IOException
            System.err.println("IOException " + e);
        }
        //return the input string
        return input;
    }
    
    /**
     * requestPeersWithSong -   asks the server what peers have the a specific song
     * 
     * @param -                 String song - song to be sent
     * @return -                String ips - all the peers ip who have the song 
     */
    public String requestPeersWithSong(String song)
    {    
        //socket for sending and receiving datagram packets
        DatagramSocket clientSocket;   
        //datagram packet for connectionless packet delivery (udp)
        DatagramPacket sendPacket;
        //datagram packet for connectionless packet delivery (udp)
        DatagramPacket receivePacket;
        //internet protocol address
        InetAddress IPAddress; 
        //new byte array object of size 1024 bytes (1kB)         
        byte[] sendData = new byte[1024];      
        //new byte array object of size 1024 bytes (1kB) 
        byte[] receiveData = new byte[1024]; 
        //message that contains the data to be sent
        String message = "Song," + song;
        //set the sendData byte array equal to the message string converted to bytes
        sendData = message.getBytes();  
        //String ips currently equal to 0, that is, there is no peers with the song
        //requested currently
        String ips = "0";
        //enclose code that might throw an exception in a try block
        try
        {
            //set the ip equal to the server IP entered by the user converted to a InetAddress object
            IPAddress = InetAddress.getByName(serverIP);
            //create a new datagram socket object
            clientSocket = new DatagramSocket();
            //create a new datagram packet object, contrcuted by passing the arguments:
            //sendData - the byte array of the message to be sent
            //sendData.length - number of bytes in the message
            //IPAddress - ip of the server
            //serverPort - port number of the server
            sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, serverPort);   
            //send the datagram packet along the connectionless client socket to the server
            clientSocket.send(sendPacket);   
            //create a new datagram packet object, contrcuted by passing the arguments:
            //receiveData - the byte array of the message to be received
            //receiveData.length - the number of the bytes in the message
            receivePacket = new DatagramPacket(receiveData, receiveData.length);   
            //receive the datagram packet along the connectionless client socket from the server
            clientSocket.receive(receivePacket); 
            //set the ips string equal to the data received from the server
            ips = new String(receivePacket.getData());
            //close the client socket
            clientSocket.close();
        }   
        //enclose exception handling code in a catch block
        catch (IOException e)
        {
            //print the error message of type IOException
            System.err.println("IOException " + e);
        }
        //print a message to the user - substring is used to get the first charcter of the message
        //message syntax as defined is as follows: number of ips then ips seperated by commas
        //example: 3,192.168.52.1,192.168.56.32,192.168.52.12 
        //3 ips, with each seperated by commas
        System.out.println(ips.substring(0, 1) + " peers have the song requested");   
        //return the ips message received from the server
        return ips;
    }
    
    /**
     * notifyUpdateSongList -   notifies the server the peer has updated its song list
     * 
     */
    public void notifyUpdateSongList()
    {
        //socket for sending and receiving datagram packets
        DatagramSocket clientSocket;
        //datagram packet for connectionless packet delivery (udp)
        DatagramPacket sendPacket;
        //internet protocol address
        InetAddress IPAddress;         
        //new byte array object of size 1024 bytes (1kB)
        byte[] sendData = new byte[1024];      
        //message that contains the data to be sent
        String message = "Update," + songs.size();       
        //loop through all the strings in the songs array list
        for (String song : songs) 
        {
            //add each song string to the message 
            message = message + "," + song;
        }
        //set the sendData byte array equal to the message string converted to bytes
        sendData = message.getBytes(); 
        //enclose code that might throw an exception in a try block
        try 
        { 
            //set the ip equal to the server IP entered by the user converted to a InetAddress object
            IPAddress = InetAddress.getByName(serverIP);
            //create a new datagram socket object
            clientSocket = new DatagramSocket(); 
            //create a new datagram packet object, contrcuted by passing the arguments:
            //sendData - the byte array of the message to be sent
            //sendData.length - number of bytes in the message
            //IPAddress - ip of the server
            //serverPort - port number of the server
            sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, serverPort);
            //send the datagram packet along the connectionless client socket to the server
            clientSocket.send(sendPacket);  
            //close the client socket
            clientSocket.close(); 
        }
        //enclose exception handling code in a catch block
        catch (IOException e)
        {
            //print the error message of type IOException
            System.err.println("IOException " + e);
        }
    }
    
    /**
     * menu -   The user menu which allows the user to execute several functions
     *          of the program
     * 
     */
    public void menu()
    {
        //create a new input stream reader which converts bytes entered by the user
        //at the command line, and convertes these bytes into chracters
        InputStreamReader isr = new InputStreamReader(System.in);
        //wrap the input stream reader in a buffered reader which reads from the
        //character stream and buffers characters into readable strings
        BufferedReader br = new BufferedReader(isr);
        //int that will hold the users input entered
        int input = 0;
        //print the menu to assist the user in executing program functionality
        System.out.println("Select functionality: ");
        System.out.println("1: Play Song | 2: Show Songs | 3: Request Song | 4: Add Song | 5: Remove Song | 6: Exit");
        //enclose code that might throw an exception in a try block
        try
        {
            //use the buffered readers method read line to read a line of text
            //which is then cast to an int and set equal to the input; a line of text is one that
            //end in a '\n' (in the users cae pressing the carriage return key)
            input = Integer.valueOf(br.readLine());
        }
        //enclose exception handling code in a catch block
        catch (IOException e)
        {
            //print the error message of type IOException
            System.err.println("IOException " + e);
        }
        //switch statement for different values of input
        switch (input) 
        {
            //Play Song functionality
            case 1:
                //play song method
                playSong();
                break;
            //Show Songs functionality
            case 2:
                //requestSongList method
                requestSongList();
                break;
            //Request Song functionality
            case 3:
                //requestSong method
                requestSong();
                break;
            //Add Song functionality
            case 4:
                //addSong method
                addSong();
                break;
            //Remove Song functionality
            case 5:
                //removeSong method
                removeSong();
                break;
            //Exit functionality
            case 6:
                //exit method
                exit();
                break;
            //Do nothing
            default:
        }
    }

    /**
     * exit -   ends the program
     * 
     */
    public void exit()
    {
        //print exiting message to the user
        System.out.println("Exiting...");
        //finish the tcp listener thread
        tcpListener.finish();
        //set the running boolean to false
        running = false;
    }
    
    /**
     * getSongsFromFile -   gets all the mp3 files in the song directory
     * 
     * @return -            ArrayList<String> - a string list of all the songs in the songs directory
     */
    public ArrayList<String> getSongsFromFile()
    {
        //create a new array list object of strings
        ArrayList<String> results = new ArrayList<String>();  
        //create a new File object equal to the directory name songs
        File songsDir = new File("songs");
        //check whether the songs directory exists
        if (songsDir.exists())
        {
            //create an array of files use the method listFiles to return an array
            //of files in the songs directory
            File[] files = songsDir.listFiles();
            //If this pathname does not denote a directory, then listFiles() returns null.  
            //loop through the array of Files
            for (File file : files) 
            {
                //checks whether the pathname is a file or a directory; true if file
                if (file.isFile()) 
                {
                    //checks whether the file is a song file (ending with .mp3)
                    if (file.getName().endsWith(".mp3"))
                    {                   
                        //add the files name to the results
                        results.add(file.getName());
                    }
                }
            }
        }
        else
        {
            //the songs directory does not exist, therefore create the directory
            //for future use using the method mkdir
            songsDir.mkdir();
        }
        //return the string array list of files found in the song directory
        return results;
    }
    
    /**
     * An example of a method - replace this comment with your own
     * 
     * @param  y   a sample parameter for a method
     * @return     the sum of x and y 
     */
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
    
    /**
     * An example of a method - replace this comment with your own
     * 
     * @param  y   a sample parameter for a method
     * @return     the sum of x and y 
     */
    public void updateSongList()
    {
        songs = getSongsFromFile();
    }
    
    /**
     * An example of a method - replace this comment with your own
     * 
     * @param  y   a sample parameter for a method
     * @return     the sum of x and y 
     */
    public void removeSong()
    {
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        Integer numberOfSongs = songs.size();
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
            if(numberOfSongs<songs.size())
            {
                System.out.println("Perhaps you meant to add a song?");
            }
            else if(numberOfSongs>songs.size())
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
    
    /**
     * An example of a method - replace this comment with your own
     * 
     * @param  y   a sample parameter for a method
     * @return     the sum of x and y 
     */
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
    
    /**
     * An example of a method - replace this comment with your own
     * 
     * @param  y   a sample parameter for a method
     * @return     the sum of x and y 
     */
    public void requestSong()
    {
        String songRequested = selectSong(); //ask the peer what song they would like
        String message = requestPeersWithSong(songRequested); //ask the server what peers have the song
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
    
    /**
     * An example of a method - replace this comment with your own
     * 
     * @param  y   a sample parameter for a method
     * @return     the sum of x and y 
     */
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
    
    /**
     * An example of a method - replace this comment with your own
     * 
     * @param  y   a sample parameter for a method
     * @return     the sum of x and y 
     */
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
