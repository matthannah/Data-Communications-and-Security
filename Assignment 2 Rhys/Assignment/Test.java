import java.util.concurrent.*;
import java.util.*;
import java.net.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

// source http://stackoverflow.com/questions/11547082/fastest-way-to-scan-ports-with-java
// code modified by A Bayley August 20th 2015
// coed modified by R Hill October 5th 

public class Test
{
    public static void main(String [] args) throws Exception
    {
        //Starts up an executor service.
        final ExecutorService es = Executors.newFixedThreadPool(20);
        
        //Sets the first IP that the user wants to scan according to the command input.
        final String startIp = args[0];
        
        //Sets the number of additional IPs that the user wants to scan.
        final int numIp = Integer.valueOf(args[1]);
        
        //Sets the start of the range of ports the user wants to scan.
        final int portStart = Integer.valueOf(args[2]);
        
        //Sets the end of the range of ports the user wants to scan.
        final int portEnd = Integer.valueOf(args[3]);
        
        //Sets the minimum timeout that can be used for testing ports.
        final int timeoutMin = 100;
        
        //Sets the maximum timeout that can be used for testing ports.
        final int timeoutMax = 500;
        
        //Sets up a list of future booleans that are used to hold the results of port tests.
        //A futrue list is used so that test can be run simultaneously and to ensure that other
        //functins wait until a test is complete before trying to access it's result.
        final List<Future<Boolean>> futures = new ArrayList<>();
        
        //Sets up a list of integers which port numbers can be inserted into in a random order.
        //A list is used for randomisation so that the contains csn be used to ensure no ports are
        //checked twice. Acts as a record of which ports have already been checked.
        final List<Integer> portToScan = new ArrayList<>();
        
        //Initialises an integer which is a count of how many of the port tests returned a true.
        int openPorts = 0;
             
        //Saves the date and time that the scan was started.
        Date date = new Date();
        
        //Generates a new filename for the report based on the time it's started
        String fileName = "Port Scan Report" + date.toString().replace(":", "-") + ".txt";
                      
        //Start an output stream to pass data to the text file and gives it a name including the date
        FileOutputStream out = new FileOutputStream(fileName);
        
        //Initialises a string to be used when writing data to the report file.
        String dataToByte = "Port Scan Report\r\n\r\n" + "Report generated: " 
        + date.toString().replace(":", "-") + "\r\nStarting IP Address: " + startIp + "\r\nRange of IP Address': "
        + numIp + "\r\nRange of Ports: from " + portStart + " to " + portEnd + "\r\n\r\n";
        
        //Initialises a byte array which can be used to write data to a text file which contains the
        //report for the port scanner
        byte dataToWrite[] = dataToByte.getBytes();
        
        //Writes the Introduction to the Port Scanner Report
        out.write(dataToWrite);
                
        //Loops through entire port scan for each additional IP the user wants to check 
        for (int ipCount = 0; ipCount < numIp; ipCount++)
        {
            //Increments which IP is used for the port tests by spliting the string into parts and adding
            //to the second have before rejoining them into a string
            
            //Creates a string made up of the start of the IP address the user provided
            String firstHalfOfIp = startIp.split("\\.")[0] + "." + startIp.split("\\.")[1] + "." + startIp.split("\\.")[2] + ".";
            
            //Creates another string which is the end of the IP the user provided
            String secondHalfOfIp = startIp.split("\\.")[3];
            
            //Converts the end of the IP into an integer and adds the ipCount to increment the IP being
            //used for port tests.
            int currentIp = Integer.valueOf(secondHalfOfIp) + ipCount;
            
            //Recombines the two pieces of the IP with into the now incremented IP
            String nextIp = firstHalfOfIp + Integer.toString(currentIp);
            
            //Checks that the IP id from the local subnet
            //@TODO ^that
            
            //Prints a message to the console to show that the next IP address is being used for a round
            //of port tests
            System.out.println("Port scan results for IP address " + nextIp);
            
            //Creates a string that will be converted to bytes then written to the report file.
            dataToByte = "Port Scan Results for IP Address " + nextIp + "\r\n\r\n";
            
            //Converts the string into bytes.
            dataToWrite = dataToByte.getBytes();
            
            //Writes the data to the report file.
            out.write(dataToWrite);
            
            //Loop counts from the starting port to the finishing port and a unique random port is tested
            //each time the counter increments.
            for (int port = portStart; port <= portEnd;) 
            {
                //Generates a random number between the start and end ports. This is done by getting a random
                //number between 0 and the difference between the start and end port. This is then added to
                //the end port.
                int portNext = (int)(Math.random()*(portEnd+1-portStart)) + portStart;
                
                //Generates a random number to be used as the timeout for port tests. This is done in the
                //same way as the port numbers.
                int timeout = (int)(Math.random()*(timeoutMax+1-timeoutMin)) + timeoutMin;
                
                //Checks to see if the port number generated is a unique number. If it is not then the loop
                //is run again without incrementing the counter so that a new number can be generated.
                if (!(portToScan.contains(portNext))) 
                {
                    //Adds the newly made unique random port number to the list of scaned ports
                    portToScan.add(portNext);
                    
                    //Prints to the console the latest port number added to this list
                    //Now only used for testing.
                    //System.out.println("" + portToScan.get(port - portStart));
                    
                    //Runs the function portIsOpen using the IP provided by the user, the latest addition
                    //to the list of ports that need to be scaned, and the last random timeout value made.
                    //This function is the one responsible for actually testing ports. It returns a boolean
                    //which is added to the futures list. This means that multiple tests can be run at the  
                    //same time and any functions that relie on the result will wait until the test is 
                    //actually complete.
                    futures.add(portIsOpen(es, nextIp, portToScan.get(port - portStart), timeout, out));
                    
                    //Increments the counter. Only used when a test of a unique port takes place
                    port++;
                }
            }      
                        
            //Checks the result of each port test undertaken. As the check is done against a list of future
            //values, the check will only be carried out once the result is actually ready.
            for (final Future<Boolean> f : futures) 
            {
                //Checks the next outcome of a port test. If the reslut is is not present it will wait until
                //it is.
                if (f.get()) 
                {
                    //Increments the count of ports that are open for each successful test.
                    openPorts++;
                }
            }
            
            //Checks if only a single port is open. This is only done so that the syntaxt and gramar used
            //for displaying results on the console makes sense.
            if (openPorts == 1)
            {   
                //Prints a message to the console containing, the number ports found to be open, the IP that
                //was checked, and the range of values used for timeouts during testing. 
                System.out.println("There is " + openPorts + " open port on host " + nextIp +  
                " probed with a timeout between " + timeoutMin + "ms and " + timeoutMax + "ms");
                
                //Creates a string that will be converted to bytes then written to the report file.
                dataToByte = "\r\nThere is " + openPorts + " open port on host " + nextIp +  
                " probed with a timeout between " + timeoutMin + "ms and " + timeoutMax + "ms\r\n\r\n";
            
                //Converts the string into bytes.
                dataToWrite = dataToByte.getBytes();
            
                //Writes the data to the report file.
                out.write(dataToWrite);
            }
            else
            {
                //Prints the same message as above but with syntaxt and gramar changes to make sense with a
                //number of ports being found not being 1.
                System.out.println("There are " + openPorts + " open ports on host " + nextIp +  
                " probed with a timeout between " + timeoutMin + "ms and " + timeoutMax + "ms");
                
                //Creates a string that will be converted to bytes then written to the report file.
                dataToByte = "\r\nThere are " + openPorts + " open ports on host " + nextIp +  
                " probed with a timeout between " + timeoutMin + "ms and " + timeoutMax + "ms\r\n\r\n";
            
                //Converts the string into bytes.
                dataToWrite = dataToByte.getBytes();
            
                //Writes the data to the report file.
                out.write(dataToWrite);
            }
            
            //Clears the list of ports which have been scaned so that the can be re-scan with a different
            //IP address
            portToScan.clear();
            
            //Clears the number of open ports for a given IP address so it can be reused
            openPorts = 0;
            
            //Clear the future list so it can be reused for the next IP
            futures.clear();
        }
        
        //Shuts down the executor service.
        es.shutdown();
        
        //Closes the file that the report has been written to.
        out.close();
    }
    
