package parser.v3;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tiny.lang.ArrayHelper;
import tiny.lang.Debug;
import tiny.lang.NumberHelper;
import tiny.lang.StringHelper;
import base.TestBase;

public class IntWordTest extends TestBase {
	static interface Code {
		Code[] EMPTY_ARRAY = {};
	}

	static class CmCode extends CmIntWord<CmCode> implements Code {
		public static final CmCode[] EMPTY_ARRAY = {};

		long coef;

		CmCode() {
			this(1, ArrayHelper.EMPTY_INT_ARRAY);
		}
		CmCode(int[] word) {
			this(1, word);
		}
		CmCode(long coef, int[] word) {
			super(word);
			this.coef = coef;
		}
		@Override
		protected CmCode newThat(int[] word) {
			return this.newThat(this.coef, word);
		}
		protected CmCode newThat(long coef, int[] word) {
			return new CmCode(coef, word);
		}
		@Override
		public boolean isOne() {
			return super.isOne() && this.coef == 1;
		}
		public boolean isZero() {
			return this.coef == 0;
		}
		@Override
		public CmCode times(CmCode y) {
			if (y == null) {
				return null;
			} else if (this.isZero()) {
				return this;
			} else if (y.isZero()) {
				return y;
			} else if (this.isOne()) {
				return y;
			} else if (y.isOne()) {
				return this.that();
			}
			int[] word = IntWord.plus(this.word, y.word);
			return this.newThat(this.coef * y.coef, CmIntWord.trim(word));
		}
		@Override
		public CmCode times(int y) {
			if (this.isZero()) {
				return this;
			}
			int[] word = IntWord.increment(this.word, y);
			return this.newThat(this.coef, CmIntWord.trim(word));
		}
		@Override
		public Appendable toString(Appendable output) throws IOException {
			return this.toPolynomial(output, "x");
		}
		@Override
		public Appendable toPolynomial(Appendable output, String variable)
				throws IOException {
			return super.toPolynomial(this.toCoef(output), variable);
		}
		protected Appendable toCoef(Appendable output) throws IOException {
			if (this.coef == 1) {
			} else if (this.coef == -1) {
				output.append('-');
			} else {
				output.append(Long.toString(this.coef));
			}
			return output;
		}
		public Appendable toDerivativeTex(Appendable output, String name)
				throws IOException {
			int[] word = this.word;
			if (this.coef == 0) {
				return output.append("0");
			} else if (this.coef == 1) {
				if (isAll(word, 0)) {
					output.append("1");
				}
			} else if (this.coef == -1) {
				if (isAll(word, 0)) {
					output.append("-1");
				} else {
					output.append('-');
				}
			} else {
				output.append(Long.toString(this.coef));
			}
			for (int i = 0, n = word.length; i < n; ++i) {
				int k = word[i];
				if (k == 0) {
					continue;
				}
				switch (i) {
				case 0:
					output.append(name);
					if (k != 1) {
						output.append("^").append(Integer.toString(k));
					}
				break;
				case 1:
					output.append("(\\partial ").append(name).append(')');
					if (k != 1) {
						output.append("^").append(Integer.toString(k));
					}
				break;
				default:
					output.append("(\\partial^").append(Integer.toString(i)).append(' ')
							.append(name).append(')');
					if (k != 1) {
						output.append("^").append(Integer.toString(k));
					}
				break;
				}
			}
			return output;
		}
		private static boolean isAll(int[] word, int value) {
			for (int n = word.length; 0 < n--;) {
				if (word[n] != value) {
					return false;
				}
			}
			return true;
		}
	}

	static abstract class CodeSet {
		Code[][] primary;
		Code[][][] secondary;

