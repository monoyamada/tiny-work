package study.automata;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import utils.lang.Messages;
import utils.lang.ObjectHelper;

public class ExpressionFactory {
	public static final int ZERO_NODE = 0;
	public static final int ONE_NODE = ZERO_NODE + 1;
	public static final int SYMBOL_NODE = ONE_NODE + 1;
	public static final int PLUS_NODE = SYMBOL_NODE + 1;
	public static final int MULTIPLIES_NODE = PLUS_NODE + 1;
	public static final int STAR_NODE = MULTIPLIES_NODE + 1;

	public static final String STAR_LITERAL = "*";
	public static final String PLUS_LITERAL = "p";
	public static final String MULTIPLIES_LITERAL = "m";

	public static void toString(Appendable output, Node node) throws IOException {
		assert output != null;
		if (node instanceof AbNode) {
			final AbNode x = (AbNode) node;
			x.toString(output);
		} else if (node != null) {
			output.append(node.toString());
		} else {
			output.append(null);
		}
	}

	public class ChildIterator implements Iterator<Node> {
		private final Node parent;
		private int index;

		public ChildIterator(Node parent) {
			assert parent != null;
			this.parent = parent;
		}
		public int getChildSize() {
			return this.parent.getChildSize();
		}
		public Node getChild(int index) {
			return this.parent.getChild(index);
		}
		public boolean hasNext() {
			return this.index < this.getChildSize();
		}
		public Node next() {
			if (this.parent.getChildSize() <= this.index) {
				String msg = Messages.getIndexOutOfRange(0, this.index, this
						.getChildSize());
				throw new IndexOutOfBoundsException(msg);
			}
			return this.getChild(this.index++);
		}
		public void remove() {
			String msg = Messages.getUnSupportedMethod(this.getClass(), "remove");
			throw new UnsupportedOperationException(msg);
		}
	}

	public interface Node extends Iterable<Node> {
		/**
		 * The place holder of the data to be used in analysis phase.
		 * 
		 * @return
		 */
		public Object getData();
		/**
		 * The place holder of the data to be used in analysis phase.
		 */
		public void setData(Object data);
		/**
		 * For fast casting.
		 * 
		 * @return
		 */
		public int getNodeType();
		/**
		 * This method dose not indicate strict equality but approximated equality.
		 * The default implementation is the followings: <code>
		 * return this == node;
		 * </code>
		 * 
		 * @param node
		 * @return
		 */
		public boolean equalNode(Node node);
		public boolean isIdempotentOne();
		public boolean isIdempotentSymbol(String value);
		public int getChildSize();
		public Node getChild(int index);
		public Node plus(Node node);
		public Node multiplies(Node node);
		public Node star();
	}

	public abstract class AbNode implements Node {
		private Object data;

		public Object getData() {
			return data;
		}
		public void setData(Object data) {
			this.data = data;
		}
		public String toString() {
			final StringBuilder buffer = new StringBuilder();
			try {
				this.toString(buffer);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			return buffer.toString();
		}
		protected void toString(Appendable output) throws IOException {
			output.append("(");
			for (int i = 0, n = this.getChildSize(); i < n; ++i) {
				if (i != 0) {
					output.append(", ");
				}
				ExpressionFactory.toString(output, this.getChild(i));
			}
			output.append(")");
		}
		public boolean equals(Object other) {
			if (this == other) {
				return true;
			}
			try {
				return this.equalNode((Node) other);
			} catch (Exception ex) {
			}
			return false;
		}
		public boolean equalNode(Node node) {
			return this == node;
		}
		public boolean isIdempotentOne() {
			return false;
		}
		public boolean isIdempotentSymbol(String value) {
			return false;
		}
		public Iterator<Node> iterator() {
			return new ChildIterator(this);
		}
		public Node getChild(int index) {
			if (index < 0 || this.getChildSize() <= index) {
				String msg = Messages.getIndexOutOfRange(0, index, this.getChildSize());
				throw new IndexOutOfBoundsException(msg);
			}
			return this.doGetChild(index);
		}
		public Node getZero() {
			return ExpressionFactory.this.getZero();
		}
		public Node getOne() {
			return ExpressionFactory.this.getOne();
		}
		protected Node newPlus(Node node) {
			return ExpressionFactory.this.newPlus(this, node);
		}
		protected Node newMultiplies(Node node) {
			return ExpressionFactory.this.newMultiplies(this, node);
		}
		protected Node newStar() {
			return ExpressionFactory.this.newStar(this);
		}
		public Node plus(Node node) {
			switch (node.getNodeType()) {
			case ExpressionFactory.ZERO_NODE:
				return this;
			default:
				break;
			}
			return this.newPlus(node);
		}
		public Node multiplies(Node node) {
			switch (node.getNodeType()) {
			case ExpressionFactory.ZERO_NODE:
				return node;
			case ExpressionFactory.ONE_NODE:
				return this;
			default:
				break;
			}
			return this.newMultiplies(node);
		}
		public Node star() {
			return this.newStar();
		}
		protected abstract Node doGetChild(int index);
	}

