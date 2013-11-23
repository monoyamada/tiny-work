package parser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import tiny.lang.Debug;
import tiny.lang.FileHelper;
import tiny.primitive.ByteArrayList;
import tiny.primitive.IntArrayList;

public class Parser_1 extends TextParser {
	protected static final int STATE_COMMENT_0 = 10;
	protected static final int STATE_COMMENT_1 = 11;
	protected static final int STATE_COMMENT_2 = 12;
	protected static final int STATE_COMMENT_3 = 13;
	protected static final int STATE_COMMENT_4 = 14;
	protected static final int STATE_COMMENT_5 = 15;
	protected static final int STATE_COMMENT_6 = 16;
	protected static final int STATE_COMMENT_7 = 17;

	private static final int MARK_COMMENT = 0;

	private static final int CH_ESCAPE = '\\';
	private static final int CH_SHARP = '#';
	private static final int CH_PAREN_L = '{';
	private static final int CH_PAREN_R = '}';
	@SuppressWarnings("unused")
	private static final int CH_MINUS = '-';
	@SuppressWarnings("unused")
	private static final int CH_PLUS = '+';
	@SuppressWarnings("unused")
	private static final int CH_SPACE = ' ';
	private static final int CH_CR = '\r';
	@SuppressWarnings("unused")
	private static final int CH_TAB = '\t';

	static class Mark extends TextParser {
		private int value;
		private String text;

		public Mark(TextParser that, int value, String text) {
			super(that);
			this.value = value;
			this.text = text;
		}
		public int getValue() {
			return this.value;
		}
		protected void setValue(int value) {
			this.value = value;
		}
		public String getText() {
			return this.text;
		}
		protected void setText(String text) {
			this.text = text;
		}
		public Appendable getReport(Appendable output) throws IOException {
			output.append(Integer.toString(this.getCount()));
			output.append(":");
			if (this.text != null) {
				output.append("\"");
				output.append(this.text);
				output.append("\"");
			}
			return output;
		}
	}

	static boolean popMark(IntArrayList stack, int mark) {
		if (stack.isEmpty()) {
			return false;
		} else if (stack.isTop(mark)) {
			stack.removeLast();
			return true;
		}
		return false;
	}
	static String toString(int ch) {
		if (0 <= ch && ch <= Character.MAX_VALUE) {
			return Character.toString((char) ch);
		}
		return Integer.toString(ch);
	}