		CodeSet get(int degree) {
			final Code dnk_0_0 = this.unit();
			Code[][] dns = new Code[degree + 1][];
			Code[][][] dnks = new Code[degree + 1][][];
			Map<Object, Code> codeMap = new HashMap<Object, Code>();
			Map<Object, Code> codeMap_r = new HashMap<Object, Code>();
			// List<Code> codeList = new ArrayList<Code>();
			dnks[0] = new Code[1][];
			dnks[0][0] = new Code[] { dnk_0_0 };
			this.times(codeMap, dnks[0][0], 0);
			dns[0] = codeMap.values().toArray(Code.EMPTY_ARRAY);
			for (int n = 0; n < degree; ++n) {
				Code[][] dnk = new Code[n + 2][];
				for (int k = 0; k <= n; ++k) {
					codeMap.clear();
					for (int r = k; r <= n; ++r) {
						Code[] ys = dnks[r][k];
						if (ys == null) {
							continue;
						}
						codeMap_r.clear();
						this.times(codeMap_r, dns[n - r], ys);
						Code[] codes = codeMap_r.values().toArray(Code.EMPTY_ARRAY);
						for (int i = codes.length; 0 < i--;) {
							this.plus(codeMap, codes[i]);
						}
					}
					dnk[k + 1] = codeMap.values().toArray(Code.EMPTY_ARRAY);
				}
				dnks[n + 1] = dnk;
				codeMap.clear();
				for (int k = 0; k <= n; ++k) {
					this.times(codeMap, dnk[k + 1], k + 1);
				}
				dns[n + 1] = codeMap.values().toArray(Code.EMPTY_ARRAY);
			}
			this.primary = dns;
			this.secondary = dnks;
			return this;
		}
		protected Collection<Code> times(Collection<Code> output, Code[] xs,
				Code[] ys) {
			for (int xn = xs.length; 0 < xn--;) {
				for (int yn = ys.length; 0 < yn--;) {
					Code code = this.times(xs[xn], ys[yn]);
					output.add(code);
				}
			}
			return output;
		}
		protected Map<Object, Code> times(Map<Object, Code> output, Code[] xs,
				Code[] ys) {
			for (int xn = xs.length; 0 < xn--;) {
				for (int yn = ys.length; 0 < yn--;) {
					Code code = this.times(xs[xn], ys[yn]);
					this.plus(output, code);
				}
			}
			return output;
		}
		protected Map<Object, Code> times(Map<Object, Code> output, Code[] xs, int y) {
			for (int xn = xs.length; 0 < xn--;) {
				Code code = this.times(xs[xn], y);
				this.plus(output, code);
			}
			return output;
		}
		private Map<Object, Code> plus(Map<Object, Code> output, Code code) {
			Object key = this.key(code);
			Code old = output.get(key);
			if (old != null) {
				code = this.coef(old, this.coef(old) + this.coef(code));
			}
			output.put(key, code);
			return output;
		}
		long sumOfCoef(Code[] codes) {
			long out = 0;
			for (int n = codes.length; 0 < n--;) {
				out += this.coef(codes[n]);
			}
			return out;
		}
		protected abstract Object key(Code code);
		protected abstract Code unit();
		protected abstract Code times(Code x, Code y);
		protected abstract Code times(Code x, int y);
		protected abstract long coef(Code x);
		protected abstract Code coef(Code x, long coef);
	}

	static class Lagrange {
		static CmCode[] derivs(int degree) {
			@SuppressWarnings("unchecked")
			List<CmCode>[] buffer = new List[2];
			buffer[0] = new ArrayList<CmCode>();
			buffer[1] = new ArrayList<CmCode>();
			CmCode code = new CmCode(new int[] { degree + 1 });
			buffer[0].add(code);
			while (0 < degree--) {
				buffer[1].clear();
				for (int n = buffer[0].size(); 0 < n--;) {
					Lagrange.derivs(buffer[1], buffer[0].get(n));
				}
				ArrayHelper.swap(buffer, 0, 1);
			}
			Map<Object, CmCode> map = new HashMap<Object, CmCode>();
			for (int n = buffer[0].size(); 0 < n--;) {
				code = buffer[0].get(n);
				CmCode x = map.get(code);
				if (x != null) {
					x.coef += code.coef;
				} else {
					map.put(code, code);
				}
			}
			return map.values().toArray(CmCode.EMPTY_ARRAY);
		}

