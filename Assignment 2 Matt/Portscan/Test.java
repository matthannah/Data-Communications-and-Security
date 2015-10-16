import java.util.concurrent.*;
import java.util.*;
import java.net.*;
import java.io.*;
import java.text.*;

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
     * @param      args[0]  :    ip address 
     * @param      args[1]  :    number of ips to be scanned
     * @param      args[2]  :    Range of ports to be scanned MIN 
     * @param      args[3]  :    Range of ports to be scanned MAX
     * 
     * @return     void
     */
    public static void main(String [] args) throws Exception
    {  
        final String ip = args[0]; // ip address which is received via the command line
        final Integer range = Integer.valueOf(args[1]);
        final List<Future<Boolean>> futures = new ArrayList<>(); // list of booleans
        List<Integer> ports = new ArrayList<>(); // list of integers of ports to be scanned
        final ExecutorService es = Executors.newFixedThreadPool(20); // start 20 threads
        //create a list of ips to be scanned
        ArrayList<String> ips = new ArrayList<String>();
        //add the first ip entered by the user
        ips.add(ip);
        //get the last number of the ip address
        String parts[] = ip.split("\\.");
        //add each ip to the list
        for (int i = 1; i < range; i++)
        {
            Integer lastNumber = Integer.valueOf(parts[3]) + i;
            ips.add(parts[0] + "." + parts[1] + "." + parts[2] + "." + lastNumber);
        }
        
        // this loop adds all the ports to be scanned into a list
        // ports to be scanned is determined by the user entering the
        // range in the command line
        for (int i =  Integer.parseInt(args[2]); i <= Integer.parseInt(args[3]); i++)
        {
            ports.add(i); //adds port to list
        }
        
        //if the ip addresses are in the subnet
        if(inSubnet(ips))
        {
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH,mm,ss");

            // Get the date today using Calendar object.
            Date today = Calendar.getInstance().getTime();        
            // Using DateFormat format method we can create a string 
            // representation of a date with the defined format.
            String date = df.format(today);
            
            File scanDir = new File("Scans");
            if(!scanDir.exists())
            {
                scanDir.mkdir();
            }
            
            String filename = scanDir.getAbsolutePath() + "/" +date + " Port Scan.txt";
            FileOutputStream fos = new FileOutputStream(filename);
            PrintWriter outToFile = new PrintWriter(fos, true);
            
            for (String address : ips)
            {
                Random random = new Random(); // creates a random object
                int min = 100;
                int max = 500;
                int randomNumber = random.nextInt(max - min) + min; // assign a random number between range
                                                                    // min and max
                final int timeout = randomNumber; // set the timeout equal to the random number
                
                Collections.shuffle(ports); // randomize the list, effectively randomizes the order in which
                                            // ports are scanned
                                            
                ArrayList<Integer> open = new ArrayList<>();
                
                          
                if (ports.size() > 1 && Integer.parseInt(args[2]) > 0)
                {
                    for (int i =  Integer.parseInt(args[2]); i <= Integer.parseInt(args[3]); i++) 
                    {
                        //System.out.println("" + ports.get(i-1)); //prints the port about to be scanned
                        futures.add(portIsOpen(es, address, ports.get(i-1), timeout, open)); // add a future boolean
                                                                                  // to the list
                    }
                }
                           
                int openPorts = 0; // number of openPorts currently
        
                for (final Future<Boolean> f : futures) //for all data in the list of futures
                {
                    if (f.get()) // return true if port is open
                    {
                        openPorts++; // add one to the open port count
                    }
                }
                
                // prints message to system detailing scan result
                System.out.println("Scan Results for " + address + ": " + openPorts + " open ports, probed with timeout of " + timeout + "ms");  
                outToFile.println("Scan Results for " + address + ": " + openPorts + " open ports, probed with timeout of " + timeout + "ms");
                for (Integer p : open)
                {
                    outToFile.println(p + " open");
                    System.out.println(p + " open");
                }
                outToFile.println("");
                System.out.println("");
                futures.clear();
            }
            outToFile.close();
        }
        else
        {
            System.out.println("IP addresses entered are not in the subnet");
        }
        es.shutdown(); // shutdown threads gracefully - meaning that they will finish their tasks 
                       // and then terminate, in our case they will finish establishing connections
                       // first
                       
        System.out.println("Scan Complete");
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
    final String ip, final int port, final int timeout, ArrayList<Integer> open) 
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
                    open.add(port);
                    return true; 
                } 
                catch (Exception ex) // an exception is thrown if the connection process exceeds the timeout time
                {
                    return false; 
                }
            }
        });
    }
    
    public static boolean inSubnet(ArrayList<String> ips)
    {
        //logic of whether or not the ip range entered is in the subnet
        boolean inSubnet = true;
        
        for (String address : ips)
        { 
            //get the last number of the ip address
            String parts[] = address.split("\\.");
            
            if (Integer.valueOf(parts[3]) > 255)
            {
                System.out.println("The ips went over 255");
                inSubnet = false;
            }
        }
        
        //now check if the ips are in the local subnet
        //first get the network
        InetAddress local = null;
        short bitsSubnet = 0;
        try
        {
            local = InetAddress.getLocalHost();
            NetworkInterface ni = NetworkInterface.getByInetAddress(local);
            //bitsSubnet = ni.getInterfaceAddresses().get(0).getNetworkPrefixLength();
            List<InterfaceAddress> ia = ni.getInterfaceAddresses();
            for (InterfaceAddress inter : ia)
            {
                if(inter.getAddress().getHostAddress().equals(local.getHostAddress()))
                {
                    bitsSubnet = inter.getNetworkPrefixLength();
                    break;
                }
            }
        }
        catch (Exception e)
        {
            inSubnet = false;
            System.out.println("an exception was thrown trying to generate a mask");
            System.err.println(e);
        }

        //check masks against the list of ips

        for (String address : ips)
        {
            try
            {
                byte[] bytes = InetAddress.getByName(address).getAddress();
                int i = ((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) | ((bytes[2] & 0xFF) << 8)  | ((bytes[3] & 0xFF) << 0);
                bytes = local.getAddress();
                int subnet = ((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) | ((bytes[2] & 0xFF) << 8)  | ((bytes[3] & 0xFF) << 0);
                int mask = -1 << (32 - bitsSubnet);
                if ((i & mask) != (subnet & mask)) 
                {
                    System.out.println("host bits of ips entered did not match network");
                    inSubnet = false;
                }
            }
            catch (Exception e)
            {
                System.err.println(e);
            }
        }
        return inSubnet;
    }
}
