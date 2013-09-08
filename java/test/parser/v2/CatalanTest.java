package parser.v2;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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
		//List<Term_2[]> xs = new ArrayList<Term_2[]>(32);
		Writer writer = new PrintWriter(System.out);
		Term[] terms = terms(cs[0]);
		//doit(xs, terms, 1);
		StringHelper.join(writer, terms, " + ").append('\n').flush();
		for (int deg = 2; deg < 7; ++deg) {
			cs[1].clear();
			for (int ii = 0, nn = cs[0].size(); ii < nn; ++ii) {
				cs[0].get(ii).next(cs[1]);
			}
			terms = terms(cs[1]);
			//doit(xs, terms, n);
			StringHelper.join(writer, terms, " + ").append('\n').flush();
			ArrayHelper.swap(cs, 0, 1);
		}
	}

//	static List<Term_2[]> doit(List<Term_2[]> xs, Term[] terms, int deg) {
//		int[] x;
//		return xs;
//	}
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
}
