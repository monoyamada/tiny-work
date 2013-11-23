package tiny.primitive;

import tiny.function.AbArray;
import tiny.lang.ArrayHelper;
import tiny.lang.Messages;

public abstract class AbLongArray extends AbArray<Number> implements LongArray {
	public String toString() {
		final StringBuilder buffer = new StringBuilder();
		buffer.append('[');
		for (int i = 0, n = this.size(); i < n; ++i) {
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
		if (index < 0 || this.size() <= index) {
			String msg = Messages.getIndexOutOfRange(0, index, this.size());
			throw new IndexOutOfBoundsException(msg);
		}
		return this.doGet(index);
	}
	@Override
	public long getFirst(long defaultValue) {
		if (this.size() < 1) {
			return defaultValue;
		}
		return this.doGet(0);
	}
	@Override
	public long getLast(long defaultValue) {
		if (this.size() < 1) {
			return defaultValue;
		}
		return this.doGet(this.size() - 1);
	}
	@Override
	public int getFirstIndex(long value) {
		for (int i = 0, n = this.size(); i < n; ++i) {
			if (this.doGet(i) == value) {
				return i;
			}
		}
		return -1;
	}
	@Override
	public int getLastIndex(long value) {
		for (int i = this.size(); 0 < i;) {
			if (this.doGet(--i) == value) {
				return i;
			}
		}
		return -1;
	}
	public boolean contains(long value) {
		return 0 <= this.getFirstIndex(value);
	}
	@Override
	protected Number doGetValue(int index) {
		return Long.valueOf(this.doGet(index));
	}
	protected abstract long doGet(int index);
	@Override
	public int toArray(long[] output) {
		if (output == null || output.length < 1) {
			return 0;
		}
		int n = this.size();
		if (output.length < n) {
			n = output.length;
		}
		for (int i = 0; i < n; ++i) {
			output[i] = this.doGet(i);
		}
		return n;
	}
	@Override
	public long[] toArray() {
		int n = this.size();
		if (n < 1) {
			return ArrayHelper.EMPTY_LONG_ARRAY;
		}
		long[] output = new long[n];
		this.toArray(output);
		return output;
	}
}
