package group;

import java.io.IOException;
import java.util.Arrays;

import junit.framework.TestCase;
import tiny.lang.Debug;
import tiny.primitive.Euclid;
import tiny.primitive.Euclid.GcdLong;

public class EuclidTest extends TestCase {
	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
		super.setUp();
	}
	static String divideToString(double[] xs, double[] ys, double[] rq, int sep) {
		StringBuilder buffer = new StringBuilder();
		try {
			if (!Euclid.toString(buffer, xs, 0, xs.length, "x")) {
				buffer.append("0");
			}
			buffer.append(" = (");
			if (!Euclid.toString(buffer, rq, sep, rq.length, "x")) {
				buffer.append("0");
			}
			buffer.append(")(");
			if (!Euclid.toString(buffer, ys, 0, ys.length, "x")) {
				buffer.append("0");
			}
			buffer.append(") + (");
			if (!Euclid.toString(buffer, rq, 0, sep, "x")) {
				buffer.append("0");
			}
			buffer.append(")");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return buffer.toString();
	}
	static String divideToString(long[] xs0, long[] xs1, long[] ys0, long[] ys1,
			long[] rq0, long[] rq1, int sep) {
		StringBuilder buffer = new StringBuilder();
		try {
			if (!Euclid.toString(buffer, xs0, xs1, 0, xs0.length, "x")) {
				buffer.append("0");
			}
			buffer.append(" = (");
			if (!Euclid.toString(buffer, rq0, rq1, sep, rq0.length, "x")) {
				buffer.append("0");
			}
			buffer.append(")(");
			if (!Euclid.toString(buffer, ys0, ys1, 0, ys0.length, "x")) {
				buffer.append("0");
			}
			buffer.append(") + (");
			if (!Euclid.toString(buffer, rq0, rq1, 0, sep, "x")) {
				buffer.append("0");
			}
			buffer.append(")");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return buffer.toString();
	}
	static String divideToString(long[] xs, long[] ys, long[] rq, int sep) {
		StringBuilder buffer = new StringBuilder();
		try {
			if (!Euclid.toString(buffer, xs, 0, xs.length, "x")) {
				buffer.append("0");
			}
			buffer.append(" = (");
			if (!Euclid.toString(buffer, rq, sep, rq.length, "x")) {
				buffer.append("0");
			}
			buffer.append(")(");
			if (!Euclid.toString(buffer, ys, 0, ys.length, "x")) {
				buffer.append("0");
			}
			buffer.append(") + (");
			if (!Euclid.toString(buffer, rq, 0, sep, "x")) {
				buffer.append("0");
			}
			buffer.append(")");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return buffer.toString();
	}
	static String gcdToString(double[] xs, double[] ys, double[] gcd, int ngcd) {
		StringBuilder buffer = new StringBuilder();
		try {
			buffer.append("(");
			if (!Euclid.toString(buffer, xs, 0, xs.length, "x")) {
				buffer.append("0");
			}
			buffer.append(") & (");
			if (!Euclid.toString(buffer, ys, 0, ys.length, "x")) {
				buffer.append("0");
			}
			buffer.append(") = ");
			if (!Euclid.toString(buffer, gcd, 0, ngcd, "x")) {
				buffer.append("0");
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return buffer.toString();
	}
	static String gcdToString(long[] xs0, long[] xs1, long[] ys0, long[] ys1,
			long[] gcd0, long[] gcd1, int ngcd) {
		StringBuilder buffer = new StringBuilder();
		try {
			buffer.append("(");
			if (!Euclid.toString(buffer, xs0, xs1, 0, xs0.length, "x")) {
				buffer.append("0");
			}
			buffer.append(") & (");
			if (!Euclid.toString(buffer, ys0, ys1, 0, ys0.length, "x")) {
				buffer.append("0");
			}
			buffer.append(") = ");
			if (!Euclid.toString(buffer, gcd0, gcd1, 0, ngcd, "x")) {
				buffer.append("0");
			}
			buffer.append(" = ");
			long[] g0 = gcd0.clone();
			long[] g1 = gcd1.clone();
			long[] scale = new long[2];
			Euclid.factor(scale, g0, g1, 0, ngcd);
			buffer.append("(");
			buffer.append(Long.toString(scale[0]));
			buffer.append('/');
			buffer.append(Long.toString(scale[1]));
			buffer.append(")(");
			if (!Euclid.toString(buffer, g0, g1, 0, ngcd, "x")) {
				buffer.append("0");
			}
			buffer.append(")");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return buffer.toString();
	}
	static String gcdToString(long[] xs, long[] ys, long[] gcd, int ngcd) {
		StringBuilder buffer = new StringBuilder();
		try {
			buffer.append("(");
			if (!Euclid.toString(buffer, xs, 0, xs.length, "x")) {
				buffer.append("0");
			}
			buffer.append(") & (");
			if (!Euclid.toString(buffer, ys, 0, ys.length, "x")) {
				buffer.append("0");
			}
			buffer.append(") = ");
			if (!Euclid.toString(buffer, gcd, 0, ngcd, "x")) {
				buffer.append("0");
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return buffer.toString();
	}

	static class X extends Euclid {
		public static int divides(double[] xs, int nx, double[] ys, int ny) {
			return Euclid.divides(xs, nx, ys, ny, Euclid.ZERO_TOLERANCE);
		}
		public static GcdDouble gcd(double[] xs, int nx, double[] ys, int ny) {
			return Euclid.gcd(xs, nx, ys, ny, Euclid.ZERO_TOLERANCE);
		}
	}

	public void testMisc() {
		{
			long[] xs = { 0, 1, 2 * 3, 0, 1, 3 * 3, 0, 1 };
			assertEquals(2 * 3 * 3, X.lcm(xs, 0, xs.length));
			assertEquals(1, X.gcd(xs, 0, xs.length));
		}
		{
			long[] xs = { 0, 0, 2 * 3, 0, 0, 3 * 3, 0, 0 };
			assertEquals(2 * 3 * 3, X.lcm(xs, 0, xs.length));
			assertEquals(3, X.gcd(xs, 0, xs.length));
		}
	}

	public void testDouble() {
		// f = x^4 − 4x^3 + 4x^2 − 3x + 14 = (x^2 − 5x + 7)h
		// g = x^4 + 8x^3 + 12x^2 + 17x + 6 = (x^2 + 7x + 3)h
		// h = x^2 + x + 2
		{
			double[] xs = { 3, 4, 5 };
			double[] ys = { 1, 2 };
			double[] rq = xs.clone();
			int sep = X.divides(rq, rq.length, ys, ys.length);
			Debug.log().debug(divideToString(xs, ys, rq, sep));
		}
		{
			double[] xs = { 3, 4, 5 };
			double[] ys = { 1, 2 };
			double[] rq = ys.clone();
			int sep = X.divides(rq, rq.length, xs, xs.length);
			Debug.log().debug(divideToString(ys, xs, rq, sep));
		}
		{
			double[] xs = { 14, -3, 4, -4, 1 };
			double[] ys = { 2, 1, 1 };
			double[] rq = xs.clone();
			int sep = X.divides(rq, rq.length, ys, ys.length);
			Debug.log().debug(divideToString(xs, ys, rq, sep));
		}
		{
			double[] xs = { 14, -3, 4, -4, 1 };
			double[] ys = { 6, 17, 12, 8, 1 };
			double[] as = xs.clone();
			double[] bs = ys.clone();
			X.GcdDouble g = X.gcd(as, as.length, bs, bs.length);
			Debug.log().debug(gcdToString(xs, ys, g.g, g.size));
		}
	}
	public void testLong() {
		// f = x^4 − 4x^3 + 4x^2 − 3x + 14 = (x^2 − 5x + 7)h
		// g = x^4 + 8x^3 + 12x^2 + 17x + 6 = (x^2 + 7x + 3)h
		// h = x^2 + x + 2
		try {
			long[] xs = { 3, 4, 5 };
			long[] ys = { 1, 2 };
			long[] rq = xs.clone();
			int sep = X.divides(rq, rq.length, ys, ys.length);
			Debug.log().debug(divideToString(xs, ys, rq, sep));
		} catch (Exception ex) {
			Debug.log().debug(ex);
		}
		{
			long[] xs = { 3, 4, 5 };
			long[] ys = { 1, 2 };
			long[] rq = ys.clone();
			int sep = X.divides(rq, rq.length, xs, xs.length);
			Debug.log().debug(divideToString(ys, xs, rq, sep));
		}
		{
			long[] xs = { 14, -3, 4, -4, 1 };
			long[] ys = { 2, 1, 1 };
			long[] rq = xs.clone();
			int sep = X.divides(rq, rq.length, ys, ys.length);
			Debug.log().debug(divideToString(xs, ys, rq, sep));
		}
		try {
			long[] xs = { 14, -3, 4, -4, 1 };
			long[] ys = { 6, 17, 12, 8, 1 };
			long[] as = xs.clone();
			long[] bs = ys.clone();
			GcdLong g = X.gcd(as, as.length, bs, bs.length);
			Debug.log().debug(gcdToString(xs, ys, g.g, g.size));
		} catch (Exception ex) {
			Debug.log().debug(ex);
		}
	}
	public void testFraction() {
		// f = x^4 − 4x^3 + 4x^2 − 3x + 14 = (x^2 − 5x + 7)h
		// g = x^4 + 8x^3 + 12x^2 + 17x + 6 = (x^2 + 7x + 3)h
		// h = x^2 + x + 2
		try {
			long[] xs0 = { 3, 4, 5 };
			long[] xs1 = one(xs0);
			long[] ys0 = { 1, 2 };
			long[] ys1 = one(ys0);
			long[] rq0 = xs0.clone();
			long[] rq1 = xs1.clone();
			int sep = X.divides(rq0, rq1, rq0.length, ys0, ys1, ys0.length);
			Debug.log().debug(divideToString(xs0, xs1, ys0, ys1, rq0, rq1, sep));
		} catch (Exception ex) {
			Debug.log().debug(ex);
		}
		{
			long[] xs0 = { 3, 4, 5 };
			long[] xs1 = one(xs0);
			long[] ys0 = { 1, 2 };
			long[] ys1 = one(ys0);
			long[] rq0 = ys0.clone();
			long[] rq1 = ys1.clone();
			int sep = X.divides(rq0, rq1, rq0.length, xs0, xs1, xs0.length);
			Debug.log().debug(divideToString(ys0, ys1, xs0, xs1, rq0, rq1, sep));
		}
		{
			long[] xs0 = { 14, -3, 4, -4, 1 };
			long[] xs1 = one(xs0);
			long[] ys0 = { 2, 1, 1 };
			long[] ys1 = one(ys0);
			long[] rq0 = xs0.clone();
			long[] rq1 = xs1.clone();
			int sep = X.divides(rq0, rq1, rq0.length, ys0, ys1, ys0.length);
			Debug.log().debug(divideToString(xs0, xs1, ys0, ys1, rq0, rq1, sep));
		}
		try {
			long[] xs0 = { 14, -3, 4, -4, 1 };
			long[] xs1 = one(xs0);
			long[] ys0 = { 6, 17, 12, 8, 1 };
			long[] ys1 = one(ys0);
			long[] as0 = xs0.clone();
			long[] as1 = xs1.clone();
			long[] bs0 = ys0.clone();
			long[] bs1 = ys1.clone();
			X.GcdFraction g = X.gcd(as0, as1, as0.length, bs0, bs1, bs0.length);
			Debug.log().debug(gcdToString(xs0, xs1, ys0, ys1, g.g0, g.g1, g.size));
		} catch (Exception ex) {
			Debug.log().debug(ex);
		}
	}
	private static long[] one(long[] xs) {
		long[] ys = new long[xs.length];
		Arrays.fill(ys, 1);
		return ys;
	}
}
