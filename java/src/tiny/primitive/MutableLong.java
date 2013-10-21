package tiny.primitive;

import tiny.lang.NumberHelper;

public class MutableLong extends Number implements Comparable<Number> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6633652634833285354L;

	public long value;

	public MutableLong() {
		this(0);
	}
	public MutableLong(long value) {
		this.value = value;
	}
	public int hashCode() {
		return NumberHelper.hashCode(this.value);
	}
	public boolean equals(Object x) {
		return this.equalValue((MutableLong) x);
	}
	private boolean equalValue(MutableLong x) {
		return this.compareTo(x) == 0;
	}
	@Override
	public int compareTo(Number x) {
		if (x == null) {
			return -1;
		} else if (this == x) {
			return 0;
		}
		return this.value < x.longValue() ? -1 : this.value == x.longValue() ? 0 : 1;
	}
	public String toString() {
		return Long.toString(this.value);
	}
	@Override
	public int intValue() {
		return (int) this.value;
	}
	@Override
	public long longValue() {
		return this.value;
	}
	@Override
	public float floatValue() {
		return this.value;
	}
	@Override
	public double doubleValue() {
		return this.value;
	}
	public long get() {
		return this.value;
	}
	public MutableLong set(long value) {
		this.value = value;
		return this;
	}
}