		private static Collection<CmCode> derivs(Collection<CmCode> output,
				CmCode code) {
			int[] word = code.word;
			for (int n = word.length - 1; 0 < n--;) {
				int[] xs = word.clone();
				int k = xs[n];
				if (k == 0) {
					continue;
				}
				xs[n] -= 1;
				xs[n + 1] += 1;
				CmCode x = new CmCode(k * code.coef, xs);
				output.add(x);
			}
			int[] xs = new int[word.length + 1];
			IntWord.setAll(xs, 0, word);
			int k = xs[word.length - 1];
			xs[word.length - 1] -= 1;
			xs[word.length] += 1;
			CmCode x = new CmCode(k * code.coef, xs);
			output.add(x);
			return output;
		}

		public static void toDerivs(Code[] codes, long factorial) {
			for (int n = codes.length; 0 < n--;) {
				CmCode code = (CmCode) codes[n];
				long k = factor(code.word);
				code = new CmCode(code.coef * factorial / k, code.word);
				codes[n] = code;
			}
		}
		private static long factor(int[] word) {
			long out = 1;
			long m = 1;
			for (int k = 2, n = word.length; k < n; ++k) {
				m *= k;
				for (int p = word[k]; 0 < p--;) {
					out *= m;
				}
			}
			return out;
		}
	}

	public void testDigest_0() throws IOException {
		CodeSet codeSet = new CodeSet() {
			@Override
			protected Object key(Code code) {
				return code;
			}
			@Override
			protected Code unit() {
				return new CmCode();
			}
			@Override
			protected Code times(Code x, Code y) {
				CmCode xx = (CmCode) x;
				CmCode yy = (CmCode) y;
				return xx.times(yy);
			}
			@Override
			protected Code times(Code x, int y) {
				CmCode xx = (CmCode) x;
				return xx.times(y);
			}
			@Override
			protected long coef(Code x) {
				CmCode xx = (CmCode) x;
				return xx.coef;
			}
			@Override
			protected Code coef(Code x, long coef) {
				CmCode xx = (CmCode) x;
				xx.coef = coef;
				return xx;
			}
		};
		int degree = 10;
		codeSet.get(degree);
		if (true) {
			long catalan = 1;
			for (int deg = 0; deg <= degree; ++deg) {
				Code[] codes = codeSet.primary[deg];
				long sum = codeSet.sumOfCoef(codes);
				assertEquals("sum of coef.", catalan, sum);
				long value = value(codes, new long[] { 1, 2, 1 });
				catalan = catalan * 2 * (2 * deg + 1) / (deg + 2);
				assertEquals("case of catalan.", catalan, value);
			}
		}
		if (true) {
			dumpCodeSet(codeSet);
		}
	}

	private static long[] value(Code[][] codes, long[] values) {
		int n = codes.length;
		long[] out = new long[n];
		while (0 < n--) {
			out[n] = value(codes[n], values);
		}
		return out;
	}
	private static long value(Code[] codes, long[] values) {
		long out = 0;
		for (int n = codes.length; 0 < n--;) {
			out += value((CmCode) codes[n], values);
		}
		return out;
	}
	private static long value(CmCode code, long[] values) {
		int[] xs = code.word;
		if (values.length < xs.length) {
			return 0;
		}
		long out = 1;
		for (int xn = xs.length; 0 < xn--;) {
			long base = values[xn];
			int pow = xs[xn];
			if (base == 0) {
				if (pow == 0) {
					continue;
				}
				return 0;
			}
			out *= NumberHelper.power(base, pow);
		}
		return code.coef * out;
	}

	private static void dumpCodeSet(CodeSet codeSet) throws IOException {
		dumpPrimarySummary(codeSet);
		// dumpPrimaryWiki(codeSet);
		dumpPrimary(codeSet);
		// dumpShiftedCatalan (codeSet);
		// dumpLagrangeTex(codeSet);
		dumpLagrangeTex_1(codeSet);
	}

	private static void dumpLagrangeTex_1(CodeSet codeSet) throws IOException {
		Writer writer = new PrintWriter(System.out);
		Code[][] primary = codeSet.primary;
		int degree = primary.length - 1;
		if (10 < degree) {
			return;
		}
		long fac = 1;
		for (int deg = 0; deg <= degree; ++deg) {
			if (deg != 0) {
				fac *= deg;
			}
			Code[][] codeList = codeSet.secondary[deg];
			for (int k = 0; k <= deg; ++k) {
				Code[] codes = codeList[k];
				if (codes == null) {
					continue;
				}
				Lagrange.toDerivs(codes = codes.clone(), fac * (deg + 1));
				Arrays.sort(codes);
				writer.append((deg + 1) + "!\\hat{f}_xD_{" + deg + "," + k + "} &= ");
				for (int i = 0, n = codes.length; i < n; ++i) {
					CmCode code = (CmCode) codes[i];
					if (i != 0) {
						writer.append(" + ");
					}
					code.toDerivativeTex(writer, "f");
				}
				writer.append("　\\\\\n").flush();
			}
		}
	}

