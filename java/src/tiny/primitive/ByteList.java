package tiny.primitive;

public interface ByteList extends ByteArray {
	public static final ByteList EMPTY_LIST = new EmptyByteList();

	public ByteList add(int index, byte value);
	public ByteList addFirst(byte value);
	public ByteList addLast(byte value);
	public ByteList remove(int index);
	public ByteList removeFirst();
	public ByteList removeLast();
	public ByteList removeAll();

	static class EmptyByteList extends AbByteList {
		@Override
		public ByteList removeAll() {
			return this;
		}
		@Override
		public int getLength() {
			return 0;
		}
		@Override
		protected AbByteList doAdd(int index, byte value) {
			throw new UnsupportedOperationException("add");
		}
		@Override
		protected AbByteList doRemove(int index) {
			throw new IndexOutOfBoundsException("empty but " + index);
		}
		@Override
		protected byte doGet(int index) {
			throw new IndexOutOfBoundsException("empty but " + index);
		}
	}
}
