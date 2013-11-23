package parser.v4;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import junit.framework.Assert;

import org.spaceroots.mantissa.random.MersenneTwister;

import tiny.lang.ArrayHelper;
import tiny.lang.Debug;
import tiny.lang.StringHelper;
import base.TestBase;

public class SortTest extends TestBase {
	static int partition(long[] word, int begin, int end, long pivot) {
		while (begin < end) {
			while (word[begin] < pivot) {
				if (++begin == end) {
					return begin;
				}
			}
			do {
				if (--end == begin) {
					return begin;
				}
			} while (pivot <= word[end]);
			ArrayHelper.swap(word, begin, end);
			++begin;
		}
		return begin;
	}
	static int partition(long[] word, int begin, int end) {
		if (end < begin + 2) {
			throw new Error("length must be ge than 0 but " + (end - begin));
		}
		int right = partition(word, begin + 1, end, word[begin]);
		ArrayHelper.swap(word, begin, right - 1);
		return right < end ? right : right - 1;
	}

	static long[] sort(long[] word) {
		if (word.length < 2) {
			return word;
		}
		return sort(word, 0, word.length);
	}
	static long[] sort(long[] word, int begin, int end) {
		if (end - begin < 2) {
			return word;
		}
		int right = partition(word, begin, end);
		sort(word, begin, right);
		return sort(word, right, end);
	}

	static int[] ensureSize(int[] word, int size, int maxSize) {
		if (size <= word.length) {
			return word;
		}
		int[] array = new int[Math.min(word.length << 1, maxSize)];
		System.arraycopy(word, 0, array, 0, word.length);
		return array;
	}
	static long[] sortInline(long[] word) {
		if (word.length < 2) {
			return word;
		}
		int capacity = (32 - Integer.numberOfLeadingZeros(word.length)) << 1;
		int[] stack = new int[capacity];
		int top = 0;
		stack[top] = word.length;
		stack[++top] = 0;
		do {
			int begin = stack[top];
			int end = stack[top - 1];
			while (begin + 1 < end) {
				stack[top] = end = partition(word, begin, end);
				stack = ensureSize(stack, top + 2, word.length);
				stack[++top] = begin;
			}
		} while (0 < --top);
		return word;
	}
	static long[] sortInline(long[] word, int[] stack) {
		int top = 0;
		stack[top] = word.length;
		stack[++top] = 0;
		do {
			int begin = stack[top];
			int end = stack[top - 1];
			while (begin + 1 < end) {
				stack[top] = end = partition(word, begin, end);
				stack[++top] = begin;
			}
		} while (0 < --top);
		return word;
	}

	static class SortProgram {
		static final int L0 = 0;
		static final int L1 = 1;
		static final int L2 = 2;
		static final int L3 = 3;
		static final int L4 = 4;

		long[] sort(long[] word) {
			int capacity = (32 - Integer.numberOfLeadingZeros(word.length)) << 1;
			int[] indices = new int[capacity];
			int[] calls = new int[capacity];
			int top = 0;
			indices[top] = 0;
			indices[++top] = word.length;
			int callTop = 0;
			calls[callTop] = L4;
			int label = L0;
			int begin = 0;
			int end = 0;
			while (label != L4) {
				switch (label) {
				case L0:
					begin = indices[top - 1];
					end = indices[top];
					while (begin + 1 < end) {
						indices[top] = begin = partition(word, begin, end);
						indices = ensureSize(indices, ++top + 1, word.length);
						indices[top] = end;
						calls = ensureSize(calls, ++callTop + 1, word.length);
						calls[callTop] = L1;
					}
					--top;
					label = L3;
				break;
				case L1:
					calls = ensureSize(calls, ++callTop + 1, word.length);
					calls[callTop] = L2;
					label = L0;
				break;
				case L2:
				case L3:
					if (callTop == 0) {
						label = L4;
					} else {
						label = calls[callTop--];
					}
				break;
				default:
					throw new Error("unexpected label = " + label);
				}
			}
			return word;
		}
	}

