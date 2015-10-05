import java.util.concurrent.*;
import java.util.*;
import java.net.*;

// source http://stackoverflow.com/questions/11547082/fastest-way-to-scan-ports-with-java
// code modified by A Bayley August 20th 2015

public class Test
{
    public static void main(String [] args) throws Exception
    {
        final ExecutorService es = Executors.newFixedThreadPool(20);
        final String ip = args[0];
        final int portStart = Integer.valueOf(args[1]);
        final int portEnd = Integer.valueOf(args[2]);
        final int timeoutMin = 100;
        final int timeoutMax = 500;
        final List<Future<Boolean>> futures = new ArrayList<>();
        final ArrayList<Integer> portToScan = new ArrayList<>();
        
        for (int port = portStart; port < portEnd;) 
        {
            int portNext = (int)(Math.random()*(portEnd-portStart)) + portStart;
            int timeout = (int)(Math.random()*(timeoutMax-timeoutMin)) + timeoutMin;
            
            if (!(portToScan.contains(portNext))) 
            {
                portToScan.add(portNext);
                System.out.println("" + portToScan.get(port - portStart));
                futures.add(portIsOpen(es, ip, portToScan.get(port - portStart), timeout));
                port++;
            }
        }
  
        es.shutdown();
        int openPorts = 0;
        
        for (final Future<Boolean> f : futures) 
        {
            if (f.get()) 
            {
                openPorts++;
            }
        }
        
        System.out.println("There are " + openPorts + " open ports on host " + ip +  
        " probed with a timeout between " + timeoutMin + "ms and " + timeoutMax + "ms");
    }
    
    
    public static Future<Boolean> portIsOpen(final ExecutorService es, 
    final String ip, final int port, final int timeout) 
    {
        return es.submit(new Callable<Boolean>() 
        {
            @Override public Boolean call() {
            try 
            {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(ip, port), timeout);
                socket.close();
                System.out.println("open port found " + port);
                return true;
            } 
            catch (Exception ex) 
            {
                return false;
            }
        }
   });
}
}