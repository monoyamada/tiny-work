package study.function;


public abstract class AbFunction<Source, Target> implements
		Function<Source, Target>, IfMap<Source, Target> {
	@Override
	public Target getValue(Source key, Target defaultValue) {
		try {
			return this.evaluate(key);
		} catch (Exception ex) {
			return defaultValue;
		}
	}
	@Override
	public <X> Function<X, ? extends Target> compose(
			Function<X, ? extends Source> source) {
		return new CompositeFunction<X, Source, Target>(this, source);
	}
}
