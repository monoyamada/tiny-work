package parser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import tiny.function.Function;
import tiny.lang.Debug;
import tiny.lang.StringHelper;
import tiny.primitive.IntArrayList;
import tiny.primitive.LongFraction;

public class ParserTest_2 extends TestCase {
	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
		super.setUp();
	}

	static class ParserException extends IOException {
		private static final long serialVersionUID = -1571377627724278609L;
		int row;
		int column;

		public ParserException() {
			super();
		}
		public ParserException(String message, Throwable cause) {
			super(message, cause);
		}
		public ParserException(String message) {
			super(message);
		}
		public ParserException(Throwable cause) {
			super(cause);
		}
		public ParserException(int row, int column, String msg) {
			super(msg);
			this.row = row;
			this.column = column;
		}
		public int getRow() {
			return this.row;
		}
		public int getColumn() {
			return this.column;
		}
		@Override
		public String toString() {
			String loc = "[" + this.row + ":" + this.column + "]";
			String msg = this.getLocalizedMessage();
			return (msg != null) ? (loc + " " + msg) : loc;
		}
		static String toString(int ch) {
			return 0 <= ch ? (ch <= Character.MAX_VALUE ? Character
					.toString((char) ch) : Integer.toString(ch)) : "EOF";
		}
		static ParserException unexpectedInput(int row, int col, int actual,
				String... expecteds) {
			String msg = "unexpected character, expecteds={";
			for (int i = 0, n = expecteds != null ? expecteds.length : 0; i < n; ++i) {
				if (i != 0) {
					msg += ", ";
				}
				msg += expecteds[i];
			}
			msg += "} but actual=" + ParserException.toString(actual);
			return new ParserException(row, col, msg);
		}
		static ParserException nonOpenBraket(int row, int col, int closed) {
			String msg = "no open bracket for=" + ParserException.toString(closed);
			return new ParserException(row, col, msg);
		}
		static ParserException mismatchMark(int row, int col, int actual,
				int... expecteds) {
			String msg = "mismatched mark, expecteds={";
			for (int i = 0, n = expecteds != null ? expecteds.length : 0; i < n; ++i) {
				if (i != 0) {
					msg += ", ";
				}
				msg += Integer.toString(expecteds[i]);
			}
			msg += "} but actual=" + Integer.toString(actual);
			return new ParserException(row, col, msg);
		}
	}

	static class Token {
		static final Token[] EMPTY_ARRAY = {};

		static final int NONE_TYPE = 0;
		static final int LPAREN_TYPE = NONE_TYPE + 1;
		static final int RPAREN_TYPE = LPAREN_TYPE + 1;
		static final int VARIABLE_TYPE = RPAREN_TYPE + 1;
		static final int NUMBER_TYPE = VARIABLE_TYPE + 1;
		static final int INFIX_TYPE = NUMBER_TYPE + 1;
		static final int PREFIX_TYPE = INFIX_TYPE + 1;
		static final int APPLY_TYPE = PREFIX_TYPE + 1;

		public static final Token LPAREN = new Token(LPAREN_TYPE, "");
		public static final Token RPAREN = new Token(RPAREN_TYPE, "");
		public static final Token APPLY = new Token(APPLY_TYPE, "");

		static String typeName(int type) {
			switch (type) {
			case Token.NONE_TYPE:
				return "none";
			case Token.LPAREN_TYPE:
				return "lparen";
			case Token.RPAREN_TYPE:
				return "rparen";
			case Token.VARIABLE_TYPE:
				return "variable";
			case Token.NUMBER_TYPE:
				return "number";
			case Token.INFIX_TYPE:
				return "infix";
			case Token.PREFIX_TYPE:
				return "prefix";
			case Token.APPLY_TYPE:
				return "apply";
			default:
				return "unknown";
			}
		}

		static Token newVariable(String text) {
			return new Token(VARIABLE_TYPE, text);
		}
		static Token newInfix(String text) {
			return new Token(INFIX_TYPE, text);
		}
		static Token newPrefix(String text) {
			return new Token(PREFIX_TYPE, text);
		}
		public static Token newNumber(String text) {
			return new Token(Long.parseLong(text));
		}

		final int type;
		final String text;
		final long number;

		private Token(int type, String text) {
			this.type = type;
			this.text = text;
			this.number = 0;
		}
		private Token(long number) {
			this.type = NUMBER_TYPE;
			this.text = "";
			this.number = number;
		}
		@Override
		public String toString() {
			String value = this.text;
			if (this.type == Token.NUMBER_TYPE) {
				value = Long.toString(this.number);
			}
			return value + ":" + Token.typeName(this.type);
		}
	}

	static class Parser_1 {
		private static final int MARK_BINARY = 1;
		private static final int MARK_OPEN = 2;
		private static final int MARK_INFIX = 4;
		private static final int MARK_PREFIX_OUTER = 3;
		private static final int MARK_PREFIX_INNER = 5;

		static final int B_0 = 0;
		static final int B_1 = B_0 + 1;
		static final int U_0 = B_1 + 1;
		static final int U_1 = U_0 + 1;
		static final int U_2 = U_1 + 1;
		static final int U_3 = U_2 + 1;
		static final int U_4 = U_3 + 1;

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
			case U_2:
				return "U_2";
			case U_3:
				return "U_3";
			case U_4:
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
			if (Parser_1.isAlphabet(ch)) {
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
			return Parser_1.isVariableFirst(ch) || Parser_1.isDigit(ch);
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
			return Parser_1.isOperatorFirst(ch);
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

		static Token[] parse(Reader reader) throws IOException {
			final int CH_EOF = -1;
			final int CH_OPEN = '(';
			final int CH_CLOSE = ')';

			int row = 0;
			int col = 0;
			List<Token> tokens = new ArrayList<Token>(1024);
			IntArrayList stack = new IntArrayList(1024);
			StringBuilder buffer = new StringBuilder(1024);
			boolean parsing = true;
			boolean next = true;
			int ch = -1;
			int state = B_0;

			while (parsing) {
				if (next) {
					ch = reader.read();
					if (ch == '\n') {
						col = 0;
						++row;
					} else {
						++col;
					}
				}
				Debug.log().debug(
						"state=" + stateName(state) + ", char="
								+ ParserException.toString(ch));
				next = true;
				switch (state) {
				case B_0:
					if (Parser_1.isSpace(ch)) {
						state = state;
					} else {
						stack.push(MARK_BINARY);
						state = U_0;
						next = false;
					}
				break;
				case B_1:
					if (ch == CH_EOF) {
						parsing = false;
					} else if (Parser_1.isSpace(ch)) {
						state = state;
					} else if (ch == CH_CLOSE) {
						if (stack.isEmpty()) {
							throw ParserException.nonOpenBraket(row, col, ch);
						} else if (stack.top(-1) != MARK_OPEN) {
							throw ParserException.mismatchMark(row, col, ch, MARK_OPEN);
						}
						stack.pop();
						tokens.add(Token.RPAREN);
						state = U_1;
					} else {
						throw ParserException.unexpectedInput(row, col, ch,
								"bracket-close", "EOF");
					}
				break;
				case U_0:
					if (Parser_1.isSpace(ch)) {
						state = state;
					} else if (Parser_1.isVariableFirst(ch)) {
						buffer.setLength(0);
						buffer.append((char) ch);
						state = U_2;
					} else if (Parser_1.isDigit(ch)) {
						buffer.setLength(0);
						buffer.append((char) ch);
						state = U_4;
					} else if (Parser_1.isOperatorFirst(ch)) {
						buffer.setLength(0);
						buffer.append((char) ch);
						stack.push(MARK_PREFIX_OUTER);
						stack.push(MARK_PREFIX_INNER);
						state = U_3;
					} else if (ch == CH_OPEN) {
						tokens.add(Token.LPAREN);
						stack.push(MARK_OPEN);
						state = B_0;
					} else {
						throw ParserException.unexpectedInput(row, col, ch, "variable",
								"operator", "bracket-open");
					}
				break;
				case U_1:
					while (stack.top(-1) == MARK_PREFIX_OUTER) {
						stack.pop();
					}
					if (ch == CH_EOF) {
						if (stack.top(-1) == MARK_BINARY) {
							stack.pop();
							state = B_1;
							next = false;
						} else {
							parsing = false;
						}
					} else if (Parser_1.isSpace(ch)) {
						state = state;
					} else if (ch == CH_OPEN) {
						tokens.add(Token.APPLY);
						tokens.add(Token.LPAREN);
						stack.push(MARK_OPEN);
						state = B_0;
					} else if (ch == CH_CLOSE) {
						if (stack.size() < 2) {
							throw ParserException.nonOpenBraket(row, col, ch);
						}
						if (stack.top(-1) != MARK_BINARY) {
							throw ParserException.mismatchMark(row, col, stack.top(-1),
									MARK_BINARY);
						}
						stack.pop();
						if (stack.top(-1) != MARK_OPEN) {
							throw ParserException.mismatchMark(row, col, ch, MARK_OPEN);
						}
						stack.pop();
						tokens.add(Token.RPAREN);
						state = state;
					} else if (Parser_1.isOperatorFirst(ch)) {
						if (stack.isEmpty()) {
							throw ParserException.nonOpenBraket(row, col, ch);
						} else if (stack.top(-1) != MARK_BINARY) {
							throw ParserException.mismatchMark(row, col, ch, MARK_BINARY);
						}
						stack.pop();
						buffer.setLength(0);
						buffer.append((char) ch);
						stack.push(MARK_INFIX);
						state = U_3;
					} else {
						throw ParserException.unexpectedInput(row, col, ch, "operator",
								"bracket-open", "bracket-close", "EOF");
					}
				break;
				case U_2:
					if (Parser_1.isVariableLast(ch)) {
						buffer.append((char) ch);
						state = state;
					} else {
						tokens.add(Token.newVariable(buffer.toString()));
						state = U_1;
						next = false;
					}
				break;
				case U_3:
					if (Parser_1.isOperatorLast(ch)) {
						buffer.append((char) ch);
						state = state;
					} else if (stack.isEmpty()) {
						throw ParserException.mismatchMark(row, col, ch, MARK_PREFIX_INNER,
								MARK_INFIX);
					} else if (stack.top(-1) == MARK_PREFIX_INNER) {
						stack.pop();
						tokens.add(Token.newPrefix(buffer.toString()));
						state = U_0;
						next = false;
					} else if (stack.top(-1) == MARK_INFIX) {
						stack.pop();
						tokens.add(Token.newInfix(buffer.toString()));
						state = B_0;
						next = false;
					} else {
						throw ParserException.unexpectedInput(row, col, ch, "operator");
					}
				break;
				case U_4:
					if (Parser_1.isDigit(ch)) {
						buffer.append((char) ch);
						state = state;
					} else {
						tokens.add(Token.newNumber(buffer.toString()));
						state = U_1;
						next = false;
					}
				break;
				default:
					throw new ParserException(row, col, "nknown state=" + state);
				}
			}
			if (0 < stack.size()) {
				throw ParserException.mismatchMark(row, col, ch);
			}
			return tokens.toArray(Token.EMPTY_ARRAY);
		}
	}

	static interface NumberOperator {
		int arity();
		void doit(List<LongFraction> stack, int index);

		static class Value implements NumberOperator {
			final long value;

			Value(long value) {
				this.value = value;
			}
			@Override
			public int arity() {
				return 0;
			}
			@Override
			public void doit(List<LongFraction> stack, int index) {
				stack.add(new LongFraction(this.value));
			}
		}

		static abstract class Unary implements NumberOperator {
			@Override
			public int arity() {
				return 1;
			}
		}

		static class Negate extends Unary {
			@Override
			public void doit(List<LongFraction> stack, int index) {
				LongFraction x = stack.get(index);
				stack.set(index, x.minus());
			}
		}

		static abstract class Binary implements NumberOperator {
			@Override
			public int arity() {
				return 2;
			}
		}

		static class Plus extends Binary {
			@Override
			public void doit(List<LongFraction> stack, int index) {
				LongFraction x = stack.get(index);
				LongFraction y = stack.get(index + 1);
				stack.set(index, x.plus(y));
				stack.remove(index + 1);
			}
		}

		static class Minus extends Binary {
			@Override
			public void doit(List<LongFraction> stack, int index) {
				LongFraction x = stack.get(index);
				LongFraction y = stack.get(index + 1);
				stack.set(index, x.minus(y));
				stack.remove(index + 1);
			}
		}

		static class Divides extends Binary {
			@Override
			public void doit(List<LongFraction> stack, int index) {
				LongFraction x = stack.get(index);
				LongFraction y = stack.get(index + 1);
				stack.set(index, x.divides(y));
				stack.remove(index + 1);
			}
		}

		static class Modulus extends Binary {
			@Override
			public void doit(List<LongFraction> stack, int index) {
				LongFraction x = stack.get(index);
				LongFraction y = stack.get(index + 1);
				if (x.isInteger() && y.isInteger()) {
					stack.set(index,
							new LongFraction(x.getIntegerPart() % y.getIntegerPart()));
					stack.remove(index + 1);
				} else {
					throw new UnsupportedOperationException("mudulus for non-integer");
				}
			}
		}

		static class Powers extends Binary {
			@Override
			public void doit(List<LongFraction> stack, int index) {
				LongFraction x = stack.get(index);
				LongFraction y = stack.get(index + 1);
				if (y.isInteger()) {
					stack.set(index, x.powers((int) y.getIntegerPart()));
					stack.remove(index + 1);
				}
			}
		}
	}

	public void testParse() throws IOException {
		final Function<Token, String> outToken = new Function<Token, String>() {
			@Override
			public String evaluate(Token x) throws Exception {
				return "[" + (x != null ? x.toString() : "null") + "]";
			}
		};
		if (true) {
			StringReader reader = new StringReader("-x + (a + b*(c+d ^ e- --f)) * g");
			Token[] tokens = Parser_1.parse(reader);
			Debug.log().debug(StringHelper.join(tokens, "", outToken));
		}
		if (true) {
			StringReader reader = new StringReader("(f+g)(x,y,z)(a,b)(987)");
			Token[] tokens = Parser_1.parse(reader);
			Debug.log().debug(StringHelper.join(tokens, "", outToken));
		}
	}
}
