package graycode;

import java.io.IOException;
import java.util.Arrays;

import junit.framework.TestCase;
import tiny.lang.ArrayHelper;
import tiny.lang.Debug;
import tiny.number.CompositionHelper;
import tiny.number.NumberComposition;
import tiny.number.NumberPartition;
import tiny.number.ReflexDictionaryComposition;
import tiny.number.ReflexDictionaryPartition;

public class TestPartitions extends TestCase {
	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
		super.setUp();
	}
	public void testReflexDictionary() throws IOException {
		NumberPartition test = new ReflexDictionaryPartition();
		{
			int n = 6;
			for (int k = 1; k <= n; ++k) {
				int[] word = test.first(n, k);
				Debug.log().info("(n=" + n + ", k=" + k + ")" + " -------------");
				do {
					CompositionHelper.writeCsv(System.out, word, "").append("\n");
				} while (test.next(word));
			}
		}
		{
			int n = 9;
			for (int k = 1; k <= n; ++k) {
				int[] word = test.first(n, k);
				Debug.log().info("(n=" + n + ", k=" + k + ")" + " -------------");
				do {
					CompositionHelper.writeCsv(System.out, word, "").append("\n");
				} while (test.next(word));
			}
		}
		{
			int n = 19;
			int k = 11;
			int[] word = test.first(n, k);
			Debug.log().info("(n=" + n + ", k=" + k + ")" + " -------------");
			do {
				CompositionHelper.writeCsv(System.out, word, "").append("\n");
			} while (test.next(word));
		}
	}
	public void testGrayCode() throws IOException {
		{
			int n = 9;
			int k = 3;
			NumberComposition test = new ReflexDictionaryComposition();
			int[] word = test.first(n - k, k);
			Debug.log().info("(n=" + n + ", k=" + k + ")" + " -------------");
			do {
				int[] newWord = word.clone();
				CompositionHelper.plus(newWord, 1);
				Arrays.sort(newWord);
				ArrayHelper.reverse(newWord);
				CompositionHelper.writeCsv(System.out, newWord, "").append("\n");
			} while (test.next(word));
		}
	}
	public void testReflexDictionary1() throws IOException {
		NumberPartition test = new ReflexDictionaryPartition();
		for (int n = 3; n < 10; ++n) {
			Debug.log().info("(n=" + n + " -------------");
			for (int k = 1; k <= n; ++k) {
				int[] word = test.first(n, k);
				do {
					CompositionHelper.writeCsv(System.out, word, "").append("\n");
				} while (test.next(word));
			}
		}
	}
}
