package study.lang;

import study.function.Equality;
import study.function.IfBinaryPredicate;

public class ObjectHelper {
	public static final IfBinaryPredicate<Object, Object> DEFAULT_EQUALITY = new Equality();

	public static boolean equals(Object first, Object second) {
		return first == second ? true : first == null || second == null ? false
				: first.equals(second);
	}

	public static <First extends Comparable<? super Second>, Second> int compare(
			First first, Second second) {
		return first == second ? 0 : first == null ? 1 : second == null ? -1
				: first.compareTo(second);
	}

	/**
	 * <code>null</code> last.
	 *
	 * @param <X>
	 * @param x1
	 * @param x2
	 * @return
	 */
	public static <X extends Comparable<? super X>> int compare(X x1, X x2) {
		if (x1 == x2) {
			return 0;
		} else if (x1 == null) {
			return 1;
		} else if (x2 == null) {
			return -1;
		}
		return x1.compareTo(x2);
	}
	public static int compare(long x1, long x2) {
		return x1 == x2 ? 0 : x1 < x2 ? -1 : 1;
	}
	public static int compare(int x1, int x2) {
		return x1 == x2 ? 0 : x1 < x2 ? -1 : 1;
	}
	public static int compare(short x1, short x2) {
		return x1 == x2 ? 0 : x1 < x2 ? -1 : 1;
	}
	public static int compare(byte x1, byte x2) {
		return x1 == x2 ? 0 : x1 < x2 ? -1 : 1;
	}
	public static int compare(boolean x1, boolean x2) {
		return x1 == x2 ? 0 : x2 ? -1 : 1;
	}

	public static int hashCode(long value) {
		return (int) (value ^ (value >>> 32));
	}
	public static int hashCode(int value) {
		return value;
	}
	public static int hashCode(short value) {
		return value;
	}
	public static int hashCode(byte value) {
		return value;
	}
	public static int hashCode(boolean value) {
		return value ? 0 : 1;
	}
	public static int hashCode(Object value) {
		return value == null ? 0 : value.hashCode();
	}

	public static <X> X avoidNull(X value, X defaultValue) {
		return value != null ? value : defaultValue;
	}

	public static long factorial(long n) {
		long x = n;
		for (--n; 0 < n; --n) {
			x *= n;
		}
		return x;
	}
}
