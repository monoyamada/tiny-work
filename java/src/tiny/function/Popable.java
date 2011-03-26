package tiny.function;

public interface Popable<Value>  {
	public boolean isEmpty();
	public Value peekValue(Value def);
	public boolean pop();
}
