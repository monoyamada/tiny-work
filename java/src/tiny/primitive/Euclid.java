package tiny.primitive;

import java.io.IOException;

public class Euclid {
	protected static final double ZERO_TOLERANCE = 1.0E-12;

	public static String dividesByZero(String what) {
		if (what == null) {
			return "divides by zero";
		}
		return "divides by zero " + what;
	}

	int gcd(int x, int y) {
		if (y == 0) {
			throw new IllegalArgumentException(dividesByZero("y"));
		}
		while (y != 0) {
			int r = x % y;
			x = y;
			y = r; /* swap */
		}
		return x;
	}
	/**
	 * when x or y is negative, the sign of answer is not correct. correctly
	 * speaking, there is no unique definition of divisor's sign. for example, the
	 * pair of natural (2,4) can be written in integer with both of the forms
	 * 2(1,2) and -2(-1,-2). we can define GCD of 2 and 4 by 2 or -2 in this case.
	 * 
	 * @param x
	 *          greater or equals than 0.
	 * @param y
	 *          greater or equals than 1.
	 * @return greatest common divisor of x and y.
	 */
	public static long gcd(long x, long y) {
		if (y == 0) {
			throw new IllegalArgumentException(dividesByZero("y"));
		}
		while (y != 0) {
			long r = x % y;
			x = y;
			y = r;
		}
		return x;
	}
	/**
	 * gets Bezout's identity. usage: <code>
	 * output = new long[2];
	 * gcd(output, 6, 9); // output = {-1, 1}
	 * </code>
	 * 
	 * @param x
	 *          input
	 * @param y
	 *          input
	 * @param output
	 *          output buffer to store result, length of array must be greater or
	 *          equals than 2.
	 * @return greatest common divisor g of x and y that satisfies: <code>
	 * g = output[0] * x + output[1] * y
	 * </code>
	 */
	public static long gcd(long[] output, long x, long y) {
		long z00 = 1;
		long z01 = 0;
		long z10 = 0;
		long z11 = 1;
		long n0 = x;
		long n1 = y;
		while (n1 != 0) {
			long q = n0 / n1;
			long w0 = z00 - q * z10;
			long w1 = z01 - q * z11;
			z00 = z10;
			z01 = z11;
			z10 = w0;
			z11 = w1;
			n0 = n1;
			n1 = x * z10 + y * z11;
		}
		output[0] = z00;
		output[1] = z01;
		return n0;
	}

	public static String notMonic(String what) {
		if (what == null) {
			return "not a monic";
		}
		return "not a monic  " + what;
	}
	public static String notDividable(String what, long x, long y) {
		if (what == null) {
			return "not a long=" + x + "/" + y;
		}
		return what + " not a long=" + x + "/" + y;
	}

	protected static int highest(double[] xs, int begin, int end, double tolerance) {
		while (begin < end--) {
			double x = xs[end];
			if (x < -tolerance || tolerance < x) {
				return end + 1;
			}
		}
		return end + 1;
	}
	protected static int highest(long[] xs, int begin, int end) {
		while (begin < end--) {
			if (xs[end] != 0) {
				return end + 1;
			}
		}
		return end + 1;
	}

