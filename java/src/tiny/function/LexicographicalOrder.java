/**
 *
 */
package tiny.function;

import java.util.Comparator;

import tiny.lang.Debug;

public class LexicographicalOrder<X> extends AbIntegerBinaryFunction<X[], X[]>
		implements Comparator<X[]> {
	private final Comparator<X> order;

	public LexicographicalOrder(Comparator<X> order) {
		Debug.isNotNull("order", order);
		this.order = order;
	}
	@Override
	public int compare(X[] o1, X[] o2) {
		return LexicographicalOrders.compare(o1, o2, this.order);
	}
	@Override
	public int evaluateInteger(X[] first, X[] second) {
		return this.compare(first, second);
	}
	/**
	 * @return the order
	 */
	public Comparator<X> getOrder() {
		return this.order;
	}
}