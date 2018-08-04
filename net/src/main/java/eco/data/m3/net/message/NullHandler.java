package eco.data.m3.net.message;

import java.io.IOException;

/**
 * A Simple Message Handler implementation
 * 
 * @author xquan
 *
 */
public class NullHandler extends MessageHandler{

	@Override
	public void receive(Message incoming, int conversationId) throws IOException {
		System.out.println(((NullMessage)incoming).getContent());		
	}

	@Override
	public void timeout(int conversationId) throws IOException {
		
	}

}
