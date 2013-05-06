package parser.v1;

import java.io.IOException;

public interface ByteInput {
	final int END_OF_INPUT = -1;
	final int NO_MARK = -1;

	long position();
	ByteInput next();

	int get() throws IOException;
	/*
	 * ByteInput get(byte[] output, int offset, long begin) throws IOException;
	 * ByteInput get(byte[] output, int offset, long begin, long end) throws
	 * IOException;
	 */

	ByteInput pushMark();
	ByteInput popMark();
	ByteInput setMark();
	ByteInput goMark();
	int getMarkSize();
	/**
	 * @return {@link #NO_MARK} iff the stack of mark is empty.
	 */
	long getMinMark();
	/**
	 * @return {@link #NO_MARK} iff the stack of mark is empty.
	 */
	long getMaxMark();
}
