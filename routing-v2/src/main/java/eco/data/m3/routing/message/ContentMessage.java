package eco.data.m3.routing.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;

import eco.data.m3.net.core.MId;
import eco.data.m3.net.message.Message;
import eco.data.m3.routing.core.StorageEntry;
import eco.data.m3.routing.serializer.JsonSerializer;

/**
 * A Message used to send content between nodes
 * 
 * @author xquan
 *
 */
public class ContentMessage extends Message{
	
	private StorageEntry content;
    
    public ContentMessage(MId origin, StorageEntry content) {
		super(origin);
		this.content = content;
	}
	
	public ContentMessage(DataInputStream in) throws IOException {
		super(in);
	}

	public StorageEntry getContent() {
		return content;
	}

	@Override
	public byte getCode() {
		return MessageCode.CONTENT;
	}

    @Override
    public void toStream(DataOutputStream out) throws IOException
    {
    	super.toStream(out);

        /* Serialize the KadContent, then send it to the stream */
        new JsonSerializer<StorageEntry>().write(content, out);
    }

    @Override
    public final void fromStream(DataInputStream in) throws IOException
    {
    	super.fromStream(in);

        try
        {
            this.content = new JsonSerializer<StorageEntry>().read(in);
        }
        catch (ClassNotFoundException e)
        {
            System.err.println("ClassNotFoundException when reading StorageEntry; Message: " + e.getMessage());
        }
    }
	
}
