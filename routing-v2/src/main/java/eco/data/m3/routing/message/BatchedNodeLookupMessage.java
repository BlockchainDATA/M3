package eco.data.m3.routing.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import eco.data.m3.net.core.MId;
import eco.data.m3.net.message.Message;

/**
 * A message sent to other nodes requesting the Closest nodes to a key sent in this message.
 * 
 * @author xquan
 *
 */
public class BatchedNodeLookupMessage extends Message{
	
	private List<MId> lookupIds;
	
	public BatchedNodeLookupMessage(MId origin, List<MId> lookupIds) {
		super(origin);
		this.lookupIds = lookupIds;
	}
	
	public BatchedNodeLookupMessage(DataInputStream in) throws IOException {
		super(in);
	}

	public List<MId> getLookupIds() {
		return lookupIds;
	}

	@Override
	public byte getCode() {
		return MessageCode.BATCHED_NODE_LOOKUP;
	}

    @Override
    public final void fromStream(DataInputStream in) throws IOException
    {
    	super.fromStream(in);
    	int len = in.readInt();
    	for (int i = 0; i < len; i++) {
            this.lookupIds.add(new MId(in));
		}
    }

    @Override
    public void toStream(DataOutputStream out) throws IOException
    {
    	super.toStream(out);
    	out.writeInt(lookupIds.size());
    	for (MId lookupId : lookupIds) {
            lookupId.toStream(out);
		}
    }
	
}
