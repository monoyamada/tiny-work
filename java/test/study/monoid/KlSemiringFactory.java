package study.monoid;

import study.function.IfFunction;
import study.lang.Messages;

public class KlSemiringFactory {
	public static final int ZERO = 0;
	public static final int ONE = ZERO + 1;
	public static final int SYMBOL = ONE + 1;
	public static final int STARS = SYMBOL + 1;
	public static final int PLUS = STARS + 1;
	public static final int MULTIPLIES = PLUS + 1;
	public static final int END = SYMBOL + 1;

	public static interface IfNode extends IfKlSemiring<IfNode>,
			IfTreeNode<IfNode> {
		public int getNodeType();
		/**
		 * Traverses in post-order.
		 *
		 * @param fnc
		 * @return
		 * @throws Exception
		 */
		public IfNode replaceNode(IfFunction<IfNode, IfNode> fnc) throws Exception;
	}

	protected class Zero extends EmptyTreeNode<IfNode> implements IfNode {
		@Override
		public int getNodeType() {
			return KlSemiringFactory.ZERO;
		}
		@Override
		public boolean isZero() {
			return true;
		}
		@Override
		public boolean isOne() {
			return false;
		}
		@Override
		public IfNode stars() {
			return this.getOne();
		}
		@Override
		public IfNode plus(IfNode x) {
			if (x == null) {
				String msg = Messages.getUnexpectedValue("plus", "non-null", x);
				throw new IllegalArgumentException(msg);
			}
			return this.doPlus(x);
		}
		@Override
		public IfNode multiplies(IfNode x) {
			if (x == null) {
				String msg = Messages.getUnexpectedValue("multiplies", "non-null", x);
				throw new IllegalArgumentException(msg);
			}
			return this.doMultiplies(x);
		}
		@Override
		public IfNode powers(int n) {
			if (n < 0) {
				String msg = Messages.getUnexpectedValue("powers", "0<=n", n);
				throw new IllegalArgumentException(msg);
			}
			return this.doPowers(n);
		}
		protected IfNode doPlus(IfNode x) {
			return this;
		}
		protected IfNode doMultiplies(IfNode x) {
			return this;
		}
		protected IfNode doPowers(int n) {
			switch (n) {
			case 0:
				return this.getOne();
			default:
				return this;
			}
		}
		protected IfNode getZero() {
			return KlSemiringFactory.this.getZero();
		}
		protected IfNode getOne() {
			return KlSemiringFactory.this.getOne();
		}
		protected IfNode newStars() {
			return KlSemiringFactory.this.newStars(this);
		}
		protected IfNode newPlus(IfNode x) {
			return KlSemiringFactory.this.newPlus(this, x);
		}
		protected IfNode newMultiplies(IfNode x) {
			return KlSemiringFactory.this.newMultiplies(this, x);
		}
		@Override
		public IfNode replaceNode(IfFunction<IfNode, IfNode> fnc) throws Exception {
			if (fnc == null) {
				String msg = Messages
						.getUnexpectedValue("replaceNode", "non-null", fnc);
				throw new IllegalArgumentException(msg);
			}
			return this.doReplaceNode(fnc);
		}
		protected IfNode doReplaceNode(IfFunction<IfNode, IfNode> fnc)
				throws Exception {
			return fnc.evaluate(this);
		}
	}

	protected class One extends Zero {
		@Override
		public int getNodeType() {
			return KlSemiringFactory.ONE;
		}
		@Override
		public boolean isZero() {
			return false;
		}
		@Override
		public boolean isOne() {
			return true;
		}
		@Override
		public IfNode stars() {
			return this;
		}
		@Override
		protected IfNode doPlus(IfNode x) {
			switch (x.getNodeType()) {
			case KlSemiringFactory.ZERO:
			case KlSemiringFactory.ONE:
				return this;
			default:
				return this.newPlus(x);
			}
		}
		protected IfNode doMultiplies(IfNode x) {
			return x;
		}
		protected IfNode doPowers(int n) {
			return this;
		}
		protected IfNode doReplaceNode(IfFunction<IfNode, IfNode> fnc)
				throws Exception {
			return fnc.evaluate(this);
		}
	}

	public class Symbol extends Zero {
		private final String value;

		protected Symbol(String value) {
			this.value = value;
		}
		public String toString() {
			return this.getValue();
		}
		public String getValue() {
			return this.value;
		}
		@Override
		public int getNodeType() {
			return KlSemiringFactory.SYMBOL;
		}
		@Override
		public boolean isZero() {
			return false;
		}
		@Override
		public boolean isOne() {
			return false;
		}
		@Override
		public IfNode stars() {
			return this.newStars();
		}
		@Override
		protected IfNode doPlus(IfNode x) {
			switch (x.getNodeType()) {
			case KlSemiringFactory.ZERO:
				return this;
			default:
				return this.newPlus(x);
			}
		}
		protected IfNode doMultiplies(IfNode x) {
			switch (x.getNodeType()) {
			case KlSemiringFactory.ZERO:
				return x;
			case KlSemiringFactory.ONE:
				return this;
			default:
				return this.newMultiplies(x);
			}
		}
		protected IfNode doPowers(int n) {
			switch (n) {
			case 0:
				return this.getOne();
			case 1:
				return this;
			default:
				break;
			}
			IfNode node = this;
			while (0 < --n) {
				node = this.doMultiplies(node);
			}
			return node;
		}
		protected IfNode doReplaceNode(IfFunction<IfNode, IfNode> fnc)
				throws Exception {
			return fnc.evaluate(this);
		}
	}

