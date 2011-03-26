package tiny.primitive;

import tiny.function.Popable;

public interface LongPopable extends Popable<Number> {
	public long peek(long def);
}
