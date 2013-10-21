package tiny.function;

import java.util.Iterator;

import tiny.lang.Messages;

public abstract class AbArray<Value> extends AbFunction<Number, Value>
		implements Array<Value> {
	@Override
	public Value getValue(Number key, Value defaultValue) {
		if (key == null) {
			return defaultValue;
		}
		final int index = key.intValue();
		if (index < 0 || this.size() <= index) {
			return defaultValue;
		}
		return this.doGetValue(index);
	}
	@Override
	public Value evaluate(Number key) throws Exception {
		if (key == null) {
			String msg = Messages.getUnexpectedValue("index", "not null", key);
			throw new NullPointerException(msg);
		}
		final int index = key.intValue();
		if (index < 0 || this.size() <= index) {
			String msg = Messages.getIndexOutOfRange(0, index, this.size());
			throw new IndexOutOfBoundsException(msg);
		}
		return this.doGetValue(index);
	}
	@Override
	public Value getValue(int index) {
		if (index < 0 || this.size() <= index) {
			String msg = Messages.getIndexOutOfRange(0, index, this.size());
			throw new IndexOutOfBoundsException(msg);
		}
		return this.doGetValue(index);
	}
	@Override
	public Iterator<Value> iterator() {
		return new ArrayIterator<Value>(this);
	}
	/**
	 * gets value without range check.
	 * 
	 * @param index
	 * @return
	 */
	protected abstract Value doGetValue(int index);
}
