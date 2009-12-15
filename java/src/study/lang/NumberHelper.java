package study.lang;


public class NumberHelper {
	/**
	 * pairing the specified positive integers.
	 * 
	 * @param left
	 *          <code>[0, 2^31)</code>.
	 * @param right
	 *          <code>[0, 2^31)</code>.
	 * @return
	 */
	public static long pairing(int left, int right) {
		return ((long) left << 32) | right;
	}
	/**
	 * gets the left part positive integer from the number made from the method
	 * {@link #pairing(int, int)}.
	 * 
	 * @param pair
	 *          the number made from the method {@link #pairing(int, int)}.
	 * @return the left part positive integer.
	 */
	public static int left(long pair) {
		return (int) (pair >> 32);
	}
	/**
	 * gets the right part positive integer from the number made from the method
	 * {@link #pairing(int, int)}.
	 * 
	 * @param pair
	 *          the number made from the method {@link #pairing(int, int)}.
	 * @return the right part positive integer.
	 */
	public static int right(long pair) {
		return (int) pair;
	}
	public static boolean or(boolean x, boolean y) {
		return x || y;
	}
	public static boolean and(boolean x, boolean y) {
		return x && y;
	}
	public static boolean xor(boolean x, boolean y) {
		return (x || y) && (!x || !y);
	}

	public static int compare(long o1, long o2) {
		return o1 == o2 ? 0 : o1 < o2 ? -1 : 1;
	}
	public static int compare(int o1, int o2) {
		return o1 == o2 ? 0 : o1 < o2 ? -1 : 1;
	}
	public static int compare(short o1, short o2) {
		return o1 == o2 ? 0 : o1 < o2 ? -1 : 1;
	}
	public static int compare(byte o1, byte o2) {
		return o1 == o2 ? 0 : o1 < o2 ? -1 : 1;
	}
	public static int compare(char o1, char o2) {
		return o1 == o2 ? 0 : o1 < o2 ? -1 : 1;
	}
}
