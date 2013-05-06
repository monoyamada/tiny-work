package tiny.primitive;

import tiny.function.Pushable;

public interface BytePushable extends Pushable<Number> {
	public Pushable<Number> push(byte value);
}
