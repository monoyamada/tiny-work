package study.function;

public interface IfFunction<Source, Target> {
	public Target evaluate(Source source) throws Exception;
	public <X> IfFunction<X, ? extends Target> compose(
			IfFunction<X, ? extends Source> source);

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
		public <X> IfFunction<X, ? extends Value> compose(
				IfFunction<X, ? extends Value> source) {
			return source;
		}
	}

	public static class CompositeFunction<Source, X, Target> extends
			AbFunction<Source, Target> {
		private final IfFunction<Source, ? extends X> source;
		private final IfFunction<X, Target> target;

		public CompositeFunction(IfFunction<X, Target> target,
				IfFunction<Source, ? extends X> source) {
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
		public IfFunction<Source, ? extends X> getSource() {
			return this.source;
		}
		/**
		 * @return the target
		 */
		public IfFunction<X, Target> getTarget() {
			return this.target;
		}
	}
}
