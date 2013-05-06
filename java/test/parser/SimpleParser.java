package parser;

import java.io.IOException;
import java.io.Reader;

import tiny.lang.ArrayHelper;
import tiny.lang.Debug;
import tiny.primitive.IntArrayList;

public class SimpleParser {
	public static final int NONE_TYPE = 0;
	public static final int BRA_TYPE = NONE_TYPE + 1;
	public static final int KET_TYPE = BRA_TYPE + 1;
	public static final int VARIABLE_TYPE = KET_TYPE + 1;
	public static final int NUMBER_TYPE = VARIABLE_TYPE + 1;
	public static final int INFIX_TYPE = NUMBER_TYPE + 1;
	public static final int PREFIX_TYPE = INFIX_TYPE + 1;
	public static final int APPLY_TYPE = PREFIX_TYPE + 1;

	private static final int MARK_BINARY = 1;
	private static final int MARK_INFIX = 2;
	private static final int MARK_PREFIX_OUTER = 3;
	private static final int MARK_PREFIX_INNER = 4;
	private static final int MARK_BRA_ROUND = 5;
	private static final int MARK_BRA_SQUARE = 6;
	private static final int MARK_BRA_CURLY = 7;

	private static char BRA_ROUND = '(';
	private static char KET_ROUND = ')';
	private static char BRA_SQUARE = '[';
	private static char KET_SQUARE = ']';
	private static char BRA_CURLY = '{';
	private static char KET_CURLY = '}';

	private static final int B_0 = 0;
	private static final int B_1 = B_0 + 1;
	private static final int U_0 = B_1 + 1;
	private static final int U_1 = U_0 + 1;
	private static final int VARIABLE_READING = U_1 + 1;
	private static final int U_3 = VARIABLE_READING + 1;
	private static final int NUMBER_READING = U_3 + 1;

	static class ParserException extends IOException {
		private static final long serialVersionUID = -7199321214842182601L;

		static ParserException unexpectedInput(int row, int col, String actual) {
			return unexpectedInput(row, col, actual, ArrayHelper.EMPTY_STRING_ARRAY);
		}
		static ParserException unexpectedInput(int row, int col, String actual,
				String... expecteds) {
			String msg = null;
			if (expecteds == null || expecteds.length < 1) {
				msg = "unexpected input=" + (actual != null ? actual : "?");
			} else {
				msg = "unexpected input, expected={";
				for (int i = 0, n = expecteds.length; i < n; ++i) {
					if (i != 0) {
						msg += (actual != null ? actual : "?");
					}
					String token = expecteds[i];
					msg += (token != null ? token : "?");
				}
				msg += "} but actual=" + (actual != null ? actual : "?");
			}
			return new ParserException(row, col, msg);
		}
		static ParserException unmatchedKet(int row, int col, String ket) {
			String msg = "unmatched ket=" + (ket != null ? ket : "?");
			return new ParserException(row, col, msg);
		}
		static ParserException mismatchMark(int row, int col, int actual, int expected) {
			String msg = "mismatch marking, expected=" + Integer.toString(expected)
					+ " but actual=" + Integer.toString(actual);
			return new ParserException(row, col, msg);
		}

		private final int row;
		private final int column;

		ParserException(int row, int column, String msg) {
			this(row, column, msg, null);
		}
		ParserException(int row, int column, String msg, Throwable cause) {
			super(msg, cause);
			this.row = row;
			this.column = column;
		}
		int getRow() {
			return this.row;
		}
		int getColumn() {
			return this.column;
		}
	}

	private static String toString(int ch) {
		if (0 <= ch && ch <= Character.MAX_VALUE) {
			return Character.toString((char) ch);
		}
		return Integer.toString(ch);
	}

	public static String typeName(int type) {
		switch (type) {
		case NONE_TYPE:
			return "none";
		case BRA_TYPE:
			return "bra";
		case KET_TYPE:
			return "ket";
		case VARIABLE_TYPE:
			return "variable";
		case NUMBER_TYPE:
			return "number";
		case INFIX_TYPE:
			return "infix";
		case PREFIX_TYPE:
			return "prefix";
		case APPLY_TYPE:
			return "apply";
		default:
			return "unknown";
		}
	}
	static String stateName(int value) {
		switch (value) {
		case B_0:
			return "B_0";
		case B_1:
			return "B_1";
		case U_0:
			return "U_0";
		case U_1:
			return "U_1";
		case VARIABLE_READING:
			return "U_2";
		case U_3:
			return "U_3";
		case NUMBER_READING:
			return "U_4";
		default:
			return "unknown state";
		}
	}

	static boolean isAlphabet(int ch) {
		return 'a' <= ch && ch <= 'z' ? true : 'A' <= ch && ch <= 'Z';
	}
	static boolean isDigit(int ch) {
		return '0' <= ch && ch <= '9';
	}
	static boolean isVariableFirst(int ch) {
		if (isAlphabet(ch)) {
			return true;
		}
		switch (ch) {
		case '_':
			return true;
		default:
			return false;
		}
	}
	static boolean isVariableLast(int ch) {
		return isVariableFirst(ch) || isDigit(ch);
	}

