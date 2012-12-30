package parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;

import parser.ParserOldTest.GrammarCommand.ExpressionData;

import junit.framework.TestCase;
import tiny.lang.ArrayHelper;
import tiny.lang.Debug;
import tiny.lang.Messages;
import tiny.lang.StringHelper;

public class ParserOldTest extends TestCase {
	static class GrammarNode {
		// generators
		static final int VARIABLE = 1;
		static final int STRING = VARIABLE + 1;
		static final int CHARACTER = STRING + 1;
		static final int EMPTY_STRING = CHARACTER + 1;
		static final int EMPTY_CHARACTER = EMPTY_STRING + 1;
		// unaries
		static final int CHARACTER_COMPLEMENTS = EMPTY_CHARACTER + 1;
		static final int STARS = CHARACTER_COMPLEMENTS + 1;
		// binaries
		static final int CHARACTER_RANGE = STARS + 1;
		static final int CHARACTER_MEETS = CHARACTER_RANGE + 1;
		static final int MULTIPLIES = CHARACTER_MEETS + 1;
		static final int PLUS = MULTIPLIES + 1;
		static final int CHARACTER_MINUS = PLUS + 1;
		static final int ASSIGNS = CHARACTER_MINUS + 1;
		// end
		static final int END_OF_TYPE = ASSIGNS + 1;

		// special nodes they do not have data.
		static final GrammarNode EMPTY_STRING_NODE = new GeneratorNode(EMPTY_STRING);
		static final GrammarNode EMPTY_CHARACTER_NODE = new GeneratorNode(
				EMPTY_CHARACTER);

		public static String getTypeName(int type) {
			switch (type) {
			case VARIABLE:
				return "variable";
			case STRING:
				return "string";
			case CHARACTER:
				return "character";
			case EMPTY_STRING:
				return "one";
			case EMPTY_CHARACTER:
				return "zero";
			case CHARACTER_COMPLEMENTS:
				return "complements";
			case STARS:
				return "stars";
			case CHARACTER_RANGE:
				return "range";
			case CHARACTER_MEETS:
				return "meets";
			case MULTIPLIES:
				return "multiplies";
			case PLUS:
				return "plus";
			case CHARACTER_MINUS:
				return "minus";
			case ASSIGNS:
				return "assigns";
			default:
				return "unknown";
			}
		}
		public static GrammarNode newNode(int type) {
			switch (type) {
			case VARIABLE:
				return new VariableNode();
			case STRING:
				return new StringNode();
			case CHARACTER:
				return new CharacterNode();
			case EMPTY_STRING:
				return EMPTY_STRING_NODE;
			case EMPTY_CHARACTER:
				return EMPTY_CHARACTER_NODE;
			case CHARACTER_COMPLEMENTS:
			case STARS:
				return new UnaryNode(type);
			case CHARACTER_RANGE:
			case CHARACTER_MEETS:
			case MULTIPLIES:
			case PLUS:
			case CHARACTER_MINUS:
			case ASSIGNS:
				return new BinaryNode(type);
			default: {
				String msg = "unknown type=" + type;
				throw new NoSuchElementException(msg);
			}
			}
		}

		final int type;

