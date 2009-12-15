package study.monoid;

import java.util.Collection;
import java.util.Iterator;

import study.lang.Messages;

public class TreeNodeHelper {
	public static abstract class AbChildIterator<Parent, Node> implements
			Iterator<Node> {
		private final Parent parent;
		private int index;

		public AbChildIterator(Parent parent) {
			assert parent != null;
			this.parent = parent;
		}
		/**
		 * @return the parent
		 */
		protected Parent getParent() {
			return this.parent;
		}
		public boolean hasNext() {
			return this.index < this.getChildSize();
		}
		public Node next() {
			if (this.getChildSize() <= this.index) {
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
		public abstract int getChildSize();
		public abstract Node getChild(int index);
	}

	public static class ChildIterator<Node extends IfTreeNode<Node>> extends
			AbChildIterator<IfTreeNode<Node>, Node> {
		public ChildIterator(IfTreeNode<Node> parent) {
			super(parent);
		}
		public int getChildSize() {
			return this.getParent().getChildSize();
		}
		public Node getChild(int index) {
			return this.getParent().getChild(index);
		}
	}

	public static <Node extends IfTreeNode<Node>> void getNodesByDepthFirst(
			Collection<? super IfTreeNode<Node>> output, IfTreeNode<Node> node) {
		assert output != null && node != null;
		output.add(node);
		for (int i = 0, n = node.getChildSize(); i < n; ++i) {
			getNodesByDepthFirst(output, node);
		}
	}
}
