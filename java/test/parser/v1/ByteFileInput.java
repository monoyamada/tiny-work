package parser.v1;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.NoSuchElementException;

import tiny.primitive.LongArrayList;

public class ByteFileInput implements ByteInput {
	static class Block {
		long begin;
		int size;
		final byte[] array;
		ByteBuffer buffer;

		Block(int size) {
			this.array = new byte[size];
		}
		long begin() {
			return this.begin;
		}
		int size() {
			return this.size;
		}
		byte[] array() {
			return this.array;
		}
		ByteBuffer buffer(boolean anyway) {
			if (this.buffer == null && anyway) {
				this.buffer = ByteBuffer.wrap(this.array);
			}
			return this.buffer;
		}
		Block read(ReadableByteChannel input, long position) throws IOException {
			ByteBuffer buffer = this.buffer(true);
			buffer.rewind();
			this.begin = position;
			this.size = input.read(buffer);
			return this;
		}
	}

	final FileChannel input;
	final long length;
	final int blockSize;
	private long position;
	private int whichBlock;
	final private Block[] blocks;
	private LongArrayList positions;
	transient int seekCount;

	ByteFileInput(FileChannel input, int blockSize) throws IOException {
		this.input = input;
		this.blockSize = blockSize;
		this.length = input.size();
		this.blocks = new Block[2];
	}
	protected LongArrayList positions(boolean anyway) {
		if (this.positions == null && anyway) {
			this.positions = new LongArrayList(8);
		}
		return this.positions;
	}
	@Override
	public long position() {
		return this.position;
	}
	@Override
	public int get() throws IOException {
		if (this.length <= this.position) {
			return ByteInput.END_OF_INPUT;
		}
		Block block = this.blocks[this.whichBlock];
		if (block != null) {
			if (block.begin <= this.position) {
				int index = (int) (this.position - block.begin);
				if (index < block.size) {
					return block.array[index] & 0xFF;
				}
			}
			this.whichBlock = this.whichBlock ^ 1;
		}
		block = this.blocks[this.whichBlock];
		if (block == null) {
			block = new Block(this.blockSize);
			this.blocks[this.whichBlock] = block;
		} else if (block.begin <= this.position) {
			int index = (int) (this.position - block.begin);
			if (index < block.size) {
				return block.array[index] & 0xFF;
			}
		}
		long begin = (this.position / this.blockSize) * this.blockSize;
		FileChannel input = this.input();
		if (input.position() != begin) {
			++this.seekCount;
			this.input.position(begin);
		}
		int index = (int) (this.position - begin);
		return block.read(input, begin).array[index] & 0xFF;
	}
	public FileChannel input() {
		return this.input;
	}
	@Override
	public ByteFileInput next() {
		if (this.position < this.length) {
			++this.position;
		}
		return this;
	}
	@Override
	public ByteFileInput pushMark() {
		this.positions(true).push(this.position);
		return this;
	}
	@Override
	public ByteFileInput popMark() {
		LongArrayList list = this.positions(false);
		if (list == null || list.getLength() < 1) {
			String msg = "there is not stored position";
			throw new NoSuchElementException(msg);
		}
		list.pop();
		return this;
	}
	@Override
	public ByteFileInput setMark() {
		LongArrayList list = this.positions(false);
		if (list == null || list.getLength() < 1) {
			String msg = "there is not stored position";
			throw new NoSuchElementException(msg);
		}
		list.setTop(this.position);
		return this;
	}
	@Override
	public ByteFileInput goMark() {
		LongArrayList list = this.positions(false);
		if (list == null || list.getLength() < 1) {
			String msg = "there is not stored position";
			throw new NoSuchElementException(msg);
		}
		this.position = list.top(this.position);
		return this;
	}
	@Override
	public int getMarkSize() {
		LongArrayList list = this.positions(false);
		return list != null ? list.getLength() : 0;
	}
	@Override
	public long getMinMark() {
		LongArrayList list = this.positions(false);
		if (list == null || list.getLength() < 1) {
			return ByteInput.NO_MARK;
		}
		return list.get(0);
	}
	@Override
	public long getMaxMark() {
		LongArrayList list = this.positions(false);
		if (list == null || list.getLength() < 1) {
			return ByteInput.NO_MARK;
		}
		return list.top(ByteInput.NO_MARK);
	}
}
