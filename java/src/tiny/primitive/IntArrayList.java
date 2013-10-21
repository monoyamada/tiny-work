package tiny.primitive;

import tiny.lang.ArrayHelper;
import tiny.lang.Messages;

public class IntArrayList extends AbIntList implements IntStack {
	private int[] array;
	private int size;

	public IntArrayList() {
		this.array = ArrayHelper.EMPTY_INT_ARRAY;
	}
	public IntArrayList(int capacity) {
		this.array = 0 < capacity ? new int[capacity] : ArrayHelper.EMPTY_INT_ARRAY;
	}
	/**
	 * @param array
	 *          not be cloned.
	 */
	public IntArrayList(int[] array) {
		this.array = array != null ? array : ArrayHelper.EMPTY_INT_ARRAY;
	}
	/**
	 * @return the array
	 */
	public int[] getArray() {
		return this.array;
	}
	/**
	 * @param array
	 *          the array to set
	 */
	protected void setArray(int[] array) {
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
	protected int doGet(int index) {
		return this.array[index];
	}
	@Override
	protected IntArrayList doAdd(int index, int value) {
		final int size = this.size;
		this.ensureCapacity(size + 1);
		int[] array = this.array;
		if (index < size) {
			System.arraycopy(array, index, array, index + 1, size - index);
		}
		array[index] = value;
		this.size += 1;
		return this;
	}
	public void ensureCapacity(int size) {
		int[] array = this.array;
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
	}
	@Override
	protected IntArrayList doRemove(int index) {
		final int size = this.size;
		if (index + 1 < size) {
			final int[] array = this.array;
			System.arraycopy(array, index + 1, array, index, size - index - 1);
		}
		this.size -= 1;
		return this;
	}
	@Override
	public IntArrayList removeAll() {
		this.setSize(0);
		return this;
	}
	@Override
	protected Number doGetValue(int index) {
		return Integer.valueOf(this.doGet(index));
	}
	public boolean isFull() {
		return Integer.MAX_VALUE <= this.size();
	}
	public IntArrayList push(int value) {
		return (IntArrayList) this.addLast(value);
	}
	public IntArrayList pushValue(Number value) {
		return this.push(value.intValue());
	}
	@Override
	public int toArray(int[] output) {
		int n = this.size < output.length ? this.size : output.length;
		if (n < 1) {
			return 0;
		}
		System.arraycopy(this.array, 0, output, 0, n);
		return n;
	}
	@Override
	public int[] toArray() {
		if (this.size < 1) {
			return ArrayHelper.EMPTY_INT_ARRAY;
		}
		final int[] newArray = new int[this.size];
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
	public int top(int def) {
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
	public boolean isTop(int value) {
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
	public IntArrayList set(int index, int value) {
		if (index < 0 || this.size() <= index) {
			String msg = Messages.getIndexOutOfRange(0, index, this.size());
			throw new IndexOutOfBoundsException(msg);
		}
		return this.doSet(index, value);
	}
	protected IntArrayList doSet(int index, int value) {
		this.array[index] = value;
		return this;
	}
	public IntArrayList setTop(int value) {
		return this.set(this.size() - 1, value);
	}
}
