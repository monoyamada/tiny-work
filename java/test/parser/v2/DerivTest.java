package parser.v2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import tiny.function.Function;
import tiny.function.LexicographicalOrders;
import tiny.lang.ArrayHelper;
import tiny.lang.Debug;
import tiny.lang.StringHelper;
import tiny.primitive.IntArrayList;
import junit.framework.TestCase;

public class DerivTest extends TestCase {
	private static final String EMPTY_STRING = new String();

	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
		super.setUp();
	}
	static String[][] dycks(int deg) {
		String[][] output = new String[deg + 1][];
		output[0] = new String[] { EMPTY_STRING };
		List<String> buffer = new ArrayList<String>(64);
		for (int n = 0; n < deg; ++n) {
			buffer.clear();
			for (int k = 0; k <= n; ++k) {
				String[] xs = output[k];
				String[] ys = output[n - k];
				for (String x : xs) {
					for (String y : ys) {
						buffer.add('b' + x + 'c' + y);
					}
				}
			}
			output[n + 1] = buffer.toArray(ArrayHelper.EMPTY_STRING_ARRAY);
		}
		return output;
	}
	static int[][][] derivs(int deg) {
		int[][][] output = new int[deg + 1][][];
		output[0] = new int[][] { new int[] { 0 } };
		List<int[]> buffer = new ArrayList<int[]>(64);
		for (int n = 0; n < deg; ++n) {
			buffer.clear();
			for (int k = 0; k <= n; ++k) {
				int[][] xs = output[k];
				int[][] ys = output[n - k];
				for (int[] x : xs) {
					for (int[] y : ys) {
						int[] z = ArrayHelper.addAll(x, y);
						z[z.length - 1] += 1;
						buffer.add(z);
					}
				}
			}
			output[n + 1] = new int[buffer.size()][];
			buffer.toArray(output[n + 1]);
		}
		return output;
	}
	public void testDycks() {
		String[][] xs = dycks(4);
		for (int i = 0, n = xs.length; i < n; ++i) {
			Debug.log().debug(StringHelper.join(xs[i], ", "));
		}
	}

	static class SortedIntArray implements Cloneable, Comparable<SortedIntArray> {
		int[] array;

		public SortedIntArray clone() {
			SortedIntArray that = null;
			try {
				that = (SortedIntArray) super.clone();
			} catch (CloneNotSupportedException ex) {
				that = new SortedIntArray();
			}
			that.array = this.array.clone();
			return that;
		}
		SortedIntArray set(int[] xs) {
			if (this.array == null || xs.length != this.array.length) {
				this.array = xs.clone();
			} else {
				System.arraycopy(xs, 0, this.array, 0, xs.length);
			}
			Arrays.sort(this.array);
			return this;
		}
		@Override
		public int compareTo(SortedIntArray x) {
			return LexicographicalOrders.compare(this.array, x.array);
		}
		public String toString() {
			return "[" + StringHelper.join(this.array, ", ") + "]";
		}
	}

	static class MutalbeInteger {
		int value;

		MutalbeInteger(int value) {
			this.value = value;
		}
		public String toString() {
			return Integer.toString(this.value);
		}
	}

	public void testDerivs() {
		int[][][] xs = derivs(4);
		for (int i = 0, n = xs.length; i < n; ++i) {
			System.out.println("E_" + i + " = "
					+ StringHelper.join(xs[i], " + ", new Function<int[], String>() {
						@Override
						public String evaluate(int[] source) throws Exception {
							return "[" + StringHelper.join(source, ", ") + "]";
						}
					}));
		}
		SortedIntArray tmp = new SortedIntArray();
		for (int i = 0, n = xs.length; i < n; ++i) {
			int[][] x = xs[i];
			Map<SortedIntArray, MutalbeInteger> ys = new TreeMap<SortedIntArray, MutalbeInteger>();
			for (int ii = 0, nn = x.length; ii < nn; ++ii) {
				tmp.set(x[ii]);
				MutalbeInteger val = ys.get(tmp);
				if (val == null) {
					ys.put(tmp.clone(), new MutalbeInteger(1));
				} else {
					val.value += 1;
				}
			}
			Debug.log().debug(ys);
		}
	}
}
