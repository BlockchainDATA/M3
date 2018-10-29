package eco.data.m3.routing.message;

import eco.data.m3.net.core.MId;
import eco.data.m3.net.message.Message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * A message used to acknowledge a request from a node; can be used in many situations.
 * Mainly used to acknowledge a connect message
 * 
 * @author xquan
 *
 */
public class StoreContentReplyMessage extends Message{
	
	private int code;
	
	public StoreContentReplyMessage(MId origin) {
		super(origin);
	}
	
	public StoreContentReplyMessage(DataInputStream in) throws IOException {
		super(in);
	}

	@Override
	public byte getCode() {
		return MessageCode.STORE_CONTENT_REPLY;
	}

	public void setCode(int code) {
		this.code = code;
	}
	
	@Override
	public void toStream(DataOutputStream out) throws IOException {
        this.origin.toStream(out);
		out.writeInt(code);
	}
	
	@Override
	public void fromStream(DataInputStream in) throws IOException {
        this.origin = new MId(in);
		code = in.readInt();
	}
}