	/**
	 * gets the quotient and reminder. <code>
	 * xs = q ys + r
	 * </code>. the way to extract r and q from rq
	 * is the followings: <code>
	 * r = xs.sub(0, ys.size - 1);
	 * q = xs.sub(ys.size - 1, rq.size);
	 * </code> xs will be destructed and will be filled with r and q.
	 * 
	 * @param xs
	 *          input and output.
	 * @param nx
	 * @param ys
	 *          input.
	 * @param ny
	 */
	public static int divides(double[] xs, int nx, double[] ys, int ny, double tol) {
		ny = highest(ys, 0, ny, tol);
		if (ny < 1) {
			throw new IllegalArgumentException(dividesByZero("y"));
		}
		nx = highest(xs, 0, nx, tol);
		double y = ys[ny - 1];
		int ix = nx;
		while (ny <= ix--) {
			double x = xs[ix];
			if (-tol <= x && x <= tol) {
				continue;
			}
			x = xs[ix] /= y;
			for (int iy = ny - 1; 0 < iy--;) {
				xs[ix - ny + iy + 1] -= ys[iy] * x;
			}
		}
		return ix + 1;
	}
	public static int divides(long[] xs0, long[] xs1, int nx, long[] ys0,
			long[] ys1, int ny) {
		return divides(xs0, xs1, nx, ys0, ys1, ny, true);
	}
	/**
	 * @param xs0
	 *          series of numerator of coefficients.
	 * @param xs1
	 *          series of denominator of coefficients.
	 * @param nx
	 * @param ys0
	 *          series of numerator of coefficients.
	 * @param ys1
	 *          series of denominator of coefficients.
	 * @param ny
	 * @param gcd
	 *          specifies whether reduce fraction with gcd or not.
	 * @return
	 */
	public static int divides(long[] xs0, long[] xs1, int nx, long[] ys0,
			long[] ys1, int ny, boolean gcd) {
		ny = highest(ys0, 0, ny);
		if (ny < 1) {
			throw new IllegalArgumentException(dividesByZero("y"));
		}
		nx = highest(xs0, 0, nx);
		long y0 = ys0[ny - 1];
		long y1 = ys1[ny - 1];
		int ix = nx;
		while (ny <= ix--) {
			if (xs0[ix] == 0) {
				continue;
			}
			xs0[ix] *= y1;
			xs1[ix] *= y0;
			reduce(xs0, xs1, ix, gcd);
			long x0 = xs0[ix];
			long x1 = xs1[ix];
			for (int iy = ny - 1; 0 < iy--;) {
				int jx = ix - ny + iy + 1;
				xs0[jx] = xs0[jx] * ys1[iy] * x1 - xs1[jx] * ys0[iy] * x0;
				xs1[jx] *= ys1[iy] * x1;
				reduce(xs0, xs1, jx, gcd);
			}
		}
		return ix + 1;
	}
	protected static void reduce(long[] xs0, long[] xs1, int ind, boolean gcd) {
		if (xs0[ind] == 0) {
			xs1[ind] = 1;
		} else if (xs0[ind] == xs1[ind]) {
			xs0[ind] = 1;
			xs1[ind] = 1;
		} else if (xs0[ind] == -xs1[ind]) {
			xs0[ind] = -1;
			xs1[ind] = 1;
		} else if (xs0[ind] % xs1[ind] == 0) {
			xs0[ind] = xs0[ind] / xs1[ind];
			xs1[ind] = 1;
		} else {
			if (xs1[ind] < 0) {
				xs0[ind] = -xs0[ind];
				xs1[ind] = -xs1[ind];
			}
			if (gcd) {
				long g = Euclid.gcd(Math.abs(xs0[ind]), xs1[ind]);
				if (g != 1) {
					xs0[ind] /= g;
					xs1[ind] /= g;
				}
			}
		}
	}
	public static int divides(long[] xs, int nx, long[] ys, int ny) {
		ny = highest(ys, 0, ny);
		if (ny < 1) {
			throw new IllegalArgumentException(dividesByZero("y"));
		}
		nx = highest(xs, 0, nx);
		long y = ys[ny - 1];
		int ix = nx;
		while (ny <= ix--) {
			long x = xs[ix];
			if (x == 0) {
				continue;
			} else if (x % y != 0) {
				throw new IllegalArgumentException(notDividable(null, x, y));
			}
			x = xs[ix] /= y;
			for (int iy = ny - 1; 0 < iy--;) {
				xs[ix - ny + iy + 1] -= ys[iy] * x;
			}
		}
		return ix + 1;
	}

	public static class GcdDouble {
		public final double[] g;
		public final int size;

		public GcdDouble(double[] g, int size) {
			this.g = g;
			this.size = size;
		}
	}

	public static GcdDouble gcd(double[] xs, int nx, double[] ys, int ny,
			double tol) {
		nx = highest(xs, 0, nx, tol);
		ny = highest(ys, 0, ny, tol);
		if (nx == 0 || ny == 0) {
			throw new IllegalArgumentException(dividesByZero("x or y"));
		} else if (nx < ny) {
			double[] zs = xs;
			int nz = nx;
			xs = ys;
			nx = ny;
			ys = zs;
			ny = nz;
		}
		while (0 < ny) {
			int nr = Euclid.divides(xs, nx, ys, ny, tol);
			double[] rs = xs;
			nr = highest(rs, 0, nr, tol);
			// Arrays.fill(rs, nr, nx, 0);
			xs = ys;
			nx = ny;
			ys = rs;
			ny = nr;
		}
		return new GcdDouble(xs, nx);
	}

	public static class GcdFraction {
		public final long[] g0;
		public final long[] g1;
		public final int size;

		public GcdFraction(long[] g0, long[] g1, int size) {
			this.g0 = g0;
			this.g1 = g1;
			this.size = size;
		}
	}

