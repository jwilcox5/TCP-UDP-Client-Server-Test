import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class UDPServer2
{
    public static void main(String[] args) throws IOException 
    {
        final int MESSAGE_AMT_1 = 16384;
        final int MESSAGE_AMT_2 = 4096;
        final int MESSAGE_AMT_3 = 1024;

        DatagramChannel server = DatagramChannel.open();
        InetSocketAddress iAdd = new InetSocketAddress("localhost", 26930);
        server.bind(iAdd);
        System.out.println("Client Connected");

        ByteBuffer encodedBuffer = ByteBuffer.allocate(2048);
        ByteBuffer ranKeyBuffer = ByteBuffer.allocate(1024);

        // Receive buffer from client
        SocketAddress remoteAdd = server.receive(ranKeyBuffer);

        // Change mode of buffer for the random key
        ranKeyBuffer.flip();
        int rLimits = ranKeyBuffer.limit();
        byte rBytes[] = new byte[rLimits];
        ranKeyBuffer.get(rBytes, 0, rLimits);
        String sRanKey = new String(rBytes);
        long ranKey = Long.parseLong(sRanKey);

        String encodedMessage = "";
        String decodedMessage = "";

        String serverAck = "ACK-----";
        ByteBuffer ackBuffer = ByteBuffer.wrap(serverAck.getBytes());

        int eLimits = 0;
        byte eBytes[] = {};

        for(int i = 0; i < MESSAGE_AMT_3; i++)
        {
            remoteAdd = server.receive(encodedBuffer);

            // Change mode of buffer for the encoded message
            encodedBuffer.flip();
            eLimits = encodedBuffer.limit();
            eBytes = new byte[eLimits];
            encodedBuffer.get(eBytes, 0, eLimits);
            encodedMessage = new String(eBytes);
            System.out.println("Encoded Message: " + encodedMessage);

            decodedMessage = xorShiftString(encodedMessage, ranKey);
            System.out.println("Decoded Message: " + decodedMessage);

            server.send(ackBuffer, remoteAdd);
        }

        server.close();
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
