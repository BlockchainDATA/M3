package eco.data.m3.routing.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import data.eco.net.p2p.message.Message;
import eco.data.m3.content.MContent;
import eco.data.m3.content.MContentMeta;

/**
 * A StoreContentMessage used to send a store message to a node
 * 
 * @author xquan
 *
 */
public class StoreInitMessage extends Message{

	private MContentMeta meta;

    public StoreInitMessage(MContent content)
    {
        this.meta = content.getMeta();
    }
	
	public StoreInitMessage(DataInputStream in) throws IOException {
		super(in);
	}

    public MContentMeta getMeta() {
		return meta;
	}

	@Override
	public byte getCode() {
		return MessageCode.STORE_INIT;
	}

    @Override
    public void toStream(DataOutputStream out) throws IOException
    {
    	super.toStream(out);
    	meta.toStream(out);
    }

    @Override
    public final void fromStream(DataInputStream in) throws IOException
    {
    	super.fromStream(in);
    	meta = new MContentMeta(in);
    }
    
    @Override
	public String toString() {
		return "StoreInitMessage " ;
	}

}
