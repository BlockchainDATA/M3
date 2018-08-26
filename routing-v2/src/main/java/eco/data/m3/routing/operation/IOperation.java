package eco.data.m3.routing.operation;

import java.io.IOException;

/**
 * An operation is an atomic process.
 * 
 * @author xquan
 *
 */
public interface IOperation {

	/**
	 * 
	 * Starts an operation and returns when the operation is finished
	 * 
	 * @throws IOException
	 */
	public void execute() throws IOException;
	
}
