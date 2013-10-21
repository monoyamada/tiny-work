package group;

import java.io.IOException;
import java.util.Arrays;

import tiny.function.LexicographicalOrders;
import tiny.lang.ArrayHelper;
import tiny.lang.Messages;
import tiny.lang.StringHelper;
import tiny.primitive.AbLongArray;

public class LongPolynomial extends AbLongArray implements
		Comparable<LongPolynomial> {
	static class DividedByZero extends RuntimeException {
		private static final long serialVersionUID = 463586406154794459L;

		public DividedByZero() {
			super("divided by zero");
		}
	}

	// static class LongArray extends LongArrayList {
	// public LongArray() {
	// super();
	// }
	// public LongArray(int capacity) {
	// super(capacity);
	// }
	// public LongArray(long[] array) {
	// super(array);
	// }
	// @Override
	// protected LongArray setSize(int size) {
	// return (LongArray) super.setSize(size);
	// }
	// }

	// static class NotDividable extends RuntimeException {
	// private static final long serialVersionUID = -3079105241723049738L;
	//
	// private static String msg(long x, long y) {
	// return "can not divide " + x + " by " + y;
	// }
	// public NotDividable(long x, long y) {
	// super(msg(x, y));
	// }
	//
	// }

	/**
	 * may not singleton.
	 */
	public static final LongPolynomial ZERO = new LongPolynomial(null);
	private static final LongPolynomial ONE = create(1);

	public static LongPolynomial create(long... ks) {
		if (ks == null || ks.length < 1) {
			return LongPolynomial.ZERO;
		} else {
			int n = ks.length;
			while (0 < n--) {
				if (ks[n] != 0) {
					++n;
					break;
				}
			}
			if (n == 0) {
				return LongPolynomial.ZERO;
			} else if (n < ks.length) {
				ks = ArrayHelper.sub(ks, 0, n);
			}
		}
		return new LongPolynomial(ks);
	}
	public static LongPolynomial plus(long x, LongPolynomial y) {
		return plus(y, x);
	}
	public static LongPolynomial plus(LongPolynomial x, long y) {
		if (x.isZero()) {
			if (y == 0) {
				return ZERO;
			} else if (y == 1) {
				return ONE;
			}
			return new LongPolynomial(new long[] { y });
		} else if (y == 0) {
			return x;
		} else if (x.size() == 1) {
			if (x.get(0) == y) {
				return ZERO;
			}
		}
		long[] ks = x.ks.clone();
		ks[0] += y;
		return new LongPolynomial(ks);
	}
	public static LongPolynomial plus(LongPolynomial x, LongPolynomial y) {
		if (x.isZero()) {
			return y;
		} else if (y.isZero()) {
			return x;
		}
		long[] k1 = x.ks;
		long[] k2 = y.ks;
		if (k1.length == k2.length) {
			long[] zs = plus_n(k1, k2, k1.length);
			return zs == null ? ZERO : new LongPolynomial(zs);
		} else if (k1.length < k2.length) {
			long[] k3 = k2;
			k2 = k1;
			k1 = k3;
		}
		long[] ks = k1.clone();
		for (int n = k2.length; 0 < n--;) {
			ks[n] += k2[n];
		}
		return new LongPolynomial(ks);
	}
	/**
	 * @return <code>null</code> means 0.
	 */
	private static long[] plus_n(long[] xs, long[] ys, int n) {
		long[] zs = null;
		while (0 < n--) {
			long z = xs[n] + ys[n];
			if (zs == null) {
				if (z == 0) {
					continue;
				}
				zs = new long[n + 1];
			}
			zs[n] = z;
		}
		return zs;
	}
	public static LongPolynomial minus(LongPolynomial x, long y) {
		if (x.isZero()) {
			if (y == 0) {
				return ZERO;
			} else if (y == -1) {
				return ONE;
			}
			return new LongPolynomial(new long[] { -y });
		} else if (y == 0) {
			return x;
		} else if (x.size() == 1) {
			if (x.get(0) == -y) {
				return ZERO;
			}
		}
		long[] ks = x.ks.clone();
		ks[0] -= y;
		return new LongPolynomial(ks);
	}
	public static LongPolynomial minus(LongPolynomial x, LongPolynomial y) {
		if (x == y) {
			return ZERO;
		} else if (x.isZero()) {
			return times(-1, y);
		} else if (y.isZero()) {
			return x;
		}
		long[] k1 = x.ks;
		long[] k2 = y.ks;
		if (k1.length == k2.length) {
			long[] zs = minus_n(k1, k2, k1.length);
			return zs == null ? ZERO : new LongPolynomial(zs);
		}
		long[] ks = null;
		if (k1.length > k2.length) {
			ks = k1.clone();
		} else {
			ks = new long[k2.length];
			System.arraycopy(k1, 0, ks, 0, k1.length);
		}
		for (int n = k2.length; 0 < n--;) {
			ks[n] -= k2[n];
		}
		return new LongPolynomial(ks);
	}
	/**
	 * @return <code>null</code> means 0.
	 */
	private static long[] minus_n(long[] xs, long[] ys, int n) {
		long[] zs = null;
		while (0 < n--) {
			long z = xs[n] - ys[n];
			if (zs == null) {
				if (z == 0) {
					continue;
				}
				zs = new long[n + 1];
			}
			zs[n] = z;
		}
		return zs;
	}
	public static LongPolynomial times(LongPolynomial x, long y) {
		return times(y, x);
	}
	public static LongPolynomial times(long x, LongPolynomial y) {
		if (x == 0) {
			return LongPolynomial.ZERO;
		} else if (x == 1) {
			return y;
		} else if (y.isZero()) {
			return y;
		}
		long[] ks = y.ks.clone();
		for (int n = ks.length; 0 < n--;) {
			ks[n] *= x;
		}
		return new LongPolynomial(ks);
	}
	public static LongPolynomial times(LongPolynomial x, LongPolynomial y) {
		if (x.isZero()) {
			return x;
		} else if (x.isConstant()) {
			return times(x.get(0), y);
		} else if (y.isZero()) {
			return y;
		} else if (y.isConstant()) {
			return times(y.get(0), x);
		}
		long[] k1 = x.ks;
		long[] k2 = y.ks;
		long[] ks = new long[k1.length + k2.length - 1];
		for (int n1 = k1.length; 0 < n1--;) {
			for (int n2 = k2.length; 0 < n2--;) {
				ks[n1 + n2] += k1[n1] * k2[n2];
			}
		}
		return new LongPolynomial(ks);
	}
	private static LongPolynomial remainder(LongPolynomial x, long y) {
		if (y == 0 && x.isZero()) {
			return ONE;
		} else if (y == 0) {
			throw new DividedByZero();
		} else if (x.isZero()) {
			return x;
		}
		long[] xs = x.ks.clone();
		int xn = remainder(xs, xs.length, y);
		if (xn == 0) {
			return ZERO;
		}
		if (xn < xs.length) {
			xs = ArrayHelper.sub(xs, 0, xn);
		}
		return new LongPolynomial(xs);
	}
	private static int remainder(long[] xs, int xn, long y) {
		if (y == 0 || xn < 1) {
			return xn;
		}
		for (; 0 < xn--;) {
			long q = xs[xn] / y;
			if (q != 0) {
				xs[xn] -= q * y;
			}
			if (xs[xn] == 0) {
				continue;
			}
			return ++xn;
		}
		// xn = - 1
		// xs[xn + 1] = xs[xn + 2] =...= 0
		if (xn < 0) {
			return 0;
		}
		while (xs[xn] == 0) {
			if (xn == 0) {
				return 0;
			}
			--xn;
		}
		return ++xn;
	}
	public static LongPolynomial remainder(LongPolynomial x, LongPolynomial y) {
		if (y.isZero() && x.isZero()) {
			return ONE;
		} else if (y.isZero()) {
			throw new DividedByZero();
		} else if (x.size() < y.size()) {
			return x;
		}
		long[] xs = x.ks.clone();
		long[] ys = y.ks;
		int xn = remainder(xs, xs.length, ys, ys.length);
		if (xn == 0) {
			return ZERO;
		}
		if (xn < xs.length) {
			xs = ArrayHelper.sub(xs, 0, xn);
		}
		return new LongPolynomial(xs);
	}
	/**
	 * @param xs
	 *          destructive. polynomial to be divided as input, polynomial of
	 *          remainder as output. remainder will be written in this array.
	 * @param xn
	 * @param ys
	 * @param yn
	 * @return length of remainder. let <code>n</code> be the return value,
	 *         0..(n-1) is the remainder. returning <code>0</code> means zero
	 *         remainder.
	 */
	private static int remainder(long[] xs, int xn, long[] ys, int yn) {
		if (yn < 1 || xn < yn) {
			return xn;
		}
		// 1 <= yn <= xn
		for (--yn; yn < xn--;) {
			long q = xs[xn] / ys[yn];
			if (q != 0) {
				for (int i = 0; i <= yn; ++i) {
					xs[xn - i] -= q * ys[yn - i];
				}
			}
			if (xs[xn] == 0) {
				continue;
			}
			return ++xn;
		}
		// xn = yn - 1 => -1 <= xn
		// xs[xn + 1] = xs[xn + 2] =...= 0
		if (xn < 0) {
			return 0;
		}
		while (xs[xn] == 0) {
			if (xn == 0) {
				return 0;
			}
			--xn;
		}
		return ++xn;
	}
	public static LongPolynomial quotient(LongPolynomial x, long y) {
		if (y == 0 && x.isZero()) {
			return ONE;
		} else if (y == 0) {
			throw new DividedByZero();
		}
		long[] xs = x.ks.clone();
		int n = quotient(xs, xs.length, y);
		if (n < 1) {
			return ZERO;
		} else if (n < xs.length) {
			xs = ArrayHelper.sub(xs, 0, n);
		}
		return new LongPolynomial(xs);
	}
	/**
	 * 
	 * @param xs
	 * @param xn
	 * @param y
	 * @return the length of polynomial, staring index of polynomial is always
	 *         <code>0</code>.
	 */
	static int quotient(long[] xs, int xn, long y) {
		if (y == 0 || xn < 1) {
			return 0;
		}
		// 1 <= xn
		int hi = -1;
		for (; 0 < xn--;) {
			long q = xs[xn] / y;
			if (q != 0) {
				xs[xn] -= q * y;
				if (hi < 0) {
					hi = xn;
				}
			}
			if (xs[xn] == 0) {
				xs[xn] = q;
				continue;
			}
			xs[xn] = q;
			Arrays.fill(xs, 0, xn, 0);
			break;
		}
		// 0 <= hi or hi = -1
		return hi < 0 ? 0 : hi + 1;
	}
	public static LongPolynomial quotient(LongPolynomial x, LongPolynomial y) {
		if (y.isZero() && x.isZero()) {
			return ONE;
		} else if (y.isZero()) {
			throw new DividedByZero();
		} else if (x.size() < y.size()) {
			return ZERO;
		}
		long[] xs = x.ks.clone();
		long[] ys = y.ks;
		int x0 = ys.length - 1;
		int n = quotient(xs, xs.length, ys, ys.length);
		if (n < 1) {
			return ZERO;
		} else if (0 < x0 || x0 + n < xs.length) {
			xs = ArrayHelper.sub(xs, x0, x0 + n);
		}
		return new LongPolynomial(xs);
	}
	/**
	 * @param xs
	 * @param xn
	 * @param ys
	 * @param yn
	 * @return the length of polynomial, staring index of polynomial is always
	 *         <code>yn- 1</code>.
	 */
	static int quotient(long[] xs, int xn, long[] ys, int yn) {
		if (yn < 1 || xn < yn) {
			return 0;
		}
		// 1 <= yn <= xn
		int hi = -1;
		for (--yn; yn < xn--;) {
			long q = xs[xn] / ys[yn];
			if (q != 0) {
				for (int i = 0; i <= yn; ++i) {
					xs[xn - i] -= q * ys[yn - i];
				}
				if (hi < 0) {
					hi = xn;
				}
			}
			if (xs[xn] == 0) {
				xs[xn] = q;
				continue;
			}
			xs[xn] = q;
			Arrays.fill(xs, yn, xn, 0);
			break;
		}
		// yn <= hi or hi = -1
		return hi < 0 ? 0 : hi - yn + 1;
	}

	/**
	 * @param x
	 * @return
	 * @throws DividedByZero
	 *           iff <code>x.isZero()</code>.
	 */
	public static long gcdOf(LongPolynomial x) {
		return gcdOf(x.ks, x.ks.length);
	}
	/**
	 * @param xs
	 * @param xn
	 * @return
	 * @throws DividedByZero
	 *           iff <code>xn == 0</code>.
	 */
	private static long gcdOf(long[] xs, int xn) {
		if (xn < 1) {
			throw new DividedByZero();
		}
		long x = xs[--xn];
		while (0 < xn--) {
			long y = xs[xn];
			if (y == 0) {
				continue;
			}
			x = gcd(x, y);
		}
		return x;
	}
	public static long gcd(long x, long y) {
		if (x == 0 || y == 0) {
			throw new DividedByZero();
		}
		x = Math.abs(x);
		y = Math.abs(y);
		if (x < y) {
			long z = y;
			y = x;
			x = z;
		}
		while (y != 0) {
			long r = x % y;
			x = y;
			y = r;
		}
		return x;
	}

	/**
	 * @param x
	 * @param y
	 * @return
	 * @throws DividedByZero
	 *           iff <code>x.isZero() && ! y.isZero()</code> or vice versa.
	 */
	public static LongPolynomial gcd(LongPolynomial x, LongPolynomial y) {
		if (x.isZero() && y.isZero()) {
			return ONE;
		} else if (x.isZero() || y.isZero()) {
			throw new DividedByZero();
		}
		if (x.size() < y.size()) {
			LongPolynomial z = x;
			x = y;
			y = z;
		}
		if (y.size() == 1) {
			long r = gcd(x.gcd(), y.get(0));
			return new LongPolynomial(new long[] { r });
		}
		long[] xs = x.ks.clone();
		int xn = xs.length;
		long[] ys = y.ks.clone();
		int yn = ys.length;
		long scale = 1;
		while (0 < yn) {
			if (xs[xn - 1] % ys[yn - 1] != 0) {
				long s = ys[yn-1]/gcd(xs[xn-1], ys[yn-1]);
				for (int xi = xn; 0 < xi--;) {
					xs[xi] *= s;
				}
				scale *= s;
			}
			xn = remainder(xs, xn, ys, yn);
			long[] zs = xs;
			xs = ys;
			xn = yn;
			ys = zs;
			yn = xn;
		}
		if (scale != 1) {
			for (int xi = xn; 0 < xi--;) {
				if (xs[xi] % scale != 0) {
					return ONE;
				}
				xs[xi] /= scale;
			}
		}
		if (xn < xs.length) {
			xs = ArrayHelper.sub(xs, 0, xn);
		}
		return new LongPolynomial(xs);
	}

	final long[] ks;

	/**
	 * the specified array will not be copied. An empty array means 0. non-empty
	 * array means non-zero polynomial. then right most element of non-empty array
	 * must not be a zero.
	 * 
	 * @param ks
	 *          represents coefficients of polynomial.
	 */
	public LongPolynomial(long[] ks) {
		if (ks == null) {
			ks = ArrayHelper.EMPTY_LONG_ARRAY;
		} else if (0 < ks.length && ks[ks.length - 1] == 0) {
			if (ks.length == 1) {
				ks = ArrayHelper.EMPTY_LONG_ARRAY;
			} else {
				String msg = Messages.getUnexpectedValue("coeff. of heightes degree",
						"not a zero", "0");
				throw new IllegalArgumentException(msg);
			}
		}
		this.ks = ks;
	}
	/**
	 * will not copy {@link #ks}.
	 */
	@Override
	public LongPolynomial clone() {
		try {
			return (LongPolynomial) super.clone();
		} catch (CloneNotSupportedException ex) {
			throw new Error(ex);
		}
	}
	@Override
	public boolean equals(Object x) {
		if (x == null) {
			return false;
		} else if (this == x) {
			return true;
		}
		try {
			return this.compareTo((LongPolynomial) x) == 0;
		} catch (Exception ex) {
			return false;
		}
	}
	@Override
	public int hashCode() {
		return Arrays.hashCode(this.ks);
	}
	@Override
	public int compareTo(LongPolynomial x) {
		if (x == null) {
			return -1;
		} else if (this == x) {
			return 0;
		}
		return LexicographicalOrders.compare(this.ks, x.ks);
	}
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		try {
			return this.toString(buffer).toString();
		} catch (IOException ex) {
			ex.printStackTrace();
			return buffer.toString();
		}
	}
	public Appendable toString(Appendable output) throws IOException {
		if (this.ks.length < 1) {
			return output.append("[]");
		}
		return StringHelper.join(output.append('['), this.ks, ", ").append(']');
	}
	public Appendable toPolynomial(Appendable output, String variable, String zero)
			throws IOException {
		if (variable == null) {
			variable = "x";
		}
		if (zero == null) {
			zero = "0";
		}
		if (this.ks.length < 1) {
			return output.append(zero);
		}
		long[] ks = this.ks;
		boolean did = false;
		for (int i = 0, n = ks.length; i < n; ++i) {
			long k = ks[i];
			if (k == 0) {
				continue;
			} else if (k < 0) {
				if (did) {
					output.append(" - ");
				} else {
					output.append("- ");
					did = true;
				}
				k -= 1;
			} else if (did) {
				output.append(" + ");
			} else {
				did = true;
			}
			if (i == 0) {
				output.append(Long.toString(k));
			} else {
				if (k != 1) {
					// output.append(Long.toString(k)).append(' ');
					output.append(Long.toString(k));
				}
				output.append(variable);
				if (1 < i) {
					output.append('^').append(Integer.toString(i));
				}
			}
		}
		return output;
	}
	/**
	 * size - 1 = degree
	 */
	@Override
	public int size() {
		return this.ks.length;
	}
	@Override
	protected long doGet(int index) {
		return this.ks[index];
	}
	public boolean isZero() {
		return this.ks.length < 1;
	}
	public boolean isOne() {
		return this.ks.length == 1 && this.ks[0] == 1;
	}
	/**
	 * including zero.
	 * 
	 * @return
	 */
	public boolean isConstant() {
		return this.ks.length < 2;
	}
	public int degree() {
		return this.ks.length - 1;
	}
	public LongPolynomial plus(long x) {
		return LongPolynomial.plus(this, x);
	}
	public LongPolynomial plus(LongPolynomial x) {
		return LongPolynomial.plus(this, x);
	}
	public LongPolynomial minus(long x) {
		return LongPolynomial.minus(this, x);
	}
	public LongPolynomial minus(LongPolynomial x) {
		return LongPolynomial.minus(this, x);
	}
	public LongPolynomial times(long x) {
		return LongPolynomial.times(this, x);
	}
	public LongPolynomial times(LongPolynomial x) {
		return LongPolynomial.times(this, x);
	}
	public LongPolynomial remainder(long x) {
		return LongPolynomial.remainder(this, x);
	}
	public LongPolynomial remainder(LongPolynomial x) {
		return LongPolynomial.remainder(this, x);
	}
	public LongPolynomial quotient(long x) {
		return LongPolynomial.quotient(this, x);
	}
	public LongPolynomial quotient(LongPolynomial x) {
		return LongPolynomial.quotient(this, x);
	}
	/**
	 * @return gcd of coefficients.
	 * @throws DividedByZero
	 *           iff <code>this.isZero()</code>.
	 */
	public long gcd() {
		return LongPolynomial.gcdOf(this);
	}
	public long gcd(long x) {
		return LongPolynomial.gcd(this.gcd(), x);
	}
	public LongPolynomial gcd(LongPolynomial x) {
		return LongPolynomial.gcd(this, x);
	}
}
