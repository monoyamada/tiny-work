package study.monoid;

import study.lang.Messages;
import study.monoid.KlSemiringFactory.IfNode;
import study.monoid.KlSemiringFactory.Symbol;

/**
 * <code>
 * Ea = Em + Em '+' Ea; //plus
 * Em = Ep + Ep '.' Em; //multiplies
 * Ep = Et + Et '^' En; //powers
 * Et = Es + '(' Ea ')'; //unary expression
 * Es = (alphabet + special) (alphabet + special + digit)^*; //variable
 * En = digit^+ + '*' + '+' + '?'; //power factor
 * alphabet = 'a'..'z' + 'A'..'Z';
 * special = '_';
 * digit = '0'..'9';
 * </code>
 *
 * @param expr
 * @return
 */
public class KlSemiringParser {
	private static final char ZERO_MORE = '*';
	private static final char ONE_MORE = '+';
	private static final char ZERO_OR_ONE = '?';
	private static final char POWERS = '^';
	private static final char MULTIPLIES = '.';
	private static final char PLUS = '+';
	private static final char OPEN = '(';
	private static final char CLOSE = ')';

	protected static class ParserData {
		final KlSemiringFactory factory;
		final String expression;
		int begin;
		int end;
		IfNode output;

		protected ParserData(KlSemiringFactory factory, String expression) {
			this.factory = factory;
			this.expression = expression;
		}
		public IfNode getOne() {
			return this.factory.getOne();
		}
		public IfNode getSymbol(String value) {
			return this.factory.getSymbol(value);
		}
	}