	public abstract class AbUnaryNode extends AbNode {
		private final Node child;

		protected AbUnaryNode(Node child) {
			assert child != null;
			this.child = child;
		}
		public int getChildSize() {
			return 1;
		}
		@Override
		protected Node doGetChild(int index) {
			return this.child;
		}
		public Node getChild() {
			return this.child;
		}
	}

	public abstract class AbBinaryNode extends AbNode {
		private final Node child0;
		private final Node child1;

		protected AbBinaryNode(Node child0, Node child1) {
			assert child0 != null;
			assert child1 != null;
			this.child0 = child0;
			this.child1 = child1;
		}
		public int getChildSize() {
			return 2;
		}
		@Override
		protected Node doGetChild(int index) {
			switch (index) {
			case 0:
				return this.child0;
			default:
				return this.child1;
			}
		}
		public Node getChild0() {
			return this.child0;
		}
		public Node getChild1() {
			return this.child1;
		}
	}

	public class ZeroNode extends AbNode {
		@Override
		protected void toString(Appendable output) throws IOException {
			output.append("0");
		}
		public boolean equalNode(Node node) {
			return node != null && this.getNodeType() == node.getNodeType();
		}
		public int getNodeType() {
			return ExpressionFactory.ZERO_NODE;
		}
		public boolean isIdempotentOne() {
			return false;
		}
		public int getChildSize() {
			return 0;
		}
		@Override
		protected Node doGetChild(int index) {
			return null;
		}
		@Override
		public Node plus(Node node) {
			return node;
		}
		@Override
		public Node multiplies(Node node) {
			return this;
		}
		@Override
		public Node star() {
			return this.getOne();
		}
	}

	public class OneNode extends ZeroNode {
		@Override
		protected void toString(Appendable output) throws IOException {
			output.append("1");
		}
		public int getNodeType() {
			return ExpressionFactory.ONE_NODE;
		}
		public boolean isIdempotentOne() {
			return true;
		}
		public Node plus(Node node) {
			switch (node.getNodeType()) {
			case ExpressionFactory.ZERO_NODE:
			case ExpressionFactory.ONE_NODE:
				return this;
			default:
				break;
			}
			return this.newPlus(node);
		}
		public Node multiplies(Node node) {
			return node;
		}
		public Node star() {
			return this;
		}
	}

	public class SymbolNode extends ZeroNode {
		private final String value;

		public SymbolNode(String value) {
			assert value != null && 0 < value.length();
			this.value = value;
		}
		@Override
		protected void toString(Appendable output) throws IOException {
			output.append(this.value);
		}
		public int hashCode() {
			return this.value != null ? this.value.hashCode() : 0;
		}
		public boolean equalNode(Node node) {
			if (node != null && this.getNodeType() == node.getNodeType()) {
				final SymbolNode symbol = (SymbolNode) node;
				return this.isIdempotentSymbol(symbol.value);
			}
			return false;
		}
		public int getNodeType() {
			return ExpressionFactory.SYMBOL_NODE;
		}
		@Override
		public boolean isIdempotentSymbol(String value) {
			return ObjectHelper.equals(this.value, value);
		}
		public String getValue() {
			return value;
		}
		public Node plus(Node node) {
			switch (node.getNodeType()) {
			case ExpressionFactory.ZERO_NODE:
				return this;
			case ExpressionFactory.SYMBOL_NODE:
				if (this.equals(node)) {
					return this;
				}
				break;
			default:
				break;
			}
			return this.newPlus(node);
		}
		@Override
		public Node multiplies(Node node) {
			switch (node.getNodeType()) {
			case ExpressionFactory.ZERO_NODE:
				return this.getZero();
			case ExpressionFactory.ONE_NODE:
				return this;
			default:
				break;
			}
			return this.newMultiplies(node);
		}
		@Override
		public Node star() {
			return this.newStar();
		}
	}

