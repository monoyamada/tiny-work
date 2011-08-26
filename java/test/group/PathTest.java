package group;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

import junit.framework.TestCase;
import tiny.function.LexicographicalOrders;
import tiny.lang.Debug;

public class PathTest extends TestCase {
	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
		super.setUp();
	}
	public static void test1() throws IOException {
		long[][] group = {
				encodeIndices(1, 1, 2, 2, 3, 3) //
				, encodeIndices(1, 2, 2, 1, 3, 3), encodeIndices(2, 3, 3, 2, 1, 1),
				encodeIndices(3, 1, 1, 3, 2, 2) //
				, encodeIndices(1, 2, 2, 3, 3, 1), encodeIndices(1, 3, 2, 1, 3, 2) };
		Arrays.sort(group, codeComparator);
		if (false) {
			for (int i = 0, n = group.length; i < n; ++i) {
				Debug.log().debug(toString(group[i]));
			}
		}
		if (true) {
			long[] tmp = new long[3];
			for (int i = 0, n = group.length; i < n; ++i) {
				for (int j = 0; j < n; ++j) {
					int size = multiplies(tmp, group[i], group[j]);
					Debug.log().debug(
							toString(group[i]) + " * " + toString(group[j]) + " = "
									+ toString(tmp, size));
				}
			}
		}
	}

	static Comparator<long[]> codeComparator = new Comparator<long[]>() {
		@Override
		public int compare(long[] o1, long[] o2) {
			return LexicographicalOrders.compare(o1, o2);
		}
	};

	static long[] encodeIndices(int... indices) {
		int n = indices.length >> 1;
		long[] pairs = new long[n];
		for (int i = 0; i < n; ++i) {
			int j = i << 1;
			pairs[i] = encode(indices[j], indices[j + 1]);
		}
		Arrays.sort(pairs);
		return pairs;
	}
	static long encode(long i, int j) {
		return (i << 32) | j;
	}
	static int decode0(long code) {
		return (int) (code >> 32);
	}
	static int decode1(long code) {
		return (int) code;
	}
	static String toString(long[] code) throws IOException {
		return toString(code, code.length);
	}
	static String toString(long[] code, int n) throws IOException {
		StringBuilder buffer = new StringBuilder();
		toString(buffer, code, n);
		return buffer.toString();
	}
	static void toString(Appendable output, long[] code, int n)
			throws IOException {
		for (int i = 0; i < n; ++i) {
			if (i != 0) {
				// output.append(' ');
				// output.append('+');
				// output.append(' ');
			}
			toString(output, code[i]);
		}
	}
	static String toString(long code) throws IOException {
		StringBuilder buffer = new StringBuilder();
		toString(buffer, code);
		return buffer.toString();
	}
	static void toString(Appendable output, long code) throws IOException {
		output.append('(');
		output.append(Integer.toString(decode0(code)));
		output.append(',');
		// output.append(' ');
		output.append(Integer.toString(decode1(code)));
		output.append(')');
	}
	static long multiplies(long x1, long x2) {
		if (decode1(x1) == decode0(x2)) {
			return encode(decode0(x1), decode1(x2));
		}
		return -1;
	}
	public static int multiplies(long[] output, long[] code1, long[] code2) throws IOException {
		int size = 0;
		for (int i = 0, m = code1.length; i < m; ++i) {
			long x = code1[i];
			for (int j = 0, n = code2.length; j < n; ++j) {
				long y = multiplies(x, code2[j]);
				if (0 <= y) {
					output[size++] = y;
				}
			}
		}
		return size;
	}
}
