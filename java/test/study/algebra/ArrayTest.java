package study.algebra;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import junit.framework.Assert;
import junit.framework.TestCase;

import study.algebra.ArrayTestClasses.Identity;
import study.algebra.ArrayTestClasses.IfArrayTransform;
import study.algebra.ArrayTestClasses.PrefixCoMagma;
import study.algebra.ArrayTestClasses.StringMagma;
import study.algebra.ArrayTestClasses.SuffixCoMagma;
import study.algebra.ArrayTestClasses.Swap;
import study.lang.ArrayHelper;
import study.lang.Debug;
import study.lang.StringHelper;

public class ArrayTest extends TestCase {
	protected static class Term {
		public static final Term[] EMPTY_ARRAY = {};
		public long value;
		public Object[] base;

		public Term(long value, Object[] base) {
			this.value = value;
			this.base = base;
		}
		public String toString() {
			if (this.value == 0) {
				return "0";
			} else if (this.value == 1) {
				return kakko(this.base);
			} else if (this.value == -1) {
				return "-" + kakko(this.base);
			}
			return this.value + kakko(this.base);
		}
		public static <X extends Comparable<? super X>> Term[] minus(X[][] x0,
				X[][] x1) {
			final Map<X[], Number> buffer = new TreeMap<X[], Number>(ArrayHelper
					.<X> getLexicographicalOrder());
			for (int i = 0, n = x0.length; i < n; ++i) {
				final Number value = buffer.get(x0[i]);
				if (value == null) {
					buffer.put(x0[i], 1);
				} else {
					buffer.put(x0[i], value.longValue() + 1);
				}
			}
			for (int i = 0, n = x1.length; i < n; ++i) {
				final Number value = buffer.get(x1[i]);
				if (value == null) {
					buffer.put(x1[i], -1);
				} else {
					buffer.put(x1[i], value.longValue() - 1);
				}
			}
			final List<Term> list = new ArrayList<Term>(buffer.size());
			for (Entry<X[], Number> t : buffer.entrySet()) {
				final Number value = t.getValue();
				if (value.longValue() == 0) {
					continue;
				}
				list.add(new Term(value.longValue(), t.getKey()));
			}
			return list.toArray(Term.EMPTY_ARRAY);
		}
	}

	final static Identity I = new ArrayTestClasses.Identity() {
		public String toString() {
			return "I";
		}
	};
	final static Swap S = new ArrayTestClasses.Swap() {
		public String toString() {
			return "S";
		}
	};
	final static StringMagma M = new ArrayTestClasses.StringMagma() {
		public String toString() {
			return "M";
		}
	};
	final static PrefixCoMagma P = new ArrayTestClasses.PrefixCoMagma("P") {
		public String toString() {
			return "P-";
		}
	};
	final static SuffixCoMagma Q = new ArrayTestClasses.SuffixCoMagma("Q") {
		public String toString() {
			return "-Q";
		}
	};

	protected static String kakko(Object[] array) {
		return "(" + StringHelper.join(array) + ")";
	}
	protected static String sum(Object[][] array) {
		final StringBuilder buffer = new StringBuilder();
		for (int i = 0, n = array.length; i < n; ++i) {
			if (i != 0) {
				buffer.append("+");
			}
			buffer.append(kakko(array[i]));
		}
		return buffer.toString();
	}
	protected static <X extends Comparable<? super X>> String minus(X[][] x0,
			X[][] x1) {
		final Term[] terms = Term.minus(x0, x1);
		final StringBuilder buffer = new StringBuilder();
		for (int i = 0, n = terms.length; i < n; ++i) {
			if (i != 0 && 0 <= terms[i].value) {
				buffer.append("+");
			}
			buffer.append(terms[i]);
		}
		return buffer.toString();
	}
	protected static String[] transform(IfArrayTransform[] transform,
			String[] source) throws Exception {
		int nT = 0;
		for (int i = 0, n = transform.length; i < n; ++i) {
			final IfArrayTransform t = transform[i];
			nT += t.getTargetDegree();
		}
		final String[] target = new String[nT];
		ArrayTestClasses.transform(target, 0, transform, 0, transform.length,
				source, 0);
		return target;
	}
	protected static String[][] transform(IfArrayTransform[][] transform,
			String[][] source) throws Exception {
		final int nF = transform.length;
		final int nS = source.length;
		final int nT = nF * nS;
		final String[][] target = new String[nT][];
		for (int iF = 0; iF < nF; ++iF) {
			for (int iS = 0; iS < nS; ++iS) {
				final int iT = iF * nS + iS;
				target[iT] = ArrayTest.transform(transform[iF], source[iS]);
			}
		}
		return target;
	}
	protected static boolean equals(Object[] x0, Object... x1) {
		return Arrays.equals(x0, x1);
	}

	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
	}
	public void testBasic() throws Exception {
		final String[] x0 = { "a", "b", "c", "d", "e" };
		final IfArrayTransform[] f0 = { M, I, S };
		final String[] x1 = transform(f0, x0);
		Debug.log().info(kakko(f0) + ": " + kakko(x0) + " -> " + kakko(x1));
		Assert.assertTrue(equals(x1, "ab", "c", "e", "d"));
	}
	public void testCoMagma() throws Exception {
		final String[][] x0 = { { "a" } };
		final IfArrayTransform[][] f1 = { { P }, { Q } };
		final String[][] x1 = transform(f1, x0);
		Debug.log().info(sum(f1) + ": " + sum(x0) + " -> " + sum(x1));
		final IfArrayTransform[][] f2 = { { P, I }, { Q, I } };
		final String[][] x2 = transform(f2, x1);
		Debug.log().info(sum(f2) + ": " + sum(x1) + " -> " + sum(x2));
		final IfArrayTransform[][] f3 = { { I, P }, { I, Q } };
		final String[][] x3 = transform(f3, x1);
		Debug.log().info(sum(f3) + ": " + sum(x1) + " -> " + sum(x3));

		/*
		 * (a,P,Q) = (P,a,Q)+([a,P],Q)
		 *  = (P,Q,a)+(P,[a,Q])+([a,P],Q)
		 */
		Debug.log().info(
				sum(f2) + "-" + sum(f3) + ": " + sum(x1) + " -> " + minus(x2, x3));
	}
}
