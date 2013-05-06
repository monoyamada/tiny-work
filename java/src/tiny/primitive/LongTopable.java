package tiny.primitive;

import tiny.function.Topable;

public interface LongTopable extends Topable<Number> {
	long top(long def);
	boolean isTop(long value);
}
