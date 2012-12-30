package tiny.primitive;

import tiny.function.Peekable;

public interface IntPeekable extends Peekable<Number> {
	int peek(int def);
}
