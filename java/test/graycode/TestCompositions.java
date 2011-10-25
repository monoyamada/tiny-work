package graycode;

import java.io.IOException;

import tiny.lang.Debug;
import tiny.number.CompositionHelper;
import tiny.number.DictionaryComposition;
import tiny.number.GrayCodeComposition;
import tiny.number.ReflexDictionaryComposition;
import junit.framework.TestCase;

public class TestCompositions extends TestCase {
	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
		super.setUp();
	}
	public void testDictionary() throws IOException {
		DictionaryComposition test = new DictionaryComposition();
		{
			int n = 3;
			int k = 2;
			int size = (int) test.size(n, k);
			int[] value = test.first(n, k);
			Debug.log().info("(n=" + n + ", k=" + k + ")" + " -------------");
			for (int i = 0; i < size; ++i) {
				CompositionHelper.writeCsv(System.out, value, "").append("\n");
				test.next(value);
			}
		}
		{
			int n = 3;
			int k = 4;
			int size = (int) test.size(n, k);
			int[] value = test.first(n, k);
			Debug.log().info("(n=" + n + ", k=" + k + ")" + " -------------");
			for (int i = 0; i < size; ++i) {
				CompositionHelper.writeCsv(System.out, value, "").append("\n");
				test.next(value);
			}
		}
		{
			int n = 3;
			int k = 3;
			int size = (int) test.size(n, k);
			int[] value = test.first(n, k);
			Debug.log().info("(n=" + n + ", k=" + k + ")" + " -------------");
			for (int i = 0; i < size; ++i) {
				CompositionHelper.writeCsv(System.out, value, "").append("\n");
				test.next(value);
			}
			Debug.log().info(
					"reverse " + "(n=" + n + ", k=" + k + ")" + " -------------");
			for (int i = 0; i < size; ++i) {
				CompositionHelper.writeCsv(System.out, value, "").append("\n");
				test.prev(value);
			}
		}
		{
			int n = 3;
			int k = 4;
			int size = (int) test.size(n, k);
			int[] value = test.first(n, k);
			Debug.log().info("(n=" + n + ", k=" + k + ")" + " -------------");
			for (int i = 0; i < size; ++i) {
				CompositionHelper.writeCsv(System.out, value, "").append("\n");
				test.nextRecursive(value);
			}
		}
		if (0 < "".length()) {
			int n = 3;
			int k = 4;
			int[][] values = test.listRecursive(n, k);
			Debug.log().info("(n=" + n + ", k=" + k + ")" + " -------------");
			for (int i = 0; i < values.length; ++i) {
				int[] value = values[i];
				CompositionHelper.writeCsv(System.out, value, "").append("\n");
				test.prev(value);
			}
		}
	}
	public void testReflexDictionary() throws IOException {
		DictionaryComposition test = new ReflexDictionaryComposition();
		{
			int n = 4;
			int k = 2;
			int size = (int) test.size(n, k);
			int[] value = test.first(n, k);
			Debug.log().info("(n=" + n + ", k=" + k + ")" + " -------------");
			for (int i = 0; i < size; ++i) {
				CompositionHelper.writeCsv(System.out, value, "").append("\n");
				test.next(value);
			}
			Debug.log().info(
					"reverse " + "(n=" + n + ", k=" + k + ")" + " -------------");
			for (int i = 0; i < size; ++i) {
				CompositionHelper.writeCsv(System.out, value, "").append("\n");
				test.prev(value);
			}
		}
		{
			int n = 4;
			int k = 3;
			int size = (int) test.size(n, k);
			int[] value = test.first(n, k);
			Debug.log().info("(n=" + n + ", k=" + k + ")" + " -------------");
			for (int i = 0; i < size; ++i) {
				CompositionHelper.writeCsv(System.out, value, "").append("\n");
				test.next(value);
			}
			Debug.log().info(
					"reverse " + "(n=" + n + ", k=" + k + ")" + " -------------");
			for (int i = 0; i < size; ++i) {
				CompositionHelper.writeCsv(System.out, value, "").append("\n");
				test.prev(value);
			}
		}
		{
			int n = 3;
			int k = 4;
			int size = (int) test.size(n, k);
			int[] value = test.first(n, k);
			Debug.log().info("(n=" + n + ", k=" + k + ")" + " -------------");
			for (int i = 0; i < size; ++i) {
				CompositionHelper.writeCsv(System.out, value, "").append("\n");
				test.next(value);
			}
			Debug.log().info(
					"reverse " + "(n=" + n + ", k=" + k + ")" + " -------------");
			for (int i = 0; i < size; ++i) {
				CompositionHelper.writeCsv(System.out, value, "").append("\n");
				test.prev(value);
			}
		}
	}

	static class Test1 extends ReflexDictionaryComposition {
		public boolean next(int[] value) {
			return next(value, 0, value.length);
		}
		public boolean next(int[] value, int begin, int end) {
			if (end < begin + 2) {
				return false;
			}
			return next(value, 0, begin, end);
		}
		private boolean next(int[] value, int begin, int index, int end) {
			if (end <= index) {
				throw new Error();
			} else if (end == index + 1) {
				return false;
			} else if (0 < value[index]) {
				value[index + 1] += 1;
				if (begin == index) {
					value[index] -= 1;
				} else if (0 < value[index]) {
					value[begin] += value[index] - 1;
					value[index] = 0;
				}
				return true;
			}
			return next(value, begin, index + 1, end);
		}
	}

	public void testGrayCode() throws IOException {
		DictionaryComposition test = new GrayCodeComposition();
		{
			int n = 3;
			int k = 2;
			int size = (int) test.size(n, k);
			int[] value = test.first(n, k);
			Debug.log().info("(n=" + n + ", k=" + k + ")" + " -------------");
			for (int i = 0; i < size; ++i) {
				CompositionHelper.writeCsv(System.out, value, "").append("\n");
				test.next(value);
			}
		}
		{
			int n = 3;
			int k = 4;
			int size = (int) test.size(n, k);
			int[] value = test.first(n, k);
			Debug.log().info("(n=" + n + ", k=" + k + ")" + " -------------");
			for (int i = 0; i < size; ++i) {
				CompositionHelper.writeCsv(System.out, value, "").append("\n");
				test.next(value);
			}
			Debug.log().info(
					"reverse " + "(n=" + n + ", k=" + k + ")" + " -------------");
			for (int i = 0; i < size; ++i) {
				CompositionHelper.writeCsv(System.out, value, "").append("\n");
				test.prev(value);
			}
		}
		{
			int n = 3;
			int k = 3;
			int size = (int) test.size(n, k);
			int[] value = test.first(n, k);
			Debug.log().info("(n=" + n + ", k=" + k + ")" + " -------------");
			for (int i = 0; i < size; ++i) {
				CompositionHelper.writeCsv(System.out, value, "").append("\n");
				test.next(value);
			}
			Debug.log().info(
					"reverse " + "(n=" + n + ", k=" + k + ")" + " -------------");
			for (int i = 0; i < size; ++i) {
				CompositionHelper.writeCsv(System.out, value, "").append("\n");
				test.prev(value);
			}
		}
		if (0 < "".length()) {
			int n = 3;
			int k = 4;
			int[][] values = test.listRecursive(n, k);
			Debug.log().info("(n=" + n + ", k=" + k + ")" + " -------------");
			for (int i = 0; i < values.length; ++i) {
				int[] value = values[i];
				CompositionHelper.writeCsv(System.out, value, "").append("\n");
				test.prev(value);
			}
		}
	}

	public void testTest1() throws IOException {
		DictionaryComposition test = new Test1();
		{
			int n = 3;
			int k = 4;
			int size = (int) test.size(n, k);
			int[] value = test.first(n, k);
			Debug.log().info("(n=" + n + ", k=" + k + ")" + " -------------");
			for (int i = 0; i < size; ++i) {
				CompositionHelper.writeCsv(System.out, value, "").append("\n");
				test.next(value);
			}
			Debug.log().info(
					"reverse " + "(n=" + n + ", k=" + k + ")" + " -------------");
			for (int i = 0; i < size; ++i) {
				CompositionHelper.writeCsv(System.out, value, "").append("\n");
				test.prev(value);
			}
		}
	}
	static long power(int[] value) {
		int n = value.length;
		double x = Math.pow(n, value[n - 1]);
		while (1 < n--) {
			x *= Math.pow(n, value[n - 1]);
		}
		return (long) Math.round(x);
	}
	public void testStirling2nd() throws IOException {
		DictionaryComposition test = new DictionaryComposition();
		int N = 10;
		for (int n = 1; n < N; ++n) {
			for (int k = 1; k <= n; ++k) {
				int[] value = test.first(n-k, k);
				long sum = power(value);
				while(test.next(value)){
					sum += power(value);
				}
				System.out.print(" " + sum);
			}
			System.out.println();
		}
	}
}
