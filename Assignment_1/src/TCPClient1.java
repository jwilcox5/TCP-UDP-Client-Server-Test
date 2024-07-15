import java.util.*;
import java.io.*;
import java.net.*;

// 8-Byte Message: 0123
// 64-Byte Message: 01234567890123456789012345678901
// 512-Byte Message: 0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345

public class TCPClient1 
{
    public static void main(String[] args) 
    {
        String host = "pi.cs.oswego.edu";
        int echoServicePortNumber = 26930;

        Socket echoSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try 
        {
            echoSocket = new Socket(host, echoServicePortNumber);
            out = new PrintWriter(echoSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
        } 
        
        catch (UnknownHostException e) 
        {
            System.err.println("Don't know about host " + host);
            e.printStackTrace();
            System.exit(1);
        } 

        catch (IOException e) 
        {
            System.err.println("Couldn't get I/O for the connection.");
            e.printStackTrace();
            System.exit(1);
        }

        try 
        {
            System.out.println("Server Connected");

            Scanner scan = new Scanner(System.in);

            // Ask user for message
            String message = scan.next();

            // Set the seed for the random number
            Random random = new Random();
            long seed = 19;
            random.setSeed(seed);

            // Generate the random key
            long ranKey = random.nextLong();

            // Encode the message
            String encodedMessage = xorShiftString(message, ranKey);

            // Start the timer immediately before sending the message and the random key to the server
            double startTime = System.nanoTime();
            out.println(encodedMessage);
            System.out.println("Echo: " + encodedMessage);
            out.println(Long.toString(ranKey));

            // Retrieve the decoded message from the server and immediately stop the timer
            String decodedMessage = in.readLine();
            System.out.println("Final Message: " + decodedMessage);
            double totalTime = (System.nanoTime() - startTime) / 1000000.0;

            // Display the total RTT in milliseconds
            System.out.println("RTT: " + totalTime + " milliseconds");
            
            out.close();
            in.close();
            echoSocket.close();
            scan.close();
        }

        catch (IOException ex) 
        {
            System.err.println("IO failure.");
            ex.printStackTrace();
        }
    }

    public static String xorShiftString(String message, long ranKey)
    {
        char[] messageC = message.toCharArray();
        int keyCounter = 0;

        for(int i = 0; i < messageC.length; i++)
        {
            messageC[i] ^= ranKey << 13;
            messageC[i] ^= ranKey >>> 7;
            messageC[i] ^= ranKey << 17;

            keyCounter += 2;

            if(keyCounter % 64 == 0)
            {
                ranKey = xorShiftLong(ranKey);
            }
        }

        return new String(messageC);
    }

    public static long xorShiftLong(long l)
    {
        l ^= l << 13; 
        l ^= l >>> 7; 
        l ^= l << 17; 
        return l;
    }
}