package eco.data.m3.routing.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;

/**
 * A simple message used for testing the system; Default message constructed if the message type sent is not available
 *
 * @author Joshua Kissoon
 * @since 20140217
 */
public class SimpleMessage implements Message
{

    /* Message constants */
    public static final byte CODE = 0x07;

    private String content;

    public SimpleMessage(String message)
    {
        this.content = message;
    }

    public SimpleMessage(DataInputStream in, DatagramPacket packet)
    {
        this.fromStream(in, packet);
    }

    @Override
    public byte code()
    {
        return CODE;
    }

    @Override
    public void toStream(DataOutputStream out)
    {
        try
        {
            out.writeInt(this.content.length());
            out.writeBytes(this.content);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public final void fromStream(DataInputStream in, DatagramPacket packet)
    {
        try
        {
            byte[] buff = new byte[in.readInt()];
            in.readFully(buff);

            this.content = new String(buff);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public String toString()
    {
        return this.content;
    }
}
