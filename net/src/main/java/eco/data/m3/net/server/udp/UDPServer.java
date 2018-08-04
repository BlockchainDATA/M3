package eco.data.m3.net.server.udp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Random;
import java.util.TimerTask;

import eco.data.m3.net.MIdLookupService;
import eco.data.m3.net.core.MId;
import eco.data.m3.net.message.Message;
import eco.data.m3.net.message.MessageHandler;
import eco.data.m3.net.server.Server;
import eco.data.m3.net.server.ServerConfig;

/**
 * An UDP Server, Supply UDP communication between Nodes.
 * 
 * @author xquan
 *
 */
public class UDPServer extends Server{
	
    /* Maximum size of a Datagram Packet */
    private static final int DATAGRAM_BUFFER_SIZE = 64 * 1024;      // 64KB
	
    private final DatagramSocket socket;

	public UDPServer(ServerConfig config, MId mid) throws SocketException {
		super(config, mid);
		int port = config.getPort();
		if (port < 0) {
			port = (int) (ServerConfig.PORT_MIN + (ServerConfig.PORT_MAX - ServerConfig.PORT_MIN) * new Random().nextFloat());
		}

		this.socket = new DatagramSocket(port);
	}

	@Override
	public void doListen() {

        try
        {
            while (isRunning)
            {
                try
                {
                    /* Wait for a packet */
                    byte[] buffer = new byte[DATAGRAM_BUFFER_SIZE];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    
                    socket.receive(packet);

                    /* Lets inform the statistician that we've received some data */
//                    this.statistician.receivedData(packet.getLength());

                    if (this.getServerConfig().isTesting())
                    {
                        /**
                         * Simulating network latency
                         * We pause for 1 millisecond/100 bytes
                         */
                        int pause = packet.getLength() / 100;
                        try
                        {
                            Thread.sleep(pause);
                        }
                        catch (InterruptedException ex)
                        {

                        }
                    }

                    /* We've received a packet, now handle it */
                    try (ByteArrayInputStream bin = new ByteArrayInputStream(packet.getData(), packet.getOffset(), packet.getLength());
                            DataInputStream din = new DataInputStream(bin);)
                    {

                        /* Read in the conversation Id to know which handler to handle this response */
                        int comm = din.readInt();
                        byte messCode = din.readByte();

                        Message msg = getMessageFactory().createMessage(messCode, din);
                        din.close();
                        
//                        System.out.println("Get Message " + total_msg++ +" : " + comm + " -- " + messCode + " -- " + packet.getSocketAddress() );

                        /* Get a handler for this message */
                        MessageHandler handler;
                        if (getHandlers().containsKey(comm))
                        {
                            /* If there is a reciever in the receivers to handle this */
                            synchronized (this)
                            {
                            	handler = getHandlers().remove(comm);
                                TimerTask task = (TimerTask) getTasks().remove(comm);
                                if (task != null)
                                {
                                    task.cancel();
                                }
                            }
                        }
                        else
                        {
                            /* There is currently no receivers, try to get one */
                            handler = getMessageFactory().createHandler(messCode, this);
                        }

                        /* Invoke the receiver */
                        if (handler != null)
                        {
                        	handler.receive(msg, comm);
                        }
                    }
                }
                catch (Exception e)
                {
                    //this.isRunning = false;
                    System.err.println("Server ran into a problem in listener method. Message: " + e.getMessage());
                }
            }
        }
        finally
        {
            if (!socket.isClosed())
            {
                socket.close();
            }
            this.isRunning = false;
        }
		
	}

	@Override
	public void shutdown() {
        this.socket.close();
        super.shutdown();		
	}

	@Override
	protected void sendMessage(MId to, Message msg, int comm) throws IOException {

        /* Use a try-with resource to auto-close streams after usage */
        try (ByteArrayOutputStream bout = new ByteArrayOutputStream(); DataOutputStream dout = new DataOutputStream(bout);)
        {
            /* Setup the message for transmission */
            dout.writeInt(comm);
            dout.writeByte(msg.getCode());
            msg.toStream(dout);
            dout.close();

            byte[] data = bout.toByteArray();

            if (data.length > DATAGRAM_BUFFER_SIZE)
            {
                throw new IOException("Message is too big");
            }

//            System.out.println("** Send :"+to.getSocketAddress() +" -- " + msg.code());

            /* Everything is good, now create the packet and send it */
            DatagramPacket pkt = new DatagramPacket(data, 0, data.length);
            pkt.setSocketAddress(MIdLookupService.getInstance().lookup(to));
            socket.send(pkt);

            /* Lets inform the statistician that we've sent some data */
//            this.statistician.sentData(data.length);
        }
		
	}

	@Override
	public int getPort() {
		return socket.getLocalPort();
	}

}
