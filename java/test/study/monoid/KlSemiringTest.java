package study.monoid;

import junit.framework.TestCase;
import study.function.AbFunction;
import study.function.IfFunction;
import study.lang.Debug;
import study.lang.ObjectHelper;
import study.monoid.KlSemiringFactory.IfNode;
import study.monoid.KlSemiringFactory.Symbol;
import study.monoid.KlSemiringGraph.GraphBuilder;
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
			final KlSemiringGraph builder0 = new KlSemiringGraph();
			final GraphData data = builder0.newGraphData(node);
			final GraphBuilder builder1 = new GraphBuilder();
			return builder1.getGraphNodes(data);
		}
	}

	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
		super.setUp();
	}
	public void testParser() {
		final Factory factory = new Factory();
		if (true) {
			Debug.log().info(factory.parse("a+a"));
			Debug.log().info(factory.parse("a+b"));
			Debug.log().info(factory.parse("(a+b)"));
			Debug.log().info(factory.parse("(a+b).(c+d)"));
			Debug.log().info(factory.parse("(a+b)^?"));
			Debug.log().info(factory.parse("(a+b)^*"));
			Debug.log().info(factory.parse("(a+b)^2"));
		}
	}
	public void testGraph() throws Exception {
		final Factory factory = new Factory();
		{
			final IfNode aba = factory.parse("(a . b)^* . a^?");
			Debug.log().info(aba);
			final GraphNode[] gaba = factory.makeGraph(aba);
			for (int i = 0, n = gaba.length; i < n; ++i) {
				Debug.log().info(gaba[i]);
			}
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
			for (int i = 0, n = gEs.length; i < n; ++i) {
				Debug.log().info(gEs[i]);
			}
			Debug.log().info(En);
			final GraphNode[] gEn = factory.makeGraph(En);
			for (int i = 0, n = gEn.length; i < n; ++i) {
				Debug.log().info(gEn[i]);
			}
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
