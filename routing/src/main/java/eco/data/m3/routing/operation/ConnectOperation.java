/**
 * @author Joshua Kissoon
 * @created 20140218
 * @desc Operation that handles connecting to an existing Kademlia network using a bootstrap node
 */
package eco.data.m3.routing.operation;

import eco.data.m3.routing.message.Receiver;
import java.io.IOException;
import eco.data.m3.routing.JKademliaNode;
import eco.data.m3.routing.KadConfiguration;
import eco.data.m3.routing.KadServer;
import eco.data.m3.routing.KademliaNode;
import eco.data.m3.routing.exceptions.RoutingException;
import eco.data.m3.routing.message.AcknowledgeMessage;
import eco.data.m3.routing.message.ConnectMessage;
import eco.data.m3.routing.message.Message;
import eco.data.m3.routing.node.Node;

public class ConnectOperation implements Operation, Receiver
{

    public static final int MAX_CONNECT_ATTEMPTS = 5;       // Try 5 times to connect to a node

    private final KadServer server;
    private final KademliaNode localNode;
    private final Node bootstrapNode;
    private final KadConfiguration config;

    private boolean error;
    private int attempts;

    /**
     * @param server    The message server used to send/receive messages
     * @param local     The local node
     * @param bootstrap Node to use to bootstrap the local node onto the network
     * @param config
     */
    public ConnectOperation(KadServer server, KademliaNode local, Node bootstrap, KadConfiguration config)
    {
        this.server = server;
        this.localNode = local;
        this.bootstrapNode = bootstrap;
        this.config = config;
    }

    @Override
    public synchronized void execute() throws IOException
    {
        try
        {
            /* Contact the bootstrap node */
            this.error = true;
            this.attempts = 0;
            Message m = new ConnectMessage(this.localNode.getNode());

            /* Send a connect message to the bootstrap node */
            server.sendMessage(this.bootstrapNode, m, this);

            /* If we haven't finished as yet, wait for a maximum of config.operationTimeout() time */
            int totalTimeWaited = 0;
            int timeInterval = 50;     // We re-check every 300 milliseconds
            while (totalTimeWaited < this.config.operationTimeout())
            {
                if (error)
                {
                    wait(timeInterval);
                    totalTimeWaited += timeInterval;
                }
                else
                {
                    break;
                }
            }
            if (error)
            {
                /* If we still haven't received any responses by then, do a routing timeout */
                throw new RoutingException("ConnectOperation: Bootstrap node did not respond: " + bootstrapNode);
            }

            /* Perform lookup for our own ID to get nodes close to us */
            Operation lookup = new NodeLookupOperation(this.server, this.localNode, this.localNode.getNode().getNodeId(), this.config);
            lookup.execute();

            /**
             * Refresh buckets to get a good routing table
             * After the above lookup operation, K nodes will be in our routing table,
             * Now we try to populate all of our buckets.
             */
            new BucketRefreshOperation(this.server, this.localNode, this.config).execute();
        }
        catch (InterruptedException e)
        {
            System.err.println("Connect operation was interrupted. ");
        }
    }

    /**
     * Receives an AcknowledgeMessage from the bootstrap node.
     *
     * @param comm
     */
    @Override
    public synchronized void receive(Message incoming, int comm)
    {
        /* The incoming message will be an acknowledgement message */
        AcknowledgeMessage msg = (AcknowledgeMessage) incoming;

        /* The bootstrap node has responded, insert it into our space */
        this.localNode.getRoutingTable().insert(this.bootstrapNode);

        /* We got a response, so the error is false */
        error = false;

        /* Wake up any waiting thread */
        notify();
    }

    /**
     * Resends a ConnectMessage to the boot strap node a maximum of MAX_ATTEMPTS
     * times.
     *
     * @param comm
     *
     * @throws java.io.IOException
     */
    @Override
    public synchronized void timeout(int comm) throws IOException
    {
        if (++this.attempts < MAX_CONNECT_ATTEMPTS)
        {
            this.server.sendMessage(this.bootstrapNode, new ConnectMessage(this.localNode.getNode()), this);
        }
        else
        {
            /* We just exit, so notify all other threads that are possibly waiting */
            notify();
        }
    }
}
