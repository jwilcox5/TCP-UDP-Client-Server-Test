import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Random;

public class UDPClient2 
{
    public static void main(String[] args) throws IOException 
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

        DatagramChannel client = DatagramChannel.open();
        client.bind(null);

        InetSocketAddress serverAddress = new InetSocketAddress("pi.cs.oswego.edu", 26930);
        System.out.println("Server Connected");

        // Set the seed for the random number
        Random random = new Random();
        long seed = 19;
        random.setSeed(seed);

        // Generate the random key
        long ranKey = random.nextLong();
        ByteBuffer ranKeyBuffer = ByteBuffer.wrap(String.valueOf(ranKey).getBytes());
        client.send(ranKeyBuffer, serverAddress);

        String encodedMessage = "";
        String serverAck = "";

        int limits = 0;
        byte bytes[] = {};

        ByteBuffer encodedbuffer = ByteBuffer.wrap(message1.getBytes());
        ByteBuffer ackbuffer = ByteBuffer.allocate(8);

        // Start the timer immediately before sending the message and the random key to the server
        double startTime = System.nanoTime();

        for(int i = 0; i < MESSAGE_AMT_1; i++)
        {
            // Encode the message
            encodedMessage = xorShiftString(message1, ranKey);
            System.out.println("Encoded Message: " + encodedMessage);

            // Send the message to the server
            client.send(encodedbuffer, serverAddress);

            client.receive(ackbuffer);

            ackbuffer.flip();
            limits = ackbuffer.limit();
            bytes = new byte[limits];
            ackbuffer.get(bytes, 0, limits);
            serverAck = new String(bytes);
            System.out.println("Server ACK: " + serverAck);
        }

        double totalTime = (System.nanoTime() - startTime) / 1000000000.0;

        // Display the total time in milliseconds
        System.out.println("Total Time: "   + totalTime + " seconds");

        // Display the Throughput in bits per second
        System.out.println("Throughput: " + (8000000 / totalTime) + " bits per second");

        client.close();
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

