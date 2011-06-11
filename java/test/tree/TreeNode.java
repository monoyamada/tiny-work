package tree;

public interface TreeNode extends ListNode {
	/**
	 * narrowing return value.
	 */
	@Override
	public TreeNode getNext();
	public TreeNode getFirstChild();
}
