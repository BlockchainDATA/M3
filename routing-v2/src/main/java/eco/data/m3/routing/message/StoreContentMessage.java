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
public class StoreContentMessage extends Message{

    private short fd;
    
    private boolean endOfFile;

    private int start;
    
    private byte [] content;
	
	public static int getHeaderSize() {
		return Message.getHeaderSize() + 11;
	}
    
    public StoreContentMessage(short fd , int start, byte [] content)
    {
    	this.fd = fd;
    	this.start = start;
        this.content = content;
    }
	
	public StoreContentMessage(DataInputStream in) throws IOException {
		super(in);
	}

	@Override
	public byte getCode() {
		return MessageCode.STORE_CONTENT;
	}

    @Override
    public void toStream(DataOutputStream out) throws IOException
    {
    	super.toStream(out);
    	out.writeShort(fd);
    	out.writeInt(start);
    	out.writeBoolean(endOfFile);
    	out.writeInt(content.length);
    	out.write(content);
    }

    @Override
    public final void fromStream(DataInputStream in) throws IOException
    {
    	super.fromStream(in);
        this.fd = in.readShort();
        this.start = in.readInt();
        this.endOfFile = in.readBoolean();
        int len = in.readInt();
        this.content = new byte[len];
        in.read(content);
    }
    
    @Override
	public String toString() {
		return "StoreContentMessage : Len " + content.length;
	}

	public short getFd() {
		return fd;
	}

	public void setFd(short fd) {
		this.fd = fd;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public boolean isEndOfFile() {
		return endOfFile;
	}

	public void setEndOfFile(boolean endOfFile) {
		this.endOfFile = endOfFile;
	}
    
}
