package study.monoid;

public interface IfKleeneSemiring<Node extends IfKleeneSemiring<Node>> extends
		IfSemiring<Node> {
	public Node stars();
}
