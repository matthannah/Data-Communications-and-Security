import java.io.*;
import java.net.*;
/**
 * TCP Listener thread object that listens on port 6789 and replies to song request
 * messages
 * 
 * @author Matthew Hannah
 * @version 14/10/2015
 */
public class TCPListener implements Runnable
{
    private MusicServerPeer musicServerPeer;
    private ServerSocket serverSocket;
    private boolean listen = true;
    
    /**
     * Constructor for objects of class TCPListener
     */
    public TCPListener(MusicServerPeer musicServerPeer)
    {
        this.musicServerPeer = musicServerPeer;
    }
    
    /**
     * run -    method that must be implemented as this class implements a
     *          runnable interface
     * 
     */
    public void run()
    { 
        //string that will contain the messages received 
        String message;
        //enclose code that might throw an exception in a try block
        try
        {
            //create a new socket object on port 6789
            serverSocket = new ServerSocket(6789);
        }
        //enclose exception handling code in a catch block
        catch (IOException e)
        {
            //print the error message of type IOException
            System.err.println("IOException " + e);
        }
        //while listening is true
        while (listen)
        {
            //enclose code that might throw an exception in a try block
            try
            {   
                //listen for a connection to be made to this socket, this method blocks
                Socket connectionSocket = serverSocket.accept();  
                //create a new input stream reader that reads the sockets input stream
                InputStreamReader isr = new InputStreamReader(connectionSocket.getInputStream());
                //create a new BufferedReader object that wraps the input stream reader
                BufferedReader inFromPeer = new BufferedReader(isr);   
                //set the message equal to the string return of readline which reads
                //from the input stream
                message = inFromPeer.readLine();
                //print message that will notify the user a message has been received
                System.out.println("received TCP message");
                //if the message is the correct syntax
                if (message.startsWith("SendSong"))
                {
                    //split the message which is seperated by commas
                    String parts[] = message.split(",");
                    System.out.println("Sending song " + parts[1] + " to: " + connectionSocket.getInetAddress().getHostName() + "...");
                    //string that represents the pathname; parts[1] is the song requested
                    String songFile = "songs/"+parts[1];
                    //create a new file object equal to the song in file
                    File myFile = new File(songFile);
                    //create a new byte array object equal to the size of the file requested
                    byte[] mybytearray = new byte[(int) myFile.length()];
                    //create a file input stream to the file
                    FileInputStream fis = new FileInputStream(myFile);
                    //wrap the file input stream in a buffered input stream
                    BufferedInputStream bis = new BufferedInputStream(fis);
                    //read the file from the input stream and write it to the byte array
                    bis.read(mybytearray, 0, mybytearray.length);
                    //get the sockets output stream
                    OutputStream os = connectionSocket.getOutputStream();
                    //write the byte array to the output stream
                    os.write(mybytearray, 0, mybytearray.length);
                    //forces any buffered output bytes to be written to the output stream
                    os.flush();
                    //close the connection socket with the peer
                    connectionSocket.close();  
                    //print a message to the user
                    System.out.println("Song sent - size: " +mybytearray.length+"bytes");
                }
            }
            //enclose exception handling code in a catch block
            catch (IOException e)
            {
                //print the error message of type IOException
                System.err.println("IOException " + e);
            }
        }
        //enclose code that might throw an exception in a try block
        try
        {
            //close the server socket
            serverSocket.close();
        }
        //enclose exception handling code in a catch block
        catch (IOException e)
        {
            //print the error message of type IOException
            System.err.println("IOException " + e);
        }
    }
    
    /**
     * finish -     ends the listening (closes the server socket)
     * 
     */
    public void finish()
    {
        //set listen to false
        listen = false;
        //enclose code that might throw an exception in a try block
        try
        {
            //close the server socket
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
