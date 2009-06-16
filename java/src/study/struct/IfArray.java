package study.struct;

import study.function.AbIterator;

public interface IfArray<Value> extends Iterable<Value> {
	public int getSize();
	public Value getValue(int index);

	public static class ArrayIterator<Value> extends AbIterator<Value> {
		private final IfArray<Value> container;
		private int index;

		public ArrayIterator(IfArray<Value> container) {
			this.container = container;
		}
		/**
		 * @return the index
		 */
		protected int getIndex() {
			return this.index;
		}
		/**
		 * @param index
		 *          the index to set
		 */
		protected void setIndex(int index) {
			this.index = index;
		}
		@Override
		public boolean hasNext() {
			return this.index < this.container.getSize();
		}
		@Override
		public Value next() {
			return this.container.getValue(this.index++);
		}
	}
}
