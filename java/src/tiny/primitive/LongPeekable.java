package tiny.primitive;

import tiny.function.Peekable;

public interface LongPeekable extends Peekable<Number> {
	long peek(long def);
}
