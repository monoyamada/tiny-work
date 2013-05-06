package parser.v1;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import tiny.lang.ArrayHelper;
import tiny.lang.Debug;
import tiny.lang.FileHelper;
import tiny.primitive.LongArrayList;

public class ParserTest_1_0 extends TestCase {
	@Override
	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
		super.setUp();
	}

	@SuppressWarnings("serial")
	static class ParserException extends IOException {
		public ParserException(String msg) {
			super(msg);
		}
		public ParserException(Throwable cause) {
			super(cause);
		}
	}

	/**
	 * represents semiring structure.
	 * 
	 * @param <T>
	 */
	static class ParseNode extends TreeNode<ParseNode> {
		public static final int EQUATION_LIST = 0;
		public static final int IGNORABLE_BLOCK = EQUATION_LIST + 1;
		public static final int ZERO = IGNORABLE_BLOCK + 1;
		public static final int ONE = ZERO + 1;
		public static final int POWERS = ONE + 1;
		public static final int MULTIPLIES = POWERS + 1;
		public static final int PLUS = MULTIPLIES + 1;
		public static final int ASSIGNS = PLUS + 1;
		public static final int VARIABLE = ASSIGNS + 1;
		public static final int NUMBER_VARIABLE = VARIABLE + 1;
		public static final int NUMBER = NUMBER_VARIABLE + 1;
		protected static final int NUMBER_OF_TYPES = NUMBER + 1;

		private static final Info[] INFOS = ParseNode.newInfo();

		protected interface Info {
			String typeName();
			String prefixName(Object node);
			String infixName(Object node);
			int precedence();
		}

		private static Info[] newInfo() {
			Info[] out = new Info[ParseNode.NUMBER_OF_TYPES];
			return ParseNode.newInfo(out);
		}
		protected static Info[] newInfo(Info[] out) {
			out[EQUATION_LIST] = new Info() {
				public String typeName() {
					return "equationList";
				}
				public String prefixName(Object node) {
					return node != null ? this.typeName() : null;
				}
				public String infixName(Object node) {
					return node != null ? ";" : null;
				}
				public int precedence() {
					return 5;
				}
			};
			out[IGNORABLE_BLOCK] = new Info() {
				public String typeName() {
					return "ignorableBlock";
				}
				String name(Object node) {
					return this.typeName();
				}
				public String prefixName(Object node) {
					return this.name(node);
				}
				public String infixName(Object node) {
					return this.name(node);
				}
				public int precedence() {
					return Integer.MAX_VALUE;
				}
			};
			out[ZERO] = new Info() {
				public String typeName() {
					return "zero";
				}
				String name(Object node) {
					return node != null ? "0" : null;
				}
				public String prefixName(Object node) {
					return this.name(node);
				}
				public String infixName(Object node) {
					return this.name(node);
				}
				public int precedence() {
					return Integer.MAX_VALUE;
				}
			};
			out[ONE] = new Info() {
				public String typeName() {
					return "one";
				}
				String name(Object node) {
					return node != null ? "1" : null;
				}
				public String prefixName(Object node) {
					return this.name(node);
				}
				public String infixName(Object node) {
					return this.name(node);
				}
				public int precedence() {
					return Integer.MAX_VALUE;
				}
			};
			out[POWERS] = new Info() {
				public String typeName() {
					return "powers";
				}
				public String prefixName(Object node) {
					return node != null ? this.typeName() : null;
				}
				public String infixName(Object node) {
					return node != null ? "^" : null;
				}
				public int precedence() {
					return 300;
				}
			};
			out[MULTIPLIES] = new Info() {
				public String typeName() {
					return "multiplies";
				}
				public String prefixName(Object node) {
					return node != null ? this.typeName() : null;
				}
				public String infixName(Object node) {
					return node != null ? "" : null;
				}
				public int precedence() {
					return 200;
				}
			};
			out[PLUS] = new Info() {
				public String typeName() {
					return "plus";
				}
				public String prefixName(Object node) {
					return node != null ? this.typeName() : null;
				}
				public String infixName(Object node) {
					return node != null ? "+" : null;
				}
				public int precedence() {
					return 100;
				}
			};
			out[ASSIGNS] = new Info() {
				public String typeName() {
					return "assigns";
				}
				public String prefixName(Object node) {
					return node != null ? this.typeName() : null;
				}
				public String infixName(Object node) {
					return node != null ? "=" : null;
				}
				public int precedence() {
					return 10;
				}
			};
			out[VARIABLE] = new Info() {
				public String typeName() {
					return "variable";
				}
				String name(Object node) {
					try {
						ParseNode x = (ParseNode) node;
						return x.value;
					} catch (Exception ex) {
					}
					return null;
				}
				public String prefixName(Object node) {
					return this.name(node);
				}
				public String infixName(Object node) {
					return this.name(node);
				}
				public int precedence() {
					return Integer.MAX_VALUE;
				}
			};
			out[NUMBER_VARIABLE] = new Info() {
				public String typeName() {
					return "numberVariable";
				}
				String name(Object node) {
					try {
						ParseNode x = (ParseNode) node;
						return x.value;
					} catch (Exception ex) {
					}
					return null;
				}
				public String prefixName(Object node) {
					return this.name(node);
				}
				public String infixName(Object node) {
					return this.name(node);
				}
				public int precedence() {
					return Integer.MAX_VALUE;
				}
			};
			out[NUMBER] = new Info() {
				public String typeName() {
					return "number";
				}
				String name(Object node) {
					try {
						ParseNode x = (ParseNode) node;
						return x.value;
					} catch (Exception ex) {
					}
					return null;
				}
				public String prefixName(Object node) {
					return this.name(node);
				}
				public String infixName(Object node) {
					return this.name(node);
				}
				public int precedence() {
					return Integer.MAX_VALUE;
				}
			};
			return out;
		}

		private int type;
		private String value;
		private long begin;
		private int size;

		ParseNode(int type) {
			this.type = type;
		}
		public int type() {
			return this.type;
		}
		public String value() {
			return this.value;
		}
		public long begin() {
			return this.begin;
		}
		public int size() {
			return this.size;
		}
		private ParseNode type(int type) {
			this.type = type;
			this.value = null;
			this.begin = 0;
			this.size = 0;
			this.clearChildren();
			this.clearSibling();
			return this;
		}
		public boolean isType(int type) {
			return this.type == type;
		}
		public ParseNode add(ParseNode child) {
			if (child == null) {
				return this;
			}
			return this.addLastNode(child).add(child.size);
		}
		public ParseNode add(int size) {
			this.size += size;
			return this;
		}
		public String toString() {
			return this.typeName() + "[" + this.begin + ", "
					+ (this.begin + this.size) + "]";
		}

		public String prefixName() {
			return this.nodeInfo().prefixName(this.that());
		}
		public String infixName() {
			return this.nodeInfo().infixName(this.that());
		}
		public String typeName() {
			return this.nodeInfo().typeName();
		}
		protected Info nodeInfo() {
			Info info = ParseNode.INFOS[this.type];
			if (info == null) {
				throw new Error("null info for type=" + this.type());
			}
			return info;
		}
		public String toInfix() {
			StringBuilder buffer = new StringBuilder();
			try {
				return this.writeInfix(buffer).toString();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			return buffer.toString();
		}
		public Appendable writeInfix(Appendable output) throws IOException {
			return this.writeInfix(output, null);
		}
		protected Appendable writeInfix(Appendable output, ParseNode parent)
				throws IOException {
			if (this.isLeaf()) {
				return this.writeInfixLeaf(output, parent);
			}
			boolean braket = false;
			if (parent != null) {
				if (this.nodeInfo().precedence() < parent.nodeInfo().precedence()) {
					braket = true;
				}
			}
			if (braket) {
				output.append('(');
			}
			String op = this.infixName();
			ParseNode child = this.firstNode();
			child.writeInfix(output, this.that());
			for (child = child.nextNode(); child != null; child = child.nextNode()) {
				if (op.length() < 1) {
					output.append(' ');
				} else {
					output.append(' ');
					output.append(op);
					output.append(' ');
				}
				child.writeInfix(output, this.that());
			}
			if (braket) {
				output.append(')');
			}
			return output;
		}
		protected Appendable writeInfixLeaf(Appendable output, ParseNode parent)
				throws IOException {
			return output.append(this.infixName());
		}
	}

	static class AsciiParser {
		private static final int COMMENT_SYMBOL = '#';
		private static final int END_OF_LINE = '\n';
		private static final int END_OF_EQUATION = ';';
		private static final int ASSIGNS = '=';
		private static final int MINUS = '-';
		private static final int GT = '>';
		private static final int EQUATION_BRA = '(';
		private static final int EQUATION_KET = ')';
		private static final int POWERS = '^';
		private static final int PLUS = '+';
		private static final int CHOICE = '/';
		private static final int OR = '|';
		private static final int ZERO = '0';
		private static final int ONE = '1';

		private static boolean isWhite(int ch) {
			switch (ch) {
			case ' ':
			case '\t':
			case '\n':
			case '\r':
				return true;
			default:
				return false;
			}
		}
		private static boolean isVariableNext(int ch) {
			return AsciiParser.isVariableFirst(ch) || AsciiParser.isDigit(ch);
		}
		private static boolean isVariableFirst(int ch) {
			switch (ch) {
			case '_':
				return true;
			default:
				return AsciiParser.isAlphabet(ch);
			}
		}
		private static boolean isAlphabet(int ch) {
			return ('a' <= ch && ch <= 'z') || ('A' <= ch && ch <= 'Z');
		}
		private static boolean isDigit(int ch) {
			return '0' <= ch && ch <= '9';
		}
		private static boolean isPlus(int ch) {
			switch (ch) {
			case AsciiParser.PLUS:
			case AsciiParser.CHOICE:
			case AsciiParser.OR:
				return true;
			default:
				return false;
			}
		}

		static long[] newLines(ReadableByteChannel input) throws IOException {
			LongArrayList lines = new LongArrayList();
			ByteBuffer buffer = ByteBuffer.wrap(new byte[1024]);
			long pos = 0;
			while (0 <= input.read(buffer)) {
				for (int i = 0, n = buffer.limit(); i < n; ++i, ++pos) {
					if (buffer.array()[i] == '\n') {
						lines.addLast(pos);
					}
				}
				buffer.rewind();
			}
			return lines.toArray();
		}

		long[] lines;
		private List<ParseNode> backedNodes;
		private boolean hasBOM;
		private int nodeCount;
		private int backCount;
		private int reusedCount;

		public long[] getLines() {
			return this.lines;
		}
		public AsciiParser setLines(long[] lines) {
			this.lines = lines;
			return this;
		}
		public boolean hasBOM() {
			return this.hasBOM;
		}
		public int nodeCount() {
			return this.nodeCount;
		}
		public ParseNode parse(ByteInput input) throws IOException {
			ParseNode ig = this.parseBOM(input);
			this.hasBOM = 0 < this.backNode(ig);
			this.skipIgnorableBlocks(input);
			ParseNode node = this.parseEquationList(input);
			this.skipIgnorableBlocks(input);
			return node;
		}
		private int skipIgnorableBlocks(ByteInput input) throws IOException {
			int out = 0;
			ParseNode node = this.parseIgnorableBlock(input.pushMark()); // 1
			while (node != null) {
				out += this.backNode(node);
				node = this.parseIgnorableBlock(input.setMark()); // 1
			}
			input.goMark().popMark(); // 1
			return out;
		}
		private ParseNode parseEquationList(ByteInput input) throws IOException {
			ParseNode node = this.newNode(ParseNode.EQUATION_LIST, input.pushMark()); // 1
			int ign = 0;
			while (true) {
				ParseNode eq = this.parseEquation(input);
				ign += this.skipIgnorableBlocks(input);
				int ch = input.get();
				if (ch == AsciiParser.END_OF_EQUATION) {
					input.next().setMark(); // 1
					node.add(eq).add(ign + 1);
					ign = this.skipIgnorableBlocks(input);
				} else {
					if (eq != null) {
						Debug.log().debug(
								this.caution(input, "maybe forget ';' at the end of eq."));
					}
					this.backNode(eq);
					input.goMark().popMark(); // 0
					return node;
				}
			}
		}
		private String caution(ByteInput input, String msg) {
			return this.caution(input.position(), msg);
		}
		private String caution(long position, String msg) {
			if (this.lines == null) {
				return "[" + position + "] " + msg;
			}
			long row = 1;
			long col = 1;
			int index = ArrayHelper.lowerBound(this.lines, position);
			if (index == 0) {
				col += position;
			} else {
				row += index;
				col = position - this.lines[index - 1];
			}
			return "[" + row + ":" + col + "] " + msg;
		}
		private ParseNode parseEquation(ByteInput input) throws IOException {
			return this.parseAssigns(input);
		}
		private ParseNode parseAssigns(ByteInput input) throws IOException {
			ParseNode node = this.newNode(ParseNode.ASSIGNS, input.pushMark()); // 1
			ParseNode lhs = this.parseVariable(input, ParseNode.VARIABLE);
			if (lhs != null) {
				node.add(lhs);
				int ign = this.skipIgnorableBlocks(input);
				node.add(ign);
				ParseNode op = this.parseAssignsOperator(input);
				if (op != null) {
					this.backNode(op);
					ign = this.skipIgnorableBlocks(input);
					node.add(ign);
					ParseNode rhs = this.parseAssignsRhs(input);
					if (rhs != null) {
						input.popMark(); // 0
						return node.add(rhs);
					}
				}
			}
			input.goMark().popMark(); // 0
			this.backNode(node);
			return null;
		}
		private ParseNode parseAssignsOperator(ByteInput input) throws IOException {
			int ch = input.get();
			if (ch == AsciiParser.ASSIGNS) {
				ParseNode node = this.newNode(ParseNode.ASSIGNS, input);
				input.next();
				return node.add(1);
			} else if (ch == AsciiParser.MINUS) {
				ParseNode node = this.newNode(ParseNode.ASSIGNS, input);
				ch = input.pushMark().next().get(); // 1
				if (ch == AsciiParser.GT) {
					input.popMark().next(); // 0
					return node.add(2);
				}
				input.goMark().popMark();
			}
			return null;
		}
		private ParseNode parseAssignsRhs(ByteInput input) throws IOException {
			return this.parsePlus(input);
		}
		private ParseNode parsePlus(ByteInput input) throws IOException {
			ParseNode node = this.parseMultiplies(input);
			if (node == null) {
				return null;
			}
			int ign = this.skipIgnorableBlocks(input.pushMark()); // 1
			int ch = input.get();
			while (AsciiParser.isPlus(ch)) {
				ign += this.skipIgnorableBlocks(input.next());
				ParseNode next = this.parseMultiplies(input);
				if (next == null) {
					input.goMark().popMark(); // 0
					return node;
				}
				ParseNode newNode = this.newNode(ParseNode.PLUS, input);
				newNode.add(node).add(ign).add(next).begin = node.begin;
				node = newNode;
				ign = this.skipIgnorableBlocks(input.setMark());
				ch = input.get();
			}
			input.goMark().popMark(); // 0
			return node;
		}
		private ParseNode parseMultiplies(ByteInput input) throws IOException {
			ParseNode node = this.parsePowers(input);
			if (node == null) {
				return null;
			}
			int ign = this.skipIgnorableBlocks(input.pushMark()); // 1
			while (true) {
				ParseNode next = this.parsePowers(input);
				if (next == null) {
					break;
				}
				ParseNode newNode = this.newNode(ParseNode.MULTIPLIES, input);
				newNode.add(node).add(ign).add(next).begin = node.begin;
				node = newNode;
				ign = this.skipIgnorableBlocks(input.setMark());
			}
			input.goMark().popMark(); // 0
			return node;
		}
		private ParseNode parsePowers(ByteInput input) throws IOException {
			ParseNode node = this.parsePrimary(input);
			if (node == null) {
				return null;
			}
			int ign = this.skipIgnorableBlocks(input.pushMark()); // 1
			int ch = input.get();
			while (ch == AsciiParser.POWERS) {
				ign += this.skipIgnorableBlocks(input.next());
				ParseNode next = this.parseNumberPrimary(input);
				if (next == null) {
					input.goMark().popMark(); // 0
					return node;
				}
				ParseNode newNode = this.newNode(ParseNode.POWERS, input);
				newNode.add(node).add(ign).add(next).begin = node.begin;
				node = newNode;
				ign = this.skipIgnorableBlocks(input.setMark());
				ch = input.get();
			}
			input.goMark().popMark(); // 0
			return node;
		}
		private ParseNode parseNumberPrimary(ByteInput input) throws IOException {
			ParseNode node = this.parseEquationBraket(input);
			if (node != null) {
				return node;
			}
			node = this.parseVariable(input, ParseNode.NUMBER_VARIABLE);
			if (node != null) {
				return node;
			}
			node = this.parseNumber(input);
			if (node != null) {
				return node;
			}
			return node;
		}
		private ParseNode parsePrimary(ByteInput input) throws IOException {
			ParseNode node = this.parseEquationBraket(input);
			if (node != null) {
				return node;
			}
			node = this.parseVariable(input, ParseNode.VARIABLE);
			if (node != null) {
				return node;
			}
			node = this.parseOne(input);
			if (node != null) {
				return node;
			}
			node = this.parseZero(input);
			if (node != null) {
				return node;
			}
			node = this.parseNumber(input);
			if (node != null) {
				return node;
			}
			return node;
		}
		private ParseNode parseEquationBraket(ByteInput input) throws IOException {
			int ch = input.get();
			if (ch == AsciiParser.EQUATION_BRA) {
				this.skipIgnorableBlocks(input.pushMark().next()); // 1
				ParseNode node = this.parseAssignsRhs(input);
				if (node != null) {
					this.skipIgnorableBlocks(input);
					ch = input.get();
					if (ch == AsciiParser.EQUATION_KET) {
						input.popMark().next(); // 0
						return node;
					}
				}
				input.goMark().popMark(); // 0
			}
			return null;
		}
		private ParseNode parseOne(ByteInput input) throws IOException {
			int ch = input.get();
			if (ch == AsciiParser.ONE) {
				ParseNode node = this.newNode(ParseNode.ONE, input);
				input.next();
				return node.add(1);
			}
			return null;
		}
		private ParseNode parseZero(ByteInput input) throws IOException {
			int ch = input.get();
			if (ch == AsciiParser.ZERO) {
				ParseNode node = this.newNode(ParseNode.ZERO, input);
				input.next();
				return node.add(1);
			}
			return null;
		}
		private ParseNode parseVariable(ByteInput input, int nodeType)
				throws IOException {
			int ch = input.get();
			if (AsciiParser.isVariableFirst(ch)) {
				ParseNode node = this.newNode(nodeType, input);
				StringBuilder buffer = new StringBuilder();
				buffer.append((char) ch);
				ch = input.next().get();
				while (AsciiParser.isVariableNext(ch)) {
					buffer.append((char) ch);
					ch = input.next().get();
				}
				node.add(buffer.length());
				node.value = buffer.toString();
				return node;
			}
			return null;
		}
		private ParseNode parseNumber(ByteInput input) throws IOException {
			int ch = input.get();
			if (AsciiParser.isDigit(ch)) {
				ParseNode node = this.newNode(ParseNode.NUMBER, input);
				StringBuilder buffer = new StringBuilder();
				buffer.append((char) ch);
				ch = input.next().get();
				while (AsciiParser.isDigit(ch)) {
					buffer.append((char) ch);
					ch = input.next().get();
				}
				node.add(buffer.length());
				node.value = buffer.toString();
				return node;
			}
			return null;
		}
		private ParseNode parseBOM(ByteInput input) throws IOException {
			int ch = input.get();
			if (ch == FileHelper.BOM_0) {
				ParseNode node = this.newNode(ParseNode.IGNORABLE_BLOCK, input);
				ch = input.pushMark().next().get(); // 1
				if (ch == FileHelper.BOM_1) {
					ch = input.next().get();
					if (ch == FileHelper.BOM_2) {
						input.popMark().next(); // 0
						return node.add(3);
					}
				}
				input.goMark().popMark(); // 0
				this.backNode(node);
			}
			return null;
		}
		private ParseNode parseIgnorableBlock(ByteInput input) throws IOException {
			int ch = input.get();
			if (AsciiParser.isWhite(ch)) {
				ParseNode node = this.newNode(ParseNode.IGNORABLE_BLOCK, input);
				int n = 1;
				ch = input.next().get();
				while (ch != ByteInput.END_OF_INPUT && AsciiParser.isWhite(ch)) {
					++n;
					ch = input.next().get();
				}
				return node.add(n);
			} else if (ch == AsciiParser.COMMENT_SYMBOL) {
				ParseNode node = this.newNode(ParseNode.IGNORABLE_BLOCK, input);
				ch = input.pushMark().next().get(); // 1
				if (ch == AsciiParser.COMMENT_SYMBOL) {
					int n = 2;
					while (ch != ByteInput.END_OF_INPUT && ch != AsciiParser.END_OF_LINE) {
						++n;
						ch = input.next().get();
					}
					input.next().popMark(); // 0
					return node.add(n + 1);
				}
				this.backNode(node);
				input.goMark().popMark(); // 0
			}
			return null;
		}
		private ParseNode newNode(int type, ByteInput input) {
			ParseNode node = this.newNode(type);
			node.begin = input.position();
			return node;
		}
		private ParseNode newNode(int type) {
			List<ParseNode> nodes = this.backedNodes(false);
			if (nodes == null || nodes.size() < 1) {
				++this.nodeCount;
				return new ParseNode(type);
			}
			ParseNode node = nodes.remove(nodes.size() - 1);
			ParseNode child = node.firstNode();
			for (; child != null; child = child.nextNode()) {
				nodes.add(child);
			}
			++this.reusedCount;
			return node.type(type);
		}
		private int backNode(ParseNode x) {
			if (x != null) {
				this.backedNodes(true).add(x);
				++this.backCount;
				return x.size;
			}
			return 0;
		}
		private List<ParseNode> backedNodes(boolean anyway) {
			if (this.backedNodes == null && anyway) {
				this.backedNodes = new ArrayList<ParseNode>();
			}
			return this.backedNodes;
		}
	}

	public void testParser() throws IOException {
		File file = new File("data/ParserTest_1.tiny");
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
			long[] lines = AsciiParser.newLines(in.getChannel());
			ByteInput input = new ByteFileInput(in.getChannel().position(0), 512);
			AsciiParser parser = new AsciiParser().setLines(lines);
			ParseNode node = parser.parse(input);
			Debug.log().debug(node != null ? node.toInfix() : null);
			Debug.log().debug(
					"returned=" + ParseNode.countNode(node) + ", remains="
							+ countNode(parser.backedNodes(true)) + " in newed="
							+ parser.nodeCount);
			Debug.log().debug(
					"#backed=" + parser.backCount + ", #reused=" + parser.reusedCount);
		} finally {
			FileHelper.close(in);
		}
	}
	private static int countNode(List<ParseNode> nodes) {
		int count = 0;
		for (int i = 0, n = nodes.size(); i < n; ++i) {
			count += ParseNode.countNode(nodes.get(i));
		}
		return count;
	}
}
