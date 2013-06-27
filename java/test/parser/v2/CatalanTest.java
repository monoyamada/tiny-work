package parser.v2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import junit.framework.TestCase;
import tiny.function.Function;
import tiny.lang.ArrayHelper;
import tiny.lang.Debug;
import tiny.lang.StringHelper;

public class CatalanTest extends TestCase {
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
}
