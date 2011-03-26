package indices;

import tiny.primitive.LongArray;
import tiny.primitive.LongPushable;

public class IndexRangeHelper {
	protected static final int INDEX_BITS = Integer.SIZE;
	public static final int INDEX_MASK = Integer.MAX_VALUE;
	public static final long EMPTY_RANGE = 0;
	protected static final ThreadLocal<SimpleLongArray> RANGE_ARRAY = new ThreadLocal<SimpleLongArray>() {
		@Override
		protected SimpleLongArray initialValue() {
			return new SimpleLongArray();
		}
	};

	protected static SimpleLongArray getRangeArray() {
		return IndexRangeHelper.RANGE_ARRAY.get();
	}

	public static long makeRange(int begin, int end) {
		if (begin < end) {
			long value = begin;
			return (value << INDEX_BITS) | end;
		}
		return EMPTY_RANGE;
	}
	public static int begin(long range) {
		return (int) (range >> INDEX_BITS);
	}
	public static int end(long range) {
		return (int) (range & INDEX_MASK);
	}
	public static boolean isEmpty(long range) {
		return range == EMPTY_RANGE;
	}

	public static long and(long x1, long x2) {
		if (isEmpty(x1)) {
			return x2;
		} else if (isEmpty(x2)) {
			return x1;
		}
		int b1 = begin(x1);
		int e1 = end(x1);
		int b2 = begin(x2);
		int e2 = end(x2);
		if (e1 <= b2) {
			return EMPTY_RANGE;
		} else if (e1 < e2) {
			return b1 < b2 ? makeRange(b2, e1) : x1;
		} else if (e1 == e2) {
			return b1 < b2 ? x2 : x1;
		} else if (b1 < e2) {
			return b1 <= b2 ? x2 : makeRange(b1, e2);
		}
		return EMPTY_RANGE;
	}
	public static LongArray and(long[] xs, long y) {
		SimpleLongArray buffer = getRangeArray();
		buffer.popAll();
		and(buffer, xs, y);
		return buffer;
	}
	public static void and(LongPushable result, long[] xs, long y) {
		int n = xs != null ? xs.length : 0;
		if (n == 0 || isEmpty(y)) {
			return;
		} else if (n == 1) {
			long x = and(xs[0], y);
			if (!isEmpty(x)) {
				result.push(x);
			}
			return;
		}
		int yb = begin(y);
		int ye = end(y);
		for (int i = 0; i < n; ++i) {
			long x = xs[i];
			int xb = begin(x);
			int xe = end(x);
			if (xe <= yb) {
				continue;
			} else if (xe < ye) {
				if (xb < yb) {
					result.push(makeRange(yb, xe));
				} else {
					result.push(x);
				}
				continue;
			} else if (xe == ye) {
				if (xb < yb) {
					result.push(y);
				} else {
					result.push(x);
				}
			} else if (xb < ye) {
				if (xb <= yb) {
					result.push(y);
				} else {
					result.push(makeRange(xb, ye));
				}
			}
			break;
		}
	}
}