		public GrammarNode(int type) {
			this.type = type;
		}
		public int getType() {
			return this.type;
		}
		public int getChildSize() {
			return 0;
		}
		public GrammarNode getChild(int index) {
			throw new NoSuchElementException("leaf dose not have children");
		}
		public String toString() {
			return this.toPreorder();
		}
		protected void toStringGenerator(StringBuilder output) {
			switch (this.type) {
			case VARIABLE: {
				VariableNode that = (VariableNode) this;
				output.append(that.value);
				return;
			}
			case STRING: {
				StringNode that = (StringNode) this;
				output.append('"');
				if (that.utf8) {
					output.append(new String(that.value, StringHelper.CHARSET_UTF_8));
				} else {
					output.append(StringHelper.toHexString(that.value));
				}
				output.append(that.value);
				output.append('"');
				return;
			}
			case CHARACTER: {
				CharacterNode that = (CharacterNode) this;
				output.append("'");
				if (that.ascii) {
					output.append(Character.toChars(that.value));
				} else {
					output.append(StringHelper.toHexString(that.value));
				}
				output.append("'");
				return;
			}
			default:
				output.append(this.getTypeName());
				return;
			}
		}
		public String toPreorder() {
			StringBuilder buffer = new StringBuilder();
			this.toPreorder(buffer);
			return buffer.toString();
		}
		public void toPreorder(StringBuilder output) {
			int n = this.getChildSize();
			if (n < 1) {
				toStringGenerator(output);
				return;
			}
			output.append('(');
			output.append(this.getTypeName());
			for (int i = 0; i < n; ++i) {
				output.append(' ');
				GrammarNode child = this.getChild(i);
				if (child != null) {
					child.toPreorder(output);
				} else {
					output.append("null");
				}
			}
			output.append(')');
		}
		public String toInorder() {
			StringBuilder buffer = new StringBuilder();
			this.toInorder(buffer);
			return buffer.toString();
		}
		protected static void toChildInorder(StringBuilder output, GrammarNode node) {
			if (node != null) {
				if (node.getChildSize() < 1) {
					node.toInorder(output);
				} else {
					output.append('(');
					node.toInorder(output);
					output.append(')');
				}
			} else {
				output.append("null");
			}
		}
		public void toInorder(StringBuilder output) {
			int n = this.getChildSize();
			if (n < 1) {
				toStringGenerator(output);
				return;
			}
			switch (this.type) {
			case CHARACTER_COMPLEMENTS: {
				toChildInorder(output, this.getChild(0));
				output.append('!');
			}
			break;
			case STARS: {
				toChildInorder(output, this.getChild(0));
				output.append('*');
			}
			break;
			case CHARACTER_RANGE: {
				toChildInorder(output, this.getChild(0));
				output.append(" .. ");
				toChildInorder(output, this.getChild(1));
			}
			break;
			case CHARACTER_MEETS: {
				toChildInorder(output, this.getChild(0));
				output.append(" & ");
				toChildInorder(output, this.getChild(1));
			}
			break;
			case MULTIPLIES: {
				toChildInorder(output, this.getChild(0));
				output.append(' ');
				toChildInorder(output, this.getChild(1));
			}
			break;
			case PLUS: {
				toChildInorder(output, this.getChild(0));
				output.append(" + ");
				toChildInorder(output, this.getChild(1));
			}
			break;
			case CHARACTER_MINUS: {
				toChildInorder(output, this.getChild(0));
				output.append(" - ");
				toChildInorder(output, this.getChild(1));
			}
			break;
			case ASSIGNS: {
				toChildInorder(output, this.getChild(0));
				output.append(" = ");
				toChildInorder(output, this.getChild(1));
			}
			break;
			default: {
				String msg = "unknown type=" + type;
				output.append(msg);
			}
			break;
			}
		}
		public String getTypeName() {
			return getTypeName(this.type);
		}
	}

	static class GeneratorNode extends GrammarNode {
		public GeneratorNode(int value) {
			super(value);
		}
	}

	static class UnaryNode extends GrammarNode {
		GrammarNode child;

		public UnaryNode(int value) {
			this(value, null);
		}
		public UnaryNode(int value, GrammarNode child) {
			super(value);
			this.setChild(child);
		}
		@Override
		public int getChildSize() {
			return 1;
		}
		@Override
		public GrammarNode getChild(int index) {
			if (index == 0) {
				return this.child;
			}
			throw new NoSuchElementException("unary dose not have child=" + index);
		}
		public UnaryNode setChild(GrammarNode child) {
			this.child = child;
			return this;
		}
	}

	static class BinaryNode extends GrammarNode {
		GrammarNode child0;
		GrammarNode child1;

