package tiny.primitive;

import tiny.function.Array;

public interface LongArray extends Array<Number> {
	public static class ArrayIterator extends AbNumberIterator implements
			LongIterator {
		private final LongArray array;
		private int index;

		public ArrayIterator(LongArray array) {
			this.array = array;
		}
		public boolean hasNext() {
			return this.index < this.array.getLength();
		}
		public long nextLong() {
			return this.array.get(this.index++);
		}
		public Number next() {
			return Long.valueOf(this.nextLong());
		}
	}

	public long get(int index);
	public long getFirst(long defaultValue);
	public long getLast(long defaultValue);
	public int getFirstIndex(long value);
	public int getLastIndex(long value);
	public int toArray(long[] output);
	public long[] toArray();
}