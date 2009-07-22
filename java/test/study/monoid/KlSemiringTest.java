package study.monoid;

import junit.framework.TestCase;
import study.function.AbFunction;
import study.function.IfFunction;
import study.lang.Debug;
import study.lang.ObjectHelper;
import study.monoid.KlSemiringFactory.IfNode;
import study.monoid.KlSemiringFactory.Symbol;
import study.monoid.KlSemiringGraph.GraphData;
import study.monoid.KlSemiringGraph.GraphNode;

public class KlSemiringTest extends TestCase {
	protected static class Factory extends KlSemiringFactory {
		class Parser extends KlSemiringParser {
			@Override
			protected boolean isAlphabet(char value) {
				return super.isAlphabet(value) || value == '\'';
			}
		}

		Parser parser = new Parser();

		public IfNode parse(String expr) {
			return this.parser.parse(this, expr);
		}
		public GraphNode[] makeGraph(IfNode node) {
			final KlSemiringGraph builder = new KlSemiringGraph();
			final GraphData data = builder.newGraphData(node);
			return data.getGraphNodes();
		}
	}

	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
		super.setUp();
	}
	public void testParser() {
		final Factory factory = new Factory();
		if (true) {
			Debug.log().info(factory.parse(""));
			Debug.log().info(factory.parse("a+a"));
			Debug.log().info(factory.parse("a+b"));
			Debug.log().info(factory.parse("(a+b)"));
			Debug.log().info(factory.parse("(a+b).(c+d)"));
			Debug.log().info(factory.parse("(a+b)^?"));
			Debug.log().info(factory.parse("(a+b)^*"));
			Debug.log().info(factory.parse("(a+b)^2"));
		}
	}
	protected static void info(GraphNode[] nodes){
		for (int i = 0, n = nodes.length; i < n; ++i) {
			Debug.log().info(nodes[i]);
		}
	}
	public void testGraph() throws Exception {
		final Factory factory = new Factory();
		{
			final IfNode one = factory.parse("");
			Debug.log().info(one);
			final GraphNode[] gone = factory.makeGraph(one);
			info(gone);
		}
		{
			final IfNode ab = factory.parse("a.b");
			Debug.log().info(ab);
			final GraphNode[] gab = factory.makeGraph(ab);
			info(gab);
		}
		{
			final IfNode aba = factory.parse("(a . b)^* . a^?");
			Debug.log().info(aba);
			final GraphNode[] gaba = factory.makeGraph(aba);
			info(gaba);
		}
		{
			// final IfNode Ea = factory.parse("Em + Em * plus * Ea");
			// final IfNode Em = factory.parse("Ep + Ep * multiplies * Em");
			// final IfNode Ep = factory.parse("Et + Et * powers * En");
			// final IfNode Et = factory.parse("Es + open * Ea * close");
			final IfNode Es = factory
					.parse("(alphabet + special) . (alphabet + special + digit)^*");
			final IfNode En = factory
					.parse("digit^+ + zeroMore + oneMore + zoroOrOne");
			Debug.log().info(Es);
			final GraphNode[] gEs = factory.makeGraph(Es);
			info(gEs);
			Debug.log().info(En);
			final GraphNode[] gEn = factory.makeGraph(En);
			info(gEn);
		}
	}
	protected IfFunction<IfNode, IfNode> replaceSymbol(final String value,
			final IfNode node) {
		return new AbFunction<IfNode, IfNode>() {
			@Override
			public IfNode evaluate(IfNode source) throws Exception {
				if (source.getNodeType() == KlSemiringFactory.SYMBOL) {
					final Symbol symbol = (Symbol) source;
					if (ObjectHelper.equals(symbol.getValue(), value)) {
						return node;
					}
				}
				return source;
			}

		};
	}
}
