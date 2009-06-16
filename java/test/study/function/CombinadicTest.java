package study.function;

import static study.lang.ArrayHelper.nextPermution;
import static study.lang.ArrayHelper.prevPermution;
import static study.lang.ArrayHelper.reverse;
import static study.lang.ObjectHelper.factorial;
import static study.lang.StringHelper.join;

import java.util.Arrays;

import junit.framework.Assert;
import junit.framework.TestCase;
import study.lang.ArrayHelper;
import study.lang.Debug;

public class CombinadicTest extends TestCase {
	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
	}
	public void testPermutation() {
		final long[] array = { 0, 1, 2 };
		int count = 0;
		Debug.log().info(count + ": " + join(array, ", "));
		for (++count; nextPermution(array, 0, array.length); ++count) {
			Debug.log().info(count + ": " + join(array, ", "));
		}
		Assert.assertEquals(factorial(array.length), count);

		reverse(array);
		for (--count; prevPermution(array, 0, array.length); --count) {
			Debug.log().info((count - 1) + ": " + join(array, ", "));
		}
		Assert.assertEquals(0, count);

		final String[] tokens = { "a", "b", "c" };
		count = 0;
		Debug.log().info(count + ": " + join(array, ", "));
		for (++count; nextPermution(tokens, 0, tokens.length); ++count) {
			Debug.log().info(count + ": " + join(tokens, ", "));
		}
		Assert.assertEquals(factorial(tokens.length), count);

		reverse(tokens);
		for (--count; prevPermution(tokens, 0, tokens.length); --count) {
			Debug.log().info((count - 1) + ": " + join(tokens, ", "));
		}
		Assert.assertEquals(0, count);
	}
	public void testCombination() {
		final int setSize = 4;
		final int chooseSize = 2;
		final int[] array = new int[chooseSize];
		final int size = Combinadic.getCombinationSize(setSize, chooseSize);
		Assert.assertEquals(4 * 3 / 2, size);
		for (int i = 0; i < size; ++i) {
			Arrays.fill(array, -1);
			Combinadic.getCombination(array, setSize, chooseSize, i);
			Debug.log().info(join(array, ", "));
		}
	}
	public void testInterval() {
		final String[] a = { "a0", "a1" };
		final String[] b = { "b0", "b1" };
		Debug.log().info("a = " + join(a, " <= "));
		Debug.log().info("b = " + join(b, " <= "));
		final String[][] ab = Combinadic.shuffleProduct(a, b);
		for (int i = 0, n = ab.length; i < n; ++i) {
			final String[] term = ab[i];
			final int[] ia = new int[2];
			final int[] ib = new int[2];
			for (int k = 0; k < 2; ++k) {
				ia[k] = ArrayHelper.indexOf(term, a[k]);
				ib[k] = ArrayHelper.indexOf(term, b[k]);
			}
			Debug.log().info(join(term, " <= "));
		}
	}
}