		public BinaryNode(int value) {
			this(value, null, null);
		}
		public BinaryNode(int value, GrammarNode child0, GrammarNode child1) {
			super(value);
			this.setChildren(child0, child1);
		}
		@Override
		public int getChildSize() {
			return 2;
		}
		@Override
		public GrammarNode getChild(int index) {
			switch (index) {
			case 0:
				return this.child0;
			case 1:
				return this.child1;
			default:
			break;
			}
			throw new NoSuchElementException("binary dose not have child=" + index);
		}
		public BinaryNode setChildren(GrammarNode child0, GrammarNode child1) {
			this.child0 = child0;
			this.child1 = child1;
			return this;
		}
	}

	static class VariableNode extends GeneratorNode {
		String value;

		public VariableNode() {
			this(null);
		}
		public VariableNode(String value) {
			super(VARIABLE);
			this.value = value;
		}
	}

	static class StringNode extends GeneratorNode {
		byte[] value;
		boolean utf8;

		public StringNode() {
			this(ArrayHelper.EMPTY_BYTE_ARRAY, false);
		}
		public StringNode(byte[] value, boolean utf8) {
			super(STRING);
			this.value = value;
			this.utf8 = utf8;
		}
	}

	static class CharacterNode extends GeneratorNode {
		byte value;
		private boolean ascii;

		public CharacterNode() {
			this((byte) 0, false);
		}
		public CharacterNode(byte value, boolean ascii) {
			super(CHARACTER);
			this.value = value;
			this.ascii = ascii;
		}
	}

	static class GrammarCommand {
		static class ExpressionData {
			GrammarNode root;

			public ExpressionData(GrammarNode node) {
				this.root = node;
			}
			public String toString() {
				StringBuilder buffer = new StringBuilder();
				buffer.append('{');
				buffer.append("expression=");
				if (root != null) {
					root.toInorder(buffer);
				} else {
					buffer.append("null");
				}
				buffer.append('}');
				return buffer.toString();
			}
		}

		// operational symbols
		static final int EXPRESSION_SEPARATOR_INDEX = GrammarNode.END_OF_TYPE + 1;
		public static final GrammarCommand EXPRESSION_SEPARATOR = new GrammarCommand(
				EXPRESSION_SEPARATOR_INDEX);
		// generators
		public static final GrammarCommand EMPTY_STRING = new GrammarCommand(
				GrammarNode.EMPTY_STRING);
		public static final GrammarCommand EMPTY_CHARACTER = new GrammarCommand(
				GrammarNode.EMPTY_CHARACTER);
		// unaries
		public static final GrammarCommand CHARACTER_COMPLEMENTS = new GrammarCommand(
				GrammarNode.CHARACTER_COMPLEMENTS);
		public static final GrammarCommand STARS = new GrammarCommand(
				GrammarNode.STARS);
		// binaries
		public static final GrammarCommand CHARACTER_RANGE = new GrammarCommand(
				GrammarNode.CHARACTER_RANGE);
		public static final GrammarCommand MULTIPLIES = new GrammarCommand(
				GrammarNode.MULTIPLIES);
		public static final GrammarCommand PLUS = new GrammarCommand(
				GrammarNode.PLUS);
		public static final GrammarCommand CHARACTER_MINUS = new GrammarCommand(
				GrammarNode.CHARACTER_MINUS);
		public static final GrammarCommand ASSIGNS = new GrammarCommand(
				GrammarNode.ASSIGNS);

		public static final GrammarCommand variable(String value) {
			return new VariableCommand(value);
		}
		public static final GrammarCommand string(String value) {
			if (value == null) {
				return GrammarCommand.EMPTY_CHARACTER;
			} else if (value.length() < 1) {
				return GrammarCommand.EMPTY_STRING;
			}
			return new StringCommand(value.getBytes(StringHelper.CHARSET_UTF_8), true);
		}
		public static final GrammarCommand character(String value) {
			if (value == null) {
				return GrammarCommand.EMPTY_CHARACTER;
			}
			byte[] array = value.getBytes(StringHelper.CHARSET_UTF_8);
			if (array.length != 1) {
				String msg = "value is not character";
				throw new IllegalArgumentException(msg);
			}
			return new CharacterCommand(array[0], true);
		}

