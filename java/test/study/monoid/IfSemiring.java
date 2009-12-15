package study.monoid;

public interface IfSemiring<Node extends IfSemiring<Node>> {
	public boolean isZero();
	public boolean isOne();
	public Node plus(Node x);
	public Node multiplies(Node x);
	/**
	 * @param n must be more than 0.
	 * @return
	 */
	public Node powers(int n);
}
