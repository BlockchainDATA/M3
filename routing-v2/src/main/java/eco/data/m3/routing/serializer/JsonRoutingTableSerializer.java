package eco.data.m3.routing.serializer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import eco.data.m3.routing.algorithm.kademlia.KademliaRoutingTable;
import eco.data.m3.routing.core.MConfiguration;
import eco.data.m3.routing.core.Contact;
import eco.data.m3.routing.core.IRoutingTable;

public class JsonRoutingTableSerializer implements ISerializer<IRoutingTable> {

    private final Gson gson = new Gson();

    Type contactCollectionType = new TypeToken<List<Contact>>()
    {
    }.getType();

    private final MConfiguration config;
    
	public JsonRoutingTableSerializer(MConfiguration config) {
		this.config = config;
	}

	@Override
	public void write(IRoutingTable data, DataOutputStream out) throws IOException {
        try (JsonWriter writer = new JsonWriter(new OutputStreamWriter(out)))
        {
            writer.beginArray();

            /* Write the basic JKademliaRoutingTable */
            gson.toJson(data, KademliaRoutingTable.class, writer);

            /* Now Store the Contacts  */
            gson.toJson(data.getAllContacts(), contactCollectionType, writer);

            writer.endArray();
        }
	}

	@Override
	public IRoutingTable read(DataInputStream in) throws IOException, ClassNotFoundException {
        try (DataInputStream din = new DataInputStream(in);
                JsonReader reader = new JsonReader(new InputStreamReader(in)))
        {
            reader.beginArray();

            /* Read the basic JKademliaRoutingTable */
            KademliaRoutingTable tbl = gson.fromJson(reader, KademliaRoutingTable.class);
            tbl.setConfiguration(config);
            
            /* Now get the Contacts and add them back to the JKademliaRoutingTable */
            List<Contact> contacts = gson.fromJson(reader, contactCollectionType);
            tbl.initialize();

            for (Contact c : contacts)
            {
                tbl.insert(c);
            }

            reader.endArray();
            /* Read and return the Content*/
            return tbl;
        }
	}

}
