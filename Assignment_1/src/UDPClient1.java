import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Random;
import java.util.Scanner;

// 8-Byte Message: 0123
// 64-Byte Message: 01234567890123456789012345678901
// 512-Byte Message: 0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345

public class UDPClient1 
{
    public static void main(String[] args) throws IOException 
    {
        DatagramChannel client = DatagramChannel.open();
        client.bind(null);

        InetSocketAddress serverAddress = new InetSocketAddress("pi.cs.oswego.edu", 26930);
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
        System.out.println("Encoded Message: " + encodedMessage);

        ByteBuffer encodedbuffer = ByteBuffer.wrap(encodedMessage.getBytes());
        ByteBuffer decodedbuffer = ByteBuffer.wrap(message.getBytes());
        ByteBuffer ranKeyBuffer = ByteBuffer.wrap(String.valueOf(ranKey).getBytes());

        // Start the timer immediately before sending the message and the random key to the server
        double startTime = System.nanoTime();

        // Send the message to the server
        client.send(encodedbuffer, serverAddress);
        client.send(ranKeyBuffer, serverAddress);

        client.receive(decodedbuffer);

        decodedbuffer.flip();
        int limits = decodedbuffer.limit();
        byte bytes[] = new byte[limits];
        decodedbuffer.get(bytes, 0, limits);
        String decodedMessage = new String(bytes);
        System.out.println("Decoded Message: " + decodedMessage);
        double totalTime = (System.nanoTime() - startTime) / 1000000.0;

        // Display the total RTT in milliseconds
        System.out.println("RTT: " + totalTime + " milliseconds");

        scan.close();
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