    /*------------------------------------------------------------------------------------------------------------------*/
     // portIsOpen checks if a given port is open by attempting to make a socket and connect to a secified IP and port 
     // number with a given timeout value. It uses a future callble so that this test can be done symultaneously with 
     // other test. The function asking for the result of this test will wait until that result is available.
    /*------------------------------------------------------------------------------------------------------------------*/
    public static Future<Boolean> portIsOpen(final ExecutorService es, final String ip, 
    final int port, final int timeout, FileOutputStream out) 
    {
        //Sets the return value to be whatever is returned by a new callable function that will have 
        //it's functionality overriden to the actual port test.
        return es.submit(new Callable<Boolean>() 
        {
            //Overrride of the call function built into Callable object. This function is used whenever
            //a reslut is rquested from the object as it is above.
            @Override public Boolean call() {
                //Shows the compiler that the next piece of code may cause an error and if so tells it to
                //do something else instead.
                try 
                {
                    //Creats a new socket object.
                    Socket socket = new Socket();
                    
                    //Gives the newly created socket an IP address, a port number and a timeout value
                    //and tells it to attempt to make a connection with that time.
                    socket.connect(new InetSocketAddress(ip, port), timeout);
                    
                    //Attempts to closes that connection 
                    socket.close();
                    
                    //Prints a message to the console to let the user know an open port was found. The
                    //message contains the port number and the timeout value that was used for the test. 
                    System.out.println("Open port found " + port + " using " + timeout + "ms as the timeout");
                    
                    //Creates a string that will be converted to bytes then written to the report file.
                    //Re-inistialised here as this is a different scope
                    String dataToByte = "Open port found " + port + " using " + timeout + "ms as the timeout\r\n";
            
                    //Converts the string into bytes. Re-inistialised here as this is a different scope
                    byte dataToWrite[] = dataToByte.getBytes();
                               
                    //Writes the data to the report file.
                    out.write(dataToWrite);
                    
                    //Returns true as the result of the test. Assumes that since no errors have occured
                    //yet, that the port must be open. 
                    return true;
                } 
                
                //The catch bracket tells the compiler what to do if the code in the try bracket causes an
                //error.
                catch (Exception ex) 
                {
                    //Returns the value as false. It is assumed that since an error occured while 
                    //attempting to connect, that the port is not open.
                    return false;
                }
        }
   });
}
}