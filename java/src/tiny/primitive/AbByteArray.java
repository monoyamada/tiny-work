package tiny.primitive;

import tiny.function.AbArray;
import tiny.lang.ArrayHelper;
import tiny.lang.Messages;

public abstract class AbByteArray extends AbArray<Number> implements ByteArray {
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
	public ByteIterator iterator() {
		return new ByteArray.ArrayIterator(this);
	}
	@Override
	public byte get(int index) {
		if (index < 0 || this.getLength() <= index) {
			String msg = Messages.getIndexOutOfRange(0, index, this.getLength());
			throw new IndexOutOfBoundsException(msg);
		}
		return this.doGet(index);
	}
	@Override
	public byte getFirst(byte defaultValue) {
		if (this.getLength() < 1) {
			return defaultValue;
		}
		return this.doGet(0);
	}
	@Override
	public byte getLast(byte defaultValue) {
		if (this.getLength() < 1) {
			return defaultValue;
		}
		return this.doGet(this.getLength() - 1);
	}
	@Override
	public int getFirstIndex(byte value) {
		for (int i = 0, n = this.getLength(); i < n; ++i) {
			if (this.doGet(i) == value) {
				return i;
			}
		}
		return -1;
	}
	@Override
	public int getLastIndex(byte value) {
		for (int i = this.getLength(); 0 < i;) {
			if (this.doGet(--i) == value) {
				return i;
			}
		}
		return -1;
	}
	@Override
	protected Number doGetValue(int index) {
		return Byte.valueOf(this.doGet(index));
	}
	protected abstract byte doGet(int index);
	@Override
	public int toArray(byte[] output) {
		if (output == null || output.length < 1) {
			return 0;
		}
		int n = this.getLength();
		if (output.length < n) {
			n = output.length;
		}
		for (int i = 0; i < n; ++i) {
			output[i] = this.doGet(i);
		}
		return n;
	}
	@Override
	public byte[] toArray() {
		int n = this.getLength();
		if (n < 1) {
			return ArrayHelper.EMPTY_BYTE_ARRAY;
		}
		byte[] output = new byte[n];
		this.toArray(output);
		return output;
	}
}
