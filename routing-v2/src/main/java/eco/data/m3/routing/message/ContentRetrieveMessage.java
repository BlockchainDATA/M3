package eco.data.m3.routing.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import data.eco.net.p2p.message.Message;
import eco.data.m3.content.MContentKey;

public class ContentRetrieveMessage extends Message{
	
	private MContentKey contentKey;
	
	public ContentRetrieveMessage(MContentKey contentKey) {
		this.setContentKey(contentKey);
	}
	
	public ContentRetrieveMessage(DataInputStream in) throws IOException {
		fromStream(in);
	}
	
	@Override
	public void toStream(DataOutputStream out) throws IOException {
		super.toStream(out);
		contentKey.toStream(out);
	}

	@Override
	public void fromStream(DataInputStream in) throws IOException {
		super.fromStream(in);
		contentKey = new MContentKey(in);
	}
	
	@Override
	public byte getCode() {
		return MessageCode.CONTENT_RETRIEVE;
	}

	public MContentKey getContentKey() {
		return contentKey;
	}

	public void setContentKey(MContentKey contentKey) {
		this.contentKey = contentKey;
	}
	
}
