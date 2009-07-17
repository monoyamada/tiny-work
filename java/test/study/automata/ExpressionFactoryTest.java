package study.automata;

import utils.lang.Debug;
import utils.regex.ExpressionFactory.Node;
import junit.framework.TestCase;

public class ExpressionFactoryTest extends TestCase {
	protected void setUp() throws Exception {
		Debug.setLogLevel("debug");
		super.setUp();
	}
	public void testPlus() {
		final ExpressionFactory factory = new ExpressionFactory();
		Node a = factory.getSymbol("a");
		Node b = factory.getSymbol("b");
		{
			Node a_b = a.plus(b);
			Debug.log().info(a_b);
		}
		{
			Node a_a = a.plus(a);
			Debug.log().info(a_a);
		}
		{
			Node a_0 = a.plus(factory.getZero());
			Debug.log().info(a_0);
		}
		{
			Node a_1 = a.plus(factory.getOne());
			Debug.log().info(a_1);
		}
	}
	public void testMultipies() {
		final ExpressionFactory factory = new ExpressionFactory();
		Node a = factory.getSymbol("a");
		Node b = factory.getSymbol("b");
		{
			Node a_b = a.multiplies(b);
			Debug.log().info(a_b);
		}
		{
			Node a_a = a.multiplies(a);
			Debug.log().info(a_a);
		}
		{
			Node a_0 = a.multiplies(factory.getZero());
			Debug.log().info(a_0);
		}
		{
			Node a_1 = a.multiplies(factory.getOne());
			Debug.log().info(a_1);
		}
	}
	public void testStar() {
		final ExpressionFactory factory = new ExpressionFactory();
		Node a = factory.getSymbol("a");
		{
			Node a_ = a.star();
			Debug.log().info(a_);
		}
		{
			Debug.log().info(factory.getZero().star());
			Debug.log().info(factory.getOne().star());
		}
	}
}
