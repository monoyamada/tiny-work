package parser.v2;

import java.io.IOException;

public interface ByteInput extends Cloneable {
	final int END_OF_INPUT = -1;

	int get(long position) throws IOException;
	long blockBegin();
	long blockEnd();
}
