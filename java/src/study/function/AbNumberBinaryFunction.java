package study.function;

public abstract class AbNumberBinaryFunction<First, Second> extends
		AbBinaryFunction<First, Second, Number> implements
		NumberBinaryFunction<First, Second> {
	@Override
	public Number evaluate(First first, Second second) throws Exception {
		return Long.valueOf(this.evaluateLong(first, second));
	}
	@Override
	public double evaluateDouble(First first, Second second) throws Exception {
		return this.evaluateLong(first, second);
	}
}