	public class StarNode extends AbUnaryNode {
		protected StarNode(Node child) {
			super(child);
		}
		@Override
		protected void toString(Appendable output) throws IOException {
			output.append(ExpressionFactory.STAR_LITERAL);
			output.append("(");
			ExpressionFactory.toString(output, this.getChild());
			output.append(")");
		}
		public int hashCode() {
			return this.getChild().hashCode() + this.getNodeType();
		}
		public boolean equalNode(Node node) {
			if (node != null && this.getNodeType() == node.getNodeType()) {
				final StarNode star = (StarNode) node;
				return this.getChild().equalNode(star.getChild());
			}
			return false;
		}
		public int getNodeType() {
			return ExpressionFactory.STAR_NODE;
		}
		@Override
		public boolean isIdempotentOne() {
			return true;
		}
		@Override
		public boolean isIdempotentSymbol(String value) {
			return this.getChild().isIdempotentSymbol(value);
		}
		public Node plus(Node node) {
			switch (node.getNodeType()) {
			case ExpressionFactory.ZERO_NODE:
			case ExpressionFactory.ONE_NODE:
				return this;
			default:
				break;
			}
			return this.newPlus(node);
		}
		public Node star() {
			return this;
		}
	}

	public class PlusNode extends AbBinaryNode {
		public PlusNode(Node child0, Node child1) {
			super(child0, child1);
		}
		@Override
		protected void toString(Appendable output) throws IOException {
			output.append(ExpressionFactory.PLUS_LITERAL);
			output.append("(");
			ExpressionFactory.toString(output, this.getChild0());
			output.append(", ");
			ExpressionFactory.toString(output, this.getChild1());
			output.append(")");
		}
		public int getNodeType() {
			return ExpressionFactory.PLUS_NODE;
		}
		@Override
		public boolean isIdempotentOne() {
			return this.getChild0().isIdempotentOne()
					|| this.getChild1().isIdempotentOne();
		}
		@Override
		public boolean isIdempotentSymbol(String value) {
			return this.getChild0().isIdempotentSymbol(value)
					|| this.getChild1().isIdempotentSymbol(value);
		}
		public Node plus(Node node) {
			switch (node.getNodeType()) {
			case ExpressionFactory.ZERO_NODE:
				return this;
			case ExpressionFactory.ONE_NODE:
				if (this.isIdempotentOne()) {
					return this;
				}
				break;
			default:
				break;
			}
			return this.newPlus(node);
		}
	}

	public class MultipliesNode extends AbBinaryNode {
		public MultipliesNode(Node child0, Node child1) {
			super(child0, child1);
		}
		@Override
		protected void toString(Appendable output) throws IOException {
			output.append(ExpressionFactory.MULTIPLIES_LITERAL);
			output.append("(");
			ExpressionFactory.toString(output, this.getChild0());
			output.append(", ");
			ExpressionFactory.toString(output, this.getChild1());
			output.append(")");
		}
		public int getNodeType() {
			return ExpressionFactory.MULTIPLIES_NODE;
		}
		@Override
		public boolean isIdempotentOne() {
			return this.getChild0().isIdempotentOne()
					&& this.getChild1().isIdempotentOne();
		}
		@Override
		public boolean isIdempotentSymbol(String value) {
			return (this.getChild0().isIdempotentSymbol(value) && this.getChild1()
					.isIdempotentOne())
					|| (this.getChild0().isIdempotentOne() && this.getChild1()
							.isIdempotentSymbol(value));
		}
	}

	private Node zero;
	private Node one;
	private Map<String, Node> symbolMap;

	public Node getZero() {
		if (this.zero == null) {
			this.zero = this.newZero();
		}
		return this.zero;
	}
	protected Node newZero() {
		return new ZeroNode();
	}
	public Node getOne() {
		if (this.one == null) {
			this.one = this.newOne();
		}
		return this.one;
	}
	protected Node newOne() {
		return new OneNode();
	}
	public Node getSymbol(String symbol) {
		final Map<String, Node> map = this.getSymbolMap(true);
		Node node = map.get(symbol);
		if (node == null) {
			node = this.newSymbol(symbol);
			map.put(symbol, node);
		}
		return node;
	}
	protected Node newSymbol(String symbol) {
		return new SymbolNode(symbol);
	}
	protected Map<String, Node> getSymbolMap(boolean anyway) {
		if (this.symbolMap == null && anyway) {
			this.symbolMap = this.newSymbolMap();
		}
		return symbolMap;
	}
	protected Map<String, Node> newSymbolMap() {
		return new HashMap<String, Node>();
	}
	protected Node newStar(Node x) {
		return new StarNode(x);
	}
	protected Node newPlus(Node x, Node y) {
		return new PlusNode(x, y);
	}
	protected Node newMultiplies(Node x, Node y) {
		return new MultipliesNode(x, y);
	}
}