	protected class Stars extends Zero {
		private final IfNode child;

		protected Stars(IfNode child) {
			assert child != null;
			this.child = child;
		}
		@Override
		public int getChildSize() {
			return 1;
		}
		@Override
		protected IfNode doGetChild(int index) {
			return this.getChild();
		}
		/**
		 * @return the child
		 */
		public IfNode getChild() {
			return this.child;
		}
		@Override
		public int getNodeType() {
			return KlSemiringFactory.STARS;
		}
		@Override
		public boolean isZero() {
			return false;
		}
		@Override
		public boolean isOne() {
			return false;
		}
		@Override
		public IfNode stars() {
			return this;
		}
		@Override
		protected IfNode doPlus(IfNode x) {
			switch (x.getNodeType()) {
			case KlSemiringFactory.ZERO:
			case KlSemiringFactory.ONE:
				return this;
			default:
				return this.newPlus(x);
			}
		}
		protected IfNode doMultiplies(IfNode x) {
			switch (x.getNodeType()) {
			case KlSemiringFactory.ZERO:
				return x;
			case KlSemiringFactory.ONE:
				return this;
			default:
				return this.newMultiplies(x);
			}
		}
		protected IfNode doPowers(int n) {
			switch (n) {
			case 0:
				return this.getOne();
			default:
				return this;
			}
		}
		protected IfNode doReplaceNode(IfFunction<IfNode, IfNode> fnc)
				throws Exception {
			final IfNode newChild = fnc.evaluate(this.getChild());
			if (newChild == this.getChild()) {
				return fnc.evaluate(this);
			}
			return fnc.evaluate(newChild.stars());
		}
	}

	protected class Plus extends Zero {
		private final IfNode child0;
		private final IfNode child1;

		protected Plus(IfNode child0, IfNode child1) {
			assert child0 != null && child1 != null;
			this.child0 = child0;
			this.child1 = child1;
		}
		@Override
		public int getChildSize() {
			return 2;
		}
		@Override
		protected IfNode doGetChild(int index) {
			switch (index) {
			case 0:
				return this.getChild0();
			default:
				return this.getChild1();
			}
		}
		/**
		 * @return the child
		 */
		public IfNode getChild0() {
			return this.child0;
		}
		/**
		 * @return the child
		 */
		public IfNode getChild1() {
			return this.child1;
		}
		@Override
		public int getNodeType() {
			return KlSemiringFactory.PLUS;
		}
		@Override
		public boolean isZero() {
			return false;
		}
		@Override
		public boolean isOne() {
			return false;
		}
		@Override
		public IfNode stars() {
			return this.newStars();
		}
		@Override
		protected IfNode doPlus(IfNode x) {
			switch (x.getNodeType()) {
			case KlSemiringFactory.ZERO:
				return this;
			default:
				return this.newPlus(x);
			}
		}
		protected IfNode doMultiplies(IfNode x) {
			switch (x.getNodeType()) {
			case KlSemiringFactory.ZERO:
				return x;
			case KlSemiringFactory.ONE:
				return this;
			default:
				return this.newMultiplies(x);
			}
		}
		protected IfNode doPowers(int n) {
			switch (n) {
			case 0:
				return this.getOne();
			case 1:
				return this;
			default:
				break;
			}
			IfNode node = this;
			while (0 < --n) {
				node = this.doMultiplies(node);
			}
			return node;
		}
		protected IfNode doReplaceNode(IfFunction<IfNode, IfNode> fnc)
				throws Exception {
			final IfNode newChild0 = fnc.evaluate(this.getChild0());
			final IfNode newChild1 = fnc.evaluate(this.getChild1());
			if (newChild0 == this.getChild0() && newChild1 == this.getChild1()) {
				return fnc.evaluate(this);
			}
			return fnc.evaluate(newChild0.plus(newChild1));
		}
	}

	protected class Multiplies extends Plus {
		protected Multiplies(IfNode child0, IfNode child1) {
			super(child0, child1);
		}
		@Override
		public int getNodeType() {
			return KlSemiringFactory.MULTIPLIES;
		}
		protected IfNode doReplaceNode(IfFunction<IfNode, IfNode> fnc)
				throws Exception {
			final IfNode newChild0 = fnc.evaluate(this.getChild0());
			final IfNode newChild1 = fnc.evaluate(this.getChild1());
			if (newChild0 == this.getChild0() && newChild1 == this.getChild1()) {
				return fnc.evaluate(this);
			}
			return fnc.evaluate(newChild0.multiplies(newChild1));
		}
	}

	private IfNode zero;
	private IfNode one;

	public IfNode getZero() {
		if (this.zero == null) {
			this.zero = this.newZero();
		}
		return this.zero;
	}
	protected IfNode newZero() {
		return new Zero();
	}
	public IfNode getOne() {
		if (this.one == null) {
			this.one = this.newOne();
		}
		return this.one;
	}
	protected IfNode newOne() {
		return new One();
	}
	public IfNode getSymbol(String value) {
		if (value == null || value.length() < 1) {
			String msg = Messages.getUnexpectedValue("symbol", "non-empty string",
					value);
			throw new IllegalArgumentException(msg);
		}
		return new Symbol(value);
	}
	protected IfNode newStars(IfNode x) {
		return new Stars(x);
	}
	protected IfNode newPlus(IfNode x, IfNode y) {
		return new Plus(x, y);
	}
	protected IfNode newMultiplies(IfNode x, IfNode y) {
		return new Multiplies(x, y);
	}
}
