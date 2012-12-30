package parser;

import java.io.IOException;
import java.io.Reader;
import java.util.Collection;

public class SimpleReader {
	static abstract class Token {
		public static final int VARIABLE = 1;
		public static final int INTEGER = VARIABLE + 1;
		public static final int ASSINGS = VARIABLE + 1;
		public static final int PLUS = ASSINGS + 1;
		public static final int TIMES = PLUS + 1;
		public static final int POWERS = TIMES + 1;
		public static final int EXP_DELIM = POWERS + 1;
		public static final int PAREN_L = EXP_DELIM + 1;
		public static final int PAREN_R = PAREN_L + 1;
		public static final int GE_0 = PAREN_R + 1;
		public static final int GE_1 = GE_0 + 1;
		public static final int LE_1 = GE_1 + 1;

		public abstract int what();
	}

	static class Variable extends Token {
		String value;

		public Variable(String value) {
			this.value = value;
		}
		@Override
		public int what() {
			return Token.VARIABLE;
		}
	}

	static class Z extends Token {
		long value;

		public Z(long value) {
			this.value = value;
		}
		@Override
		public int what() {
			return Token.INTEGER;
		}
	}

	static class Assings extends Token {
		static final Assings INSTANCE = new Assings();

		@Override
		public int what() {
			return Token.ASSINGS;
		}
	}

	static class Plus extends Token {
		static final Plus INSTANCE = new Plus();

		@Override
		public int what() {
			return Token.PLUS;
		}
	}

	static class Times extends Token {
		static final Times INSTANCE = new Times();

		@Override
		public int what() {
			return Token.TIMES;
		}
	}

	static class Powers extends Token {
		static final Powers INSTANCE = new Powers();

		@Override
		public int what() {
			return Token.POWERS;
		}
	}

	static class ParenL extends Token {
		static final ParenL INSTANCE = new ParenL();

		@Override
		public int what() {
			return Token.PAREN_L;
		}
	}

	static class ParenR extends Token {
		static final ParenR INSTANCE = new ParenR();

		@Override
		public int what() {
			return Token.PAREN_R;
		}
	}

	static class Ge0 extends Token {
		static final Ge0 INSTANCE = new Ge0();

		@Override
		public int what() {
			return Token.GE_0;
		}
	}

	static class Ge1 extends Token {
		static final Ge1 INSTANCE = new Ge1();

		@Override
		public int what() {
			return Token.GE_1;
		}
	}

	static class Le1 extends Token {
		static final Le1 INSTANCE = new Le1();

		@Override
		public int what() {
			return Token.LE_1;
		}
	}

	static class ExpDelim extends Token {
		static final ExpDelim INSTANCE = new ExpDelim();

		@Override
		public int what() {
			return Token.EXP_DELIM;
		}
	}

	@SuppressWarnings("serial")
	static class ReaderException extends IOException {
		int line;
		int column;

		public ReaderException(String message, int line, int column) {
			super(message);
			this.line = line;
			this.column = column;
		}
		public String toString() {
			return '[' + line + ':' + column + "] " + super.toString();
		}
	}

