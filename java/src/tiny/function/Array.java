package tiny.function;


public interface Array<Value> extends Iterable<Value> {
	public static class ArrayIterator<Value> extends AbIterator<Value> {
		private final Array<Value> container;
		private int index;

		public ArrayIterator(Array<Value> container) {
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
			return this.index < this.container.getLength();
		}
		@Override
		public Value next() {
			return this.container.getValue(this.index++);
		}
	}
	
	public int getLength();
	public Value getValue(int index);
}
