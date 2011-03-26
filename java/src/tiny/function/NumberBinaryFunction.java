package tiny.function;

public interface NumberBinaryFunction<First, Second> extends
		BinaryFunction<First, Second, Number> {
	public long evaluateLong(First first, Second second) throws Exception;
	public double evaluateDouble(First first, Second second) throws Exception;
}