		public static void makeSystem(Map<String, ExpressionData> output,
				List<GrammarCommand> input) {
			GrammarCommand.makeSystem(output, input, 0, input.size());
		}
		static String msgStackUnderflow(int expected, int actual) {
			return "expected stack size <= " + expected + " but actual=" + actual;
		}
		public static void makeSystem(Map<String, ExpressionData> output,
				List<GrammarCommand> input, int begin, int end) {
			int nCommand = end - begin;
			ArrayList<GrammarNode> stack = new ArrayList<GrammarNode>(nCommand);
			for (; begin < end; ++begin) {
				GrammarCommand cmd = input.get(begin);
				switch (cmd.type) {
				case EXPRESSION_SEPARATOR_INDEX: {
				}
				break;
				// generators
				case GrammarNode.VARIABLE:
				case GrammarNode.STRING:
				case GrammarNode.CHARACTER: {
					GrammarNode node = cmd.newNode();
					stack.add(node);
				}
				break;
				case GrammarNode.CHARACTER_COMPLEMENTS:
				case GrammarNode.STARS: {
					int n = stack.size();
					if (n < 1) {
						throw new NoSuchElementException(msgStackUnderflow(1, n));
					}
					GrammarNode child = stack.remove(n - 1);
					UnaryNode parent = (UnaryNode) cmd.newNode();
					parent.setChild(child);
					stack.add(parent);
				}
				break;
				case GrammarNode.CHARACTER_RANGE:
				case GrammarNode.CHARACTER_MEETS:
				case GrammarNode.MULTIPLIES:
				case GrammarNode.PLUS:
				case GrammarNode.CHARACTER_MINUS: {
					int n = stack.size();
					if (n < 2) {
						throw new NoSuchElementException(msgStackUnderflow(2, n));
					}
					GrammarNode child1 = stack.remove(n - 1);
					GrammarNode child0 = stack.remove(n - 2);
					BinaryNode parent = (BinaryNode) cmd.newNode();
					parent.setChildren(child0, child1);
					stack.add(parent);
				}
				break;
				case GrammarNode.ASSIGNS: {
					int n = stack.size();
					if (n < 2) {
						throw new NoSuchElementException(msgStackUnderflow(2, n));
					}
					GrammarNode child1 = stack.remove(n - 1);
					GrammarNode child0 = stack.remove(n - 2);
					if (child0.getType() != GrammarNode.VARIABLE) {
						String msg = Messages.getUnexpectedValue("type", "variable", child0
								.getTypeName());
						throw new IllegalArgumentException(msg);
					}
					VariableNode var = (VariableNode) child0;
					ExpressionData datum = new ExpressionData(child1);
					output.put(var.value, datum);
					stack.add(var);
				}
				break;
				default: {
					String msg = "unkonwn command=" + GrammarNode.getTypeName(cmd.type);
					throw new NoSuchElementException(msg);
				}
				}
			}
		}

		final int type;

		public GrammarCommand(int type) {
			this.type = type;
		}
		protected GrammarNode newNode() {
			return GrammarNode.newNode(this.type);
		}
	}

	static class VariableCommand extends GrammarCommand {
		final String value;

		public VariableCommand(String value) {
			super(GrammarNode.VARIABLE);
			this.value = value;
		}
		@Override
		protected GrammarNode newNode() {
			return new VariableNode(this.value);
		}
	}

	static class StringCommand extends GrammarCommand {
		final byte[] value;
		final boolean utf8;

		public StringCommand(byte[] value, boolean utf8) {
			super(GrammarNode.STRING);
			this.value = value;
			this.utf8 = utf8;
		}
		@Override
		protected GrammarNode newNode() {
			return new StringNode(this.value, this.utf8);
		}
	}

	static class CharacterCommand extends GrammarCommand {
		final byte value;
		final boolean ascii;

