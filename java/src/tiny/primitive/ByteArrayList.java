package tiny.primitive;

import tiny.lang.ArrayHelper;
import tiny.lang.Messages;

public class ByteArrayList extends AbByteList implements ByteStack {
	private byte[] array;
	private int size;

	public ByteArrayList() {
		this.array = ArrayHelper.EMPTY_BYTE_ARRAY;
	}
	public ByteArrayList(int capacity) {
		this.array = 0 < capacity ? new byte[capacity]
				: ArrayHelper.EMPTY_BYTE_ARRAY;
	}
	/**
	 * @param array
	 *          not be cloned.
	 */
	public ByteArrayList(byte[] array) {
		this.array = array != null ? array : ArrayHelper.EMPTY_BYTE_ARRAY;
	}
	/**
	 * @return the array
	 */
	public byte[] getArray() {
		return this.array;
	}
	/**
	 * @param array
	 *          the array to set
	 */
	protected void setArray(byte[] array) {
		this.array = array;
	}
	@Override
	public int getLength() {
		return this.size;
	}
	protected void setSize(int size) {
		this.size = size;
	}
	@Override
	protected byte doGet(int index) {
		return this.array[index];
	}
	@Override
	protected ByteArrayList doAdd(int index, byte value) {
		final int size = this.size;
		this.ensureCapacity(size + 1);
		byte[] array = this.array;
		if (index < size) {
			System.arraycopy(array, index, array, index + 1, size - index);
		}
		array[index] = value;
		this.size += 1;
		return this;
	}
	public void ensureCapacity(int size) {
		byte[] array = this.array;
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
	protected ByteArrayList doRemove(int index) {
		final int size = this.size;
		if (index + 1 < size) {
			final byte[] array = this.array;
			System.arraycopy(array, index + 1, array, index, size - index - 1);
		}
		this.size -= 1;
		return this;
	}
	@Override
	public ByteArrayList removeAll() {
		this.setSize(0);
		return this;
	}
	@Override
	protected Number doGetValue(int index) {
		return Byte.valueOf(this.doGet(index));
	}
	public boolean isFull() {
		return Integer.MAX_VALUE <= this.getLength();
	}
	public ByteArrayList push(byte value) {
		return (ByteArrayList) this.addLast(value);
	}
	public ByteArrayList pushValue(Number value) {
		return this.push(value.byteValue());
	}
	@Override
	public int toArray(byte[] output) {
		int n = this.size < output.length ? this.size : output.length;
		if (n < 1) {
			return 0;
		}
		System.arraycopy(this.array, 0, output, 0, n);
		return n;
	}
	@Override
	public byte[] toArray() {
		if (this.size < 1) {
			return ArrayHelper.EMPTY_BYTE_ARRAY;
		}
		final byte[] newArray = new byte[this.size];
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
	public byte top(byte def) {
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
	public boolean isTop(byte value) {
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
	public ByteArrayList set(int index, byte value) {
		if (index < 0 || this.getLength() <= index) {
			String msg = Messages.getIndexOutOfRange(0, index, this.getLength());
			throw new IndexOutOfBoundsException(msg);
		}
		return this.doSet(index, value);
	}
	protected ByteArrayList doSet(int index, byte value) {
		this.array[index] = value;
		return this;
	}
	public ByteArrayList setTop(byte value) {
		return this.set(this.getLength() - 1, value);
	}
}
