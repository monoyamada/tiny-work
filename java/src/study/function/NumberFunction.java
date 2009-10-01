package study.function;

public interface NumberFunction<Source> extends Function<Source, Number> {
	public long evaluateLong(Source x) throws Exception;
	public double evaluateDouble(Source x) throws Exception;
}
