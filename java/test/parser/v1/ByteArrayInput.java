package parser.v1;

import java.io.IOException;
import java.util.NoSuchElementException;

import tiny.lang.ArrayHelper;
import tiny.primitive.IntArrayList;

public class ByteArrayInput implements ByteInput {
	private final byte[] array;
	private int position;
	private IntArrayList positions;

	public ByteArrayInput(byte[] array) {
		if (array == null) {
			array = ArrayHelper.EMPTY_BYTE_ARRAY;
		}
		this.array = array;
	}
	public byte[] array() {
		return this.array;
	}
	protected IntArrayList positions(boolean anyway) {
		if (this.positions == null && anyway) {
			this.positions = new IntArrayList(8);
		}
		return this.positions;
	}

	@Override
	public long position() {
		return this.position;
	}
	@Override
	public int get() throws IOException {
		if (this.position < this.array.length) {
			return this.array[this.position] & 0xFF;
		}
		return ByteInput.END_OF_INPUT;
	}
	@Override
	public ByteArrayInput next() {
		if (this.position < this.array.length) {
			++this.position;
		}
		return this;
	}
	/*
	 * @Override public ByteArrayInput get(byte[] output, int offset, long begin)
	 * throws IOException { return this.get(output, offset, begin,
	 * this.position()); }
	 * 
	 * @Override public ByteArrayInput get(byte[] output, int offset, long begin,
	 * long end) throws IOException { if (this.markSize() < 1) { String msg =
	 * "not marked"; throw new IOException(msg); } else if (begin <
	 * this.markMin(this.position) || this.position() < end) { String msg =
	 * "expected range=[" + this.markMin(this.position) + ", " + this.position() +
	 * ")" + " but actual=[" + begin + ", " + end + ")"; throw new
	 * IOException(msg); } System.arraycopy(this.array, (int) begin, output,
	 * offset, (int) (end - begin)); return this; }
	 */
	@Override
	public ByteArrayInput pushMark() {
		this.positions(true).push(this.position);
		return this;
	}
	@Override
	public ByteArrayInput popMark() {
		IntArrayList list = this.positions(false);
		if (list == null || list.size() < 1) {
			String msg = "there is not stored position";
			throw new NoSuchElementException(msg);
		}
		this.position = list.top(this.position);
		list.removeLast();
		return this;
	}
	@Override
	public ByteArrayInput setMark() {
		IntArrayList list = this.positions(false);
		if (list == null || list.size() < 1) {
			String msg = "there is not stored position";
			throw new NoSuchElementException(msg);
		}
		list.setTop(this.position);
		return this;
	}
	@Override
	public ByteInput goMark() {
		IntArrayList list = this.positions(false);
		if (list == null || list.size() < 1) {
			String msg = "there is not stored position";
			throw new NoSuchElementException(msg);
		}
		this.position = list.top(this.position);
		return this;
	}
	@Override
	public int getMarkSize() {
		IntArrayList list = this.positions(false);
		return list != null ? list.size() : 0;
	}
	@Override
	public long getMinMark() {
		IntArrayList list = this.positions(false);
		if (list == null || list.size() < 1) {
			return ByteInput.NO_MARK;
		}
		return list.get(0);
	}
	@Override
	public long getMaxMark() {
		IntArrayList list = this.positions(false);
		if (list == null || list.size() < 1) {
			return ByteInput.NO_MARK;
		}
		return list.top(ByteInput.NO_MARK);
	}
}
