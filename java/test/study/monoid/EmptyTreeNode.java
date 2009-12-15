package study.monoid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import study.lang.Messages;

public class EmptyTreeNode<Node extends IfTreeNode<Node>> implements IfTreeNode<Node> {
	@Override
	public int getChildSize() {
		return 0;
	}
	@Override
	public Node getChild(int index) {
		if (index < 0 || this.getChildSize() <= index) {
			String msg = Messages.getIndexOutOfRange(0, index, this.getChildSize());
			throw new IndexOutOfBoundsException(msg);
		}
		return this.doGetChild(index);
	}
	protected Node doGetChild(int index) {
		throw new Error();
	}
	@Override
	public Iterator<Node> iterator() {
		return new TreeNodeHelper.ChildIterator<Node>(this);
	}
	public void getNodesByDepthFirst(Collection<? super IfTreeNode<Node>> output) {
		TreeNodeHelper.getNodesByDepthFirst(output, this);
	}
	public <T> T[] toArray(T[] output) {
		final List<Object> buffer = new ArrayList<Object>();
		this.getNodesByDepthFirst(buffer);
		return buffer.toArray(output);
	}
}
