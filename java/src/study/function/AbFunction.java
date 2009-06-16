package study.function;

import study.struct.IfMap;

public abstract class AbFunction<Source, Target> implements
		IfFunction<Source, Target>, IfMap<Source, Target> {
	@Override
	public Target getValue(Source key, Target defaultValue) {
		try {
			return this.evaluate(key);
		} catch (Exception ex) {
			return defaultValue;
		}
	}
	@Override
	public <X> IfFunction<X, ? extends Target> compose(
			IfFunction<X, ? extends Source> source) {
		return new CompositeFunction<X, Source, Target>(this, source);
	}
}
