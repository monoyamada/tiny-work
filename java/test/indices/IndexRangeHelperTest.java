package indices;

import static indices.IndexRangeHelper.EMPTY_RANGE;
import static indices.IndexRangeHelper.and;
import static indices.IndexRangeHelper.begin;
import static indices.IndexRangeHelper.end;
import static indices.IndexRangeHelper.isEmpty;
import static indices.IndexRangeHelper.makeRange;
import junit.framework.Assert;
import junit.framework.TestCase;
import tiny.lang.Debug;
import tiny.primitive.LongArray;

public class IndexRangeHelperTest extends TestCase {
	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
		super.setUp();
	}
	public void testMakeRange() {
		{
			int begin = 1;
			int end = Integer.MAX_VALUE;
			long range = makeRange(begin, end);
			Assert.assertEquals(begin, begin(range));
			Assert.assertEquals(end, end(range));
			Assert.assertEquals(false, isEmpty(range));
		}
	}
	public void testAnd() {
		{
			long x1 = makeRange(1, 3);
			long x2 = makeRange(0, 1);
			Assert.assertEquals(EMPTY_RANGE, and(x1, x2));
		}
		{
			long x1 = makeRange(1, 3);
			long x2 = makeRange(0, 2);
			Assert.assertEquals(makeRange(1, 2), and(x1, x2));
		}
		{
			long x1 = makeRange(1, 3);
			long x2 = makeRange(0, 4);
			Assert.assertEquals(makeRange(1, 3), and(x1, x2));
		}
		{
			long x1 = makeRange(1, 3);
			long x2 = makeRange(2, 4);
			Assert.assertEquals(makeRange(2, 3), and(x1, x2));
		}
		{
			long x1 = makeRange(1, 3);
			long x2 = makeRange(3, 4);
			Assert.assertEquals(makeRange(3, 3), and(x1, x2));
		}
		{
			long x1 = makeRange(1, 3);
			long x2 = makeRange(5, 7);
			long[] xs = { x1, x2 };

			long y = makeRange(0, 1);
			LongArray xy = and(xs, y);
			Assert.assertEquals(0, xy.getLength());

			y = makeRange(0, 2);
			xy = and(xs, y);
			Assert.assertEquals(1, xy.getLength());
			Assert.assertEquals(makeRange(1, 2), xy.get(0));

			y = makeRange(0, 4);
			xy = and(xs, y);
			Assert.assertEquals(1, xy.getLength());
			Assert.assertEquals(makeRange(1, 3), xy.get(0));

			y = makeRange(0, 6);
			xy = and(xs, y);
			Assert.assertEquals(2, xy.getLength());
			Assert.assertEquals(makeRange(1, 3), xy.get(0));
			Assert.assertEquals(makeRange(5, 6), xy.get(1));

			y = makeRange(0, 7);
			xy = and(xs, y);
			Assert.assertEquals(2, xy.getLength());
			Assert.assertEquals(makeRange(1, 3), xy.get(0));
			Assert.assertEquals(makeRange(5, 7), xy.get(1));

			y = makeRange(0, 8);
			xy = and(xs, y);
			Assert.assertEquals(2, xy.getLength());
			Assert.assertEquals(makeRange(1, 3), xy.get(0));
			Assert.assertEquals(makeRange(5, 7), xy.get(1));

			y = makeRange(2, 6);
			xy = and(xs, y);
			Assert.assertEquals(2, xy.getLength());
			Assert.assertEquals(makeRange(2, 3), xy.get(0));
			Assert.assertEquals(makeRange(5, 6), xy.get(1));

			y = makeRange(4, 6);
			xy = and(xs, y);
			Assert.assertEquals(1, xy.getLength());
			Assert.assertEquals(makeRange(5, 6), xy.get(0));

			y = makeRange(7, 8);
			xy = and(xs, y);
			Assert.assertEquals(0, xy.getLength());
		}
	}
}
