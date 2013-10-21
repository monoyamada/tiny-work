package tiny.primitive;

public interface IntList extends IntArray {
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
	}
}
