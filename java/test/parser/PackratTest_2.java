package parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import tiny.lang.Debug;
import tiny.lang.Messages;

public class PackratTest_2 extends TestCase {
	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
		super.setUp();
	}

	public static abstract class Node {
		public static final int TYPE_ADDITIVE = 0;
		public static final int TYPE_MULTITIVE = TYPE_ADDITIVE + 1;
		public static final int TYPE_PRIMARY = TYPE_MULTITIVE + 1;
		public static final int TYPE_BLOCK = TYPE_PRIMARY + 1;
		public static final int TYPE_NUMBER = TYPE_BLOCK + 1;
		public static final int TYPE_VARIABLE = TYPE_NUMBER + 1;
		public static final int TYPE_NONE = TYPE_VARIABLE + 1;
		public static final int TYPE_RAW = TYPE_NONE + 1;

		public static String getTypeName(Node node) {
			if (node == null) {
				return "null";
			}
			return Node.getTypeName(node.getType());
		}
		public static String getTypeName(int type) {
			switch (type) {
			case TYPE_ADDITIVE:
				return "additive";
			case TYPE_MULTITIVE:
				return "multitive";
			case TYPE_PRIMARY:
				return "primary";
			case TYPE_BLOCK:
				return "block";
			case TYPE_NUMBER:
				return "number";
			case TYPE_VARIABLE:
				return "variable";
			case TYPE_NONE:
				return "none";
			case TYPE_RAW:
				return "raw";
			default:
				return "unknown=" + type;
			}
		}
		public abstract int getType();
		public int sizeChild() {
			return 0;
		}
		public Node getChild(int index) {
			if (index < 0 || this.sizeChild() <= index) {
				throw new IndexOutOfBoundsException(Messages.getIndexOutOfRange(0,
						index, this.sizeChild()));
			}
			return this.doGetChild(index);
		}
		Node doGetChild(int index) {
			return null;
		}
		public abstract int getBegin();
		public abstract int getEnd();

		public boolean isNumber() {
			return false;
		}
		public long getNumber(long def) {
			return def;
		}
	}

	static abstract class Node_1 extends Node {
		final Node node;

		Node_1(Node node) {
			this.node = node;
		}
		@Override
		public int sizeChild() {
			return 1;
		}
		@Override
		Node doGetChild(int index) {
			switch (index) {
			case 0:
				return this.node;
			default:
				return null;
			}
		}
		@Override
		public int getBegin() {
			return this.node.getBegin();
		}
		@Override
		public int getEnd() {
			return this.node.getEnd();
		}
	}

	static abstract class Node_2 extends Node {
		final Node node_0;
		final Node node_1;

		Node_2(Node node_0, Node node_1) {
			this.node_0 = node_0;
			this.node_1 = node_1;
		}
		@Override
		public int sizeChild() {
			return 2;
		}
		@Override
		Node doGetChild(int index) {
			switch (index) {
			case 0:
				return this.node_0;
			case 1:
				return this.node_1;
			default:
				return null;
			}
		}
		@Override
		public int getBegin() {
			return this.node_0.getBegin();
		}
		@Override
		public int getEnd() {
			return this.node_1.getEnd();
		}
	}

	static abstract class Node_3 extends Node {
		final Node node_0;
		final Node node_1;
		final Node node_2;

		Node_3(Node node_0, Node node_1, Node node_2) {
			this.node_0 = node_0;
			this.node_1 = node_1;
			this.node_2 = node_2;
		}
		@Override
		public int sizeChild() {
			return 3;
		}
		@Override
		Node doGetChild(int index) {
			switch (index) {
			case 0:
				return this.node_0;
			case 1:
				return this.node_1;
			case 2:
				return this.node_2;
			default:
				return null;
			}
		}
		@Override
		public int getBegin() {
			return this.node_0.getBegin();
		}
		@Override
		public int getEnd() {
			return this.node_2.getEnd();
		}
		@Override
		public boolean isNumber() {
			switch (this.getType()) {
			case TYPE_ADDITIVE:
			case TYPE_MULTITIVE:
				return this.node_0.isNumber() && this.node_2.isNumber();
			case TYPE_BLOCK:
				return this.node_1.isNumber();
			default:
				return false;
			}
		}
		@Override
		public long getNumber(long def) {
			switch (this.getType()) {
			case TYPE_ADDITIVE:
				if (this.node_0.isNumber() && this.node_2.isNumber()) {
					return this.node_0.getNumber(def) + this.node_2.getNumber(def);
				}
			break;
			case TYPE_MULTITIVE:
				if (this.node_0.isNumber() && this.node_2.isNumber()) {
					return this.node_0.getNumber(def) * this.node_2.getNumber(def);
				}
			break;
			case TYPE_BLOCK:
				if (this.node_1.isNumber()) {
					return this.node_1.getNumber(def);
				}
			break;
			default:
			break;
			}
			return def;
		}
	}

	static class Additive extends Node_3 {
		Additive(Node node_0, Node node_1, Node node_2) {
			super(node_0, node_1, node_2);
		}
		@Override
		public int getType() {
			return TYPE_ADDITIVE;
		}
		@Override
		public boolean isNumber() {
			return this.node_0.isNumber() && this.node_2.isNumber();
		}
		@Override
		public long getNumber(long def) {
			if (this.isNumber()) {
				return this.node_0.getNumber(def) + this.node_2.getNumber(def);
			}
			return def;
		}
	}

	static class Multitive extends Node_3 {
		Multitive(Node node_0, Node node_1, Node node_2) {
			super(node_0, node_1, node_2);
		}
		@Override
		public int getType() {
			return TYPE_MULTITIVE;
		}
		@Override
		public boolean isNumber() {
			return this.node_0.isNumber() && this.node_2.isNumber();
		}
		@Override
		public long getNumber(long def) {
			if (this.isNumber()) {
				return this.node_0.getNumber(def) * this.node_2.getNumber(def);
			}
			return def;
		}
	}

	static class Block extends Node_3 {
		Block(Node node_0, Node node_1, Node node_2) {
			super(node_0, node_1, node_2);
		}
		@Override
		public int getType() {
			return TYPE_BLOCK;
		}
		@Override
		public boolean isNumber() {
			return this.node_1.isNumber();
		}
		@Override
		public long getNumber(long def) {
			if (this.isNumber()) {
				return this.node_1.getNumber(def);
			}
			return def;
		}
	}

	static class Number extends Node_2 {
		final long value;

		Number(Node node_0, Node node_1, long value) {
			super(node_0, node_1);
			this.value = value;
		}
		@Override
		public int getType() {
			return Node.TYPE_NUMBER;
		}
		@Override
		public boolean isNumber() {
			return true;
		}
		@Override
		public long getNumber(long def) {
			return this.value;
		}
	}

	static class Variable extends Node_2 {
		final String value;

		Variable(Node node_0, Node node_1, String value) {
			super(node_0, node_1);
			this.value = value;
		}
		@Override
		public int getType() {
			return Node.TYPE_VARIABLE;
		}
	}

	static class None extends Node_1 {
		None(Node node) {
			super(node);
		}
		@Override
		public int getType() {
			return TYPE_NONE;
		}
	}

	static class Row extends Node {
		static boolean isDigit(int ch) {
			return '0' <= ch && ch <= '9';
		}
		static boolean isVariableFirst(int ch) {
			switch (ch) {
			case '_':
				return true;
			default:
				return ('a' <= ch && ch <= 'z') || ('A' <= ch && ch <= 'Z');
			}
		}
		static boolean isVariableLast(int ch) {
			return isVariableFirst(ch) || ('0' <= ch && ch <= '9');
		}

		final int index;
		final int value;
		Node[] nodes;
		None none;

		Row(int index, int value) {
			this.index = index;
			this.value = value;
		}
		@Override
		public int getType() {
			return Node.TYPE_RAW;
		}
		@Override
		public int getBegin() {
			return this.index;
		}
		@Override
		public int getEnd() {
			return this.index + 1;
		}
		int getIndex() {
			return this.index;
		}
		int getValue() {
			return this.value;
		}
		Row getRow(Parser parser, int index) {
			return parser.getRow(index);
		}
		Row getNextRow(Parser parser) {
			return parser.getRow(this.getIndex() + 1);
		}
		Row getPrevRow(Parser parser) {
			return parser.getRow(this.getIndex() - 1);
		}
		Node[] getNodes(boolean anyway) {
			if (this.nodes == null && anyway) {
				this.nodes = new Node[TYPE_NONE];
			}
			return this.nodes;
		}
		Node getNode(Parser parser, int type) {
			switch (type) {
			case TYPE_RAW:
				return this;
			case TYPE_NONE:
				if (this.none == null) {
					this.none = new None(this);
				}
				return this.none;
			default:
			break;
			}
			Node[] nodes = this.getNodes(true);
			if (nodes[type] == null) {
				nodes[type] = this.newNode(parser, type);
			}
			return nodes[type];
		}
		Node newNode(Parser parser, int token) {
			switch (token) {
			case TYPE_ADDITIVE: {
				Node left = this.getNode(parser, TYPE_MULTITIVE);
				if (left.getType() != TYPE_NONE) {
					Row mid = this.getRow(parser, left.getEnd());
					if (mid.getValue() == '+') {
						Node right = mid.getNextRow(parser).getNode(parser, TYPE_ADDITIVE);
						if (right.getType() != TYPE_NONE) {
							return new Additive(left, mid, right);
						}
					}
				}
				return left;
			}
			case TYPE_MULTITIVE: {
				Node left = this.getNode(parser, TYPE_PRIMARY);
				if (left.getType() != TYPE_NONE) {
					Row mid = this.getRow(parser, left.getEnd());
					if (mid.getValue() == '*') {
						Node right = mid.getNextRow(parser).getNode(parser, TYPE_MULTITIVE);
						if (right.getType() != TYPE_NONE) {
							return new Multitive(left, mid, right);
						}
					}
				}
				return left;
			}
			case TYPE_PRIMARY: {
				Node node = this.getNode(parser, TYPE_BLOCK);
				if (node.getType() != TYPE_NONE) {
					return node;
				}
				node = this.getNode(parser, TYPE_NUMBER);
				if (node.getType() != TYPE_NONE) {
					return node;
				}
				return this.getNode(parser, TYPE_VARIABLE);
			}
			case TYPE_BLOCK: {
				if (this.getValue() == '(') {
					Node mid = this.getNextRow(parser).getNode(parser, TYPE_ADDITIVE);
					if (mid.getType() != TYPE_NONE) {
						Row right = this.getRow(parser, mid.getEnd());
						if (right.getValue() == ')') {
							return new Block(this, mid, right);
						}
					}
				}
				return this.getNode(parser, TYPE_NONE);
			}
			case TYPE_NUMBER: {
				if (Row.isDigit(this.getValue())) {
					long val = this.getValue() - '0';
					Row next = this.getNextRow(parser);
					while (Row.isDigit(next.getValue())) {
						val = 10 + val + next.getValue() - '0';
						next = next.getNextRow(parser);
					}
					return new Number(this, next.getPrevRow(parser), val);
				}
				return this.getNode(parser, TYPE_NONE);
			}
			case TYPE_VARIABLE: {
				if (Row.isVariableFirst(this.getValue())) {
					StringBuilder buffer = parser.getInstantBuffer(true);
					buffer.setLength(0);
					buffer.append((char) this.getValue());
					Row next = this.getNextRow(parser);
					while (Row.isVariableLast(next.getValue())) {
						buffer.append((char) next.getValue());
						next = next.getNextRow(parser);
					}
					return new Variable(this, next.getPrevRow(parser), buffer.toString());
				}
				return this.getNode(parser, TYPE_NONE);
			}
			default:
				throw new Error("unknown token=" + token);
			}
		}
	}

	static class Parser {
		String input;
		List<Row> table;
		StringBuilder instantBuffer;

		Node parse(String text, int type) {
			Row row = this.parse(text);
			return row.getNode(this, type);
		}
		Row parse(String text) {
			this.input = text;
			this.table = new ArrayList<Row>(text.length());
			Row row = new Row(0, this.get(0));
			this.table.add(row);
			return row;
		}
		StringBuilder getInstantBuffer(boolean anyway) {
			if (this.instantBuffer == null && anyway) {
				this.instantBuffer = new StringBuilder();
			}
			return this.instantBuffer;
		}
		Row getRow(int index) {
			List<Row> table = this.table;
			while (table.size() <= index) {
				int ind = table.size();
				table.add(new Row(ind, this.get(ind)));
			}
			return table.get(index);
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
		dump(parser, "2+3");
		dump(parser, "2*(3+5)");
		dump(parser, "(2*(3+5)+7)*11+13*17");
	}
	static void dump(Parser parser, String expr) throws IOException {
		Node node = parser.parse(expr, Node.TYPE_ADDITIVE);
		dumpResult(parser, node);
		dumpTable(parser.table);
		dumpTree(node);
	}
	static void dump(Parser parser, Node node) throws IOException {
		dumpResult(parser, node);
		dumpTable(parser.table);
	}
	static void dumpTree(Node node) {
		int depth = 0;
		dumpTree(node, depth);
	}
	static void dumpTree(Node node, int depth) {
		for (int i = 0; i < depth; ++i) {
			System.out.print('\t');
		}
		switch (node.getType()) {
		case Node.TYPE_NUMBER:
			System.out.println(Node.getTypeName(node) + "=" + ((Number) node).value);
			return;
		case Node.TYPE_VARIABLE:
			System.out
					.println(Node.getTypeName(node) + "=" + ((Variable) node).value);
			return;
		case Node.TYPE_RAW:
			System.out.println(Node.getTypeName(node) + "="
					+ (char) ((Row) node).value);
			return;
		default:
			System.out.println(Node.getTypeName(node));
		break;
		}

		for (int i = 0, n = node.sizeChild(); i < n; ++i) {
			dumpTree(node.getChild(i), depth + 1);
		}
	}
	static void dumpResult(Parser parser, Node node) {
		if (node.getEnd() < parser.input.length()) {
			Debug.log().debug("failed to parse at=" + node.getEnd());
		} else if (node.isNumber()) {
			Debug.log().debug(
					"successed to parse by number=" + node.getNumber(Long.MIN_VALUE));
		} else {
			Debug.log().debug("successed to parse by expression");
		}
	}

	static void dumpTable(List<Row> table) throws IOException {
		System.out.print("| index | alphabet | |");
		for (int i = 0, n = Node.TYPE_NONE; i < n; ++i) {
			System.out.print(" ");
			System.out.print(Node.getTypeName(i));
			System.out.print(" |");
		}
		System.out.println();
		System.out.print("|---|---|---|");
		for (int i = 0, n = Node.TYPE_NONE; i < n; ++i) {
			System.out.print("---|");
		}
		System.out.println();
		StringBuilder buffer = new StringBuilder();
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
		int ch = row.getValue();
		if (Character.MIN_VALUE <= ch && ch <= Character.MAX_VALUE) {
			output.append((char) ch);
		} else {
			output.append('$');
		}
		output.append(" |");
		Node[] nodes = row.getNodes(false);
		for (int i = 0, n = Node.TYPE_NONE; i < n; ++i) {
			output.append(" | ");
			if (nodes != null) {
				Node node = nodes[i];
				if (node != null) {
					output.append(Node.getTypeName(node));
					output.append(": ");
					output.append(Integer.toString(node.getEnd()));
				}
			}
		}
		output.append(" |");
	}
}
