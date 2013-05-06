package parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.NoSuchElementException;

import junit.framework.Assert;
import junit.framework.TestCase;
import tiny.lang.ArrayHelper;
import tiny.lang.Debug;
import tiny.lang.FileHelper;
import tiny.primitive.ByteArrayList;
import tiny.primitive.IntArrayList;

public class SimpleTest_2 extends TestCase {
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
				this.positions = new IntArrayList(8);
			}
			return this.positions;
		}
		@Override
		public ByteInput pushMark() {
			this.getPositions(true).push(this.position);
			Debug.log().debug(this.getPositions(true).getLength());
			return this;
		}
		@Override
		public ByteInput popMark() {
			IntArrayList list = this.getPositions(false);
			if (list == null || list.getLength() < 1) {
				String msg = "there is not stored position";
				throw new NoSuchElementException(msg);
			}
			list.pop();
			return this;
		}
		@Override
		public ByteInput setMark() {
			IntArrayList list = this.getPositions(false);
			if (list == null || list.getLength() < 1) {
				String msg = "there is not stored position";
				throw new NoSuchElementException(msg);
			}
			list.getArray()[list.getLength() - 1] = this.position;
			return this;
		}
		@Override
		public ByteInput goMark() {
			IntArrayList list = this.getPositions(false);
			if (list == null || list.getLength() < 1) {
				String msg = "there is not stored position";
				throw new NoSuchElementException(msg);
			}
			this.position = list.top(this.position);
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
				return this.array[this.position] & 0xff;
			}
			return ByteInput.END_OF_INPUT;
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
		@SuppressWarnings("unchecked")
		T addNode(T child) {
			if (child == null) {
			} else if (this.lastNode == null) {
				this.firstNode = this.lastNode = child;
			} else {
				this.lastNode = this.lastNode.nextNode = child;
			}
			return (T) this;
		}
	}

	static class TagNode extends Node<TagNode> {
		static final TagNode DUMMY_NODE = new TagNode(Tagger.RAW_TAG,
				Tagger.END_OF_INPUT);
		final int name;
		final long begin;

		TagNode(int name, long begin) {
			this.name = name;
			this.begin = begin;
		}
		int name() {
			return this.name;
		}
		long begin() {
			return this.begin;
		}
		long end(long def) {
			TagNode next = this.nextNode();
			if (next != null) {
				return next.begin();
			}
			return def;
		}
		@Override
		TagNode addNode(TagNode child) {
			if (child == TagNode.DUMMY_NODE) {
				return this;
			}
			return super.addNode(child);
		}
	}

	static class EmptyTagNode extends TagNode {
		EmptyTagNode(int name, long begin) {
			super(name, begin);
		}
		long end(long def) {
			return this.end();
		}
		long end() {
			return this.begin();
		}
	}

	static class RangedTagNode extends TagNode {
		final long end;

		RangedTagNode(int name, long begin, long end) {
			super(name, begin);
			this.end = end;
		}
		long end(long def) {
			return this.end();
		}
		long end() {
			return this.end;
		}
	}

	static class FlattenTagNode extends TagNode {
		FlattenTagNode(int name, long begin) {
			super(name, begin);
		}
		@Override
		FlattenTagNode addNode(TagNode child) {
			if (child == null) {
				return this;
			} else if (child.firstNode() != null) {
				return this.addSibling(child.firstNode());
			}
			return (FlattenTagNode) super.addNode(child);
		}
		private FlattenTagNode addSibling(TagNode node) {
			while (node != null) {
				super.addNode(node);
				node = node.nextNode();
			}
			return this;
		}
	}

	static class Parser {
		Tagger[] taggers = new Tagger[Tagger.TAG_SIZE];
		private boolean rangeVariable;
		private boolean skipIgnorable;
		public boolean flattenEquation;

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
			case Tagger.PAIIRED_EQUATION_TAG:
				return new EquationList(this);
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
		public TagNode newTagNode(int name, long begin, long end) {
			switch (name) {
			case Tagger.VARIABLE_TAG:
			case Tagger.CHARACTER_TAG:
			case Tagger.STRING_TAG:
			case Tagger.INFIX_OPERATOR_TAG:
			case Tagger.PREFIX_OPERATOR_TAG:
			case Tagger.POSTFIX_OPERATOR_TAG:
				if (this.rangeVariable) {
					return new RangedTagNode(name, begin, end);
				}
			break;
			case Tagger.IGNORABLE_BLOCK_TAG:
			case Tagger.WHITE_BLCOK_TAG:
			case Tagger.LINE_COMMENT_TAG:
			case Tagger.BLOCK_COMMENT_TAG:
				if (this.skipIgnorable) {
					return TagNode.DUMMY_NODE;
				}
			break;
			case Tagger.EQUATION_TAG:
			case Tagger.MULTITIVE_EQUATON_TAG:
			case Tagger.PRIMARY_EQUATION_TAG:
				if (this.flattenEquation) {
					return new FlattenTagNode(name, begin);
				}
			break;
			case Tagger.EMPTY_INFIX_OPERATOR_TAG:
			case Tagger.EMPTY_VARIABLE_TAG:
				return new EmptyTagNode(name, begin);
			default:
			break;
			}
			return new TagNode(name, begin);
		}
		public TagNode parse(int name, ByteInput input) throws IOException {
			return this.getTagger(name, input.position()).parse(input);
		}
	}

	static interface Tagger {
		int RAW_TAG = 0;
		int COMMENT_TAG = RAW_TAG + 1;
		int LINE_COMMENT_TAG = COMMENT_TAG + 1;
		int BLOCK_COMMENT_TAG = LINE_COMMENT_TAG + 1;
		int WHITE_BLCOK_TAG = BLOCK_COMMENT_TAG + 1;
		int IGNORABLE_BLOCK_TAG = WHITE_BLCOK_TAG + 1;
		int PAIIRED_EQUATION_TAG = IGNORABLE_BLOCK_TAG + 1;
		int EQUATION_TAG = PAIIRED_EQUATION_TAG + 1;
		int MULTITIVE_EQUATON_TAG = EQUATION_TAG + 1;
		int PRIMARY_EQUATION_TAG = MULTITIVE_EQUATON_TAG + 1;
		int VARIABLE_TAG = PRIMARY_EQUATION_TAG + 1;
		int EMPTY_VARIABLE_TAG = VARIABLE_TAG + 1;
		int NUMBER_TAG = EMPTY_VARIABLE_TAG + 1;
		int CHARACTER_TAG = NUMBER_TAG + 1;
		int STRING_TAG = CHARACTER_TAG + 1;
		int INFIX_OPERATOR_TAG = STRING_TAG + 1;
		int EMPTY_INFIX_OPERATOR_TAG = INFIX_OPERATOR_TAG + 1;
		int PREFIX_OPERATOR_TAG = EMPTY_INFIX_OPERATOR_TAG + 1;
		int POSTFIX_OPERATOR_TAG = PREFIX_OPERATOR_TAG + 1;
		int TAG_SIZE = POSTFIX_OPERATOR_TAG + 1;

		int END_OF_INPUT = ByteInput.END_OF_INPUT;

		byte EOL = AsciiHelper.NEW_LINE;
		byte COMMENT_SYMBOL = AsciiHelper.SHARP;
		byte COMMENT_BRA = AsciiHelper.CURLY_BRA;
		byte COMMENT_KET = AsciiHelper.CURLY_KET;
		byte COMMENT_ESCAPE = AsciiHelper.BACK_SOLIDUS;
		byte BLOCK_BRA = AsciiHelper.CURLY_BRA;
		byte BLOCK_KET = AsciiHelper.CURLY_KET;
		byte ESCAPE_SYMBOL = AsciiHelper.BACK_SOLIDUS;
		byte EQUATION_STOP = AsciiHelper.SEMICOLON;
		byte EQUATION_BRA = AsciiHelper.ROUND_BRA;
		byte EQUATION_KET = AsciiHelper.ROUND_KET;
		byte CHARACTER_BRA = AsciiHelper.SINGLE_QUOTE;
		byte CHARACTER_KET = CHARACTER_BRA;
		byte STRING_BRA = AsciiHelper.DOUBLE_QUOTE;
		byte STRING_KET = STRING_BRA;
		byte ESCAPED_BACK_SPACE = AsciiHelper.A + ('b' - 'a');
		byte ESCAPED_HORIZONTAL_TAB = AsciiHelper.A + ('t' - 'a');
		byte ESCAPED_NEW_LINE = AsciiHelper.A + ('n' - 'a');
		byte ESCAPED_FORM_FEED = AsciiHelper.A + ('f' - 'a');
		byte ESCAPED_CARRIAGE_RETURN = AsciiHelper.A + ('r' - 'a');
		byte ESCAPED_HEX_DIGIT = AsciiHelper.A;

		TagNode parse(ByteInput input) throws IOException;
	}

	static abstract class AbTagger implements Tagger {
		public static String tagName(TagNode node) {
			if (node == null) {
				return "null";
			}
			return AbTagger.tagName(node.name());
		}
		public static String tagName(int name) {
			switch (name) {
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
				return "Unknown";
			}
		}

		final Parser parser;

		AbTagger(Parser parser) {
			this.parser = parser;
		}
		Parser getParser() {
			return this.parser;
		}
		TagNode newTag(ByteInput input, long begin) {
			return this.newTag(input, begin, input.position());
		}
		TagNode newTag(ByteInput input, long begin, long end) {
			return this.getParser().newTagNode(this.tagName(), begin, end);
		}
		TagNode parse(int name, ByteInput input) throws IOException {
			return this.getParser().parse(name, input);
//			ArrayInput x = (ArrayInput) input;
//			long n = x.getPositions(true).getLength();
//			TagNode node = this.getParser().parse(name, input);
//			n = x.getPositions(true).getLength() - n;
//			if (n != 0) {
//				Debug.log().debug("name=" + AbTagger.tagName(name) + ": delta=" + n);
//			}
//			return node;
		}

		abstract int tagName();
	}

	static class Raw extends AbTagger {
		Raw(Parser parser) {
			super(parser);
		}
		@Override
		int tagName() {
			return Tagger.RAW_TAG;
		}
		@Override
		public TagNode parse(ByteInput input) throws IOException {
			int ch = input.get();
			if (ch != Tagger.END_OF_INPUT) {
				long begin = input.position();
				return this.newTag(input.next(), begin);
			}
			return null;
		}
	}

	static class EquationList extends AbTagger {
		EquationList(Parser parser) {
			super(parser);
		}
		@Override
		int tagName() {
			return Tagger.PAIIRED_EQUATION_TAG;
		}
		@Override
		public TagNode parse(ByteInput input) throws IOException {
			return null;
		}
	}

	static class Equation extends AbTagger {
		Equation(Parser parser) {
			super(parser);
		}
		@Override
		int tagName() {
			return Tagger.EQUATION_TAG;
		}
		@Override
		public TagNode parse(ByteInput input) throws IOException {
			long begin = input.position();
			TagNode child = this.parse(Tagger.MULTITIVE_EQUATON_TAG, input);
			if (child != null) {
				return this.newTag(input, begin).addNode(child);
			}
			return null;
		}
	}

	static class MultitiveEquation extends AbTagger {
		MultitiveEquation(Parser parser) {
			super(parser);
		}
		@Override
		int tagName() {
			return Tagger.MULTITIVE_EQUATON_TAG;
		}
		@Override
		public TagNode parse(ByteInput input) throws IOException {
			long begin = input.position();
			TagNode var_1 = this.parse(PRIMARY_EQUATION_TAG, input);
			if (var_1 != null) {
				input.pushMark();
				TagNode ig_1 = this.parse(IGNORABLE_BLOCK_TAG, input);
				TagNode op = this.parse(INFIX_OPERATOR_TAG, input);
				TagNode ig_2 = null;
				if (op != null) {
					ig_2 = this.parse(IGNORABLE_BLOCK_TAG, input);
				} else {
					op = this.parse(EMPTY_INFIX_OPERATOR_TAG, input);
				}
				TagNode var_2 = this.parse(MULTITIVE_EQUATON_TAG, input);
				if (var_2 != null) {
					TagNode node = this.newTag(input, begin);
					node.addNode(var_1);
					if (ig_1 != null) {
						node.addNode(ig_1);
					}
					node.addNode(op);
					if (ig_2 != null) {
						node.addNode(ig_2);
					}
					input.popMark();
					return node.addNode(var_2);
				} else {
					input.goMark().popMark();
					return this.newTag(input, begin).addNode(var_1);
				}
			}
			return null;
		}
	}

	static class PrimaryEquation extends AbTagger {
		PrimaryEquation(Parser parser) {
			super(parser);
		}
		@Override
		int tagName() {
			return Tagger.PRIMARY_EQUATION_TAG;
		}
		@Override
		public TagNode parse(ByteInput input) throws IOException {
			long begin = input.pushMark().position();
			TagNode child = this.parseBraket(input);
			if (child != null) {
				return this.newTag(input.popMark(), begin).addNode(child);
			}
			child = this.parse(CHARACTER_TAG, input.goMark());
			if (child != null) {
				return this.newTag(input.popMark(), begin).addNode(child);
			}
			child = this.parse(STRING_TAG, input.goMark());
			if (child != null) {
				return this.newTag(input.popMark(), begin).addNode(child);
			}
			child = this.parse(NUMBER_TAG, input.goMark());
			if (child != null) {
				return this.newTag(input.popMark(), begin).addNode(child);
			}
			child = this.parse(VARIABLE_TAG, input.goMark());
			if (child != null) {
				return this.newTag(input.popMark(), begin).addNode(child);
			}
			input.popMark();
			return null;
		}
		private TagNode parseBraket(ByteInput input) throws IOException {
			long begin = input.position();
			if (input.get() != Tagger.EQUATION_BRA) {
				return null;
			}
			TagNode ig_1 = this.parse(IGNORABLE_BLOCK_TAG, input.next().pushMark());
			if (ig_1 == null) {
				input.goMark();
			}
			TagNode var = this.parse(EQUATION_TAG, input);
			if (var != null) {
				TagNode ig_2 = this.parse(IGNORABLE_BLOCK_TAG, input.setMark());
				if (ig_2 == null) {
					input.goMark();
				}
				if (input.get() != Tagger.EQUATION_KET) {
					input.popMark();
					return null;
				}
				TagNode node = this.newTag(input.next(), begin);
				if (ig_1 != null) {
					node.addNode(ig_1);
				}
				node.addNode(var);
				if (ig_2 != null) {
					node.addNode(ig_2);
				}
				input.popMark();
				return node;
			}
			if (input.get() != Tagger.EQUATION_KET) {
				input.popMark();
				return null;
			}
			var = this.parse(EMPTY_VARIABLE_TAG, input);
			TagNode node = this.newTag(input.next().popMark(), begin);
			if (ig_1 != null) {
				node.addNode(ig_1);
			}
			node.addNode(var);
			return node;
		}
	}

	static class InfixOperator extends AbTagger {
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
				return this.newTag(input, begin);
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
			case AsciiHelper.SEMICOLON:
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
			return this.newTag(input, input.position());
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

	static class Variable extends AbTagger {
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
				return this.newTag(input, begin);
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
			return this.newTag(input, input.position());
		}
	}

	static class IgnorableBlock extends AbTagger {
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
			if (child != null) {
				TagNode node = this.newTag(input, begin);
				while (child != null) {
					node.addNode(child);
					child = this.parseChild(input);
				}
				return node;
			}
			return null;
		}
		private TagNode parseChild(ByteInput input) throws IOException {
			TagNode node = this.parse(WHITE_BLCOK_TAG, input.pushMark());
			if (node != null) {
				input.popMark();
				return node;
			}
			node = this.parse(LINE_COMMENT_TAG, input.goMark());
			if (node != null) {
				input.popMark();
				return node;
			}
			return this.parse(BLOCK_COMMENT_TAG, input.goMark().popMark());
		}
	}

	static class WhiteBlock extends AbTagger {
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
				return this.newTag(input, begin);
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

	static class NumberTagger extends AbTagger {
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
				return this.newTag(input, begin);
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

	static class CharacterTagger extends AbTagger {
		CharacterTagger(Parser parser) {
			super(parser);
		}
		@Override
		int tagName() {
			return Tagger.CHARACTER_TAG;
		}
		@Override
		public TagNode parse(ByteInput input) throws IOException {
			long begin = input.pushMark().position();
			if (this.next(input)) {
				return this.newTag(input.popMark(), begin);
			}
			input.goMark().popMark();
			return null;
		}
		boolean next(ByteInput input) throws IOException {
			if (input.get() == Tagger.CHARACTER_BRA) {
				if (this.nextCharacter(input.next())) {
					if (input.get() == Tagger.CHARACTER_KET) {
						input.next();
						return true;
					}
				}
			}
			return false;
		}
		boolean nextCharacter(ByteInput input) throws IOException {
			int ch = input.get();
			switch (ch) {
			case Tagger.CHARACTER_KET:
				return false;
			case Tagger.ESCAPE_SYMBOL:
				ch = input.next().get();
				switch (ch) {
				case Tagger.ESCAPED_BACK_SPACE:
				case Tagger.ESCAPED_HORIZONTAL_TAB:
				case Tagger.ESCAPED_NEW_LINE:
				case Tagger.ESCAPED_FORM_FEED:
				case Tagger.ESCAPED_CARRIAGE_RETURN:
				case Tagger.CHARACTER_KET:
				case Tagger.STRING_KET:
				break;
				case Tagger.ESCAPED_HEX_DIGIT:
					ch = input.next().get();
					if (!AsciiHelper.isHexDigit(ch)) {
						return false;
					}
					ch = input.next().get();
					if (!AsciiHelper.isHexDigit(ch)) {
						return false;
					}
				break;
				default:
					return false;
				}
			break;
			default:
				if (ch < 0 || Byte.MAX_VALUE < ch) {
					return false;
				}
			break;
			}
			input.next();
			return true;
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
		boolean next(ByteInput input) throws IOException {
			if (input.get() == Tagger.STRING_BRA) {
				int ch = input.next().get();
				while (ch != Tagger.END_OF_INPUT) {
					switch (ch) {
					case Tagger.STRING_KET:
						input.next();
						return true;
					case AsciiHelper.BACK_SPACE:
					case AsciiHelper.CARRIAGE_RETURN:
					case AsciiHelper.NEW_LINE:
					case AsciiHelper.FORM_FEED:
						return false;
					default:
						if (this.nextCharacter(input)) {
							ch = input.get();
						} else {
							return false;
						}
					break;
					}
				}
			}
			return false;
		}
	}

	static class Comment extends AbTagger {
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
				return this.newTag(input, child.begin()).addNode(child);
			}
			child = this.parse(BLOCK_COMMENT_TAG, input);
			if (child != null) {
				return this.newTag(input, child.begin()).addNode(child);
			}
			return null;
		}
	}

	static class LineComment extends AbTagger {
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
			if (input.get() == Tagger.COMMENT_SYMBOL) {
				if (input.next().get() == Tagger.COMMENT_SYMBOL) {
					int ch = input.next().get();
					while (ch != Tagger.EOL && ch != Tagger.END_OF_INPUT) {
						ch = input.next().get();
					}
					if (ch != Tagger.END_OF_INPUT) {
						input.next();
					}
					return this.newTag(input, begin);
				}
			}
			return null;
		}
	}

	static class BlockComment extends AbTagger {
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
			if (input.get() == Tagger.COMMENT_SYMBOL) {
				if (input.next().get() == Tagger.BLOCK_BRA) {
					TagNode node = this.newTag(input, begin);
					int ch = input.next().get();
					while (ch != Tagger.END_OF_INPUT) {
						switch (ch) {
						case Tagger.BLOCK_KET:
							ch = input.next().get();
							if (ch == Tagger.COMMENT_SYMBOL) {
								input.next();
								return node;
							}
						break;
						case Tagger.COMMENT_SYMBOL:
							ch = input.pushMark().next().get();
							if (ch == Tagger.COMMENT_BRA) {
								TagNode child = this.parse(BLOCK_COMMENT_TAG, input.goMark()
										.popMark());
								if (child != null) {
									node.addNode(child);
								} else {
									return null;
								}
							} else {
								input.popMark();
							}
						break;
						case Tagger.ESCAPE_SYMBOL:
							ch = input.next().get();
						break;
						default:
						break;
						}
						ch = input.next().get();
					}
				}
			}
			return null;
		}
	}

	private static String toString(ByteArrayList list)
			throws UnsupportedEncodingException {
		return toString(list.getArray(), 0, list.getLength());
	}
	private static String toString(byte[] array, int begin, int size)
			throws UnsupportedEncodingException {
		return new String(array, begin, size, FileHelper.UTF_8);
	}

	// public void testVariable() throws IOException {
	// File file = new File("data/SimpleTest_2.tiny");
	// ArrayInput input = ArrayInput.readUTF8(file);
	// Tagger tag = new IgnorableBlock();
	// TagNode node = tag.parse(input);
	// if (node != null) {
	// tag = new Variable();
	// node = tag.parse(input);
	// if (node != null) {
	// Debug.log().debug("successed parse until=" + input.position);
	// dumpNode(input.array, input.position, node);
	// } else {
	// Debug.log().debug("failed to parse at=" + input.position);
	// }
	// } else {
	// Debug.log().debug("failed to parse at=" + input.position);
	// }
	// }
	//
	public void testEquation() throws IOException {
		File file = new File("data/SimpleTest_2.tiny");
		ArrayInput input = ArrayInput.readUTF8(file);
		Parser parser = new Parser();
		parser.rangeVariable = true;
		// parser.skipIgnorable = true;
		// parser.flattenEquation = true;
		parser.parse(Tagger.IGNORABLE_BLOCK_TAG, input);
		TagNode node = parser.parse(Tagger.EQUATION_TAG, input);
		if (node != null) {
			Debug.log().debug(
					"successed parse until=" + input.position + ", depth=" + depth(node)
							+ ", input-stack=" + input.positions.getLength());
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
		while (child != null) {
			depth = Math.max(depth, depth(child, depth + 1));
			child = child.nextNode();
		}
		return depth;
	}
	public void testIgnorable() throws IOException {
		File file = new File("data/SimpleTest_2.tiny");
		ArrayInput input = ArrayInput.readUTF8(file);
		Parser parser = new Parser();
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
			dumpNode(writer, array, end, node, 0);
		} finally {
			FileHelper.close(writer);
		}
	}
	static void dumpNode(byte[] array, int end, TagNode node) {
		try {
			PrintWriter writer = new PrintWriter(System.out);
			dumpNode(writer, array, end, node, 0);
			writer.flush();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	static Writer dumpNode(Writer output, byte[] array, int end, TagNode node,
			int depth) throws IOException {
		String name = AbTagger.tagName(node);
		for (int n = depth; 0 < n--;) {
			output.append("\t");
		}
		int start = (int) node.begin();
		end = (int) node.end(end);
		output.append(name).append(" [").append(Integer.toString(start));
		output.append(", ").append(Integer.toString(end)).append("]");
		switch (node.name()) {
		case Tagger.VARIABLE_TAG:
		case Tagger.PREFIX_OPERATOR_TAG:
		case Tagger.INFIX_OPERATOR_TAG:
		case Tagger.POSTFIX_OPERATOR_TAG:
		case Tagger.CHARACTER_TAG:
		case Tagger.STRING_TAG:
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
				dumpNode(output, array, end, child, depth + 1);
				output.flush();
				child = child.nextNode();
			}
			for (int n = depth; 0 < n--;) {
				output.append("\t");
			}
			return output.append("}\n");
		} else {
			return output.append("\n");
		}
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
						if (0 < line.getLength()) {
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
			if (0 < line.getLength()) {
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
}
