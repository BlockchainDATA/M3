package eco.data.m3.routing.exception;

/**
 * 
 * An exception used to indicate that a content does not exist on the DHT
 * 
 * @author xquan
 *
 */
public class ContentNotFoundException extends Exception{

    public ContentNotFoundException(String message)
    {
        super(message);
    }
    
}
