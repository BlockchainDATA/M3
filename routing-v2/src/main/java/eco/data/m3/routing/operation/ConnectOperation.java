package eco.data.m3.routing.operation;

import java.io.IOException;

import eco.data.m3.net.core.MId;
import eco.data.m3.net.exception.RoutingException;
import eco.data.m3.net.message.Message;
import eco.data.m3.net.message.MessageHandler;
import eco.data.m3.net.server.Server;
import eco.data.m3.routing.MNode;
import eco.data.m3.routing.message.AcknowledgeMessage;
import eco.data.m3.routing.message.ConnectMessage;

public class ConnectOperation extends MessageHandler implements IOperation{

    public static final int MAX_CONNECT_ATTEMPTS = 5;       // Try 5 times to connect to a node

    private final MNode localNode;
    private final MId bootstrapNode;

    private boolean error;
    private int attempts;

    public ConnectOperation(MNode local, MId bootstrap)
    {
    	super(local.getServer());
        this.localNode = local;
        this.bootstrapNode = bootstrap;
    }

    @Override
    public synchronized void execute() throws IOException
    {
        try
        {
            /* Contact the bootstrap node */
            this.error = true;
            this.attempts = 0;
            Message m = new ConnectMessage(this.localNode.getNodeId());

            /* Send a connect message to the bootstrap node */
            server.sendMessage(this.bootstrapNode, m, this);

            /* If we haven't finished as yet, wait for a maximum of config.operationTimeout() time */
              
            int totalTimeWaited = 0;
            int timeInterval = 50;     // We re-check every 300 milliseconds
            while (totalTimeWaited < localNode.getCurrentConfiguration().operationTimeout())
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
                throw new RoutingException("ConnectOperation: node to join did not respond: " + bootstrapNode);
            }

            /* Perform lookup for our own ID to get nodes close to us */
            IOperation lookup = new NodeLookupOperation(this.localNode, this.localNode.getNodeId());
            lookup.execute();

            /**
             * Refresh buckets to get a good routing table
             * After the above lookup operation, K nodes will be in our routing table,
             * Now we try to populate all of our buckets.
             */
            new BucketRefreshOperation(this.localNode).execute();
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
            this.server.sendMessage(this.bootstrapNode, new ConnectMessage(this.localNode.getNodeId()), this);
        }
        else
        {
            /* We just exit, so notify all other threads that are possibly waiting */
            notify();
        }
    }

}
