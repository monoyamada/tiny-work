package study.monoid;

import junit.framework.TestCase;
import study.lang.Debug;
import study.monoid.KlSemiringFactory.IfNode;
import study.monoid.KlSemiringGraph.DfaBuilder;
import study.monoid.KlSemiringGraph.DfaNode;
import study.monoid.KlSemiringGraph.GraphData;
import study.monoid.KlSemiringGraph.NfaBuilder;
import study.monoid.KlSemiringGraph.NfaNode;

public class KlSemiringTest extends TestCase {
	static final KlSemiringFormatter FORMATTER = new DfaFormatter();

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
		public NfaNode[] makeGraph(IfNode node) {
			final KlSemiringGraph builder = new KlSemiringGraph();
			final GraphData data = builder.newGraphData(node);
			return new NfaBuilder(data).getNodes();
		}
	}

	protected static void infoNode(Object node) {
		if (node instanceof IfNode) {
			Debug.log().info(FORMATTER.format((IfNode) node));
		} else {
			Debug.log().info(node);
		}
	}
	protected static void infoArray(Object[] nodes) {
		for (int i = 0, n = nodes != null ? nodes.length : 0; i < n; ++i) {
			infoNode(nodes[i]);
		}
	}
	private static IfNode[] toExpression(DfaNode[] nodes) {
		final DfaExpressionBuilder builder = new DfaExpressionBuilder();
		final IfNode[] array = new IfNode[nodes.length];
		for (int i = 0, n = nodes.length; i < n; ++i) {
			final DfaNode node = nodes[i];
			array[i] = builder.getExpression(node);
		}
		return array;
	}

	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
		super.setUp();
	}
	public void testParser() {
		final Factory factory = new Factory();
		if (true) {
			infoNode(factory.parse(""));
			infoNode(factory.parse("a+a"));
			infoNode(factory.parse("a+b"));
			infoNode(factory.parse("(a+b)"));
			infoNode(factory.parse("(a+b).(c+d)"));
			infoNode(factory.parse("(a+b)^?"));
			infoNode(factory.parse("(a+b)^*"));
			infoNode(factory.parse("(a+b)^2"));
		}
	}
	public void testNfa() throws Exception {
		final Factory factory = new Factory();
		{
			final IfNode expr = factory.parse("");
			infoNode(expr);
			final NfaNode[] nodes = factory.makeGraph(expr);
			infoArray(nodes);
		}
		{
			final IfNode expr = factory.parse("a.b");
			infoNode(expr);
			final NfaNode[] nodes = factory.makeGraph(expr);
			infoArray(nodes);
		}
		{
			final IfNode expr = factory.parse("(a . b)^* . a^?");
			infoNode(expr);
			final NfaNode[] nodes = factory.makeGraph(expr);
			infoArray(nodes);
		}
	}
	public void testDfa() throws Exception {
		final Factory factory = new Factory();
		{
			final IfNode expr = factory.parse("(a . b) + (a . c)");
			infoNode(expr);
			final KlSemiringGraph builder = new KlSemiringGraph();
			final GraphData data = builder.newGraphData(expr);
			final DfaNode[] nodes = new DfaBuilder(data).getNodes();
			infoArray(nodes);
		}
		{
			final IfNode expr = factory
					.parse("(alphabet + special) . (alphabet + special + digit)^*");
			infoNode(expr);
			final KlSemiringGraph builder = new KlSemiringGraph();
			final GraphData data = builder.newGraphData(expr);
			final DfaNode[] nodes = new DfaBuilder(data).getNodes();
			infoArray(nodes);
		}
		{
			final IfNode expr = factory
					.parse("digit^+ + zeroMore + oneMore + zoroOrOne");
			infoNode(expr);
			final KlSemiringGraph builder = new KlSemiringGraph();
			final GraphData data = builder.newGraphData(expr);
			final DfaNode[] nodes = new DfaBuilder(data).getNodes();
			infoArray(nodes);
		}
	}
	public void testMinDfa() throws Exception {
		final Factory factory = new Factory();
		{
			final IfNode expr = factory.parse("(a . b) + (a . c)");
			infoNode(expr);
			final KlSemiringGraph builder = new KlSemiringGraph();
			final GraphData data = builder.newGraphData(expr);
			final DfaNode[] nodes = new DfaBuilder(data).reduceStates();
			final IfNode[] exprs = toExpression(nodes);
			infoArray(exprs);
		}
		{
			final IfNode expr = factory
					.parse("(alphabet + special) . (alphabet + special + digit)^*");
			infoNode(expr);
			final KlSemiringGraph builder = new KlSemiringGraph();
			final GraphData data = builder.newGraphData(expr);
			final DfaNode[] nodes = new DfaBuilder(data).reduceStates();
			final IfNode[] exprs = toExpression(nodes);
			infoArray(exprs);
		}
		{
			final IfNode expr = factory
					.parse("digit^+ + zeroMore + oneMore + zoroOrOne");
			infoNode(expr);
			final KlSemiringGraph builder = new KlSemiringGraph();
			final GraphData data = builder.newGraphData(expr);
			final DfaNode[] nodes = new DfaBuilder(data).reduceStates();
			final IfNode[] exprs = toExpression(nodes);
			infoArray(exprs);
		}
	}
}
