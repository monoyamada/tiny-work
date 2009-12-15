package study.lang;

import junit.framework.TestCase;
import static study.lang.ArrayHelper.*;

public class ArrayHelperTest extends TestCase {
	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
		super.setUp();
	}
	public void test_getLowerBound() {
		{
			final int[] array = {};
			int index = getLowerBound(array, 0);
			assertEquals(0, index);
		}
		{
			final int[] array = {1};
			int index = getLowerBound(array, 0);
			assertEquals(0, index);
			index = getLowerBound(array, 1);
			assertEquals(0, index);
			index = getLowerBound(array, 2);
			assertEquals(1, index);
		}
		{
			final int[] array = { 1, 2, 3 };
			int index = getLowerBound(array, 0);
			assertEquals(0, index);
			index = getLowerBound(array, 1);
			assertEquals(0, index);
			index = getLowerBound(array, 2);
			assertEquals(1, index);
			index = getLowerBound(array, 3);
			assertEquals(2, index);
			index = getLowerBound(array, 4);
			assertEquals(3, index);
		}
		{
			final int[] array = { 1, 1, 2, 2, 2, 3, 3, 3, 3 };
			int index = getLowerBound(array, 0);
			assertEquals(0, index);
			index = getLowerBound(array, 1);
			assertEquals(0, index);
			index = getLowerBound(array, 2);
			assertEquals(2, index);
			index = getLowerBound(array, 3);
			assertEquals(5, index);
			index = getLowerBound(array, 4);
			assertEquals(9, index);
		}
	}
}
