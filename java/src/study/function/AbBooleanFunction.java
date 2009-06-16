package study.function;

public abstract class AbBooleanFunction<Source> extends
		AbFunction<Source, Boolean> implements IfBooleanFunction<Source> {
	@Override
	public Boolean evaluate(Source x) throws Exception {
		return Boolean.valueOf(this.evaluateBoolean(x));
	}
}
