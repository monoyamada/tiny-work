package tiny.primitive;

import tiny.function.Topable;

public interface IntTopable extends Topable<Number> {
	int top(int def);
	boolean isTop(int value);
}
