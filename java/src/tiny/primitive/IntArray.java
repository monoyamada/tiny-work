package tiny.primitive;

import tiny.function.Array;

public interface IntArray extends Array<Number> {
	public static class ArrayIterator extends AbNumberIterator implements
			IntIterator {
		private final IntArray array;
		private int index;

		public ArrayIterator(IntArray array) {
			this.array = array;
		}
		public boolean hasNext() {
			return this.index < this.array.size();
		}
		public int nextInt() {
			return this.array.get(this.index++);
		}
		public Number next() {
			return Integer.valueOf(this.nextInt());
		}
	}

	public int get(int index);
	public int getFirst(int defaultValue);
	public int getLast(int defaultValue);
	public int getFirstIndex(int value);
	public int getLastIndex(int value);
	public int toArray(int[] output);
	public int[] toArray();
}
