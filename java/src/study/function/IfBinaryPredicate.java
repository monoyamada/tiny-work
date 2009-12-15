package study.function;

public interface IfBinaryPredicate<First, Second> {
	public boolean evaluateBoolean(First first, Second second) throws Exception;
}
