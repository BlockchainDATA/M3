package eco.data.m3.net.message;

import java.io.DataInputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;

import eco.data.m3.net.server.Server;

/**
 * Message Factory, use for replying and handling messages.
 * Messages should be registed in the factory.
 * 
 * @author xquan
 *
 */
public class MessageFactory {
	
	private HashMap<Byte, Class> messageMap = new HashMap<>();
	
	private HashMap<Byte, Class> handlerMap = new HashMap<>();
	
	public MessageFactory() {
		registMessage((byte)0, NullMessage.class);
		registHandler((byte)0, NullHandler.class);
	}
	
	public void registMessage(byte code, Class c) {
		messageMap.put(code, c);
	}
	
	public void registHandler(byte code, Class c) {
		handlerMap.put(code, c);
	}

	public Message createMessage(byte code, DataInputStream in) throws Exception {
		Class c = messageMap.get(code);
		if(c!=null) {
			Constructor con = c.getConstructor(DataInputStream.class);
			Message msg = (Message) con.newInstance(in);
//			msg.fromStream(in);
			return msg;
		}
		return null;
	}
	
	public MessageHandler createHandler(byte code, Server server) throws Exception{
		Class c = handlerMap.get(code);
		if(c!=null) {
			MessageHandler handler = (MessageHandler) c.newInstance();	
			handler.setServer(server);
			return handler;
		}
		return null;
	}
	
}
