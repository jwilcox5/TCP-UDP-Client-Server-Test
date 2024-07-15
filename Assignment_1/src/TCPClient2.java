import java.util.*;
import java.io.*;
import java.net.*;

public class TCPClient2 
{
    public static void main(String[] args) 
    {
        final int MESSAGE_AMT_1 = 16384;
        final int MESSAGE_AMT_2 = 4096;
        final int MESSAGE_AMT_3 = 1024;

        // 64-Byte Message
        String message1 = "01234567890123456789012345678901";
        // 256-Byte Message
        String message2 = "01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567";
        // 1024-Byte Message
        String message3 = "01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901";

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

            // Set the seed for the random number
            Random random = new Random();
            long seed = 19;
            random.setSeed(seed);

            // Generate the random key
            long ranKey = random.nextLong();
            out.println(ranKey);

            String encodedMessage = "";
            String serverAck = "";

            // Start the timer immediately before sending the message and the random key to the server
            double startTime = System.nanoTime();

            for(int i = 0; i < MESSAGE_AMT_3; i++)
            {
                // Encode the message
                encodedMessage = xorShiftString(message3, ranKey);
                System.out.println("Client Echo: " + encodedMessage);

                out.println(encodedMessage);

                serverAck = in.readLine();
                System.out.println("Server Ack: " + serverAck);
            }

            double totalTime = (System.nanoTime() - startTime) / 1000000000.0;

            // Display the total RTT in milliseconds
            System.out.println("\nRTT:        " + totalTime + " seconds");

            // Display the Throughput in bits per second
            System.out.println("Throughput: " + (8000000 / totalTime) + " bits per second");
            
            out.close();
            in.close();
            echoSocket.close();
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