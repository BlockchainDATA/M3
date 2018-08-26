package eco.data.m3.routing.exception;

/**
 * 
 * An exception used to indicate an unknown message type or communication identifier
 * 
 * @author xquan
 *
 */
public class UnknownMessageException extends RuntimeException{

    public UnknownMessageException(String message)
    {
        super(message);
    }
    
}
