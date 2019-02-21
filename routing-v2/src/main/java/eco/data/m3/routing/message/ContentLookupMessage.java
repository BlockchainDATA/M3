package eco.data.m3.routing.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import data.eco.net.p2p.message.Message;
import eco.data.m3.content.MContentKey;

/**
 * Messages used to send to another node requesting content.
 * 
 * @author xquan
 *
 */
public class ContentLookupMessage extends Message{

    private MContentKey param;
    
    public ContentLookupMessage(MContentKey param) {
		this.param = param;
	}
	
	public ContentLookupMessage(DataInputStream in) throws IOException {
		super(in);
	}
    
	public MContentKey getParam() {
		return param;
	}
	
	@Override
	public void fromStream(DataInputStream in) throws IOException {
		super.fromStream(in);
		param = new MContentKey(in);
	}
	
	@Override
	public void toStream(DataOutputStream out) throws IOException {
		super.toStream(out);
		param.toStream(out);
	}

	@Override
	public byte getCode() {
		return MessageCode.CONTENT_LOOKUP;
	}
}
