package parser;

/**
 * this class represents power set of uint8_t. byte in Java is not uint8_t but
 * int8_t. thus this class dose not represent power set of java's byte.
 */
public interface ByteSet {
	public static final short MIN_VALUE = 0;
	public static final short MAX_VALUE = 255;
	public static final short MASK = 255;

	public static int EMPTY_SET = 0;
	public static int FULL_SET = EMPTY_SET + 1;
	public static int SINGLETON_SET = FULL_SET + 1;
	public static int RANGE_SET = SINGLETON_SET + 1;
	public static int COMPOSIT_SET = RANGE_SET + 1;

	public int getSetType();
	public int size();
	/**
	 * inclusive lower bound of this set.
	 * 
	 * @param def
	 *          value to be returned iff this set is an empty-set.
	 * @return
	 */
	public short lower(short def);
	/**
	 * inclusive upper bound of this set.
	 * 
	 * @param def
	 *          value to be returned iff this set is an empty-set.
	 * @return
	 */
	public short upper(short def);
	public boolean contains(short value);
	public ByteSet complement();
	public ByteSet or(ByteSet x);
	public ByteSet and(ByteSet x);
	/**
	 * gives the operation <code>this - (this and x)</code> . this operation is
	 * related XOR, XOR is given by this operation with <code>
	 * x.minus(y).or(y.minus(x))</code>
	 * 
	 * @param x
	 * @return
	 */
	public ByteSet minus(ByteSet x);

	public static class EmptySet implements ByteSet {
		protected static final ByteSet EMPTY_SET = new EmptySet();
		protected static final ByteSet FULL_SET = new FullSet();

		public static boolean isElement(short val) {
			return ByteSet.MIN_VALUE <= val && val <= ByteSet.MAX_VALUE;
		}
		private static boolean isValidRange(short lower, short upper) {
			return ByteSet.MIN_VALUE <= lower && upper <= ByteSet.MAX_VALUE
					&& lower <= upper;
		}

		protected ByteSet newEmptySet() {
			return EmptySet.EMPTY_SET;
		}
		protected ByteSet newFullSet() {
			return EmptySet.FULL_SET;
		}
		protected ByteSet newSingleton(short value) {
			if (value < ByteSet.MIN_VALUE || ByteSet.MAX_VALUE < value) {
				return this.newEmptySet();
			}
			return this.newSingleton_0(value);
		}
		protected ByteSet newSingleton_0(short value) {
			return new Singleton(value);
		}

		protected ByteSet newRange(short lower, short upper) {
			if (!isValidRange(lower, upper)) {
				return this.newEmptySet();
			}
			if (upper < lower) {
				return this.newEmptySet();
			} else if (lower == upper) {
				if (lower < ByteSet.MIN_VALUE || ByteSet.MAX_VALUE < lower) {
					return this.newEmptySet();
				}
				return this.newSingleton_0(lower);
			}
			return this.newRange_0(lower, upper);
		}

		protected ByteSet newRange_0(short lower, short upper) {
			return new Range(lower, upper);
		}

		protected ByteSet newDisjointOr(short lo0, short hi0, short lo1, short hi1) {
		}
		@Override
		public int getSetType() {
			return ByteSet.EMPTY_SET;
		}
		@Override
		public int size() {
			return 0;
		}
		@Override
		public short lower(short def) {
			return def;
		}
		@Override
		public short upper(short def) {
			return def;
		}
		@Override
		public boolean contains(short value) {
			return false;
		}
		@Override
		public ByteSet complement() {
			return this.newFullSet();
		}
		@Override
		public ByteSet or(ByteSet x) {
			return x;
		}
		@Override
		public ByteSet and(ByteSet x) {
			return this;
		}
		@Override
		public ByteSet minus(ByteSet x) {
			return this;
		}
	}

	public static class FullSet extends EmptySet {
		@Override
		public int getSetType() {
			return ByteSet.FULL_SET;
		}
		@Override
		public int size() {
			return Byte.SIZE;
		}
		@Override
		public short lower(short def) {
			return ByteSet.MIN_VALUE;
		}
		@Override
		public short upper(short def) {
			return ByteSet.MAX_VALUE;
		}
		@Override
		public boolean contains(short value) {
			return EmptySet.isElement(value);
		}
		@Override
		public ByteSet complement() {
			return this.newEmptySet();
		}
		@Override
		public ByteSet or(ByteSet x) {
			return this;
		}
		@Override
		public ByteSet and(ByteSet x) {
			return x;
		}
		@Override
		public ByteSet minus(ByteSet x) {
			return x.complement();
		}
	}

	public static class Singleton extends EmptySet {
		private short value;

		/**
		 * unguarded, not check value.
		 */
		protected Singleton(short value) {
			this.set(value);
		}
		public short get() {
			return this.value;
		}
		/**
		 * unguarded, not check value.
		 */
		protected Singleton set(short value) {
			this.value = value;
			return this;
		}
		@Override
		public int getSetType() {
			return ByteSet.SINGLETON_SET;
		}
		@Override
		public int size() {
			return 1;
		}
		@Override
		public short lower(short def) {
			return this.value;
		}
		@Override
		public short upper(short def) {
			return this.value;
		}
		@Override
		public boolean contains(short value) {
			return this.value == value;
		}
		@Override
		public ByteSet complement() {
			short hi = this.value;
			short lo = hi;
			return this.newDisjointOr(this.newRange(Byte.MIN_VALUE, --lo), this
					.newRange(++hi, Byte.MAX_VALUE));
		}
		@Override
		public ByteSet or(ByteSet x) {
			if (x.contains(this.value)) {
				return x;
			}
			return this.newDisjointOr(this, x);
		}
		@Override
		public ByteSet and(ByteSet x) {
			if (x.contains(this.value)) {
				return this;
			}
			return this.newEmptySet();
		}
		@Override
		public ByteSet minus(ByteSet x) {
			if (x.contains(this.value)) {
				return this.newEmptySet();
			}
			return this;
		}
	}

	public static class Range extends EmptySet {
		private short lower;
		private short upper;

		/**
		 * unguarded, not check values.
		 */
		public Range(short lower, short upper) {
			this.set(lower, upper);
		}
		/**
		 * unguarded, not check values.
		 */
		protected Range set(short lower, short upper) {
			this.lower = lower;
			this.upper = upper;
			return this;
		}
		@Override
		public int getSetType() {
			return ByteSet.RANGE_SET;
		}
		@Override
		public int size() {
			return this.upper - this.lower + 1;
		}
		@Override
		public short lower(short def) {
			return this.lower;
		}
		@Override
		public short upper(short def) {
			return this.upper;
		}
		@Override
		public boolean contains(short value) {
			return this.lower <= value && value <= this.upper;
		}
		@Override
		public ByteSet complement() {
			short lo = this.lower;
			short hi = this.upper;
			return this.newDisjointOr(this.newRange(Byte.MIN_VALUE, --lo), this
					.newRange(++hi, Byte.MAX_VALUE));
		}
		@Override
		public ByteSet or(ByteSet x) {
			return this;
		}
		@Override
		public ByteSet and(ByteSet x) {
			if (x.contains(this.get())) {
				return this;
			}
			return this.newEmptySet();
		}
		@Override
		public ByteSet minus(ByteSet x) {
			return x.complement();
		}

	}
}