	public static GcdFraction gcd(long[] xs0, long[] xs1, int nx, long[] ys0,
			long[] ys1, int ny) {
		nx = highest(xs0, 0, nx);
		ny = highest(ys0, 0, ny);
		if (nx == 0 || ny == 0) {
			throw new IllegalArgumentException(dividesByZero("x or y"));
		} else if (nx < ny) {
			long[] zs0 = xs0;
			long[] zs1 = xs1;
			int nz = nx;
			xs0 = ys0;
			xs1 = ys1;
			nx = ny;
			ys0 = zs0;
			ys1 = zs1;
			ny = nz;
		}
		while (0 < ny) {
			int nr = Euclid.divides(xs0, xs1, nx, ys0, ys1, ny);
			long[] rs0 = xs0;
			long[] rs1 = xs1;
			nr = highest(rs0, 0, nr);
			// Arrays.fill(rs0, nr, nx, 0);
			xs0 = ys0;
			xs1 = ys1;
			nx = ny;
			ys0 = rs0;
			ys1 = rs1;
			ny = nr;
		}
		return new GcdFraction(xs0, xs1, nx);
	}

	public static class GcdLong {
		public final long[] g;
		public final int size;

		public GcdLong(long[] g, int size) {
			this.g = g;
			this.size = size;
		}
	}

	public static GcdLong gcd(long[] xs, int nx, long[] ys, int ny) {
		nx = highest(xs, 0, nx);
		ny = highest(ys, 0, ny);
		if (nx == 0 || ny == 0) {
			throw new IllegalArgumentException(dividesByZero("x or y"));
		} else if (nx < ny) {
			long[] zs = xs;
			int nz = nx;
			xs = ys;
			nx = ny;
			ys = zs;
			ny = nz;
		}
		while (0 < ny) {
			int nr = Euclid.divides(xs, nx, ys, ny);
			long[] rs = xs;
			nr = highest(rs, 0, nr);
			// Arrays.fill(rs, nr, nx, 0);
			xs = ys;
			nx = ny;
			ys = rs;
			ny = nr;
		}
		return new GcdLong(xs, nx);
	}

	/**
	 * <ol>
	 * <li>extracts least common multiplier of denominators, say lcm.
	 * <li>multiplies lcm to each fractions.
	 * <li>extracts greatest common divisor of numerators, say gcd.
	 * <li>returns a pair <code>q:=(gcd, lcm)</code> and modified fractions (all
	 * denominators equals 1). let f be input fractions, g be modified fractions,
	 * then these are satisfied the equation: <code>f = qg</code>.
	 * </ol>
	 * 
	 * @param scale
	 * @param xs0
	 * @param xs1
	 * @param begin
	 * @param end
	 */
	public static void factor(long[] scale, long[] xs0, long[] xs1, int begin,
			int end) {
		long lcm = lcm(xs1, begin, end);
		for (int n = end; begin < n--;) {
			xs0[n] *= (xs1[n] < 0 ? -1 : 1) * lcm / xs1[n];
			xs1[n] = 1;
		}
		long gcd = gcd(xs0, begin, end);
		for (int n = end; begin < n--;) {
			xs0[n] /= gcd;
		}
		scale[0]=gcd;
		scale[1]=lcm;
	}
	/**
	 * skips 0, 1, -1.
	 * 
	 * @param array
	 * @param begin
	 * @param end
	 * @return
	 */
	public static long lcm(long[] array, int begin, int end) {
		long out = 1;
		while (begin < end--) {
			long x = array[end];
			if (x == 0) {
				continue;
			} else if (x < 0) {
				x = -x;
			}
			if (x == 1) {
				continue;
			}
			long g = gcd(out, x);
			out = (out / g) * (x / g) * g;
		}
		return out;
	}
	/**
	 * skips 0.
	 * 
	 * @param array
	 * @param begin
	 * @param end
	 * @return
	 */
	public static long gcd(long[] array, int begin, int end) {
		long out = 0;
		while (begin < end--) {
			long x = array[end];
			if (x == 0) {
				continue;
			} else if (x < 0) {
				x = -x;
			}
			if (out < 1) {
				out = x;
				continue;
			}
			out = gcd(out, x);
		}
		return out;
	}

