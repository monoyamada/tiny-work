package tiny.primitive;

public interface IntList extends IntStack, IntArray {
	public static final IntList EMPTY_LIST = new EmptyIntList();

	public IntList add(int index, int value);
	public IntList addFirst(int value);
	public IntList addLast(int value);
	public IntList remove(int index);
	public IntList removeFirst();
	public IntList removeLast();
	public IntList removeAll();

	static class EmptyIntList extends AbIntList {
		@Override
		public AbIntList push(int value) {
			return this.addLast(value);
		}
		@Override
		public int pop(int none) {
			if (0 < this.size()) {
				none = this.getLast(none);
				this.removeLast();
			}
			return none;
		}
		@Override
		public IntList removeAll() {
			return this;
		}
		@Override
		public int size() {
			return 0;
		}
		@Override
		protected AbIntList doAdd(int index, int value) {
			throw new UnsupportedOperationException("add");
		}
		@Override
		protected AbIntList doRemove(int index) {
			throw new IndexOutOfBoundsException("empty but " + index);
		}
		@Override
		protected int doGet(int index) {
			throw new IndexOutOfBoundsException("empty but " + index);
		}
		@Override
		public boolean isFull() {
			return true;
		}
		@Override
		public boolean isEmpty() {
			return true;
		}
		@Override
		public int top(int none) {
			return none;
		}
		@Override
		public boolean isTop(int value) {
			return false;
		}
	}
}
