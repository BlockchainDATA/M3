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
public class BatchedNodeReplyMessage extends Message{
	
	private List<List<MId>> nodeArrays;

    public BatchedNodeReplyMessage(MId origin, List<List<MId>> nodes)
    {
    	super(origin);
        this.nodeArrays = nodes;
    }
	
	public BatchedNodeReplyMessage(DataInputStream in) throws IOException {
		super(in);
	}

	@Override
	public byte getCode() {
		return MessageCode.BATCHED_NODE_REPLY;
	}

    @Override
    public final void fromStream(DataInputStream in) throws IOException
    {
    	super.fromStream(in);

        /* Get the number of incoming nodes */
        int total_len = in.readInt();
        this.nodeArrays = new ArrayList<>();

        /* Read in all nodes */
        for (int i = 0; i < total_len; i++)
        {
        	int len = in.readInt();
        	List<MId> nodes = new ArrayList<>();
        	for (int j = 0; j < len ; j++) {
                nodes.add(new MId(in));
			}
        	this.nodeArrays.add(nodes);
        }
    }

    @Override
    public void toStream(DataOutputStream out) throws IOException
    {
        /* Add the origin node to the stream */
        super.toStream(out);

        /* Add all other nodes to the stream */
        int total_len = this.nodeArrays.size();

        /* Writing the nodes to the stream */
        out.writeInt(total_len);
        for (int i = 0; i < total_len; i++) {
        	List<MId> nodes = nodeArrays.get(i);
        	out.writeInt(nodes.size());
            for (MId n : nodes)
            {
                n.toStream(out);
            }
		}
    }

	public List<List<MId>> getNodeArrays() {
		return nodeArrays;
	}
    
}
