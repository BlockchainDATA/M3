package eco.data.m3.routing.message;

import eco.data.m3.net.core.MId;
import eco.data.m3.net.message.Message;
import eco.data.m3.routing.core.GetParameter;
import eco.data.m3.routing.serializer.JsonSerializer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Messages used to send to another node requesting content.
 * 
 * @author xquan
 *
 */
public class ContentLookupMessage extends Message{

    private GetParameter param;
    
    public ContentLookupMessage(MId origin, GetParameter param) {
    	super(origin);
		this.param = param;
	}
	
	public ContentLookupMessage(DataInputStream in) throws IOException {
		super(in);
	}
    
	public GetParameter getParam() {
		return param;
	}
	
	@Override
	public void fromStream(DataInputStream in) throws IOException {
		super.fromStream(in);

        /* Read the params from the stream */
        try
        {
            this.param = new JsonSerializer<GetParameter>().read(in);
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
	}
	
	@Override
	public void toStream(DataOutputStream out) throws IOException {
		super.toStream(out);
		new JsonSerializer<GetParameter>().write(this.param, out);
	}

	@Override
	public byte getCode() {
		return MessageCode.CONTENT_LOOKUP;
	}
}
