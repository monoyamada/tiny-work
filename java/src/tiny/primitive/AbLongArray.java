package tiny.primitive;

import tiny.function.AbArray;
import tiny.lang.Messages;

public abstract class AbLongArray extends AbArray<Number> implements LongArray {
	public String toString() {
		final StringBuilder buffer = new StringBuilder();
		buffer.append('[');
		for (int i = 0, n = this.getLength(); i < n; ++i) {
			if (i != 0) {
				buffer.append(", ");
			}
			buffer.append(this.doGet(i));
		}
		buffer.append(']');
		return buffer.toString();
	}
	@Override
	public LongIterator iterator() {
		return new LongArray.ArrayIterator(this);
	}
	@Override
	public long get(int index) {
		if (index < 0 || this.getLength() <= index) {
			String msg = Messages.getIndexOutOfRange(0, index, this.getLength());
			throw new IndexOutOfBoundsException(msg);
		}
		return this.doGet(index);
	}
	@Override
	public long getFirst(long defaultValue) {
		if (this.getLength() < 1) {
			return defaultValue;
		}
		return this.doGet(0);
	}
	@Override
	public long getLast(long defaultValue) {
		if (this.getLength() < 1) {
			return defaultValue;
		}
		return this.doGet(this.getLength() - 1);
	}
	@Override
	public int getFirstIndex(long value) {
		for (int i = 0, n = this.getLength(); i < n; ++i) {
			if (this.doGet(i) == value) {
				return i;
			}
		}
		return -1;
	}
	@Override
	public int getLastIndex(long value) {
		for (int i = this.getLength(); 0 < i;) {
			if (this.doGet(--i) == value) {
				return i;
			}
		}
		return -1;
	}
	@Override
	protected Number doGetValue(int index) {
		return Long.valueOf(this.doGet(index));
	}
	protected abstract long doGet(int index);
}