		public CharacterCommand(byte value, boolean ascii) {
			super(GrammarNode.CHARACTER);
			this.value = value;
			this.ascii = ascii;
		}
		@Override
		protected GrammarNode newNode() {
			return new CharacterNode(this.value, this.ascii);
		}
	}

	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
		super.setUp();
	}
	public void testGrammarTree() {
		List<GrammarCommand> commands = new ArrayList<GrammarCommand>(64);
		if (true) {
			commands.add(GrammarCommand.variable("B"));
			commands.add(GrammarCommand.variable("U"));
			commands.add(GrammarCommand.variable("b"));
			commands.add(GrammarCommand.variable("B"));
			commands.add(GrammarCommand.MULTIPLIES);
			commands.add(GrammarCommand.STARS);
			commands.add(GrammarCommand.MULTIPLIES);
			commands.add(GrammarCommand.ASSIGNS);
			commands.add(GrammarCommand.EXPRESSION_SEPARATOR);
		}
		if (true) {
			commands.add(GrammarCommand.variable("U"));
			commands.add(GrammarCommand.variable("V"));
			commands.add(GrammarCommand.character("("));
			commands.add(GrammarCommand.variable("B"));
			commands.add(GrammarCommand.MULTIPLIES);
			commands.add(GrammarCommand.character(")"));
			commands.add(GrammarCommand.MULTIPLIES);
			commands.add(GrammarCommand.PLUS);
			commands.add(GrammarCommand.ASSIGNS);
			commands.add(GrammarCommand.EXPRESSION_SEPARATOR);
		}
		if (true) {
			commands.add(GrammarCommand.variable("V"));
			commands.add(GrammarCommand.variable("v0"));
			commands.add(GrammarCommand.variable("a"));
			commands.add(GrammarCommand.character("_"));
			commands.add(GrammarCommand.PLUS);
			commands.add(GrammarCommand.ASSIGNS);
			commands.add(GrammarCommand.variable("v1"));
			commands.add(GrammarCommand.variable("v0"));
			commands.add(GrammarCommand.variable("d"));
			commands.add(GrammarCommand.PLUS);
			commands.add(GrammarCommand.ASSIGNS);
			commands.add(GrammarCommand.STARS);
			commands.add(GrammarCommand.PLUS);
			commands.add(GrammarCommand.ASSIGNS);
			commands.add(GrammarCommand.EXPRESSION_SEPARATOR);
		}
		if (true) {
			commands.add(GrammarCommand.variable("b"));
			commands.add(GrammarCommand.character("*"));
			commands.add(GrammarCommand.character(" "));
			commands.add(GrammarCommand.PLUS);
			commands.add(GrammarCommand.ASSIGNS);
			commands.add(GrammarCommand.EXPRESSION_SEPARATOR);
		}
		if (true) {
			commands.add(GrammarCommand.variable("a"));
			commands.add(GrammarCommand.character("a"));
			commands.add(GrammarCommand.character("z"));
			commands.add(GrammarCommand.CHARACTER_RANGE);
			commands.add(GrammarCommand.character("A"));
			commands.add(GrammarCommand.character("Z"));
			commands.add(GrammarCommand.CHARACTER_RANGE);
			commands.add(GrammarCommand.PLUS);
			commands.add(GrammarCommand.ASSIGNS);
			commands.add(GrammarCommand.EXPRESSION_SEPARATOR);
		}
		if (true) {
			commands.add(GrammarCommand.variable("d"));
			commands.add(GrammarCommand.character("0"));
			commands.add(GrammarCommand.character("9"));
			commands.add(GrammarCommand.CHARACTER_RANGE);
			commands.add(GrammarCommand.ASSIGNS);
			commands.add(GrammarCommand.EXPRESSION_SEPARATOR);
		}
		if (true) {
			Map<String, ExpressionData> nodes = new TreeMap<String, ExpressionData>();
			GrammarCommand.makeSystem(nodes, commands);
			Debug.log().debug(nodes);
		}
	}
}
