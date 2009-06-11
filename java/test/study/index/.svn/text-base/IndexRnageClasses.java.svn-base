package study.index;

import study.lang.NumberHelper;

public class IndexRnageClasses {
	public static long empty() {
		return 0;
	}
	public static long range(int begin, int end) {
		assert 0 <= begin && 0 <= end;
		return begin < end ? NumberHelper.pairing(begin, end) : empty();
	}
	public static boolean empty(long range) {
		return range == 0;
	}
	public static int begin(long range) {
		return NumberHelper.left(range);
	}
	public static int end(long range) {
		return NumberHelper.right(range);
	}
	public static long and(long r1, long r2) {
		final int b1 = begin(r1);
		final int e1 = end(r1);
		final int b2 = begin(r2);
		final int e2 = end(r2);
		if (e1 <= b2 || e2 <= b1) {
			return empty();
		} else if (b1 <= b2) {
			return e2 <= e1 ? r2 : range(b2, e1);
		} else {
			return b1 <= b2 ? r1 : range(b1, e2);
		}
	}
}
