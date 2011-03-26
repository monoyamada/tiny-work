package tiny.function;

public interface Pushable<Value> {
	public boolean isFull();
	public Pushable<?extends Value> pushValue(Value value);
}
