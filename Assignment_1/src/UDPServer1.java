import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class UDPServer1
{
    public static void main(String[] args) throws IOException 
    {
        for(;;)
        {
            DatagramChannel server = DatagramChannel.open();
            InetSocketAddress iAdd = new InetSocketAddress("pi.cs.oswego.edu", 26930);
            server.bind(iAdd);
            System.out.println("Client Connected");

            ByteBuffer encodedBuffer = ByteBuffer.allocate(2048);
            ByteBuffer ranKeyBuffer = ByteBuffer.allocate(1024);
            // Receive buffer from client
            SocketAddress remoteAdd = server.receive(encodedBuffer);
            remoteAdd = server.receive(ranKeyBuffer);

            // Change mode of buffer for the encoded message
            encodedBuffer.flip();
            int eLimits = encodedBuffer.limit();
            byte eBytes[] = new byte[eLimits];
            encodedBuffer.get(eBytes, 0, eLimits);
            String encodedMessage = new String(eBytes);
            System.out.println("Encoded Message: " + encodedMessage);

            // Change mode of buffer for the random key
            ranKeyBuffer.flip();
            int rLimits = ranKeyBuffer.limit();
            byte rBytes[] = new byte[rLimits];
            ranKeyBuffer.get(rBytes, 0, rLimits);
            String sRanKey = new String(rBytes);
            long ranKey = Long.parseLong(sRanKey);

            String decodedMessage = xorShiftString(encodedMessage, ranKey);

            System.out.println("Decoded Message: " + decodedMessage);
            ByteBuffer decodedBuffer = ByteBuffer.wrap(decodedMessage.getBytes());
            server.send(decodedBuffer, remoteAdd);
            server.close();
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
