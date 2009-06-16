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
}
