package parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import tiny.lang.Debug;

public class PackratTest extends TestCase {
	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
		super.setUp();
	}

	static class ResultNone {
		public static final int TYPE_NONE = 0;
		public static final int TYPE_CHAR = TYPE_NONE + 1;
		public static final int TYPE_INT = TYPE_CHAR + 1;

		static String typeName(int type) {
			switch (type) {
			case TYPE_NONE:
				return "none";
			case TYPE_CHAR:
				return "char";
			case TYPE_INT:
				return "int";
			default:
				return "unknown";
			}
		}

		int count;

		ResultNone(Parser parser) {
			this.count = parser.count++;
		}
		@Override
		public String toString() {
			StringBuilder buffer = new StringBuilder();
			try {
				this.toString(buffer);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			return buffer.toString();
		}
		Appendable toString(Appendable output) throws IOException {
			output.append("type=");
			output.append(ResultNone.typeName(this.type()));
			return output;
		}
		int type() {
			return ResultNone.TYPE_NONE;
		}
		Column next() {
			return null;
		}
	}

	static class ResultValue extends ResultNone {
		final Column next;

		public ResultValue(Parser parser, Column next) {
			super(parser);
			this.next = next;
		}
		@Override
		Column next() {
			return this.next;
		}
		@Override
		Appendable toString(Appendable output) throws IOException {
			super.toString(output);
			output.append(", next=");
			output.append(Integer.toString(this.next().index));
			return output;
		}
	}

	static class ResultInt extends ResultValue {
		final int value;

		public ResultInt(Parser parser, int value, Column next) {
			super(parser, next);
			this.value = value;
		}
		@Override
		Appendable toString(Appendable output) throws IOException {
			super.toString(output);
			output.append(", value=");
			output.append(Integer.toString(this.value));
			return output;
		}
		@Override
		int type() {
			return ResultNone.TYPE_INT;
		}
	}

	static class ResultChar extends ResultValue {
		final char value;

		public ResultChar(Parser parser, char value, Column next) {
			super(parser, next);
			this.value = value;
		}
		@Override
		Appendable toString(Appendable output) throws IOException {
			super.toString(output);
			output.append(", value=");
			output.append(Character.toString(this.value));
			return output;
		}
		@Override
		int type() {
			return ResultNone.TYPE_CHAR;
		}
	}

	static class Column {
		ResultNone additive;
		ResultNone multitive;
		ResultNone primary;
		ResultNone digit;
		ResultNone raw;
		final Parser parser;
		final int index;

		public String toMarkdown() {
			StringBuilder buffer = new StringBuilder();
			try {
				this.toMarkdown(buffer);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			return buffer.toString();
		}
		Appendable toMarkdown(Appendable output) throws IOException {
			this.toMarkdown(output, this.additive);
			this.toMarkdown(output.append(" | "), this.multitive);
			this.toMarkdown(output.append(" | "), this.primary);
			this.toMarkdown(output.append(" | "), this.digit);
			this.toMarkdown(output.append(" | "), this.raw);
			return output;
		}
		Appendable toMarkdown(Appendable output, ResultNone result)
				throws IOException {
			if (result != null) {
				switch (result.type()) {
				case ResultNone.TYPE_NONE:
					return output.append(Integer.toString(result.count)).append(": ")
							.append("none");
				case ResultNone.TYPE_CHAR:
					ResultChar x = (ResultChar) result;
					return output.append(Integer.toString(result.count)).append(": ")
							.append(Character.toString(x.value)).append(" -> ")
							.append(Integer.toString(result.next().index));
				case ResultNone.TYPE_INT:
					ResultInt y = (ResultInt) result;
					return output.append(Integer.toString(result.count)).append(": ")
							.append(Integer.toString(y.value)).append(" -> ")
							.append(Integer.toString(result.next().index));
				default:
					return output.append(Integer.toString(result.count)).append(": ")
							.append("unknown");
				}
			}
			return output;
		}
		@Override
		public String toString() {
			StringBuilder buffer = new StringBuilder();
			try {
				this.toString(buffer);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			return buffer.toString();
		}
		Appendable toString(Appendable output) throws IOException {
			this.toString(output.append("additive="), this.additive);
			this.toString(output.append(", multitive="), this.multitive);
			this.toString(output.append(", primary="), this.primary);
			this.toString(output.append(", digit="), this.digit);
			this.toString(output.append(", raw="), this.raw);
			return output;
		}
		Appendable toString(Appendable output, ResultNone result)
				throws IOException {
			if (result == null) {
				output.append("null");
			} else {
				output.append("(");
				result.toString(output);
				output.append(")");
			}
			return output;
		}
		public Column(Parser parser, int index) {
			this.parser = parser;
			this.index = index;
		}
		public boolean isRaw(char ch) {
			ResultNone result = this.getRaw();
			if (result.type() == ResultNone.TYPE_CHAR) {
				ResultChar x = (ResultChar) result;
				return x.value == ch;
			}
			return false;
		}
		public Column nextColumn(char ch) {
			if (this.isRaw(ch)) {
				return this.getRaw().next();
			}
			return null;
		}
		public ResultNone getAdditive() {
			if (this.additive == null) {
				this.additive = this.newAdditive();
			}
			return this.additive;
		}
		public ResultNone newAdditive() {
			ResultNone result = this.getMultitive();
			if (result.type() == ResultNone.TYPE_INT) {
				ResultInt x = (ResultInt) result;
				Column next = x.next().nextColumn('+');
				if (next != null) {
					result = next.getAdditive();
					if (result.type() == ResultNone.TYPE_INT) {
						ResultInt y = (ResultInt) result;
						return this.newResultInt(x.value + y.value, y.next());
					}
				}
			}
			return this.getMultitive();
		}
		public ResultNone getMultitive() {
			if (this.multitive == null) {
				this.multitive = this.newMultitive();
			}
			return this.multitive;
		}
		public ResultNone newMultitive() {
			ResultNone result = this.getPrimary();
			if (result.type() == ResultNone.TYPE_INT) {
				ResultInt x = (ResultInt) result;
				Column next = x.next().nextColumn('*');
				if (next != null) {
					result = next.getMultitive();
					if (result.type() == ResultNone.TYPE_INT) {
						ResultInt y = (ResultInt) result;
						return this.newResultInt(x.value * y.value, y.next());
					}
				}
			}
			return this.getPrimary();
		}
		public ResultNone getPrimary() {
			if (this.primary == null) {
				this.primary = this.newPrimary();
			}
			return this.primary;
		}
		ResultNone newPrimary() {
			Column next = this.nextColumn('(');
			if (next != null) {
				ResultNone result = next.getAdditive();
				if (result.type() == ResultNone.TYPE_INT) {
					ResultInt x = (ResultInt) result;
					next = x.next().nextColumn(')');
					if (next != null) {
						return this.newResultInt(x.value, next);
					}
				}
			}
			return this.getDigit();
		}
		public ResultNone getDigit() {
			if (this.digit == null) {
				this.digit = this.newDigit();
			}
			return this.digit;
		}
		ResultNone newDigit() {
			ResultNone result = this.getRaw();
			if (result.type() == ResultNone.TYPE_CHAR) {
				ResultChar x = (ResultChar) result;
				if ('0' <= x.value && x.value <= '9') {
					return this.newResultInt(x.value - '0', x.next());
				}
			}
			return this.newResultNone();
		}
		ResultNone getRaw() {
			if (this.raw == null) {
				this.raw = this.newRaw();
			}
			return this.raw;
		}
		ResultNone newRaw() {
			Parser parser = this.parser;
			String input = parser.input;
			if (this.index < input.length()) {
				return this.newResultChar(input.charAt(this.index),
						parser.getColumn(this.index + 1));
			}
			return this.newResultNone();
		}
		ResultNone newResultNone() {
			return new ResultNone(this.parser);
		}
		ResultChar newResultChar(char value, Column next) {
			return new ResultChar(this.parser, value, next);
		}
		ResultInt newResultInt(int value, Column next) {
			return new ResultInt(this.parser, value, next);
		}
	}

	static class Parser {
		String input;
		List<Column> table;
		int count;

		Column parse(String text) {
			this.input = text;
			this.table = new ArrayList<Column>();
			this.count = 0;
			Column column = new Column(this, 0);
			this.table.add(column);
			return column;
		}
		Column getColumn(int index) {
			while (this.table.size() <= index) {
				this.table.add(new Column(this, this.table.size()));
			}
			return this.table.get(index);
		}
	}

	public void testParser() {
		Parser parser = new Parser();
		Debug.log().debug(parser.parse("2*(3+5)").getAdditive());
		dumpTable(parser);
		Debug.log().debug(parser.parse("(2*(3+5)+7)*5+3*2").getAdditive());
		dumpTable(parser);
	}

	static void dumpTable(Parser parser) {
		System.out
				.println("| index | character | | additive | multitive | primary | digit | raw |");
		System.out.println("|---|---|---|---|---|---|---|---|");
		List<Column> cols = parser.table;
		for (int i = 0, n = cols.size(); i < n; ++i) {
			char c = '$';
			if (i < parser.input.length()) {
				c = parser.input.charAt(i);
			}
			System.out.println("| " + i + " | " + c + " | | "
					+ cols.get(i).toMarkdown() + " |");
		}
	}
}
