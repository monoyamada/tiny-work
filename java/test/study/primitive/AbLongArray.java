package study.primitive;

import study.lang.Messages;

public abstract class AbLongArray implements IfLongArray {
	@Override
	public IfNumberIterator iterator() {
		return new PrimitiveHelper.LongArrayIterator(this);
	}
	@Override
	public long get(int index) {
		if (index < 0 || this.size() <= index) {
			String msg = Messages.getIndexOutOfRange(0, index, this.size());
			throw new IndexOutOfBoundsException(msg);
		}
		return this.doGet(index);
	}
	@Override
	public long front(long defaultValue) {
		if (this.size() < 1) {
			return defaultValue;
		}
		return this.doGet(0);
	}
	@Override
	public long back(long defaultValue) {
		if (this.size() < 1) {
			return defaultValue;
		}
		return this.doGet(this.size() - 1);
	}
	protected abstract long doGet(int index);
}
