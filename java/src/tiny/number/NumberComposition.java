/**
 * 
 */
package tiny.number;

public interface NumberComposition extends NumberPartition {
	/**
	 * calculation of size of a set is relatively easy w.r.t. the case of
	 * {@link NumberComposition}.
	 * 
	 * @param n
	 *          the number of balls. should be <code>0<n</code>.
	 * @param k
	 *          the number of boxes. should be <code>0<k</code>.
	 * @return binomial coefficient <code>c(n - k, k)</code>.
	 */
	public long size(int n, int k);
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