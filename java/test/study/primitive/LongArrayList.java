package study.primitive;

import study.lang.ArrayHelper;

public class LongArrayList extends AbLongList {
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
	/**
	 * @param array
	 *          the array to set
	 */
	protected void setArray(long[] array) {
		this.array = array;
	}
	@Override
	public int size() {
		return this.size;
	}
	protected void setSize(int size) {
		this.size = size;
	}
	@Override
	protected long doGetLong(int index) {
		return this.array[index];
	}
	@Override
	protected LongArrayList doAdd(int index, long value) {
		final int size = this.size;
		long[] array = this.array;
		if (array.length <= size) {
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
			this.setArray(array);
		}
		if (index < size) {
			System.arraycopy(array, index, array, index + 1, size - index);
		}
		array[index] = value;
		this.size += 1;
		return this;
	}
	@Override
	protected IfLongList doRemove(int index) {
		final int size = this.size;
		if (index + 1 < size) {
			System.arraycopy(array, index + 1, array, index, size - index - 1);
		}
		this.size -= 1;
		return this;
	}
}
