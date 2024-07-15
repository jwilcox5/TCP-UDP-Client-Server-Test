import java.net.*;
import java.io.*;

public class TCPServer1 
{
    static final int PORT = 26930;
    public static void main(String[] args) 
    {
        try 
        {
            ServerSocket serverSocket = new ServerSocket(PORT);
            
            for(;;) 
            {
                Socket client = serverSocket.accept();

                System.out.println("Client Connected");
                
                PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

                // Retrieve the encoded message and the random key from the client
                String encodedMessage = in.readLine();
                System.out.println("Echo: " + encodedMessage);
                long ranKey = Long.parseLong(in.readLine());

                // Decode the message and send it back to the client
                String decodedMessage = xorShiftString(encodedMessage, ranKey);
                System.out.println("Echo: " + decodedMessage);
                out.println(decodedMessage);

                out.close();
                in.close();
                client.close();
            }
        }

        catch (IOException ex) 
        {
            ex.printStackTrace();
            System.exit(-1);
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