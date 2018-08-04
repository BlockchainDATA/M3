package eco.data.m3.net.exception;

/**
 * 
 * An exception to be thrown whenever the Kad Server is down
 * 
 * @author xquan
 *
 */
public class ServerDownException extends RoutingException {

    public ServerDownException()
    {
        super();
    }

    public ServerDownException(String message)
    {
        super(message);
    }
}
