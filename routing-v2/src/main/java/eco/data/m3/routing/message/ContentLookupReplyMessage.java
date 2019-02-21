package eco.data.m3.routing.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import data.eco.net.p2p.message.Message;

/**
 * A Message used to send content between nodes
 * 
 * @author xquan
 *
 */
public class ContentLookupReplyMessage extends Message{
	
	private boolean found;
    
    public ContentLookupReplyMessage(boolean found) {
		this.found = found;
	}
	
	public ContentLookupReplyMessage(DataInputStream in) throws IOException {
		super(in);
	}

	@Override
	public byte getCode() {
		return MessageCode.CONTENT_LOOKUP_REPLY;
	}

    @Override
    public void toStream(DataOutputStream out) throws IOException
    {
    	super.toStream(out);
    	out.writeBoolean(found);
    }

    @Override
    public final void fromStream(DataInputStream in) throws IOException
    {
    	super.fromStream(in);
    	this.found = in.readBoolean();
    }

	public boolean isFound() {
		return found;
	}

	public void setFound(boolean found) {
		this.found = found;
	}
	
}
