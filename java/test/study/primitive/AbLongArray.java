package study.primitive;

import study.lang.Messages;

public abstract class AbLongArray implements IfLongArray {
	public String toString() {
		final StringBuilder buffer = new StringBuilder();
		buffer.append('[');
		for (int i = 0, n = this.size(); i < n; ++i) {
			if (i != 0) {
				buffer.append(", ");
			}
			buffer.append(this.doGetLong(i));
		}
		buffer.append(']');
		return buffer.toString();
	}
	@Override
	public IfNumberIterator iterator() {
		return new PrimitiveHelper.LongArrayIterator(this);
	}
	@Override
	public long getLong(int index) {
		if (index < 0 || this.size() <= index) {
			String msg = Messages.getIndexOutOfRange(0, index, this.size());
			throw new IndexOutOfBoundsException(msg);
		}
		return this.doGetLong(index);
	}
	@Override
	public long getFirst(long defaultValue) {
		if (this.size() < 1) {
			return defaultValue;
		}
		return this.doGetLong(0);
	}
	@Override
	public long getLast(long defaultValue) {
		if (this.size() < 1) {
			return defaultValue;
		}
		return this.doGetLong(this.size() - 1);
	}
	@Override
	public int getFirstIndex(long value) {
		for (int i = 0, n = this.size(); i < n; ++i) {
			if (this.doGetLong(i) == value) {
				return i;
			}
		}
		return -1;
	}
	@Override
	public int getLastIndex(long value) {
		for (int i = this.size(); 0 < i;) {
			if (this.doGetLong(--i) == value) {
				return i;
			}
		}
		return -1;
	}
	protected abstract long doGetLong(int index);
}
