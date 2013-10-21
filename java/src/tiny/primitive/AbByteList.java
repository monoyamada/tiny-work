package tiny.primitive;

import tiny.lang.Messages;

public abstract class AbByteList extends AbByteArray implements ByteList {
	@Override
	public AbByteList add(int index, byte value) {
		if (index < 0 || this.size() < index) {
			String msg = Messages.getIndexOutOfRange(0, index, this.size() + 1);
			throw new IndexOutOfBoundsException(msg);
		}
		return this.doAdd(index, value);
	}
	@Override
	public AbByteList addFirst(byte value) {
		return this.doAdd(0, value);
	}
	@Override
	public AbByteList addLast(byte value) {
		return this.doAdd(this.size(), value);
	}
	@Override
	public AbByteList remove(int index) {
		if (index < 0 || this.size() <= index) {
			String msg = Messages.getIndexOutOfRange(0, index, this.size() + 1);
			throw new IndexOutOfBoundsException(msg);
		}
		return this.doRemove(index);
	}
	@Override
	public AbByteList removeFirst() {
		if (this.size() < 1) {
			String msg = Messages.getIndexOutOfRange(0, 0, 0);
			throw new IndexOutOfBoundsException(msg);
		}
		return this.doRemove(0);
	}
	@Override
	public AbByteList removeLast() {
		if (this.size() < 1) {
			String msg = Messages.getIndexOutOfRange(0, 0, 0);
			throw new IndexOutOfBoundsException(msg);
		}
		return this.doRemove(this.size() - 1);
	}
	protected abstract AbByteList doAdd(int index, byte value);
	protected abstract AbByteList doRemove(int index);
}
