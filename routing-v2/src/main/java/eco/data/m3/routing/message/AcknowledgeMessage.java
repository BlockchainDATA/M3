package eco.data.m3.routing.message;

import java.io.DataInputStream;
import java.io.IOException;

import eco.data.m3.net.core.MId;
import eco.data.m3.net.message.Message;

/**
 * A message used to acknowledge a request from a node; can be used in many situations.
 * Mainly used to acknowledge a connect message
 * 
 * @author xquan
 *
 */
public class AcknowledgeMessage extends Message{

	public AcknowledgeMessage(MId origin) {
		super(origin);
	}
	
	public AcknowledgeMessage(DataInputStream in) throws IOException {
		super(in);
	}

	@Override
	public byte getCode() {
		return MessageCode.ACKNOWLEDGE;
	}
}
