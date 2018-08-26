package eco.data.m3.routing.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import eco.data.m3.net.core.MId;
import eco.data.m3.net.message.Message;
import eco.data.m3.routing.core.StorageEntry;
import eco.data.m3.routing.serializer.JsonSerializer;

/**
 * A StoreContentMessage used to send a store message to a node
 * 
 * @author xquan
 *
 */
public class StoreContentMessage extends Message{

	private StorageEntry content;

    public StoreContentMessage(MId origin, StorageEntry content)
    {
    	super(origin);
        this.content = content;
    }
	
	public StoreContentMessage(DataInputStream in) throws IOException {
		super(in);
	}

	public StorageEntry getContent() {
		return content;
	}

	@Override
	public byte getCode() {
		return MessageCode.STORE_CONTENT;
	}

    @Override
    public void toStream(DataOutputStream out) throws IOException
    {
        this.origin.toStream(out);

        /* Serialize the KadContent, then send it to the stream */
        new JsonSerializer<StorageEntry>().write(content, out);
    }

    @Override
    public final void fromStream(DataInputStream in) throws IOException
    {
        this.origin = new MId(in);
        try
        {
            this.content = new JsonSerializer<StorageEntry>().read(in);
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }

}
