package parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;
import tiny.lang.Debug;

public class PackratTest_1 extends TestCase {
	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
		super.setUp();
	}

	static class Row {
		static final int INDEX_UNDEFINED = Integer.MIN_VALUE;
		static final int INDEX_NONE = -1;

		static final int TOKEN_ADDITIVE = 0;
		static final int TOKEN_MULTITIVE = TOKEN_ADDITIVE + 1;
		static final int TOKEN_PRIMARY = TOKEN_MULTITIVE + 1;
		static final int TOKEN_DIGITS = TOKEN_PRIMARY + 1;
		static final int TOKEN_VARIABLE = TOKEN_DIGITS + 1;
		static final int END_OF_TOKEN = TOKEN_VARIABLE + 1;

		final Parser parser;
		final int index;
		final int raw;
		int[] ends;

		Row(Parser parser, int index) {
			this.parser = parser;
			this.index = index;
			this.raw = parser.get(index);
		}
		Parser getParser() {
			return this.parser;
		}
		int getIndex() {
			return this.index;
		}
		int get() {
			return this.raw;
		}
		Row getRow(int index) {
			return this.getParser().getRow(index);
		}
		Row getNextRow() {
			return this.getParser().getRow(this.getIndex() + 1);
		}
		int[] getEnds(boolean anyway) {
			if (this.ends == null && anyway) {
				this.ends = new int[END_OF_TOKEN];
				Arrays.fill(this.ends, INDEX_UNDEFINED);
			}
			return this.ends;
		}
		boolean isDefined(int token){
			return 0<= token;
		}
		int getEnd(int token) {
			int[] ends = this.getEnds(true);
			if (ends[token] == INDEX_UNDEFINED) {
				ends[token] = this.newEnd(token);
			}
			return ends[token];
		}
		int newEnd(int token) {
			switch (token) {
			case TOKEN_ADDITIVE: {
				int end = this.getEnd(TOKEN_MULTITIVE);
				if (this.isDefined(end)) {
					if (this.getRow(end++).get() == '+') {
						end = this.getRow(end).getEnd(TOKEN_ADDITIVE);
						if (this.isDefined(end)) {
							return end;
						}
					}
				}
				return this.getEnd(TOKEN_MULTITIVE);
			}
			case TOKEN_MULTITIVE: {
				int end = this.getEnd(TOKEN_PRIMARY);
				if (this.isDefined(end)) {
					if (this.getRow(end++).get() == '*') {
						end = this.getRow(end).getEnd(TOKEN_MULTITIVE);
						if (this.isDefined(end)) {
							return end;
						}
					}
				}
				return this.getEnd(TOKEN_PRIMARY);
			}
			case TOKEN_PRIMARY: {
				if (this.get() == '(') {
					int end = this.getNextRow().getEnd(TOKEN_ADDITIVE);
					if (this.isDefined(end)) {
						if (this.getRow(end++).get() == ')') {
							return end;
						}
					}
				}
				int end = this.getEnd(TOKEN_DIGITS);
				if (this.isDefined(end)) {
					return end;
				}
				return this.getEnd(TOKEN_VARIABLE);
			}
			case TOKEN_DIGITS: {
				if (this.isDigit(this.get())) {
					Row row = this.getNextRow();
					while (this.isDigit(row.get())) {
						row = row.getNextRow();
					}
					return row.getIndex();
				}
				return INDEX_NONE;
			}
			case TOKEN_VARIABLE: {
				if (this.isVariableFirst(this.get())) {
					Row row = this.getNextRow();
					while (this.isVariableLast(row.get())) {
						row = row.getNextRow();
					}
					return row.getIndex();
				}
				return INDEX_NONE;
			}
			default:
				throw new Error("unknown token=" + token);
			}
		}
		boolean isDigit(int ch) {
			return '0' <= ch && ch <= '9';
		}
		boolean isVariableFirst(int ch) {
			switch (ch) {
			case '_':
				return true;
			default:
				return ('a' <= ch && ch <= 'z') || ('A' <= ch && ch <= 'Z');
			}
		}
		boolean isVariableLast(int ch) {
			return this.isVariableFirst(ch) || ('0' <= ch && ch <= '9');
		}
	}

	static class Parser {
		String input;
		List<Row> table;

		Row parse(String text) {
			this.input = text;
			this.table = new ArrayList<Row>(text.length());
			Row row = new Row(this, 0);
			this.table.add(row);
			return row;
		}
		Row getRow(int index) {
			while (this.table.size() <= index) {
				this.table.add(new Row(this, this.table.size()));
			}
			return this.table.get(index);
		}
		int get(int index) {
			if (0 <= index && index < this.input.length()) {
				return input.charAt(index);
			}
			return -1;
		}
	}

	public void testParse() throws IOException {
		Parser parser = new Parser();
		parser.parse("2*(3+5)").getEnd(Row.TOKEN_ADDITIVE);
		dumpTable(parser.table);
		parser.parse("(2*(3+5)+7)*5+3*2-").getEnd(Row.TOKEN_ADDITIVE);
		dumpTable(parser.table);
	}

	static void dumpTable(List<Row> table) throws IOException {
		StringBuilder buffer = new StringBuilder();
		buffer
				.append("| index | alphabet | | additive | multitive | primary | digits | variable |");
		System.out.println(buffer.toString());
		buffer.setLength(0);
		buffer.append("|---|---|---|---|---|---|---|---|");
		System.out.println(buffer.toString());
		for (int i = 0, n = table.size(); i < n; ++i) {
			buffer.setLength(0);
			dumpRow(buffer, table.get(i));
			System.out.println(buffer.toString());
		}
	}
	static void dumpRow(Appendable output, Row row) throws IOException {
		output.append("| ");
		output.append(Integer.toString(row.getIndex()));
		output.append(" | ");
		int ch = row.get();
		if (Character.MIN_VALUE <= ch && ch <= Character.MAX_VALUE) {
			output.append((char) ch);
		} else {
			output.append('$');
		}
		output.append(" |");
		int[] ends = row.getEnds(false);
		for (int i = 0, n = Row.END_OF_TOKEN; i < n; ++i) {
			output.append(" | ");
			if (ends != null) {
				int val = ends[i];
				switch (val) {
				case Row.INDEX_UNDEFINED:
				break;
				case Row.INDEX_NONE:
					output.append("none");
				break;
				default:
					output.append(Integer.toString(ends[i]));
				break;
				}
			}
		}
		output.append(" |");
	}
}
