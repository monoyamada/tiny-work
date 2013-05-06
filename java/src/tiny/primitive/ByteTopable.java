package tiny.primitive;

import tiny.function.Topable;

public interface ByteTopable extends Topable<Number> {
	byte top(byte def);
	boolean isTop(byte value);
}
