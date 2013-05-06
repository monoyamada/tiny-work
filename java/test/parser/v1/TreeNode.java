package parser.v1;

public class TreeNode<T extends TreeNode<T>> implements Cloneable {
	/**
	 * counts the number of node under the specified root. this method is slow.
	 * 
	 * @param root
	 * @return the number of node under the specified root including the specified root.
	 */
	public static int countNode(TreeNode<?> root) {
		if (root == null) {
			return 0;
		}
		int count = 1;
		TreeNode<?> child = root.firstNode();
		for (; child != null; child = child.nextNode()) {
			count += TreeNode.countNode(child);
		}
		return count;
	}
	/**
	 * counts the number of node under the specified parent.
	 * @param parent
	 * @return
	 */
	public static int countChildren(TreeNode<?> parent) {
		if (parent == null) {
			return 0;
		}
		int count = 0;
		TreeNode<?> child = parent.firstNode();
		for (; child != null; child = child.nextNode()) {
			++count;
		}
		return count;
	}

	private T nextNode;
	private T firstNode;
	private T lastNode;

	protected T clone() {
		try {
			@SuppressWarnings("unchecked")
			T that = (T) super.clone();
			that.nextNode = null;
			return that;
		} catch (CloneNotSupportedException ex) {
			ex.printStackTrace();
			throw new Error("could not clone");
		}
	}
	@SuppressWarnings("unchecked")
	protected T that() {
		return (T) this;
	}
	/**
	 * @return next sibling.
	 */
	public T nextNode() {
		return this.nextNode;
	}
	/**
	 * @return leftmost child.
	 */
	public T firstNode() {
		return this.firstNode;
	}
	/**
	 * @return rightmost child.
	 */
	public T lastNode() {
		return this.lastNode;
	}
	public T mostNextNode() {
		T that = this.that();
		while (that.nextNode() != null) {
			that = that.nextNode();
		}
		return that;
	}
	public T mostFirstNode() {
		T that = this.that();
		while (that.firstNode() != null) {
			that = that.firstNode();
		}
		return that;
	}
	public T mostLastNode() {
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
	public T addFirstNode(T child) {
		if (child == null) {
		} else if (this.firstNode == null) {
			this.firstNode = child;
			this.lastNode = child.mostNextNode();
		} else {
			child.mostNextNode().nextNode = this.firstNode;
			this.firstNode = child;
		}
		return this.that();
	}
	/**
	 * @param child
	 *          do nothing iff child is <cod>null</code>.
	 * @return
	 */
	public T addLastNode(T child) {
		if (child == null) {
		} else if (this.lastNode == null) {
			this.firstNode = child;
			this.lastNode = child.mostNextNode();
		} else {
			this.lastNode.nextNode = child;
			this.lastNode = child.mostNextNode();
		}
		return this.that();
	}
	public T removeFirstNode() {
		if (this.firstNode == null) {
			return this.that();
		}
		this.firstNode = this.firstNode.nextNode;
		if (this.firstNode == null) {
			this.lastNode = null;
		}
		return this.that();
	}
	T clearChildren() {
		this.firstNode = this.lastNode = null;
		return this.that();
	}
	T clearSibling() {
		this.nextNode = null;
		return this.that();
	}
	public boolean isBinary() {
		T first = this.firstNode();
		T last = this.lastNode();
		if (first == null || last == null) {
			return false;
		}
		return first.nextNode() == last;
	}
	public boolean isUnary() {
		T first = this.firstNode();
		T last = this.lastNode();
		if (first == null || last == null) {
			return false;
		}
		return first == last;
	}
	public boolean isLeaf() {
		return this.firstNode() == null;
	}
}
