package eco.data.m3.net.udp;

import java.io.IOException;

import org.junit.Test;

import eco.data.m3.net.NetService;
import eco.data.m3.net.core.MId;
import eco.data.m3.net.message.MessageFactory;
import eco.data.m3.net.message.NullHandler;
import eco.data.m3.net.message.NullMessage;
import eco.data.m3.net.server.ServerConfig;
import eco.data.m3.net.server.udp.UDPServer;

public class UDPServerTest {

	@Test
	public void testSendMessageNodeMessageInt() throws IOException, InterruptedException {
		MessageFactory messageFactory = new MessageFactory();
		 
		ServerConfig s1 = new ServerConfig();		
		s1.setMessageFactory(messageFactory);
		UDPServer server1 = (UDPServer) NetService.getInstance().createServer(s1, new MId("12345678901234567890"));
		server1.listen();
		
		ServerConfig s2 = new ServerConfig();		
		s2.setMessageFactory(messageFactory);
		UDPServer server2 = (UDPServer) NetService.getInstance().createServer(s2, new MId("12345678901234567891"));
		server2.listen();
		
		server1.sendMessage(server2.getMId(), new NullMessage(server1.getMId(), "Hello"), new NullHandler());		
		Thread.sleep(3);
	}

}
