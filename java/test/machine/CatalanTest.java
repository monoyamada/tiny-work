package machine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import junit.framework.TestCase;
import tiny.function.LexicographicalOrders;
import tiny.lang.ArrayHelper;
import tiny.lang.Debug;
import tiny.lang.StringHelper;

public class CatalanTest extends TestCase {
	static long[] q(int q) {
		long[] array = new long[q];
		Arrays.fill(array, 1);
		return array;
	}
	static long[] plus(long[] x, long[] y) {
		int n = Math.max(x.length, y.length);
		long[] z = new long[n];
		System.arraycopy(x, 0, z, 0, x.length);
		for (n = y.length; 0 < n--;) {
			z[n] += y[n];
		}
		return z;
	}
	static long[] multiplies(long[] x, long[] y) {
		int n = x.length + y.length - 1;
		long[] z = new long[n];
		for (int nx = x.length; 0 < nx--;) {
			for (int ny = y.length; 0 < ny--;) {
				z[nx + ny] += x[nx] * y[ny];
			}
		}
		return z;
	}
	protected void setUp() throws Exception {
		super.setUp();
		Debug.setLogLevel("debug");
	}
	public void test1() {
		long[] x = ArrayHelper.EMPTY_LONG_ARRAY;
		for (int i = 1; i <= 4; ++i) {
			x = plus(x, q(i));
		}
		Debug.log().debug(StringHelper.join(x));
		Debug.log().debug(StringHelper.join(multiplies(x, q(3))));
	}
	public void test2() {
		final int N = 10;
		long[][] qs = new long[N][];
		for (int n = N; 0 < n--;) {
			qs[n] = q(n + 1);
		}
		int K = (N * (N - 1)) >> 1;
		long[][] old = new long[N][];
		Arrays.fill(old, new long[] { 1 });
		long[][] now = old.clone();
		for (int k = 0; k <= K; ++k) {
			System.out.print(" & $q^{" + k + "}$");
		}
		System.out.println(" \\\\\\hline");
		long C = 1; // cataln C(n+1) = (4n + 2)C(n)/(n+2)
		long D = 1; // D(n+1) = (2n + 1)D(n)
		for (int i = 1; i < N; ++i) {
			now[0] = plus(old[0], multiplies(qs[1], old[1]));
			if (true) {
				C *= 4 * i + 2;
				C /= i + 2;
				D *= 2 * i + 1;
				// Debug.log().debug(f + "! = " + F);
				// Debug.log().debug(StringHelper.join(now[0]));
				// q=0
				long c = now[0][0];
				assertEquals(C, c);
				// q=1
				c = 0;
				for (int j = 0; j < now[0].length; ++j) {
					c += now[0][j];
				}
				assertEquals(D, c);
				// q=-1
				c = 0;
				for (int j = 0; j < now[0].length; ++j) {
					if ((j & 1) == 0) {
						c += now[0][j];
					} else {
						c -= now[0][j];
					}
				}
				assertEquals(1, c);
			}
			int n = N - i;
			for (int j = 1; j < n; ++j) {
				now[j] = plus(now[j - 1], multiplies(qs[j + 1], old[j + 1]));
			}
			for (int j = 0; j < n; ++j) {
				System.out.print("$C_{" + i + ", " + (j + 1) + "}$");
				for (int k = 0; k < now[j].length; ++k) {
					System.out.print(" & " + now[j][k]);
				}
				for (int k = now[j].length; k <= K; ++k) {
					System.out.print(" &");
				}
				if (j + 1 < n) {
					System.out.println(" \\\\");
				} else {
					System.out.println(" \\\\\\hline");
				}
			}
			long[][] tmp = old;
			old = now;
			now = tmp;
		}
	}
	public void test3() {
		final int n = 10;
		List<int[]> buffer = new ArrayList<int[]>(1024);
		for (int r = 0; r <= 2 * n; ++r) {
			for (int s = 0; s <= Math.min(r, 2 * n - r); ++s) {
				buffer.add(new int[] { s, r - s, 2 * n - r - s });
				// Debug.log().debug(s + ", " + (r - s) + ", " + (2 * n - r - s));
			}
		}
		int[][] array = buffer.toArray(new int[buffer.size()][]);
		Arrays.sort(array, new Comparator<int[]>() {
			@Override
			public int compare(int[] o1, int[] o2) {
				return LexicographicalOrders.compare(o1, 0, o2, 0, 3);
			}
		});
		for (int i = 0; i < array.length; ++i) {
			Debug.log().debug(StringHelper.join(array[i]));
		}
	}

	static class MatrixValue {
		StringBuilder buffer;
		int count;
		public MatrixValue(char value, int count) {
			this.appendValue(value);
			this.
		}
		public static MatrixValue x(char value, int count) {
			return new MatrixValue(value, count);
		}
	}

	public void test4() {
		MatrixValue[][] matrix = new MatrixValue[][] {
				{ MatrixValue.x('b', 1), MatrixValue.x('a', 0) },
				{ null, MatrixValue.x('c', -1) }

		};
	}
}
