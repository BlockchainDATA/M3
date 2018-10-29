package eco.data.m3.routing.core;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import eco.data.m3.net.core.IStreamable;

public class StorageEntry implements IStreamable{

    private byte [] content;
    private StorageEntryMetadata metadata;

    public StorageEntry(final MContent content)
    {
        this(content, new StorageEntryMetadata(content));
    }

    public StorageEntry(DataInputStream in) throws IOException
    {
    	this.fromStream(in);
    }

    public StorageEntry(final MContent content, final StorageEntryMetadata metadata)
    {
        this.setContent(content.toSerializedForm());
        this.metadata = metadata;
    }

    public final void setContent(final byte[] data)
    {
        this.content = data;
    }

    public final byte[] getContent()
    {
        return this.content;
    }

    public final StorageEntryMetadata getContentMetadata()
    {
        return this.metadata;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder("[StorageEntry: ");

        sb.append("[Content: ");
        sb.append(this.getContent());
        sb.append("]");

        sb.append(this.getContentMetadata());

        sb.append("]");

        return sb.toString();
    }

	@Override
	public void toStream(DataOutputStream out) throws IOException {
		this.metadata.toStream(out);
		out.writeInt(content.length);
		out.write(content);
	}

	@Override
	public void fromStream(DataInputStream in) throws IOException {
		this.metadata = new StorageEntryMetadata(in);
		int len = in.readInt();
		content = new byte [len];
		in.read(content);
	}
}
