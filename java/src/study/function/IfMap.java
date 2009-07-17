package study.function;

public interface IfMap<Key, Value> {
	public Value getValue(Key key, Value defaultValue);
}
