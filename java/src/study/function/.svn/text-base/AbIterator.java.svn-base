package study.function;

import java.util.Iterator;

import study.lang.Messages;

/**
 * A class to avoid writing same code again and again the unsupported method
 * message in the method {@link #remove()}.
 * 
 * @author shirakata
 * 
 * @param <Value>
 */
public abstract class AbIterator<Value> implements Iterator<Value> {
	@Override
	public void remove() {
		String msg = Messages.getUnSupportedMethod(this.getClass(), "remove");
		throw new UnsupportedOperationException(msg);
	}
}
