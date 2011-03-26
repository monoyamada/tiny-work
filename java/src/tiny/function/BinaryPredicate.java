package tiny.function;

public interface BinaryPredicate<First, Second> {
	public boolean evaluateBoolean(First first, Second second) throws Exception;
}
