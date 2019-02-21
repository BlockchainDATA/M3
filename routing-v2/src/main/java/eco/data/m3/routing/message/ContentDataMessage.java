package eco.data.m3.routing.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import data.eco.net.p2p.message.Message;

public class ContentDataMessage extends Message{

    private byte [] data;
    
    public ContentDataMessage(byte [] data) {
		this.data = data;
	}
	
	public ContentDataMessage(DataInputStream in) throws IOException {
		super(in);
	}
	@Override
	public void fromStream(DataInputStream in) throws IOException {
		super.fromStream(in);
		int len = in.readInt();
		data = new byte[len];
		in.read(data);
	}
	
	@Override
	public void toStream(DataOutputStream out) throws IOException {
		super.toStream(out);
		out.writeInt(data.length);
		out.write(data);
	}
	
	@Override
	public byte getCode() {
		return MessageCode.CONTENT_DATA;
	}

}
