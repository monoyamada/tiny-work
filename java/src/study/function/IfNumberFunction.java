package study.function;

public interface IfNumberFunction<Source> extends IfFunction<Source, Number> {
	public long evaluateLong(Source x) throws Exception;
	public double evaluateDouble(Source x) throws Exception;
}
