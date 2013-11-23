package tiny.primitive;

import tiny.lang.Messages;

public abstract class AbLongList extends AbLongArray implements LongList {
	@Override
	public AbLongList add(int index, long value) {
		if (index < 0 || this.size() < index) {
			String msg = Messages.getIndexOutOfRange(0, index, this.size() + 1);
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
		return this.doAdd(this.size(), value);
	}
	@Override
	public AbLongList remove(int index) {
		if (index < 0 || this.size() <= index) {
			String msg = Messages.getIndexOutOfRange(0, index, this.size() + 1);
			throw new IndexOutOfBoundsException(msg);
		}
		return this.doRemove(index);
	}
	@Override
	public AbLongList removeFirst() {
		if (this.size() < 1) {
			String msg = Messages.getIndexOutOfRange(0, 0, 0);
			throw new IndexOutOfBoundsException(msg);
		}
		return this.doRemove(0);
	}
	@Override
	public AbLongList removeLast() {
		if (this.size() < 1) {
			String msg = Messages.getIndexOutOfRange(0, 0, 0);
			throw new IndexOutOfBoundsException(msg);
		}
		return this.doRemove(this.size() - 1);
	}
	public AbLongList addLastAll(long[] values) {
		for (int i = 0, n = values != null ? values.length : 0; i < n; ++i) {
			this.addLast(values[i]);
		}
		return this;
	}
	protected abstract AbLongList doAdd(int index, long value);
	protected abstract AbLongList doRemove(int index);
}
