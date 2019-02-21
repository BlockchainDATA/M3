package eco.data.m3.routing.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import data.eco.net.p2p.message.Message;

public class ContentRetrieveReplyMessage extends Message{
	
	public boolean contentReady;

	public ContentRetrieveReplyMessage(boolean contentReady) {
		this.contentReady = contentReady;
	}
	
	public ContentRetrieveReplyMessage(DataInputStream in) throws IOException {
		fromStream(in);
	}

	@Override
	public byte getCode() {
		return MessageCode.CONTENT_RETRIEVE_REPLY;
	}
	
	@Override
	public void toStream(DataOutputStream out) throws IOException {
		super.toStream(out);
		out.writeBoolean(contentReady);
	}
	
	@Override
	public void fromStream(DataInputStream in) throws IOException {
		super.fromStream(in);
		contentReady = in.readBoolean();
	}

	public boolean isContentReady() {
		return contentReady;
	}

	public void setContentReady(boolean contentReady) {
		this.contentReady = contentReady;
	}

}
