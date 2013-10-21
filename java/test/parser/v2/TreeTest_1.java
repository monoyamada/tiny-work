package parser.v2;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import parser.v2.TreeTest_1.AbelianCode;

import junit.framework.Assert;

import tiny.lang.ArrayHelper;
import tiny.lang.Debug;
import tiny.lang.Messages;
import tiny.lang.NumberHelper;
import tiny.lang.StringHelper;
import tiny.primitive.IntArrayList;

public class TreeTest_1 extends BaseTest {
	static class TreeCode {
		static TreeCode unit() {
			TreeCode unit = TreeCode.newByDegree(0);
			return unit;
		}
		static TreeCode newByDegree(int n) {
			if (n < 0) {
				String msg = Messages.getUnexpectedValue("degree", "ge than 0",
						Integer.toString(n));
				throw new IllegalArgumentException(msg);
			}
			return new TreeCode(new int[n + 1]);
		}

		int q;
		final int[] ns;

		TreeCode(int[] ns) {
			this.ns = ns;
		}
		public String toString() {
			StringBuilder buffer = buffer();
			try {
				this.toString(buffer);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			return buffer.toString();
		}
		protected Appendable toString(Appendable output) throws IOException {
			return StringHelper.join(output.append(Integer.toString(q)).append('['),
					this.ns, ", ").append(']');
		}
		Collection<TreeCode> next(Collection<TreeCode> output) {
			int q = this.q;
			int[] ns = this.ns;
			int dq = 0;
			for (int i = 0, k = ns.length; i < k; ++i) {
				TreeCode next = TreeCode.newByDegree(k);
				System.arraycopy(ns, 0, next.ns, 0, i);
				next.ns[i] = 0;
				next.ns[i + 1] = ns[i] + 1;
				if (i + 1 < k) {
					System.arraycopy(ns, i + 1, next.ns, i + 2, k - i - 1);
				}
				next.q = q + dq;
				dq += ns[i];
				output.add(next);
			}
			return output;
		}
		int[] wordDegree(int[] output) {
			Arrays.fill(output, 0);
			int[] ns = this.ns;
			for (int i = 0, n = ns.length; i < n; ++i) {
				++output[ns[i]];
			}
			return output;
		}
	}

	static class AbelianCode {
		public static final AbelianCode[] EMPTY_ARRAY = {};
		static private final ThreadLocal<int[][]> INT_ARRAYS = new ThreadLocal<int[][]>() {
			@Override
			protected int[][] initialValue() {
				int[][] xs = new int[2][];
				int n = 8;
				xs[0] = new int[n];
				xs[1] = new int[n];
				return xs;
			}
		};

		static protected int[][] intArrays() {
			return INT_ARRAYS.get();
		}

		final int[] qs;
		final int[] ns;

		AbelianCode(int[] qs, int[] ns) {
			this.qs = qs;
			this.ns = ns;
		}
		public String toString() {
			StringBuilder buffer = buffer();
			try {
				this.toString(buffer);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			return buffer.toString();
		}
		protected Appendable toString(Appendable output) throws IOException {
			StringHelper.join(output.append('['), this.qs, ", ").append(']');
			return StringHelper.join(output.append('['), this.ns, ", ").append(']');
		}
		int[] numerator() {
			int[] qs = this.qs;
			int[] ns = this.ns;
			int[][] xs = intArrays();
			int n = qs.length;
			xs[0] = ArrayHelper.ensureSize(xs[0], n);
			System.arraycopy(qs, 0, xs[0], 0, n);
			for (int i = 2, nn = ns.length; i < nn; ++i) {
				int d = ns[i];
				while (0 < d--) {
					int m = n + i;
					xs[1] = ArrayHelper.ensureSize(xs[1], m);
					Arrays.fill(xs[1], 0, m, 0);
					for (int j = 0; j < i; ++j) {
						for (int k = 0; k < n; ++k) {
							xs[1][k + j] += xs[0][k];
						}
					}
					int[] ys = xs[0];
					xs[0] = xs[1];
					xs[1] = ys;
					n = m;
				}
			}
			for(int nn=n;0<nn--;){
				if(xs[0][nn]!=0){
					break;
				}
				n = nn;
			}
			return ArrayHelper.sub(xs[0], 0, n);
		}
		long denominator() {
			long x = 1;
			int[] ns = this.ns;
			for (int i = 2, n = ns.length; i < n; ++i) {
				if (ns[i] != 0) {
					x *= NumberHelper.power(factorial(i), ns[i]);
				}
			}
			return x;
		}
		private long factorial(int n) {
			long x = n;
			while (2 < n--) {
				x *= n;
			}
			return x;
		}
	}

