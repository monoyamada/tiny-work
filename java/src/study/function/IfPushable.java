package study.function;

public interface IfPushable<Value> {
	public boolean isFull();
	public IfPushable<?extends Value> pushValue(Value value);
}
