package tiny.function;


public interface IfMutableMap<Value, Key> extends IfMap<Value, Key> {
	public void setValue(Key key, Value value);
}
