package eco.data.m3.net.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import eco.data.m3.net.core.MId;

/**
 * A Simple Message implementation
 * 
 * @author xquan
 *
 */
public class NullMessage extends Message{

	public static byte CODE = 0x00;
	
	private String content;
	
	public NullMessage(DataInputStream in) throws IOException {
		fromStream(in);
	}
	
	public NullMessage(MId origin, String message) {
		super(origin);
		this.content = message;
	}

	public String getContent() {
		return content;
	}
	
	@Override
	public void toStream(DataOutputStream out) throws IOException {
        try
        {
        	super.toStream(out);
            out.writeInt(this.content.length());
            out.writeBytes(this.content);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
	}

	@Override
	public void fromStream(DataInputStream in) throws IOException {
        try
        {
        	super.fromStream(in);
            byte[] buff = new byte[in.readInt()];
            in.readFully(buff);

            this.content = new String(buff);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
	}
}
