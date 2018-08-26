package eco.data.m3.routing.serializer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * A Serializer is used to transform data to and from a specified form.
 * Here we define the structure of any Serializer 
 *
 * @author xquan
 *
 */
public interface ISerializer<T> {

    /**
     * Write a Content to a DataOutput stream
     *
     * @param data The data to write
     * @param out  The output Stream to write to
     *
     * @throws java.io.IOException
     */
    public void write(T data, DataOutputStream out) throws IOException;

    /**
     * Read data of type T from a DataInput Stream
     *
     * @param in The InputStream to read the data from
     *
     * @return T Data of type T
     *
     * @throws java.io.IOException
     * @throws java.lang.ClassNotFoundException
     */
    public T read(DataInputStream in) throws IOException, ClassNotFoundException;

}
