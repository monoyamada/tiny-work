package parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import junit.framework.Assert;
import junit.framework.TestCase;
import tiny.lang.ArrayHelper;
import tiny.lang.Debug;
import tiny.lang.FileHelper;
import tiny.primitive.ByteArrayList;
import tiny.primitive.IntArrayList;

public class SimpleTest_3 extends TestCase {
	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
		super.setUp();
	}

	static interface ByteInput {
		int END_OF_INPUT = Byte.MIN_VALUE - 1;

		long position();
		ByteInput pushMark();
		ByteInput popMark();
		ByteInput setMark();
		ByteInput goMark();
		ByteInput next();

		int get() throws IOException;
		byte[] array(long begin) throws IOException;
	}

	static class ArrayInput implements ByteInput {
		static ArrayInput readUTF8(File file) throws IOException {
			FileChannel input = null;
			try {
				input = new FileInputStream(file).getChannel();
				long size = input.size();
				if (Integer.MAX_VALUE <= size) {
					String msg = "too big file=" + file;
					throw new IOException(msg);
				}
				byte[] array = new byte[(int) size];
				ByteBuffer buffer = ByteBuffer.wrap(array);
				int n = input.read(buffer);
				while (0 < n) {
					buffer.rewind();
					n = input.read(buffer);
				}
				if (buffer.limit() < size) {
					String msg = "could not read file=" + file;
					throw new IOException(msg);
				}
				return new ArrayInput(array);
			} finally {
				FileHelper.close(input);
			}
		}

		int position;
		IntArrayList positions;
		final byte[] array;

		int maxBacktrace;
		int countBacktrace;
		int maxLookahead;

		ArrayInput(byte[] array) {
			this.array = array != null ? array : ArrayHelper.EMPTY_BYTE_ARRAY;
		}
		public byte[] array() {
			return this.array;
		}
		@Override
		public long position() {
			return this.position;
		}
		IntArrayList getPositions(boolean anyway) {
			if (this.positions == null && anyway) {
				this.positions = new IntArrayList(128);
			}
			return this.positions;
		}
		@Override
		public ByteInput pushMark() {
			this.getPositions(true).push(this.position);
			return this;
		}
		@Override
		public ByteInput popMark() {
			IntArrayList list = this.getPositions(false);
			if (list == null || list.size() < 1) {
				String msg = "there is not stored position";
				throw new NoSuchElementException(msg);
			}
			list.removeLast();
			return this;
		}
		@Override
		public ByteInput setMark() {
			IntArrayList list = this.getPositions(false);
			if (list == null || list.size() < 1) {
				String msg = "there is not stored position";
				throw new NoSuchElementException(msg);
			}
			list.setTop(this.position);
			return this;
		}
		@Override
		public ByteInput goMark() {
			IntArrayList list = this.getPositions(false);
			if (list == null || list.size() < 1) {
				String msg = "there is not stored position";
				throw new NoSuchElementException(msg);
			}
			int top = list.top(this.position);
			if (this.position != top) {
				++this.countBacktrace;
				this.maxBacktrace = Math.max(this.maxBacktrace, this.position - top);
			}
			this.position = top;
			return this;
		}
		@Override
		public ArrayInput next() {
			if (this.position < this.array.length) {
				++this.position;
			}
			return this;
		}
		@Override
		public int get() {
			if (this.position < this.array.length) {
				IntArrayList list = this.getPositions(false);
				if (list != null && 0 < list.size()) {
					this.maxLookahead = Math.max(this.maxLookahead,
							this.position - list.get(0));
				}
				return this.array[this.position] & 0xff;
			}
			return ByteInput.END_OF_INPUT;
		}
		@Override
		public byte[] array(long begin) throws IOException {
			int index = (int) begin;
			return Arrays.copyOfRange(this.array, index, this.position);
		}
	}

	/**
	 * tree node.
	 */
	static class Node<T extends Node<T>> {
		T nextNode;
		T firstNode;
		T lastNode;

		/**
		 * @return next sibling.
		 */
		T nextNode() {
			return this.nextNode;
		}
		/**
		 * @return leftmost child.
		 */
		T firstNode() {
			return this.firstNode;
		}
		/**
		 * @return rightmost child.
		 */
		T lastNode() {
			return this.lastNode;
		}
		T mostNextNode() {
			@SuppressWarnings("unchecked")
			T that = (T) this;
			while (that.nextNode() != null) {
				that = that.nextNode();
			}
			return that;
		}
		T mostFirstNode() {
			@SuppressWarnings("unchecked")
			T that = (T) this;
			while (that.firstNode() != null) {
				that = that.firstNode();
			}
			return that;
		}
		T mostLastNode() {
			@SuppressWarnings("unchecked")
			T that = (T) this;
			while (that.lastNode() != null) {
				that = that.lastNode();
			}
			return that;
		}
		/**
		 * @param child
		 *          do nothing iff child is <cod>null</code>.
		 * @return
		 */
		@SuppressWarnings("unchecked")
		T addNode(T child) {
			if (child == null) {
			} else if (this.lastNode == null) {
				this.firstNode = child;
				this.lastNode = child.mostNextNode();
			} else {
				this.lastNode.nextNode = child;
				this.lastNode = child.mostNextNode();
			}
			return (T) this;
		}
		@SuppressWarnings("unchecked")
		T removeNode() {
			if (this.firstNode != null) {
				this.firstNode = this.firstNode.nextNode;
				if (this.firstNode == null) {
					this.lastNode = null;
				}
			}
			return (T) this;
		}
		@SuppressWarnings("unchecked")
		T clearNode() {
			this.firstNode = this.lastNode = null;
			return (T) this;
		}
	}

	static class TagNode extends Node<TagNode> implements Cloneable {
		public static final TagNode DUMMY_NODE = new Dummy();

		static class Dummy extends TagNode {
			Dummy() {
				super(Tagger.TAG_SIZE, Tagger.END_OF_INPUT, Tagger.END_OF_INPUT);
			}
			@Override
			Dummy addNode(TagNode child) {
				return this;
			}
		};

		static class Eliminatable extends TagNode {
			Eliminatable(int name, long begin, long end) {
				super(name, begin, end);
			}
			@Override
			boolean isEliminatable() {
				return true;
			}
		}

		static class OperatorNode extends TagNode {
			final OperatorInfo info;

			OperatorNode(int name, long begin, long end, OperatorInfo info) {
				super(name, begin, end);
				this.info = info;
			}
			@Override
			OperatorInfo getOperatorInfo() {
				return this.info;
			}
		}

		final int name;
		final long begin;
		long end;

		TagNode(int name, long begin, long end) {
			this.name = name;
			this.begin = begin;
			this.end = end;
		}
		public TagNode clone() {
			try {
				TagNode that = (TagNode) super.clone();
				that.nextNode = null;
				return that;
			} catch (CloneNotSupportedException ex) {
				ex.printStackTrace();
			}
			TagNode that = new TagNode(this.name, this.begin, this.end);
			that.firstNode = this.firstNode;
			that.lastNode = this.lastNode;
			return that;
		}
		@Override
		public String toString() {
			OperatorInfo info = this.getOperatorInfo();
			if (info == null) {
				return Tagger.tagName(this.name) + " [" + this.begin + ", " + this.end
						+ "]";
			}
			return Tagger.tagName(this.name) + " [" + this.begin + ", " + this.end
					+ "] = " + info.expression;
		}
		int name() {
			return this.name;
		}
		long begin() {
			return this.begin;
		}
		long end() {
			return this.end;
		}
		TagNode setEnd(long value) {
			this.end = value;
			return this;
		}
		boolean isEliminatable() {
			return false;
		}
		OperatorInfo getOperatorInfo() {
			return null;
		}
		@Override
		TagNode addNode(TagNode child) {
			if (child == null || child == DUMMY_NODE) {
				return this;
			} else if (child.isEliminatable()) {
				return super.addNode(child.firstNode());
			}
			return super.addNode(child);
		}
		TagNode firstNodeOf(int name) {
			if (this.name == name) {
				return this;
			}
			TagNode child = this.firstNode();
			TagNode node = null;
			while (child != null && node == null) {
				node = child.firstNodeOf(name);
				child = child.nextNode();
			}
			return node;
		}
		int nodeOf(Collection<TagNode> output, int name) {
			int n = 0;
			if (this.name == name) {
				n += 1;
				if (output != null) {
					output.add(this);
				}
			}
			TagNode child = this.firstNode();
			while (child != null) {
				n += child.nodeOf(output, name);
				child = child.nextNode();
			}
			return n;
		}
	}

	static class Parser {
		Tagger[] taggers = new Tagger[Tagger.TAG_SIZE];
		private Map<String, OperatorInfo> operatorInfoMap;

		public Tagger getTagger(int name, long begin) {
			Tagger tagger = this.taggers[name];
			if (tagger == null) {
				tagger = this.newTagger(name);
				this.taggers[name] = tagger;
			}
			return tagger;
		}
		private Tagger newTagger(int name) {
			switch (name) {
			case Tagger.RAW_TAG:
				return new Raw(this);
			case Tagger.DOCUMENT_TAG:
				return new Document(this);
			case Tagger.BOM_TAG:
				return new Bom(this);
			case Tagger.COMMENT_TAG:
				return new Comment(this);
			case Tagger.LINE_COMMENT_TAG:
				return new LineComment(this);
			case Tagger.BLOCK_COMMENT_TAG:
				return new BlockComment(this);
			case Tagger.WHITE_BLCOK_TAG:
				return new WhiteBlock(this);
			case Tagger.IGNORABLE_BLOCK_TAG:
				return new IgnorableBlock(this);
			case Tagger.EQUATION_LIST_TAG:
				return new EquationList(this);
			case Tagger.STOPPED_EQUATION_TAG:
				return new StoppedEquation(this);
			case Tagger.EMPTY_EQUATION_TAG:
				return new EmptyEquation(this);
			case Tagger.EQUATION_TAG:
				return new Equation(this);
			case Tagger.MULTITIVE_EQUATON_TAG:
				return new MultitiveEquation(this);
			case Tagger.PRIMARY_EQUATION_TAG:
				return new PrimaryEquation(this);
			case Tagger.VARIABLE_TAG:
				return new Variable(this);
			case Tagger.EMPTY_VARIABLE_TAG:
				return new EmptyVariable(this);
			case Tagger.CHARACTER_TAG:
				return new CharacterTagger(this);
			case Tagger.STRING_TAG:
				return new StringTagger(this);
			case Tagger.INFIX_OPERATOR_TAG:
				return new InfixOperator(this);
			case Tagger.EMPTY_INFIX_OPERATOR_TAG:
				return new EmptyInfixOperator(this);
			case Tagger.NUMBER_TAG:
				return new NumberTagger(this);
			case Tagger.PREFIX_OPERATOR_TAG:
				return new PrefixOperator(this);
			case Tagger.POSTFIX_OPERATOR_TAG:
				return new PostfixOperator(this);
			default:
				String msg = "unknown tagger=" + name;
				throw new NoSuchElementException(msg);
			}
		}
		public TagNode parse(int name, ByteInput input) throws IOException {
			return this.getTagger(name, input.position()).parse(input);
		}
		public Parser setNodeType(int type, int... tags) {
			for (int i = 0, n = tags != null ? tags.length : 0; i < n; ++i) {
				this.getTagger(tags[i], 0).setNodeType(type);
			}
			return this;
		}
		public OperatorInfo getOperatorInfo(String word) {
			Map<String, OperatorInfo> ops = this.getOperatorInfoMap();
			if (ops == null) {
				return null;
			}
			return ops.get(word);
		}
		private Map<String, OperatorInfo> getOperatorInfoMap() {
			return this.operatorInfoMap;
		}
		void setOperatorInfoMap(Map<String, OperatorInfo> map) {
			this.operatorInfoMap = map;
		}
	}

	static abstract class Tagger {
		static final int RAW_TAG = 0;
		static final int COMMENT_TAG = RAW_TAG + 1;
		static final int LINE_COMMENT_TAG = COMMENT_TAG + 1;
		static final int BLOCK_COMMENT_TAG = LINE_COMMENT_TAG + 1;
		static final int WHITE_BLCOK_TAG = BLOCK_COMMENT_TAG + 1;
		static final int IGNORABLE_BLOCK_TAG = WHITE_BLCOK_TAG + 1;
		static final int EQUATION_LIST_TAG = IGNORABLE_BLOCK_TAG + 1;
		static final int STOPPED_EQUATION_TAG = EQUATION_LIST_TAG + 1;
		static final int EMPTY_EQUATION_TAG = STOPPED_EQUATION_TAG + 1;
		static final int EQUATION_TAG = EMPTY_EQUATION_TAG + 1;
		static final int MULTITIVE_EQUATON_TAG = EQUATION_TAG + 1;
		static final int PRIMARY_EQUATION_TAG = MULTITIVE_EQUATON_TAG + 1;
		static final int VARIABLE_TAG = PRIMARY_EQUATION_TAG + 1;
		static final int EMPTY_VARIABLE_TAG = VARIABLE_TAG + 1;
		static final int NUMBER_TAG = EMPTY_VARIABLE_TAG + 1;
		static final int CHARACTER_TAG = NUMBER_TAG + 1;
		static final int STRING_TAG = CHARACTER_TAG + 1;
		static final int INFIX_OPERATOR_TAG = STRING_TAG + 1;
		static final int EMPTY_INFIX_OPERATOR_TAG = INFIX_OPERATOR_TAG + 1;
		static final int PREFIX_OPERATOR_TAG = EMPTY_INFIX_OPERATOR_TAG + 1;
		static final int POSTFIX_OPERATOR_TAG = PREFIX_OPERATOR_TAG + 1;
		static final int DOCUMENT_TAG = POSTFIX_OPERATOR_TAG + 1;
		static final int BOM_TAG = DOCUMENT_TAG + 1;
		static final int TAG_SIZE = BOM_TAG + 1;

		static final int END_OF_INPUT = ByteInput.END_OF_INPUT;

		static final byte EOL = AsciiHelper.NEW_LINE;
		static final byte COMMENT_SYMBOL = AsciiHelper.SHARP;
		static final byte COMMENT_BRA = AsciiHelper.CURLY_BRA;
		static final byte COMMENT_KET = AsciiHelper.CURLY_KET;
		static final byte COMMENT_ESCAPE = AsciiHelper.BACK_SOLIDUS;
		static final byte BLOCK_BRA = AsciiHelper.CURLY_BRA;
		static final byte BLOCK_KET = AsciiHelper.CURLY_KET;
		static final byte ESCAPE_SYMBOL = AsciiHelper.BACK_SOLIDUS;
		static final byte EQUATION_STOP = AsciiHelper.SEMICOLON;
		static final byte EQUATION_BRA = AsciiHelper.ROUND_BRA;
		static final byte EQUATION_KET = AsciiHelper.ROUND_KET;
		static final byte CHARACTER_BRA = AsciiHelper.SINGLE_QUOTE;
		static final byte CHARACTER_KET = CHARACTER_BRA;
		static final byte STRING_BRA = AsciiHelper.DOUBLE_QUOTE;
		static final byte STRING_KET = STRING_BRA;
		static final byte ESCAPED_BACK_SPACE = AsciiHelper.A + ('b' - 'a');
		static final byte ESCAPED_HORIZONTAL_TAB = AsciiHelper.A + ('t' - 'a');
		static final byte ESCAPED_NEW_LINE = AsciiHelper.A + ('n' - 'a');
		static final byte ESCAPED_FORM_FEED = AsciiHelper.A + ('f' - 'a');
		static final byte ESCAPED_CARRIAGE_RETURN = AsciiHelper.A + ('r' - 'a');
		static final byte ESCAPED_HEX_DIGIT = AsciiHelper.A;

		static final int NONE_NODE = 0;
		static final int NORMAL_NODE = NONE_NODE + 1;
		static final int ELIMINATABLE_NODE = NORMAL_NODE + 1;
		static final int OPERATOR_NODE = ELIMINATABLE_NODE + 1;

		public static String tagName(TagNode node) {
			if (node == null) {
				return "null";
			}
			return Tagger.tagName(node.name());
		}
		public static String tagName(int name) {
			switch (name) {
			case RAW_TAG:
				return "Raw";
			case DOCUMENT_TAG:
				return "Document";
			case BOM_TAG:
				return "BOM";
			case EQUATION_LIST_TAG:
				return "EquationList";
			case STOPPED_EQUATION_TAG:
				return "StoppedEquation";
			case EMPTY_EQUATION_TAG:
				return "EmptyEquation";
			case EQUATION_TAG:
				return "Equation";
			case MULTITIVE_EQUATON_TAG:
				return "Multitive";
			case PRIMARY_EQUATION_TAG:
				return "Primary";
			case INFIX_OPERATOR_TAG:
				return "Infix";
			case EMPTY_INFIX_OPERATOR_TAG:
				return "EmptyInfix";
			case PREFIX_OPERATOR_TAG:
				return "Prefix";
			case POSTFIX_OPERATOR_TAG:
				return "Postfix";
			case VARIABLE_TAG:
				return "Variable";
			case EMPTY_VARIABLE_TAG:
				return "EmptyVariable";
			case CHARACTER_TAG:
				return "Character";
			case STRING_TAG:
				return "String";
			case NUMBER_TAG:
				return "Number";
			case IGNORABLE_BLOCK_TAG:
				return "IgnorableBlock";
			case WHITE_BLCOK_TAG:
				return "WhiteBlock";
			case COMMENT_TAG:
				return "Comment";
			case LINE_COMMENT_TAG:
				return "LineComment";
			case BLOCK_COMMENT_TAG:
				return "BlockComment";
			default:
				return "Unknown=" + name;
			}
		}
		public static int[] tags() {
			int[] tags = new int[TAG_SIZE];
			for (int n = TAG_SIZE; 0 < n--;) {
				tags[n] = n;
			}
			return tags;
		}

		final Parser parser;
		int nodeType;

		Tagger(Parser parser) {
			this.parser = parser;
			this.nodeType = NORMAL_NODE;
		}
		Parser getParser() {
			return this.parser;
		}
		TagNode parse(int name, ByteInput input) throws IOException {
			ArrayInput x = (ArrayInput) input;
			int n = x.getPositions(true).size();
			TagNode node = this.getParser().parse(name, input);
			n = x.getPositions(true).size() - n;
			if (0 < n) {
				Debug.log().debug(Tagger.tagName(name) + ":" + n);
			}
			return node;
			// return this.getParser().parse(name, input);
		}
		TagNode newNode(long begin, ByteInput input) throws IOException {
			long end = input.position();
			switch (this.nodeType) {
			case NONE_NODE:
				return TagNode.DUMMY_NODE;
			case NORMAL_NODE:
				return new TagNode(this.tagName(), begin, end);
			case ELIMINATABLE_NODE:
				return new TagNode.Eliminatable(this.tagName(), begin, end);
			case OPERATOR_NODE: {
				String word = new String(input.array(begin), FileHelper.UTF_8);
				OperatorInfo info = this.getParser().getOperatorInfo(word);
				if (info == null) {
					Debug.log().debug("could not fine operator info for=" + word);
				}
				return new TagNode.OperatorNode(this.tagName(), begin, end, info);
			}
			default:
				throw new NoSuchElementException("uknown node type=" + this.nodeType);
			}
		}
		Tagger setNodeType(int type) {
			this.nodeType = type;
			return this;
		}
		abstract int tagName();
		abstract TagNode parse(ByteInput input) throws IOException;
	}

	static class Raw extends Tagger {
		Raw(Parser parser) {
			super(parser);
		}
		@Override
		int tagName() {
			return Tagger.RAW_TAG;
		}
		@Override
		public TagNode parse(ByteInput input) throws IOException {
			long begin = input.position();
			if (input.get() == Tagger.END_OF_INPUT) {
				return null;
			}
			return this.newNode(begin, input.next());
		}
	}

	static class Document extends Tagger {
		Document(Parser parser) {
			super(parser);
		}
		@Override
		int tagName() {
			return Tagger.DOCUMENT_TAG;
		}
		@Override
		public TagNode parse(ByteInput input) throws IOException {
			long begin = input.position();
			TagNode bom = this.parse(Tagger.BOM_TAG, input);
			TagNode ig_1 = this.parse(Tagger.IGNORABLE_BLOCK_TAG, input);
			TagNode eq = this.parse(Tagger.EQUATION_LIST_TAG, input);
			TagNode ig_2 = this.parse(Tagger.IGNORABLE_BLOCK_TAG, input);
			if (input.get() != Tagger.END_OF_INPUT) {
				return null;
			}
			return this.newNode(begin, input).addNode(bom).addNode(ig_1).addNode(eq)
					.addNode(ig_2);
		}
	}

	static class Bom extends Tagger {
		Bom(Parser parser) {
			super(parser);
		}
		@Override
		int tagName() {
			return Tagger.BOM_TAG;
		}
		@Override
		public TagNode parse(ByteInput input) throws IOException {
			long begin = input.position();
			if (input.get() != AsciiHelper.BOM_0) {
				return null;
			}
			TagNode bom_0 = this.parse(Tagger.RAW_TAG, input);
			if (input.get() != AsciiHelper.BOM_1) {
				return null;
			}
			TagNode bom_1 = this.parse(Tagger.RAW_TAG, input);
			if (input.get() != AsciiHelper.BOM_2) {
				return null;
			}
			TagNode bom_2 = this.parse(Tagger.RAW_TAG, input);
			return this.newNode(begin, input).addNode(bom_0).addNode(bom_1)
					.addNode(bom_2);
		}
	}

	static class EquationList extends Tagger {
		EquationList(Parser parser) {
			super(parser);
		}
		@Override
		int tagName() {
			return Tagger.EQUATION_LIST_TAG;
		}
		@Override
		public TagNode parse(ByteInput input) throws IOException {
			long begin = input.position();
			TagNode node = this.newNode(begin, input.pushMark()); // 1
			TagNode ig = null;
			TagNode child = this.parse(Tagger.STOPPED_EQUATION_TAG, input);
			while (child != null) {
				node.addNode(ig).addNode(child);
				input.setMark();
				ig = this.parse(Tagger.IGNORABLE_BLOCK_TAG, input); // 1
				child = this.parse(Tagger.STOPPED_EQUATION_TAG, input);
			}
			return node.setEnd(input.goMark().popMark().position()); // 0
		}
	}

	static class StoppedEquation extends Tagger {
		StoppedEquation(Parser parser) {
			super(parser);
		}
		@Override
		int tagName() {
			return Tagger.STOPPED_EQUATION_TAG;
		}
		@Override
		public TagNode parse(ByteInput input) throws IOException {
			long begin = input.pushMark().position(); // 1
			TagNode eq = this.parse(Tagger.EQUATION_TAG, input);
			if (eq == null) {
				eq = this.parse(Tagger.EMPTY_EQUATION_TAG, input);
			}
			TagNode ig = this.parse(Tagger.IGNORABLE_BLOCK_TAG, input);
			TagNode stop = null;
			if (input.get() == Tagger.EQUATION_STOP) {
				stop = this.parse(Tagger.RAW_TAG, input);
			} else {
				input.goMark().popMark(); // 0
				return null;
			}
			TagNode node = this.newNode(begin, input);
			node.addNode(eq).addNode(ig).addNode(stop);
			return node.setEnd(input.popMark().position()); // 0
		}
	}

	static class EmptyEquation extends Equation {
		EmptyEquation(Parser parser) {
			super(parser);
		}
		@Override
		int tagName() {
			return Tagger.EMPTY_EQUATION_TAG;
		}
		@Override
		public TagNode parse(ByteInput input) throws IOException {
			return this.newNode(input.position(), input);
		}
	}

	static class Equation extends Tagger {
		Equation(Parser parser) {
			super(parser);
		}
		@Override
		int tagName() {
			return Tagger.EQUATION_TAG;
		}
		@Override
		public TagNode parse(ByteInput input) throws IOException {
			long begin = input.pushMark().position(); // 1
			TagNode node = null;
			TagNode op = this.parse(Tagger.PREFIX_OPERATOR_TAG, input);
			if (op != null) {
				TagNode ig = this.parse(Tagger.IGNORABLE_BLOCK_TAG, input);
				TagNode eq = this.parse(Tagger.MULTITIVE_EQUATON_TAG, input);
				if (eq == null) {
					input.goMark().popMark(); // 0
					return null;
				}
				node = this.newNode(begin, input);
				node.addNode(op).addNode(ig).addNode(eq);
			} else {
				TagNode eq = this.parse(Tagger.MULTITIVE_EQUATON_TAG, input);
				if (eq == null) {
					input.goMark().popMark(); // 0
					return null;
				}
				TagNode ig = this.parse(Tagger.IGNORABLE_BLOCK_TAG, input);
				op = this.parse(Tagger.POSTFIX_OPERATOR_TAG, input);
				node = this.newNode(begin, input);
				node.addNode(eq).addNode(ig).addNode(op);
			}
			input.popMark(); // 0
			return node;
		}
	}

	static class MultitiveEquation extends Tagger {
		MultitiveEquation(Parser parser) {
			super(parser);
		}
		@Override
		int tagName() {
			return Tagger.MULTITIVE_EQUATON_TAG;
		}
		@Override
		public TagNode parse(ByteInput input) throws IOException {
			long begin = input.pushMark().position(); // 1
			TagNode var = this.parse(PRIMARY_EQUATION_TAG, input);
			if (var == null) {
				input.goMark().popMark(); // 0
				return null;
			}
			TagNode node = this.newNode(begin, input);
			TagNode ig_1 = null;
			TagNode ig_2 = null;
			TagNode op = null;
			while (var != null) {
				node.addNode(ig_1).addNode(op).addNode(ig_2).addNode(var);
				input.setMark(); // 1
				ig_1 = this.parse(IGNORABLE_BLOCK_TAG, input);
				op = this.parse(INFIX_OPERATOR_TAG, input);
				if (op == null) {
					op = this.parse(EMPTY_INFIX_OPERATOR_TAG, input);
					ig_2 = null;
				} else {
					ig_2 = this.parse(IGNORABLE_BLOCK_TAG, input);
				}
				var = this.parse(PRIMARY_EQUATION_TAG, input);
			}
			input.goMark().popMark(); // 0
			return node;
		}
	}

	static class PrimaryEquation extends Tagger {
		PrimaryEquation(Parser parser) {
			super(parser);
		}
		@Override
		int tagName() {
			return Tagger.PRIMARY_EQUATION_TAG;
		}
		@Override
		public TagNode parse(ByteInput input) throws IOException {
			long begin = input.pushMark().position(); // 1
			TagNode node = this.newNode(begin, input);
			TagNode check = this.parseBraket(node, input);
			if (check != null) {
				input.popMark(); // 0
				return node;
			}
			TagNode child = this.parse(CHARACTER_TAG, input);
			if (child != null) {
				input.popMark(); // 0
				return node.addNode(child);
			}
			child = this.parse(STRING_TAG, input);
			if (child != null) {
				input.popMark(); // 0
				return node.addNode(child);
			}
			child = this.parse(NUMBER_TAG, input);
			if (child != null) {
				input.popMark(); // 0
				return node.addNode(child);
			}
			child = this.parse(VARIABLE_TAG, input);
			if (child != null) {
				input.popMark(); // 0
				return node.addNode(child);
			}
			input.goMark().popMark(); // 0
			return null;
		}
		private TagNode parseBraket(TagNode output, ByteInput input)
				throws IOException {
			if (input.get() != Tagger.EQUATION_BRA) {
				return null;
			}
			TagNode bra = this.parse(RAW_TAG, input.pushMark()); // 1
			TagNode ig_1 = this.parse(IGNORABLE_BLOCK_TAG, input);
			TagNode var = this.parse(EQUATION_TAG, input);
			if (var == null) {
				if (input.get() == Tagger.EQUATION_KET) {
					var = this.parse(EMPTY_VARIABLE_TAG, input);
					TagNode ket = this.parse(RAW_TAG, input);
					input.popMark(); // 0
					return output.addNode(bra).addNode(ig_1).addNode(var).addNode(ket);
				} else {
					input.goMark().popMark(); // 0
					return null;
				}
			}
			TagNode ig_2 = this.parse(IGNORABLE_BLOCK_TAG, input);
			if (input.get() != Tagger.EQUATION_KET) {
				input.goMark().popMark(); // 0
				return null;
			}
			TagNode ket = this.parse(RAW_TAG, input);
			input.popMark(); // 0
			return output.addNode(bra).addNode(ig_1).addNode(var).addNode(ig_2)
					.addNode(ket);
		}
	}

	static class InfixOperator extends Tagger {
		InfixOperator(Parser parser) {
			super(parser);
		}
		@Override
		int tagName() {
			return Tagger.INFIX_OPERATOR_TAG;
		}
		@Override
		public TagNode parse(ByteInput input) throws IOException {
			int ch = input.get();
			if (this.isFirst(ch)) {
				long begin = input.position();
				ch = input.next().get();
				while (this.isLast(ch)) {
					ch = input.next().get();
				}
				return this.newNode(begin, input);
			}
			return null;
		}
		private boolean isFirst(int ch) {
			switch (ch) {
			case AsciiHelper.AND:
			case AsciiHelper.AT:
			case AsciiHelper.ANGLE_BRA:
			case AsciiHelper.COLON:
			case AsciiHelper.COMMA:
			case AsciiHelper.DOLLAR:
				// case AsciiHelper.DOUBLE_QUOTE:
			case AsciiHelper.EQUAL:
			case AsciiHelper.EXCLAMATION:
			case AsciiHelper.GRAVE:
			case AsciiHelper.HAT:
			case AsciiHelper.ANGLE_KET:
			case AsciiHelper.MINUS:
			case AsciiHelper.VERTICAL_LINE:
			case AsciiHelper.PERCENT:
			case AsciiHelper.PERIOD:
			case AsciiHelper.PLUS:
			case AsciiHelper.QUESTION:
				// case AsciiHelper.SEMICOLON:
				// case AsciiHelper.SINGLE_QUOTE:
			case AsciiHelper.SOLIDUS:
			case AsciiHelper.STAR:
			case AsciiHelper.TILDE:
				return true;
			default:
				return false;
			}
		}
		private boolean isLast(int ch) {
			return this.isFirst(ch);
		}
	}

	static class EmptyInfixOperator extends InfixOperator {
		EmptyInfixOperator(Parser parser) {
			super(parser);
		}
		@Override
		int tagName() {
			return Tagger.EMPTY_INFIX_OPERATOR_TAG;
		}
		@Override
		public TagNode parse(ByteInput input) throws IOException {
			return this.newNode(input.position(), input);
		}
	}

	static class PrefixOperator extends InfixOperator {
		PrefixOperator(Parser parser) {
			super(parser);
		}
		@Override
		int tagName() {
			return Tagger.PREFIX_OPERATOR_TAG;
		}
	}

	static class PostfixOperator extends InfixOperator {
		PostfixOperator(Parser parser) {
			super(parser);
		}
		@Override
		int tagName() {
			return Tagger.POSTFIX_OPERATOR_TAG;
		}
	}

	static class Variable extends Tagger {
		Variable(Parser parser) {
			super(parser);
		}
		@Override
		int tagName() {
			return Tagger.VARIABLE_TAG;
		}
		@Override
		public TagNode parse(ByteInput input) throws IOException {
			int ch = input.get();
			if (this.isFirst(ch)) {
				long begin = input.position();
				ch = input.next().get();
				while (isLast(ch)) {
					ch = input.next().get();
				}
				return this.newNode(begin, input);
			}
			return null;
		}
		private boolean isFirst(int ch) {
			switch (ch) {
			case AsciiHelper.UNDERLINE:
				return true;
			default:
				return AsciiHelper.isAlphabet(ch);
			}
		}
		private boolean isLast(int ch) {
			switch (ch) {
			case AsciiHelper.UNDERLINE:
				return true;
			default:
				return AsciiHelper.isAlphabet(ch) || AsciiHelper.isDigit(ch);
			}
		}
	}

	static class EmptyVariable extends Variable {
		EmptyVariable(Parser parser) {
			super(parser);
		}
		@Override
		int tagName() {
			return Tagger.EMPTY_VARIABLE_TAG;
		}
		@Override
		public TagNode parse(ByteInput input) throws IOException {
			return this.newNode(input.position(), input);
		}
	}

	static class NumberTagger extends Tagger {
		NumberTagger(Parser parser) {
			super(parser);
		}
		@Override
		int tagName() {
			return Tagger.NUMBER_TAG;
		}
		@Override
		public TagNode parse(ByteInput input) throws IOException {
			int ch = input.get();
			if (this.isFirst(ch)) {
				long begin = input.position();
				ch = input.next().get();
				while (isLast(ch)) {
					ch = input.next().get();
				}
				return this.newNode(begin, input);
			}
			return null;
		}
		private boolean isFirst(int ch) {
			return AsciiHelper.isDigit(ch);
		}
		private boolean isLast(int ch) {
			return AsciiHelper.isDigit(ch);
		}
	}

	static class CharacterTagger extends Tagger {
		CharacterTagger(Parser parser) {
			super(parser);
		}
		@Override
		int tagName() {
			return Tagger.CHARACTER_TAG;
		}
		@Override
		public TagNode parse(ByteInput input) throws IOException {
			long begin = input.position();
			if (input.get() != Tagger.CHARACTER_BRA) {
				return null;
			}
			TagNode node = this.newNode(begin, input.pushMark()); // 1
			node.addNode(this.parse(RAW_TAG, input));
			if (!parse_1(node, input)) {
				input.goMark().popMark(); // 0
				return null;
			}
			if (input.get() != Tagger.CHARACTER_KET) {
				input.goMark().popMark(); // 0
				return null;
			}
			node.addNode(this.parse(RAW_TAG, input));
			return node.setEnd(input.popMark().position()); // 0
		}
		protected boolean parse_1(TagNode node, ByteInput input) throws IOException {
			switch (input.get()) {
			case Tagger.CHARACTER_KET:
				return false;
			case Tagger.ESCAPE_SYMBOL:
				TagNode esc = this.parse(RAW_TAG, input);
				switch (input.get()) {
				case Tagger.ESCAPED_BACK_SPACE:
				case Tagger.ESCAPED_HORIZONTAL_TAB:
				case Tagger.ESCAPED_NEW_LINE:
				case Tagger.ESCAPED_FORM_FEED:
				case Tagger.ESCAPED_CARRIAGE_RETURN:
				case Tagger.CHARACTER_KET:
				case Tagger.STRING_KET:
					node.addNode(esc).addNode(this.parse(RAW_TAG, input));
					return true;
				case Tagger.ESCAPED_HEX_DIGIT:
					TagNode hex = this.parse(RAW_TAG, input);
					if (!AsciiHelper.isHexDigit(input.get())) {
						return false;
					}
					TagNode n1 = this.parse(RAW_TAG, input);
					if (!AsciiHelper.isHexDigit(input.get())) {
						return false;
					}
					TagNode n2 = this.parse(RAW_TAG, input);
					node.addNode(esc).addNode(hex).addNode(n1).addNode(n2);
					return true;
				default:
					return false;
				}
			default:
				if (input.get() < 0 || Byte.MAX_VALUE < input.get()) {
					return false;
				}
				node.addNode(this.parse(RAW_TAG, input));
				return true;
			}
		}
	}

	static class StringTagger extends CharacterTagger {
		StringTagger(Parser parser) {
			super(parser);
		}
		@Override
		int tagName() {
			return Tagger.STRING_TAG;
		}
		@Override
		public TagNode parse(ByteInput input) throws IOException {
			long begin = input.position();
			if (input.get() != Tagger.STRING_BRA) {
				return null;
			}
			TagNode node = this.newNode(begin, input.pushMark()); // 1
			node.addNode(this.parse(RAW_TAG, input));
			while (parse_1(node, input)) {
			}
			if (input.get() != Tagger.STRING_KET) {
				input.goMark().popMark(); // 0
				return null;
			}
			node.addNode(this.parse(RAW_TAG, input));
			return node.setEnd(input.popMark().position()); // 0
		}
	}

	static class IgnorableBlock extends Tagger {
		IgnorableBlock(Parser parser) {
			super(parser);
		}
		@Override
		int tagName() {
			return Tagger.IGNORABLE_BLOCK_TAG;
		}
		@Override
		public TagNode parse(ByteInput input) throws IOException {
			long begin = input.position();
			TagNode child = this.parseChild(input);
			if (child == null) {
				return null;
			}
			TagNode node = this.newNode(begin, input);
			while (child != null) {
				node.addNode(child);
				child = this.parseChild(input);
			}
			return node;
		}
		private TagNode parseChild(ByteInput input) throws IOException {
			TagNode node = this.parse(WHITE_BLCOK_TAG, input.pushMark()); // 1
			if (node != null) {
				input.popMark(); // 0
				return node;
			}
			node = this.parse(LINE_COMMENT_TAG, input.goMark()); // 1
			if (node != null) {
				input.popMark(); // 0
				return node;
			}
			return this.parse(BLOCK_COMMENT_TAG, input.goMark().popMark());
		}
	}

	static class WhiteBlock extends Tagger {
		WhiteBlock(Parser parser) {
			super(parser);
		}
		@Override
		int tagName() {
			return Tagger.WHITE_BLCOK_TAG;
		}
		@Override
		public TagNode parse(ByteInput input) throws IOException {
			int ch = input.get();
			if (this.isWhite(ch)) {
				long begin = input.position();
				ch = input.next().get();
				while (this.isWhite(ch)) {
					ch = input.next().get();
				}
				return this.newNode(begin, input);
			}
			return null;
		}
		private boolean isWhite(int ch) {
			switch (ch) {
			case AsciiHelper.SPACE:
			case Tagger.EOL:
			case AsciiHelper.HORIZONTAL_TAB:
			case AsciiHelper.CARRIAGE_RETURN:
				return true;
			default:
				return false;
			}
		}
	}

	static class Comment extends Tagger {
		Comment(Parser parser) {
			super(parser);
		}
		@Override
		int tagName() {
			return Tagger.COMMENT_TAG;
		}
		@Override
		public TagNode parse(ByteInput input) throws IOException {
			TagNode child = this.parse(LINE_COMMENT_TAG, input);
			if (child != null) {
				return this.newNode(child.begin(), input).addNode(child);
			}
			child = this.parse(BLOCK_COMMENT_TAG, input);
			if (child != null) {
				return this.newNode(child.begin(), input).addNode(child);
			}
			return null;
		}
	}

	static class LineComment extends Tagger {
		LineComment(Parser parser) {
			super(parser);
		}
		@Override
		int tagName() {
			return Tagger.LINE_COMMENT_TAG;
		}
		@Override
		public TagNode parse(ByteInput input) throws IOException {
			long begin = input.position();
			if (input.get() != Tagger.COMMENT_SYMBOL) {
				return null;
			}
			TagNode a_1 = this.parse(RAW_TAG, input.pushMark()); // 1
			if (input.get() != Tagger.COMMENT_SYMBOL) {
				input.goMark().popMark(); // 0
				return null;
			}
			TagNode a_2 = this.parse(RAW_TAG, input);
			TagNode node = this.newNode(begin, input);
			node.addNode(a_1).addNode(a_2);
			int ch = input.get();
			while (ch != Tagger.EOL && ch != Tagger.END_OF_INPUT) {
				node.addNode(this.parse(RAW_TAG, input));
				ch = input.get();
			}
			if (ch != Tagger.END_OF_INPUT) {
				node.addNode(this.parse(RAW_TAG, input));
			}
			input.popMark();
			return node;
		}
	}

	static class BlockComment extends Tagger {
		BlockComment(Parser parser) {
			super(parser);
		}
		@Override
		int tagName() {
			return Tagger.BLOCK_COMMENT_TAG;
		}
		@Override
		public TagNode parse(ByteInput input) throws IOException {
			long begin = input.position();
			if (input.get() != Tagger.COMMENT_SYMBOL) {
				return null;
			}
			TagNode node = this.newNode(begin, input.pushMark()); // 1
			node.addNode(this.parse(RAW_TAG, input));
			if (input.get() != Tagger.BLOCK_BRA) {
				input.goMark().popMark(); // 0
				return null;
			}
			node.addNode(this.parse(RAW_TAG, input));
			int ch = input.get();
			while (ch != Tagger.END_OF_INPUT) {
				switch (ch) {
				case Tagger.BLOCK_KET:
					node.addNode(this.parse(RAW_TAG, input));
					ch = input.get();
					if (ch == Tagger.COMMENT_SYMBOL) {
						node.addNode(this.parse(RAW_TAG, input));
						input.popMark(); // 0
						return node;
					}
				break;
				case Tagger.COMMENT_SYMBOL:
					ch = input.pushMark().next().get(); // 2
					if (ch == Tagger.COMMENT_BRA) {
						input.goMark().popMark(); // 1
						TagNode child = this.parse(BLOCK_COMMENT_TAG, input);
						if (child != null) {
							node.addNode(child);
						} else {
							input.popMark(); // 0
							return node;
						}
					} else {
						input.popMark(); // 1
					}
				break;
				case Tagger.ESCAPE_SYMBOL:
					node.addNode(this.parse(RAW_TAG, input));
					if (input.get() == Tagger.END_OF_INPUT) {
						input.popMark(); // 0
						return node;
					}
					node.addNode(this.parse(RAW_TAG, input));
				break;
				default:
					node.addNode(this.parse(RAW_TAG, input));
				break;
				}
				ch = input.get();
			}
			return null;
		}
	}

	static abstract class OperatorInfo {
		static final int RIGHT_INFIX_TYPE = 0;
		static final int LEFT_INFIX_TYPE = RIGHT_INFIX_TYPE + 1;
		static final int PREFIX_TYPE = LEFT_INFIX_TYPE + 1;
		static final int POSTFIX_TYPE = PREFIX_TYPE + 1;

		static final int MIN_INFIX_ORDER = 0;
		static final int MAX_INFIX_ORDER = MIN_INFIX_ORDER + 1000;
		static final int PREFIX_ORDER = MAX_INFIX_ORDER + 1;
		static final int POSTFIX_ORDER = PREFIX_ORDER + 1;

		static String typeName(OperatorInfo info) {
			if (info == null) {
				return null;
			}
			return typeName(info.type());
		}
		static String typeName(int type) {
			switch (type) {
			case RIGHT_INFIX_TYPE:
				return "RightInfix";
			case LEFT_INFIX_TYPE:
				return "LeftInfix";
			case PREFIX_TYPE:
				return "Prefix";
			case POSTFIX_TYPE:
				return "Postfix";
			default:
				return "Unknown";
			}
		}

		static abstract class InfixPrecedance extends OperatorInfo {
			final int order;

			InfixPrecedance(String expr, int order) {
				super(expr);
				if (order < MIN_INFIX_ORDER || MAX_INFIX_ORDER < order) {
					String msg = "infix operator's order must been within ["
							+ MIN_INFIX_ORDER + ", " + MAX_INFIX_ORDER + "], but specified="
							+ order;
					throw new IllegalArgumentException(msg);
				}
				this.order = order;
			}
			@Override
			int order() {
				return this.order;
			}
		}

		static class LefInfix extends InfixPrecedance {
			LefInfix(String expr, int order) {
				super(expr, order);
			}
			@Override
			int type() {
				return OperatorInfo.LEFT_INFIX_TYPE;
			}
		}

		static class RightInfix extends InfixPrecedance {
			RightInfix(String expr, int order) {
				super(expr, order);
			}
			@Override
			int type() {
				return OperatorInfo.RIGHT_INFIX_TYPE;
			}
		}

		static class Prefix extends OperatorInfo {
			Prefix(String expr) {
				super(expr);
			}
			@Override
			int type() {
				return OperatorInfo.PREFIX_TYPE;
			}
			@Override
			int order() {
				return OperatorInfo.PREFIX_ORDER;
			}
		}

		static class Postfix extends OperatorInfo {
			Postfix(String expr) {
				super(expr);
			}
			@Override
			int type() {
				return OperatorInfo.POSTFIX_TYPE;
			}
			@Override
			int order() {
				return OperatorInfo.POSTFIX_ORDER;
			}
		}

		static OperatorInfo left(String expr, int order) {
			return new LefInfix(expr, order);
		}
		static OperatorInfo right(String expr, int order) {
			return new RightInfix(expr, order);
		}
		static OperatorInfo pre(String expr) {
			return new Prefix(expr);
		}
		static OperatorInfo post(String expr) {
			return new Postfix(expr);
		}

		public static TagNode parse(TagNode node) throws IOException {
			TagNode root = node.clone().clearNode();
			List<TagNode> vStack = new ArrayList<TagNode>();
			List<TagNode> oStack = new ArrayList<TagNode>();
			return parse(root, null, vStack, oStack, node);
		}
		private static TagNode parse(TagNode output, TagNode eq, List<TagNode> vas,
				List<TagNode> ops, TagNode input) throws IOException {
			if (input == null) {
				return output;
			}
			switch (input.name()) {
			case Tagger.EQUATION_TAG:
				if (eq == null) {
					eq = input.clone().clearNode();
					int nvas = vas.size();
					int nops = ops.size();
					parse(output, eq, vas, ops, input.firstNode());
					while (nops < ops.size()) {
						apply(vas, ops.remove(ops.size() - 1));
					}
					if (vas.size() != nvas + 1) {
						throw new IOException("unexpected number of equations="
								+ (vas.size() - nvas));
					}
					eq.addNode(vas.remove(vas.size() - 1));
					output.addNode(eq);
					eq = null;
				} else {
					ops.add(null);
					int n = ops.size();
					parse(output, eq, vas, ops, input.firstNode());
					while (n < ops.size()) {
						TagNode op = ops.remove(ops.size() - 1);
						apply(vas, op);
					}
					ops.remove(ops.size() - 1);
				}
				parse(output, eq, vas, ops, input.nextNode());
			break;
			case Tagger.PREFIX_OPERATOR_TAG:
			case Tagger.POSTFIX_OPERATOR_TAG:
			case Tagger.INFIX_OPERATOR_TAG:
			case Tagger.EMPTY_INFIX_OPERATOR_TAG: {
				OperatorInfo info = input.getOperatorInfo();
				if (info == null) {
					throw new IOException("unknown operator=" + input);
				}
				int n = ops.size();
				LOOP: while (0 < n--) {
					TagNode op = ops.get(n);
					if (op == null) {
						break;
					} else {
						switch (info.type()) {
						case OperatorInfo.RIGHT_INFIX_TYPE:
						case OperatorInfo.PREFIX_TYPE:
							if (op.getOperatorInfo().order() <= info.order()) {
								break LOOP;
							}
						break;
						case OperatorInfo.LEFT_INFIX_TYPE:
						case OperatorInfo.POSTFIX_TYPE:
							if (op.getOperatorInfo().order() < info.order()) {
								break LOOP;
							}
						break;
						default:
							throw new IOException("unknown type of operator=" + info);
						}
					}
					apply(vas, ops.remove(n));
				}
				ops.add(input.clone().clearNode());
				parse(output, eq, vas, ops, input.nextNode());
			}
			break;
			case Tagger.VARIABLE_TAG:
			case Tagger.EMPTY_VARIABLE_TAG:
			case Tagger.NUMBER_TAG:
			case Tagger.CHARACTER_TAG:
			case Tagger.STRING_TAG:
				vas.add(input.clone().clearNode());
				parse(output, eq, vas, ops, input.nextNode());
			break;
			default:
				parse(output, eq, vas, ops, input.firstNode());
				parse(output, eq, vas, ops, input.nextNode());
			break;
			}
			return output;
		}

		private static void apply(List<TagNode> vas, TagNode op) throws IOException {
			int n = vas.size();
			switch (op.name()) {
			case Tagger.PREFIX_OPERATOR_TAG:
			case Tagger.POSTFIX_OPERATOR_TAG:
				if (n < 1) {
					throw new IOException("too few to apply prefix/postfix operator="
							+ op);
				}
				op.addNode(vas.remove(--n));
				vas.add(op);
			break;
			case Tagger.INFIX_OPERATOR_TAG:
			case Tagger.EMPTY_INFIX_OPERATOR_TAG:
				if (n < 2) {
					throw new IOException("too few to apply infix operator=" + op);
				} else {
					TagNode v_2 = vas.remove(--n);
					TagNode v_1 = vas.remove(--n);
					op.addNode(v_1).addNode(v_2);
					vas.add(op);
					Debug.log().debug(
							op + " (" + op.firstNode() + ", " + op.firstNode().nextNode()
									+ ")");
				}
			break;
			default:
				throw new IOException("unknown operator=" + Tagger.tagName(op));
			}
		}

		final String expression;

		OperatorInfo(String expr) {
			this.expression = expr;
		}
		@Override
		public String toString() {
			return typeName(this) + " {order=" + this.order() + "}";
		}
		abstract int type();
		abstract int order();
	}

	public void testPrecedance() throws IOException {
		File file = new File("data/SimpleTest_2.tiny");
		ArrayInput input = ArrayInput.readUTF8(file);
		Parser parser = new Parser();
		if (true) {
			parser.setNodeType(Tagger.NONE_NODE, //
					Tagger.RAW_TAG, //
					Tagger.WHITE_BLCOK_TAG, //
					Tagger.LINE_COMMENT_TAG, //
					Tagger.BLOCK_COMMENT_TAG, //
					Tagger.IGNORABLE_BLOCK_TAG //
					);
			parser.setNodeType(Tagger.ELIMINATABLE_NODE //
					, Tagger.EQUATION_LIST_TAG //
					, Tagger.MULTITIVE_EQUATON_TAG //
					, Tagger.PRIMARY_EQUATION_TAG //
					);
			parser.setNodeType(Tagger.OPERATOR_NODE //
					, Tagger.PREFIX_OPERATOR_TAG //
					, Tagger.POSTFIX_OPERATOR_TAG //
					, Tagger.INFIX_OPERATOR_TAG //
					, Tagger.EMPTY_INFIX_OPERATOR_TAG);
		}
		OperatorInfo[] ops = { OperatorInfo.post("+") //
				, OperatorInfo.left("^", 100) //
				, OperatorInfo.left("", 80) //
				, OperatorInfo.left("..", 60) //
				, OperatorInfo.left("/", 40) //
				, OperatorInfo.right("=", 20) //
		};
		Map<String, OperatorInfo> opMap = new HashMap<String, OperatorInfo>();
		for (int ii = 0, nn = ops.length; ii < nn; ++ii) {
			opMap.put(ops[ii].expression, ops[ii]);
		}
		parser.setOperatorInfoMap(opMap);

		parser.parse(Tagger.IGNORABLE_BLOCK_TAG, input);
		TagNode node = parser.parse(Tagger.DOCUMENT_TAG, input);
		if (node == null) {
			Debug.log().debug("failed to parse at=" + input.position);
			return;
		}

		TagNode eqs = OperatorInfo.parse(node);
		PrintWriter writer = new PrintWriter(System.out);
		dumpNode(writer, input.array(), eqs, 0);
		writer.flush();
	}

	private static String toString(ByteArrayList list)
			throws UnsupportedEncodingException {
		return toString(list.getArray(), 0, list.size());
	}
	private static String toString(byte[] array, int begin, int size)
			throws UnsupportedEncodingException {
		String x = new String(array, begin, size, FileHelper.UTF_8);
		x = x.replaceAll("\r", "\\\\r");
		x = x.replaceAll("\n", "\\\\n");
		x = x.replaceAll("\t", "\\\\t");
		x = x.replaceAll("\b", "\\\\b");
		x = x.replaceAll("\f", "\\\\f");
		return x;
	}

	public void testDocument() throws IOException {
		File file = new File("data/SimpleTest_2.tiny");
		ArrayInput input = ArrayInput.readUTF8(file);
		Parser parser = new Parser();
		if (true) {
			parser.setNodeType(Tagger.NONE_NODE, //
					Tagger.RAW_TAG, //
					Tagger.WHITE_BLCOK_TAG, //
					Tagger.LINE_COMMENT_TAG, //
					Tagger.BLOCK_COMMENT_TAG, //
					Tagger.IGNORABLE_BLOCK_TAG //
					);
			parser.setNodeType(Tagger.ELIMINATABLE_NODE, //
					Tagger.EQUATION_LIST_TAG, //
					Tagger.MULTITIVE_EQUATON_TAG, //
					Tagger.PRIMARY_EQUATION_TAG);
		}
		parser.parse(Tagger.IGNORABLE_BLOCK_TAG, input);
		TagNode node = parser.parse(Tagger.DOCUMENT_TAG, input);
		if (node != null) {
			Debug.log().debug(
					"successed parse until=" + input.position + ", depth=" + depth(node)
							+ ", input-stack=" + input.positions.size()
							+ ", max-backtrace=" + input.maxBacktrace + ", count-backtrace="
							+ input.countBacktrace + ", max-lookahead=" + input.maxLookahead);
			File out = new File("data/dump.txt");
			dumpNode(out, input.array, input.position, node);
			Debug.log().debug("wrote=" + out.getAbsolutePath());
		} else {
			Debug.log().debug("failed to parse at=" + input.position);
		}
	}
	public void testEquationList() throws IOException {
		File file = new File("data/SimpleTest_2.tiny");
		ArrayInput input = ArrayInput.readUTF8(file);
		Parser parser = new Parser();
		parser.setNodeType(Tagger.NONE_NODE, Tagger.RAW_TAG,
				Tagger.IGNORABLE_BLOCK_TAG);
		parser.setNodeType(Tagger.ELIMINATABLE_NODE, Tagger.MULTITIVE_EQUATON_TAG,
				Tagger.PRIMARY_EQUATION_TAG);
		parser.parse(Tagger.IGNORABLE_BLOCK_TAG, input);
		TagNode node = parser.parse(Tagger.EQUATION_LIST_TAG, input);
		if (node != null) {
			Debug.log().debug(
					"successed parse until=" + input.position + ", depth=" + depth(node)
							+ ", input-stack=" + input.positions.size());
			File out = new File("data/dump.txt");
			dumpNode(out, input.array, input.position, node);
			Debug.log().debug("wrote=" + out.getAbsolutePath());
		} else {
			Debug.log().debug("failed to parse at=" + input.position);
		}
	}
	public void testEquation() throws IOException {
		File file = new File("data/SimpleTest_2.tiny");
		ArrayInput input = ArrayInput.readUTF8(file);
		Parser parser = new Parser();
		parser.setNodeType(Tagger.NONE_NODE, Tagger.RAW_TAG,
				Tagger.IGNORABLE_BLOCK_TAG);
		parser.setNodeType(Tagger.ELIMINATABLE_NODE, Tagger.MULTITIVE_EQUATON_TAG,
				Tagger.PRIMARY_EQUATION_TAG);
		parser.parse(Tagger.IGNORABLE_BLOCK_TAG, input);
		TagNode node = parser.parse(Tagger.EQUATION_TAG, input);
		if (node != null) {
			Debug.log().debug(
					"successed parse until=" + input.position + ", depth=" + depth(node)
							+ ", input-stack=" + input.positions.size());
			File out = new File("data/dump.txt");
			dumpNode(out, input.array, input.position, node);
			Debug.log().debug("wrote=" + out.getAbsolutePath());
		} else {
			Debug.log().debug("failed to parse at=" + input.position);
		}
	}
	private static int depth(TagNode node) {
		if (node == null) {
			return 0;
		}
		return depth(node, 1);
	}
	private static int depth(TagNode node, int depth) {
		TagNode child = node.firstNode();
		int max = depth;
		while (child != null) {
			max = Math.max(max, depth(child, depth + 1));
			child = child.nextNode();
		}
		return max;
	}
	public void testIgnorable() throws IOException {
		File file = new File("data/SimpleTest_2.tiny");
		ArrayInput input = ArrayInput.readUTF8(file);
		Parser parser = new Parser();
		parser.setNodeType(Tagger.NONE_NODE, Tagger.RAW_TAG);
		TagNode node = parser.parse(Tagger.IGNORABLE_BLOCK_TAG, input);
		if (node != null) {
			Debug.log().debug("successed parse until=" + input.position);
			dumpNode(input.array, input.position, node);
		} else {
			Debug.log().debug("failed to parse at=" + input.position);
		}
	}

	public void testComment() throws IOException {
		File file = new File("data/SimpleTest_2.tiny");
		ArrayInput input = ArrayInput.readUTF8(file);
		Parser parser = new Parser();
		TagNode node = parser.parse(Tagger.COMMENT_TAG, input);
		if (node != null) {
			Debug.log().debug("successed parse until=" + input.position);
			dumpNode(input.array, input.position, node);
		} else {
			Debug.log().debug("failed to parse at=" + input.position);
		}
	}

	static void dumpNode(File file, byte[] array, int end, TagNode node)
			throws IOException {
		Writer writer = null;
		try {
			writer = FileHelper.getWriter(file, FileHelper.UTF_8);
			dumpNode(writer, array, node, 0);
		} finally {
			FileHelper.close(writer);
		}
	}
	static void dumpNode(byte[] array, int end, TagNode node) {
		try {
			PrintWriter writer = new PrintWriter(System.out);
			dumpNode(writer, array, node, 0);
			writer.flush();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	static Writer dumpNode(Writer output, byte[] array, TagNode node, int depth)
			throws IOException {
		String name = Tagger.tagName(node);
		for (int n = depth; 0 < n--;) {
			output.append("\t");
		}
		int start = (int) node.begin();
		int end = (int) node.end();
		output.append(name).append(" [").append(Integer.toString(start));
		output.append(", ").append(Integer.toString(end)).append("]");
		switch (node.name()) {
		case Tagger.RAW_TAG:
		case Tagger.VARIABLE_TAG:
		case Tagger.PREFIX_OPERATOR_TAG:
		case Tagger.INFIX_OPERATOR_TAG:
		case Tagger.POSTFIX_OPERATOR_TAG:
		case Tagger.CHARACTER_TAG:
		case Tagger.STRING_TAG:
		case Tagger.NUMBER_TAG:
			output.append(" ");
			output.append(toString(array, start, end - start));
		break;
		default:
		break;
		}
		TagNode child = node.firstNode();
		if (child != null) {
			output.append(" {\n");
			while (child != null) {
				dumpNode(output, array, child, depth + 1);
				output.flush();
				child = child.nextNode();
			}
			repeat(output, "\t", depth);
			return output.append("}\n");
		} else {
			return output.append("\n");
		}
	}
	static String repeat(String text, int n) {
		StringBuilder buffer = new StringBuilder();
		try {
			repeat(buffer, text, n);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return buffer.toString();
	}
	static Appendable repeat(Appendable output, String text, int n)
			throws IOException {
		while (0 < n--) {
			output.append(text);
		}
		return output;
	}

	public void testReadLine() throws IOException {
		File file = new File("data/SimpleTest_2.tiny");
		FileChannel input = null;
		try {
			input = new FileInputStream(file).getChannel();
			long size = input.size();
			Assert.assertTrue("too big file=" + file, size < Integer.MAX_VALUE);
			byte[] array = new byte[4];
			ByteBuffer buffer = ByteBuffer.wrap(array);
			ByteArrayList line = new ByteArrayList(1024);
			int n = input.read(buffer);
			boolean first = true;
			while (0 < n) {
				for (int i = 0; i < n; ++i) {
					byte ch = array[i];
					switch (ch) {
					case AsciiHelper.NEW_LINE:
						if (0 < line.size()) {
							Debug.log().debug(toString(line));
						}
						line.removeAll();
					break;
					case AsciiHelper.CARRIAGE_RETURN:
					break;
					case AsciiHelper.SPACE:
					case AsciiHelper.HORIZONTAL_TAB:
						if (first) {
						} else {
							line.push(ch);
						}
					break;
					default:
						first = false;
						line.push(ch);
					break;
					}
				}
				buffer.rewind();
				n = input.read(buffer);
			}
			if (0 < line.size()) {
				Debug.log().debug(toString(line));
			}
		} finally {
			FileHelper.close(input);
		}
	}
	public void testReadAll() throws IOException {
		File file = new File("data/SimpleTest_2.tiny");
		FileChannel input = null;
		try {
			input = new FileInputStream(file).getChannel();
			long size = input.size();
			Assert.assertTrue("too big file=" + file, size < Integer.MAX_VALUE);
			byte[] array = new byte[(int) size];
			ByteBuffer buffer = ByteBuffer.wrap(array);
			int total = 0;
			int n = input.read(buffer);
			while (0 < n) {
				total += n;
				buffer.rewind();
				n = input.read(buffer);
			}
			Assert.assertEquals("", size, total);
		} finally {
			FileHelper.close(input);
		}
	}
	public void testAscii() {
		for (byte i = 0, n = Byte.MAX_VALUE; i < n; ++i) {
			System.out.println(i + "=" + (char) i);
		}
	}
}