	static long[] randomWord(int length) {
		return randomWord(length, 127);
	}
	static long[] randomWord(int length, long seed) {
		Random random = new MersenneTwister();
		random.setSeed(seed);
		long[] word = new long[length];
		while (0 < length--) {
			word[length] = random.nextLong();
		}
		return word;
	}

	static StringBuilder buffer = new StringBuilder();

	static String toString(long[] word) throws IOException {
		buffer.setLength(0);
		return StringHelper.join(buffer.append('['), word, ", ").append(']')
				.toString();
	}

	public void testSortSpeed() {
		Random random = new MersenneTwister();
		SortProgram prg = new SortProgram();
		final boolean MACHINE = true;
		int n = 1 << 20;
		long[] word_0 = new long[n];
		long[] word_1 = word_0.clone();
		long[] word_2 = word_1.clone();
		long[] word_3 = word_1.clone();
		long[] word_4 = word_1.clone();
		int[] stack = new int[n];
		long t0 = 0;
		long t1 = 0;
		long t2 = 0;
		long t3 = 0;
		long t4 = 0;
		final int N = 20;
		for (int i = 0; i < N; ++i) {
			for (int nn = n; 0 < nn--;) {
				word_0[nn] = random.nextLong();
			}
			System.arraycopy(word_0, 0, word_1, 0, n);
			System.arraycopy(word_0, 0, word_2, 0, n);
			System.arraycopy(word_0, 0, word_3, 0, n);
			System.arraycopy(word_0, 0, word_4, 0, n);
			long start = 0;
			{
				start = System.currentTimeMillis();
				Arrays.sort(word_0);
				t0 += System.currentTimeMillis() - start;
			}
			{
				start = System.currentTimeMillis();
				sort(word_1);
				t1 += System.currentTimeMillis() - start;
			}
			{
				start = System.currentTimeMillis();
				sortInline(word_2);
				t2 += System.currentTimeMillis() - start;
			}
			{
				start = System.currentTimeMillis();
				sortInline(word_3, stack);
				t3 += System.currentTimeMillis() - start;
			}
			if (MACHINE) {
				start = System.currentTimeMillis();
				prg.sort(word_4);
				t4 += System.currentTimeMillis() - start;
			}
			Assert.assertTrue("recursive", Arrays.equals(word_0, word_1));
			Assert.assertTrue("non-recursive", Arrays.equals(word_0, word_2));
			Assert.assertTrue("non-recursive-stack", Arrays.equals(word_0, word_3));
			if (MACHINE) {
				Assert.assertTrue("machine", Arrays.equals(word_0, word_4));
			}
		}
		Debug.log().debug(
				"java.runtime.version=" + System.getProperty("java.runtime.version"));
		Debug.log()
				.debug("Arrays.sort        " + t0 + " [ms] for " + n + " x " + N);
		Debug.log()
				.debug("recursion          " + t1 + " [ms] for " + n + " x " + N);
		Debug.log()
				.debug("inline-new-stack   " + t2 + " [ms] for " + n + " x " + N);
		Debug.log()
				.debug("inline-given-stack " + t3 + " [ms] for " + n + " x " + N);
		if (MACHINE) {
			Debug.log().debug(
					"machine            " + t4 + " [ms] for " + n + " x " + N);
		}
	}

	public void testSortProgram() throws IOException {
		SortProgram prg = new SortProgram();
		if (true) {
			long[] word = { 2, 1 };
			word = prg.sort(word);
			Debug.log().debug(toString(word));
		}
		if (true) {
			long[] word = { 3, 2, 1 };
			word = prg.sort(word);
			Debug.log().debug(toString(word));
		}
		if (true) {
			long[] word = { 1, 1, 1 };
			word = prg.sort(word);
			Debug.log().debug(toString(word));
		}
		if (true) {
			int n = 1 << 20;
			long[] word_0 = randomWord(n);
			long[] word_1 = word_0.clone();
			Arrays.sort(word_0);
			word_1 = prg.sort(word_1);
			Assert.assertTrue("program", Arrays.equals(word_0, word_1));
		}
	}
}
