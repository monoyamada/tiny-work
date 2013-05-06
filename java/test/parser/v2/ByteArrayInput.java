package parser.v2;

import tiny.lang.ArrayHelper;

public class ByteArrayInput implements ByteInput, Cloneable {
	private final byte[] array;

	public ByteArrayInput(byte[] array) {
		if (array == null) {
			array = ArrayHelper.EMPTY_BYTE_ARRAY;
		}
		this.array = array;
	}
	public byte[] array() {
		return this.array;
	}
	@Override
	public int get(long position) {
		if (position < this.array.length) {
			return this.array[(int) position] & 0xFF;
		}
		return ByteInput.END_OF_INPUT;
	}
	@Override
	public long blockBegin() {
		return 0;
	}
	@Override
	public long blockEnd() {
		return this.array.length;
	}
}
