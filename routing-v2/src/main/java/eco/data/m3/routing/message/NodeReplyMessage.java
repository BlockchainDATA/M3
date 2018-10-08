package eco.data.m3.routing.message;

import eco.data.m3.net.core.MId;
import eco.data.m3.net.message.Message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A message used to connect nodes.
 * When a NodeLookup Request comes in, we respond with a NodeReplyMessage.
 *
 * @author xquan
 *
 */
public class NodeReplyMessage extends Message{
	
	private List<MId> nodes;

    public NodeReplyMessage(MId origin, List<MId> nodes)
    {
    	super(origin);
        this.nodes = nodes;
    }
	
	public NodeReplyMessage(DataInputStream in) throws IOException {
		super(in);
	}

	public List<MId> getNodes() {
		return nodes;
	}

	@Override
	public byte getCode() {
		return MessageCode.NODE_REPLY;
	}

    @Override
    public final void fromStream(DataInputStream in) throws IOException
    {
    	super.fromStream(in);

        /* Get the number of incoming nodes */
        int len = in.readInt();
        this.nodes = new ArrayList<>(len);

        /* Read in all nodes */
        for (int i = 0; i < len; i++)
        {
            this.nodes.add(new MId(in));
        }
    }

    @Override
    public void toStream(DataOutputStream out) throws IOException
    {
        /* Add the origin node to the stream */
        super.toStream(out);

        /* Add all other nodes to the stream */
        int len = this.nodes.size();
        if (len > 255)
        {
            throw new IndexOutOfBoundsException("Too many nodes in list to send in NodeReplyMessage. Size: " + len);
        }

        /* Writing the nodes to the stream */
        out.writeInt(len);
        for (MId n : this.nodes)
        {
            n.toStream(out);
        }
    }
    
}
