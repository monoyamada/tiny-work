package parser.v4;

import java.util.Arrays;

import junit.framework.Assert;
import tiny.lang.Debug;
import tiny.lang.StringHelper;
import base.TestBase;

public class FibTest extends TestBase {
	static long fib(int n) {
		if (n < 0) {
			String msg = "n must be ge than 0";
			throw new IllegalArgumentException(msg);
		}
		if (n <= 1) {
			return n;
		}
		long a = 0;
		long b = 1;
		long c = 1;
		long d = 1;
		while (2 < n--) {
			long tmp = b;
			b += a;
			a = tmp;
			tmp = d;
			d += c;
			c = tmp;
		}
		return d;
	}
	public void testFib() {
		final long[] A000045 = { 0, 1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144, 233,
				377, 610, 987, 1597, 2584, 4181, 6765, 10946, 17711, 28657, 46368,
				75025, 121393, 196418, 317811, 514229, 832040, 1346269, 2178309,
				3524578, 5702887, 9227465, 14930352, 24157817, 39088169 };
		final int N = A000045.length;
		long[] as = new long[N];
		for (int i = 0; i < N; ++i) {
			as[i] = fib(i);
		}
		Assert.assertTrue("fibonacci", Arrays.equals(A000045, as));
	}
}
