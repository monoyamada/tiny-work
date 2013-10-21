package parser.v2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import junit.framework.TestCase;
import tiny.function.Function;
import tiny.function.LexicographicalOrders;
import tiny.lang.ArrayHelper;
import tiny.lang.Debug;
import tiny.lang.StringHelper;
import tiny.primitive.MutableInt;

public class CatalanTest extends TestCase {
	static final ThreadLocal<StringBuilder> STRING_BUILDER = new ThreadLocal<StringBuilder>() {
		@Override
		protected StringBuilder initialValue() {
			return new StringBuilder();
		}
	};

	static StringBuilder buffer() {
		StringBuilder out = STRING_BUILDER.get();
		out.setLength(0);
		return out;
	}

	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
		super.setUp();
	}
	public void test_1() {
		List<List<int[]>> memo = new ArrayList<List<int[]>>();
		List<int[]> xs = Collections.singletonList(new int[] { 0 });
		memo.add(xs);
		for (int n = 0; n < 5; ++n) {
			xs = new ArrayList<int[]>();
			for (int k = 0; k <= n; ++k) {
				List<int[]> ys = memo.get(k);
				List<int[]> zs = memo.get(n - k);
				for (int[] y : ys) {
					for (int[] z : zs) {
						int[] x = ArrayHelper.addAll(y, z);
						x[0] += 1;
						xs.add(x);
					}
				}
			}
			memo.add(xs);
		}
		if (true) {
			for (int i = 0, n = memo.size(); i < n; ++i) {
				xs = memo.get(i);
				String out = "";
				for (int xi = 0, xn = xs.size(); xi < xn; ++xi) {
					if (xi != 0) {
						out += " + ";
					}
					out += "(" + StringHelper.join(xs.get(xi), ", ") + ")";
				}
				System.out.println(out);
			}
		}
		for (int n = 1, N = memo.size(); n < N; ++n) {
			xs = memo.get(n);
			@SuppressWarnings({ "unchecked" })
			Map<String, Integer>[] ys = new Map[n + 1];
			for (int xi = 0, xn = xs.size(); xi < xn; ++xi) {
				int[] x = xs.get(xi);
				int k = x[0];
				int[] y = ArrayHelper.sub(x, 1, x.length);
				Arrays.sort(y);
				ArrayHelper.reverse(y);
				if (ys[k] == null) {
					ys[k] = new TreeMap<String, Integer>();
				}
				String yy = StringHelper.join(y);
				Integer v = ys[k].get(yy);
				if (v == null) {
					ys[k].put(yy, Integer.valueOf(1));
				} else {
					ys[k].put(yy, Integer.valueOf(v.intValue() + 1));
				}
			}
			for (int k = 1; k <= n; ++k) {
				Map<String, Integer> y = ys[k];
				Debug.log().debug("(" + n + ", " + k + "): " + y);
			}
		}
	}

	public void testBinom() {
		Binomial gen = new Binomial();
		for (int n = 0; n < 10; ++n) {
			for (int k = 0; k <= n; ++k) {
				if (k != 0) {
					System.out.print(", ");
				}
				System.out.print(gen.get(n, k));
			}
			System.out.println();
		}
	}

	@SuppressWarnings("unused")
	public void test_2() {
		int N = 9;
		Binomial binom = new Binomial();
		for (int K = 1; K < 4; ++K) {
			List<int[]> memo = new ArrayList<int[]>();
			int[] xs = new int[] { 1 };
			memo.add(xs);
			for (int n = 0; n < N; ++n) {
				xs = new int[n + 2];
				xs[0] = 0;
				for (int k = 0; k <= n; ++k) {
					int x = 0;
					for (int r = k; r <= n; ++r) {
						int y = memo.get(r)[k];
						int[] zs = memo.get(n - r);
						for (int s = 0; s <= n - r; ++s) {
							x += y * binom.get(K + s - 1, s) * zs[s];
						}
					}
					xs[k + 1] = x;
				}
				memo.add(xs);
			}
			Debug.log().debug(K);
			for (int n = 1; n < N; ++n) {
				xs = memo.get(n - 1);
				int[] ys = memo.get(n);
				int[] zs = ys.clone();
				int xn = xs.length;
				for (int xi = 0; xi < xn; ++xi) {
					int z = 0;
					for (int s = xi; s < xn; ++s) {
						z += binom.get(K + s - 1 - xi, s - xi) * xs[s];
					}
					zs[xi + 1] = z;
				}
				Debug.log().debug(StringHelper.join(ys, ", "));
				Debug.log().debug(StringHelper.join(zs, ", "));
			}
			if (false) {
				for (int n = 0; n < N; ++n) {
					xs = memo.get(n);
					System.out.println(n + " & " + ArrayHelper.sum(xs) + " & "
							+ StringHelper.join(xs, " & ") + " \\\\");
				}
			}
		}
	}

	static class Test_3 {
		static void grow_0(Collection<int[]> output, int[] xs) {
			for (int i = 0, n = xs.length; i < n; ++i) {
				int x = xs[i];
				if (0 <= x) {
					int[] ys = new int[n + 1];
					System.arraycopy(xs, 0, ys, 0, i);
					ys[i] = -1;
					System.arraycopy(xs, i, ys, i + 1, n - i);
					ys[i + 1] += 1;
					output.add(ys);
					break;
				}
				int[] ys = new int[n + 1];
				System.arraycopy(xs, 0, ys, 0, i + 1);
				ys[i] -= 1;
				ys[i + 1] = 1;
				System.arraycopy(xs, i + 1, ys, i + 2, n - i - 1);
				output.add(ys);
			}
		}
		static String toString(int[] xs) {
			return new String(xs, 0, xs.length);
		}
	}

	static class Stupid_1 {
		@SuppressWarnings({ "rawtypes", "unchecked" })
		static List<int[]> INT_ARRAYS = new ArrayList<int[]>();

		static int[] array(int size) {
			while (INT_ARRAYS.size() <= size) {
				INT_ARRAYS.add(new int[INT_ARRAYS.size()]);
			}
			return INT_ARRAYS.get(size);
		}
		static int[] clone(int[] xs) {
			int[] ys = array(xs.length);
			System.arraycopy(xs, 0, ys, 0, xs.length);
			return ys;
		}
		static Stupid_1 add(Map<String, Stupid_1> map, int[] xs) {
			int[] ys = clone(xs);
			Arrays.sort(ys);
			StringBuilder buffer = buffer();
			try {
				StringHelper.join(buffer, ys, ",");
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			String key = buffer.toString();
			Stupid_1 val = map.get(key);
			if (val == null) {
				map.put(key, val = new Stupid_1(ys.clone()));
			}
			val.count += 1;
			return val;
		}

		final int[] word;
		int count;

		Stupid_1(int[] word) {
			this.word = word;
		}
		public String toString() {
			StringBuilder buffer = buffer();
			if (1 < this.count) {
				buffer.append(Integer.toString(this.count));
			}
			try {
				StringHelper.join(buffer.append('['), this.word, ", ").append(']');
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			return buffer.toString();
		}
	}

	static Function<int[], String> dumpIntArray = new Function<int[], String>() {
		@Override
		public String evaluate(int[] source) throws Exception {
			return "[" + StringHelper.join(source) + "]";
		}
	};

	public void test_3() {
		@SuppressWarnings("unchecked")
		List<int[]>[] buffers = new List[2];
		buffers[0] = new ArrayList<int[]>();
		buffers[1] = new ArrayList<int[]>();
		int[] zero = { 0 };
		buffers[0].add(zero);
		for (int depth = 0; depth < 5; ++depth) {
			for (int i = 0, n = buffers[0].size(); i < n; ++i) {
				int[] xs = buffers[0].get(i);
				Test_3.grow_0(buffers[1], xs);
			}
			// Debug.log().debug(StringHelper.join(buffers[1], ", ", dumpIntArray));
			if (true) {
				Map<String, Stupid_1> stupids = new TreeMap<String, Stupid_1>();
				for (int[] xs : buffers[1]) {
					Stupid_1.add(stupids, xs);
				}
				Debug.log().debug(StringHelper.join(stupids.values(), " + "));
			}
			buffers[0].clear();
			ArrayHelper.swap(buffers, 0, 1);
		}
	}

	static class WordPair {
		public static final WordPair UNIT = new WordPair(new int[] { 1 },
				new int[] { 0 });

		static WordPair newPair(int size) {
			int[] k = new int[size];
			int[] w = new int[size];
			return new WordPair(k, w);
		}

		int[] ks;
		int[] word;

		WordPair(int[] ks, int[] word) {
			this.ks = ks;
			this.word = word;
		}
	}

	static class Word implements Comparable<Word> {
		final int[] word;

		Word() {
			this(ArrayHelper.EMPTY_INT_ARRAY);
		}
		Word(int[] word) {
			this.word = word;
		}
		public int hashCode() {
			return Arrays.hashCode(this.word);
		}
		public boolean equals(Object x) {
			return this.equalWord((Word) x);
		}
		public boolean equalWord(Word x) {
			if (x == null) {
				return false;
			} else if (this == x) {
				return true;
			}
			return ArrayHelper.equalArray(this.word, x.word);
		}
		@Override
		public int compareTo(Word x) {
			if (x == null || this.word.length < x.word.length) {
				return -1;
			} else if (this == x) {
				return 0;
			}
			return LexicographicalOrders.compare(this.word, x.word);
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
		public Appendable toString(Appendable output) throws IOException {
			return StringHelper.join(output.append('['), this.word, ", ").append(']');
		}
	}

	static class Composition extends Word {
		public static final Composition UNIT = new Composition(
				ArrayHelper.EMPTY_INT_ARRAY);

		Composition sorted;

		Composition(int[] word) {
			super(word);
		}
		Composition sorted() {
			if (this.sorted == null) {
				this.sorted = this.newSorted();
			}
			return this.sorted;
		}
		private Composition newSorted() {
			int[] xs = this.word.clone();
			Arrays.sort(xs);
			return new Composition(xs);
		}
		void next(List<Composition> out) {
			int[] xs = this.word;
			for (int i = 0, n = xs.length; i < n; ++i) {
				int[] ys = xs.clone();
				ys[i] += 1;
				out.add(new Composition(ys));
				if (1 < xs[i]) {
					return;
				}
			}
			int[] ys = new int[xs.length + 1];
			System.arraycopy(xs, 0, ys, 0, xs.length);
			ys[xs.length] = 1;
			out.add(new Composition(ys));
		}
	}

	static class Term extends Word {
		int k;

		Term(int[] word) {
			this(word, 1);
		}
		Term(int[] word, int k) {
			super(word);
			this.k = k;
		}
		public Appendable toString(Appendable output) throws IOException {
			if (this.k != 1) {
				output.append(Integer.toString(this.k)).append(' ');
			}
			return super.toString(output);
		}
	}

	public void test_4() throws IOException {
		@SuppressWarnings("unchecked")
		List<Composition>[] cs = new List[2];
		cs[0] = new ArrayList<Composition>(32);
		cs[1] = new ArrayList<Composition>(32);
		cs[0].add(Composition.UNIT);
		// List<Term_2[]> xs = new ArrayList<Term_2[]>(32);
		Writer writer = new PrintWriter(System.out);
		Term[] terms = terms(cs[0]);
		// doit(xs, terms, 1);
		StringHelper.join(writer, terms, " + ").append('\n').flush();
		for (int deg = 2; deg < 7; ++deg) {
			cs[1].clear();
			for (int ii = 0, nn = cs[0].size(); ii < nn; ++ii) {
				cs[0].get(ii).next(cs[1]);
			}
			terms = terms(cs[1]);
			// doit(xs, terms, n);
			StringHelper.join(writer, terms, " + ").append('\n').flush();
			ArrayHelper.swap(cs, 0, 1);
		}
	}

	// static List<Term_2[]> doit(List<Term_2[]> xs, Term[] terms, int deg) {
	// int[] x;
	// return xs;
	// }
	static Term[] terms(List<Composition> xs) {
		Map<Composition, MutableInt> map = sort(xs);
		Term[] out = new Term[map.size()];
		Iterator<Entry<Composition, MutableInt>> p = map.entrySet().iterator();
		for (int i = 0; p.hasNext(); ++i) {
			Entry<Composition, MutableInt> ent = p.next();
			Term y = new Term(ent.getKey().word);
			y.k = ent.getValue().value;
			out[i] = y;
		}
		return out;
	}
	static Map<Composition, MutableInt> sort(List<Composition> xs) {
		Map<Composition, MutableInt> map = new TreeMap<Composition, MutableInt>();
		for (int i = 0, n = xs.size(); i < n; ++i) {
			Composition x = xs.get(i).sorted();
			MutableInt val = map.get(x);
			if (val == null) {
				val = new MutableInt(1);
				map.put(x, val);
			} else {
				++val.value;
			}
		}
		return map;
	}
	static long dim(long n, long k) {
		if (k < 0 || n < 0) {
			throw new Error("wrong");
		} else if (k == 0) {
			return n == 0 ? 1 : 0;
		} else if (k == 1 || n == 0) {
			return 1;
		}
		long q = n / k;
		long r = n - q * k;
		long out = 0;
		while (0 < q--) {
			out += dim(r, k - 1);
			r += k;
			// --q;
		}
		return out + dim(n, k - 1);
	}
	public void test_5() throws IOException {
		int N = 34;
		Writer writer = new PrintWriter(System.out);
		long c = 1;
		writer.append("| " + 0 + " | " + c + " | " + 1 + " |\n").flush();
		for (int n = 1; n <= N; ++n) {
			if (c % (n + 1) == 0) {
				c /= n + 1;
				c *= 2 * (2 * n - 1);
			} else {
				c = 2 * (2 * n - 1) * c / (n + 1);
			}
			long d = dim(n, n);
			writer.append("| " + n + " | " + c + " | " + d + " |\n").flush();
		}
	}
	public void test_6() {
		int[][] as = new int[3][];
		Debug.log().debug(as.length);
	}

	static class Term_1 extends Word {
		public static final Term_1[] EMPTY_ARRAY = {};

		long coef;

		Term_1() {
			this(0, ArrayHelper.EMPTY_INT_ARRAY);
		}
		Term_1(int[] word) {
			this(1, word);
		}
		Term_1(long coef, int[] word) {
			super(word);
			this.coef = coef;
		}
		public Appendable toString(Appendable output) throws IOException {
			if (this.coef == 1) {
				return super.toString(output);
			}
			return super.toString(output.append(Long.toString(this.coef)));
		}
	}

	static class G {
		private static HashMap<Word, Term_1> TERM_MAP = new HashMap<Word, Term_1>();
		Term_1[][] terms;
		Term_1[] poly;

		G(int n) {
			this.terms = new Term_1[n + 1][];
			Arrays.fill(this.terms, Term_1.EMPTY_ARRAY);
		}
		private Term_1[] poly() {
			if (this.poly == null) {
				this.poly = this.newPoly();
			}
			return this.poly;
		}
		private Term_1[] newPoly() {
			TERM_MAP.clear();
			int k = this.terms.length;
			if (k == 1) {
				return new Term_1[] { new Term_1(1, new int[] { 1 }) };
			}
			while (1 < k--) {
				Term_1[] xs = this.terms[k];
				for (int xi = xs.length; 0 < xi--;) {
					Term_1 x = xs[xi];
					if (x.coef == 0) {
						continue;
					}
					int size = Math.max(x.word.length, k + 1);
					int[] word;
					if (x.word.length < size) {
						word = new int[size];
						System.arraycopy(x.word, 0, word, 0, x.word.length);
					} else {
						word = x.word.clone();
					}
					word[k] += 1;
					Term_1 y = new Term_1(x.coef, word);
					Term_1 z = TERM_MAP.get(y);
					if (z == null) {
						TERM_MAP.put(y, y);
					} else {
						z.coef += y.coef;
						if (z.coef == 0) {
							TERM_MAP.remove(y);
						}
					}
				}
			}
			return TERM_MAP.values().toArray(Term_1.EMPTY_ARRAY);
		}
		HashMap<Word, Term_1> times(HashMap<Word, Term_1> output, Term_1[] terms) {
			for (int yn = terms.length; 0 < yn--;) {
				Term_1 y = terms[yn];
				if (y.coef == 0) {
					continue;
				}
				Term_1[] poly = this.poly();
				for (int xn = poly.length; 0 < xn--;) {
					G.times(output, poly[xn], y);
				}
			}
			return output;
		}
		static HashMap<Word, Term_1> times(HashMap<Word, Term_1> output, Term_1 x,
				Term_1 y) {
			if (x.coef == 0 || y.coef == 0) {
				return output;
			}
			if (x.word.length < y.word.length) {
				Term_1 z = x;
				x = y;
				y = z;
			}
			int[] word = x.word.clone();
			for (int n = y.word.length; 0 < n--;) {
				word[n] += y.word[n];
			}
			Term_1 a = new Term_1(x.coef * y.coef, word);
			Term_1 b = output.get(a);
			if (b == null) {
				output.put(a, a);
			} else {
				b.coef += a.coef;
			}
			return output;
		}
	}

	public void test_7() throws IOException {
		Writer writer = new PrintWriter(System.out);
		final int N = 35;
		G[] gs = new G[N + 1];
		gs[0] = new G(0);
		gs[0].terms[0] = new Term_1[] { new Term_1(1, ArrayHelper.EMPTY_INT_ARRAY) };
		HashMap<Word, Term_1> terms = new HashMap<Word, Term_1>();
		for (int n = 0; n < N; ++n) {
			gs[n + 1] = new G(n + 1);
			for (int k = 0; k <= n; ++k) {
				terms.clear();
				for (int r = 0; r <= n - k; ++r) {
					gs[r].times(terms, gs[n - r].terms[k]);
				}
				gs[n + 1].terms[k + 1] = terms.values().toArray(Term_1.EMPTY_ARRAY);
			}
			long d = dim(n + 1, n + 1);
			writer
					.append(
							"| " + (n + 1) + " | " + d + " | " + gs[n + 1].poly().length
									+ " |\n").flush();
			// Debug.log().debug(StringHelper.join(gs[n + 1].poly(), " + "));
		}
	}
	public void test_7_1() throws IOException {
		Writer writer = new PrintWriter(System.out);
		final int N = 20;
		G[] gs = new G[N + 1];
		gs[0] = new G(0);
		gs[0].terms[0] = new Term_1[] { new Term_1(1, ArrayHelper.EMPTY_INT_ARRAY) };
		HashMap<Word, Term_1> terms = new HashMap<Word, Term_1>();
		writer.append("| $\\deg$ | $g_n$");
		for (int k = 0; k < N; ++k) {
			writer.append(" | $g_{n," + (k + 1) + "}$");
		}
		writer.append("|\n").flush();
		writer.append("|--|--");
		for (int k = 0; k < N; ++k) {
			writer.append("|--");
		}
		writer.append("|\n").flush();
		for (int n = 0; n < N; ++n) {
			gs[n + 1] = new G(n + 1);
			for (int k = 0; k <= n; ++k) {
				terms.clear();
				for (int r = 0; r <= n - k; ++r) {
					gs[r].times(terms, gs[n - r].terms[k]);
				}
				gs[n + 1].terms[k + 1] = terms.values().toArray(Term_1.EMPTY_ARRAY);
			}
			writer.append("| " + (n + 1) + " | " + gs[n + 1].poly().length);
			for (int k = 0; k <= n; ++k) {
				writer.append(" | " + gs[n + 1].terms[k + 1].length);
			}
			for (int k = n + 1; k < N; ++k) {
				writer.append(" |");
			}
			writer.append(" |\n").flush();
		}
	}
	public void test_7_2() throws IOException {
		Writer writer = new PrintWriter(System.out);
		final int N = 5;
		G[] gs = new G[N + 1];
		gs[0] = new G(0);
		gs[0].terms[0] = new Term_1[] { new Term_1(1, ArrayHelper.EMPTY_INT_ARRAY) };
		HashMap<Word, Term_1> terms = new HashMap<Word, Term_1>();
		writer.append("| $\\deg$");
		for (int k = 0; k < N; ++k) {
			writer.append(" | $G_{n," + (k + 1) + "}$");
		}
		writer.append(" |\n").flush();
		writer.append("|--");
		for (int k = 0; k < N; ++k) {
			writer.append("|--");
		}
		writer.append("|\n").flush();
		for (int n = 0; n < N; ++n) {
			gs[n + 1] = new G(n + 1);
			for (int k = 0; k <= n; ++k) {
				terms.clear();
				for (int r = 0; r <= n - k; ++r) {
					gs[r].times(terms, gs[n - r].terms[k]);
				}
				gs[n + 1].terms[k + 1] = terms.values().toArray(Term_1.EMPTY_ARRAY);
			}
			writer.append("| " + (n + 1));
			for (int k = 0; k <= n; ++k) {
				writer.append(" | ");
				// writeTerms(writer, gs[n + 1].terms[k + 1]);
				writeDerivatives(writer, gs[n + 1].terms[k + 1]);
			}
			for (int k = n + 1; k < N; ++k) {
				writer.append(" |");
			}
			writer.append(" |\n").flush();
		}
	}

	private static class TermComparator implements Comparator<Term_1> {
		public static final Comparator<? super Term_1> INSTANCE = new TermComparator();

		@Override
		public int compare(Term_1 o1, Term_1 o2) {
			int[] w1 = o1.word;
			int[] w2 = o2.word;
			if (w1.length > w2.length) {
				return 1;
			} else if (w1.length < w2.length) {
				return -1;
			}
			int n = w1.length - 1;
			while (0 < n--) {
				if (w1[n] > w2[n]) {
					return 1;
				} else if (w1[n] < w2[n]) {
					return -1;
				}
			}
			return 0;
		}
	}

	private static Appendable writeTerms(Appendable output, Term_1[] terms)
			throws IOException {
		terms = terms.clone();
		Arrays.sort(terms, 0, terms.length, TermComparator.INSTANCE);
		ArrayHelper.reverse(terms);
		output.append("$");
		for (int i = 0, n = terms.length; i < n; ++i) {
			Term_1 term = terms[i];
			if (i != 0) {
				output.append(" + ");
			}
			if (term.coef != 1) {
				output.append(Long.toString(term.coef));
			}
			output.append("\\word{");
			int[] word = term.word;
			boolean second = false;
			for (int ii = 0, nn = word.length; ii < nn; ++ii) {
				int w = word[ii];
				if (w != 0) {
					if (second) {
						output.append(" + ");
					}
					switch (ii) {
					case 0:
						output.append(Integer.toString(w));
					break;
					case 1:
						if (w != 1) {
							output.append(Integer.toString(w));
						}
						output.append("x");
					break;
					default:
						if (w != 1) {
							output.append(Integer.toString(w));
						}
						output.append("x^{" + Integer.toString(ii) + "}");
					}
					break;
				}
				second = true;
			}
			// StringHelper.join(output, term.word, ", ");
			output.append("}");
		}
		return output.append("$");
	}
	private static Appendable writeDerivatives(Appendable output, Term_1[] terms)
			throws IOException {
		terms = terms.clone();
		Arrays.sort(terms, 0, terms.length, TermComparator.INSTANCE);
		ArrayHelper.reverse(terms);
		output.append("$");
		for (int i = 0, n = terms.length; i < n; ++i) {
			Term_1 term = terms[i];
			if (i != 0) {
				output.append(" + ");
			}
			if (term.coef != 1) {
				output.append(Long.toString(term.coef));
			}
			String v = "f";
			int[] word = term.word;
			for (int ii = 0, nn = word.length; ii < nn; ++ii) {
				int w = word[ii];
				if (w != 0) {
					switch (ii) {
					case 0:
						if (w == 1) {
							output.append(v);
						} else {
							output.append(v + "^{" + w + "}");
						}
					break;
					case 1:
						if (w == 1) {
							if (nn < 2) {
								output.append("\\partial " + v);
							} else {
								output.append("(\\partial " + v + ")");
							}
						} else {
							output.append("(\\partial " + v + ")^{" + w + "}");
						}
					break;
					default:
						if (w == 1) {
							if (nn < 2) {
								output.append("\\partial^{" + ii + "}" + v);
							} else {
								output.append("(\\partial^{" + ii + "}" + v + ")");
							}
						} else {
							output.append("(\\partial^{" + ii + "}" + v + ")^{" + w + "}");
						}
					break;
					}
				}
			}
		}
		return output.append("$");
	}

	public void test_8() throws IOException {
		File outFile = new File("data/test_8.csv");
		Writer writer = new FileWriter(outFile);
		final int N = 35;
		G[] gs = new G[N + 1];
		gs[0] = new G(0);
		gs[0].terms[0] = new Term_1[] { new Term_1(1, ArrayHelper.EMPTY_INT_ARRAY) };
		HashMap<Word, Term_1> terms = new HashMap<Word, Term_1>();
		writer.append("deg, k, dim\n").flush();
		for (int n = 0; n < N; ++n) {
			gs[n + 1] = new G(n + 1);
			for (int k = 0; k <= n; ++k) {
				terms.clear();
				for (int r = 0; r <= n - k; ++r) {
					gs[r].times(terms, gs[n - r].terms[k]);
				}
				gs[n + 1].terms[k + 1] = terms.values().toArray(Term_1.EMPTY_ARRAY);
			}
			for (int k = 0; k <= n + 1; ++k) {
				Term_1[] xs = gs[n + 1].terms[k];
				long sum = 0;
				for (int xn = xs.length; 0 < xn--;) {
					sum += xs[xn].coef;
				}
				writer.append(Integer.toString(n + 1)).append(", ")
						.append(Long.toString(k)).append(", ").append(Long.toString(sum))
						.append('\n').flush();
			}
		}
		writer.close();
		Debug.log().debug("wrote " + outFile.getAbsolutePath());
	}
	public void test_9() throws IOException {
		// final int N = 35;
		final int N = 36;
		long[][] gs = new long[N + 1][];
		gs[0] = new long[] { 1 };
		for (int n = 0; n < N; ++n) {
			gs[n + 1] = new long[n + 2];
			for (int k = 0; k <= n; ++k) {
				for (int r = 0; r <= n - k; ++r) {
					for (int i = 0; i <= r; ++i) {
						gs[n + 1][k + 1] += gs[r][i] * gs[n - r][k];
					}
				}
			}
			Debug.log().debug(StringHelper.join(gs[n + 1], ", "));
		}
	}
	public void test_10() throws IOException {
		Debug.log().debug(Integer.MAX_VALUE * 2 / Integer.MAX_VALUE);
		Debug.log().debug(Integer.MAX_VALUE / Integer.MAX_VALUE * 2);
	}
	public void test_11() throws IOException {
		final int N = 5;
		Word[][] ws = new Word[N + 1][];
		ws[0] = new Word[] { new Word(new int[] { 0 }) };
		int size = 1;
		for (int n = 0; n < N; ++n) {
			size *= 2 * (2 * n + 1);
			size /= n + 2;
			ws[n + 1] = new Word[size];
			int ind = 0;
			for (int r = 0; r <= n; ++r) {
				Word[] xs = ws[r];
				Word[] ys = ws[n - r];
				for (int xi = xs.length; 0 < xi--;) {
					Word x = xs[xi];
					for (int yi = ys.length; 0 < yi--;) {
						int[] word = ArrayHelper.addAll(x.word, ys[yi].word);
						word[word.length - 1] += 1;
						ws[n + 1][ind++] = new Word(word);
					}
				}
			}
		}
		Writer writer = new PrintWriter(System.out);
		List<int[]> buffer = new ArrayList<int[]>(N);
		for (int n = 0; n <= N; ++n) {
			for (int k = 0; k <= n; ++k) {
				buffer.clear();
				select(buffer, ws[n], k);
				if (buffer.size() < 1) {
					continue;
				}
				writer.append("D_{" + n + ", " + k + "} &= ");
				StringHelper.join(writer, buffer, " + ", new Function<int[], String>() {
					@Override
					public String evaluate(int[] source) throws Exception {
						return "\\word{" + StringHelper.join(source, ", ") + "}";
					}
				});
				writer.append(" \\\\\n").flush();
			}
		}
	}
	private static Collection<int[]> select(Collection<int[]> output,
			Word[] words, int k) {
		for (int n = words.length; 0 < n--;) {
			int[] w = words[n].word;
			if (w.length < 1 || w[w.length - 1] != k) {
				continue;
			}
			w = ArrayHelper.sub(w, 0, w.length - 1);
			output.add(w);
		}
		return output;
	}
}
