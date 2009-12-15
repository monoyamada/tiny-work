package study.monoid;

public interface IfKlSemiring<Node extends IfKlSemiring<Node>> extends
		IfSemiring<Node> {
	public Node stars();
}
