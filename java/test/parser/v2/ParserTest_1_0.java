package parser.v2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
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
		public static final int MULTIPLIES = ONE + 1;
		public static final int PLUS = MULTIPLIES + 1;
		public static final int ASSIGNS = PLUS + 1;
		public static final int VARIABLE = ASSIGNS + 1;
		protected static final int NUMBER_OF_TYPES = VARIABLE + 1;

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
		protected ParseNode type(int type) {
			this.type = type;
			return this;
		}
		public boolean isType(int type) {
			return this.type == type;
		}
		public String value() {
			return this.value;
		}
		public ParseNode value(String value) {
			this.value = value;
			return this;
		}
		public long begin() {
			return this.begin;
		}
		public ParseNode begin(long begin) {
			this.begin = begin;
			return this;
		}
		public int size() {
			return this.size;
		}
		public ParseNode size(int size) {
			this.size = size;
			return this;
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

		public String toPrefix() {
			StringBuilder buffer = new StringBuilder();
			try {
				return this.writePrefix(buffer).toString();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			return buffer.toString();
		}
		public Appendable writePrefix(Appendable output) throws IOException {
			output.append(this.prefixName());
			ParseNode child = this.firstNode();
			if (child == null) {
				return output;
			}
			child.writePrefix(output.append('('));
			for (child = child.nextNode(); child != null; child = child.nextNode()) {
				child.writePrefix(output.append(", "));
			}
			return output.append(')');
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

	static class ParseNodeRecycler extends Recycler<ParseNodeRecycler, ParseNode> {
		private int nodeCount;
		private int backCount;
		private int reusedCount;

		protected ParseNode newNode(int type, long position) {
			List<ParseNode> nodes = this.backedValues(false);
			ParseNode newNode = null;
			if (nodes == null || nodes.size() < 1) {
				++this.nodeCount;
				newNode = new ParseNode(type);
			} else {
				newNode = nodes.remove(nodes.size() - 1);
				ParseNode child = newNode.firstNode();
				for (; child != null; child = child.nextNode()) {
					nodes.add(child);
				}
				newNode.clearChildren().clearSibling().type(type).size(0).value(null);
				++this.reusedCount;
			}
			return newNode.begin(position);
		}
		protected ParseNode cloneNode(ParseNode node) {
			List<ParseNode> nodes = this.backedValues(false);
			ParseNode newNode = null;
			if (nodes == null || nodes.size() < 1) {
				++this.nodeCount;
				newNode = node.clone();
			} else {
				newNode = nodes.remove(nodes.size() - 1);
				ParseNode child = newNode.firstNode();
				for (; child != null; child = child.nextNode()) {
					nodes.add(child);
				}
				newNode.clearChildren().clearSibling().type(node.type())
						.begin(node.begin()).size(node.size()).value(node.value());
				++this.reusedCount;
			}
			return newNode;
		}
		protected int backNode(ParseNode x) {
			if (x != null) {
				this.backedValues(true).add(x);
				++this.backCount;
				return x.size;
			}
			return 0;
		}
		protected ParseNodeRecycler clearNodes() {
			return this.clearBackValues();
		}
		public int nodeCount() {
			return this.nodeCount;
		}
		public int backCount() {
			return this.backCount;
		}
		public int reusedCount() {
			return this.reusedCount;
		}
	}

	static class AsciiParser extends ParseNodeRecycler {
		static interface Data {
			int get() throws IOException;
			Data next() throws IOException;
			public long position();
			public Data position(long position);
		}

		static class AsciiData implements Data {
			final ByteInput input;
			private long position;

			AsciiData(ByteInput input) {
				this.input = input;
			}
			public int get() throws IOException {
				return this.input.get(this.position);
			}
			public AsciiData next() {
				++this.position;
				return this;
			}
			public long position() {
				return this.position;
			}
			public AsciiData position(long position) {
				this.position = position;
				return this;
			}
		}

		private static final int COMMENT_SYMBOL = '#';
		private static final int END_OF_LINE = '\n';
		private static final int END_OF_EQUATION = ';';
		private static final int ASSIGNS = '=';
		private static final int MINUS = '-';
		private static final int GT = '>';
		private static final int EQUATION_BRA = '(';
		private static final int EQUATION_KET = ')';
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
		private boolean hasBOM;
		private StringBuilder buffer;

		protected StringBuilder buffer() {
			return this.buffer(true);
		}
		protected StringBuilder buffer(boolean anyway) {
			if (this.buffer == null && anyway) {
				this.buffer = new StringBuilder();
			}
			this.buffer.setLength(0);
			return this.buffer;
		}
		protected void addError(Data data, String msg) {
			Debug.log().debug(this.caution(data, msg));
		}

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
		public ParseNode parse(ByteInput input) throws IOException {
			Data data = new AsciiData(input);
			ParseNode node = this.parse(data);
			if (node != null && data.get() == ByteInput.END_OF_INPUT) {
				return node;
			}
			this.backNode(node);
			return null;
		}
		private ParseNode parse(Data data) throws IOException {
			long position = data.position();
			if (0 < this.skipBOM(data)) {
				this.hasBOM = true;
			}
			this.skipIgnorableBlocks(data);
			ParseNode node = this.parseEquationList(data);
			if (node == null) {
				data.position(position);
				return null;
			}
			this.skipIgnorableBlocks(data);
			return node;
		}

		private int skipBOM(Data data) throws IOException {
			if (data.get() == FileHelper.BOM_0) {
				long position = data.position();
				if (data.next().get() == FileHelper.BOM_1) {
					if (data.next().get() == FileHelper.BOM_2) {
						return 3;
					}
				}
				data.position(position);
			}
			return 0;
		}
		private int skipIgnorableBlocks(Data data) throws IOException {
			int out = 0;
			int delta = this.skipIgnorableBlock(data);
			while (0 < delta) {
				out += delta;
				delta = this.skipIgnorableBlock(data);
			}
			return out;
		}
		private int skipIgnorableBlock(Data data) throws IOException {
			long position = data.position();
			int ch = data.get();
			if (AsciiParser.isWhite(ch)) {
				ch = data.next().get();
				while (ch != ByteInput.END_OF_INPUT && AsciiParser.isWhite(ch)) {
					ch = data.next().get();
				}
				return (int) (data.position() - position);
			} else if (ch == AsciiParser.COMMENT_SYMBOL) {
				ch = data.next().get();
				if (ch != AsciiParser.COMMENT_SYMBOL) {
					data.position(position);
					return 0;
				}
				while (ch != ByteInput.END_OF_INPUT && ch != AsciiParser.END_OF_LINE) {
					ch = data.next().get();
				}
				if (ch == AsciiParser.END_OF_LINE) {
					data.next();
				}
				return (int) (data.position() - position);
			}
			return 0;
		}

		private ParseNode parseEquationList(Data data) throws IOException {
			long position = data.position();
			ParseNode node = this.newNode(ParseNode.EQUATION_LIST, data);
			int ign = 0;
			while (true) {
				ParseNode eq = this.parseEquation(data);
				ign += this.skipIgnorableBlocks(data);
				if (data.get() != AsciiParser.END_OF_EQUATION) {
					if (eq != null) {
						this.addError(data, "maybe forget ';' at the end of eq.");
					}
					this.backNode(eq);
					data.position(position);
					return node;
				}
				position = data.next().position();
				node.add(eq).add(ign + 1);
				ign = this.skipIgnorableBlocks(data);
			}
		}
		private ParseNode parseEquation(Data data) throws IOException {
			long position = data.position();
			ParseNode lhs = this.parseVariable(data, ParseNode.VARIABLE);
			if (lhs == null) {
				this.addError(data, "could not find lhs variable of assignment.");
				return null;
			}
			int ig_1 = this.skipIgnorableBlocks(data);
			int op = this.skipAssignsOperator(data);
			if (op < 1) {
				this.addError(data, "could not find lhs variable of assignment.");
				data.position(position);
				this.backNode(lhs);
				return null;
			}
			int ig_2 = this.skipIgnorableBlocks(data);
			ParseNode rhs = this.parseAssignsRhs(data);
			if (rhs == null) {
				this.addError(data, "could not find rhs equation of assignment.");
				data.position(position);
				this.backNode(lhs);
				return null;
			}
			return this.newNode(ParseNode.ASSIGNS, position).add(lhs).add(ig_1)
					.add(op).add(ig_2).add(rhs);
		}
		private int skipAssignsOperator(Data data) throws IOException {
			long position = data.position();
			int ch = data.get();
			if (ch == AsciiParser.ASSIGNS) {
				data.next();
				return 1;
			} else if (ch == AsciiParser.MINUS) {
				ch = data.next().get();
				if (ch == AsciiParser.GT) {
					data.next();
					return 2;
				}
			}
			data.position(position);
			return 0;
		}
		private ParseNode parseAssignsRhs(Data data) throws IOException {
			return this.parsePlus(data);
		}
		private ParseNode parsePlus(Data data) throws IOException {
			ParseNode out = this.parseMultiplies(data);
			if (out == null) {
				return null;
			}
			long position = data.position();
			int ig_1 = this.skipIgnorableBlocks(data);
			int ch = data.get();
			while (AsciiParser.isPlus(ch)) {
				int ig_2 = this.skipIgnorableBlocks(data.next());
				ParseNode right = this.parseMultiplies(data);
				if (right == null) {
					break;
				}
				out = this.newNode(ParseNode.PLUS, position).add(out).add(ig_1).add(1)
						.add(ig_2).add(right);
				position = data.position();
				ig_1 = this.skipIgnorableBlocks(data);
				ch = data.get();
			}
			data.position(position);
			return out;
		}
		private ParseNode parseMultiplies(Data data) throws IOException {
			ParseNode out = this.parsePrimary(data);
			if (out == null) {
				return null;
			}
			long position = data.position();
			int ig = this.skipIgnorableBlocks(data);
			ParseNode right = this.parsePrimary(data);
			while (right != null) {
				out = this.newNode(ParseNode.MULTIPLIES, position).add(out).add(ig)
						.add(right);
				position = data.position();
				ig = this.skipIgnorableBlocks(data);
				right = this.parsePrimary(data);
			}
			data.position(position);
			return out;
		}
		private ParseNode parsePrimary(Data data) throws IOException {
			ParseNode out = this.parseEquationBraket(data);
			if (out != null) {
				return out;
			}
			out = this.parseVariable(data, ParseNode.VARIABLE);
			if (out != null) {
				return out;
			}
			out = this.parseOne(data);
			if (out != null) {
				return out;
			}
			out = this.parseZero(data);
			if (out != null) {
				return out;
			}
			return null;
		}
		private ParseNode parseEquationBraket(Data data) throws IOException {
			long position = data.position();
			if (data.get() != AsciiParser.EQUATION_BRA) {
				return null;
			}
			this.skipIgnorableBlocks(data.next());
			ParseNode out = this.parseAssignsRhs(data);
			if (out == null) {
				this.addError(data, "could not find expression in braket.");
				data.position(position);
				return null;
			}
			this.skipIgnorableBlocks(data);
			if (data.get() != AsciiParser.EQUATION_KET) {
				this.addError(data, "could not find ket.");
				data.position(position);
				return null;
			}
			data.next();
			return out;
		}
		private ParseNode parseOne(Data data) throws IOException {
			if (data.get() != AsciiParser.ONE) {
				return null;
			}
			ParseNode out = this.newNode(ParseNode.ONE, data);
			data.next();
			return out;
		}
		private ParseNode parseZero(Data data) throws IOException {
			if (data.get() != AsciiParser.ZERO) {
				return null;
			}
			ParseNode out = this.newNode(ParseNode.ZERO, data);
			data.next();
			return out;
		}
		private ParseNode parseVariable(Data data, int nodeType) throws IOException {
			int ch = data.get();
			if (!AsciiParser.isVariableFirst(ch)) {
				return null;
			}
			ParseNode node = this.newNode(nodeType, data);
			StringBuilder buffer = this.buffer();
			buffer.append((char) ch);
			ch = data.next().get();
			while (AsciiParser.isVariableNext(ch)) {
				buffer.append((char) ch);
				ch = data.next().get();
			}
			return node.add(buffer.length()).value(buffer.toString());
		}

		private String caution(Data data, String msg) {
			return this.caution(data.position(), msg);
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

		private ParseNode newNode(int type, Data data) {
			return this.newNode(type, data.position());
		}
	}
	private static int countNode(List<ParseNode> nodes) {
		int count = 0;
		for (int i = 0, n = nodes.size(); i < n; ++i) {
			count += ParseNode.countNode(nodes.get(i));
		}
		return count;
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
							+ countNode(parser.backedValues(true)) + " in newed="
							+ parser.nodeCount());
			Debug.log()
					.debug(
							"#backed=" + parser.backCount() + ", #reused="
									+ parser.reusedCount());
		} finally {
			FileHelper.close(in);
		}
	}
}
