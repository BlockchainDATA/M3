package eco.data.m3.routing.message;

/**
 * Message Code Define
 * 
 * @author xquan
 *
 */
public class MessageCode {

	public static final byte ACKNOWLEDGE = 0x11;
	
	public static final byte NODE_LOOKUP = 0x12;
	public static final byte NODE_REPLY = 0x13;

	public static final byte CONTENT_LOOKUP = 0x14;
	public static final byte CONTENT_LOOKUP_REPLY = 0x15;
	
	public static final byte CONTENT_RETRIEVE = 0x16;
	public static final byte CONTENT_RETRIEVE_REPLY = 0x17;
	public static final byte CONTENT_DATA = 0x18;
	
	public static final byte STORE_INIT = 0x19;
	public static final byte STORE_INIT_REPLY = 0x1A;
	public static final byte STORE_CONTENT = 0x1B;
	
}