	public static String toString(double[] xs, String var) {
		return Euclid.toString(xs, 0, xs.length, var);
	}
	public static String toString(double[] xs, int begin, int end, String var) {
		StringBuilder buffer = new StringBuilder();
		try {
			if (!Euclid.toString(buffer, xs, begin, end, var)) {
				buffer.append("0");
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return buffer.toString();
	}
	public static boolean toString(Appendable writer, double[] xs, int begin,
			int end, String var) throws IOException {
		if (end < begin + 1) {
			return false;
		}
		boolean first = true;
		while (begin < end--) {
			double x = xs[end];
			if (x == 0) {
				continue;
			}
			boolean minus = x < 0;
			if (minus) {
				x = -x;
			}
			if (first) {
				first = false;
				if (minus) {
					writer.append("- ");
				}
				if (begin == end) {
					writer.append(Double.toString(x));
				} else {
					if (x != 1) {
						writer.append(Double.toString(x));
					}
					writer.append(var);
					if (begin + 1 < end) {
						writer.append('^');
						writer.append(Integer.toString(end - begin));
					}
				}
			} else {
				if (minus) {
					writer.append(" - ");
				} else {
					writer.append(" + ");
				}
				if (end == 0 || x != 1) {
					writer.append(Double.toString(x));
				}
				switch (end - begin) {
				case 0:
				break;
				case 1:
					writer.append(var);
				break;
				default:
					writer.append(var);
					writer.append('^');
					writer.append(Integer.toString(end - begin));
				break;
				}
			}
		}
		return true;
	}

	public static String toString(long[] xs0, long[] xs1, String var) {
		return Euclid.toString(xs0, xs1, 0, xs0.length, var);
	}
	public static String toString(long[] xs0, long[] xs1, int begin, int end,
			String var) {
		StringBuilder buffer = new StringBuilder();
		try {
			if (!Euclid.toString(buffer, xs0, xs1, begin, end, var)) {
				buffer.append("0");
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return buffer.toString();
	}
	public static boolean toString(Appendable writer, long[] xs0, long[] xs1,
			int begin, int end, String var) throws IOException {
		end = highest(xs0, begin, end);
		if (end < begin + 1) {
			return false;
		}
		boolean first = true;
		while (begin < end--) {
			long x0 = xs0[end];
			long x1 = xs1[end];
			if (x0 == 0) {
				continue;
			} else if (x0 == x1) {
				x0 = 1;
				x1 = 1;
			} else if (x0 == -x1) {
				x0 = -1;
				x1 = 1;
			} else if (x1 < 0) {
				x0 = -x0;
				x1 = -x1;
			}
			boolean minus = x0 < 0;
			if (minus) {
				x0 = -x0;
			}
			if (first) {
				first = false;
				if (minus) {
					writer.append("- ");
				}
				if (begin == end) {
					toString(writer, x0, x1);
				} else {
					if (x0 != x1) {
						toString(writer, x0, x1);
					}
					writer.append(var);
					if (begin + 1 < end) {
						writer.append('^');
						writer.append(Integer.toString(end));
					}
				}
			} else {
				if (minus) {
					writer.append(" - ");
				} else {
					writer.append(" + ");
				}
				if (end == 0 || x0 != x1) {
					toString(writer, x0, x1);
				}
				switch (end - begin) {
				case 0:
				break;
				case 1:
					writer.append(var);
				break;
				default:
					writer.append(var);
					writer.append('^');
					writer.append(Integer.toString(end - begin));
				break;
				}
			}
		}
		return true;
	}
	protected static void toString(Appendable writer, long x0, long x1)
			throws IOException {
		if (x0 % x1 == 0) {
			writer.append(Long.toString(x0 / x1));
		} else {
			writer.append('(');
			writer.append(Long.toString(x0));
			writer.append('/');
			writer.append(Long.toString(x1));
			writer.append(')');
		}
	}

	public static String toString(long[] xs, String var) {
		return Euclid.toString(xs, 0, xs.length, var);
	}
	public static String toString(long[] xs, int begin, int end, String var) {
		StringBuilder buffer = new StringBuilder();
		try {
			if (!Euclid.toString(buffer, xs, begin, end, var)) {
				buffer.append("0");
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return buffer.toString();
	}
	public static boolean toString(Appendable writer, long[] xs, int begin,
			int end, String var) throws IOException {
		end = highest(xs, begin, end);
		if (end < begin + 1) {
			return false;
		}
		boolean first = true;
		while (begin < end--) {
			long x = xs[end];
			if (x == 0) {
				continue;
			}
			boolean minus = x < 0;
			if (minus) {
				x = -x;
			}
			if (first) {
				first = false;
				if (minus) {
					writer.append("- ");
				}
				if (begin == end) {
					writer.append(Long.toString(x));
				} else {
					if (x != 1) {
						writer.append(Long.toString(x));
					}
					writer.append(var);
					if (begin + 1 < end) {
						writer.append('^');
						writer.append(Integer.toString(end - begin));
					}
				}
			} else {
				if (minus) {
					writer.append(" - ");
				} else {
					writer.append(" + ");
				}
				if (end == 0 || x != 1) {
					writer.append(Long.toString(x));
				}
				switch (end - begin) {
				case 0:
				break;
				case 1:
					writer.append(var);
				break;
				default:
					writer.append(var);
					writer.append('^');
					writer.append(Integer.toString(end - begin));
				break;
				}
			}
		}
		return true;
	}
}
