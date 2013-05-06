package parser.v2;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

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
		public long end() {
			return this.begin + this.size;
		}
	}

	final FileChannel input;
	final long length;
	final int blockSize;
	private int whichBlock;
	final private Block[] blocks;

	ByteFileInput(FileChannel input, int blockSize) throws IOException {
		this.input = input;
		this.blockSize = blockSize;
		this.length = input.size();
		this.blocks = new Block[2];
	}
	@Override
	public int get(long position) throws IOException {
		if (this.length <= position) {
			return ByteInput.END_OF_INPUT;
		}
		Block block = this.blocks[this.whichBlock];
		if (block != null) {
			if (block.begin <= position) {
				int index = (int) (position - block.begin);
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
		} else if (block.begin <= position) {
			int index = (int) (position - block.begin);
			if (index < block.size) {
				return block.array[index] & 0xFF;
			}
		}
		long begin = (position / this.blockSize) * this.blockSize;
		int index = (int) (position - begin);
		FileChannel input = this.input();
		if (input.position() != begin) {
			this.input.position(begin);
		}
		return block.read(input, begin).array[index] & 0xFF;
	}
	public FileChannel input() {
		return this.input;
	}
	@Override
	public long blockBegin() {
		if (this.blocks[0] == null) {
			if (this.blocks[1] == null) {
				return END_OF_INPUT;
			}
			return this.blocks[1].begin;
		} else if (this.blocks[1] == null) {
			return this.blocks[0].begin;
		}
		return Math.min(this.blocks[0].begin, this.blocks[1].begin);
	}
	@Override
	public long blockEnd() {
		if (this.blocks[0] == null) {
			if (this.blocks[1] == null) {
				return END_OF_INPUT;
			}
			return this.blocks[1].end();
		} else if (this.blocks[1] == null) {
			return this.blocks[0].end();
		}
		return Math.max(this.blocks[0].end(), this.blocks[1].end());
	}
}
