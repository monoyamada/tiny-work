package indices;

import tiny.lang.ArrayHelper;
import tiny.lang.Messages;
import tiny.primitive.AbLongArray;
import tiny.primitive.LongPopable;
import tiny.primitive.LongPushable;

public class SimpleLongArray extends AbLongArray implements LongPushable, LongPopable {
	protected static final int INCREMENT_BITS = 3;
	public static final int INCREMENT_SIZE = 1 << INCREMENT_BITS;
	protected static final int INCREMENT_MASK = INCREMENT_SIZE - 1;
	public static final int MAX_CAPACITY = Integer.MAX_VALUE;
	private int length;
	private long[] array;

	public SimpleLongArray() {
		this.array = ArrayHelper.EMPTY_LONG_ARRAY;
	}
	public SimpleLongArray(int capacity) {
		this.array = ArrayHelper.EMPTY_LONG_ARRAY;
		this.ensureCapacity(capacity);
	}
	public long[] getArray() {
		return this.array;
	}
	protected void setArray(long[] array) {
		this.array = array;
	}
	@Override
	public int getLength() {
		return this.length;
	}
	protected void setLength(int rangeSize) {
		this.length = rangeSize;
	}
	public boolean ensureCapacity(int capacity) {
		long[] oldValue = this.getArray();
		if (capacity <= oldValue.length) {
			return false;
		}
		long[] newValue = new long[capacity];
		System.arraycopy(oldValue, 0, newValue, 0, this.getLength());
		this.setArray(newValue);
		return true;
	}
	public int getCapacity() {
		return this.getArray().length;
	}
	public SimpleLongArray pushLast(long value) {
		int size = this.getLength();
		if (this.getCapacity() <= size) {
			if (size == Integer.MAX_VALUE) {
				String msg = Messages.getFailedOperation("increase size to "
						+ ((long) size + 1));
				throw new StackOverflowError(msg);
			}
			long capacity = size + 1;
			capacity = (capacity >> INCREMENT_BITS)
					+ (0 < (capacity & INCREMENT_MASK) ? 1 : 0);
			capacity *= INCREMENT_SIZE;
			this.ensureCapacity((int) Math.min(capacity, Integer.MAX_VALUE));
		}
		this.getArray()[size] = value;
		this.setLength(size + 1);
		return this;
	}
	public boolean popLast() {
		int size = this.getLength();
		if (size < 1) {
			return false;
		}
		this.setLength(size - 1);
		return true;
	}
	public void popAll() {
		this.setLength(0);
	}
	@Override
	public boolean isEmpty() {
		return this.getLength() < 1;
	}
	@Override
	public boolean isFull() {
		return this.getCapacity() == MAX_CAPACITY;
	}
	@Override
	protected long doGet(int index) {
		return this.array[index];
	}
	@Override
	public SimpleLongArray pushValue(Number value) {
		return this.push(value.longValue());
	}
	@Override
	public long peek(long def) {
		return this.getLast(def);
	}
	@Override
	public Number peekValue(Number def) {
		if(this.isEmpty()){
			return def;
		}
		return Long.valueOf(this.peek(0));
	}
	@Override
	public SimpleLongArray push(long value) {
		return this.pushLast(value);
	}
	@Override
	public boolean pop() {
		return this.popLast();
	}
}
