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
import tiny.lang.StringHelper;
import base.TestBase;

public class CatalanTest extends TestBase {
//	static class LongTerm extends IntWord {
//		public static final LongTerm[] EMPTY_ARRAY = {};
//		private static final LongTerm ZERO = new LongTerm(0,
//				ArrayHelper.EMPTY_INT_ARRAY);
//
//		public static LongTerm term(int... word) {
//			return new LongTerm(1, word);
//		}
//
//		long coef;
//
//		LongTerm() {
//			this(1, ArrayHelper.EMPTY_INT_ARRAY);
//		}
//		LongTerm(int[] word) {
//			this(1, word);
//		}
//		LongTerm(long coef, int[] word) {
//			super(word);
//			this.coef = coef;
//		}
//		public boolean isZero() {
//			return this.coef == 0;
//		}
//		public LongTerm times(LongTerm y) {
//			if (this.isZero()) {
//				return this;
//			} else if (y == null || y.isZero()) {
//				return y;
//			}
//			int[] xs = this.word;
//			int[] ys = y.word;
//			if (xs.length < 1) {
//				return y.scale(this.coef);
//			} else if (ys.length < 1) {
//				return this.scale(y.coef);
//			}
//			int[] word = IntWord.times(xs, ys);
//			return new LongTerm(this.coef * y.coef, word);
//		}
//		public LongTerm times(int a) {
//			if (this.isZero()) {
//				return this;
//			}
//			int[] word = IntWord.times(this.word, a);
//			return new LongTerm(this.coef, word);
//		}
//		public LongTerm scale(long coef) {
//			if (this.coef == 0 || coef == 1) {
//				return this;
//			} else if (coef == 0) {
//				return this.zero();
//			}
//			LongTerm x = (LongTerm) this.clone();
//			x.coef *= coef;
//			return x;
//		}
//		public LongTerm zero() {
//			return LongTerm.ZERO;
//		}
//		public LongTerm[] singleton() {
//			return new LongTerm[] { this };
//		}
//		@Override
//		public Appendable toString(Appendable output) throws IOException {
//			if (this.coef == 0) {
//				return output.append("0");
//			} else if (this.coef == -1) {
//				output.append("-");
//			} else if (this.coef != 1) {
//				output.append(Long.toString(this.coef));
//			}
//			return super.toString(output);
//		}
//		public Appendable toTex(Appendable output) throws IOException {
//			if (this.coef == 0) {
//				return output.append("0");
//			} else if (this.coef == -1) {
//				output.append("-");
//			} else if (this.coef != 1) {
//				output.append(Long.toString(this.coef));
//			}
//			return super.toTex(output);
//		}
//		public static long sum(LongTerm[] xs) {
//			long sum = 0;
//			for (int n = xs.length; 0 < n--;) {
//				sum += xs[n].coef;
//			}
//			return sum;
//		}
//	}
//
//	static class QsNumber extends LongTerm {
//		private static final QsNumber ZERO = new QsNumber(0,
//				ArrayHelper.EMPTY_INT_ARRAY);;
//
//		static QsNumber binom(int n, int k) {
//			if (n < 0 || k < 0 || n < k) {
//				String msg = "must be 0 <= k <= n";
//				throw new IllegalArgumentException(msg);
//			} else if (k == 0 || n == k) {
//				return QsNumber.one();
//			}
//			QsNumber q = QsNumber.factorial(n);
//			int[] word = q.word;
//			int d = n - k - k;
//			if (d < 0) {
//				k = n - k;
//				d = -d;
//			}
//			for (int i = 2; i <= k; ++i) {
//				word[i - 2] -= 2;
//			}
//			for (int i = 1; i <= d; ++i) {
//				word[k + i - 2] -= 1;
//			}
//			return q;
//		}
//		static QsNumber factorial(int n) {
//			if (n == 0) {
//				n = 1;
//			}
//			QsNumber q = QsNumber.number(n);
//			if (q.word.length < 1) {
//				return q;
//			}
//			Arrays.fill(q.word, 0, q.word.length, 1);
//			return q;
//		}
//		static QsNumber number(int n) {
//			if (n < 0) {
//				String msg = "number must be positive";
//				throw new IllegalArgumentException(msg);
//			} else if (n == 0) {
//				return QsNumber.ZERO;
//			} else if (n == 1) {
//				return QsNumber.one();
//			}
//			int[] word = new int[n - 1];
//			word[n - 2] = 1;
//			return new QsNumber(word);
//		}
//		static QsNumber one() {
//			return new QsNumber(ArrayHelper.EMPTY_INT_ARRAY);
//		}
//
//		QsNumber(int[] word) {
//			this(1, word);
//		}
//		QsNumber(long coef, int[] word) {
//			super(coef, word);
//		}
//		public QsNumber times(QsNumber y) {
//			if (this.isZero()) {
//				return this;
//			} else if (y == null || y.isZero()) {
//				return y;
//			}
//			int[] xs = this.word;
//			int[] ys = y.word;
//			if (xs.length < 1) {
//				return y.scale(this.coef);
//			} else if (ys.length < 1) {
//				return this.scale(y.coef);
//			}
//			int[] word = IntWord.plus(xs, ys);
//			return new QsNumber(this.coef * y.coef, word);
//		}
//		@Override
//		public QsNumber scale(long coef) {
//			return (QsNumber) super.scale(coef);
//		}
//		@Override
//		public QsNumber zero() {
//			return QsNumber.ZERO;
//		}
//		public Appendable toString(Appendable output) throws IOException {
//			int[] xs = this.word;
//			if (xs.length < 1) {
//				return output.append(Long.toString(this.coef));
//			}
//			if (this.coef == 0) {
//				return output.append("0");
//			} else if (this.coef == -1) {
//				output.append("-");
//			} else if (this.coef != 1) {
//				output.append(Long.toString(this.coef));
//			}
//			for (int xn = xs.length; 0 < xn--;) {
//				int x = xs[xn];
//				if (x == 0) {
//					continue;
//				}
//				output.append('[').append(Integer.toString(xn + 2)).append(']');
//				if (x != 1) {
//					output.append('^').append(Integer.toString(x));
//				}
//			}
//			return output;
//		}
//	}
//
//	static class QsTerm extends IntWord {
//		public static final QsTerm[] EMPTY_ARRAY = {};
//		private static final QsTerm ZERO = new QsTerm(QsNumber.ZERO,
//				ArrayHelper.EMPTY_INT_ARRAY);
//
//		public static QsTerm term(int... word) {
//			return new QsTerm(QsNumber.one (), word);
//		}
//
//		QsNumber coef;
//
//		QsTerm() {
//			this(QsNumber.one(), ArrayHelper.EMPTY_INT_ARRAY);
//		}
//		QsTerm(int[] word) {
//			this(QsNumber.one(), word);
//		}
//		QsTerm(QsNumber coef, int[] word) {
//			super(word);
//			this.coef = coef;
//		}
//		public boolean isZero() {
//			return this.coef.isZero();
//		}
//		public QsTerm times(QsTerm y) {
//			if (this.isZero()) {
//				return this;
//			} else if (y == null || y.isZero()) {
//				return y;
//			}
//			int[] xs = this.word;
//			int[] ys = y.word;
//			if (xs.length < 1) {
//				return y.scale(this.coef);
//			} else if (ys.length < 1) {
//				return this.scale(y.coef);
//			}
//			int[] word = IntWord.times(xs, ys);
//			return new QsTerm(this.coef.times(y.coef), word);
//		}
//		public QsTerm times(int a) {
//			if (this.isZero()) {
//				return this;
//			}
//			int[] word = IntWord.times(this.word, a);
//			return new QsTerm(this.coef, word);
//		}
//		public QsTerm scale(QsNumber coef) {
//			QsTerm x = (QsTerm) this.clone();
//			x.coef = x.coef.times (coef);
//			return x;
//		}
//		public QsTerm zero() {
//			return QsTerm.ZERO;
//		}
//		public QsTerm[] singleton() {
//			return new QsTerm[] { this };
//		}
//		@Override
//		public Appendable toString(Appendable output) throws IOException {
//			this.coef.toString(output);
//			return super.toString(output);
//		}
//		public static long sum(LongTerm[] xs) {
//			long sum = 0;
//			for (int n = xs.length; 0 < n--;) {
//				sum += xs[n].coef;
//			}
//			return sum;
//		}
//	}
//
//	static class LongPolynomial {
//		final LongTerm[] terms;
//
//		LongPolynomial(LongTerm[] terms) {
//			this.terms = terms;
//		}
//		public Map<IntWord, LongTerm> times(Map<IntWord, LongTerm> output,
//				LongPolynomial y) {
//			LongTerm[] xs = this.terms;
//			LongTerm[] ys = y.terms;
//			for (int xn = xs.length; 0 < xn--;) {
//				for (int yn = ys.length; 0 < yn--;) {
//					this.plus(output, this.times(xs[xn], ys[yn]));
//				}
//			}
//			return output;
//		}
//		public Map<IntWord, LongTerm> times(Map<IntWord, LongTerm> output, int a) {
//			LongTerm[] xs = this.terms;
//			for (int xn = xs.length; 0 < xn--;) {
//				this.plus(output, this.times(xs[xn], a));
//			}
//			return output;
//		}
//		public LongTerm times(LongTerm x, LongTerm y) {
//			return x.times(y);
//		}
//		public LongTerm times(LongTerm x, int a) {
//			return x.times(a);
//		}
//		public Map<IntWord, LongTerm> plus(Map<IntWord, LongTerm> output, LongTerm x) {
//			LongTerm y = output.get(x);
//			if (y == null) {
//				output.put(x, x);
//			} else {
//				y.coef += x.coef;
//			}
//			return output;
//		}
//	}
//
//	static class LongCommutative extends LongPolynomial {
//		LongCommutative(LongTerm[] terms) {
//			super(terms);
//		}
//		@Override
//		public LongTerm times(LongTerm x, LongTerm y) {
//			if (x == null || x.isZero()) {
//				return x;
//			} else if (y == null || y.isZero()) {
//				return y;
//			}
//			int[] xs = x.word;
//			int[] ys = y.word;
//			if (xs.length < 1) {
//				return y.scale(x.coef);
//			} else if (ys.length < 1) {
//				return x.scale(y.coef);
//			}
//			int[] word = IntWord.plus (xs, ys);
//			return new LongTerm(x.coef * y.coef, word);
//		}
//		@Override
//		public LongTerm times(LongTerm x, int a) {
//			if (x == null || x.isZero()) {
//				return x;
//			}
//			int[] word = IntWord.plus (x.word, a);
//			return new LongTerm(x.coef, word);
//		}
//	}
//
//	public void testQsNumber() throws IOException {
//		Debug.log().debug("(2,0) = " + QsNumber.binom(2, 0));
//		Debug.log().debug("(2,1) = " + QsNumber.binom(2, 1));
//		Debug.log().debug("(2,2) = " + QsNumber.binom(2, 2));
//		Debug.log().debug("(3,2) = " + QsNumber.binom(3, 2));
//		Debug.log().debug("(4,2) = " + QsNumber.binom(4, 2));
//		Debug.log().debug(
//				"(3,2)(2,1) = " + QsNumber.binom(3, 2).times(QsNumber.binom(2, 1)));
//	}
//
//	@SuppressWarnings({ "unused" })
//	public void testOrdered() throws IOException {
//		LongTerm[] stpd = LongTerm.EMPTY_ARRAY;
//		final int N = 10;
//		final LongPolynomial[] dn = new LongPolynomial[N + 1];
//		final LongPolynomial[][] dnk = new LongPolynomial[N + 1][];
//		dnk[0] = new LongPolynomial[1];
//		dnk[0][0] = new LongPolynomial(LongTerm.term().singleton());
//		dn[0] = new LongPolynomial(LongTerm.term(0).singleton());
//		long catalan = 1;
//		Map<IntWord, LongTerm> termMap = new HashMap<IntWord, LongTerm>();
//		for (int n = 0; n < N; ++n) {
//			LongPolynomial[] nks = new LongPolynomial[n + 2];
//			for (int k = 0; k <= n; ++k) {
//				termMap.clear();
//				for (int r = k; r <= n; ++r) {
//					if (dnk[r][k] == null) {
//						continue;
//					}
//					dn[n - r].times(termMap, dnk[r][k]);
//					LongTerm[] terms = termMap.values().toArray(stpd);
//					nks[k + 1] = new LongPolynomial(terms);
//				}
//			}
//			dnk[n + 1] = nks;
//			termMap.clear();
//			for (int k = nks.length; 1 < k--;) {
//				nks[k].times(termMap, k);
//			}
//			LongTerm[] terms = termMap.values().toArray(stpd);
//			dn[n + 1] = new LongPolynomial(terms);
//			if (true) {
//				catalan = catalan * 2 * (2 * n + 1) / (n + 2);
//				if (false) {
//					Debug.log().debug(StringHelper.join(dn[n + 1].terms, " + "));
//					Debug.log().debug("C_" + (n + 1) + " = " + catalan);
//					Debug.log().debug("#words = " + dn[n + 1].terms.length);
//					Debug.log().debug("#terms = " + LongTerm.sum(dn[n + 1].terms));
//					assertEquals("#words", catalan, dn[n + 1].terms.length);
//				}
//				assertEquals("#terms", catalan, LongTerm.sum(dn[n + 1].terms));
//			}
//		}
//	}
//
//	@SuppressWarnings({ "unused" })
//	public void testUnOrdered() throws IOException {
//		LongTerm[] stpd = LongTerm.EMPTY_ARRAY;
//		final int N = 33;
//		final LongCommutative[] dn = new LongCommutative[N + 1];
//		final LongCommutative[][] dnk = new LongCommutative[N + 1][];
//		dnk[0] = new LongCommutative[1];
//		dnk[0][0] = new LongCommutative(LongTerm.term().singleton());
//		dn[0] = new LongCommutative(LongTerm.term(0).singleton());
//		long catalan = 1;
//		Map<IntWord, LongTerm> termMap = new HashMap<IntWord, LongTerm>();
//		for (int n = 0; n < N; ++n) {
//			LongCommutative[] nks = new LongCommutative[n + 2];
//			for (int k = 0; k <= n; ++k) {
//				termMap.clear();
//				for (int r = k; r <= n; ++r) {
//					if (dnk[r][k] == null) {
//						continue;
//					}
//					dn[n - r].times(termMap, dnk[r][k]);
//					LongTerm[] terms = termMap.values().toArray(stpd);
//					nks[k + 1] = new LongCommutative(terms);
//				}
//			}
//			dnk[n + 1] = nks;
//			termMap.clear();
//			for (int k = nks.length; 1 < k--;) {
//				nks[k].times(termMap, k);
//			}
//			LongTerm[] terms = termMap.values().toArray(stpd);
//			dn[n + 1] = new LongCommutative(terms);
//			if (true) {
//				catalan = catalan * 2 * (2 * n + 1) / (n + 2);
//				if (true) {
//					Debug.log().debug("#words = " + dn[n + 1].terms.length);
//					if (false) {
//						Debug.log().debug(StringHelper.join(dn[n + 1].terms, " + "));
//						Debug.log().debug("C_" + (n + 1) + " = " + catalan);
//						Debug.log().debug("#words = " + dn[n + 1].terms.length);
//						Debug.log().debug("#terms = " + LongTerm.sum(dn[n + 1].terms));
//						// assertEquals("#words", catalan, dn[n + 1].terms.length);
//					}
//				}
//				assertEquals("#terms", catalan, LongTerm.sum(dn[n + 1].terms));
//			}
//		}
//	}
//
//	static class D_nk {
//		final LongTerm[] terms;
//
//		D_nk() {
//			this(new LongTerm[] { new LongTerm() });
//		}
//		D_nk(LongTerm[] terms) {
//			this.terms = terms != null ? terms : LongTerm.EMPTY_ARRAY;
//		}
//		public String toString() {
//			StringBuilder buffer = buffer();
//			try {
//				this.toString(buffer);
//			} catch (IOException ex) {
//				ex.printStackTrace();
//			}
//			return buffer.toString();
//		}
//		public Appendable toString(Appendable output) throws IOException {
//			LongTerm[] ws = this.terms;
//			for (int i = 0, n = ws.length; i < n; ++i) {
//				if (i != 0) {
//					output.append(" + ");
//				}
//				ws[i].toString(output);
//			}
//			return output;
//		}
//		Collection<LongTerm> times(Collection<LongTerm> output, int k) {
//			LongTerm[] ts = this.terms;
//			for (int tn = ts.length; 0 < tn--;) {
//				LongTerm t = ts[tn];
//				int[] ws = t.word;
//				int[] word = new int[ws.length + 1];
//				System.arraycopy(ws, 0, word, 0, ws.length);
//				word[ws.length] = k;
//				LongTerm u = new LongTerm(t.coef, word);
//				output.add(u);
//			}
//			return output;
//		}
//		Appendable toTex(Appendable output) throws IOException {
//			LongTerm[] ts = this.terms;
//			for (int i = 0, n = ts.length; i < n; ++i) {
//				if (i != 0) {
//					output.append(" + ");
//				}
//				ts[i].toTex(output);
//			}
//			return output;
//		}
//	}
//
//	static class SD_nk extends D_nk {
//		SD_nk() {
//			this(new LongTerm[] { new LongTerm() });
//		}
//		SD_nk(LongTerm[] terms) {
//			super(terms);
//		}
//		Map<IntWord, LongTerm> times(Map<IntWord, LongTerm> output, int k) {
//			LongTerm[] ts = this.terms;
//			for (int tn = ts.length; 0 < tn--;) {
//				LongTerm t = ts[tn];
//				int[] ws = t.word;
//				int[] word = null;
//				if (k < ws.length) {
//					word = ws.clone();
//					word[k] += 1;
//				} else {
//					word = new int[k + 1];
//					System.arraycopy(ws, 0, word, 0, ws.length);
//					word[k] = 1;
//				}
//				LongTerm u = new LongTerm(t.coef, word);
//				LongTerm v = output.get(u);
//				if (v == null) {
//					output.put(u, u);
//				} else {
//					v.coef += u.coef;
//				}
//			}
//			return output;
//		}
//		public String toPolynomial() {
//			return this.toPolynomial(false);
//		}
//		public String toPolynomial(boolean eliminateConst) {
//			StringBuilder buffer = buffer();
//			try {
//				this.toPolynomial(buffer, eliminateConst);
//			} catch (IOException ex) {
//				ex.printStackTrace();
//			}
//			return buffer.toString();
//		}
//		public Appendable toPolynomial(Appendable output) throws IOException {
//			return this.toPolynomial(output, false);
//		}
//		public Appendable toPolynomial(Appendable output, boolean eliminateConst)
//				throws IOException {
//			LongTerm[] ws = this.terms;
//			for (int i = 0, n = ws.length; i < n; ++i) {
//				if (i != 0) {
//					output.append(" + ");
//				}
//				SD_nk.toPolynomial(output, ws[i], eliminateConst);
//			}
//			return output;
//		}
//		public static Appendable toPolynomial(Appendable output, LongTerm term,
//				boolean eliminateConst) throws IOException {
//			if (term.coef == 0) {
//				return output.append("0");
//			} else if (term.coef == -1) {
//				output.append("-");
//			} else if (term.coef != 1) {
//				output.append(Long.toString(term.coef));
//			}
//			return SD_nk.toPolynomial(output.append("\\clr{"), term.word,
//					eliminateConst).append("}");
//		}
//		private static Appendable toPolynomial(Appendable output, int[] word,
//				boolean eliminateConst) throws IOException {
//			final String zero = "0";
//			final String name = "x";
//			if (word.length < 1) {
//				return output.append(zero);
//			}
//			boolean first = true;
//			final int stop = eliminateConst ? 1 : 0;
//			for (int n = word.length; stop < n--;) {
//				int a = word[n];
//				if (a == 0) {
//					continue;
//				} else if (a < 0) {
//					output.append(" - ");
//					a = -a;
//				} else if (!first) {
//					output.append(" + ");
//				}
//				String k = "";
//				String d = "";
//				switch (n) {
//				case 0:
//					k = Integer.toString(a);
//					output.append(k);
//				break;
//				case 1:
//					if (a != 1) {
//						k = Integer.toString(a);
//					}
//					output.append(k).append(name);
//				break;
//				default:
//					if (a != 1) {
//						k = Integer.toString(a);
//					}
//					d = Integer.toString(n);
//					output.append(k).append(name).append("^").append(d);
//				break;
//				}
//				first = false;
//			}
//			return output;
//		}
//	}
//
//	public void test_11() throws IOException {
//		final int N = 5;
//		Writer writer = new PrintWriter(System.out);
//		List<LongTerm> buffer = new ArrayList<LongTerm>(N);
//		@SuppressWarnings("unchecked")
//		List<D_nk>[] lists = new ArrayList[2];
//		lists[0] = new ArrayList<D_nk>(N + 1);
//		lists[1] = new ArrayList<D_nk>(N + 1);
//		lists[0].add(new D_nk());
//		for (int n = 0; n < N; ++n) {
//			List<D_nk> xs = lists[0];
//			List<D_nk> ys = lists[1];
//			ys.clear();
//			ys.add(null);
//			for (int k = 0; k <= n; ++k) {
//				buffer.clear();
//				for (int i = k; i <= n; ++i) {
//					D_nk x = xs.get(i);
//					if (x == null) {
//						continue;
//					}
//					x.times(buffer, i - k);
//				}
//				LongTerm[] ts = LongTerm.EMPTY_ARRAY;
//				ts = buffer.toArray(ts);
//				D_nk dnk = new D_nk(ts);
//				ys.add(dnk);
//				writer.append("D_{" + (n + 1) + "," + (k + 1) + "} &= ");
//				dnk.toTex(writer);
//				writer.append(" \\\\\n").flush();
//			}
//			ArrayHelper.swap(lists, 0, 1);
//		}
//	}
//	@SuppressWarnings("unused")
//	public void test_12() throws IOException {
//		final int N = 5;
//		Writer writer = new PrintWriter(System.out);
//		Map<IntWord, LongTerm> buffer = new HashMap<IntWord, LongTerm>();
//		@SuppressWarnings("unchecked")
//		List<SD_nk>[] lists = new ArrayList[2];
//		lists[0] = new ArrayList<SD_nk>(N + 1);
//		lists[1] = new ArrayList<SD_nk>(N + 1);
//		lists[0].add(new SD_nk());
//
//		writer.append("| deg");
//		for (int n = 1; n <= N; ++n) {
//			writer.append("| " + Integer.toString(n));
//		}
//		writer.append("|\n").flush();
//		writer.append("|--");
//		for (int n = 1; n <= N; ++n) {
//			writer.append("|--");
//		}
//		writer.append("|\n").flush();
//
//		for (int n = 0; n < N; ++n) {
//			List<SD_nk> xs = lists[0];
//			List<SD_nk> ys = lists[1];
//			ys.clear();
//			ys.add(null);
//			for (int k = 0; k <= n; ++k) {
//				buffer.clear();
//				for (int i = k; i <= n; ++i) {
//					SD_nk x = xs.get(i);
//					if (x == null) {
//						continue;
//					}
//					x.times(buffer, i - k);
//				}
//				LongTerm[] ts = LongTerm.EMPTY_ARRAY;
//				ts = buffer.values().toArray(ts);
//				SD_nk dnk = new SD_nk(ts);
//				ys.add(dnk);
//			}
//			ArrayHelper.swap(lists, 0, 1);
//			writer.append("| " + Integer.toString(n + 1));
//			xs = lists[0];
//			int xi = 1;
//			for (; xi < xs.size(); ++xi) {
//				if (true) {
//					SD_nk dnk = xs.get(xi);
//					Arrays.sort(dnk.terms, IntWord.COMPARATOR);
//					ArrayHelper.reverse(dnk.terms);
//					dnk.toPolynomial(writer.append("| $"), true).append("$");
//				} else {
//					writer.append("\\pi_xD_{" + (n + 1) + "," + xi + "} &= ");
//					xs.get(xi).toPolynomial(writer);
//				}
//			}
//			for (; xi <= N; ++xi) {
//				writer.append("| ");
//			}
//			writer.append("|\n").flush();
//		}
//	}
}
