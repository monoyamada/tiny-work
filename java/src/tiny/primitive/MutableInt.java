package tiny.primitive;


public class MutableInt extends Number implements Comparable<Number> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8520184719892482079L;

	public int value;

	public MutableInt() {
		this(0);
	}
	public MutableInt(int value) {
		this.value = value;
	}
	public int hashCode() {
		return this.value;
	}
	public boolean equals(Object x) {
		return this.equalValue((MutableInt) x);
	}
	private boolean equalValue(MutableInt x) {
		return this.compareTo(x) == 0;
	}
	@Override
	public int compareTo(Number x) {
		if (x == null) {
			return -1;
		} else if (this == x) {
			return 0;
		}
		return this.value < x.intValue() ? -1 : this.value == x.intValue() ? 0 : 1;
	}
	public String toString() {
		return Integer.toString(this.value);
	}
	@Override
	public int intValue() {
		return this.value;
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
	public int get() {
		return this.value;
	}
	public MutableInt set(int value) {
		this.value = value;
		return this;
	}
}
