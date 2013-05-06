package tiny.primitive;

import tiny.lang.Messages;

public class LongFraction extends Number {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8266918089275237017L;
	private static final String DIVIDES_BY_ZERO = "divides by zero";
	private static final String NULL_PARAM = Messages.getUnexpectedValue("param",
			"non-null", "null");
	private final long numerator;
	private final long denominator;

	public LongFraction(long numerator, long denominator) {
		if (denominator == 0) {
			throw new IllegalArgumentException(DIVIDES_BY_ZERO);
		} else if (0 < denominator) {
			this.numerator = numerator;
			this.denominator = denominator;
		} else {
			this.numerator = -numerator;
			this.denominator = -denominator;
		}
	}

	public LongFraction(long numerator) {
		this.numerator = numerator;
		this.denominator = 1;
	}

	public long getNumerator() {
		return this.numerator;
	}

	public long getDenominator() {
		return this.denominator;
	}

	public byte byteValue() {
		return (byte) this.doubleValue();
	}

	public double doubleValue() {
		return ((double) numerator) / ((double) denominator);
	}

	public float floatValue() {
		return (float) this.doubleValue();
	}

	public int intValue() {
		return (int) this.doubleValue();
	}

	public long longValue() {
		return (long) this.doubleValue();
	}

	public short shortValue() {
		return (short) this.doubleValue();
	}

	public boolean equals(LongFraction frac) {
		return this.compareTo(frac) == 0;
	}

	public int compareTo(LongFraction x) {
		long a = this.getNumerator() * x.getDenominator();
		long b = x.getNumerator() * this.getDenominator();
		return a < b ? -1 : b < a ? 1 : 0;
	}

	public boolean isZero() {
		return this.getNumerator() == 0;
	}
	public boolean isOne() {
		return this.getNumerator() == this.getDenominator();
	}
	public int sign() {
		long n = this.getNumerator();
		return n == 0 ? 0 : 0 < n ? 1 : -1;
	}
	public LongFraction minus() {
		if (this.isZero()) {
			return this;
		}
		return new LongFraction(-this.getNumerator(), this.getDenominator());
	}
	public LongFraction plus(LongFraction x) {
		if (x == null) {
			throw new IllegalArgumentException(NULL_PARAM);
		} else if (this.isZero()) {
			return x;
		} else if (x.isZero()) {
			return this;
		}
		return new LongFraction(this.getNumerator() * x.getDenominator()
				+ this.getDenominator() * x.getNumerator(), this.getDenominator()
				* x.getDenominator());
	}
	public LongFraction minus(LongFraction x) {
		if (x == null) {
			throw new IllegalArgumentException(NULL_PARAM);
		} else if (this.isZero()) {
			return x.minus();
		} else if (x.isZero()) {
			return this;
		}
		return new LongFraction(this.getNumerator() * x.getDenominator()
				- this.getDenominator() * x.getNumerator(), this.getDenominator()
				* x.getDenominator());
	}
	public LongFraction multiplies(LongFraction x) {
		if (x == null) {
			throw new IllegalArgumentException(NULL_PARAM);
		} else if (this.isZero()) {
			return this;
		} else if (x.isZero()) {
			return x;
		}
		return new LongFraction(this.getNumerator() * x.getNumerator(),
				this.getDenominator() * x.getDenominator());
	}
	public LongFraction divides(LongFraction x) {
		if (x == null) {
			throw new IllegalArgumentException(NULL_PARAM);
		} else if (x.isZero()) {
			throw new IllegalArgumentException(DIVIDES_BY_ZERO);
		} else if (this.isZero()) {
			return this;
		}
		return new LongFraction(this.getNumerator() * x.getDenominator(),
				this.getDenominator() * x.getNumerator());
	}
	public boolean isInteger() {
		long x = this.numerator;
		long y = this.denominator;
		return x < y ? false : x % y == 0;
	}
	public long getIntegerPart() {
		long x = this.numerator;
		long y = this.denominator;
		return x < y ? 0 : x / y;
	}

	public LongFraction powers(int n) {
		long x = this.numerator;
		if (x == 0) {
			if (n == 0) {
				return new LongFraction(1);
			} else if (n < 0) {
				throw new IllegalArgumentException(DIVIDES_BY_ZERO);
			}
			return this;
		} else if (n == 0) {
			return new LongFraction(1);
		} else if (n == 1) {
			return this;
		} else if (n == -1) {
			if (x == 0) {
				throw new IllegalArgumentException(DIVIDES_BY_ZERO);
			}
			return new LongFraction(this.denominator, x);
		}
		long y = this.denominator;
		if (n < 0) {
			x = this.denominator;
			y = this.numerator;
			n = -n;
		}
		long xx = 1;
		long yy = 1;
		while (0 < n) {
			if ((n & 1) == 1) {
				xx *= x;
				yy *= y;
			}
			n >>= 1;
			x *= x;
			y *= y;
		}
		return new LongFraction(xx, yy);
	}
}
