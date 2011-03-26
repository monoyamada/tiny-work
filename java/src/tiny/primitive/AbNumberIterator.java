package tiny.primitive;

import java.util.Iterator;

import tiny.lang.Messages;

public abstract class AbNumberIterator implements Iterator<Number> {
	@Override
	public void remove() {
		String msg = Messages.getUnSupportedMethod(this.getClass(), "remove");
		throw new UnsupportedOperationException(msg);
	}
}