	static class Polynomial {
		int degree(int[] ks) {
			int n = ks.length;
			while (0 < n--) {
				if (ks[n] != 0) {
					return n;
				}
			}
			return 0;
		}
		int[][] derivatives(int[] ks) {
			int[][] out = new int[ks.length][];
			out[0] = ks.clone();
			for (int i = 1; i < ks.length; ++i) {
				int[] x = out[i - 1];
				int[] y = new int[x.length - 1];
				for (int j = 0; j < y.length; ++j) {
					y[j] = x[j + 1] * (j + 1);
				}
				out[i] = y;
			}
			return out;
		}

		final int[][] ns;

		Polynomial(int[] ks) {
			this.ns = derivatives(ks);
		}
		int[] get(int d) {
			if (d < 0) {
				String msg = Messages.getUnexpectedValue("derivative", "ge than 0",
						Integer.toString(d));
				throw new IllegalArgumentException(msg);
			} else if (this.ns.length <= d) {
				return ArrayHelper.EMPTY_INT_ARRAY;
			}
			return this.ns[d];
		}
		long value(int d, int value) {
			if (d < 0) {
				String msg = Messages.getUnexpectedValue("derivative", "ge than 0",
						Integer.toString(d));
				throw new IllegalArgumentException(msg);
			} else if (this.ns.length <= d) {
				return 0;
			}
			int[] xs = this.ns[d];
			long out = xs[0];
			int k = value;
			for (int i = 1, n = xs.length; i < n; ++i) {
				out += xs[i] * k;
				k *= value;
			}
			return out;
		}
	}

	public void testNext() {
		List<TreeCode> xs = new ArrayList<TreeCode>(128);
		List<TreeCode> ys = new ArrayList<TreeCode>(128);
		xs.add(TreeCode.unit());
		Debug.log().debug(StringHelper.join(xs, " + "));
		for (int deg = 1; deg < 5; ++deg) {
			for (int i = 0, n = xs.size(); i < n; ++i) {
				xs.get(i).next(ys);
			}
			List<TreeCode> zs = ys;
			ys = xs;
			xs = zs;
			ys.clear();
			Debug.log().debug(StringHelper.join(xs, " + "));
		}
	}
	public void testAbelianize() throws IOException {
		Writer writer = new PrintWriter(System.out);
		List<TreeCode> xs = new ArrayList<TreeCode>(128);
		List<TreeCode> ys = new ArrayList<TreeCode>(128);
		xs.add(TreeCode.unit());
		writeTex(writer, abelianize(xs, 0)).append('\n').flush();
		for (int deg = 1; deg < 6; ++deg) {
			for (int i = 0, n = xs.size(); i < n; ++i) {
				xs.get(i).next(ys);
			}
			List<TreeCode> zs = ys;
			ys = xs;
			xs = zs;
			ys.clear();
			writeTex(writer, abelianize(xs, deg)).append('\n').flush();
		}
	}
	public void testDyck() throws IOException {
		Writer writer = new PrintWriter(System.out);
		List<TreeCode> xs = new ArrayList<TreeCode>(32);
		List<TreeCode> ys = new ArrayList<TreeCode>(32);
		Polynomial rhs = new Polynomial(new int[] { 0, 0, 1 });
		int value = 1;
		xs.add(TreeCode.unit());
		writeDyckTex(writer, abelianize(xs, 0), rhs, value).append('\n').flush();
		for (int deg = 1; deg < 8; ++deg) {
			for (int i = 0, n = xs.size(); i < n; ++i) {
				xs.get(i).next(ys);
			}
			List<TreeCode> zs = ys;
			ys = xs;
			xs = zs;
			ys.clear();
			writeDyckTex(writer, abelianize(xs, deg), rhs, value).append('\n')
					.flush();
		}
	}

	public void testDyck_2() throws IOException {
		final int DEG = 10;
		Writer writer = new PrintWriter(System.out);
		List<TreeCode> xs = new ArrayList<TreeCode>(32);
		List<TreeCode> ys = new ArrayList<TreeCode>(32);
		Polynomial rhs = new Polynomial(new int[] { 0, 0, 1 });
		int value = 1;
		xs.add(TreeCode.unit());
		for (int deg = 1; deg < DEG; ++deg) {
			for (int i = 0, n = xs.size(); i < n; ++i) {
				xs.get(i).next(ys);
			}
			List<TreeCode> zs = ys;
			ys = xs;
			xs = zs;
			ys.clear();
	
		}
		int[] qs = dyckQs(abelianize(xs, DEG), rhs, value);
		StringHelper.join(writer.append("c("), qs, ",").append(")\n");
		writer.flush();
	}

	public void testPolynomial() throws IOException {
		Polynomial x = new Polynomial(new int[] { 0, 0, 0, 1 });
		for (int i = 0; i < x.ns.length; ++i) {
			Debug.log().debug(StringHelper.join(x.get(i)));
		}
		Assert.assertEquals("rank-0", 1 * (2 * 2 * 2), x.value(0, 2));
		Assert.assertEquals("rank-1", 3 * (2 * 2), x.value(1, 2));
		Assert.assertEquals("rank-2", 3 * 2 * (2), x.value(2, 2));
		Assert.assertEquals("rank-3", 3 * 2 * (1), x.value(3, 2));
	}

