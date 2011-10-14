package tiny.primitive;

import tiny.lang.Messages;

public abstract class AbLongList extends AbLongArray implements LongList {
	@Override
	public AbLongList add(int index, long value) {
		if (index < 0 || this.getLength() < index) {
			String msg = Messages.getIndexOutOfRange(0, index, this.getLength() + 1);
			throw new IndexOutOfBoundsException(msg);
		}
		return this.doAdd(index, value);
	}
	@Override
	public AbLongList addFirst(long value) {
		return this.doAdd(0, value);
	}
	@Override
	public AbLongList addLast(long value) {
		return this.doAdd(this.getLength(), value);
	}
	@Override
	public AbLongList remove(int index) {
		if (index < 0 || this.getLength() <= index) {
			String msg = Messages.getIndexOutOfRange(0, index, this.getLength() + 1);
			throw new IndexOutOfBoundsException(msg);
		}
		return this.doRemove(index);
	}
	@Override
	public AbLongList removeFirst() {
		if (this.getLength() < 1) {
			String msg = Messages.getIndexOutOfRange(0, 0, 0);
			throw new IndexOutOfBoundsException(msg);
		}
		return this.doRemove(0);
	}
	@Override
	public AbLongList removeLast() {
		if (this.getLength() < 1) {
			String msg = Messages.getIndexOutOfRange(0, 0, 0);
			throw new IndexOutOfBoundsException(msg);
		}
		return this.doRemove(this.getLength() - 1);
	}
	protected abstract AbLongList doAdd(int index, long value);
	protected abstract AbLongList doRemove(int index);
}