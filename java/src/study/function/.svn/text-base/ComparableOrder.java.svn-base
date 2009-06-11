/**
 *
 */
package study.function;

import java.util.Comparator;

import study.lang.ObjectHelper;

public class ComparableOrder<X extends Comparable<? super X>> extends
		AbBinaryFunction<X, X, Number> implements Comparator<X> {
	@Override
	public int compare(X o1, X o2) {
		return ObjectHelper.compare(o1, o2);
	}
	@Override
	public Number evaluate(X first, X second) throws Exception {
		return Integer.valueOf(this.compare(first, second));
	}
}