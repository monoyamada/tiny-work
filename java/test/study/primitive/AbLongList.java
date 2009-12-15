package study.primitive;

import study.lang.Messages;

public abstract class AbLongList extends AbLongArray implements IfLongList {
	@Override
	public IfLongList add(int index, long value) {
		if (index < 0 || this.size() < index) {
			String msg = Messages.getIndexOutOfRange(0, index, this.size() + 1);
			throw new IndexOutOfBoundsException(msg);
		}
		return this.doAdd(index, value);
	}
	@Override
	public IfLongList addFront(long value) {
		return this.doAdd(0, value);
	}
	@Override
	public IfLongList addBack(long value) {
		return this.doAdd(this.size(), value);
	}
	@Override
	public IfLongList remove(int index) {
		if (index < 0 || this.size() <= index) {
			String msg = Messages.getIndexOutOfRange(0, index, this.size() + 1);
			throw new IndexOutOfBoundsException(msg);
		}
		return this.doRemove(index);
	}
	@Override
	public IfLongList removeFront() {
		if (this.size() < 1) {
			String msg = Messages.getIndexOutOfRange(0, 0, 0);
			throw new IndexOutOfBoundsException(msg);
		}
		return this.doRemove(0);
	}
	@Override
	public IfLongList removeBack() {
		if (this.size() < 1) {
			String msg = Messages.getIndexOutOfRange(0, 0, 0);
			throw new IndexOutOfBoundsException(msg);
		}
		return this.doRemove(this.size() - 1);
	}
	protected abstract IfLongList doAdd(int index, long value);
	protected abstract IfLongList doRemove(int index);
}
