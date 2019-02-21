package eco.data.m3.routing.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import data.eco.net.p2p.message.Message;

/**
 * A StoreContentMessage used to send a store message to a node
 * 
 * @author xquan
 *
 */
public class StoreInitReplyMessage extends Message{	

    private boolean needStore;
    
    private short fd;

    public boolean isNeedStore() {
		return needStore;
	}
    
	public StoreInitReplyMessage(boolean needStore, short fd)
    {
        this.needStore = needStore;
        this.fd = fd;
    }
	
	public StoreInitReplyMessage(DataInputStream in) throws IOException {
		super(in);
	}

	@Override
	public byte getCode() {
		return MessageCode.STORE_INIT_REPLY;
	}

    @Override
    public void toStream(DataOutputStream out) throws IOException
    {
    	super.toStream(out);
    	out.writeBoolean(needStore);
    	out.writeShort(fd);
    }

    @Override
    public final void fromStream(DataInputStream in) throws IOException
    {
    	super.fromStream(in);
    	needStore = in.readBoolean();
    	fd = in.readShort();
    }
    
    @Override
	public String toString() {
		return "StoreInitAck" ;
	}

	public short getFd() {
		return fd;
	}

	public void setFd(short fd) {
		this.fd = fd;
	}

}
