package study.function;

import java.util.Comparator;

/**
 * a class to implement {@link Comparator}.
 * 
 * @author shirakata
 * 
 * @param <First>
 * @param <Second>
 */
public abstract class AbIntegerBinaryFunction<First, Second> extends
		AbNumberBinaryFunction<First, Second> {
	@Override
	public Number evaluate(First first, Second second) throws Exception {
		return Long.valueOf(this.evaluateInteger(first, second));
	}
	@Override
	public long evaluateLong(First first, Second second) throws Exception {
		return this.evaluateInteger(first, second);
	}
	@Override
	public double evaluateDouble(First first, Second second) throws Exception {
		return this.evaluateInteger(first, second);
	}
	public abstract int evaluateInteger(First first, Second second)
			throws Exception;
}
