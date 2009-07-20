package study.primitive;

import study.lang.Messages;

public abstract class AbNumberIterator implements IfNumberIterator {
	@Override
	public void remove() {
		String msg = Messages.getUnSupportedMethod(this.getClass(), "remove");
		throw new UnsupportedOperationException(msg);
	}
}
