package study.primitive;

import study.function.AbArray;
import study.lang.Messages;

public abstract class AbLongArray extends AbArray<Number> implements LongArray {
	public String toString() {
		final StringBuilder buffer = new StringBuilder();
		buffer.append('[');
		for (int i = 0, n = this.getSize(); i < n; ++i) {
			if (i != 0) {
				buffer.append(", ");
			}
			buffer.append(this.doGetLong(i));
		}
		buffer.append(']');
		return buffer.toString();
	}
	@Override
	public LongIterator iterator() {
		return new LongArray.ArrayIterator(this);
	}
	@Override
	public long getLong(int index) {
		if (index < 0 || this.getSize() <= index) {
			String msg = Messages.getIndexOutOfRange(0, index, this.getSize());
			throw new IndexOutOfBoundsException(msg);
		}
		return this.doGetLong(index);
	}
	@Override
	public long getFirst(long defaultValue) {
		if (this.getSize() < 1) {
			return defaultValue;
		}
		return this.doGetLong(0);
	}
	@Override
	public long getLast(long defaultValue) {
		if (this.getSize() < 1) {
			return defaultValue;
		}
		return this.doGetLong(this.getSize() - 1);
	}
	@Override
	public int getFirstIndex(long value) {
		for (int i = 0, n = this.getSize(); i < n; ++i) {
			if (this.doGetLong(i) == value) {
				return i;
			}
		}
		return -1;
	}
	@Override
	public int getLastIndex(long value) {
		for (int i = this.getSize(); 0 < i;) {
			if (this.doGetLong(--i) == value) {
				return i;
			}
		}
		return -1;
	}
	protected abstract long doGetLong(int index);
}
