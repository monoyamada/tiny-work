package study.monoid;

public interface IfTreeNode<Node extends IfTreeNode<Node>> extends
		Iterable<Node> {
	public int getChildSize();
	public Node getChild(int index);
}
