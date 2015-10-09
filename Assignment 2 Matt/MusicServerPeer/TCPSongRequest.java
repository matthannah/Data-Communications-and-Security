import java.io.*;
import java.net.*;
/**
 * Write a description of class TCPSongRequest here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class TCPSongRequest
{
    public TCPSongRequest()
    {
        
    }

    public void run() {
        try {
            String sentence;   
            String modifiedSentence;   
            BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));   
            Socket clientSocket = new Socket("localhost", 6789);   
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());   
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));   
            sentence = inFromUser.readLine();   
            outToServer.writeBytes(sentence + '\n');   
            modifiedSentence = inFromServer.readLine();   
            System.out.println("FROM SERVER: " + modifiedSentence);   
            clientSocket.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
