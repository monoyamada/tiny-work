package tiny.primitive;

public interface LongList extends LongArray {
	public static final LongList EMPTY_LIST = new EmptyLongList();

	public LongList add(int index, long value);
	public LongList addFirst(long value);
	public LongList addLast(long value);
	public LongList remove(int index);
	public LongList removeFirst();
	public LongList removeLast();
	public LongList removeAll();

	static class EmptyLongList extends AbLongList {
		@Override
		public LongList removeAll() {
			return this;
		}
		@Override
		public int getLength() {
			return 0;
		}
		@Override
		protected AbLongList doAdd(int index, long value) {
			throw new UnsupportedOperationException("add");
		}
		@Override
		protected AbLongList doRemove(int index) {
			throw new IndexOutOfBoundsException("empty but " + index);
		}
		@Override
		protected long doGet(int index) {
			throw new IndexOutOfBoundsException("empty but " + index);
		}
	}
}
