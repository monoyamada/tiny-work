package study.function;

public abstract class AbNumberFunction<Source> extends
		AbFunction<Source, Number> implements IfNumberFunction<Source> {
	@Override
	public Number evaluate(Source x) throws Exception {
		return Long.valueOf(this.evaluateLong(x));
	}
	@Override
	public double evaluateDouble(Source x) throws Exception {
		return this.evaluateLong(x);
	}
}
