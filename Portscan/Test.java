import java.util.concurrent.*;
import java.util.*;
import java.net.*;

/**
 * The class Test implements a way to scan a number of ports defined by the user at the command line. 
 * 
 * 
 * @author Matt Hannah
 * @version 11/09/2015
 */
public class Test
{  
    /**
     * Main method - runs the port scan (no data vaildation is implemented)
     * 
     * 
     * @param      arg[0]  :    ip address 
     * @param      arg [1] :    Range of ports to be scanned MIN 
     * @param      arg[2]  :    Range of ports to be scanned MAX
     * 
     * @return     void
     */
    public static void main(String [] args) throws Exception
    {  
        final ExecutorService es = Executors.newFixedThreadPool(20); // start 20 threads
        final String ip = args[0]; // ip address which is received via the command line
        final List<Future<Boolean>> futures = new ArrayList<>(); // list of booleans
        List<Integer> ports = new ArrayList<>(); // list of integers of ports to be scanned
        
        Random random = new Random(); // creates a random object
        int min = 100;
        int max = 500;
        int randomNumber = random.nextInt(max - min) + min; // assign a random number between range
                                                            // min and max
        final int timeout = randomNumber; // set the timeout equal to the random number
        
        // this loop adds all the ports to be scanned into a list
        // ports to be scanned is determined by the user entering the
        // range in the command line
        for (int i =  Integer.parseInt(args[1]); i <= Integer.parseInt(args[2]); i++)
        {
            ports.add(i); //adds port to list
        }
        
        Collections.shuffle(ports); // randomize the list, effectively randomizes the order in which
                                    // ports are scanned
        
        if (ports.size() > 1 && Integer.parseInt(args[1]) > 0)
        {
            for (int i =  Integer.parseInt(args[1]); i <= Integer.parseInt(args[2]); i++) 
            {
                System.out.println("" + ports.get(i-1)); //prints the port about to be scanned
                futures.add(portIsOpen(es, ip, ports.get(i-1), timeout)); // add a future boolean
                                                                          // to the list
            }
        }
        es.shutdown(); // shutdown threads gracefully - meaning that they will finish their tasks 
                       // and then terminate, in our case they will finish establishing connections
                       // first
                       
        int openPorts = 0; // number of openPorts currently

        for (final Future<Boolean> f : futures) //for all data in the list of futures
        {
            if (f.get()) // return true if port is open
            {
                openPorts++; // add one to the open port count
            }
        }
        
        // prints message to system detailing scan result
        System.out.println("There are " + openPorts + " open ports on host " + ip +  
        " probed with a timeout of " + timeout + "ms");
    }
       
     /**
     * portIsOpen - attempts to establish a connection with open port 
     * 
     * @param      ip      :    ip address to be scanned for open ports
     * @param      port    :    the port to be scanned
     * @param      timeout :    how long to wait for the connection to be established
     * 
     * @return     true    :    If a connection was established return true
     * @return     false   :    If an exception was thrown due to an connection not being 
     *                          established return false
     */
    public static Future<Boolean> portIsOpen(final ExecutorService es, 
    final String ip, final int port, final int timeout) 
    {
        return es.submit(new Callable<Boolean>() 
        {
            @Override public Boolean call() 
            {
                try 
                {
                    Socket socket = new Socket(); // create a new socket object
                    socket.connect(new InetSocketAddress(ip, port), timeout); // attempt to connect to a socket at specified ip and port
                    socket.close(); // close the socket
                    System.out.println("open port found " + port); // if a connection was established (no exception thrown) print success to screen
                    return true; 
                } 
                catch (Exception ex) // an exception is thrown if the connection process exceeds the timeout time
                {
                    return false; 
                }
            }
        });
    }
    
    
    public static Future<Boolean> checkHosts(final ExecutorService es, 
    final String ip, final int timeout) 
    {
       if (InetAddress.getByName(ip).isReachable(timeout)){
           return true;
       }
    }
}
