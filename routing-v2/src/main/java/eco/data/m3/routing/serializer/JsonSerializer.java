package eco.data.m3.routing.serializer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class JsonSerializer<T> implements ISerializer<T> {

    private final Gson gson = new Gson();

	@Override
	public void write(T data, DataOutputStream out) throws IOException {
        try (JsonWriter writer = new JsonWriter(new OutputStreamWriter(out)))
        {
            writer.beginArray();

            /* Store the content type */
            gson.toJson(data.getClass().getName(), String.class, writer);

            /* Now Store the content */
            gson.toJson(data, data.getClass(), writer);

            writer.endArray();
        }
		
	}

	@Override
	public T read(DataInputStream in) throws IOException, ClassNotFoundException {
        try (DataInputStream din = new DataInputStream(in);
                JsonReader reader = new JsonReader(new InputStreamReader(in))){
            reader.beginArray();

            /* Read the class name */
            String className = gson.fromJson(reader, String.class);

            /* Read and return the Content*/
            T ret = gson.fromJson(reader, Class.forName(className));
            
            reader.endArray();
            
            return ret;
        }
	}


}
