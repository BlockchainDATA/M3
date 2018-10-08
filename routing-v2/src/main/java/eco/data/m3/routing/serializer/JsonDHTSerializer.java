package eco.data.m3.routing.serializer;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import eco.data.m3.routing.core.DHT;
import eco.data.m3.routing.core.StorageEntryMetadata;

import java.io.*;
import java.lang.reflect.Type;
import java.util.List;

public class JsonDHTSerializer implements ISerializer<DHT> {

	private final Gson gson = new Gson();
	private final Type storageEntriesCollectionType = new TypeToken<List<StorageEntryMetadata>>() {
	}.getType();

	@Override
	public void write(DHT data, DataOutputStream out) throws IOException {

		try (JsonWriter writer = new JsonWriter(new OutputStreamWriter(out))) {
			writer.beginArray();

			/* Write the basic DHT */
			gson.toJson(data, DHT.class, writer);

			/* Now Store the Entries */
			gson.toJson(data.getStorageEntries(), this.storageEntriesCollectionType, writer);

			writer.endArray();
		}
	}

	@Override
	public DHT read(DataInputStream in) throws IOException, ClassNotFoundException {
		try (DataInputStream din = new DataInputStream(in);
				JsonReader reader = new JsonReader(new InputStreamReader(in))) {
			reader.beginArray();

			/* Read the basic DHT */
			DHT dht = gson.fromJson(reader, DHT.class);
			dht.initialize();

			/* Now get the entries and add them back to the DHT */
			List<StorageEntryMetadata> entries = gson.fromJson(reader, this.storageEntriesCollectionType);
			dht.putStorageEntries(entries);

			reader.endArray();
			return dht;
		}
	}

}
