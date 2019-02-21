package eco.data.m3.routing;

import data.eco.net.p2p.message.MessageFactory;
import eco.data.m3.routing.message.AcknowledgeMessage;
import eco.data.m3.routing.message.ContentDataMessage;
import eco.data.m3.routing.message.ContentLookupMessage;
import eco.data.m3.routing.message.ContentLookupReplyMessage;
import eco.data.m3.routing.message.ContentRetrieveMessage;
import eco.data.m3.routing.message.ContentRetrieveReplyMessage;
import eco.data.m3.routing.message.MessageCode;
import eco.data.m3.routing.message.NodeLookupMessage;
import eco.data.m3.routing.message.NodeReplyMessage;
import eco.data.m3.routing.message.StoreContentMessage;
import eco.data.m3.routing.message.StoreInitMessage;
import eco.data.m3.routing.message.StoreInitReplyMessage;
import eco.data.m3.routing.message.handler.ContentLookupHandler;
import eco.data.m3.routing.message.handler.ContentRetrieveHandler;
import eco.data.m3.routing.message.handler.NodeLookupHandler;
import eco.data.m3.routing.message.handler.StoreContentHandler;
import eco.data.m3.routing.message.handler.StoreInitHandler;

public class DefaultMessageFactory extends MessageFactory{

	public DefaultMessageFactory() {
		registMessage(MessageCode.ACKNOWLEDGE, AcknowledgeMessage.class);
		registMessage(MessageCode.NODE_LOOKUP, NodeLookupMessage.class);
		registMessage(MessageCode.NODE_REPLY, NodeReplyMessage.class);
		registMessage(MessageCode.CONTENT_LOOKUP, ContentLookupMessage.class);
		registMessage(MessageCode.CONTENT_LOOKUP_REPLY, ContentLookupReplyMessage.class);
		registMessage(MessageCode.CONTENT_RETRIEVE, ContentRetrieveMessage.class);
		registMessage(MessageCode.CONTENT_RETRIEVE_REPLY, ContentRetrieveReplyMessage.class);
		registMessage(MessageCode.CONTENT_DATA, ContentDataMessage.class);
		registMessage(MessageCode.STORE_INIT, StoreInitMessage.class);
		registMessage(MessageCode.STORE_INIT_REPLY, StoreInitReplyMessage.class);
		registMessage(MessageCode.STORE_CONTENT, StoreContentMessage.class);

		registHandler(MessageCode.CONTENT_LOOKUP, ContentLookupHandler.class);
		registHandler(MessageCode.CONTENT_RETRIEVE, ContentRetrieveHandler.class);
		registHandler(MessageCode.NODE_LOOKUP, NodeLookupHandler.class);
		registHandler(MessageCode.STORE_CONTENT, StoreContentHandler.class);
		registHandler(MessageCode.STORE_INIT, StoreInitHandler.class);
	}
	
}
