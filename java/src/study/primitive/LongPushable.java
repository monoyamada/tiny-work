package study.primitive;

import study.function.Pushable;

public interface LongPushable extends Pushable<Number> {
	public Pushable<Number> pushLong(long value);
}
