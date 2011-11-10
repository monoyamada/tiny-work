package tiny.number;

public interface NumberPartition {
	/**
	 * @param n
	 *          the number of balls.
	 * @param k
	 *          the number of boxes.
	 * @return initial value of the sequence of this class providing.
	 */
	public int[] first(int n, int k);
	/**
	 * @param word
	 *          mutable
	 * @return <code>true</code> if being success to get next value
	 *         <code>false</code> otherwise.
	 */
	public boolean next(int[] word);
}