	protected IfNode parse(KlSemiringFactory factory, String expr) {
		assert factory != null;
		if (expr.length() < 1) {
			return factory.getOne();
		}
		final ParserData data = new ParserData(factory, expr);
		int end = expr.length();
		data.begin = this.skipWhitespace(expr, 0, end);
		data.end = end;
		this.parseEa(data);
		end = this.skipWhitespace(expr, data.begin, data.end);
		if (end != data.end) {
			String msg = Messages.getUnexpectedValue("expression", "", "[" + end
					+ "] " + expr);
			throw new IllegalArgumentException(msg);
		}
		return data.output;
	}
	protected void parseEa(ParserData data) {
		this.parseEm(data);
		final String expr = data.expression;
		data.begin = this.skipWhitespace(expr, data.begin, data.end);
		if (data.begin == data.end) {
			return;
		}
		if (expr.charAt(data.begin) == KlSemiringParser.PLUS) {
			++data.begin;
			final IfNode node = data.output;
			this.parseEa(data);
			data.output = node.plus(data.output);
		}
	}
	protected void parseEm(ParserData data) {
		this.parseEp(data);
		final String expr = data.expression;
		data.begin = this.skipWhitespace(expr, data.begin, data.end);
		if (data.begin == data.end) {
			return;
		} else if (expr.charAt(data.begin) == KlSemiringParser.MULTIPLIES) {
			++data.begin;
			final IfNode node = data.output;
			this.parseEm(data);
			data.output = node.multiplies(data.output);
		}
	}
	protected void parseEp(ParserData data) {
		this.parseEt(data);
		final String expr = data.expression;
		data.begin = this.skipWhitespace(expr, data.begin, data.end);
		if (data.begin == data.end) {
			return;
		} else if (expr.charAt(data.begin) == KlSemiringParser.POWERS) {
			++data.begin;
			IfNode node = data.output;
			this.parseEn(data);
			if (data.output instanceof Symbol) {
				final Symbol symbol = (Symbol) data.output;
				final String value = symbol.getValue();
				if (this.equalChar(value, KlSemiringParser.ZERO_MORE)) {
					data.output = node.stars();
					return;
				} else if (this.equalChar(value, KlSemiringParser.ONE_MORE)) {
					data.output = node.multiplies(node.stars());
					return;
				} else if (this.equalChar(value, KlSemiringParser.ZERO_OR_ONE)) {
					data.output = node.plus(data.getOne());
					return;
				} else {
					try {
						final int number = Integer.parseInt(value);
						data.output = node.powers(number);
						return;
					} catch (NumberFormatException ex) {
						ex.printStackTrace();
						String msg = Messages.getUnexpectedValue("power",
								KlSemiringParser.POWERS, "[" + data.begin + "] " + expr);
						throw new IllegalArgumentException(msg);
					}
				}
			}
		}
	}
	protected void parseEt(ParserData data) {
		final String expr = data.expression;
		data.begin = this.skipWhitespace(expr, data.begin, data.end);
		if (data.begin == data.end) {
			return;
		} else if (expr.charAt(data.begin) == KlSemiringParser.OPEN) {
			++data.begin;
			this.parseEa(data);
			data.begin = this.skipWhitespace(expr, data.begin, data.end);
			if (data.begin < data.end
					&& expr.charAt(data.begin) == KlSemiringParser.CLOSE) {
				++data.begin;
				return;
			}
			String msg = Messages.getUnexpectedValue("brace", KlSemiringParser.CLOSE,
					"[" + data.begin + "] " + expr);
			throw new IllegalArgumentException(msg);
		}
		this.parseEs(data);
	}
	protected void parseEs(ParserData data) {
		final String expr = data.expression;
		final int begin = this.skipWhitespace(expr, data.begin, data.end);
		if (begin == data.end) {
			String msg = Messages.getUnexpectedValue("symbol", "alphabet", "["
					+ data.end + "] " + expr);
			throw new IllegalArgumentException(msg);
		}
		int end = begin;
		for (int n = data.end; end < n; ++end) {
			final char ch = expr.charAt(end);
			if (this.isAlphabet(ch)) {
				continue;
			} else if (this.isSpecial(ch)) {
				continue;
			} else if (this.isDigit(ch)) {
				if (begin == end) {
					String msg = Messages.getUnexpectedValue("symbol", "alphabet", "["
							+ end + "] " + expr);
					throw new IllegalArgumentException(msg);
				}
				continue;
			}
			break;
		}
		if (begin == end) {
			String msg = Messages.getUnexpectedValue("symbol", "alphabet", "[" + end
					+ "] " + expr);
			throw new IllegalArgumentException(msg);
		}
		final String value = expr.substring(begin, end);
		data.output = data.getSymbol(value);
		data.begin = end;
	}
	protected void parseEn(ParserData data) {
		final String expr = data.expression;
		final int begin = this.skipWhitespace(expr, data.begin, data.end);
		if (begin == data.end) {
			String msg = Messages.getUnexpectedValue("symbol", "alphabet", "["
					+ data.end + "] " + expr);
			throw new IllegalArgumentException(msg);
		}
		switch (expr.charAt(begin)) {
		case KlSemiringParser.ZERO_MORE:
		case KlSemiringParser.ONE_MORE:
		case KlSemiringParser.ZERO_OR_ONE:
			data.output = data.getSymbol(Character.toString(expr.charAt(begin)));
			data.begin = begin + 1;
			return;
		default:
			break;
		}
		int end = begin;
		for (int n = data.end; end < n; ++end) {
			if (this.isDigit(expr.charAt(begin))) {
				continue;
			}
			String msg = Messages.getUnexpectedValue("digit", "0-9", "[" + end + "] "
					+ expr);
			throw new IllegalArgumentException(msg);
		}
		final String value = expr.substring(begin, end);
		data.output = data.getSymbol(value);
		data.begin = end;
	}
	protected int skipWhitespace(String text, int begin, int end) {
		for (; begin < end && Character.isWhitespace(text.charAt(begin)); ++begin) {
		}
		return begin;
	}
	protected boolean equalChar(String text, char letter) {
		if (text == null || text.length() != 1) {
			return false;
		}
		return text.charAt(0) == letter;
	}
	protected boolean isAlphabet(char value) {
		return ('a' <= value && value <= 'z') || ('A' <= value && value <= 'Z');
	}
	protected boolean isSpecial(char value) {
		switch (value) {
		case '_':
			return true;
		default:
			return false;
		}
	}
	protected boolean isDigit(char value) {
		return '0' <= value && value <= '9';
	}
}