	private static Writer writeTex(Writer output, AbelianCode[] codes)
			throws IOException {
		for (int i = 0, n = codes.length; i < n; ++i) {
			if (i != 0) {
				output.write(" + ");
			}
			AbelianCode x = codes[i];
			writeQs(output, x.qs);
			writeNs(output, x.ns);
		}
		return output;
	}

	private Writer writeDyckTex(Writer output, AbelianCode[] codes,
			Polynomial rhs, int value) throws IOException {
		int[] xs = dyckQs(codes, rhs, value);
		writeQs(output, xs, false, false);
		return output;
	}

	private int[] dyckQs(AbelianCode[] codes, Polynomial rhs, int value) {
		IntArrayList xs = new IntArrayList(32);
		OUTER: for (int i = 0, n = codes.length; i < n; ++i) {
			AbelianCode x = codes[i];
			long up = 1;
			for (int ix = 0, nx = x.ns.length; ix < nx; ++ix) {
				int k = x.ns[ix];
				if (k == 0) {
					continue;
				}
				up *= NumberHelper.power(rhs.value(ix, value), k);
				if (up == 0) {
					continue OUTER;
				}
			}
			int[] qs = x.numerator();
			long down = x.denominator();
			long gcd = NumberHelper.gcd(up, down);
			up /= gcd;
			down /= gcd;
			if (down != 1) {
				String msg = Messages.getUnexpectedValue("denomiator must be one", 1,
						down);
				throw new IllegalArgumentException(msg);
			}
			scales((int) up, qs);
			while (xs.size() < qs.length) {
				xs.addLast(0);
			}
			for (int qi = qs.length; 0 < qi--;) {
				xs.getArray()[qi] += qs[qi];
			}
		}
		return xs.toArray();
	}
	private int[] scales(int k, int[] xs) {
		for (int n = xs.length; 0 < n--;) {
			xs[n] *= k;
		}
		return xs;
	}
	private static Writer writeNs(Writer output, int[] ns) throws IOException {
		for (int i = 0, n = ns.length; i < n; ++i) {
			int d = ns[i];
			if (d == 0) {
				continue;
			}
			output.append("\\word{").append(Integer.toString(i)).append('}');
			if (d != 1) {
				output.append("^").append(Integer.toString(d));
			}
		}
		return output;
	}
	private static Writer writeQs(Writer output, int[] qs) throws IOException {
		return writeQs(output, qs, true, true);
	}
	private static Writer writeQs(Writer output, int[] qs, boolean bracket,
			boolean skipOne) throws IOException {
		// int[] qs = code.qs;
		if (isQconstant(qs)) {
			int k = qs[0];
			if (k != 1 || !skipOne) {
				output.append(Integer.toString(k));
			}
		} else if (bracket) {
			writeQ(output.append('('), qs).append(')');
		} else {
			writeQ(output, qs);
		}
		return output;
	}
	private static boolean isQconstant(int[] qs) {
		if (qs.length < 1) {
			return false;
		}
		for (int i = 1, n = qs.length; i < n; ++i) {
			if (qs[i] != 0) {
				return false;
			}
		}
		return true;
	}
	private static Writer writeQ(Writer output, int[] qs) throws IOException {
		for (int i = 0, n = qs.length; i < n; ++i) {
			if (i == 0) {
				output.append(Integer.toString(qs[i]));
			} else if (qs[i] != 0) {
				output.append(" + ");
				if (qs[i] != 1) {
					output.append(Integer.toString(qs[i]));
				}
				output.append("q");
				if (i != 1) {
					output.append('^').append(Integer.toString(i));
				}
			}
		}
		return output;
	}
	private static AbelianCode[] abelianize(List<TreeCode> xs, int deg) {
		int maxq = 0;
		for (int i = 0, n = xs.size(); i < n; ++i) {
			maxq = Math.max(xs.get(i).q, maxq);
		}
		IntArrayKey key = new IntArrayKey(new int[deg + 1]);
		AbelianCode code = null;
		Map<IntArrayKey, AbelianCode> map = new TreeMap<IntArrayKey, AbelianCode>();
		for (int i = 0, n = xs.size(); i < n; ++i) {
			TreeCode x = xs.get(i);
			x.wordDegree(key.array);
			code = map.get(key);
			if (code == null) {
				int[] array = key.array.clone();
				code = new AbelianCode(new int[maxq + 1], array);
				code.qs[x.q] = 1;
				map.put(new IntArrayKey(array), code);
			} else {
				code.qs[x.q] += 1;
			}
		}
		return map.values().toArray(AbelianCode.EMPTY_ARRAY);
	}
}
