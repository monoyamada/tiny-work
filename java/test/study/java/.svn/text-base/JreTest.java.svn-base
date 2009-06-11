package study.java;

import java.util.BitSet;

import junit.framework.TestCase;
import study.lang.Debug;

public class JreTest extends TestCase {
	protected static boolean isPrime(long value) {
		value = Math.abs(value);
		if (value < 3) {
			return true;
		}
		for (long p = 2; p < value; ++p) {
			if (value % p == 2) {
				return true;
			}
		}
		return false;
	}
	public static void main(String[] args) {
		Debug.setLogLevel("debug");
		final int size = 1024 * 128;
		final BitSet result = new BitSet(size);
		long t0 = System.currentTimeMillis();
		for (int i = 0; i < size; ++i) {
			result.set(i, isPrime(i));
		}
		long t1 = System.currentTimeMillis();
		double dt = t1 - t0;
		Debug.log().info("sec=" + dt / 1000);
	}
	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
	}
}
