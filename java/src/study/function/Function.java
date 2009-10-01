package study.function;

public interface Function<Source, Target> {
	public Target evaluate(Source source) throws Exception;
	public <X> Function<X, ? extends Target> compose(
			Function<X, ? extends Source> source);

	public static class IdentityFunction<Value> extends AbFunction<Value, Value> {
		@Override
		public Value evaluate(Value source) {
			return source;
		}
		@Override
		public Value getValue(Value key, Value defaultValue) {
			return key;
		}
		@Override
		public <X> Function<X, ? extends Value> compose(
				Function<X, ? extends Value> source) {
			return source;
		}
	}

	public static class CompositeFunction<Source, X, Target> extends
			AbFunction<Source, Target> {
		private final Function<Source, ? extends X> source;
		private final Function<X, Target> target;

		public CompositeFunction(Function<X, Target> target,
				Function<Source, ? extends X> source) {
			this.source = source;
			this.target = target;
		}
		@Override
		public Target evaluate(Source source) throws Exception {
			final X x = this.source.evaluate(source);
			return this.target.evaluate(x);
		}
		/**
		 * @return the source
		 */
		public Function<Source, ? extends X> getSource() {
			return this.source;
		}
		/**
		 * @return the target
		 */
		public Function<X, Target> getTarget() {
			return this.target;
		}
	}
}
