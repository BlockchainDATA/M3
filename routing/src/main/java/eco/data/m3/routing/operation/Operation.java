package eco.data.m3.routing.operation;

import java.io.IOException;
import eco.data.m3.routing.exceptions.RoutingException;

/**
 * An operation in the Kademlia routing protocol
 *
 * @author Joshua Kissoon
 * @since 20140218
 */
public interface Operation
{

    /**
     * Starts an operation and returns when the operation is finished
     *
     * @throws eco.data.m3.routing.exceptions.RoutingException
     */
    public void execute() throws IOException, RoutingException;
}
