package tiny.primitive;

import tiny.function.Pushable;

public interface IntPushable extends Pushable<Number> {
	public Pushable<Number> push(int value);
}
