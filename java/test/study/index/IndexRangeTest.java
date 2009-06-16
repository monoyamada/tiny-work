package study.index;

import junit.framework.TestCase;
import study.lang.Debug;
import study.lang.NumberHelper;

public class IndexRangeTest extends TestCase {
	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
	}
	public static String pair(int left, int right) {
		return "(" + left + ", " + right + ")";
	}
	public static String range(long range) {
		return "[" + IndexRnageClasses.begin(range) + ", "
				+ IndexRnageClasses.end(range) + ")";
	}
	public void testNumber() {
		int left = 3;
		int right = 7;
		long pair = NumberHelper.pairing(left, right);
		Debug.log().info(
				pair(left, right) + " -> " + pair + " -> "
						+ pair(NumberHelper.left(pair), NumberHelper.right(pair)));
	}
	public void testAnd() {
		{
			long r1 = IndexRnageClasses.range(2, 4);
			long r2 = IndexRnageClasses.range(3, 5);
			long r = IndexRnageClasses.and(r1, r2);
			Debug.log().info(range(r1) + "*" + range(r2) + "=" + range(r));
			assertEquals(3, IndexRnageClasses.begin(r));
			assertEquals(4, IndexRnageClasses.end(r));
		}
		{
			long r1 = IndexRnageClasses.range(2, 4);
			long r2 = IndexRnageClasses.range(4, 5);
			long r = IndexRnageClasses.and(r1, r2);
			Debug.log().info(range(r1) + "*" + range(r2) + "=" + range(r));
			assertEquals(0, IndexRnageClasses.begin(r));
			assertEquals(0, IndexRnageClasses.end(r));
		}
	}
}
