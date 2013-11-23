package tiny.primitive;

import tiny.lang.ArrayHelper;
import tiny.lang.Messages;

public class LongArrayList extends AbLongList implements LongStack {
	private long[] array;
	private int size;

	public LongArrayList() {
		this.array = ArrayHelper.EMPTY_LONG_ARRAY;
	}
	public LongArrayList(int capacity) {
		this.array = 0 < capacity ? new long[capacity]
				: ArrayHelper.EMPTY_LONG_ARRAY;
	}
	/**
	 * @param array
	 *          not be cloned.
	 */
	public LongArrayList(long[] array) {
		this.array = array != null ? array : ArrayHelper.EMPTY_LONG_ARRAY;
	}
	/**
	 * @return the array
	 */
	public long[] getArray() {
		return this.array;
	}
	@Override
	public int size() {
		return this.size;
	}
	protected LongArrayList setSize(int size) {
		this.size = size;
		return this;
	}
	@Override
	protected long doGet(int index) {
		return this.array[index];
	}
	@Override
	protected LongArrayList doAdd(int index, long value) {
		final int size = this.size;
		this.ensureCapacity(size + 1);
		long[] array = this.array;
		if (index < size) {
			System.arraycopy(array, index, array, index + 1, size - index);
		}
		array[index] = value;
		this.size += 1;
		return this;
	}
	public LongArrayList ensureCapacity(int size) {
		long[] array = this.array;
		if (size < array.length) {
			return this;
		}
		int capacity = (size * 3) / 2 + 1;
		if (capacity < size) {
			capacity = size;
		}
		if (capacity < size + 1) {
			capacity = size + 1;
		}
		if (capacity < 4) {
			capacity = 4;
		}
		array = ArrayHelper.ensureSize(array, capacity);
		this.array = array;
		return this;
	}
	@Override
	protected LongArrayList doRemove(int index) {
		final int size = this.size;
		if (index + 1 < size) {
			final long[] array = this.array;
			System.arraycopy(array, index + 1, array, index, size - index - 1);
		}
		this.size -= 1;
		return this;
	}
	@Override
	public LongArrayList removeAll() {
		this.setSize(0);
		return this;
	}
	@Override
	protected Number doGetValue(int index) {
		return Long.valueOf(this.doGet(index));
	}
	@Override
	public boolean isFull() {
		return Integer.MAX_VALUE <= this.size();
	}
	@Override
	public LongArrayList push(long value) {
		return (LongArrayList) this.addLast(value);
	}
	@Override
	public LongArrayList pushValue(Number value) {
		return this.push(value.longValue());
	}
	@Override
	public LongArrayList addLastAll(long[] values) {
		if (values == null) {
			return this;
		}
		this.ensureCapacity(this.size + values.length);
		System.arraycopy(values, 0, this.size, this.size, values.length);
		this.size += values.length;
		return this;
	}
	@Override
	public int toArray(long[] output) {
		int n = this.size < output.length ? this.size : output.length;
		if (n < 1) {
			return 0;
		}
		System.arraycopy(this.array, 0, output, 0, n);
		return n;
	}
	@Override
	public long[] toArray() {
		if (this.size < 1) {
			return ArrayHelper.EMPTY_LONG_ARRAY;
		}
		final long[] newArray = new long[this.size];
		System.arraycopy(this.array, 0, newArray, 0, this.size);
		return newArray;
	}
	@Override
	public boolean isEmpty() {
		return this.size < 1;
	}
	@Override
	public boolean pop() {
		int n = this.size;
		if (n < 1) {
			return false;
		}
		this.remove(n - 1);
		return true;
	}
	@Override
	public long top(long def) {
		int n = this.size;
		if (n < 1) {
			return def;
		}
		return this.doGet(n - 1);
	}
	@Override
	public Number topValue(Number def) {
		int n = this.size;
		if (n < 1) {
			return def;
		}
		return this.doGetValue(n - 1);
	}
	@Override
	public boolean isTop(long value) {
		int n = this.size;
		if (n < 1) {
			return false;
		}
		return this.doGet(n - 1) == value;
	}
	@Override
	public boolean isTopValue(Number value) {
		return value != null ? this.isTop(value.byteValue()) : false;
	}
	public LongArrayList set(int index, long value) {
		if (index < 0 || this.size() <= index) {
			String msg = Messages.getIndexOutOfRange(0, index, this.size());
			throw new IndexOutOfBoundsException(msg);
		}
		return this.doSet(index, value);
	}
	protected LongArrayList doSet(int index, long value) {
		this.array[index] = value;
		return this;
	}
	public LongArrayList setTop(long value) {
		return this.set(this.size() - 1, value);
	}
}
