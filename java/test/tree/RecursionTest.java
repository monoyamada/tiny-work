package tree;

import java.util.Arrays;
import java.util.Random;

import junit.framework.TestCase;

import org.spaceroots.mantissa.random.MersenneTwister;

import tiny.lang.ArrayHelper;
import tiny.lang.Debug;
import tiny.primitive.LongArrayList;

public class RecursionTest extends TestCase {
	static final Random random = new MersenneTwister();

	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
		super.setUp();
	}
	static void sort2(int[] array, int begin) {
		if (array[begin] <= array[begin + 1]) {
			return;
		}
		ArrayHelper.swap(array, begin, begin + 1);
	}
	static int split(int[] array, int begin, int end) {
		int left = begin;
		int right = end - 1;
		int pivot = array[left];
		while (left < right) {
			while ((pivot <= array[right]) && (left < right)) {
				--right;
			}
			if (left != right) {
				array[left++] = array[right];
			}
			while ((array[left] <= pivot) && (left < right)) {
				++left;
			}
			if (left != right) {
				array[right--] = array[left];
			}
		}
		array[left] = pivot;
		// is this OK?
		return begin < left ? left : left + 1;
	}
	static long encode(int begin, int end) {
		long code = begin;
		return (code << 32) | end;
	}
	static int begin(long code) {
		return (int) (code >> 32);
	}
	static int end(long code) {
		return (int) code;
	}
	static void sort(int[] array) {
		sort(array, 0, array.length);
	}
	static void sort(int[] array, int begin, int end) {
		int n = end - begin;
		LongArrayList left = new LongArrayList(n / 2);
		LongArrayList right = new LongArrayList(n / 2);
		OUTER: while (true) {
			n = end - begin;
			if (n < 3) {
				if (n == 2) {
					sort2(array, begin);
				}
				while (0 < left.size()) {
					long code = left.getLast(0);
					if (code < 0) {
						code = encode(begin, end);
						left.addLast(code);
						code = right.getLast(0);
						right.removeLast();
						begin = begin(code);
						end = end(code);
						continue OUTER;
					}
					code = left.getLast(0);
					left.removeLast();
					left.removeLast();
					begin = begin(code);
				}
				return;
			} else {
				left.addLast(-1);
				int mid = split(array, begin, end);
				right.addLast(encode(mid, end));
				end = mid;
			}
		}
	}
	public void testQsort() {
		int[] sorted = new int[1024 * 128];
		ArrayHelper.sequence(sorted, 0);
		int[] array = sorted.clone();
		for (int cnt = 0; cnt < 128; ++cnt) {
			ArrayHelper.shuffle(array, random);
			sort(array);
			assertTrue(Arrays.equals(sorted, array));
		}
	}
}
