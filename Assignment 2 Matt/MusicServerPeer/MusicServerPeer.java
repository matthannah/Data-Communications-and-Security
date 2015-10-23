import java.io.*; 
import java.net.*;
import java.util.*;
import java.awt.Desktop;
/**
 * Class that handles all user functionality of the peer program
 * 
 * @author Matthew Hannah
 * @version 14/10/2015
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
        System.out.println("1: Play Song | 2: Show Songs | 3: Request Song | 4: Add/Remove Song | 5: Exit");
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
            //Add/Remove Song functionality
            case 4:
                //addOrRemoveSong method
                addOrRemoveSong();
                break;
            //exit functionality
            case 5:
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
        //notify offline with the server
        goOffline();
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
     * addOrRemoveSong -    prompts the user to move a song into or out of the songs directory
     *                      then notifies the server that its song list has been updated
     * 
     */
    public void addOrRemoveSong()
    {
        //create a new input stream reader which converts bytes entered by the user
        //at the command line, and convertes these bytes into chracters
        InputStreamReader isr = new InputStreamReader(System.in);
        //wrap the input stream reader in a buffered reader which reads from the
        //character stream and buffers characters into readable strings
        BufferedReader br = new BufferedReader(isr);
        //string that will hold the users input entered, default to c
        String input = "c";
        //the number of elements in the songs array list
        Integer numberOfSongs = songs.size();
        //prints message to user to assist in input
        System.out.println("press 'c' to cancel");
        System.out.println("Add or Remove a song from the songs directory and enter any other key to continue");
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
        //if the input does not equal "c" -cancel
        if (!input.equals("c"))
        {
            //run the update song list method
            updateSongList();
            //if the songs array list has increased in size
            if(numberOfSongs<songs.size())
            {
                //print message to the user
                System.out.println("Song added - notifying server");
                //run the notifyUpdateSongList method
                notifyUpdateSongList();
            }
            //if the songs array list has decreased in size
            else if(numberOfSongs>songs.size())
            {
                //print message to the user
                System.out.println("Song removed - notifying server");
                //run the notifyUpdateSongList method
                notifyUpdateSongList();
            }
            //if the songs array list has remained the same size
            else
            {
                //print message to the user
                System.out.println("Did you forget to add or remove a song?");
            }
        }
        else
        {
            //the c key had been enteres - cancel
            //print message to the user
            System.out.println("operation cancelled");
        }
    }
    
    /**
     * updateSongList -     sets the songs array list equal to the get songs from file method
     * 
     */
    public void updateSongList()
    {
        //set songs equal to the array list returned from the get songs from file method
        songs = getSongsFromFile();
    }
    
    /**
     * requestSongList -    requests the list of songs the server knows about
     * 
     */
    public void requestSongList()
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
        String message = "List";      
        //set the sendData byte array equal to the message string converted to bytes
        sendData = message.getBytes(); 
        //String songList currently equal to 0, that is, there is no songs
        String songList = "0";
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
            songList = new String(receivePacket.getData());
            //close the client socket
            clientSocket.close();
        }   
        //enclose exception handling code in a catch block
        catch (IOException e)
        {
            //print the error message of type IOException
            System.err.println("IOException " + e);
        }
        //print a message to the user - the split method is used because the message
        //syntax is divided by commas, and the first part being the number of
        //data sent
        //example 4 songs: 4,song1,song2,song3,song4
        songList = songList.trim();
        String parts[] = songList.split(",");   
        System.out.println("------SONG LIST-------");
        for( int i = Integer.valueOf(parts[0]); i > 0; i--)
        {
            //print all the songs received
            System.out.println(parts[i]);
        }  
    }
    
    /**
     * requestSong -    requests a song from a peer
     * 
     */
    public void requestSong()
    {
        //ask the peer what song they would like
        String songRequested = selectSong(); 
        //check if you already have this song
        boolean hasSong = false;
        //loop through songs
        for (String song : songs)
        {
            //if songs equal
            if(songRequested.equals(song))
            {
                //you have the song
                hasSong = true;
                //print message to user
                System.out.println("You already have that song!");
            }
        }
        if (!hasSong)
        {
            //ask the server what peers have the song
            String message = requestPeersWithSong(songRequested); 
            //if the message doesn't start with 0, that is atleast one peer has the song requested
            if (!message.startsWith("0"))
            {
                //split method is used because the message syntax is divided by commas, 
                //and the first part being the number of data sent
                //example 4 peers with song requested: 4,ip1,ip2,ip3,ip4
                String parts[] = message.split(",");
                //create a new string array list object
                ArrayList<String> peersWithSong = new ArrayList<String>();
                //print message to user
                System.out.println("----- PEERS WITH SONG -----");
                //loop through all data in the message
                for( int i = Integer.valueOf(parts[0]); i > 0; i--)
                {
                   //add each ip to the array list of peers with songs
                   peersWithSong.add(parts[i].substring(1).trim());
                   //print each peer with song
                   System.out.println(parts[1].substring(1).trim());
                } 
                //another check to make sure the list isn't empty - this probably isn't needed
                if (!peersWithSong.isEmpty()) {
                    //create a new input stream reader which converts bytes entered by the user
                    //at the command line, and convertes these bytes into chracters
                    InputStreamReader isr = new InputStreamReader(System.in);
                    //wrap the input stream reader in a buffered reader which reads from the
                    //character stream and buffers characters into readable strings
                    BufferedReader br = new BufferedReader(isr);
                    //string that will hold the users input entered
                    String input = "";
                    //prints message to user to assist in input
                    System.out.println("Enter the ip of the peer you'd like the song from");
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
                    //trim any whitespace
                    input = input.trim();
                    //check if the input was equal to one of the ips
                    if(peersWithSong.contains(input))
                    {
                    	//print a message to the user
                        System.out.println("Requesting song from: " + p);
                        //requests the song from the ip entered
                        TCPRequestSong(p, songRequested);
                        return;
          
                    }
                    System.out.println("Error: ip entered did not match a peer");
                }
            }
        }
    }
    
    /**
     * TCPRequestSong -     creates a TCP connection with a peer and exchanges messages
     *                      to receive a song file which was requested
     * 
     * @param -             String ip - the ip of the peer which the songs will be transfered from
     * @param -             String songRequested - the song requested
     */
    public void TCPRequestSong(String ip, String songRequested) 
    {  
        //socket - the endpoint for communication between client and server (peers)
        Socket clientSocket;
        //data output stream that allows string to be written to output stream
        DataOutputStream outToPeer;
        //inputstream of bytes
        InputStream is;
        //file output stream used for writing data to a file (in our case .mp3)
        FileOutputStream fos;
        //buffered output stream that writes bytes to a stream
        BufferedOutputStream bos;
        //an output stream whos buffere automatically grows as data is written to it
        ByteArrayOutputStream baos;
        //a single byte object
        byte[] aByte = new byte[1];
        //number of bytes read
        int bytesRead;
        //message to send to the peer requesting the specific song entered as an argument
        //to the method
        String message = "SendSong," + songRequested +",\n"; 
        //pathname of the song file to be written
        final String fileOutput = "songs/"+songRequested;
        //enclose code that might throw an exception in a try block
        try 
        {
            //create a new socket object with port equal to 6789, and ip equal to the ip
            //entered as argument to the mthod
            clientSocket = new Socket(ip, 6789);  
            //create a new data output stream object and pass in the sockets output stream
            outToPeer = new DataOutputStream(clientSocket.getOutputStream()); 
            //write the message to the clientsockets output stream
            outToPeer.writeBytes(message);
            //print a message to the user
            System.out.println("Receiving song...");
            //create a new buffered array output  stream
            baos = new ByteArrayOutputStream();
            //set the input stream equal to the client sockets input stream
            is = clientSocket.getInputStream();
            //create a new file output stream to the pathname specified as fileOutput
            fos = new FileOutputStream(fileOutput);
            //wrap the file output stream in a buffered output stream object
            bos = new BufferedOutputStream(fos);
            //read first byte and write it into aByte
            bytesRead = is.read(aByte, 0, aByte.length);
            //keep reading and writing bytes until the end of the stream has been reached (-1)
            do 
            {
                //write aByte into the byte array output stream
                baos.write(aByte);
                //read the next byte and write it into aByte
                bytesRead = is.read(aByte);         
            } while (bytesRead != -1); 
            //write all bytes in the buffered array output stream into the buffered output stream
            bos.write(baos.toByteArray());
            //forces any buffered output bytes to be written to the output stream
            bos.flush();
            //close the output stream
            bos.close();
            //close the client socket
            clientSocket.close();
        }
        //enclose exception handling code in a catch block
        catch (IOException e)
        {
            //print the error message of type IOException
            System.err.println("IOException " + e);
        }
        //print message to the user
        System.out.println("Song transfer complete");
        //add the song received to the song list
        songs.add(songRequested);
        //notify the server that the user now has a new song
        notifyUpdateSongList();
    }
    
    /**
     * playSong -   plays the song selected by the user
     * 
     */
    public void playSong()
    {
        //print message to the user
        System.out.println("----- Enter the name of the song you'd like to play -----");
        //loop through all the songs in the songs array list
        for (String song : songs)
        {
            //print the song to the user
            System.out.println(song);
        }
        System.out.println("---------------------------------------------------------");
        //create a new input stream reader which converts bytes entered by the user
        //at the command line, and convertes these bytes into chracters
        InputStreamReader isr = new InputStreamReader(System.in);
        //wrap the input stream reader in a buffered reader which reads from the
        //character stream and buffers characters into readable strings
        BufferedReader br = new BufferedReader(isr);
        //string that will hold the users input entered
        String input = "";
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
        //boolean used for method flow (whether or not the song entered exists)
        boolean songExists = false;
        //loops through all the songs the user has
        for (String song : songs)
        {
            //if the song the user entered is in the song list
            if (input.equals(song))
            {
                //set song exists equal to true
                songExists = true;
            }
        }
        //if the song exists
        if (songExists)
        {
            //create a new file object with pathname equal to the user input
            File f = new File("songs/"+input);
            //if the file still exists
            if (f.exists()) 
            {
                //if the class is supported
                if (Desktop.isDesktopSupported()) 
                {
                    //enclose code that might throw an exception in a try block
                    try
                    {
                        //return the instance of the desktop and attempt to open
                        //the music file with the default application associated
                        //with the file extension
                        Desktop.getDesktop().open(f);
                    }
                    //enclose exception handling code in a catch block
                    catch (Exception e)
                    {
                        //print the error message of type Exception
                        System.err.println("Exception " + e);
                    }
                } 
                //the file no longer exists
                else
                {
                    System.out.println("File does not exists!");
                }
 
            }
        }
        //the songExists was false, therefore the user entered an incorrect song name
        else 
        {
            System.out.println("Song name does not exist");
        }
    }
    
    public void goOffline()
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
        String message = "Offline";   
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
}
