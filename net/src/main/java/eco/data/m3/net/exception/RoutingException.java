package eco.data.m3.net.exception;

import java.io.IOException;

/**
 * 
 * An exception to be thrown whenever there is a routing problem
 * 
 * @author xquan
 *
 */
public class RoutingException extends IOException {

    public RoutingException()
    {
        super();
    }

    public RoutingException(String message)
    {
        super(message);
    }
}
