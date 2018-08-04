package eco.data.m3.net.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import eco.data.m3.net.core.IStreamable;
import eco.data.m3.net.core.MId;

/**
 * Base Message template.
 * 
 * @author xquan
 *
 */
public class Message implements IStreamable{

	/**
	 * message's type code, for parsing and handling message 
	 */
	public static byte CODE = 0;
	
	/**
	 * message's origin node's MId
	 */
	protected MId origin;
	
	public byte getCode() {
		return CODE;
	}

	public MId getOrigin() {
		return origin;
	}

	public void setOrigin(MId origin) {
		this.origin = origin;
	}
	
	public Message() {
	}
	
    public Message(MId origin)
    {
        this.origin = origin;
    }
	
	public Message(DataInputStream in) throws IOException {
		fromStream(in);
	}
	
	
	@Override
	public void toStream(DataOutputStream out) throws IOException {
        origin.toStream(out);
	}

	@Override
	public void fromStream(DataInputStream in) throws IOException {
        this.origin = new MId(in);
	}
	
}
