import java.net.*;
import java.io.*;

public class TCPServer2 
{
    static final int PORT = 26930;
    public static void main(String[] args) 
    {
        final int MESSAGE_AMT_1 = 16384;
        final int MESSAGE_AMT_2 = 4096;
        final int MESSAGE_AMT_3 = 1024;

        try 
        {
            ServerSocket serverSocket = new ServerSocket(PORT);
            
            for(;;) 
            {
                Socket client = serverSocket.accept();

                System.out.println("Client Connected");
                
                PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

                long ranKey = Long.parseLong(in.readLine());

                String encodedMessage = "";
                String decodedMessage = "";
                String serverAck = "ACK-----";

                for(int i = 0; i < MESSAGE_AMT_3; i++)
                {
                    // Retrieve the encoded message and the random key from the client
                    encodedMessage = in.readLine();
                    System.out.println("Server Echo: " + encodedMessage);

                    // Decode the message and send it back to the client
                    decodedMessage = xorShiftString(encodedMessage, ranKey);
                    System.out.println("Server Echo: " + decodedMessage);
                    
                    out.println(serverAck);
                }

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