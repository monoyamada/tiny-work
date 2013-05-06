package parser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import junit.framework.TestCase;
import tiny.function.Function;
import tiny.lang.Debug;
import tiny.lang.StringHelper;
import tiny.primitive.LongArrayList;
import tiny.primitive.LongStack;

public class SimpleParserTest extends TestCase {
	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
		super.setUp();
	}

	public void test() {
		if (true) {
			int x = 1;
			int y = 3;
			Debug.log().debug((x --- y) + " (x, y)=(" + x + ", " + y + ")");
		}
	}

	static class Token {
		public static final Token[] EMPTY_ARRAY = {};
		int type;
		String text;

		public Token(int type, String text) {
			this.type = type;
			this.text = text;
		}
		@Override
		public String toString() {
			String value = this.text;
			return value + ":" + SimpleParser.typeName(this.type);
		}
	}

	static class TestParser extends SimpleParser {
		List<Token> tokens = new ArrayList<Token>(32);

		@Override
		protected void write(int type, String text) {
			this.tokens.add(new Token(type, text));
		}
		public Token[] read(Reader reader) throws IOException {
			this.tokens.clear();
			this.parse(reader);
			return this.tokens.toArray(Token.EMPTY_ARRAY);
		}
	}

	public void testParse() throws IOException {
		final Function<Token, String> outToken = new Function<Token, String>() {
			@Override
			public String evaluate(Token x) throws Exception {
				return "[" + (x != null ? x.toString() : "null") + "]";
			}
		};
		final TestParser parser = new TestParser();
		if (true) {
			StringReader reader = new StringReader("-x + (a + b*(c+d ^ e- --f)) * g");
			Token[] tokens = parser.read(reader);
			Debug.log().debug(StringHelper.join(tokens, "", outToken));
		}
		if (true) {
			StringReader reader = new StringReader("(f+g)(x,y,z)(a,b)(987)");
			Token[] tokens = parser.read(reader);
			Debug.log().debug(StringHelper.join(tokens, "", outToken));
		}
		if (true) {
			StringReader reader = new StringReader("({}[])");
			Token[] tokens = parser.read(reader);
			Debug.log().debug(StringHelper.join(tokens, "", outToken));
		}
	}

	static class NumberOperation {
		static final NumberOperation[] EMPTY_ARRAY = {};

		static final int LEFT_ASSOCIATIVE = 0;
		static final int RIGHT_ASSOCIATIVE = LEFT_ASSOCIATIVE + 1;

		static final int UNARY_PRIORITY = 2000;
		static final int BINARY_PRIORITY = 1000;
		static final int BRAKET_PRIORITY = 0;

		static final Operation BRA = new Bra();
		static final Operation KET = new Ket();
		static final Operation NEGATES = new Negates();
		static final Operation PLUS = new Plus();
		static final Operation MINUS = new Minus();
		static final Operation MULTIPLIES = new Multiplies();
		static final Operation DIVIDES = new Divides();
		static final Operation MODULUS = new Modulus();
		static final Operation POWERS = new Powers();

		int associativity() {
			return LEFT_ASSOCIATIVE;
		}
		int priority() {
			return Integer.MIN_VALUE;
		}
		void apply(LongStack stack) {
		}

		static class Value extends NumberOperation {
			final long value;

			Value(long value) {
				this.value = value;
			}
			int priority() {
				return Integer.MAX_VALUE;
			}
			void apply(LongStack stack) {
				stack.push(this.value);
			}
			@Override
			public String toString() {
				return Long.toString(this.value);
			}
		}

		static class Operation extends NumberOperation {
			long pop(LongStack stack) {
				if (stack.isEmpty()) {
					throw new NoSuchElementException("empty stack");
				}
				long x = stack.top(-1);
				stack.pop();
				return x;
			}
		}

		static class Bra extends Operation {
			int priority() {
				return BRAKET_PRIORITY;
			}
			@Override
			public String toString() {
				return "(";
			}
		}

		static class Ket extends Operation {
			int priority() {
				return BRAKET_PRIORITY;
			}
			@Override
			public String toString() {
				return ")";
			}
		}

		static class Negates extends Operation {
			int priority() {
				return UNARY_PRIORITY + 10;
			}
			void apply(LongStack stack) {
				stack.push(-pop(stack));
			}
			@Override
			public String toString() {
				return "-";
			}
		}

		static class Plus extends Operation {
			int priority() {
				return BINARY_PRIORITY + 10;
			}
			void apply(LongStack stack) {
				stack.push(pop(stack) + pop(stack));
			}
			@Override
			public String toString() {
				return "+";
			}
		}

		static class Minus extends Operation {
			int priority() {
				return BINARY_PRIORITY + 10;
			}
			void apply(LongStack stack) {
				long x = pop(stack);
				stack.push(pop(stack) - x);
			}
			@Override
			public String toString() {
				return "-";
			}
		}

		static class Multiplies extends Operation {
			int priority() {
				return BINARY_PRIORITY + 20;
			}
			void apply(LongStack stack) {
				stack.push(pop(stack) * pop(stack));
			}
			@Override
			public String toString() {
				return "*";
			}
		}

		static class Divides extends Operation {
			int priority() {
				return BINARY_PRIORITY + 20;
			}
			void apply(LongStack stack) {
				long x = pop(stack);
				stack.push(pop(stack) / x);
			}
			@Override
			public String toString() {
				return "/";
			}
		}

		static class Modulus extends Operation {
			int priority() {
				return BINARY_PRIORITY + 20;
			}
			void apply(LongStack stack) {
				long x = pop(stack);
				stack.push(pop(stack) % x);
			}
			@Override
			public String toString() {
				return "%";
			}
		}

		static class Powers extends Operation {
			@Override
			int associativity() {
				return RIGHT_ASSOCIATIVE;
			}
			@Override
			int priority() {
				return BINARY_PRIORITY + 30;
			}
			void apply(LongStack stack) {
				long x = pop(stack);
				stack.push(Math.round(Math.pow(pop(stack), x)));
			}
			@Override
			public String toString() {
				return "^";
			}
		}
	}

	static class NumberParser extends SimpleParser {
		NumberOperation.Operation infix(char symbol) {
			switch (symbol) {
			case '+':
				return NumberOperation.PLUS;
			case '-':
				return NumberOperation.MINUS;
			case '*':
				return NumberOperation.MULTIPLIES;
			case '/':
				return NumberOperation.DIVIDES;
			case '%':
				return NumberOperation.MODULUS;
			case '^':
				return NumberOperation.POWERS;
			default:
				return null;
			}
		}

		List<NumberOperation> tokens = new ArrayList<NumberOperation>(32);
		List<NumberOperation> yard = new ArrayList<NumberOperation>(32);

		void popBra() throws ParserException {
			int n = this.yard.size();
			while (0 < n--) {
				if (this.yard.get(n) == NumberOperation.BRA) {
					this.yard.remove(n);
					break;
				}
				this.tokens.add(this.yard.remove(n));
			}
			if (n < 0) {
				throw new ParserException(this.row, this.column, "mismatched )");
			}
		}
		void pushOperation(NumberOperation op) {
			if (op.associativity() == NumberOperation.LEFT_ASSOCIATIVE) {
				int n = this.yard.size();
				while (0 < n-- && op.priority() <= this.yard.get(n).priority()) {
					this.tokens.add(this.yard.remove(n));
				}
			} else {
				int n = this.yard.size();
				while (0 < n-- && op.priority() < this.yard.get(n).priority()) {
					this.tokens.add(this.yard.remove(n));
				}
			}
			this.yard.add(op);
		}
		@Override
		protected void write(int type, String text) throws IOException {
			if (type == SimpleParser.NUMBER_TYPE) {
				NumberOperation op = new NumberOperation.Value(Long.parseLong(text));
				this.tokens.add(op);
			} else if (type == SimpleParser.BRA_TYPE) {
				if (text.length() != 1 || text.charAt(0) != '(') {
					throw ParserException.unexpectedInput(this.row, this.column, text,
							"(");
				}
				this.yard.add(NumberOperation.BRA);
			} else if (type == SimpleParser.KET_TYPE) {
				if (text.length() != 1 || text.charAt(0) != ')') {
					throw ParserException.unexpectedInput(this.row, this.column, text,
							")");
				}
				popBra();
			} else if (type == SimpleParser.PREFIX_TYPE) {
				if (text.length() != 1 && text.charAt(0) != '-') {
					throw ParserException.unexpectedInput(this.row, this.column, text,
							"-");
				}
				pushOperation(NumberOperation.NEGATES);
			} else if (type == SimpleParser.INFIX_TYPE) {
				if (text.length() != 1) {
					throw ParserException.unexpectedInput(this.row, this.column, text);
				}
				NumberOperation op = infix(text.charAt(0));
				if (op == null) {
					throw ParserException.unexpectedInput(this.row, this.column, text);
				}
				pushOperation(op);
			} else {
				String msg = "unsupported type=" + SimpleParser.typeName(type);
				throw new ParserException(this.row, this.column, msg);
			}
		}
		public NumberOperation[] read(Reader reader) throws IOException {
			this.tokens.clear();
			this.parse(reader);
			int n = this.yard.size();
			while (0 < n--) {
				this.tokens.add(this.yard.remove(n));
			}
			return this.tokens.toArray(NumberOperation.EMPTY_ARRAY);
		}
	}

	@SuppressWarnings("unused")
	public void testNumber() throws IOException {
		final NumberParser parser = new NumberParser();
		if (false) {
			StringReader reader = new StringReader("3-4^5");
			NumberOperation[] tokens = parser.read(reader);
			Debug.log().debug(StringHelper.join(tokens, ","));
			LongArrayList stack = new LongArrayList(32);
			for (int i = 0, n = tokens.length; i < n; ++i) {
				tokens[i].apply(stack);
			}
			Debug.log().debug(StringHelper.join(stack, ","));
		}
		if (true) {
			StringReader reader = new StringReader("(-1+(2))*3-4^5+6*7");
			NumberOperation[] tokens = parser.read(reader);
			Debug.log().debug(StringHelper.join(tokens, ","));
			LongArrayList stack = new LongArrayList(32);
			for (int i = 0, n = tokens.length; i < n; ++i) {
				tokens[i].apply(stack);
			}
			Debug.log().debug(StringHelper.join(stack, ","));
		}
		if (true) {
			StringReader reader = new StringReader("2 + ( 1 - 5 )*3");
			NumberOperation[] tokens = parser.read(reader);
			Debug.log().debug(StringHelper.join(tokens, ","));
			LongArrayList stack = new LongArrayList(32);
			for (int i = 0, n = tokens.length; i < n; ++i) {
				tokens[i].apply(stack);
			}
			Debug.log().debug(StringHelper.join(stack, ","));
		}
		if (true) {
			StringReader reader = new StringReader(
					" 3 + 4 *( 2 - ( 1 - 5 ) ^ 2)^2 ^ 3");
			NumberOperation[] tokens = parser.read(reader);
			Debug.log().debug(StringHelper.join(tokens, ","));
			LongArrayList stack = new LongArrayList(32);
			for (int i = 0, n = tokens.length; i < n; ++i) {
				tokens[i].apply(stack);
			}
			Debug.log().debug(StringHelper.join(stack, ","));
		}
	}
}
