package study.function;

import study.struct.IfMap;

public interface IfMutableMap<Value, Key> extends IfMap<Value, Key> {
	public void setValue(Key key, Value value);
}
