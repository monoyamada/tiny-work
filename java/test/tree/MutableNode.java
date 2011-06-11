package tree;

public class MutableNode<X> implements TreeNode {
	private MutableNode<X> next;
	private MutableNode<X> firstChild;
	private MutableNode<X> lastChild;

	public MutableNode<X> getNext() {
		return this.next;
	}
	protected void setNext(MutableNode<X> node) {
		this.next = node;
	}
	public MutableNode<X> getFirstChild() {
		return this.firstChild;
	}
	protected void setFirstChild(MutableNode<X> node) {
		this.firstChild = node;
	}
	public MutableNode<X> getLastChild() {
		return this.lastChild;
	}
	protected void setLastChild(MutableNode<X> node) {
		this.lastChild = node;
	}

	public void addChild(MutableNode<X> node) {
		this.addLastChild(node);
	}
	public void addFirstChild(MutableNode<X> node) {
		node.setNext(this.firstChild);
		if (this.firstChild == null) {
			this.lastChild = node;
			this.firstChild = node;
		} else {
			this.firstChild = node;
		}
	}
	public void addLastChild(MutableNode<X> node) {
		node.setNext(null);
		if (this.lastChild == null) {
			this.firstChild = node;
			this.lastChild = node;
		} else {
			this.lastChild.setNext(node);
			this.lastChild = node;
		}
	}
}
