package eco.data.m3.routing.message;

import eco.data.m3.net.core.MId;
import eco.data.m3.net.message.Message;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * A message sent to another node requesting to connect to them.
 * 
 * @author xquan
 *
 */
public class ConnectMessage extends Message{

	public ConnectMessage(MId origin) {
		super(origin);
	}
	
	public ConnectMessage(DataInputStream in) throws IOException {
		super(in);
	}

	@Override
	public byte getCode() {
		return MessageCode.CONNECT;
	}
	
}
