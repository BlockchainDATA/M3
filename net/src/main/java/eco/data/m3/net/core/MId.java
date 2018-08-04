package eco.data.m3.net.core;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

/**
 * MId is a node's unique id on the network.
 * 
 * @author xquan
 *
 */
public class MId implements IStreamable{

    public final transient static int ID_LENGTH = 160;
    
    protected byte[] keyBytes;

    /**
     * Construct the NodeId from some string
     *
     * @param data The user generated key string
     */
    public MId(String data)
    {
        keyBytes = data.getBytes();
        if (keyBytes.length != ID_LENGTH / 8)
        {
            throw new IllegalArgumentException("Specified Data need to be " + (ID_LENGTH / 8) + " characters long.");
        }
    }
    
    /**
     * Generate a random key
     */
    public MId()
    {
        keyBytes = new byte[ID_LENGTH / 8];
        new Random().nextBytes(keyBytes);
    }

    /**
     * Generate the NodeId from a given byte[]
     *
     * @param bytes
     */
    public MId(byte[] bytes)
    {
        if (bytes.length != ID_LENGTH / 8)
        {
            throw new IllegalArgumentException("Specified Data need to be " + (ID_LENGTH / 8) + " characters long. Data Given: '" + new String(bytes) + "'");
        }
        this.keyBytes = bytes;
    }
    
    /**
     * Load the NodeId from a DataInput stream
     *
     * @param in The stream from which to load the NodeId
     *
     * @throws IOException
     */
    public MId(DataInputStream in) throws IOException
    {
        this.fromStream(in);
    }
    
	@Override
	public void toStream(DataOutputStream out) throws IOException {
        /* Add the NodeId to the stream */
        out.write(this.getBytes());
	}

	@Override
	public void fromStream(DataInputStream in) throws IOException {
        byte[] input = new byte[ID_LENGTH / 8];
        in.readFully(input);
        this.keyBytes = input;		
	}

    public byte[] getBytes()
    {
        return this.keyBytes;
    }
    
    /**
     * @return The BigInteger representation of the key
     */
    public BigInteger getInt()
    {
        return new BigInteger(1, this.getBytes());
    }

    public String hexRepresentation()
    {
        /* Returns the hex format of this NodeId */
        BigInteger bi = new BigInteger(1, this.keyBytes);
        return String.format("%0" + (this.keyBytes.length << 1) + "X", bi);
    }

    @Override
    public String toString()
    {
        return this.hexRepresentation();
    }

}
