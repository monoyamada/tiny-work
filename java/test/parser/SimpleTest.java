package parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import junit.framework.Assert;
import junit.framework.TestCase;
import tiny.lang.ArrayHelper;
import tiny.lang.Debug;
import tiny.lang.FileHelper;
import tiny.primitive.ByteArrayList;
import tiny.primitive.ByteList;
import tiny.primitive.IntArrayList;

public class SimpleTest extends TestCase {
	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
		super.setUp();
	}

	public void testAscii() {
		Charset utf8 = Charset.forName(FileHelper.UTF_8);
		byte[] array = new byte[1];
		for (byte i = 0, n = Byte.MAX_VALUE; i < n; ++i) {
			array[0] = i;
			String text = new String(array, utf8);
			if (i != 0) {
				System.out.print(", ");
			}
			if (text.length() == 1) {
				char ch = text.charAt(0);
				switch (ch) {
				case ' ':
					System.out.print("'" + ch + "'");
				break;
				case '\t':
					System.out.print("'\\t'");
				break;
				case '\r':
					System.out.print("'\\r'");
				break;
				case '\n':
					System.out.print("'\\n'");
				break;
				case '\b':
					System.out.print("'\\b'");
				break;
				case '\f':
					System.out.print("'\\f'");
				default:
					if (32 <= i && i <= 126) {
						System.out.print("'" + ch + "'");
					} else {
						System.out.print((int) ch);
					}
				break;
				}
			} else {
				System.out.print("---");
			}
			System.out.print(" // ");
			System.out.println(i);
		}
	}

	static interface Input {
		public int get(long index) throws IOException;
	}

	static class StringInput implements Input {
		final byte[] array;

		public StringInput(String input) {
			if (input == null || input.length() < 1) {
				this.array = ArrayHelper.EMPTY_BYTE_ARRAY;
			} else {
				byte[] array = ArrayHelper.EMPTY_BYTE_ARRAY;
				try {
					array = input.getBytes(FileHelper.UTF_8);
				} catch (UnsupportedEncodingException ex) {
					ex.printStackTrace();
				}
				this.array = array;
			}
		}
		@Override
		public String toString() {
			try {
				return new String(this.array, FileHelper.UTF_8);
			} catch (UnsupportedEncodingException ex) {
				ex.printStackTrace();
			}
			return "";
		}
		@Override
		public int get(long index) {
			if (0 <= index && index < this.array.length) {
				return this.array[(int) index];
			}
			return Phrase.END_OF_INPUT;
		}
	}

	static class StreamInput implements Input {
		final InputStream input;
		final byte[] buffer;
		final int readSize;
		boolean done;
		long offset;
		int size;

		StreamInput(InputStream input, int readSize) {
			this.input = input;
			this.readSize = readSize;
			this.buffer = new byte[readSize << 1];
		}
		@Override
		public int get(long index) throws IOException {
			if (index < this.offset) {
				String msg = "the specified data at=" + index + " already discarded";
				throw new IOException(msg);
			}
			final byte[] array = this.buffer;
			int delta = (int) (index - this.offset);
			if (this.size <= delta) {
				if (this.done) {
					return Phrase.END_OF_INPUT;
				}
				int n = array.length - this.size;
				if (n < this.readSize) {
					int r = this.readSize - n;
					System.arraycopy(this.buffer, r, this.buffer, 0, this.size -= r);
					this.offset += r;
					delta -= r;
				}
				n = this.input.read(array, this.size, this.readSize);
				if (n < 0) {
					this.done = true;
					return Phrase.END_OF_INPUT;
				}
				this.size += n;
			}
			return array[delta] & 0xff;
		}
		/**
		 * should call before parsing.
		 * 
		 * @return
		 * @throws IOException
		 */
		boolean isBOM() throws IOException {
			if (this.get(0) == 0xEF) {
				if (this.get(1) == 0xBB) {
					if (this.get(2) == 0xBF) {
						return true;
					}
				}
			}
			return false;
		}
	}

	static class Phrase {
		static final byte END_OF_INPUT = -1;
		static final byte ASCII_END_OF_LINE = 10; // charToAscii('\n',
																							// END_OF_INPUT);
		static final byte ASCII_SPACE = 32; // charToAscii(' ', END_OF_INPUT);
		static final byte ASCII_TAB = 9; // charToAscii('\t', END_OF_INPUT);
		static final byte ASCII_CR = 13; // charToAscii('\r', END_OF_INPUT);
		static final byte ASCII_SHARP = 35; // charToAscii('#', END_OF_INPUT);
		static final byte ASCII_BACKSLASH = 92; // charToAscii('\\', END_OF_INPUT);
		static final byte ASCII_CURLY_BRA = 123; // charToAscii('{', END_OF_INPUT);
		static final byte ASCII_CURLY_KET = 125; // charToAscii('}', END_OF_INPUT);
		static final byte ASCII_ROUND_BRA = 40; // (
		static final byte ASCII_ROUND_KET = 41; // )
		static final byte ASCII_SQUARE_BRA = 91; // (
		static final byte ASCII_SQUARE_KET = 93; // )
		static final byte ASCII_A = 97; // charToAscii('a', END_OF_INPUT);
		static final byte ASCII_Z = 122; // charToAscii('z', END_OF_INPUT);
		static final byte ASCII_AA = 65; // charToAscii('A', END_OF_INPUT);
		static final byte ASCII_ZZ = 90; // charToAscii('Z', END_OF_INPUT);
		static final byte ASCII_0 = 48; // charToAscii('0', END_OF_INPUT);
		static final byte ASCII_9 = 57; // charToAscii('0', END_OF_INPUT);
		static final byte ASCII__ = 95; // charToAscii('_', END_OF_INPUT);
		static final byte ASCII_EXCLAMATION = 33; // ?
		static final byte ASCII_DOLLAR = 36; // $
		static final byte ASCII_PERCENT = 37; // %
		static final byte ASCII_AND = 38; // &
		static final byte ASCII_OR = 124; // |
		static final byte ASCII_EQ = 61; // =
		static final byte ASCII_TILDE = 126; // ~
		static final byte ASCII_HAT = 94; // ^
		static final byte ASCII_GRAVE = 96; // `
		static final byte ASCII_AT = 64; // @
		static final byte ASCII_PLUS = 43; // +
		static final byte ASCII_MINUS = 45; // -
		static final byte ASCII_STAR = 42; // *
		static final byte ASCII_SLASH = 47; // /
		static final byte ASCII_PERIOD = 46; // .
		static final byte ASCII_COMMA = 44; // ,
		static final byte ASCII_COLON = 58; // :
		static final byte ASCII_SEMICOLON = 59; // ;
		static final byte ASCII_BRA = 60; // <
		static final byte ASCII_KET = 62; // >
		static final byte ASCII_QUESTION = 63; // ?

		static final long NONE_INDEX = -1;
	}

	static class EquationBlock extends Phrase {
		static long parse(Input input, long index) throws IOException {
			return parse(input, index, false);
		}
		static long parse(Input input, long index, boolean allowSpaceOperator)
				throws IOException {
			byte state = 0;
			// int depth = 0;
			int ch = END_OF_INPUT;
			long result = NONE_INDEX;
			IntArrayList stack = new IntArrayList(16);
			ByteArrayList buffer = new ByteArrayList(1024);
			while (true) {
				switch (state) {
				case 0:
					ch = input.get(index++);
					switch (ch) {
					case ASCII_CURLY_BRA:
					case ASCII_ROUND_BRA:
					case ASCII_SQUARE_BRA:
						stack.push(ch);
						index = IgnorableBlock.parse(input, index);
					// state = state;
					break;
					default:
						if (Variable.isFirst(ch)) {
							buffer.removeAll().addLast((byte) ch);
							ch = input.get(index++);
							while (Variable.isSecond(ch)) {
								buffer.addLast((byte) ch);
								ch = input.get(index++);
							}
							Debug.log().debug("variable=" + getString(buffer));
							--index;
							state = 1;
						} else {
							return result;
						}
					break;
					}
				break;
				case 1:
					if (stack.isEmpty()) {
						result = index;
					}
					index = IgnorableBlock.parse(input, index);
					state = 2;
				break;
				case 2:
					ch = input.get(index++);
					switch (ch) {
					case ASCII_CURLY_KET:
						if (pop(stack, ASCII_CURLY_BRA)) {
							state = 1;
						} else {
							return result;
						}
					break;
					case ASCII_ROUND_KET:
						if (pop(stack, ASCII_ROUND_BRA)) {
							state = 1;
						} else {
							return result;
						}
					break;
					case ASCII_SQUARE_KET:
						if (pop(stack, ASCII_SQUARE_BRA)) {
							state = 1;
						} else {
							return result;
						}
					break;
					default:
						if (Operator.isFirst(ch)) {
							buffer.removeAll().addLast((byte) ch);
							ch = input.get(index++);
							while (Operator.isSecond(ch)) {
								buffer.addLast((byte) ch);
								ch = input.get(index++);
							}
							Debug.log().debug("operator=" + getString(buffer));
							index = IgnorableBlock.parse(input, --index);
							state = 0;
						} else if (allowSpaceOperator) {
							--index;
							ch = input.get(index++);
							switch (ch) {
							case ASCII_CURLY_BRA:
							case ASCII_ROUND_BRA:
							case ASCII_SQUARE_BRA:
								index = IgnorableBlock.parse(input, index);
								Debug.log().debug("operator=empty");
								stack.push(ch);
								state = 0;
							break;
							default:
								if (Variable.isFirst(ch)) {
									buffer.removeAll().addLast((byte) ch);
									ch = input.get(index++);
									while (Variable.isSecond(ch)) {
										buffer.addLast((byte) ch);
										ch = input.get(index++);
									}
									Debug.log().debug("operator=empty");
									Debug.log().debug("variable=" + getString(buffer));
									--index;
									state = 1;
								} else {
									return result;
								}
							break;
							}
						} else {
							return result;
						}
					break;
					}
				break;
				default:
					throw new Error("unkown state=" + state);
				}
			}
		}
		static boolean pop(IntArrayList stack, int value) {
			if (stack.isEmpty()) {
				return false;
			} else if (stack.top(END_OF_INPUT) != value) {
				return false;
			}
			return stack.pop();
		}
		static String getString(ByteArrayList list)
				throws UnsupportedEncodingException {
			return new String(list.getArray(), 0, list.getLength(), FileHelper.UTF_8);
		}
	}

	static class Operator extends Phrase {
		static long parse(Input input, long index) throws IOException {
			int ch = input.get(index++);
			if (isFirst(ch)) {
				ch = input.get(index++);
				while (isSecond(ch)) {
					ch = input.get(index++);
				}
				return --index;
			}
			return NONE_INDEX;
		}
		public static boolean isFirst(int ch) {
			switch (ch) {
			case ASCII_EXCLAMATION:
				// case ASCII_DOLLAR:
			case ASCII_PERCENT:
			case ASCII_AND:
			case ASCII_OR:
			case ASCII_EQ:
			case ASCII_TILDE:
			case ASCII_AT:
			case ASCII_PLUS:
			case ASCII_MINUS:
			case ASCII_STAR:
			case ASCII_SLASH:
			case ASCII_HAT:
			case ASCII_PERIOD:
			case ASCII_COMMA:
			case ASCII_COLON:
			case ASCII_SEMICOLON:
			case ASCII_BRA:
			case ASCII_KET:
			case ASCII_QUESTION:
				return true;
			default:
				return false;
			}
		}
		public static boolean isSecond(int ch) {
			return isFirst(ch);
		}
	}

	static class Variable extends Phrase {
		static long parse(Input input, long index) throws IOException {
			return parse(null, input, index);
		}
		static long parse(ByteList output, Input input, long index)
				throws IOException {
			int ch = input.get(index++);
			if (isFirst(ch)) {
				if (output != null) {
					output.addLast((byte) ch);
				}
				ch = input.get(index++);
				while (isSecond(ch)) {
					if (output != null) {
						output.addLast((byte) ch);
					}
					ch = input.get(index++);
				}
				return --index;
			}
			return NONE_INDEX;
		}
		public static boolean isFirst(int ch) {
			switch (ch) {
			case ASCII__:
				return true;
			default:
				if (ASCII_A <= ch && ch <= ASCII_Z) {
					return true;
				} else if (ASCII_AA <= ch && ch <= ASCII_ZZ) {
					return true;
				}
				return false;
			}
		}
		public static boolean isSecond(int ch) {
			return isFirst(ch) || isDigit(ch);
		}
		public static boolean isDigit(int ch) {
			return ASCII_0 <= ch && ch <= ASCII_9;
		}
	}

	static class IgnorableBlock extends Phrase {
		/**
		 * @return non-negative index iff the specified index is non-negative,
		 *         because this block allows empty text.
		 */
		static long parse(Input input, long index) throws IOException {
			long newIndex = parseBlock(input, index);
			while (newIndex != NONE_INDEX) {
				index = newIndex;
				newIndex = parseBlock(input, index);
			}
			return index;
		}
		static long parseBlock(Input input, long index) throws IOException {
			long newIndex = WhiteBlock.parse(input, index);
			if (newIndex != NONE_INDEX) {
				return newIndex;
			}
			return Comment.parse(input, index);
		}
	}

	static class WhiteBlock extends Phrase {
		static long parse(Input input, long index) throws IOException {
			int ch = input.get(index++);
			if (isWhite(ch)) {
				ch = input.get(index++);
				while (isWhite(ch)) {
					ch = input.get(index++);
				}
				return --index;
			}
			return NONE_INDEX;
		}
		static boolean isWhite(int ch) {
			switch (ch) {
			case ASCII_SPACE:
			case ASCII_TAB:
			case ASCII_END_OF_LINE:
			case ASCII_CR:
				return true;
			default:
				return false;
			}
		}
	}

	static class Comment extends Phrase {
		static long parse(Input input, long index) throws IOException {
			if (input.get(index++) == ASCII_SHARP) {
				return parse_1(input, index);
			}
			return NONE_INDEX;
		}
		static long parse_1(Input input, long index) throws IOException {
			long ind = CommentLine.parse_1(input, index);
			if (ind != NONE_INDEX) {
				return ind;
			}
			return CommentBlock.parse_1(input, index);
		}
	}

	static class CommentLine extends Comment {
		static long parse(Input input, long index) throws IOException {
			if (input.get(index++) == ASCII_SHARP) {
				return parse_1(input, index);
			}
			return NONE_INDEX;
		}
		static long parse_1(Input input, long index) throws IOException {
			if (input.get(index++) == ASCII_SHARP) {
				int ch = input.get(index++);
				while (ch != ASCII_END_OF_LINE && ch != END_OF_INPUT) {
					ch = input.get(index++);
				}
				return index;
			}
			return NONE_INDEX;
		}
	}

	static class CommentBlock extends Comment {
		static long parse(Input input, long index) throws IOException {
			if (input.get(index++) == ASCII_SHARP) {
				return parse_1(input, index);
			}
			return NONE_INDEX;
		}
		static long parse_1(Input input, long index) throws IOException {
			if (input.get(index++) == ASCII_CURLY_BRA) {
				int state = 2;
				int depth = 1;
				int ch = input.get(index++);
				while (ch != END_OF_INPUT) {
					switch (state) {
					case 0:
						if (ch != ASCII_SHARP) {
							return NONE_INDEX;

						}
						state = 1;
					break;
					case 1:
						if (ch != ASCII_CURLY_BRA) {
							return NONE_INDEX;

						}
						state = 2;
					break;
					case 2:
						switch (ch) {
						case ASCII_SHARP:
							state = 5;
						break;
						case ASCII_CURLY_KET:
							state = 3;
						break;
						case ASCII_BACKSLASH:
							state = 6;
						break;
						default:
						// state=state;
						break;
						}
					break;
					case 3:
						if (ch == ASCII_SHARP) {
							if (depth == 1) {
								return index;
							} else if (depth < 1) {
								return NONE_INDEX;
							}
							--depth;
						}
						state = 2;
					break;
					case 5:
						if (ch == ASCII_CURLY_BRA) {
							++depth;
						}
						state = 2;
					break;
					case 6:
						state = 2;
					break;
					default:
						throw new Error("unknown state=" + state);
					}
					ch = input.get(index++);
				}
			}
			return NONE_INDEX;
		}
	}

	public void test_1() throws IOException {
		{
			StringInput input = new StringInput("## line");
			Debug.log().debug(Comment.parse(input, 0));
		}
		{
			StringInput input = new StringInput("#{ level-1#{ level-2 }#}#");
			Debug.log().debug(Comment.parse(input, 0));
		}
		{
			StringInput input = new StringInput(" \t\n\r");
			Debug.log().debug(WhiteBlock.parse(input, 0));
		}
		{
			String hello = "hello";
			StringInput input = new StringInput(
					" \t\n\r#{ level-1#{ level-2 }#}### line\n #{##}#" + hello);
			long index = IgnorableBlock.parse(input, 0);
			Assert.assertEquals(hello, input.toString().substring((int) index));
		}
		{
			StringInput input = new StringInput("hello_123$");
			long index = Variable.parse(input, 0);
			Assert.assertEquals("$", input.toString().substring((int) index));
		}
		{
			StringInput input = new StringInput("x+(y=z);x=y+z#{comment}#+w$");
			long index = EquationBlock.parse(input, 0);
			Assert.assertEquals("$", input.toString().substring((int) index));
		}
	}
	public void test_2() throws IOException {
		{
			// File file = new File("data/book-2.txt");
			File file = new File("data/SimpleTest.txt");
			FileInputStream stream = null;
			try {
				stream = new FileInputStream(file);
				StreamInput input = new StreamInput(stream, 1024);
				int BOM = input.isBOM() ? 3 : 0;
				long index = IgnorableBlock.parse(input, BOM);
				index = EquationBlock.parse(input, index, true);
				Debug.log().debug("read " + index + " bytes");
			} finally {
				FileHelper.close(stream);
			}
		}
	}
	@SuppressWarnings("unused")
	public void testStreamInput() throws IOException {
		if (true) {
			Debug.log().debug(Integer.MAX_VALUE);
		}
		if (true) {
			File file = new File("data/book-2.txt");
			FileInputStream stream = null;
			try {
				stream = new FileInputStream(file);
				StreamInput input = new StreamInput(stream, 8);
				long i = 0;
				for (; true; ++i) {
					int ch = input.get(i);
					if (ch == Phrase.END_OF_INPUT) {
						break;
					}
					if (2299778 - 100 < i) {
						System.out.print(AsciiHelper.asciiToChar((byte) ch, '?'));
					}
				}
				Debug.log().debug("read " + i + " bytes");
			} finally {
				FileHelper.close(stream);
			}
		}
	}
	public void testFileSize() throws IOException {
		File file = new File("data/ec1.pdf");
		FileInputStream stream = null;
		long actual = 0;
		try {
			stream = new FileInputStream(file);
			StreamInput input = new StreamInput(stream, 1024);
			for (; true; ++actual) {
				int ch = input.get(actual);
				if (ch == Phrase.END_OF_INPUT) {
					break;
				}
			}
		} finally {
			FileHelper.close(stream);
		}
		String msg = "file size exptected=" + file.length() + ", actual=" + actual
				+ " in bytes";
		Debug.log().debug(msg);
	}
}
