package study.primitive;

import study.function.Array;

public interface LongArray extends Array<Number> {
	public static class ArrayIterator extends AbNumberIterator implements
			LongIterator {
		private final LongArray array;
		private int index;

		public ArrayIterator(LongArray array) {
			this.array = array;
		}
		public boolean hasNext() {
			return this.index < this.array.getSize();
		}
		public long nextLong() {
			return this.array.getLong(this.index++);
		}
		public Number next() {
			return Long.valueOf(this.nextLong());
		}
	}

	public long getLong(int index);
	public long getFirst(long defaultValue);
	public long getLast(long defaultValue);
	public int getFirstIndex(long value);
	public int getLastIndex(long value);
}