	@SuppressWarnings("unused")
	private static void dumpLagrangeTex(CodeSet codeSet) throws IOException {
		Writer writer = new PrintWriter(System.out);
		Code[][] primary = codeSet.primary;
		int degree = primary.length - 1;
		if (10 < degree) {
			return;
		}
		long fac = 1;
		for (int deg = 0; deg <= degree; ++deg) {
			if (deg != 0) {
				fac *= deg;
			}
			Code[] codes = codeSet.primary[deg].clone();
			Lagrange.toDerivs(codes, fac * (deg + 1));
			Arrays.sort(codes);
			writer.append((deg + 1) + "!\\hat{f}_xD_" + deg + " &= ");
			for (int i = 0, n = codes.length; i < n; ++i) {
				CmCode code = (CmCode) codes[i];
				if (i != 0) {
					writer.append(" + ");
				}
				code.toDerivativeTex(writer, "f");
			}
			writer.append("　\\\\\n").flush();

			CmCode[] xs = Lagrange.derivs(deg);
			Arrays.sort(xs);
			writer.append("\\partial^" + deg + "f^" + (deg + 1) + " &= ");
			for (int i = 0, n = codes.length; i < n; ++i) {
				CmCode code = xs[i];
				if (i != 0) {
					writer.append(" + ");
				}
				code.toDerivativeTex(writer, "f");
			}
			writer.append("　\\\\\n").flush();
		}
	}

	@SuppressWarnings("unused")
	private static void dumpShiftedCatalan(CodeSet codeSet) throws IOException {
		Code[][] primary = codeSet.primary;
		long[] derivs = new long[primary.length];
		Arrays.fill(derivs, 1);
		long[] value = value(primary, derivs);
		Writer writer = new PrintWriter(System.out);
		StringHelper.join(writer, value, ", ");
		writer.append("\n").flush();
	}
	private static void dumpPrimary(CodeSet codeSet) {
		Code[][] primary = codeSet.primary;
		int degree = primary.length - 1;
		if (10 < degree) {
			return;
		}
		for (int deg = 0; deg <= degree; ++deg) {
			Code[] codes = codeSet.primary[deg];
			Debug.log().debug("D_" + deg + " = " + StringHelper.join(codes, " + "));
		}
	}

	private static void dumpPrimarySummary(CodeSet codeSet) throws IOException {
		Code[][] primary = codeSet.primary;
		int degree = primary.length - 1;
		for (int deg = 0; deg <= degree; ++deg) {
			Code[] codes = codeSet.primary[deg];
			long sum = codeSet.sumOfCoef(codes);
			Debug.log().debug("#codes=" + codes.length + ", #sum=" + sum);
		}
	}
	@SuppressWarnings("unused")
	private static void dumpPrimaryWiki(CodeSet codeSet) throws IOException {
		Code[][] primary = codeSet.primary;
		int degree = primary.length - 1;
		Writer writer = new PrintWriter(System.out);
		writer.append("| ");
		for (int deg = 0; deg <= degree; ++deg) {
			if (deg != 0) {
				writer.append(" | ");
			}
			writer.append(Long.toString(deg));
		}
		writer.append("|\n").flush();
		writer.append("|-");
		for (int deg = 0; deg <= degree; ++deg) {
			if (deg != 0) {
				writer.append("-|-");
			}
			writer.append("-");
		}
		writer.append("|\n").flush();
		writer.append("| ");
		for (int deg = 0; deg <= degree; ++deg) {
			if (deg != 0) {
				writer.append(" | ");
			}
			Code[] codes = primary[deg];
			writer.append(Long.toString(codes.length));
		}
		writer.append("|\n").flush();
	}
}
