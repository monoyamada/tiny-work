package tiny.primitive;

import tiny.function.Array;

public interface ByteArray extends Array<Number> {
	public static class ArrayIterator extends AbNumberIterator implements
			ByteIterator {
		private final ByteArray array;
		private int index;

		public ArrayIterator(ByteArray array) {
			this.array = array;
		}
		public boolean hasNext() {
			return this.index < this.array.size();
		}
		public byte nextByte() {
			return this.array.get(this.index++);
		}
		public Number next() {
			return Byte.valueOf(this.nextByte());
		}
	}

	public byte get(int index);
	public byte getFirst(byte defaultValue);
	public byte getLast(byte defaultValue);
	public int getFirstIndex(byte value);
	public int getLastIndex(byte value);
	public int toArray(byte[] output);
	public byte[] toArray();
}
