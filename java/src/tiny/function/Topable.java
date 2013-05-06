package tiny.function;

public interface Topable<Value> {
	Value topValue(Value def);
	boolean isTopValue(Value value);
}
