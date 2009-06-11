package study.function;

public interface IfBinaryFunction<First, Second, Target> {
	public Target evaluate(First first, Second second) throws Exception;
	public IfFunction<Second, Target> bindFirst(First value);
	public IfFunction<First, Target> bindSecond(Second value);

	public class BindFirstFunction<X, Source, Target> extends
			AbFunction<Source, Target> {
		final IfBinaryFunction<? super X, ? super Source, Target> function;
		final X bind;

		public BindFirstFunction(
				IfBinaryFunction<? super X, ? super Source, Target> function, X bind) {
			this.function = function;
			this.bind = bind;
		}
		@Override
		public Target evaluate(Source source) throws Exception {
			return this.function.evaluate(this.bind, source);
		}
	}

	public static class BindSecondFunction<Source, X, Target> extends
			AbFunction<Source, Target> {
		final IfBinaryFunction<? super Source, ? super X, Target> function;
		final X bind;

		public BindSecondFunction(
				IfBinaryFunction<? super Source, ? super X, Target> function, X bind) {
			this.function = function;
			this.bind = bind;
		}
		@Override
		public Target evaluate(Source source) throws Exception {
			return this.function.evaluate(source, this.bind);
		}
	}
}