	protected void tokenize(Collection<Token> output, Reader reader)
			throws IOException {
		final int V_i = 0 + 1;
		final int V_o = V_i + 1;
		final int V_s = V_o + 1;
		final int V_ic = V_s + 1;
		final int V_sc = V_ic + 1;
		final int D_i = V_sc + 1;
		final int D_o = D_i + 1;
		final int D_s = D_o + 1;
		final int D_ic = D_s + 1;
		final int D_oc = D_ic + 1;

		final char cEol = '\n';
		final char cAssigns = '=';
		final char cExpDelim = ';';
		final char cPlus = '+';
		final char cTimes = '*';
		final char cPowers = '^';
		final char cGe0 = '*';
		final char cGe1 = '+';
		final char cLe1 = '?';
		final char cComment = '#';
		final char cParenL = '(';
		final char cParenR = ')';

		StringBuilder buffer = new StringBuilder();
		int state = V_i;
		int line = 0;
		int column = 0;
		int ci = reader.read();
		while (0 <= ci) {
			char ch = (char) ci;
			switch (ch) {
			case cEol:
				++line;
				column = 0;
			break;
			default:
			break;
			}
			switch (state) {
			case V_i:
				switch (ch) {
				case cComment:
					state = V_ic;
				break;
				case cExpDelim:
					output.add(ExpDelim.INSTANCE);
				break;
				case cParenL:
					output.add(ParenL.INSTANCE);
				break;
				default:
					if (Character.isWhitespace(ch)) {
					} else if (Character.isJavaIdentifierStart(ch)) {
						buffer.delete(0, buffer.length());
						buffer.append(ch);
						state = V_o;
					} else {
						throw new ReaderException("unexpected alphabet=" + ch, line + 1,
								column + 1);
					}
				}
			break;
			case D_i:
				switch (ch) {
				case cComment:
					state = D_ic;
				break;
				case cGe0:
					output.add(Ge0.INSTANCE);
					state = D_o;
				break;
				case cGe1:
					output.add(Ge1.INSTANCE);
					state = D_o;
				break;
				case cLe1:
					output.add(Le1.INSTANCE);
					state = D_o;
				break;
				default:
					if (Character.isWhitespace(ch)) {
					} else if ('1' <= ch && ch <= '9') {
						output.add(new Z(ch));
						state = D_o;
					} else {
						throw new ReaderException("unexpected alphabet=" + ch, line + 1,
								column + 1);
					}
				}
			break;
			case V_o:
				switch (ch) {
				case cComment:
					output.add(new Variable(buffer.toString()));
					state = V_sc;
				break;
				case cExpDelim:
					output.add(new Variable(buffer.toString()));
					output.add(ExpDelim.INSTANCE);
					state = V_i;
				break;
				case cAssigns:
					output.add(new Variable(buffer.toString()));
					output.add(Assings.INSTANCE);
					state = V_i;
				case cPlus:
					output.add(new Variable(buffer.toString()));
					output.add(Plus.INSTANCE);
					state = V_i;
				break;
				case cTimes:
					output.add(new Variable(buffer.toString()));
					output.add(Times.INSTANCE);
					state = V_i;
				break;
				case cPowers:
					output.add(new Variable(buffer.toString()));
					output.add(Powers.INSTANCE);
					state = D_i;
				break;
				case cParenL:
					output.add(new Variable(buffer.toString()));
					output.add(ParenL.INSTANCE);
					state = V_i;
				break;
				case cParenR:
					output.add(new Variable(buffer.toString()));
					output.add(ParenR.INSTANCE);
					state = V_s;
				break;
				default:
					if (Character.isWhitespace(ch)) {
						output.add(new Variable(buffer.toString()));
						state = V_s;
					} else if (Character.isJavaIdentifierPart(ch)) {
						buffer.append(ch);
					} else {
						throw new ReaderException("unexpected alphabet=" + ch, line + 1,
								column + 1);
					}
				}
			break;
			case V_s:
				switch (ch) {
				case cComment:
					state = V_sc;
				break;
				case cExpDelim:
					output.add(ExpDelim.INSTANCE);
					state = V_i;
				break;
				case cAssigns:
					output.add(Assings.INSTANCE);
					state = V_i;
				break;
				case cPlus:
					output.add(Plus.INSTANCE);
					state = V_i;
				break;
				case cTimes:
					output.add(Times.INSTANCE);
					state = V_i;
				break;
				case cPowers:
					output.add(Powers.INSTANCE);
					state = D_i;
				break;
				case cParenL:
					output.add(ParenL.INSTANCE);
					state = V_i;
				break;
				case cParenR:
					output.add(ParenR.INSTANCE);
				break;
				default:
					if (Character.isWhitespace(ch)) {
					} else if (Character.isJavaIdentifierStart(ch)) {
						output.add(Times.INSTANCE);
						buffer.delete(0, buffer.length());
						buffer.append(ch);
						state = V_o;
					} else {
						throw new ReaderException("unexpected alphabet=" + ch, line + 1,
								column + 1);
					}
				}
			break;
			case V_ic:
				switch (ch) {
				case cEol:
					state = V_i;
				break;
				default:
				break;
				}
			break;
			case V_sc:
				switch (ch) {
				case cEol:
					state = V_s;
				break;
				default:
				break;
				}
			break;
			case D_o:
				switch (ch) {
				case cComment:
					state = D_oc;
				break;
				case cExpDelim:
					output.add(ExpDelim.INSTANCE);
					state = V_i;
				break;
				case cPlus:
					output.add(Plus.INSTANCE);
					state = V_i;
				break;
				case cTimes:
					output.add(Times.INSTANCE);
					state = V_i;
				break;
				case cPowers:
					output.add(Powers.INSTANCE);
					state = D_i;
				break;
				case cParenL:
					output.add(new Variable(buffer.toString()));
					output.add(ParenL.INSTANCE);
					state = V_i;
				break;
				case cParenR:
					output.add(ParenR.INSTANCE);
					state = V_s;
				break;
				default:
					if (Character.isWhitespace(ch)) {
						state = V_s;
					} else {
						throw new ReaderException("unexpected alphabet=" + ch, line + 1,
								column + 1);
					}
				}
			break;
			case D_s:
				switch (ch) {
				case cComment:
					state = D_oc;
				break;
				case cExpDelim:
					output.add(ExpDelim.INSTANCE);
					state = V_i;
				break;
				case cAssigns:
					output.add(Assings.INSTANCE);
					state = V_i;
				break;
				case cPlus:
					output.add(Plus.INSTANCE);
					state = V_i;
				break;
				case cTimes:
					output.add(Times.INSTANCE);
					state = V_i;
				break;
				case cPowers:
					output.add(Powers.INSTANCE);
					state = D_i;
				break;
				case cParenL:
					output.add(ParenL.INSTANCE);
					state = V_i;
				break;
				case cParenR:
					output.add(ParenR.INSTANCE);
				break;
				default:
					if (Character.isWhitespace(ch)) {
					} else if (Character.isJavaIdentifierStart(ch)) {
						output.add(Times.INSTANCE);
						buffer.delete(0, buffer.length());
						buffer.append(ch);
						state = V_o;
					} else {
						throw new ReaderException("unexpected alphabet=" + ch, line + 1,
								column + 1);
					}
				}
			break;
			case D_ic:
				switch (ch) {
				case cEol:
					state = D_i;
				break;
				default:
				break;
				}
			break;
			case D_oc:
				switch (ch) {
				case cEol:
					state = D_s;
				break;
				default:
				break;
				}
			break;
			default:
				throw new ReaderException("unknown state=" + state, line + 1,
						column + 1);
			}
			ci = reader.read();
			++column;
		}
	}
}