	public boolean parseComment(InputStream reader, Charset charset)
			throws IOException {
		List<Mark> stack = new ArrayList<Mark>(1024);
		return this.parseComment(reader, charset, STATE_COMMENT_0, stack);
	}
	protected boolean parseComment(InputStream reader, Charset charset,
			int state, List<Mark> stack) throws IOException {
		ByteArrayList buffer = null;
		if (Debug.isDebug()) {
			buffer = new ByteArrayList(1024);
		}
		boolean parsing = true;
		boolean next = true;
		int ch = CH_EOF;
		while (parsing) {
			if (next) {
				ch = reader.read();
				this.plusCount(1);
				if (Debug.isDebug() && ch != CH_CR) {
					buffer.push((byte) ch);
				}
			}
			next = true;
			switch (state) {
			case STATE_COMMENT_0:
				switch (ch) {
				case CH_SHARP:
					state = STATE_COMMENT_1;
				break;
				default:
					throw this.errorUnexpectedInput(toString(ch), toString(CH_SHARP));
				}
			break;
			case STATE_COMMENT_1:
				switch (ch) {
				case CH_SHARP:
					this.addMarkTop(stack, MARK_COMMENT, "##");
					state = STATE_COMMENT_3;
					if (Debug.isDebug()) {
						Debug.log().debug(" in-line: " + this.toString(buffer, charset));
					}
				break;
				case CH_PAREN_L:
					this.addMarkTop(stack, MARK_COMMENT, "#{");
					state = STATE_COMMENT_4;
					if (Debug.isDebug()) {
						Debug.log().debug(" in-block: " + this.toString(buffer, charset));
					}
				break;
				default:
					throw this.errorUnexpectedInput(toString(ch), toString(CH_SHARP),
							toString(CH_PAREN_L));
				}
			break;
			case STATE_COMMENT_2:
				if (this.isMarkTop(stack, MARK_COMMENT)) {
					next = false;
					state = STATE_COMMENT_4;
				} else {
					parsing = false;
				}
			break;
			case STATE_COMMENT_3:
				switch (ch) {
				case CH_EOF:
				case CH_EOL:
					if (this.popMarkTop(stack, MARK_COMMENT)) {
						next = false;
						state = STATE_COMMENT_2;
					} else {
						throw this.errorNoBra("EOL", "EOF");
					}
					if (Debug.isDebug()) {
						Debug.log().debug(" out-line: " + this.toString(buffer, charset));
					}
				break;
				default:
				// state = state;
				break;
				}
			break;
			case STATE_COMMENT_4:
				switch (ch) {
				case CH_EOF:
					if (this.isMarkTop(stack, MARK_COMMENT)) {
						throw this.errorNoKet(this.getMarkTop(stack, null));
					}
					throw this.errorEOF(state);
				case CH_ESCAPE:
					state = STATE_COMMENT_6;
				break;
				case CH_PAREN_R:
					state = STATE_COMMENT_5;
				break;
				case CH_SHARP:
					state = STATE_COMMENT_7;
				break;
				default:
				// state = state;
				break;
				}
			break;
			case STATE_COMMENT_5:
				switch (ch) {
				case CH_EOF:
					if (this.isMarkTop(stack, MARK_COMMENT)) {
						throw this.errorNoKet(this.getMarkTop(stack, null));
					}
					throw this.errorEOF(state);
				case CH_SHARP:
					if (this.popMarkTop(stack, MARK_COMMENT)) {
						next = false;
						state = STATE_COMMENT_2;
					} else {
						throw this.errorNoBra("}#");
					}
					if (Debug.isDebug()) {
						Debug.log().debug(" out-block: " + this.toString(buffer, charset));
					}
				break;
				default:
					next = false;
					state = STATE_COMMENT_4;
					if (Debug.isDebug()) {
						Debug.log().debug("cancel: " + this.toString(buffer, charset));
					}
				break;
				}
			break;
			case STATE_COMMENT_6:
				switch (state) {
				case CH_EOF:
					if (this.isMarkTop(stack, MARK_COMMENT)) {
						throw this.errorNoKet(this.getMarkTop(stack, null));
					}
					throw this.errorEOF(state);
				default:
					state = STATE_COMMENT_4;
				break;
				}
			break;
			case STATE_COMMENT_7:
				switch (ch) {
				case CH_EOF:
					if (this.isMarkTop(stack, MARK_COMMENT)) {
						throw this.errorNoKet(this.getMarkTop(stack, null));
					}
					throw this.errorEOF(state);
				case CH_SHARP:
					this.addMarkTop(stack, MARK_COMMENT, "##");
					state = STATE_COMMENT_3;
					if (Debug.isDebug()) {
						Debug.log().debug(" in-line: " + this.toString(buffer, charset));
					}
				break;
				case CH_PAREN_L:
					this.addMarkTop(stack, MARK_COMMENT, "#{");
					state = STATE_COMMENT_4;
					if (Debug.isDebug()) {
						Debug.log().debug(" in-block: " + this.toString(buffer, charset));
					}
				break;
				default:
					next = false;
					state = STATE_COMMENT_4;
					if (Debug.isDebug()) {
						Debug.log().debug("cancel: " + this.toString(buffer, charset));
					}
				break;
				}
			break;
			default:
				throw this.errorUnknownState(state);
			}
		}
		return true;
	}
	private String toString(ByteArrayList array, Charset charset) {
		// Charset charset = Charset.forName(FileHelper.UTF_8);
		if (charset != null) {
			return new String(array.getArray(), 0, array.size(), charset);
		}
		return array.toString();
	}
	private Mark getMarkTop(List<Mark> stack, Mark def) {
		if (stack.size() < 1) {
			return def;
		}
		return stack.get(stack.size() - 1);
	}
	private void addMarkTop(List<Mark> stack, int mark, String text) {
		stack.add(new Mark(this, mark, text));
	}
	private boolean popMarkTop(List<Mark> stack, int mark) {
		if (stack.size() < 1) {
			return false;
		}
		int n = stack.size() - 1;
		if (stack.get(n).value == mark) {
			stack.remove(n);
			return true;
		}
		return false;
	}
	@SuppressWarnings("unused")
	private List<Mark> removeMarkTop(List<Mark> stack) {
		if (stack.size() < 1) {
			throw new NoSuchElementException("could not pop empty stack");
		}
		stack.remove(stack.size() - 1);
		return stack;
	}
	private boolean isMarkTop(List<Mark> stack, int mark) {
		if (stack.size() < 1) {
			return false;
		}
		return stack.get(stack.size() - 1).getValue() == mark;
	}
	ParserException errorEOF(int state) {
		String msg = "reached EOF while state=" + getStateName(state, "unknown");
		return new ParserException(this, msg);
	}
	ParserException errorNoBra(String... expecteds) {
		String msg = null;
		if (expecteds == null || expecteds.length < 1) {
			msg = "there was no bra.";
		} else if (expecteds.length == 1) {
			String token = expecteds[0];
			msg = "there was no bra for ket=" + (token != null ? token : "?");
		} else {
			msg = "there was no bra for kets={";
			for (int i = 0, n = expecteds.length; i < n; ++i) {
				if (i != 0) {
					msg += ", ";
				}
				String token = expecteds[i];
				msg += (token != null ? token : "?");
			}
			msg += "}";
		}
		return new ParserException(this, msg);
	}
	ParserException errorNoKet(Mark mark) {
		String msg = null;
		if (mark == null) {
			msg = "there was no ket.";
		} else {
			StringBuilder buffer = new StringBuilder(64);
			buffer.append("[");
			try {
				mark.getReport(buffer);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			buffer.append("]");
			msg = "there was no ket for a bra=" + buffer.toString();
		}
		return new ParserException(this, msg);
	}
	ParserException errorMismatchBraket(String bra, String ket) {
		String msg = "mismatched braket, bra=" + bra + " ket=" + ket;
		return new ParserException(this, msg);
	}
	ParserException errorUnknownState(int state) {
		String name = getStateName(state, null);
		if (name == null) {
			name = Integer.toString(state);
		}
		String msg = "uknown state=" + name;
		return new ParserException(this, msg);
	}
	ParserException errorUnexpectedInput(String actual, String... expecteds) {
		String msg = null;
		if (expecteds == null || expecteds.length < 1) {
			msg = "unexpected input=" + (actual != null ? actual : "?");
		} else if (expecteds.length == 1) {
			String token = expecteds[0];
			msg = "unexpected input, expected=" + (token != null ? token : "?")
					+ " but actual=" + (actual != null ? actual : "?");
		} else {
			msg = "unexpected input, expected={";
			for (int i = 0, n = expecteds.length; i < n; ++i) {
				if (i != 0) {
					msg += ", ";
				}
				String token = expecteds[i];
				msg += (token != null ? token : "?");
			}
			msg += "} but actual=" + (actual != null ? actual : "?");
		}
		return new ParserException(this, msg);
	}
	String getStateName(int state, String def) {
		switch (state) {
		case STATE_COMMENT_0:
			return "comment_0";
		case STATE_COMMENT_1:
			return "comment_1";
		case STATE_COMMENT_2:
			return "comment_2";
		case STATE_COMMENT_3:
			return "comment_3";
		case STATE_COMMENT_4:
			return "comment_4";
		case STATE_COMMENT_5:
			return "comment_5";
		case STATE_COMMENT_6:
			return "comment_6";
		case STATE_COMMENT_7:
			return "comment_7";
		default:
			return def;
		}
	}
}
