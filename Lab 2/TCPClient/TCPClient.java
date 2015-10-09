
/**
 * Write a description of class TCPClient here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
import java.io.*;
import java.net.*;

class TCPClient
{
  public static void main(String argv[]) throws Exception
  {
   String sentence;
   String modifiedSentence;
   BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));
   InetAddress address = InetAddress.getByName("10.1.51.131");
   Socket clientSocket = new Socket(address, 6789);
   DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
   BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
   sentence = inFromUser.readLine();
   outToServer.writeBytes(sentence + '\n');
   modifiedSentence = inFromServer.readLine();
   System.out.println("FROM SERVER: " + modifiedSentence);
   clientSocket.close();
  }
}