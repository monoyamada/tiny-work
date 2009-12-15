package study.algebra;

import study.function.Function;
import study.lang.Messages;

public class MagmaTestClasses {
	public static interface IfNode {
		public int getChildSize();
		public IfNode getChild(int index);
	}

	public static interface IfNodeTransform extends IfNode,
			Function<IfNode, IfNode> {
	}

	public static class Node implements IfNode {
		@Override
		public int getChildSize() {
			return 0;
		}
		@Override
		public IfNode getChild(int index) {
			if (index <= 0 || this.getChildSize() <= index) {
				String msg = Messages.getIndexOutOfRange(0, index, this.getChildSize());
				throw new IndexOutOfBoundsException(msg);
			}
			return this.doGetChild(index);
		}
		protected IfNode doGetChild(int index) {
			return null;
		}
	}

	public static class BinaryNode extends Node {
		private final IfNode chld0;
		private final IfNode chld1;

		public BinaryNode(IfNode chld0, IfNode chld1) {
			this.chld0 = chld0;
			this.chld1 = chld1;
		}
		@Override
		public int getChildSize() {
			return 2;
		}
		protected IfNode doGetChild(int index) {
			switch (index) {
			case 0:
				return this.chld0;
			case 1:
				return this.chld1;
			default:
				break;
			}
			return null;
		}
		public IfNode getChild0() {
			return this.chld0;
		}
		public IfNode getChild1() {
			return this.chld1;
		}
	}

	public static class IdentityTransfoorm extends Node implements
			IfNodeTransform {
		@Override
		public IfNode evaluate(IfNode source) throws Exception {
			return source;
		}
		@Override
		public <X> Function<X, ? extends IfNode> compose(
				Function<X, ? extends IfNode> source) {
			return source;
		}
	}
}
