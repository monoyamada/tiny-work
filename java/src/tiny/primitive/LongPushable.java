package tiny.primitive;

import tiny.function.Pushable;

public interface LongPushable extends Pushable<Number> {
	public Pushable<Number> push(long value);
}
