package tiny.primitive;

import tiny.lang.Messages;

public abstract class AbIntList extends AbIntArray implements IntList {
	@Override
	public AbIntList add(int index, int value) {
		if (index < 0 || this.size() < index) {
			String msg = Messages.getIndexOutOfRange(0, index, this.size() + 1);
			throw new IndexOutOfBoundsException(msg);
		}
		return this.doAdd(index, value);
	}
	@Override
	public AbIntList addFirst(int value) {
		return this.doAdd(0, value);
	}
	@Override
	public AbIntList addLast(int value) {
		return this.doAdd(this.size(), value);
	}
	@Override
	public AbIntList remove(int index) {
		if (index < 0 || this.size() <= index) {
			String msg = Messages.getIndexOutOfRange(0, index, this.size() + 1);
			throw new IndexOutOfBoundsException(msg);
		}
		return this.doRemove(index);
	}
	@Override
	public AbIntList removeFirst() {
		if (this.size() < 1) {
			String msg = Messages.getIndexOutOfRange(0, 0, 0);
			throw new IndexOutOfBoundsException(msg);
		}
		return this.doRemove(0);
	}
	@Override
	public AbIntList removeLast() {
		if (this.size() < 1) {
			String msg = Messages.getIndexOutOfRange(0, 0, 0);
			throw new IndexOutOfBoundsException(msg);
		}
		return this.doRemove(this.size() - 1);
	}
	public AbIntList addLastAll(int[] values) {
		for (int i = 0, n = values != null ? values.length : 0; i < n; ++i) {
			this.addLast(values[i]);
		}
		return this;
	}
	protected abstract AbIntList doAdd(int index, int value);
	protected abstract AbIntList doRemove(int index);
}