	static boolean isOperatorFirst(int ch) {
		switch (ch) {
		case '+':
		case '-':
		case '*':
		case '/':
		case '%':
		case '^':
		case '&':
		case '|':
		case '=':
		case '!':
		case '<':
		case '>':
			return true;
		case ',':
			return true;
		default:
			return false;
		}
	}
	static boolean isOperatorLast(int ch) {
		return isOperatorFirst(ch);
	}

	static boolean isSpace(int ch) {
		switch (ch) {
		case '\n':
		case '\r':
		case '\t':
		case ' ':
			return true;
		default:
			return false;
		}
	}

	static boolean popMark(IntArrayList stack, int mark) {
		if (stack.isEmpty()) {
			return false;
		} else if (stack.isTop(mark)) {
			stack.pop();
			return true;
		}
		return false;
	}

	int row;
	int column;

	int getRow() {
		return this.row;
	}
	int getColumn() {
		return this.column;
	}
	void setRowColumn(int row, int col) {
		this.row = row;
		this.column = col;
	}
	@SuppressWarnings("unused")
	public void parse(Reader reader) throws IOException {
		final int CH_EOF = -1;
		final int CH_EOL = '\n';

		final String braRound = Character.toString(BRA_ROUND);
		final String ketRound = Character.toString(KET_ROUND);
		final String braSquare = Character.toString(BRA_SQUARE);
		final String ketSquare = Character.toString(KET_SQUARE);
		final String braCurly = Character.toString(BRA_CURLY);
		final String ketCurly = Character.toString(KET_CURLY);

		int row = 0;
		int col = 0;
		IntArrayList stack = new IntArrayList(1024);
		StringBuilder buffer = new StringBuilder(1024);
		boolean parsing = true;
		boolean next = true;
		int ch = -1;
		int state = B_0;

		while (parsing) {
			if (next) {
				ch = reader.read();
				if (ch == CH_EOL) {
					col = 0;
					++row;
				} else {
					++col;
				}
			}
			this.setRowColumn(row, col);
			if (false) {
				Debug.log()
						.debug(
								"state=" + stateName(state) + ", char="
										+ SimpleParser.toString(ch));
			}
			next = true;
			switch (state) {
			case B_0:
				if (isSpace(ch)) {
					// state = state;
				} else if (ch == KET_ROUND) {
					if (!popMark(stack, MARK_BRA_ROUND)) {
						throw ParserException.unmatchedKet(row, col, ketRound);
					}
					this.write(KET_TYPE, ketRound);
					state = U_1;
				} else if (ch == KET_SQUARE) {
					if (!popMark(stack, MARK_BRA_SQUARE)) {
						throw ParserException.unmatchedKet(row, col, ketRound);
					}
					this.write(KET_TYPE, ketSquare);
					state = U_1;
				} else if (ch == KET_CURLY) {
					if (!popMark(stack, MARK_BRA_CURLY)) {
						throw ParserException.unmatchedKet(row, col, ketRound);
					}
					this.write(KET_TYPE, ketCurly);
					state = U_1;
				} else {
					stack.push(MARK_BINARY);
					state = U_0;
					next = false;
				}
			break;
			case B_1:
				if (ch == CH_EOF) {
					parsing = false;
				} else if (ch == KET_ROUND) {
					if (!popMark(stack, MARK_BRA_ROUND)) {
						throw ParserException.unmatchedKet(row, col, ketRound);
					}
					this.write(KET_TYPE, ketRound);
					state = U_1;
				} else if (ch == KET_SQUARE) {
					if (!popMark(stack, MARK_BRA_SQUARE)) {
						throw ParserException.unmatchedKet(row, col, ketRound);
					}
					this.write(KET_TYPE, ketSquare);
					state = U_1;
				} else if (ch == KET_CURLY) {
					if (!popMark(stack, MARK_BRA_CURLY)) {
						throw ParserException.unmatchedKet(row, col, ketRound);
					}
					this.write(KET_TYPE, ketCurly);
					state = U_1;
				} else if (isSpace(ch)) {
					// state = state;
				} else {
					throw ParserException.unexpectedInput(row, col, toString(ch),
							"bracket-close", "EOF");
				}
			break;
			case U_0:
				if (isSpace(ch)) {
					// state = state;
				} else if (isVariableFirst(ch)) {
					buffer.setLength(0);
					buffer.append((char) ch);
					state = VARIABLE_READING;
				} else if (isDigit(ch)) {
					buffer.setLength(0);
					buffer.append((char) ch);
					state = NUMBER_READING;
				} else if (isOperatorFirst(ch)) {
					buffer.setLength(0);
					buffer.append((char) ch);
					stack.push(MARK_PREFIX_OUTER);
					stack.push(MARK_PREFIX_INNER);
					state = U_3;
				} else if (ch == BRA_ROUND) {
					this.write(BRA_TYPE, braRound);
					stack.push(MARK_BRA_ROUND);
					state = B_0;
				} else if (ch == BRA_SQUARE) {
					this.write(BRA_TYPE, braSquare);
					stack.push(MARK_BRA_SQUARE);
					state = B_0;
				} else if (ch == BRA_CURLY) {
					this.write(BRA_TYPE, braCurly);
					stack.push(MARK_BRA_CURLY);
					state = B_0;
				} else {
					throw ParserException.unexpectedInput(row, col, toString(ch),
							"variable", "operator", "bracket-open");
				}
			break;
			case U_1:
				while (popMark(stack, MARK_PREFIX_OUTER)) {
				}
				if (ch == CH_EOF) {
					if (popMark(stack, MARK_BINARY)) {
						state = B_1;
						next = false;
					} else {
						parsing = false;
					}
				} else if (isSpace(ch)) {
					// state = state;
				} else if (ch == BRA_ROUND) {
					this.write(APPLY_TYPE, "");
					this.write(BRA_TYPE, braRound);
					stack.push(MARK_BRA_ROUND);
					state = B_0;
				} else if (ch == BRA_SQUARE) {
					this.write(APPLY_TYPE, "");
					this.write(BRA_TYPE, braSquare);
					stack.push(MARK_BRA_SQUARE);
					state = B_0;
				} else if (ch == BRA_CURLY) {
					this.write(APPLY_TYPE, "");
					this.write(BRA_TYPE, braCurly);
					stack.push(MARK_BRA_CURLY);
					state = B_0;
				} else if (ch == KET_ROUND) {
					if (!popMark(stack, MARK_BINARY)) {
						throw ParserException.mismatchMark(row, col, stack.top(-1),
								MARK_BINARY);
					} else if (!popMark(stack, MARK_BRA_ROUND)) {
						throw ParserException.mismatchMark(row, col, stack.top(-1),
								MARK_BRA_ROUND);
					}
					this.write(KET_TYPE, ketRound);
					// state = state;
				} else if (ch == KET_SQUARE) {
					if (!popMark(stack, MARK_BINARY)) {
						throw ParserException.mismatchMark(row, col, stack.top(-1),
								MARK_BINARY);
					} else if (!popMark(stack, MARK_BRA_SQUARE)) {
						throw ParserException.mismatchMark(row, col, stack.top(-1),
								MARK_BRA_SQUARE);
					}
					this.write(KET_TYPE, ketSquare);
					// state = state;
				} else if (ch == KET_CURLY) {
					if (!popMark(stack, MARK_BINARY)) {
						throw ParserException.mismatchMark(row, col, stack.top(-1),
								MARK_BINARY);
					} else if (!popMark(stack, MARK_BRA_CURLY)) {
						throw ParserException.mismatchMark(row, col, stack.top(-1),
								MARK_BRA_CURLY);
					}
					this.write(KET_TYPE, ketCurly);
					// state = state;
				} else if (isOperatorFirst(ch)) {
					if (!popMark(stack, MARK_BINARY)) {
						throw ParserException.mismatchMark(row, col, stack.top(-1),
								MARK_BINARY);
					}
					buffer.setLength(0);
					buffer.append((char) ch);
					stack.push(MARK_INFIX);
					state = U_3;
				} else {
					throw ParserException.unexpectedInput(row, col, toString(ch), "operator",
							"bracket-open", "bracket-close", "EOF");
				}
			break;
			case VARIABLE_READING:
				if (isVariableLast(ch)) {
					buffer.append((char) ch);
					// state = state;
				} else {
					this.write(VARIABLE_TYPE, buffer.toString());
					state = U_1;
					next = false;
				}
			break;
			case U_3:
				if (isOperatorLast(ch)) {
					buffer.append((char) ch);
					// state = state;
				} else if (popMark(stack, MARK_PREFIX_INNER)) {
					this.write(PREFIX_TYPE, buffer.toString());
					state = U_0;
					next = false;
				} else if (popMark(stack, MARK_INFIX)) {
					this.write(INFIX_TYPE, buffer.toString());
					state = B_0;
					next = false;
				} else {
					throw ParserException.unexpectedInput(row, col, toString(ch),
							"operator");
				}
			break;
			case NUMBER_READING:
				if (isDigit(ch)) {
					buffer.append((char) ch);
					// state = state;
				} else {
					this.write(NUMBER_TYPE, buffer.toString());
					state = U_1;
					next = false;
				}
			break;
			default:
				throw new ParserException(row, col, "unknown state=" + state);
			}
		}
		if (0 < stack.getLength()) {
			throw ParserException.unexpectedInput(row, col, toString(ch));
		}
	}
	protected void write(int type, String text) throws IOException {
	}
}
